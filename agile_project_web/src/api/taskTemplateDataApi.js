import QS from 'qs';
import { fetch } from '../utils/fetch';


/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateTaskTemplateData(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task-template-data/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 批量 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateTaskTemplateDatas(datas) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task-template-data/saveOrUpdateList`,
    method: 'post',
    data: QS.stringify(datas),
  });
}

