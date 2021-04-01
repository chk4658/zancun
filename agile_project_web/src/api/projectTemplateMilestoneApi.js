import QS from 'qs';
import { fetch } from '../utils/fetch';


/**
 * 项目列表
 * @returns {Promise<Response>}
 */
export function _listProjectTemplateMilestone(query) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/project-template-milestone`,
      method: 'get',
      params: query,
    });
}


/**
 * @returns {Promise<Response>}
 */
export function _getProjectTemplateMilestoneById(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-milestone/assemble/${id}`,
    method: 'get',
  });
}


/**
 * @returns {Promise<Response>}
 */
export function _getProjectTemplateAttrsByProjectTemplateId(projectTemplateId) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-milestone/assemble/attrs/${projectTemplateId}`,
    method: 'get',
  });
}



/**
 * 根据 id 查询
 * @returns {Promise<Response>}
 */
export function _queryProjectTemplateMilestone(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-milestone/${id}`,
    method: 'get',
  });
}



/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateProjectTemplateMilestone(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-milestone/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 删除
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteProjectTemplateMilestone(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-milestone/${id}`,
    method: 'delete',
  });
}


/**
 * 上移
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _upProjectTemplateMilestone(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-milestone/up`,
    method: 'get',
    params: {id,id}
  });
}


/**
 * 下移
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _downProjectTemplateMilestone(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-milestone/down`,
    method: 'get',
    params: {id,id}
  });
}