package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Comment;
import ru.practicum.model.CommentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment AS c " +
            "WHERE (?1 IS NULL OR c.commentator.id IN ?1) " +
            "AND (?2 IS NULL OR c.status IN ?2) " +
            "AND (?3 IS NULL OR c.event.id IN ?3) " +
            "AND (c.created >= ?4)"
    )
    List<Comment> findAllComments(List<Long> userIds,
                                  List<CommentStatus> statuses,
                                  List<Long> events,
                                  LocalDateTime created,
                                  Pageable pageable);

    @Query("SELECT c FROM Comment AS c " +
            "WHERE c.status = 'PUBLISHED' " +
            "AND c.created >= ?1")
    List<Comment> findPublishedComments(LocalDateTime created, Pageable pageable);

    List<Comment> findAllByCommentatorId(Long userId, Pageable pageable);

    Optional<Comment> findByIdAndCommentatorId(Long id, Long userId);
}
