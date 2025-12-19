package com.example.shop_clothes.controller;

import com.example.shop_clothes.dto.user.UserCreateRequest;
import com.example.shop_clothes.dto.user.UserResponse;
import com.example.shop_clothes.repository.UserRepository;
import com.example.shop_clothes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/my-info")
    public ResponseEntity<UserResponse> getUser() {
        UserResponse userResponse = userService.getMyInfo();
        return ResponseEntity.ok(userResponse);
    }


}
