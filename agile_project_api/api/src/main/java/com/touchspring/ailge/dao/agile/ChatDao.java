package com.touchspring.ailge.dao.agile;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchspring.ailge.entity.agile.Chat;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ChatDao extends BaseMapper<Chat> {

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "id", property = "children",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.ChatDao.findListByParentId")),
            @Result(column = "CREATE_USER_ID", property = "createUser",
                    many = @Many(select = "com.touchspring.ailge.dao.sys.SysUserDao.selectById")),
    })

    @Select("select * from CHAT where is_delete = 0 " +
            "and FOREIGN_ID = #{foreignId} AND TYPE=#{type} " +
            "and (PARENT_ID IS NULL OR PARENT_ID = '') ORDER BY CREATE_AT DESC")
    List<Chat> findList(Chat chat);




    @Results({
            @Result(column = "CREATE_USER_ID", property = "createUserId"),
            @Result(column = "CREATE_USER_ID", property = "createUser",
                    many = @Many(select = "com.touchspring.ailge.dao.sys.SysUserDao.selectById")),

            @Result(column = "REPLY_ID", property = "replyId"),
            @Result(column = "REPLY_ID", property = "reply",
                    many = @Many(select = "com.touchspring.ailge.dao.agile.ChatDao.findById"))
    })
    @Select("select * from CHAT where is_delete = 0 and PARENT_ID = #{parentId} ORDER BY CREATE_AT")
    List<Chat> findListByParentId(String parentId);


    @Results({
            @Result(column = "CREATE_USER_ID", property = "createUserId"),
            @Result(column = "CREATE_USER_ID", property = "createUser",
                    many = @Many(select = "com.touchspring.ailge.dao.sys.SysUserDao.selectById")),
    })
    @Select("select * from CHAT where is_delete = 0 and id = #{id}")
    Chat findById(String id) ;


}
