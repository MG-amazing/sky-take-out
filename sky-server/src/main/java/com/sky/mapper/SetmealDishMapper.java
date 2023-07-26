package com.sky.mapper;


import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id去查对应的套餐id
     *
     * @param dishIds
     * @return
     */

    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    void insertBatch(List<SetmealDish> setmealDishList);

    @Select("select * from setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> getSetmealWithId(Long id);

    @Delete("delete from setmeal_dish where setmeal_id=#{setmealId}")
    void deleteById(Long setmealId);

    @Select("select *from setmeal_dish s left join dish d on s.dish_id = d.id " +
            "where s.setmeal_id=#{id}")
    List<Dish> getSetmealId(Long id);
}
