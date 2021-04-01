import QS from 'qs';
import {fetch} from '../utils/fetch';


/**
 * 查找归档任务
 * @returns {Promise<Response>}
 */
export function _findStMarkTaskByProjectId(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/findStMarkTaskByProjectId`,
    method: 'get',
    params: query,
  });
}


/**
 * 设为归档任务
 * taskId
 * @returns {Promise<Response>}
 */
export function _letTaskToStMark(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/letTaskToStMark`,
    method: 'get',
    params: query,
  });
}
