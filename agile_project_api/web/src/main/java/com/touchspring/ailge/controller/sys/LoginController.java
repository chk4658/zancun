package com.touchspring.ailge.controller.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.touchspring.ailge.config.LoginConfig;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.dao.sys.SysUserRoleDao;
import com.touchspring.ailge.dto.login.UserAuthDTO;
import com.touchspring.ailge.entity.ldap.User;
import com.touchspring.ailge.entity.sys.SysMenu;
import com.touchspring.ailge.entity.sys.SysRole;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.CircleOperationType;
import com.touchspring.ailge.enums.FixedField;
import com.touchspring.ailge.service.agile.CircleService;
import com.touchspring.ailge.service.ldap.UserService;
import com.touchspring.ailge.service.sys.SysUserRoleService;
import com.touchspring.ailge.utils.JWTUtils;
import com.touchspring.ailge.utils.MD5Util;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Data
class LoginRequest {
    private String account;
    private String password;
    private String userId;
    private String authUser;
}


@RestController
@Slf4j
public class LoginController {

    private final SysUserDao sysUserDao;


    private final SysUserRoleService sysUserRoleService;

    private final CircleService circleService;

    private final UserService userService;


    private final LoginConfig loginConfig;

    private final SysUserRoleDao sysUserRoleDao;


    @Autowired
    public LoginController(SysUserDao sysUserDao,
                           SysUserRoleService sysUserRoleService,
                           CircleService circleService,
                           UserService userService, LoginConfig loginConfig, SysUserRoleDao sysUserRoleDao) {
        this.sysUserDao = sysUserDao;
        this.userService = userService;
        this.sysUserRoleService = sysUserRoleService;
        this.circleService = circleService;
        this.loginConfig = loginConfig;
        this.sysUserRoleDao = sysUserRoleDao;
    }

    /**
     * 用户登录接口
     *
     * @param loginRequest .
     * @return .
     */
    @PostMapping("login")
    public ResultData login(@RequestBody LoginRequest loginRequest) {
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper.eq(SysUser::getAccount, loginRequest.getAccount());

        SysUser sysUser = sysUserDao.selectOne(sysUserLambdaQueryWrapper);


        ResultData resultData = ResultData.errorRequest();
        resultData.setMessage("用户名或密码错误，请重新输入！");

        if (sysUser == null) {
            return resultData;
        }

        if (loginConfig.getType().equals(LoginConfig.SEARCH_SQL)){
            String encodePassword = MD5Util.MD5Encode(loginRequest.getPassword(), null);
            if (!StringUtils.equals(sysUser.getPassword(), encodePassword)) {
                return resultData;
            }
        } else if (loginConfig.getType().equals(LoginConfig.SEARCH_LDAP)
                && !userService.verifyLDAP(loginRequest.getAccount(),loginRequest.getPassword())){
            return resultData;
        }

        String token = JWTUtils.sign(new UserAuthDTO(sysUser), 24 * 1000 * 60 * 60L);
//        List<SysRole> roles = sysUserRoleRepository.findByUserId(sysUser.getId()).stream().map(SysUserRole::getRole).collect(Collectors.toList());
//        Set<SysMenu> menus = sysRoleMenuRepository.findByRoleIdIn(roles.stream().map(BaseIdEntity::getId).collect(Collectors.toList())).stream().map(SysRoleMenu::getMenu).collect(Collectors.toSet());
        List<SysRole> roles = sysUserRoleService.findRoleByUserId(sysUser.getId());
        Set<SysMenu> menus = sysUserRoleService.findMenuUnionByRole(roles);
//        List<SysMenu> treeMenuByRole = sysMenuService.findTreeMenuByRole(roles);
        CircleOperationType.UserOperation circleOperation = circleService.getCircleRoleOperation(sysUser.getId());
        return ResultData.ok().putDataValue("user", sysUser)
                .putDataValue("token", token)
                .putDataValue("roles", roles)
                .putDataValue("menus", menus)
                .putDataValue("circleOperation",circleOperation)
                .putDataValue("isAdmin",sysUserRoleDao.findByUserIdAndRoleNameLimit1(sysUser.getId(),FixedField.ADMIN_ROLE) != null);
    }


    /**
     * 自动登录接口
     *
     * @return .
     */
    @PostMapping("autoLogin")
    public ResultData autoLogin(@RequestBody LoginRequest loginRequest) {

        ResultData resultData = ResultData.errorRequest();
        resultData.setMessage("自动登录验证不通过，请用账号密码登录!");

        if (StringUtils.isBlank(loginRequest.getAuthUser())) return resultData;

        log.info("获取客户端，主机名:{},-------------->自动登录",loginRequest.getAuthUser());

        User user = userService.findOneBySAMAccountName(loginRequest.getAuthUser());

        log.info("获取域账号{} ------------------->",user);
        if (user == null) return resultData;

        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper.eq(SysUser::getAccount, user.getAccount());

        SysUser sysUser = sysUserDao.selectOne(sysUserLambdaQueryWrapper);

        if (sysUser == null) return resultData;

        String token = JWTUtils.sign(new UserAuthDTO(sysUser), 24 * 1000 * 60 * 60L);
        List<SysRole> roles = sysUserRoleService.findRoleByUserId(sysUser.getId());
        Set<SysMenu> menus = sysUserRoleService.findMenuUnionByRole(roles);
        CircleOperationType.UserOperation circleOperation = circleService.getCircleRoleOperation(sysUser.getId());
        return ResultData.ok().putDataValue("user", sysUser)
                .putDataValue("token", token)
                .putDataValue("roles", roles)
                .putDataValue("menus", menus)
                .putDataValue("circleOperation",circleOperation)
                .putDataValue("isAdmin",sysUserRoleDao.findByUserIdAndRoleNameLimit1(sysUser.getId(),FixedField.ADMIN_ROLE) != null);
    }
}
