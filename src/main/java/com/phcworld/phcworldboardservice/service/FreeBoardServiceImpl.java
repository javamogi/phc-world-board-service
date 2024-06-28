package com.phcworld.phcworldboardservice.service;

import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSearch;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardService;
import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.domain.Authority;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.domain.FreeBoardRequest;
import com.phcworld.phcworldboardservice.exception.model.DeletedEntityException;
import com.phcworld.phcworldboardservice.exception.model.ForbiddenException;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.security.utils.SecurityUtil;
import com.phcworld.phcworldboardservice.service.port.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
public class FreeBoardServiceImpl implements FreeBoardService {
	private final FreeBoardRepository freeBoardRepository;
//	private final UploadFileService uploadFileService;
	private final KafkaProducer boardProducer;
	private final LocalDateTimeHolder localDateTimeHolder;
	private final UuidHolder uuidHolder;

	private final WebclientService webclientService;

	@Override
	public FreeBoard register(FreeBoardRequest request) {
		String userId = SecurityUtil.getCurrentMemberId();

//		String contents = uploadFileService.registerImages(request.contents());

		FreeBoard freeBoard = FreeBoard.from(request, userId, localDateTimeHolder, uuidHolder);

		return boardProducer.send("boards", freeBoard, false);
//		return freeBoardRepository.save(freeBoard);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FreeBoard> getSearchList(FreeBoardSearch search) {
		PageRequest pageRequest = PageRequest.of(
				search.pageNum() - 1,
				search.pageSize(),
				Sort.by("createDate").descending());

		return freeBoardRepository.findByKeyword(search, pageRequest);
	}

	@Override
	public FreeBoard update(FreeBoardRequest request) {
		FreeBoard freeBoard = freeBoardRepository.findByBoardId(request.boardId())
				.orElseThrow(NotFoundException::new);
		if(freeBoard.isDeleted()){
			throw new DeletedEntityException();
		}

		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();

		if(!freeBoard.matchUser(userId) && authorities != Authority.ROLE_ADMIN){
			throw new ForbiddenException();
		}

//		String contents = uploadFileService.registerImages(request.contents());

//		freeBoard.update(request.title(), contents);
		freeBoard = freeBoard.update(request.title(), request.contents(), localDateTimeHolder);
		return boardProducer.send("boards", freeBoard, true);

//		return freeBoardRepository.save(freeBoard);
	}

	@Override
	public FreeBoard delete(String boardId) {
		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();
		FreeBoard freeBoard = freeBoardRepository.findByBoardId(boardId)
						.orElseThrow(NotFoundException::new);
		if(freeBoard.isDeleted()){
			throw new DeletedEntityException();
		}
		if(!freeBoard.matchUser(userId) && authorities != Authority.ROLE_ADMIN){
			throw new ForbiddenException();
		}
		freeBoard = freeBoard.delete();
		return boardProducer.send("boards", freeBoard, true);
//		return freeBoardRepository.save(freeBoard);
	}

	@Override
	public List<FreeBoard> getFreeBoardsByUserId(String writerId) {
		return freeBoardRepository.findByWriterId(writerId);
	}

	@Override
	public FreeBoard existBoard(String boardId){
		return freeBoardRepository.findByBoardId(boardId).orElseThrow(NotFoundException::new);
	}

	@Override
	public FreeBoard getFreeBoard(String boardId) {
		FreeBoard freeBoard = freeBoardRepository.findByBoardId(boardId)
				.orElseThrow(NotFoundException::new);
		if(freeBoard.isDeleted()){
			throw new DeletedEntityException();
		}

		String userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();
		freeBoard = freeBoard.setAuthority(userId, authorities);
		freeBoard = freeBoard.addCount();

		return boardProducer.send("boards", freeBoard, true);
	}

}
