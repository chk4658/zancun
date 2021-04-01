import QS from 'qs';
import {fetch} from '../utils/fetch';


/**
 * 圈子统计 每月新增圈子数
 * @returns {Promise<Response>}
 */
export function _getNewProjectNumPerMonth() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-chart/newProjectNumPerMonth`,
    method: 'get',
    params: '',
  });
}

/**
 * 项目任务数Top10
 * @returns {Promise<Response>}
 */
export function _getTopTenTaskNumInProject() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-chart/topTenTaskNumInProject`,
    method: 'get',
    params: '',
  });
}

/**
 * 项目参与人数Top10
 * @returns {Promise<Response>}
 */
export function _getTopTenUserNumInProject() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-chart/topTenUserNumInProject`,
    method: 'get',
    params: '',
  });
}

/**
 * 按状态统计项目
 * @returns {Promise<Response>}
 */
export function _getProjectNumByStatus() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-chart/projectNumByStatus`,
    method: 'get',
    params: '',
  });
}



/**
 * 多项目比较
 * @returns {Promise<Response>}
 */
export function _getNewTaskNumPerDayInProjectIds(data) {
  console.log(data)
  const stringify = QS.stringify(data, {arrayFormat: 'repeat'});
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-chart/newTaskNumPerDayInProjectIds`,
    method: 'post',
    data: stringify
  });
}


export default {};
