import QS from 'qs';
import {fetch} from '../utils/fetch';


/**
 * 圈子统计 每月新增圈子数
 * @returns {Promise<Response>}
 */
export function _getNewCircleNumPerMonth() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle-chart/newCircleNumPerMonth`,
    method: 'get',
    params: '',
  });
}

/**
 * 圈子任务数Top10
 * @returns {Promise<Response>}
 */
export function _getTopTenTaskNumInCircle() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle-chart/topTenTaskNumInCircle`,
    method: 'get',
    params: '',
  });
}

/**
 * 圈子参与人数Top10
 * @returns {Promise<Response>}
 */
export function _getTopTenUserNumInCircle() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle-chart/topTenUserNumInCircle`,
    method: 'get',
    params: '',
  });
}


/**
 * 多圈子比较
 * @returns {Promise<Response>}
 */
export function _getNewTaskNumPerDayInCircleIds(data) {
  console.log(data)
  const stringify = QS.stringify(data, {arrayFormat: 'repeat'});
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle-chart/newTaskNumPerDayInCircleIds`,
    method: 'post',
    data: stringify
  });
}


export default {};
