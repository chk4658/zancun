import QS from 'qs';
import { fetch } from '../utils/fetch';

/**
 * 列表
 * sysRoleList
 * @returns {Promise<Response>}
 */
export function _listRole() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role/all`,
    method: 'get',
  });
}


/**
 * 根据roleId查询成员信息 分页
 * users, totalAmount
 * @returns {Promise<Response>}
 */
export function _findUserByRoleId(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-user-role/roleId`,
    method: 'get',
    params: data,
  });
}

/**
 * 根据roleId查询相应的菜单信息
 * @param roleId 角色ID
 * sysMenus
 * @return {Promise<Response>}
 */
export function _findMenuByRoleId(roleId) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role-menu/roleMenu/${roleId}`,
    method: 'get',
  });
}


/**
 * 保存/更新  角色  id判定
 * @param sysRole
 * sysMenus
 * @return {Promise<Response>}
 */
export function _saveOrUpdateRole(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 根据角色id删除角色 及关联角色表
 * @param id 角色ID
 * @return {Promise<Response>}
 */
export function _deleteRole(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role/${id}`,
    method: 'delete',
  });
}


/**
 * 绑定菜单角色 关联表信息
 * @param roleId 角色ID
 * @param menuIds 菜单ID
 * @return .
 */
export function _bindMenu(roleId, data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role-menu/roleMenu/${roleId}`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 搜索
 * @param roleName 角色名称
 * @return .
 */
export function _searchByRoleName(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role/search`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 角色下 增加用户信息
 * @param roleId 角色ID
 * @param userIds 人员ID
 * @return .
 */
export function _bindUser(roleId, data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-user-role/userRole/${roleId}`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 根据userIds,roleId删除关联表
 * @param userIds
 * @param roleId
 * @return {Promise<Response>}
 */
export function _deleteByRoleIdAndUserId(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-user-role/userRole/unbind`,
    method: 'delete',
    params: data
  });
}

export default {};
