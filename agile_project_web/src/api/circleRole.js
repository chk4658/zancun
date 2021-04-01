import QS from 'qs';
import { fetch } from '../utils/fetch';


/**
 * 角色列表
 * @returns {Promise<Response>}
 */
export function _listCircleRoles(data) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/circle-role/`,
      method: 'get',
      params: data,
    });
  }
  


  /**
 * 删除
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteCircleRole(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle-role/${id}`,
    method: 'delete',
  });
}


  /**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _queryCircleRole(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle-role/${id}`,
    method: 'get',
  });
}


/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateCircleRole(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle-role/`,
    method: 'post',
    data: QS.stringify(data),
  });
}
