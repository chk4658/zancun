import QS from 'qs';
import {fetch} from '../utils/fetch';


/**
 * 日志列表
 * @returns {Promise<Response>}
 */
export function _listProjectLogs(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-log/list`,
    method: 'get',
    params: query,
  });
}
