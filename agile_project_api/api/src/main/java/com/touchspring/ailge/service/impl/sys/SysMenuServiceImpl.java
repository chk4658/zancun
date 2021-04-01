package com.touchspring.ailge.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.sys.SysRoleMenuDao;
import com.touchspring.ailge.entity.sys.SysMenu;
import com.touchspring.ailge.dao.sys.SysMenuDao;
import com.touchspring.ailge.entity.sys.SysRole;
import com.touchspring.ailge.entity.sys.SysRoleMenu;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.sys.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.utils.IdWorker;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenu> implements SysMenuService {

    @Autowired
    private SysMenuDao sysMenuDao;
    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;

    /**
     * 查询菜单信息
     */
    @Override
    public List<SysMenu> findAll() {
        List<SysMenu> sysMenuList = new LambdaQueryChainWrapper<SysMenu>(sysMenuDao).isNull(SysMenu::getParentId).orderByAsc(SysMenu::getSort).list();
        for (SysMenu parent: sysMenuList){
            List<SysMenu> children = this.findChildren(parent.getId());
            parent.setChildren(children);
        }
        return sysMenuList;
    }

    /**
     * 删除菜单,同时删除关联子菜单
     */
    @Override
    public boolean delete(String id) {
        //删除角色菜单
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getSysMenuId, id);
        sysRoleMenuDao.delete(queryWrapper);

        //删除菜单
        List<String> menuIds = new ArrayList<>();
        menuIds.add(id);
        this.getChildMenuIds(id, menuIds);
        for (String menuId: menuIds)
            sysMenuDao.deleteById(menuId);
        return true;
    }

    /**
     * 搜索
     */
    @Override
    public List<SysMenu> search(String value) {
        return new LambdaQueryChainWrapper<SysMenu>(sysMenuDao).like(SysMenu::getName, value).or().like(SysMenu::getType, value).or()
                .like(SysMenu::getIcon, value).or().like(SysMenu::getPath, value).list();
    }

    /**
     * 新增 / 更新 菜单信息
     * @param sysMenu
     * @return
     */
    @Override
    public ResultData insert(SysMenu sysMenu) {
        SysMenu target;
        if (StringUtils.isNotBlank(sysMenu.getId())) {
            target = sysMenuDao.selectById(sysMenu.getId());
            if (!StringUtils.equals(target.getPath(), sysMenu.getPath())) {
                List<SysMenu> menuList = new LambdaQueryChainWrapper<SysMenu>(sysMenuDao).eq(SysMenu::getPath, sysMenu.getPath()).list();
                if (!CollectionUtils.isEmpty(menuList)) return new ResultData(ResultStatus.IS_REPEAT.getCode(), ResultStatus.IS_REPEAT.getMessage());
            }
            sysMenuDao.updateById(sysMenu);
        }
        else {
            List<SysMenu> menuList = new LambdaQueryChainWrapper<SysMenu>(sysMenuDao).eq(SysMenu::getPath, sysMenu.getPath()).list();
            if (!CollectionUtils.isEmpty(menuList)) return new ResultData(ResultStatus.IS_REPEAT.getCode(), ResultStatus.IS_REPEAT.getMessage());
            target = sysMenu;
            target.setSort(IdWorker.nextId());
//            if (StringUtils.isBlank(target.getParentId())) target.setParentId("0");
            sysMenuDao.insert(target);
        }
        return ResultData.ok();
    }

    /**
     * 菜单上移
     * @param id 菜单ID
     * @return .
     */
    @Override
    public ResultData upMenu(String id) {
        SysMenu sysMenu = sysMenuDao.selectById(id);
        if (sysMenu == null || StringUtils.isBlank(sysMenu.getParentId())) return ResultData.notFound();
        //获取当前parentId的所有子菜单
        List<SysMenu> menus = new LambdaQueryChainWrapper<SysMenu>(sysMenuDao).eq(SysMenu::getParentId, sysMenu.getParentId()).list();
        menus.sort((a, b) -> -a.getSort().compareTo(b.getSort()));
        Optional<SysMenu> lessOptional = menus.stream().filter(x -> x.getSort().compareTo(sysMenu.getSort()) < 0).findFirst();
        if (!lessOptional.isPresent()) {
            ResultData resultData = ResultData.errorRequest();
            resultData.setMessage("菜单无法上移");
            return resultData;
        }
        //获取比当前小的第一个菜单
        SysMenu less = lessOptional.get();
        this.swapMenuSort(sysMenu, less);
        return  ResultData.ok();
    }

    /**
     * 菜单下移
     * @param id 菜单ID
     * @return .
     */
    @Override
    public ResultData downMenu(String id) {
        SysMenu sysMenu = sysMenuDao.selectById(id);
        if (sysMenu == null || StringUtils.isBlank(sysMenu.getParentId())) return ResultData.notFound();
        //获取当前parentId的所有子菜单
        List<SysMenu> menus = new LambdaQueryChainWrapper<SysMenu>(sysMenuDao).eq(SysMenu::getParentId, sysMenu.getParentId()).list();
        menus.sort(Comparator.comparing(SysMenu::getSort));
        Optional<SysMenu> greaterOptional = menus.stream().filter(x -> x.getSort().compareTo(sysMenu.getSort()) > 0).findFirst();
        if (!greaterOptional.isPresent()) {
            ResultData resultData = ResultData.errorRequest();
            resultData.setMessage("菜单无法下移");
            return resultData;
        }
        SysMenu greater = greaterOptional.get();
        this.swapMenuSort(sysMenu, greater);
        return ResultData.ok();
    }

    /**
     * 根据用户角色查找可用菜单树结构
     * @param sysRoles  角色
     * @return .
     */
    @Override
    public List<SysMenu> findTreeMenuByRole(List<SysRole> sysRoles) {

        List<SysMenu> sysMenuList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sysRoles)) {
            sysRoles.forEach(sysRole -> sysMenuList.addAll(sysRole.getSysMenuList()));
        }
        //去重
        List<SysMenu> sysMenus = new ArrayList<>(new HashSet<>(sysMenuList));
        //排序
        Collections.sort(sysMenus, new Comparator<SysMenu>() {
            @Override
            public int compare(SysMenu o1, SysMenu o2) {
                int diff = o1.getSort().compareTo(o2.getSort());
//                int diff = Integer.valueOf(o1.getSort()) - Integer.valueOf(o2.getSort());
                if (diff > 0) return 1;
                else if (diff < 0) return -1;
                return 0;
            }
        });
        List<SysMenu> menuTreeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sysMenus)) {
            for (SysMenu sysMenu : sysMenus){
                if (StringUtils.isBlank(sysMenu.getParentId())) {
                    menuTreeList.add(sysMenu);
                }
                for (SysMenu menu : sysMenus){
                    if (menu.getParentId().equals(sysMenu.getId())) {
                        if (sysMenu.getChildren() == null) {
                            List<SysMenu> myChildrenList = new ArrayList<>();
                            myChildrenList.add(menu);
                            sysMenu.setChildren(myChildrenList);
                        } else
                            sysMenu.getChildren().add(menu);
                    }
                }
            }
        }
        return menuTreeList;
    }

    /**
     * 交换顺序
     * @param a 当前的菜单
     * @param b 要交换的菜单
     */
    private void swapMenuSort(SysMenu a, SysMenu b){
        String tempSort = b.getSort();
        b.setSort(a.getSort());
        a.setSort(tempSort);
        sysMenuDao.updateById(b);
        sysMenuDao.updateById(a);
    }

    /**
     * 获取子菜单
     */
    private List<SysMenu> findChildren(String parentId) {
        List<SysMenu> menus = new LambdaQueryChainWrapper<SysMenu>(sysMenuDao).eq(SysMenu::getParentId, parentId).orderByAsc(SysMenu::getSort).list();
        for (SysMenu menu : menus) {
            List<SysMenu> children = this.findChildren(menu.getId());
            menu.setChildren(children);
        }
        return menus;
    }

    /**
     * 获取关联子菜单
     */
    private void getChildMenuIds(String id,List<String> menuIds){
        List<SysMenu> menus = new LambdaQueryChainWrapper<SysMenu>(sysMenuDao).eq(SysMenu::getParentId, id).list();
        for (SysMenu menu: menus) {
            menuIds.add(menu.getId());
            List<SysMenu> menus1 = new LambdaQueryChainWrapper<SysMenu>(sysMenuDao).eq(SysMenu::getParentId, menu.getId()).list();
            if(CollectionUtils.isEmpty(menus1)) continue;
            this.getChildMenuIds(menu.getId(),menuIds);
        }
    }
}
