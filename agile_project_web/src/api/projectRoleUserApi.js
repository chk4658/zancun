import QS from 'qs';
import { fetch } from '../utils/fetch';

/**
 * 添加/修改
 * projectRole
 * @returns {Promise<Response>}
 */
export function _updateProjectRole(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-role/`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 查看
 * projectId
 * projectRoleList, totalAmount
 * @returns {Promise<Response>}
 */
export function _queryProjectRole(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-role/`,
    method: 'get',
    params: data,
  });
}

/**
 * 添加/修改  项目角色下绑定的人员
 * projectRoleUser
 * @returns {Promise<Response>}
 */
export function _saveOrUpdateRoleUser(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-role-user/`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 查看  根据项目角色id
 * projectRoleId
 * sysUserList
 * @returns {Promise<Response>}
 */
export function _listProjectRoleUser(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-role-user/`,
    method: 'get',
    params: data,
  });
}

export default {};
