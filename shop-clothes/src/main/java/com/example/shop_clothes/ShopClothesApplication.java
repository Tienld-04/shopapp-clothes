package com.example.shop_clothes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ShopClothesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopClothesApplication.class, args);
    }

}
