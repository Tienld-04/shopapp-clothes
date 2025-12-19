package com.example.shop_clothes.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {
    @NotBlank(message = "Email not null")
    @Email(message = "Email is not in correct format")
    private String email;

    @NotBlank(message = "Password not null")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "FullName not null")
    private String fullname;

    @NotBlank(message = "Address not null")
    private String address;

    private String phoneNumber;


}
