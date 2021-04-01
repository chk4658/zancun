import QS from 'qs';
import { fetch, fetchBody } from '../utils/fetch';


/**
 * 全部 ----> 全部项目当前用户所有可见项目 排除草稿箱
 * allVisibleProject
 * @returns {Promise<Response>}
 */
export function _queryAllProjectList(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/all-project`,
    method: 'get',
    params: data,
  });
}

/**
 * 我的项目 ----> 当前用户参与的项目 排除草稿箱
 * curUserProject
 * @returns {Promise<Response>}
 */
export function _queryOwnerProjectList(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/user-project`,
    method: 'get',
    params: data,
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
 *未上线 ----> NOT_ONLINE
 * curUserProject
 * @returns {Promise<Response>}
 */
export function _queryNotOnlineProjectList(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/not-online`,
    method: 'get',
    params: data,
  });
}

/**
 * 保存项目新增属性
 * projectAttr
 * @returns {Promise<Response>}
 */
export function _saveProjectAttr(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-attr/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 保存/更新 保存为正常项目
 * @returns {Promise<Response>}
 */
export function _updateProject(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 获取 根据id   项目
 * project
 * @returns {Promise<Response>}
 */
export function _getProjectById(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/${id}`,
    method: 'get',
  });
}

/**
 * 获取交付物个数 项目
 * project
 * @returns {Promise<Response>}
 */
export function _getDeliveryCount(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/getDeliveryCount/${id}`,
    method: 'get',
  });
}


/**
 * 获取角色 项目
 * project
 * @returns {Promise<Response>}
 */
export function _getProjectRole(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/getProjectRole/${id}`,
    method: 'get',
  });
}


/**
 * 保存/更新  里程碑
 * @returns {Promise<Response>}
 */
export function _saveOrUpdateProjectMilestone(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 删除  里程碑
 * @returns {Promise<Response>}
 */
export function _deleteProjectMilestone(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/${id}`,
    method: 'delete',
  });
}

/**
 * 上移  里程碑
 * @returns {Promise<Response>}
 */
export function _upProjectMilestone(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/up`,
    method: 'get',
    params: data,
  });
}

/**
 * 下移  里程碑
 * @returns {Promise<Response>}
 */
export function _downProjectMilestone(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/down`,
    method: 'get',
    params: data,
  });
}


/**
 * String proOrMileId, String single, boolean flag
 * 展开 收起相关
 * single标识单个里程碑的展开收起还是全部展开全部收起
 * 'ONE'=>milestoneId
 * 'ALL'=>projectId
 * flag  true表示展开动作，false表示收起动作
 * @returns {Promise<Response>}
 */
export function _updateReferCollapsed(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/collapsed`,
    method: 'post',
    data: QS.stringify(data),
  });
}




/**
 * 删除
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteProject(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/${id}`,
    method: 'delete',
  });
}

/**
 * 根据项目ID查找部门
 * @param projectId 项目ID
 * projectDepartments
 * @private
 */
export function _findDepartmentsByProjectId(data) {
  return fetchBody({
    url: `${process.env.VUE_APP_BASE_URL}/project-department/listByProjectId`,
    method: 'get',
    params: data,
  });
}


/**
 * 导入
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _importProject(id, data) {
  return fetchBody({
    url: `${process.env.VUE_APP_BASE_URL}/project/importByTemplate/${id}`,
    method: 'post',
    data: data,
  });
}


/**
 * 导入
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _importByExistProject(id, data) {
  return fetchBody({
    url: `${process.env.VUE_APP_BASE_URL}/project/importByExistProject/${id}`,
    method: 'post',
    data: data,
  });
}

/**
 * 获取指定项目下的里程碑 from Redis
 * String projectId, @RequestParam(required = false) String searchName
 * listInRedis
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _listInRedis(data) {
  return fetchBody({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/inRedis`,
    method: 'get',
    params: data,
  });
}


//-------------------------------------
/**
 * 进入时将当前用户能看见的所有项目 的里程碑及任务信息存入indexedDB
 * @returns {Promise<*|undefined>}
 */

import indexedDB from '@/api/IndexedDB';

export async function _putProjectInformation(that, projectId, childTaskList, milestoneArray, cols) {

  const db = await that.$GET_INDEXED_DB.then(function (r) {
    return new Promise((resolve, reject) => {
      resolve(r);
    });
  });
  let projectIndexDB = {};
  const dbStore = that.$INDEXED_DB.INDEXED_DB_DBSTORES[1];


  projectIndexDB.projectId = projectId;
  projectIndexDB.projectInformation = milestoneArray;
  projectIndexDB.cols = cols;
  projectIndexDB.childTaskList = childTaskList;
  indexedDB.updateData(db, dbStore.name, projectIndexDB);

}


/**
 * 获取IndexedDB中的项目信息
 * @param {*} projectId
 */
export async function _getProjectInformation(projectId, that) {
  const db = await that.$GET_INDEXED_DB.then(function (r) {
    return new Promise((resolve, reject) => {
      resolve(r);
    });
  });
  const dbStore = that.$INDEXED_DB.INDEXED_DB_DBSTORES[1];
  const projectInformation = await indexedDB.getDataByKey(db, dbStore.name, projectId);
  return projectInformation;
}




/**
 *
 * @returns {Promise<Response>}
 */
export function _enableOrForbiddenProjectMilestone(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/enableOrForbidden`,
    method: 'get',
    params: query,
  });
}


/**
 *
 * @returns {Promise<Response>}
 */
export function _updateForbiddenProjectMilestone(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/notForbidden`,
    method: 'post',
    data: data,
  });
}


/**
 *
 * @returns {Promise<Response>}
 */
export function _updateEnabledProjectMilestone(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/toEnabled`,
    method: 'post',
    data: data,
  });
}


/**
 *
 * @returns {Promise<Response>}
 */
export function _updateToForbiddenProjectMilestone(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/toForbidden`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 *
 * @returns {Promise<Response>}
 */
export function _updateToNotEnabledProjectMilestone(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/notEnabled`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 *
 * @returns {Promise<Response>}
 */
export function _copyProjectMilestone(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-milestone/copy`,
    method: 'get',
    params: { milestoneId: id },
  });
}


/**
 * 我的项目 ----> 当前用户参与的项目 排除草稿箱
 * curUserProject
 * @returns {Promise<Response>}
 */
export function _queryOwnerNotOnlineProjectList(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/user-notOnline-project`,
    method: 'get',
    params: data,
  });
}


/**
 *
 * @returns {Promise<Response>}
 */
export function _updateProjectOnLine(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/setOnLine`,
    method: 'put',
    data: QS.stringify(data),
  });
}


/**
 *
 * @returns {Promise<Response>}
 */
export function _treeResultProject(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/tree`,
    method: 'get',
    params: query,
  });
}


/**
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _hasByIdAndToken(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/has/${id}`,
    method: 'get',
  });
}


/**
 * @returns {Promise<*|undefined>}
 * @private
 */
 export function _letProjectToStMark(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/letProjectToStMark/${id}`,
    method: 'get',
  });
}


/**
 * 我的项目 ----> 归档项目
 * curUserProject
 * @returns {Promise<Response>}
 */
 export function _findStMarkProject(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project/findStMarkProject`,
    method: 'get',
    params: data,
  });
}





export default {};
