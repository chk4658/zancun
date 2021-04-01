import QS from 'qs';
import { fetch } from '../utils/fetch';


/**
 * 人员列表(未使用，默认不用全部显示)
 * @returns {Promise<Response>}
 */
export function _queryUsers() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-user/all`,
    method: 'get',
  });
}

/**
 * 根据账号或姓名 是否禁用 分页查找
 * @returns {Promise<Response>}
 */
export function _findByCompanyAndDepartment(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-user/list`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 批量删除
 * @param userIds
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _batchDeleteUser(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-user/batch-delete`,
    method: 'delete',
    params: data,
  });
}

/**
 * 批量禁用
 * @param userIds
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _batchEnabledUser(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-user/batch-enabled`,
    method: 'delete',
    params: data,
  });
}

/**
 * 批量可用
 * @param userIds
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _batchAvailableUser(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-user/batch-available`,
    method: 'delete',
    params: data,
  });
}

/**
 * 更新用户尼玛
 * @param newPassword
 * @param oldPassword
 * @returns {Promise<null|*>}
 * @private
 */
export function _updateUserPassword(
  {
    newPassword,
    oldPassword,
  },
) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/password`,
    method: 'patch',
    data: {
      oldPassword,
      newPassword,
    },
  });
}

/**
 * 根据 id 查询
 * @returns {Promise<Response>}
 */
export function _querySysUser(sysUserId) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-user/${sysUserId}`,
    method: 'get',
  });
}

/**
 * 根据 Circle 查询
 * @returns {Promise<Response>}
 */
export function _querySysUsersByCircle(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle-user/user`,
    method: 'get',
    params:query
  });
}

/**
 * 根据 IDS 查询
 * @returns {Promise<Response>}
 */
export function _querySysUsersByIds(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-user/ids`,
    method: 'get',
    params:query
  });
}


/**
 * 刷新
 * @returns {Promise<Response>}
 */
 export function _pullSysUserData() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-user/pull`,
    method: 'post',
  });
}


/**
 * 刷新
 * @returns {Promise<Response>}
 */
 export function _saveSysUser(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-user/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


export default {};
