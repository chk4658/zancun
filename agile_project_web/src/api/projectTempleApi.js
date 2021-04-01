import QS from 'qs';
import { fetch } from '../utils/fetch';


/**
 * 项目列表根据条件查询
 * @returns {Promise<Response>}
 */
export function _listProjectTemplate(query) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/project-template/get-list`,
      method: 'get',
      params: query,
    });
}


/**
 * 项目列表
 * @returns {Promise<Response>}
 */
export function _listAllProjectTemplate(query) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/project-template/all`,
      method: 'get',
      params: query,
    });
}

/**
 * 项目列表
 * @returns {Promise<Response>}
 */
export function _listEnabledProjectTemplate(query) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/project-template/enabled`,
      method: 'get',
      params: query,
    });
}


/**
 * 根据 id 查询
 * @returns {Promise<Response>}
 */
export function _queryProjectTemplate(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template/${id}`,
    method: 'get',
  });
}


/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateProjectTemplate(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 删除
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteProjectTemplate(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template/${id}`,
    method: 'delete',
  });
}


/**
 * 删除
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _enabledProjectTemplate(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template/disable-or-restore`,
    method: 'get',
    params: data,
  });
}


/**
 * 项目列表根据条件查询
 * @returns {Promise<Response>}
 */
export function _templatesByTypeId(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template/templatesByTypeId`,
    method: 'get',
    params: query,
  });
}

/**
 * 项目列表根据条件查询
 * @returns {Promise<Response>}
 */
export function _TreeResultProjectTemplate(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template/tree`,
    method: 'get',
    params: query,
  });
}


/**
 *  对象下所有数据 包含 属性 任务 子任务 属性值等
 * @returns {Promise<Response>}
 */
export function _queryProjectTemplateBean(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template/projectTemplateBean`,
    method: 'get',
    params: query,
  });
}