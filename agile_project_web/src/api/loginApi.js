import { fetch } from '../utils/fetch';

/**
 * 查询列表
 * @returns {Promise<Response>}
 */
export function _login(
  {
    account, password,
  },
) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/login`,
    method: 'post',
    data: {
      account,
      password,
    },
  });
}


/**
 * 查询列表
 * @returns {Promise<Response>}
 */
export function _autoLogin(authUser) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/autoLogin`,
    method: 'post',
    data: authUser
  });
}


/**
 * 查询列表
 * @returns {Promise<Response>}
 */
export function _loginByUserId(
  {
    userId,
  },
) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/auto-login`,
    method: 'post',
    data: {
      userId,
    },
  });
}


/**
 * 根据 id 查询
 * @returns {Promise<Response>}
 */
export function _queryFlow(flowId) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/flows/${flowId}`,
    method: 'get',
  });
}

/**
 * 新增
 * @param  id
 * @param  name
 * @param  description
 * @param  isActive
 * @param  deleteStatus
 * @param  createUserId
 * @param  updateUserId
 * @param  createAt
 * @param  updateAt
 * @param  type
 * @param  equipmentTypeId
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _addFlow(
  {
    id,
    name,
    description,
    isActive,
    deleteStatus,
    createUserId,
    updateUserId,
    createAt,
    updateAt,
    type,
    equipmentTypeId,
  },
) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/flows`,
    method: 'post',
    data: {
      id,
      name,
      description,
      isActive,
      deleteStatus,
      createUserId,
      updateUserId,
      createAt,
      updateAt,
      type,
      equipmentTypeId,
    },
  });
}

/**
 * 更新
 * @param flowId
 * @param  id
 * @param  name
 * @param  description
 * @param  isActive
 * @param  deleteStatus
 * @param  createUserId
 * @param  updateUserId
 * @param  createAt
 * @param  updateAt
 * @param  type
 * @param  equipmentTypeId
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateFlow(flowId, {
  id,
  name,
  description,
  isActive,
  deleteStatus,
  createUserId,
  updateUserId,
  createAt,
  updateAt,
  type,
  equipmentTypeId,
}) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/flows/${flowId}`,
    method: 'patch',
    data: {
      id,
      name,
      description,
      isActive,
      deleteStatus,
      createUserId,
      updateUserId,
      createAt,
      updateAt,
      type,
      equipmentTypeId,
    },
  });
}

/**
 * 删除
 * @param flowId
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteFlow(flowId) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/flows/${flowId}`,
    method: 'delete',
  });
}

export default {};
