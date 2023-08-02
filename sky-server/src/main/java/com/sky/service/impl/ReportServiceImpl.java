package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    /**
     * 统计指定时间区间内的数据
     * @param begin
     * @param end
     * @return
     */
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放begin到end范围内每天的日期
        List<LocalDate>dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            //日期计算，可能死循环
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> turnOverList=new ArrayList<>();

        for (LocalDate date : dateList) {
            //查询日期对应的数据,营业额：状态为已完成的订单合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map=new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnOver=orderMapper.sumByMap(map);
            turnOver = turnOver == null ? 0.0 : turnOver;
            turnOverList.add(turnOver);
        }



        return TurnoverReportVO.builder().
                dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnOverList,","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放begin到end范围内每天的日期
        List<LocalDate>dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            //日期计算，可能死循环
            begin=begin.plusDays(1);
            dateList.add(begin);
        }


        //存放新增用户
        List<Integer>newUserList=new ArrayList<>();
        //存放总用户
        List<Integer>totalUserList=new ArrayList<>();
        for (LocalDate date : dateList) {

            //查询日期对应的数据
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map=new HashMap();
            map.put("end",endTime);
            Integer totalUser = userMapper.countByMap(map);
            map.put("begin",beginTime);
            Integer newUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }




        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 统计区间内的订单数据
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放begin到end范围内每天的日期
        List<LocalDate>dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            //日期计算，可能死循环
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        //每天订单数
        List<Integer>orderCountList=new ArrayList<>();
        //每天有效订单数
        List<Integer>validOrderCountList=new ArrayList<>();

        //遍历datalist集合查询有效订单数和总数
        for (LocalDate date : dateList) {

            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //订单每天总数select count(id)from orders where order_time>?and order_time<?
            Integer orderCount = getOrderCount(beginTime, endTime, null);

            //每天的有效订单数select count(id)from orders where order_time>?and order_time<? and status=5
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        //计算时间区间内的订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //计算时间区间内的有效订单数量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //计算订单完成率
        Double orderCompletionRate = 0.0;

        if (totalOrderCount!=0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();

    }
    private Integer getOrderCount(LocalDateTime begin,LocalDateTime end ,Integer status){
        Map map=new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);
        return orderMapper.countByMap(map);
    }

    /**
     * 统计区间内销量前十
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        //统计完成状态的订单总数select od.name,sum(od.number) from order_detail od,orders o where od.order.id=o.id and o.status=5
        //and o.time<? and order_time<?  group by od.name
        //order by number desc
        //limit 0,10
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);

        List<String> name = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(name, ",");
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        return SalesTop10ReportVO.builder()
                .numberList(numberList)
                .nameList(nameList)
                .build();


    }


}
