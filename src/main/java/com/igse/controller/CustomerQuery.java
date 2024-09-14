package com.igse.controller;

import com.igse.dto.IgseResponse;
import com.igse.dto.UserResponse;
import com.igse.entity.UserMaster;
import com.igse.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "customer", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerQuery {

    private final AdminService adminService;

    @GetMapping(path = "/all")
    public ResponseEntity<IgseResponse<List<UserMaster>>> allCustomerList(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "page", defaultValue = "10") int pageSize,
            @RequestParam(name = "role", defaultValue = "All") String role) {
        IgseResponse<List<UserMaster>> igseResponse = new IgseResponse<>();
        igseResponse.setData(adminService.getAllCustomerRecord(offset, pageSize, role));
        igseResponse.setStatus(HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(igseResponse);
    }

    @GetMapping(path = "/user/info/{customerId}")
    public ResponseEntity<IgseResponse<UserResponse>> dashboardData(
            @PathVariable String customerId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        IgseResponse<UserResponse> igseResponse = new IgseResponse<>();
        igseResponse.setData(adminService.dashBoardData(customerId, token.substring(7)));
        igseResponse.setStatus(HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(igseResponse);
    }
}
