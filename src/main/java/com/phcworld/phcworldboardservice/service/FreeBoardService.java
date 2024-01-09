package com.phcworld.phcworldboardservice.service;

import com.phcworld.phcworldboardservice.dto.*;
import com.phcworld.phcworldboardservice.exception.model.DeletedEntityException;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.exception.model.UnauthorizedException;
import com.phcworld.phcworldboardservice.domain.Authority;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.repository.FreeBoardRepository;
import com.phcworld.phcworldboardservice.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeBoardService {
	private final FreeBoardRepository freeBoardRepository;
//	private final UploadFileService uploadFileService;
	private final RestTemplate restTemplate;
	private final Environment env;

	@Transactional
	public FreeBoardResponseDto registerFreeBoard(FreeBoardRequestDto request, String token) {
		String userId = SecurityUtil.getCurrentMemberId();

//		String contents = uploadFileService.registerImages(request.contents());

		FreeBoard freeBoard = FreeBoard.builder()
				.writerId(userId)
				.title(request.title())
//				.contents(contents)
				.contents(request.contents())
				.build();

		freeBoardRepository.save(freeBoard);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		HttpEntity<List<String>> entity = new HttpEntity<>(headers);

		String userUrl = String.format(env.getProperty("user_service.url") + "/%s", freeBoard.getWriterId());
		ResponseEntity<UserResponseDto> userResponse =
				restTemplate.exchange(userUrl,
						HttpMethod.GET,
						entity,
						new ParameterizedTypeReference<UserResponseDto>() {
						});

		UserResponseDto user = userResponse.getBody();

		return FreeBoardResponseDto.builder()
				.id(freeBoard.getId())
				.title(freeBoard.getTitle())
				.contents(freeBoard.getContents())
				.writer(user)
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

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);

		HttpEntity<List<String>> entity = new HttpEntity<>(userIds, headers);

		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(env.getProperty("user_service.url"))
				.queryParam("userIds", userIds);
		ResponseEntity<Map<String, UserResponseDto>> userResponse =
				restTemplate.exchange(builder.toUriString(),
						HttpMethod.GET,
						entity,
						new ParameterizedTypeReference<Map<String, UserResponseDto>>() {
						});
		Map<String, UserResponseDto> users = userResponse.getBody();

		return list.stream()
				.map(f -> {
					return FreeBoardResponseDto.builder()
							.id(f.getId())
							.title(f.getTitle())
							.contents(f.getContents())
							.writer(users.get(f.getWriterId()))
							.build();
				})
				.toList();
	}

	@Transactional
	public Map<String, Object> getFreeBoard(Long id, String token) {
		FreeBoard freeBoard = freeBoardRepository.findById(id)
				.orElseThrow(NotFoundException::new);
		if(freeBoard.getDeleted()){
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

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		HttpEntity<List<String>> entity = new HttpEntity<>(headers);

		String userUrl = String.format(env.getProperty("user_service.url") + "/%s", freeBoard.getWriterId());
		ResponseEntity<UserResponseDto> userResponse =
				restTemplate.exchange(userUrl,
						HttpMethod.GET,
						entity,
						new ParameterizedTypeReference<UserResponseDto>() {
						});

		UserResponseDto user = userResponse.getBody();

		//rest 통신
		List<FreeBoardAnswerResponseDto> answers = new ArrayList<>();

		FreeBoardResponseDto response = FreeBoardResponseDto.builder()
				.id(freeBoard.getId())
				.title(freeBoard.getTitle())
				.contents(freeBoard.getContents())
				.writer(user)
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
		if(freeBoard.getDeleted()){
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

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		HttpEntity<List<String>> entity = new HttpEntity<>(headers);

		String userUrl = String.format(env.getProperty("user_service.url") + "/%s", freeBoard.getWriterId());
		ResponseEntity<UserResponseDto> userResponse =
				restTemplate.exchange(userUrl,
						HttpMethod.GET,
						entity,
						new ParameterizedTypeReference<UserResponseDto>() {
						});

		UserResponseDto user = userResponse.getBody();

		return FreeBoardResponseDto.builder()
				.id(freeBoard.getId())
				.title(freeBoard.getTitle())
				.contents(freeBoard.getContents())
				.writer(user)
				.build();
	}

	@Transactional
	public SuccessResponseDto deleteFreeBoard(Long id) {
		FreeBoard freeBoard = freeBoardRepository.findById(id)
				.orElseThrow(NotFoundException::new);
		if(freeBoard.getDeleted()){
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

}
