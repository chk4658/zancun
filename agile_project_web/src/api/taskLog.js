import QS from 'qs';
import {fetch} from '../utils/fetch';


/**
 * 日志列表  任务
 * @returns {Promise<Response>}
 */
export function _listTaskLogs(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task-log/list`,
    method: 'get',
    params: query,
  });
}


/**
 * 日志列表  里程碑
 * @returns {Promise<Response>}
 */
export function _listMilestonesLogs(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone-log/list`,
    method: 'get',
    params: query,
  });
}
