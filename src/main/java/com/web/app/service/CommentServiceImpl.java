package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.dto.CommentRequestDTO;
import com.web.app.dto.CommentResponseDTO;
import com.web.app.exception.BusinessLogicException;
import com.web.app.exception.ExceptionCode;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.CommentRepository;
import com.web.app.repository.MemberRepository;
import com.web.app.repository.ReplyRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j(topic = "kafka-logger")
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final ReplyRepository replyRepository;

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    @Override
    public CommentResponseDTO register(CommentRequestDTO commentRequestDTO) {

        Member member = memberRepository.findById(commentRequestDTO.getMemberEmail()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 회원은 존재하지 않습니다.");
        });

        Board board = boardRepository.findById(commentRequestDTO.getBoardId()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 게시글은 존재하지 않습니다.");
        });

        Comment comment = commentRequestDTO.toEntity(null, board, member, commentRequestDTO.getContent());

        Comment save = commentRepository.save(comment);

        log.info(String.format("CREATE COMMENT: id=%d, board_id=%d, writer=%s, email=%s",
                save.getId(),
                board.getId(),
                member.getName(),
                member.getEmail()));
        return new CommentResponseDTO(save);
    }

    @Override
    public void update(CommentRequestDTO commentRequestDTO, HttpServletRequest request) throws BusinessLogicException {

        Member member = memberRepository.findById(commentRequestDTO.getMemberEmail()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 회원은 존재하지 않습니다.");
        });

        Board board = boardRepository.findById(commentRequestDTO.getBoardId()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 게시글은 존재하지 않습니다.");
        });

        Comment comment = commentRepository.findById(commentRequestDTO.getId()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 댓글은 존재하지 않습니다.");
        });

        String requestEmail = (String) request.getAttribute("userEmail");

        if (!Objects.equals(comment.getMember().getEmail(), requestEmail)) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
        }

        comment.modify(commentRequestDTO.getContent());

        commentRepository.save(comment);// dirty check possible?
        log.info(String.format("UPDATE COMMENT: id=%d, board_id=%d, writer=%s, email=%s",
                comment.getId(),
                board.getId(),
                member.getName(),
                member.getEmail()));
    }

    @Override
    public void remove(Long id, HttpServletRequest request) throws BusinessLogicException {

        Comment comment = commentRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("해당 댓글은 존재하지 않습니다.");
        });

        String requestEmail = (String) request.getAttribute("userEmail");

        Member member = comment.getMember();

        if (!Objects.equals(member.getEmail(), requestEmail)) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
        }

        replyRepository.deleteRepliesByCommentIs(comment);
        commentRepository.delete(comment);
        log.info(String.format("DELETE COMMENT: id=%d, writer=%s, email=%s",
                comment.getId(),
                member.getName(),
                member.getEmail()));
    }
}
