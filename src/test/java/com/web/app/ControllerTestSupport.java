package com.web.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.app.controller.*;
import com.web.app.proxy.LikesUseCase;
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
        ReviewController.class,
        AdminController.class
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

    // CommentController
    @MockBean
    protected CommentService commentService;

    // LikesController
    @MockBean
    protected LikesUseCase likesUseCase;

    // MemberController
    @MockBean
    protected MemberService memberService;

    // ReplyController
    @MockBean
    protected ReplyService replyService;

    // ReviewController
    @MockBean
    protected ReviewService reviewService;

    // AdminController
    @MockBean
    protected AdminService adminService;
}
