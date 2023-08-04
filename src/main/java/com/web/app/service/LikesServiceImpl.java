package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.likes.Likes;
import com.web.app.domain.member.Member;
import com.web.app.dto.LikeRequestDTO;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.LikesRepository;
import com.web.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService{

    private final LikesRepository likesRepository;

    private final MemberRepository memberRepository;

    private final BoardRepository boardRepository;

    @Override
    public void postLike(LikeRequestDTO likeRequestDTO) {

        // TODO: 중복 좋아요 예외 처리

        Optional<Member> optionalMember = memberRepository.findById(likeRequestDTO.getMemberEmail());
        Optional<Board> optionalBoard = boardRepository.findById(likeRequestDTO.getBoardId());

        // TODO: 옵셔널 예외 처리 추가
        Member member = optionalMember.orElseThrow();
        Board board = optionalBoard.orElseThrow();

        Likes likes = likeRequestDTO.toEntity(board, member);

        likesRepository.save(likes);
    }
}
