package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.UserDto;
import com.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAnyAuthority('ADMIN','HR')")
    public ResponseEntity<ApiResponse> getUsers(@RequestParam(required = false) String search,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        Page<UserDto> users = userService.getUsers(search, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Users fetched successfully").data(users).build());
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','HR')")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("User fetched successfully").data(userService.getUserById(id)).build());
    }
}
