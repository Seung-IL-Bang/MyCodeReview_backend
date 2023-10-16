package com.web.app.controller;

import com.web.app.dto.ApiResponse;
import com.web.app.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @GetMapping("/auth/admin")
    public ApiResponse<Object> testAdminAPI(HttpServletRequest request) {

        ApiResponse<Object> response = adminService.testAdminAuthority(request);

        return response;
    }

    @PostMapping("/auth/admin/bulk-insert")
    public ResponseEntity bulkInsertDummyData(HttpServletRequest request,
                                         @Positive @RequestParam("numberOfMembers") Long numberOfMembers,
                                         @Positive @RequestParam("numberOfBoards") Long numberOfBoards,
                                         @Positive @RequestParam("numberOfReviews") Long numberOfReviews,
                                         @Positive @RequestParam("numberOfComments") Long numberOfComments,
                                         @Positive @RequestParam("numberOfReplies") Long numberOfReplies,
                                         @Positive @RequestParam("numberOfLikes") Long numberOfLikes) {
        ApiResponse<Object> response = adminService.bulkInsertDummyData(
                request,
                numberOfMembers,
                numberOfBoards,
                numberOfReviews,
                numberOfComments,
                numberOfReplies,
                numberOfLikes);
        return new ResponseEntity(response.getData(), response.getStatus());
    }
}
