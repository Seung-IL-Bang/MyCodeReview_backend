package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.dto.CommentRequestDTO;
import com.web.app.exception.BusinessLogicException;
import com.web.app.exception.ExceptionCode;
import com.web.app.mediator.GetEmailFromJWT;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.CommentRepository;
import com.web.app.repository.MemberRepository;
import com.web.app.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    private final GetEmailFromJWT getEmailFromJWT;


    @Override
    public Comment register(CommentRequestDTO commentRequestDTO) {

        Member member = memberRepository.findById(commentRequestDTO.getMemberEmail()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 회원은 존재하지 않습니다.");
        });

        Board board = boardRepository.findById(commentRequestDTO.getBoardId()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 게시글은 존재하지 않습니다.");
        });

        Comment comment = commentRequestDTO.toEntity(null, board, member, commentRequestDTO.getContent());

        Comment save = commentRepository.save(comment);

        return save;
    }

    @Override
    public void update(CommentRequestDTO commentRequestDTO, HttpServletRequest request) throws BusinessLogicException {

        memberRepository.findById(commentRequestDTO.getMemberEmail()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 회원은 존재하지 않습니다.");
        });

        boardRepository.findById(commentRequestDTO.getBoardId()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 게시글은 존재하지 않습니다.");
        });

        Comment comment = commentRepository.findById(commentRequestDTO.getId()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 댓글은 존재하지 않습니다.");
        });

        String requestEmail = getEmailFromJWT.execute(request);

        if (!Objects.equals(comment.getMember().getEmail(), requestEmail)) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
        }

        comment.modify(commentRequestDTO.getContent());

        commentRepository.save(comment); // dirty check possible?
    }

    @Override
    public void remove(Long id, HttpServletRequest request) throws BusinessLogicException {

        Comment comment = commentRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("해당 댓글은 존재하지 않습니다.");
        });

        String requestEmail = getEmailFromJWT.execute(request);

        if (!Objects.equals(comment.getMember().getEmail(), requestEmail)) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
        }

        commentRepository.delete(comment);
    }
}
