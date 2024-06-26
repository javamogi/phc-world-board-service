package com.phcworld.phcworldboardservice.infrastructure;

import com.phcworld.phcworldboardservice.domain.FreeBoard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
//@DynamicUpdate
@DynamicInsert
@Table(name = "boards",
		indexes = {@Index(name = "idx__create_date", columnList = "createDate"),
				@Index(name = "idx__writer_id_create_date", columnList = "writer_id, createDate")})
public class FreeBoardEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARD_SEQ_GENERATOR")
	private Long id;

	@Column(nullable = false, unique = true)
	private String boardId;

	@Column(nullable = false)
	private String writerId;

	@Column(nullable = false)
	private String title;

	@Lob
	private String contents;

	@Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP()")
	private LocalDateTime createDate;

	@Column(nullable = false)
	private LocalDateTime updateDate;

	@ColumnDefault("0")
	@Builder.Default
	private Integer count = 0;

	@ColumnDefault("false")
	@Column(nullable = false)
	private Boolean isDeleted;

	@ColumnDefault("0")
	@Builder.Default
	private Integer countOfAnswer = 0;

	public static FreeBoardEntity from(FreeBoard freeBoard) {
		return FreeBoardEntity.builder()
				.id(freeBoard.getId())
				.boardId(freeBoard.getBoardId())
				.writerId(freeBoard.getWriterId())
				.title(freeBoard.getTitle())
				.contents(freeBoard.getContents())
				.createDate(freeBoard.getCreateDate())
				.updateDate(freeBoard.getCreateDate())
				.count(freeBoard.getCount())
				.isDeleted(freeBoard.isDeleted())
				.countOfAnswer(freeBoard.getCountOfAnswer())
				.build();
	}

	public FreeBoard toModel() {
		return FreeBoard.builder()
				.id(id)
				.boardId(boardId)
				.writerId(writerId)
				.title(title)
				.contents(contents)
				.createDate(createDate)
				.updateDate(updateDate)
				.count(count)
				.countOfAnswer(countOfAnswer)
				.isDeleted(isDeleted)
				.build();
	}
}
