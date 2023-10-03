package com.web.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.app.controller.*;
import com.web.app.mediator.GetBoardListFromEmailOfJWT;
import com.web.app.mediator.GetEmailFromJWT;
import com.web.app.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        BoardController.class,
        CommentController.class,
        LikesController.class,
        MemberController.class,
        ReplyController.class,
        ReviewController.class
})
public abstract class ControllerTestSupport {

    // Common
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    // MemberController, ReviewController
    @MockBean
    protected ModelMapper modelMapper;

    // BoardController
    @MockBean
    protected BoardService boardService;

    @MockBean
    protected GetBoardListFromEmailOfJWT getBoardListFromEmailOfJWT;

    @MockBean
    protected GetEmailFromJWT getEmailFromJWT;

    // CommentController
    @MockBean
    protected CommentService commentService;

    // LikesController
    @MockBean
    protected LikesService likesService;

    // MemberController
    @MockBean
    protected MemberService memberService;

    // ReplyController
    @MockBean
    protected ReplyService replyService;

    // ReviewController
    @MockBean
    protected ReviewService reviewService;


}
