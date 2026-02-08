package com.sky.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.TingshouDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@Service
@Slf4j

public class DishServiceImpl implements DishService {

    /**
     * 新增菜品和口味信息
     *
     * @param dishDTO
     */
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表插入数据、插入一条数据
        dishMapper.insert(dish);

        //获取insert语句生成的主键值
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }


    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //1.判断菜品是否起售中
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //起售中不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //2.判断菜品是否和套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            //菜品被套餐关联不允许删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //3.可以删除，删除菜品表中的菜品数据
        for (Long id : ids) {
            dishMapper.deleteById(id);
            //4.删除菜品关联的口味数据
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    /**
     * 根据菜品id查询菜品信息
     *
     * @param id
     * @return
     */
    public DishVO getById(Long id) {
        //查询菜品数据
        Dish dish = dishMapper.getById(id);
        //根据菜品id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);


        return dishVO;
    }

    /**
     * 更新菜品
     *
     * @param dishDTO
     */
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //删除口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }


    }

    /**
     * 根据id修改售卖状态
     *
     * @param status
     * @param id
     */
    public void updateStatus(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    public List<Dish> getBycategoryId(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.getByCategoryId(dish);
    }


    /**
     * 条件查询菜品和口味
     *
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.getByCategoryId(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    @Override
    public boolean selectByid(Long id) {
        List<TingshouDTO> ts = dishMapper.selectByid(id);
        AtomicBoolean flag = new AtomicBoolean(false);
        for (TingshouDTO d : ts) {
            if (d.getStatus() == 1||d.getStatus()==2||d.getStatus()==3) {
                flag.set(true);
                break;
            }
        }

        return flag.get();
    }

    @Override
    public void xinzeng(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert1(dish);
        if (CollUtil.isNotEmpty(dishDTO.getFlavors())) {
            List<DishFlavor> flavors = dishDTO.getFlavors();
            for (int i = 0; i < flavors.size(); i++) {
                flavors.get(i).setDishId(dish.getId());
            }
            dishFlavorMapper.insertBatch(dishDTO.getFlavors());

        }



    }



}
