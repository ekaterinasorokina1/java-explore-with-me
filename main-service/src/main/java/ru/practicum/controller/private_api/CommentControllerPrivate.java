package ru.practicum.controller.private_api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdatedCommentDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "users")
public class CommentControllerPrivate {
    public final CommentService commentService;

    @GetMapping("/{userId}/comments")
    public List<CommentDto> getAllCommentsByUserId(@PathVariable Long userId,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        return commentService.getUsersComments(userId, PageRequest.of(from / size, size));
    }

    @PostMapping("/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createCommentByUserId(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.createNewUserComment(userId, eventId, newCommentDto);
    }

    @GetMapping("/{userId}/comments/{commentId}")
    public CommentDto getCommentByUserId(@PathVariable Long userId, @PathVariable Long commentId) {
        return commentService.getUserCommentById(userId, commentId);
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    public CommentDto updateEventByUserId(@PathVariable Long userId,
                                          @PathVariable Long commentId,
                                          @RequestBody @Valid UpdatedCommentDto updateCommentUserRequest) {
        return commentService.updateUserCommentById(userId, commentId, updateCommentUserRequest);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long commentId) {
        commentService.deleteById(commentId);
    }
}
