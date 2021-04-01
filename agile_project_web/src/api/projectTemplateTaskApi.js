import QS from 'qs';
import { fetch } from '../utils/fetch';

/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateProjectTemplateTask(data) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/project-template-task/`,
      method: 'post',
      data: QS.stringify(data),
    });
}



/**
 * 删除
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteProjectTemplateTask(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-task/${id}`,
    method: 'delete',
  });
}


/**
 * 上移
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _upProjectTemplateTask(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-task/up`,
    method: 'get',
    params: {id,id}
  });
}


/**
 * 下移
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _downProjectTemplateTask(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-task/down`,
    method: 'get',
    params: {id,id}
  });
}


/**
 * 下移
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _batchDeleteProjectTemplateTask(ids) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-task/batchDelete`,
    method: 'delete',
    params: {ids,ids}
  });
}
