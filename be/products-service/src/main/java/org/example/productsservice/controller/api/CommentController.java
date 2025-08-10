package org.example.productsservice.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.productsservice.dto.CommentRequestBodyDto;
import org.example.productsservice.entity.CommentEntity;
import org.example.productsservice.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{productId}")
    public ResponseEntity<?> getCommentByProductId(@PathVariable Long productId) {
        List<CommentEntity> comments = commentService.getCommentByProductId(productId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentRequestBodyDto comment) {
        try {
            CommentEntity createdComment = commentService.addComment(comment);
            return ResponseEntity.ok(createdComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
