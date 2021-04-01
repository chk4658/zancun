import QS from 'qs';
import { fetch } from '../utils/fetch';


/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateEquivalen(data) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/equivalent/`,
      method: 'post',
      data: QS.stringify(data),
    });
}



/**
 * 列表分页
 * @returns {Promise<Response>}
 */
export function _listEquivalens(query) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/equivalent/list`,
      method: 'get',
      params: query,
    });
}


/**
 * 列表分页
 * @returns {Promise<Response>}
 */
export function _getEquivalen(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/equivalent/${id}`,
    method: 'get',
  });
}


/**
 * 导入
 * @returns {Promise<Response>}
 */
export function _excelImportEquivalen(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/equivalent/excelImport`,
    method: 'post',
    data: QS.stringify(data),
  });
}