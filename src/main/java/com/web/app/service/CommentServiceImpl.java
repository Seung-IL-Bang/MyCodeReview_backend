package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.dto.CommentRequestDTO;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.CommentRepository;
import com.web.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;


    @Override
    public void register(CommentRequestDTO commentRequestDTO) {

        Member member = memberRepository.findById(commentRequestDTO.getMemberEmail()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 회원은 존재하지 않습니다.");
        });

        Board board = boardRepository.findById(commentRequestDTO.getBoardId()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 게시글은 존재하지 않습니다.");
        });

        Comment comment = commentRequestDTO.toEntity(board, member, commentRequestDTO.getContent());

        commentRepository.save(comment);
    }
}
