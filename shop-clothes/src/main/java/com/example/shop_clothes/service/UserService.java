package com.example.shop_clothes.service;

import com.example.shop_clothes.dto.user.LoginGoogleDTO;
import com.example.shop_clothes.dto.user.UserCreateRequest;
import com.example.shop_clothes.dto.user.UserResponse;

public interface UserService {
    UserResponse createUser(UserCreateRequest userCreateRequest );
    UserResponse createUserWithGoogle(LoginGoogleDTO loginGoogleDTO);
}
