import QS from 'qs';
import { fetch } from '../utils/fetch';


/**
 * 圈子列表
 * @returns {Promise<Response>}
 */
export function _queryFavoriteList(name) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-collection/favorite`,
    method: 'get',
    params: { name },
  });
}


/**
 * 根据 id 查询
 * @returns {Promise<Response>}
 */
export function _queryCircle(circleId) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/${circleId}`,
    method: 'get',
  });
}


/**
 * 人员列表
 * @returns {Promise<Response>}
 */
export function _queryCircles() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/all`,
    method: 'get',
  });
}


/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateCircle(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _queryChildrenCircles(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/children-list`,
    method: 'get',
    params: query,
  });
}


/**
 * 删除
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteCircle(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/${id}`,
    method: 'delete',
  });
}


/**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _queryCirclesMenus() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle`,
    method: 'get',
  });
}

/**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateCircleNameAndDescription(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/updateNameAndDescription`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 获取指定项目下的里程碑 倒序排列
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _listMilestoneByProjectId(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/`,
    method: 'get',
    params: data,
  });
}

/**
 * 保存/修改项目任务信息表
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateTaskById(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 当前用户的收藏项目
 * @param token 当前用户token
 * @param searchName 要搜索的项目名称
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _userFavorites(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-collection/favorite`,
    method: 'get',
    params: data,
  });
}

/**
 * 添加/取消 收藏
 * @param token 当前token
 * @param projectId 项目ID
 * @param flag true -> 收藏 false -> 取消收藏
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _addOrCancelCollection(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-collection/addOrCancel`,
    method: 'get',
    params: data,
  });
}


export default {};
