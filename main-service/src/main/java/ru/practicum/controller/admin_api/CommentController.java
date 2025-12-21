package ru.practicum.controller.admin_api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.UpdateCommentStatusDto;
import ru.practicum.service.comment.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "admin/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllComment(@RequestParam(required = false) List<Long> users,
                                          @RequestParam(required = false) List<String> statuses,
                                          @RequestParam(required = false) List<Long> events,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime created,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        return commentService.getAllCommentsForAdmin(users, statuses, events, created, PageRequest.of(from / size, size));
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentStatus(@PathVariable Long commentId,
                                          @RequestBody @Valid UpdateCommentStatusDto updatedStatus) {
        return commentService.updateCommentStatusByAdmin(commentId, updatedStatus);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long commentId) {
        commentService.deleteById(commentId);
    }
}
