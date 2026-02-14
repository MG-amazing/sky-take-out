package com.sky.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class UserDTO {

    @NotBlank
    private String username;   // 必须项

    @NotNull
    @Min(18)
    @Max(60)
    private Integer age;       // 必须项 + 范围限制

}
