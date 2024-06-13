package com.phcworld.phcworldboardservice.service;

import com.phcworld.phcworldboardservice.infrastructure.port.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardResponse;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardSearchDto;
import com.phcworld.phcworldboardservice.controller.port.SuccessResponse;
import com.phcworld.phcworldboardservice.domain.Authority;
import com.phcworld.phcworldboardservice.domain.port.FreeBoardRequestDto;
import com.phcworld.phcworldboardservice.infrastructure.FreeBoardEntity;
import com.phcworld.phcworldboardservice.exception.model.DeletedEntityException;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.exception.model.UnauthorizedException;
import com.phcworld.phcworldboardservice.infrastructure.port.FreeBoardSelectDto;
import com.phcworld.phcworldboardservice.messagequeue.producer.BoardProducer;
import com.phcworld.phcworldboardservice.infrastructure.FreeBoardJpaJpaRepository;
import com.phcworld.phcworldboardservice.security.utils.SecurityUtil;
import com.phcworld.phcworldboardservice.service.port.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeBoardService {
	private final FreeBoardJpaJpaRepository freeBoardJpaRepository;
//	private final UploadFileService uploadFileService;
//	private final RestTemplate restTemplate;
	private final BoardProducer boardProducer;
	private final WebclientService webclientService;

	public FreeBoardResponse registerFreeBoard(FreeBoardRequestDto request, String token) {
		String userId = SecurityUtil.getCurrentMemberId();

//		String contents = uploadFileService.registerImages(request.contents());


		FreeBoardEntity freeBoardEntity = request.toEntity(userId);

//		freeBoardRepository.save(freeBoard);
		boardProducer.send("boards", freeBoardEntity);

		UserResponse user = webclientService.getUserResponseDto(token, freeBoardEntity);

		return FreeBoardResponse.of(user, freeBoardEntity);
	}

	@Transactional(readOnly = true)
	public List<FreeBoardResponse> getSearchList(FreeBoardSearchDto search, String token) {
		PageRequest pageRequest = PageRequest.of(search.pageNum() - 1, search.pageSize(), Sort.by("createDate").descending());
		List<FreeBoardSelectDto> list = freeBoardJpaRepository.findByKeyword(search, pageRequest);
		List<String> userIds = list.stream()
				.map(FreeBoardSelectDto::getWriterId)
				.distinct()
				.toList();

		Map<String, UserResponse> users = webclientService.getUserResponseDtoMap(token, userIds);

		UserResponse user = UserResponse.builder()
				.email("")
				.name("")
				.createDate("")
				.profileImage("")
				.userId("")
				.build();

		return list.stream()
				.map(f -> {
					return FreeBoardResponse.builder()
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
	public FreeBoardResponse getFreeBoard(String boardId, String token) {
		FreeBoardEntity freeBoardEntity = freeBoardJpaRepository.findByBoardId(boardId)
				.orElseThrow(NotFoundException::new);
		if(freeBoardEntity.getIsDeleted()){
			throw new DeletedEntityException();
		}
		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();
		boolean isDeleteAuthority = false;
		boolean isModifyAuthority = false;

		if(!freeBoardEntity.matchUser(userId)){
			isModifyAuthority = true;
			isDeleteAuthority = true;
		}
		if(authorities == Authority.ROLE_ADMIN){
			isDeleteAuthority = true;
		}

		freeBoardEntity.addCount();

		UserResponse user = webclientService.getUserResponseDto(token, freeBoardEntity);
		List<FreeBoardAnswerResponse> answers = webclientService.getFreeBoardAnswerResponseDtoList(token, freeBoardEntity);

		return FreeBoardResponse.builder()
//				.boardId(freeBoardEntity.getBoardId())
				.title(freeBoardEntity.getTitle())
				.contents(freeBoardEntity.getContents())
				.writer(user)
				.isNew(freeBoardEntity.isNew())
				.count(freeBoardEntity.getCount())
				.answers(answers)
				.isDeleteAuthority(isDeleteAuthority)
				.isModifyAuthority(isModifyAuthority)
				.build();
	}

	public FreeBoardResponse updateFreeBoard(FreeBoardRequestDto request, String token) {
//		FreeBoard freeBoard = freeBoardRepository.findById(request.id())
//				.orElseThrow(NotFoundException::new);

		FreeBoardEntity freeBoardEntity = freeBoardJpaRepository.findByBoardId(request.id())
				.orElseThrow(NotFoundException::new);

		if(freeBoardEntity.getIsDeleted()){
			throw new DeletedEntityException();
		}

		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();

		if(freeBoardEntity.matchUser(userId) && authorities != Authority.ROLE_ADMIN){
			throw new UnauthorizedException();
		}

//		String contents = uploadFileService.registerImages(request.contents());

//		freeBoard.update(request.title(), contents);
		freeBoardEntity.update(request.title(), request.contents());
		boardProducer.send("boards", freeBoardEntity);

		UserResponse user = webclientService.getUserResponseDto(token, freeBoardEntity);

		return FreeBoardResponse.builder()
//				.boardId(freeBoardEntity.getBoardId())
				.title(freeBoardEntity.getTitle())
				.contents(freeBoardEntity.getContents())
				.writer(user)
				.isNew(freeBoardEntity.isNew())
				.count(freeBoardEntity.getCount())
				.countOfAnswer(freeBoardEntity.getCountOfAnswer())
				.build();
	}

	public SuccessResponse deleteFreeBoard(String boardId) {
		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();
		freeBoardJpaRepository.findByBoardId(boardId)
				.ifPresentOrElse(
						f -> {
							if(f.getIsDeleted()){
								throw new DeletedEntityException();
							}
							if(f.matchUser(userId) && authorities != Authority.ROLE_ADMIN){
								throw new UnauthorizedException();
							}
							f.delete();
							boardProducer.send("boards", f);
						},
						() -> {
							throw new NotFoundException();
						}
				);

		return SuccessResponse.builder()
				.statusCode(200)
				.message("삭제 성공")
				.build();
	}

	public boolean existFreeBoard(String boardId){
		return freeBoardJpaRepository.findByBoardId(boardId)
				.isPresent();
	}

}
