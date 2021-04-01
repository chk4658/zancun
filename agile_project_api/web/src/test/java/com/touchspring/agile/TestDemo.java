//package com.touchspring.agile;
//
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.touchspring.ailge.dao.sys.SysUserDao;
//import com.touchspring.ailge.entity.sys.SysUser;
//import org.junit.jupiter.api.Test;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@SpringBootTest(classes = TestDemo.class)
//@MapperScan("com.touchspring.ailge.dao")
//public class TestDemo {
//
//    @Autowired
//    private SysUserDao sysUserDao;
//
//    @Test
//    public  void test(){
//
//        List<String> userIds = new ArrayList<>();
//        userIds.add("61f85cc44fe5d226e5ae05b7e726547c");
//        userIds.add("3670303e225b880209f60fe86bfe6446");
//        userIds.add("457ae5d33133c9d5ad93d40bcac7d78e");
//        userIds.add("cffcc0f1cc06e3207309bd58a06bdae7");
//
//        List<SysUser> sysUsers = sysUserDao.findByUserIds(new Page(1, 20), userIds);
//        for (SysUser sysUser:sysUsers)
//            System.out.println(sysUser.toString());
//    }
//}
