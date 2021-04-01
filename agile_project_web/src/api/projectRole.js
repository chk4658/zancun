import QS from 'qs';
import { fetch } from '../utils/fetch';


/**
 * 角色列表
 * @returns {Promise<Response>}
 */
export function _listProjectRoles(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-role/`,
    method: 'get',
    params: data,
  });
}


/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateProjectRole(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-role/`,
    method: 'post',
    data: QS.stringify(data),
  });
}



  /**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _queryProjectRole(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-role/${id}`,
    method: 'get',
  });
}



  /**
 * 删除
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteProjectRole(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-role/${id}`,
    method: 'delete',
  });
}