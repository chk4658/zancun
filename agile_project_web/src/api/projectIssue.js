import QS from 'qs';
import {fetch} from '../utils/fetch';

/**
 * 获取所有阶段
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _getProjectIssueStage() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-issue/getProjectIssueStage`,
    method: 'get',
  });
}


/**
 * 获取所有清单 和自己相关的
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _findAllProjectIssue(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-issue/`,
    method: 'get',
    params: data
  });
}

/**
 * 新增或修改
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _saveProjectIssue(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-issue/`,
    method: 'post',
    data: QS.stringify(data)
  });
}


/**
 * 获取根据项目id和time 获取里time 最近的时间 若为空则全部返回
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _getByProjectIdAndTime(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/getByProjectIdAndTime`,
    method: 'get',
    params: data
  });
}


/**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _getById(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-issue/${id}`,
    method: 'get',
  });
}


/**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _getProjectIssueStatus() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-issue/getProjectIssueStatus`,
    method: 'get',
  });
}


/**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _setProjectIssueStage(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-issue/setProjectIssueStage`,
    method: 'put',
    data: QS.stringify(data)
  });
}


export default {};
