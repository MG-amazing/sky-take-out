package com.sky;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DishService dishService;

    @Test
    public void testList() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get("/user/dish/list")
                                .param("categoryId", "12")
                                .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJlbXBJZCI6MSwiZXhwIjoxNzcxMTAzNTk5fQ.4h6bybKmig0h_Q5Wgt5Y4RPah9cucRFthb0ONfq6yBk")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().isOk());
        System.out.println( resultActions.andReturn().getResponse().getContentAsString());
        if (true) {
            return;
        }
        //构造redis中的key，构造dish分类id
        String categoryId = "12";
        String key="dish_"+categoryId;
        //查询redis中是否存在菜品数据
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);

        if (CollUtil.isNotEmpty(list)) {
            check(list);

        }else {

            //如果存在，直接返回，如果不存在，查询数据库，将查询的数据注入redis中
            Dish dish = new Dish();
            dish.setCategoryId(Long.valueOf(categoryId));
            dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

            list = dishService.listWithFlavor(dish);

            redisTemplate.opsForValue().set(key, list);

            check(list);

        }
    }
    private void check(List<DishVO> list){
        assertTrue(list.size() == 2);

        //3.驗證"description"是不是"优质面粉"
        assertEquals("优质面粉", list.get(0).getDescription());
    }


}
