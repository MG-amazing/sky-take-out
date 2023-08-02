package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select *from user where openid=#{openid}")
    User getByOpenid(String openid);

    void insert(User user);
    @Select("select *from user where id=#{id}")
    User getById(Long userId);


    Integer countByMap(Map map);
}
