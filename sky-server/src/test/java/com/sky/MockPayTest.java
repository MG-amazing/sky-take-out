package com.sky;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.constant.StatusConstant;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.User;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.OrderService;
import com.sky.service.SetmealService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
public class MockPayTest {
    /**
     * 1.前端点击支付
     * 2.调用你这个接口 /payment
     * 3.后端调用微信统一下单接口
     * 4.微信返回预支付信息（prepay_id）
     * 5️.后端把支付参数返回前端
     * 6️.前端调起微信支付SDK
     * 7️.用户支付
     * 8️.微信回调后台 notify 接口
     * 9️.后台修改订单状态为“已支付”
     */

    @Autowired
    private OrderService orderService;
    @MockBean
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SetmealService setmealService;
    @MockBean
    private SetmealMapper setmealMapper;

    @Test
    public void payTest() throws Exception {

        String s = "{\n" + " \"timeStamp\": \"1707987600\",\n" + " \"nonceStr\": \"5K8264ILTKCH16CQ2502SI8ZNMTM67VS\",\n" + " \"package\": \"prepay_id=wx201410272009395522657a690389285100\",\n" + " \"signType\": \"RSA\",\n" + " \"paySign\": \"签名字符串\"\n" + "}\n";
        OrderPaymentVO vo1 = objectMapper.readValue(s, OrderPaymentVO.class);
        JSONObject jsonObject = new JSONObject(objectMapper.convertValue(vo1, Map.class));
        when(userMapper.getById(any())).thenReturn(User.builder().id(1L).build());
        when(weChatPayUtil.pay(
                any(),
                any(),
                any(),
                any()
        )).thenReturn(jsonObject);


        OrdersPaymentDTO dto = new OrdersPaymentDTO();
        dto.setPayMethod(1);
        dto.setOrderNumber("3333");


        OrderPaymentVO payment = orderService.payment(dto);
        System.out.println(payment);

    }

    @Test
    public void cancelTest2() throws Exception {
        Long id = 11L;
        when(weChatPayUtil.refund(
                any(), //商户订单号
                any(), //商户退款单号
                any(),//退款金额，单位 元
                any())).thenReturn("");
        orderService.userCancelById(id);
    }

    @Test
    public void listTest()  {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(StatusConstant.ENABLE);
        List<Setmeal> setmeals = new ArrayList<>();
        setmeals.add(setmeal);
        when( setmealMapper.list(any())).thenReturn(setmeals);
        List<Setmeal> list = setmealService.list(setmeal);
        System.out.println(list);

    }


}
