package com.phcworld.phcworldboardservice.service;

import com.phcworld.phcworldboardservice.domain.Authority;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.dto.*;
import com.phcworld.phcworldboardservice.exception.model.DeletedEntityException;
import com.phcworld.phcworldboardservice.exception.model.DuplicationException;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.exception.model.UnauthorizedException;
import com.phcworld.phcworldboardservice.messagequeue.producer.BoardProducer;
import com.phcworld.phcworldboardservice.repository.FreeBoardRepository;
import com.phcworld.phcworldboardservice.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeBoardService {
	private final FreeBoardRepository freeBoardRepository;
//	private final UploadFileService uploadFileService;
	private final RestTemplate restTemplate;
	private final Environment env;
//	private final WebClient webClient;
	private final WebClient.Builder webClient;
	private final BoardProducer boardProducer;
	private final CircuitBreakerFactory circuitBreakerFactory;

	public FreeBoardResponseDto registerFreeBoard(FreeBoardRequestDto request, String token) {
		String userId = SecurityUtil.getCurrentMemberId();

//		String contents = uploadFileService.registerImages(request.contents());


		String boardId = UUID.randomUUID().toString();
		boolean exist = freeBoardRepository.findByBoardId(boardId)
				.isPresent();
		if(exist){
			throw new DuplicationException();
		}
		FreeBoard freeBoard = FreeBoard.builder()
				.boardId(boardId)
				.writerId(userId)
				.title(request.title())
//				.contents(contents)
				.contents(request.contents())
				.build();

//		freeBoardRepository.save(freeBoard);
		boardProducer.send("boards", freeBoard);

		log.info("Before call users microservice");
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
		UserResponseDto user = circuitBreaker.run(
				() -> getUserResponseDto(token, freeBoard),
				throwable -> UserResponseDto.builder()
						.email("")
						.name("")
						.createDate("")
						.profileImage("")
						.userId("")
						.build());
		log.info("After called users microservice");

		return FreeBoardResponseDto.builder()
				.boardId(freeBoard.getBoardId())
				.title(freeBoard.getTitle())
				.contents(freeBoard.getContents())
				.writer(user)
				.isNew(freeBoard.isNew())
				.count(freeBoard.getCount())
				.countOfAnswer(freeBoard.getCountOfAnswer())
				.build();
	}

	@Transactional(readOnly = true)
	public List<FreeBoardResponseDto> getSearchList(FreeBoardSearchDto search, String token) {
		PageRequest pageRequest = PageRequest.of(search.pageNum() - 1, search.pageSize(), Sort.by("createDate").descending());
		List<FreeBoardSelectDto> list = freeBoardRepository.findByKeyword(search, pageRequest);
		List<String> userIds = list.stream()
				.map(FreeBoardSelectDto::getWriterId)
				.distinct()
				.toList();

		log.info("Before call users microservice");
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
		Map<String, UserResponseDto> users = circuitBreaker.run(
				() -> getUserResponseDtoMap(token, userIds),
				throwable -> new HashMap<>());
		log.info("After called users microservice");

		UserResponseDto user = UserResponseDto.builder()
				.email("")
				.name("")
				.createDate("")
				.profileImage("")
				.userId("")
				.build();

		return list.stream()
				.map(f -> {
					return FreeBoardResponseDto.builder()
							.boardId(f.getBoardId())
							.title(f.getTitle())
							.contents(f.getContents())
							.writer(users.isEmpty() ? user : users.get(f.getWriterId()))
							.isNew(f.isNew())
							.count(f.getCount())
							.countOfAnswer(f.getCountOfAnswer())
							.build();
				})
				.toList();
	}

	@Transactional
	public Map<String, Object> getFreeBoard(String boardId, String token) {
		FreeBoard freeBoard = freeBoardRepository.findByBoardId(boardId)
				.orElseThrow(NotFoundException::new);
		if(freeBoard.getIsDeleted()){
			throw new DeletedEntityException();
		}
		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();
		boolean isDeleteAuthority = false;
		boolean isModifyAuthority = false;

		if(!freeBoard.matchUser(userId)){
			isModifyAuthority = true;
			isDeleteAuthority = true;
		}
		if(authorities == Authority.ROLE_ADMIN){
			isDeleteAuthority = true;
		}

		freeBoard.addCount();

		Map<String, Object> map = new HashMap<>();

		log.info("Before call users microservice");
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
		UserResponseDto user = circuitBreaker.run(
				() -> getUserResponseDto(token, freeBoard),
				throwable -> UserResponseDto.builder()
						.email("")
						.name("")
						.createDate("")
						.profileImage("")
						.userId("")
						.build());
		log.info("After called users microservice");

		log.info("Before call answers microservice");
		List<FreeBoardAnswerResponseDto> answers = circuitBreaker.run(
				() -> getFreeBoardAnswerResponseDtoList(token, freeBoard),
				throwable -> new ArrayList<>());
		log.info("After called answers microservice");

		FreeBoardResponseDto response = FreeBoardResponseDto.builder()
				.boardId(freeBoard.getBoardId())
				.title(freeBoard.getTitle())
				.contents(freeBoard.getContents())
				.writer(user)
				.isNew(freeBoard.isNew())
				.count(freeBoard.getCount())
				.answers(answers)
				.build();
		map.put("freeboard", response);
		map.put("isDeleteAuthority", isDeleteAuthority);
		map.put("isModifyAuthority", isModifyAuthority);
		return map;
	}

	@Transactional
	public FreeBoardResponseDto updateFreeBoard(FreeBoardRequestDto request, String token) {
		FreeBoard freeBoard = freeBoardRepository.findById(request.id())
				.orElseThrow(NotFoundException::new);
		if(freeBoard.getIsDeleted()){
			throw new DeletedEntityException();
		}
		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();

		if(freeBoard.matchUser(userId) && authorities != Authority.ROLE_ADMIN){
			throw new UnauthorizedException();
		}

//		String contents = uploadFileService.registerImages(request.contents());

//		freeBoard.update(request.title(), contents);
		freeBoard.update(request.title(), request.contents());

		log.info("Before call users microservice");
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
		UserResponseDto user = circuitBreaker.run(
				() -> getUserResponseDto(token, freeBoard),
				throwable -> UserResponseDto.builder()
						.email("")
						.name("")
						.createDate("")
						.profileImage("")
						.userId("")
						.build());
		log.info("After called users microservice");

		return FreeBoardResponseDto.builder()
				.boardId(freeBoard.getBoardId())
				.title(freeBoard.getTitle())
				.contents(freeBoard.getContents())
				.writer(user)
				.isNew(freeBoard.isNew())
				.count(freeBoard.getCount())
				.countOfAnswer(freeBoard.getCountOfAnswer())
				.build();
	}

	@Transactional
	public SuccessResponseDto deleteFreeBoard(String boardId) {
		FreeBoard freeBoard = freeBoardRepository.findByBoardId(boardId)
				.orElseThrow(NotFoundException::new);
		if(freeBoard.getIsDeleted()){
			throw new DeletedEntityException();
		}
		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();

		if(freeBoard.matchUser(userId) && authorities != Authority.ROLE_ADMIN){
			throw new UnauthorizedException();
		}

		freeBoard.delete();

		return SuccessResponseDto.builder()
				.statusCode(200)
				.message("삭제 성공")
				.build();
	}

	public boolean existFreeBoard(String boardId){
		return freeBoardRepository.findByBoardId(boardId)
				.isPresent();
	}

	@Nullable
	private UserResponseDto getUserResponseDto(String token, FreeBoard freeBoard) {
		return webClient.build()
				.mutate().baseUrl("http://localhost:8080/users")
//				.mutate().baseUrl(env.getProperty("user_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/{id}")
						.build(freeBoard.getWriterId()))
				.header(HttpHeaders.AUTHORIZATION, token)
				.retrieve()
				.bodyToMono(UserResponseDto.class)
				.block();
	}

	@Nullable
	private Map<String, UserResponseDto> getUserResponseDtoMap(String token, List<String> userIds) {
		return webClient.build()
				.mutate().baseUrl("http://localhost:8080/users")
//				.mutate().baseUrl(env.getProperty("user_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("")
						.queryParam("userIds", userIds)
						.build())
				.header(HttpHeaders.AUTHORIZATION, token)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<Map<String, UserResponseDto>>() {})
				.block();
	}

	@Nullable
	private List<FreeBoardAnswerResponseDto> getFreeBoardAnswerResponseDtoList(String token, FreeBoard freeBoard) {
		return webClient.build()
				.mutate().baseUrl("http://localhost:8080/users")
//				.mutate().baseUrl(env.getProperty("answer_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/freeboards/{id}")
						.build(freeBoard.getBoardId()))
				.header(HttpHeaders.AUTHORIZATION, token)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<FreeBoardAnswerResponseDto>>() {})
				.block();
	}

}
