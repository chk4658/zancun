import QS from 'qs';
import { fetch } from '../utils/fetch';



/**
 * 日子列表
 * @returns {Promise<Response>}
 */
export function _listCircleLogs(query) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/circle-log/list`,
      method: 'get',
      params: query,
    });
  }