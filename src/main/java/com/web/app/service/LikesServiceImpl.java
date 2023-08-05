package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.likes.Likes;
import com.web.app.domain.member.Member;
import com.web.app.dto.LikeRequestDTO;
import com.web.app.exception.BusinessLogicException;
import com.web.app.exception.ExceptionCode;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.LikesRepository;
import com.web.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService{

    private final LikesRepository likesRepository;

    private final MemberRepository memberRepository;

    private final BoardRepository boardRepository;

    @Override
    public void postLike(LikeRequestDTO likeRequestDTO) throws BusinessLogicException {

        List<Long> liked = likesRepository.isLiked(likeRequestDTO.getBoardId(), likeRequestDTO.getMemberEmail());
        if (!liked.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.NOT_DUPLICATE_LIKE);
        }

        Member member = memberRepository.findById(likeRequestDTO.getMemberEmail()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 회원은 존재하지 않습니다.");
        });

        Board board = boardRepository.findById(likeRequestDTO.getBoardId()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 게시글은 존재하지 않습니다.");
        });

        Likes likes = likeRequestDTO.toEntity(board, member);

        likesRepository.save(likes);
    }

    @Override
    public void deleteLike(LikeRequestDTO likeRequestDTO) throws BusinessLogicException {

        memberRepository.findById(likeRequestDTO.getMemberEmail()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 회원은 존재하지 않습니다.");
        });
        boardRepository.findById(likeRequestDTO.getBoardId()).orElseThrow(() -> {
            throw new NoSuchElementException("해당 게시글은 존재하지 않습니다.");
        });


        List<Long> liked = likesRepository.isLiked(likeRequestDTO.getBoardId(), likeRequestDTO.getMemberEmail());
        if (liked.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.EMPTY_LIKE);
        }

        likesRepository.deleteById(liked.get(0));
    }


}
