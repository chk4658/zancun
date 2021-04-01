package com.touchspring.ailge.controller.sys;


import com.touchspring.ailge.dao.sys.SysMenuDao;
import com.touchspring.ailge.entity.sys.SysMenu;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.sys.SysMenuService;
import com.touchspring.core.mvc.ui.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@RestController
@RequestMapping("/sys-menu")
public class SysMenuController {
    private final SysMenuDao sysMenuDao;
    private final SysMenuService sysMenuService;

    @Autowired
    public SysMenuController(SysMenuDao sysMenuDao, SysMenuService sysMenuService) {
        this.sysMenuDao = sysMenuDao;
        this.sysMenuService = sysMenuService;
    }

    /**
     * 查询菜单及子菜单信息 默认正序排列
     */
    @GetMapping("")
    public ResultData findAllMenus(@RequestParam(defaultValue = "1", required = false) Integer page,
                                   @RequestParam(defaultValue = "20", required = false) Integer size){
        List<SysMenu> sysMenuList = sysMenuService.findAll();
        return ResultData.ok().putDataValue("sysMenuList", sysMenuList);
    }


    /**
     * 保存/更新 parentId为空，则为顶级菜单
     */
    @PostMapping("")
    public ResultData saveOrUpdate(SysMenu sysMenu){
        return sysMenuService.insert(sysMenu);
    }

    /**
     * 删除菜单,同时删除关联子菜单
     */
    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable("id") String id){
        boolean delete = sysMenuService.delete(id);
        if (delete) return ResultData.ok();
        return new ResultData(ResultStatus.DELETE_ERROR.getCode(), ResultStatus.DELETE_ERROR.getMessage());
    }

    /**
     * 获取
     */
    @GetMapping("{id}")
    public ResultData get(@PathVariable("id") String id){
        return ResultData.ok().putDataValue("SysMenu", sysMenuDao.selectById(id));
    }

    /**
     * 搜索
     */
    @PostMapping("search")
    public ResultData search(String value){
        List<SysMenu> search = sysMenuService.search(value);
//        if (CollectionUtils.isEmpty(search)) return new ResultData(ResultStatus.NOT_EXIST.getCode(), ResultStatus.NOT_EXIST.getMessage());
        return ResultData.ok().putDataValue("search", search);
    }

    /**
     * 菜单上移
     */
    @PatchMapping("menus/{id}/up")
    public ResultData upMenu(@PathVariable("id") String id){
        return sysMenuService.upMenu(id);
    }


    /**
     * 菜单下移
     */
    @PatchMapping("menus/{id}/down")
    public ResultData downMenu(@PathVariable("id") String id){
        return sysMenuService.downMenu(id);
    }

    /**
     * 查询 类别下相关菜单
     */
    @GetMapping("get-groups")
    public ResultData findAllMenusKeyByType(){
        List<SysMenu> sysMenuList = sysMenuService.findAll();
        Map<String, SysMenu> sysMenuMap = sysMenuList.stream().collect(Collectors.toMap(SysMenu::getGroups, a -> a, (k1, k2) -> k1));
//        Map<String, List<SysMenu>> sysMenuMap = sysMenuList.stream().collect(Collectors.groupingBy(SysMenu::getGroups));
        return ResultData.ok().putDataValue("sysMenuMap", sysMenuMap);
    }

}
