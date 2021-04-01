import QS from 'qs';
import { fetch } from '../utils/fetch';

/**
 * 保存/更新
 * @param sysDepartment
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateDepartment(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-department/`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 删除
 * @param Id
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteDepartment(Id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-department/${Id}`,
    method: 'delete',
  });
}


/**
 * huoqu
 * @param Id
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _getDepartment(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-department/${id}`,
    method: 'get',
  });
}

export default {};
