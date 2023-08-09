package com.web.app.service;

import com.web.app.dto.LikeRequestDTO;
import com.web.app.exception.BusinessLogicException;

public interface LikesService {

    void postLike(LikeRequestDTO likeRequestDTO) throws BusinessLogicException;

    void deleteLike(LikeRequestDTO likeRequestDTO) throws BusinessLogicException;
}
