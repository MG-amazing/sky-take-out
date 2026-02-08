package com.sky.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TingshouDTO {
    private Long id;
    private String name;
    private String number;
    private Integer status;
    private LocalDateTime orderTime;
}
