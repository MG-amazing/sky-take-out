package com.sky;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.dto.*;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.EmployeeService;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.service.impl.ShoppingCartServiceImpl;
import com.sky.vo.OrderSubmitVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.xml.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = SkyApplication.class)
public class ShoppingTest {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private EmployeeService employeeService;



    @Test
    public void test() throws Exception {
        //1.添加购物车单体测试
        List<ShoppingCartDTO> shoppingCartDTO = new ArrayList<>();
        ShoppingCartDTO shoppingCartDTO1 = new ShoppingCartDTO();
        shoppingCartDTO1.setDishFlavor("无糖");
        shoppingCartDTO1.setDishId(61L);
        ShoppingCartDTO shoppingCartDTO2 = new ShoppingCartDTO();
        shoppingCartDTO2.setDishFlavor("少糖");
        shoppingCartDTO2.setDishId(62L);
        ShoppingCartDTO shoppingCartDTO3 = new ShoppingCartDTO();
        shoppingCartDTO3.setDishFlavor("多糖");
        shoppingCartDTO3.setDishId(63L);
        shoppingCartDTO.add(shoppingCartDTO1);
        shoppingCartDTO.add(shoppingCartDTO2);
        shoppingCartDTO.add(shoppingCartDTO3);
        for (ShoppingCartDTO cartDTO : shoppingCartDTO) {
            shoppingCartService.addShoppingCart(cartDTO);
        }


        //2.计算价格
        BigDecimal total = shoppingCartMapper.sum(4L);

        //3.用户下单
        OrdersSubmitDTO ordersSubmitDTO = new OrdersSubmitDTO();
        ordersSubmitDTO.setAddressBookId(2L);
        ordersSubmitDTO.setAmount(total);
        ordersSubmitDTO.setDeliveryStatus(1);
        LocalDateTime localDateTime = LocalDateTime.now();
        ordersSubmitDTO.setEstimatedDeliveryTime(localDateTime);
        ordersSubmitDTO.setPackAmount(1);
        ordersSubmitDTO.setPayMethod(1);
        ordersSubmitDTO.setRemark("11111");
        ordersSubmitDTO.setTablewareNumber(1);
        ordersSubmitDTO.setTablewareStatus(1);

        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);

        //4.更新状态为待接单
        orderMapper.updatestatus(orderSubmitVO.getId(), 2L);
        orderMapper.updatestatus(orderSubmitVO.getId(), 3L);
        orderMapper.updatestatus(orderSubmitVO.getId(), 4L);
        orderMapper.updatestatus(orderSubmitVO.getId(), 5L);
        orderMapper.updatestatus(orderSubmitVO.getId(), 6L);


        //5.拒单
//        OrdersRejectionDTO ordersRejectionDTO = new OrdersRejectionDTO();
//        orderService.rejection(ordersRejectionDTO);

    }


    @Test
    void testPage() {
        CategoryPageQueryDTO queryDTO = new CategoryPageQueryDTO();
        queryDTO.setPageSize(10);
        queryDTO.setPage(1);

        PageResult pageResult = categoryService.pageQuery(queryDTO);
        // 1️⃣ 不为空
        assertNotNull(pageResult);
        // 2️⃣ total 是否正确
        assertEquals(12, pageResult.getTotal());
        // 3️⃣ records 不为空
        assertNotNull(pageResult.getRecords());
        // 4️⃣ 数量是否一致
        assertEquals(10, pageResult.getRecords().size());
        // 5️⃣ 校验第一条数据
        Category o = (Category) pageResult.getRecords().get(0);

        assertEquals(16L, o.getId());
        assertEquals("蜀味烤鱼", o.getName());
        assertEquals(0, o.getStatus());

    }

    @Test
    void testyuangongguanli() {
        EmployeePageQueryDTO employeePageQueryDTO = new EmployeePageQueryDTO();
        employeePageQueryDTO.setPageSize(10);
        employeePageQueryDTO.setPage(1);

        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        // 1️⃣ 不为空
        assertNotNull(pageResult);
        // 2️⃣ total 是否正确
        assertEquals(4, pageResult.getTotal());
        // 3️⃣ records 不为空
        assertNotNull(pageResult.getRecords());
        // 4️⃣ 数量是否一致
        assertEquals(4, pageResult.getRecords().size());
        // 5️⃣ 校验第一条数据

//        assertThrows(ClassCastException.class, () -> {
            if (pageResult.getRecords().get(0) instanceof Employee) {
                Employee o = (Employee) pageResult.getRecords().get(0);
                assertEquals(8L, o.getId());
                assertEquals("王五", o.getName());
                assertEquals(1, o.getStatus());
            } else {
                System.out.println(6666);
            }



    }
    @Test
    void testyuangonggu() {
        // 下边界
        assertEquals(0, calculateScore(0));

        // 及格临界点
        assertEquals(1, calculateScore(60));

        // 刚好不及格
        assertEquals(0, calculateScore(59));

        // 上边界
        assertEquals(1, calculateScore(100));

        // 非法下边界
        assertThrows(IllegalArgumentException.class,
                () -> calculateScore(-1));

        // 非法上边界
        assertThrows(IllegalArgumentException.class,
                () -> calculateScore(101));
    }
    @Test
    void testRange() {

        // 不及格区间
        assertEquals(0, calculateScore(10));
        assertEquals(0, calculateScore(30));
        assertEquals(0, calculateScore(59));

        // 及格区间
        assertEquals(1, calculateScore(70));
        assertEquals(1, calculateScore(85));
        assertEquals(1, calculateScore(100));
    }



    @Test
    public void test1() throws Exception {
        //取消订单
        OrdersCancelDTO ordersCancelDTO = new OrdersCancelDTO();
        ordersCancelDTO.setCancelReason("8888888");
        ordersCancelDTO.setId(16L);
        orderService.cancel(ordersCancelDTO);
    }

    @Test
    public void test2() throws Exception {
        //派送订单
        orderService.delivery(27L);
    }


    @Test
    void testDivide() {

        assertThrows(ArithmeticException.class, () -> {
            int x = 1 / 0;
        });//指定抛出预期异常

    }

    public <T> List<T> toList(String json, Class<T> clazz) {
        try {
            JavaType type = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, clazz);
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("JSON 转换失败", e);
        }
    }

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("对象转 JSON 失败", e);
        }
    }
    public int calculateScore(int score) {

        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("分数非法");
        }

        if (score >= 60) {
            return 1;   // 及格
        } else {
            return 0;   // 不及格
        }
    }


}
