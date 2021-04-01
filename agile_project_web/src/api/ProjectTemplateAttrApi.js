import QS from 'qs';
import { fetch } from '../utils/fetch';


/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateProjectTemplateAttr(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-attr/`,
    method: 'post',
    data: QS.stringify(data),
  });
}



/**
 * 删除
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteProjectTemplateAttr(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-template-attr/${id}`,
    method: 'delete',
  });
}

