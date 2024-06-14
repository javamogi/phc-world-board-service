package com.phcworld.phcworldboardservice.infrastructure;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FreeBoardJpaRepository extends JpaRepository<FreeBoardEntity, Long>, FreeBoardJpaRepositoryCustom {
	List<FreeBoardEntity> findByWriterId(String writerId);

//	@Query("select f from FreeBoard f join fetch f.writer")
//	List<FreeBoard> findAllByFetch();

	Optional<FreeBoardEntity> findById(Long boardId);
}
