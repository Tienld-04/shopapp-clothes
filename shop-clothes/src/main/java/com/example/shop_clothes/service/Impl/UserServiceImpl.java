package com.example.shop_clothes.service.Impl;

import com.example.shop_clothes.dto.user.LoginGoogleDTO;
import com.example.shop_clothes.dto.user.UserCreateRequest;
import com.example.shop_clothes.dto.user.UserResponse;
import com.example.shop_clothes.enums.AuthProvider;
import com.example.shop_clothes.exception.ApplicationException;
import com.example.shop_clothes.exception.ErrorCode;
import com.example.shop_clothes.mapper.UserMapper;
import com.example.shop_clothes.model.Role;
import com.example.shop_clothes.model.User;
import com.example.shop_clothes.repository.RoleRepository;
import com.example.shop_clothes.repository.UserRepository;
import com.example.shop_clothes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private  final RoleRepository roleRepository;

    @Override
    public UserResponse createUser(UserCreateRequest userCreateRequest) {
        if(userRepository.existsByEmail(userCreateRequest.getEmail())) {
            throw new ApplicationException(ErrorCode.EMAIL_EXISTED);
        }
        if(userCreateRequest.getPhoneNumber() != null){
            if(userRepository.existsByPhoneNumber(userCreateRequest.getPhoneNumber())) {
                throw new ApplicationException(ErrorCode.PHONENUMBER_EXISTED);
            }
        }
        User user = userMapper.toUser(userCreateRequest);
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        Role role = roleRepository.findByName("USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setIsActive(true);
        user.setProvider(AuthProvider.LOCAL);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse createUserWithGoogle(LoginGoogleDTO loginGoogleDTO) {
        if (userRepository.existsByEmail(loginGoogleDTO.getEmail())) {
            return null;
        } else {
            User user = new User();
            user.setEmail(loginGoogleDTO.getEmail());
            user.setPassword(null);
            user.setPhoneNumber(null);
            user.setAvatarUrl(loginGoogleDTO.getAvatarUrl());
            user.setFullname(loginGoogleDTO.getFullName());
            user.setProviderId(loginGoogleDTO.getProviderId());
            user.setProvider(AuthProvider.GOOGLE);
            Role role = roleRepository.findByName("USER");
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
            return userMapper.toUserResponse(userRepository.save(user));
        }
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return userMapper.toUserResponse(user);
    }


}
