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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

	public FreeBoardResponseDto registerFreeBoard(FreeBoardRequestDto request, String token) {
		String userId = SecurityUtil.getCurrentMemberId();

//		String contents = uploadFileService.registerImages(request.contents());


		String boardId = UUID.randomUUID().toString();
		freeBoardRepository.findByBoardId(boardId)
				.orElseThrow(DuplicationException::new);
		FreeBoard freeBoard = FreeBoard.builder()
				.boardId(boardId)
				.writerId(userId)
				.title(request.title())
//				.contents(contents)
				.contents(request.contents())
				.build();

//		freeBoardRepository.save(freeBoard);
		boardProducer.send("boards", freeBoard);

		UserResponseDto user = webClient.build()
//				.mutate().baseUrl("http://localhost:9634/users")
				.mutate().baseUrl(env.getProperty("user_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/{id}")
						.build(freeBoard.getWriterId()))
				.header(HttpHeaders.AUTHORIZATION, token)
				.retrieve()
				.bodyToMono(UserResponseDto.class)
				.block();

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

		Mono<Map<String, UserResponseDto>> response = webClient.build()
//				.mutate().baseUrl("http://localhost:9634/users")
				.mutate().baseUrl(env.getProperty("user_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("")
						.queryParam("userIds", userIds)
						.build())
				.header(HttpHeaders.AUTHORIZATION, token)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<Map<String, UserResponseDto>>() {});

		Map<String, UserResponseDto> users = response.block();

		return list.stream()
				.map(f -> {
					return FreeBoardResponseDto.builder()
							.boardId(f.getBoardId())
							.title(f.getTitle())
							.contents(f.getContents())
							.writer(users.get(f.getWriterId()))
							.isNew(f.isNew())
							.count(f.getCount())
							.countOfAnswer(f.getCountOfAnswer())
							.build();
				})
				.toList();
	}

	@Transactional
	public Map<String, Object> getFreeBoard(Long id, String token) {
		FreeBoard freeBoard = freeBoardRepository.findById(id)
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

		UserResponseDto user = webClient.build()
//				.mutate().baseUrl("http://localhost:9634/users")
				.mutate().baseUrl(env.getProperty("user_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/{id}")
						.build(freeBoard.getWriterId()))
				.header(HttpHeaders.AUTHORIZATION, token)
				.retrieve()
				.bodyToMono(UserResponseDto.class)
				.block();

		//rest 통신
		List<FreeBoardAnswerResponseDto> answers = webClient.build()
//				.mutate().baseUrl("http://localhost:9634/users")
				.mutate().baseUrl(env.getProperty("answer_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/freeboards/{id}")
						.build(freeBoard.getId()))
				.header(HttpHeaders.AUTHORIZATION, token)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<FreeBoardAnswerResponseDto>>() {})
				.block();

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

		UserResponseDto user = webClient.build()
//				.mutate().baseUrl("http://localhost:9634/users")
				.mutate().baseUrl(env.getProperty("user_service.url"))
				.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/{id}")
						.build(freeBoard.getWriterId()))
				.header(HttpHeaders.AUTHORIZATION, token)
				.retrieve()
				.bodyToMono(UserResponseDto.class)
				.block();

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

}
