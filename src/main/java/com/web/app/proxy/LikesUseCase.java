package com.web.app.proxy;

import com.web.app.dto.LikeRequestDTO;
import com.web.app.exception.BusinessLogicException;
import com.web.app.exception.ExceptionCode;
import com.web.app.service.LikesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Slf4j(topic = "kafka-logger")
@RequiredArgsConstructor
@Service
public class LikesUseCase {

    private final LikesService likesService;

    public void executePost(LikeRequestDTO likeRequestDTO) throws BusinessLogicException {
        try {
            likesService.postLike(likeRequestDTO);
            log.info(String.format("LIKE: board_id=%d, email=%s", likeRequestDTO.getBoardId(), likeRequestDTO.getMemberEmail()));
        } catch (ObjectOptimisticLockingFailureException e) {
            int maxAttempt = 3;
            for (int attempt = 1; attempt <= maxAttempt; attempt++) {
                try {
                    likesService.postLike(likeRequestDTO);
                    log.info(String.format("LIKE: board_id=%d, email=%s", likeRequestDTO.getBoardId(), likeRequestDTO.getMemberEmail()));
                    break;
                } catch (ObjectOptimisticLockingFailureException oe) {
                    if (attempt == maxAttempt) {
                        throw new BusinessLogicException(ExceptionCode.LOCKING_FAILURE);
                    }
                }
            }
        }
    }

    public void executeDelete(LikeRequestDTO likeRequestDTO) throws BusinessLogicException{
        try {
            likesService.deleteLike(likeRequestDTO);
            log.info(String.format("UNLIKE: board_id=%d, email=%s", likeRequestDTO.getBoardId(), likeRequestDTO.getMemberEmail()));
        } catch (ObjectOptimisticLockingFailureException e) {
            int maxAttempt = 3;
            for (int attempt = 1; attempt <= maxAttempt; attempt++) {
                try {
                    likesService.deleteLike(likeRequestDTO);
                    log.info(String.format("UNLIKE: board_id=%d, email=%s", likeRequestDTO.getBoardId(), likeRequestDTO.getMemberEmail()));
                    break;
                } catch (ObjectOptimisticLockingFailureException oe) {
                    if (attempt == maxAttempt) {
                        throw new BusinessLogicException(ExceptionCode.LOCKING_FAILURE);
                    }
                }
            }
        }
    }

}
