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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeBoardService {
	private final FreeBoardRepository freeBoardRepository;
//	private final UploadFileService uploadFileService;
//	private final RestTemplate restTemplate;
	private final BoardProducer boardProducer;
	private final WebclientService webclientService;

	public FreeBoardResponseDto registerFreeBoard(FreeBoardRequestDto request, String token) {
		String userId = SecurityUtil.getCurrentMemberId();

//		String contents = uploadFileService.registerImages(request.contents());

		String boardId = UUID.randomUUID().toString();
		while(freeBoardRepository.findByBoardId(boardId).isPresent()){
			boardId = UUID.randomUUID().toString();
		}

		FreeBoard freeBoard = request.toEntity(boardId, userId);

//		freeBoardRepository.save(freeBoard);
		boardProducer.send("boards", freeBoard);

		UserResponseDto user = webclientService.getUserResponseDto(token, freeBoard);

		return FreeBoardResponseDto.of(user, freeBoard);
	}

	@Transactional(readOnly = true)
	public List<FreeBoardResponseDto> getSearchList(FreeBoardSearchDto search, String token) {
		PageRequest pageRequest = PageRequest.of(search.pageNum() - 1, search.pageSize(), Sort.by("createDate").descending());
		List<FreeBoardSelectDto> list = freeBoardRepository.findByKeyword(search, pageRequest);
		List<String> userIds = list.stream()
				.map(FreeBoardSelectDto::getWriterId)
				.distinct()
				.toList();

		Map<String, UserResponseDto> users = webclientService.getUserResponseDtoMap(token, userIds);

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

		UserResponseDto user = webclientService.getUserResponseDto(token, freeBoard);
		List<FreeBoardAnswerResponseDto> answers = webclientService.getFreeBoardAnswerResponseDtoList(token, freeBoard);

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
		boardProducer.send("boards", freeBoard);

		UserResponseDto user = webclientService.getUserResponseDto(token, freeBoard);

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

	public SuccessResponseDto deleteFreeBoard(String boardId) {
		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();
		freeBoardRepository.findByBoardId(boardId)
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
