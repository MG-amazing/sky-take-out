package com.sky.controller.admin;
/**
 * 员工管理
 */

import com.sky.dto.SetmealDTO;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐接口")
public class SetmealController {


    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增套餐")
    public Result<SetmealDTO> addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("{}",setmealDTO);
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }




}
