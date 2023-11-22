package com.web.app.service;


import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.domain.reply.Reply;
import com.web.app.dto.ReplyRequestDTO;
import com.web.app.dto.ReplyResponseDTO;
import com.web.app.exception.BusinessLogicException;
import com.web.app.exception.ExceptionCode;
import com.web.app.repository.CommentRepository;
import com.web.app.repository.MemberRepository;
import com.web.app.repository.ReplyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j(topic = "kafka-logger")
@Service
@Transactional
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;

    private final CommentRepository commentRepository;

    private final MemberRepository memberRepository;


    @Override
    public ReplyResponseDTO register(ReplyRequestDTO replyRequestDTO) {

        Member member = memberRepository.findById(replyRequestDTO.getMemberEmail()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 회원은 존재하지 않습니다.");
        });

        Comment comment = commentRepository.findById(replyRequestDTO.getCommentId()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 댓글은 존재하지 않습니다.");
        });

        Reply reply = replyRequestDTO.toEntity(null, member, comment);

        Reply saved = replyRepository.save(reply);
        log.info(String.format("CREATE REPLY: id=%d, comment_id=%d, writer=%s, email=%s",
                saved.getId(),
                comment.getId(),
                member.getName(),
                member.getEmail()));
        return new ReplyResponseDTO(saved);
    }

    @Override
    public void update(ReplyRequestDTO replyRequestDTO, HttpServletRequest request) throws BusinessLogicException {

        Member member = memberRepository.findById(replyRequestDTO.getMemberEmail()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 회원은 존재하지 않습니다.");
        });

        Comment comment = commentRepository.findById(replyRequestDTO.getCommentId()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 댓글은 존재하지 않습니다.");
        });

        Reply reply = replyRepository.findById(replyRequestDTO.getId()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 답글을 존재하지 않습니다.");
        });

        String requestEmail = (String) request.getAttribute("userEmail");


        if (!Objects.equals(reply.getMember().getEmail(), requestEmail)) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
        }

        reply.modify(replyRequestDTO.getContent());

        replyRepository.save(reply);
        log.info(String.format("UPDATE REPLY: id=%d, comment_id=%d, writer=%s, email=%s",
                reply.getId(),
                comment.getId(),
                member.getName(),
                member.getEmail()));
    }

    @Override
    public void remove(Long id, HttpServletRequest request) throws BusinessLogicException {

        Reply reply = replyRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("해당 답글은 존재하지 않습니다.");
        });

        String requestEmail = (String) request.getAttribute("userEmail");

        Member member = reply.getMember();

        if (!Objects.equals(member.getEmail(), requestEmail)) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
        }

        replyRepository.delete(reply);
        log.info(String.format("DELETE REPLY: id=%d, writer=%s, email=%s",
                reply.getId(),
                member.getName(),
                member.getEmail()));
    }
}
