package com.web.app.service;

import com.web.app.dto.LikeRequestDTO;

public interface LikesService {

    void postLike(LikeRequestDTO likeRequestDTO);
}
