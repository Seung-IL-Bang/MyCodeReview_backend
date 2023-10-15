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
    public ResponseEntity postBulkBoards(HttpServletRequest request,
                                         @Positive @RequestParam("numberOfBoards") Long numberOfBoards,
                                         @Positive @RequestParam("numberOfMembers") Long numberOfMembers,
                                         @Positive @RequestParam("numberOfComments") Long numberOfComments) {
        ApiResponse<Object> response = adminService.bulkInsertDummyData(request, numberOfBoards, numberOfMembers, numberOfComments);
        return new ResponseEntity(response.getData(), response.getStatus());
    }
}
