package com.web.app.service;

import com.web.app.dto.ReplyRequestDTO;
import com.web.app.dto.ReplyResponseDTO;
import com.web.app.exception.BusinessLogicException;
import jakarta.servlet.http.HttpServletRequest;

public interface ReplyService {

    ReplyResponseDTO register(ReplyRequestDTO replyRequestDTO);

    void update(ReplyRequestDTO replyRequestDTO, HttpServletRequest request) throws BusinessLogicException;

    void remove(Long id, HttpServletRequest request) throws BusinessLogicException;

}
