package ru.practicum.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentStatusDto;
import ru.practicum.dto.comment.UpdatedCommentDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.*;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepo;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto getCommentById(Long commentId) {
        return CommentMapper.toDto(getComment(commentId));
    }

    @Override
    @Transactional
    public CommentDto updateCommentStatusByAdmin(Long commentId, UpdateCommentStatusDto updatedComment) {
        Comment comment = getComment(commentId);
        comment.setStatus(updatedComment.getStatus());
        return CommentMapper.toDto(commentRepo.save(comment));
    }

    @Override
    public List<CommentDto> getAllCommentsForAdmin(List<Long> userIds, List<String> statuses, List<Long> events, LocalDateTime created, Pageable pageable) {
        List<CommentStatus> commentStatuses = statuses == null ? null : statuses.stream().map(CommentStatus::valueOf).toList();
        created = getRangeCreated(created);
        return commentRepo.findAllComments(userIds, commentStatuses, events, created, pageable)
                .stream().map(CommentMapper::toDto).toList();
    }

    @Override
    @Transactional
    public void deleteById(Long commentId) {
        getComment(commentId);
        commentRepo.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getUsersComments(Long userId, Pageable pageable) {
        getUser(userId);
        return commentRepo.findAllByCommentatorId(userId, pageable).stream()
                .map(CommentMapper::toDto).toList();
    }

    @Override
    @Transactional
    public CommentDto createNewUserComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User commentator = getUser(userId);
        Event event = getEvent(eventId);
        Comment comment = CommentMapper.toEntityComment(newCommentDto, event, commentator);
        return CommentMapper.toDto(commentRepo.save(comment));
    }

    @Override
    public CommentDto getUserCommentById(Long userId, Long commentId) {
        getUser(userId);
        return CommentMapper.toDto(commentRepo.findByIdAndCommentatorId(commentId, userId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден")));
    }

    @Override
    @Transactional
    public CommentDto updateUserCommentById(Long userId, Long commentId, UpdatedCommentDto updateCommentUserRequest) {
        getUser(userId);
        Comment comment = getComment(commentId);
        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Редактировать комментарий можно в статусе в ожидании");
        }
        CommentMapper.commentFromUpdate(comment, updateCommentUserRequest);
        return CommentMapper.toDto(commentRepo.save(comment));
    }

    @Override
    public List<CommentDto> getPublishedComments(LocalDateTime created, Pageable pageable) {
        created = getRangeCreated(created);
        return commentRepo.findPublishedComments(created, pageable)
                .stream().map(CommentMapper::toDto).toList();
    }

    private Comment getComment(Long commentId) {
        return commentRepo.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = " + commentId + " не найден"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    private LocalDateTime getRangeCreated(LocalDateTime created) {
        return created == null ? LocalDateTime.of(1970, 1, 1, 0, 0, 0) : created;
    }
}
