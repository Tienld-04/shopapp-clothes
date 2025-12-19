package com.example.shop_clothes.mapper;

import com.example.shop_clothes.dto.user.UserCreateRequest;
import com.example.shop_clothes.dto.user.UserResponse;
import com.example.shop_clothes.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest userCreateRequest);
    UserResponse toUserResponse(User user);

}
