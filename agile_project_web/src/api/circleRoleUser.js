import QS from 'qs';
import { fetch } from '../utils/fetch';



/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateCircleRoleUser(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle-role-user/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _queryCircleUserNum(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle-role-user/num`,
    method: 'get',
    params: query,
  });
}