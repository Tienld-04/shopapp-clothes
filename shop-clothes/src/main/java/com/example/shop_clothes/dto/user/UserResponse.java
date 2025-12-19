package com.example.shop_clothes.dto.user;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String fullname;
    private String email;
    private String address;
    private String phoneNumber;
    private LocalDate dateOfBirth;
}
