package com.web.app.service;

import com.web.app.domain.comment.Comment;
import com.web.app.dto.CommentRequestDTO;
import com.web.app.dto.CommentResponseDTO;
import com.web.app.exception.BusinessLogicException;
import jakarta.servlet.http.HttpServletRequest;

public interface CommentService {

    CommentResponseDTO register(CommentRequestDTO commentRequestDTO);

    void update(CommentRequestDTO commentRequestDTO, HttpServletRequest request) throws BusinessLogicException;

    void remove(Long id, HttpServletRequest request) throws BusinessLogicException;
}
