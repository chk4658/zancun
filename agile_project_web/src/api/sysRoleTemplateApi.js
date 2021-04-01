import QS from 'qs';
import { fetch } from '../utils/fetch';

/**
 * 角色列表
 * @returns {Promise<Response>}
 */
export function _listRoleTemplate(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role-template/`,
    method: 'get',
    params: data,
  });
}

/**
 * 获取
 * @returns {Promise<Response>}
 */
export function _getRoleTemplateById(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role-template/${id}`,
    method: 'get',
  });
}

/**
 * 搜索
 * @param value
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _searchRoleTemplateByName(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role-template/search`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 保存/更新
 * @param sysRoleTemplate
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _saveOrUpdateRoleTemplate(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role-template/`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 删除
 * @param id
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteRoleTemplate(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role-template/${id}`,
    method: 'delete',
  });
}

/**
 * 批量删除
 * @param tempRoleIds
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _batchDeleteRoleTemplate(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role-template/batch-delete`,
    method: 'delete',
    params: data,
  });
}

/**
 * 批量禁用
 * @param roleIds
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _batchEnabledRoleTemplate(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role-template/batch-enabled`,
    method: 'delete',
    params: data,
  });
}

/**
 * 批量可用
 * @param roleIds
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _batchAvailableRoleTemplate(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role-template/batch-available`,
    method: 'delete',
    params: data,
  });
}


/**
 * 根据 Circle 查询
 * @returns {Promise<Response>}
 */
export function _queryRoleTemplatesByCircle(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle-role/list`,
    method: 'get',
    params: query,
  });
}

/**
 * 根据 Circle 查询
 * @returns {Promise<Response>}
 */
export function _queryRoleTemplatesByRoleNames(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role-template/roleNames`,
    method: 'get',
    params: query,
  });
}

/**
 * 角色列表
 * @returns {Promise<Response>}
 */
export function _queryRoleTemplates() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-role-template/all`,
    method: 'get',
  });
}


export default {};
