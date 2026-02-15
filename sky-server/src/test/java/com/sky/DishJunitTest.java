package com.sky;


import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.*;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import com.sky.result.Result;
import com.sky.service.DishService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DishJunitTest {

    @Autowired
    private DishService dishService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private Validator validator;
    @Autowired
    private RedisTemplate redisTemplate;



    @Test
    public void dengLu() {

        //判空TODO  待完成
        //字符串
        String a="";


        //創建接慘DTO
        EmployeeLoginDTO employeeLoginDTO = new EmployeeLoginDTO();
        employeeLoginDTO.setUsername("admin");
        employeeLoginDTO.setPassword("123456");
        //調用service方法
        //根据用户名查询数据库中的数据，核對用戶名和密碼，返回實體類對象
        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();
        System.out.println(employeeLoginVO.getToken());
    }

    @Test
    public void xinZeng() {
        //創建接慘DTO
        DishDTO dishDTO = new DishDTO();
        //set所需字段的值
        dishDTO.setId(1L);
        dishDTO.setImage("1");
        dishDTO.setName("1");
        dishDTO.setPrice(BigDecimal.valueOf(0.1));
        dishDTO.setStatus(1);
        dishDTO.setCategoryId(15L);
        DishFlavor flavor = new DishFlavor();
        flavor.setDishId(1L);
        flavor.setName("1");
        flavor.setId(1L);
        flavor.setValue("1");
        List<DishFlavor> flavorList = new ArrayList<>();
        flavorList.add(flavor);
        dishDTO.setFlavors(flavorList);
        //調用service實現新增
        dishService.xinzeng(dishDTO);
    }

    @Test
    public void xiuGai() {
        //創建接慘DTO
        DishDTO dishDTO = new DishDTO();
        dishDTO.setId(46L);
        dishDTO.setImage("1");
        dishDTO.setName("1");
        dishDTO.setPrice(BigDecimal.valueOf(0.1));
        dishDTO.setStatus(1);
        dishDTO.setCategoryId(15L);
        dishDTO.setDescription("1");
        //用valid校驗數據是否合規
        Set<ConstraintViolation<DishDTO>> violations = validator.validate(dishDTO);
        //assertEquals判斷是否有不合規數據結果
        assertEquals(0, violations.size());

        DishFlavor flavor = new DishFlavor();
        flavor.setDishId(1L);
        flavor.setName("1");
        flavor.setId(1L);
        flavor.setValue("1");

        List<DishFlavor> flavorList = new ArrayList<>();
        flavorList.add(flavor);
        dishDTO.setFlavors(flavorList);
        //調用service修改數據
        dishService.updateWithFlavor(dishDTO);

    }

    @Test
    public void piLiangShanChu() {
        //創建接慘list對象
        List<Long> ids = List.of(69L);
        //查找redis中所有以dish_開頭的key
        Set keys = redisTemplate.keys("dish_*");
        //刪除redis中的一組key
        redisTemplate.delete(keys);
        //調用service刪除指定數據
        dishService.deleteBatch(ids);
    }

    @Test
    public void chaXun() {
        //創建變量並附上初始值，用於模擬從前端接受的id
        Long categoryId = 21L;
        //調用service，用id查出數據庫中的那條數據
        List<Dish> dish = dishService.getBycategoryId(categoryId);
        //打印數據
        System.out.println(dish);
    }

    @Test
    public void chaFenYe() {
        DishPageQueryDTO dishPageQueryDTO = new DishPageQueryDTO();
        //按照前端傳進來的頁數和size進行分頁
        // d.category_id = c.id作爲主鍵，根據name，categoryId，status返回查詢結果
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        //打印查詢結果
        System.out.println(pageResult);
    }

    @Test
    public void zhuangTaiXiuGai() {
        //創建變量id和status並附上初始值，用於模擬前端數據
        Integer status = 0;
        Long id = 50L;
        //用id查詢訂單狀態並返回
        boolean ts = dishService.selectByid(id);
        //ts=false（123)为未出餐则不能修改状态，但status为0（停售）时可以修改为起售
        if (status == 0 && ts) {
            System.out.println("订单未完成，修改失败");
        }
        //ts=true（4567)为已出餐，执行修改状态
        dishService.updateStatus(status, id);
    }

}
