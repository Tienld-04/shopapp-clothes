package com.example.shop_clothes.controller;

import com.example.shop_clothes.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WebUiController {
    private final ProductService productService;

    @GetMapping("/index")
    public String productManagement(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", productService.getAllCategories());
        return "product-management";
    }
}
