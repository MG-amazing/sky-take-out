package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * 套餐相关接口
 */
public interface SetmealService {
    /**
     * 新增套餐
     * @param setmealDTO
     */
    void addSetmeal(SetmealDTO setmealDTO);

    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO getById(Long id);

    void deleteBatch(List<Long> ids);

    void updateById(SetmealDTO setmealDTO);
}
