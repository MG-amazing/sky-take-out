package com.sky.controller.admin;

import com.sky.dto.UserDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserControllerCCC {

    @PostMapping
    public String create(@RequestBody @Validated UserDTO dto) {
        return "ok";
    }
}
