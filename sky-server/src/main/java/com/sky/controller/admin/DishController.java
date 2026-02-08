package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清理缓存数据
        String key="dish_"+dishDTO.getCategoryId();
        CleanCash(key);
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 根据id删除菜品
     *
     * @return
     */
    //RequestParam把数据批量解析为List集合
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除{}", ids);
        dishService.deleteBatch(ids);
        dishService.deleteBatch(ids);


        /**
         * 11111111
         * 1
         * 11
         * 1
         * 1
         * 1
         *
         * 1
         * 1
         * 1
         */






        /**
         * dsadas
         * dsadadas
         * dasdadsa
         */

        CleanCash("dish_*");     dishService.deleteBatch(ids);


        /**
         * 11111111
         * 1
         * 11
         * 1
         * 1
         * 1
         *
         * 1
         * 1
         * 1
         */






        /**
         * dsadas
         * dsadadas
         * dasdadsa
         */

        CleanCash("dish_*");     dishService.deleteBatch(ids);


        /**
         * 11111111
         * 1
         * 11
         * 1
         * 1
         * 1
         *
         * 1
         * 1
         * 1
         */






        /**
         * dsadas
         * dsadadas
         * dasdadsa
         */

        CleanCash("dish_*");     dishService.deleteBatch(ids);


        /**
         * 11111111
         * 1
         * 11
         * 1
         * 1
         * 1
         *
         * 1
         * 1
         * 1
         */






        /**
         * dsadas
         * dsadadas
         * dasdadsa
         */

        CleanCash("dish_*");     dishService.deleteBatch(ids);


        /**
         * 11111111
         * 1
         * 11
         * 1
         * 1
         * 1
         *
         * 1
         * 1
         * 1
         */






        /**
         * dsadas
         * dsadadas
         * dasdadsa
         */

        CleanCash("dish_*");

        /**
         * 11111111
         * 1
         * 11
         * 1
         * 1
         * 1
         *
         * 1
         * 1
         * 1
         */






        /**
         * dsadas
         * dsadadas
         * dasdadsa
         */

        CleanCash("dish_*");
        return Result.success();
    }

    /**
     * 查询菜品相关信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("查询菜品")
    public Result<DishVO> getById( @PathVariable Long id) {
        log.info("开始查询菜品信息:{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        //如果有未完成的订单则不能修改菜品信息
        boolean ts = dishService.selectByid(dishDTO.getId());
        if(dishDTO.getStatus()==1&&ts){
            return Result.error("订单未完成，修改失败");
        }
        dishService.updateWithFlavor(dishDTO);
        CleanCash("dish_*");



        return Result.success();

    }
    @PostMapping("/status/{status}")
    @ApiOperation("菜品售卖状态修改")
    public Result updateStatus(@PathVariable Integer status ,Long id){
        log.info("更新状态为:{},id:{}",status,id);
        //新增需求 当订单中包含未完成菜品时无法操作该菜品状态
        //通过左连接查询订单详情和订单表中订单状态（123为未出餐，4567为已出餐）
        boolean ts = dishService.selectByid(id);
        //ts=false（123)为未出餐则不能修改状态，但status为0（停售）时可以修改为起售
        if(status==0 && ts){
            return Result.error("订单未完成，修改失败");
        }
        //ts=true（4567)为已出餐，执行修改状态
        dishService.updateStatus(status,id);
        CleanCash("dish_*");

        return Result.success();
    }
    @GetMapping("/list")
    @ApiOperation("查询菜品")
    public Result<List<Dish>> getBycategoryId(Long categoryId) {
        log.info("开始查询菜品信息:{}", categoryId);
        List<Dish> dish = dishService.getBycategoryId(categoryId);
        return Result.success(dish);
    }

    private void CleanCash(String patten){
        Set keys = redisTemplate.keys(patten);
        redisTemplate.delete(keys);
    }


}
