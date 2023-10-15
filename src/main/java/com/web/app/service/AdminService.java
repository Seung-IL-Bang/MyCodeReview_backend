package com.web.app.service;

import com.web.app.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminService {

    ApiResponse<Object> testAdminAuthority(HttpServletRequest request);

    ApiResponse<Object> bulkInsertDummyData(HttpServletRequest request,
                                            Long numberOfBoards,
                                            Long numberOfMembers,
                                            Long numberOfComments);
}
