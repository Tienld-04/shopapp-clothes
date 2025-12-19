package com.example.shop_clothes.repository;

import com.example.shop_clothes.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(@NotBlank(message = "Email not null") @Email(message = "Email is not in correct format") String email);
    User findByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
