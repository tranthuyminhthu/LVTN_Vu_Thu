package org.example.productsservice.service;

import lombok.RequiredArgsConstructor;
import org.example.productsservice.dao.CommentDao;
import org.example.productsservice.dao.ProductDao;
import org.example.productsservice.dto.CommentRequestBodyDto;
import org.example.productsservice.entity.CommentEntity;
import org.example.productsservice.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentDao commentDao;
    private final ProductDao productDao;
    private final UserContext userContext;

    public List<CommentEntity> getCommentByProductId(Long productId) {
        return commentDao.findByProductId(productId);
    }

    public CommentEntity addComment(CommentRequestBodyDto comment) {
        if (!productDao.existsById(comment.getProductId())) {
            throw new IllegalArgumentException("Product with ID " + comment.getProductId() + " does not exist.");
        }
        String userId = userContext.getUserId();
        if (userId == null) {
            throw new IllegalArgumentException("User ID not found in request context.");
        }
        String username = userContext.getUsername();
        CommentEntity commentEntity = new CommentEntity();
        BeanUtils.copyProperties(comment, commentEntity);
        commentEntity.setUserId(userId);
        commentEntity.setUserName(username);
        return commentDao.save(commentEntity);
    }

}
