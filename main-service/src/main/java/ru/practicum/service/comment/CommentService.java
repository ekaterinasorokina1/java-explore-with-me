package ru.practicum.service.comment;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentStatusDto;
import ru.practicum.dto.comment.UpdatedCommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    public CommentDto getCommentById(Long commentId);

    public CommentDto updateCommentStatusByAdmin(Long commentId, UpdateCommentStatusDto updatedComment);

    public List<CommentDto> getAllCommentsForAdmin(List<Long> userIds,
                                                   List<String> statuses,
                                                   List<Long> events,
                                                   LocalDateTime created,
                                                   Pageable pageable);

    public void deleteById(Long commentId);

    public List<CommentDto> getUsersComments(Long userId, Pageable pageable);

    public CommentDto createNewUserComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    public CommentDto getUserCommentById(Long userId, Long commentId);

    public CommentDto updateUserCommentById(Long userId, Long commentId, UpdatedCommentDto updateCommentUserRequest);

    public List<CommentDto> getPublishedComments(LocalDateTime created, Pageable pageable);

}
