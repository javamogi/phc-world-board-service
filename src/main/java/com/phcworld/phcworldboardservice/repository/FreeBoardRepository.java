package com.phcworld.phcworldboardservice.repository;


import com.phcworld.phcworldboardservice.domain.FreeBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long>, FreeBoardRepositoryCustom {
	List<FreeBoard> findByWriterId(String writerId);

//	@Query("select f from FreeBoard f join fetch f.writer")
//	List<FreeBoard> findAllByFetch();

	Optional<FreeBoard> findByBoardId(String boardId);
}
