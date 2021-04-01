import QS from 'qs';
import { fetch } from '../utils/fetch';



/**
 * 项目模板列表()
 * @returns {Promise<Response>}
 */
export function _queryProjectTemplateTypes(query) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/project-template-type/all`,
      method: 'get',
      params: query
    });
}


/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateProjectTemplateType(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-type/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 删除
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteProjectTemplateType(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-type/${id}`,
    method: 'delete',
  });
}


/**
 * 删除
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _queryProjectTemplateType(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-type/${id}`,
    method: 'get',
  });
}
