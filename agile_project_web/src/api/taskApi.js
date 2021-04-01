import QS from 'qs';
import {fetch} from '../utils/fetch';


/**
 * 保存/更新
 * @param taskData 任务数据
 * @returns {Promise<Response>}
 */
export function _saveTaskData(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task-data/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 删除  projectAttr中新增的属性
 * @returns {Promise<Response>}
 */
export function _deleteProjectAttrData(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-attr/${id}`,
    method: 'delete',
  });
}

/**
 * 删除任务、关联任务及关联表
 * @returns {Promise<Response>}
 */
export function _deleteTask(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/${id}`,
    method: 'delete',
  });
}

/**
 * 上移任务
 * @returns {Promise<Response>}
 */
export function _upTask(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/up`,
    method: 'get',
    params: data,
  });
}

/**
 * 下移任务
 * @returns {Promise<Response>}
 */
export function _downTask(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/down`,
    method: 'get',
    params: data,
  });
}

/**
 * 根据项目ID 获取TASK
 * @param projectId 项目ID
 * @returns {Promise<Response>}
 */
export function _getTaskByProjectId(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/getByProjectId`,
    method: 'get',
    params: data,
  });
}


/**
 * 根据项目ID 获取TASK树状结构
 * @param projectId 项目ID
 * @returns {Promise<Response>}
 */
 export function _getTreeListByProjectId(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/getTreeListByProjectId`,
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
 * 批量删除 并删除关联表 renwu
 * ids
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _batchDeleteTask(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/batch-delete`,
    method: 'delete',
    params: data,
  });
}


/**
 * 批量更新截至日期
 * ids
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _batchUpdateTaskTime(data, isUpdateMilestoneFlag) {
  console.log(isUpdateMilestoneFlag)
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/updateTaskTime?isUpdateMilestoneFlag=${isUpdateMilestoneFlag}`,
    method: 'post',
    data: data,
  });
}


/**
 * 复制
 * @returns {Promise<Response>}
 */
export function _copyTask(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/copy`,
    method: 'get',
    params: {taskId: id},
  });
}


/**
 * 获取根据id
 * task
 * @returns {Promise<Response>}
 */
export function _listByTaskId(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/${id}`,
    method: 'get',
  });
}


/**
 * 获取根据id
 * task
 * @returns {Promise<Response>}
 */
export function _listAllTaskByToken(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/token-all`,
    method: 'get',
    params: query,
  });
}


/**
 * task
 * @returns {Promise<Response>}
 */
export function _listTaskByToken(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/get-list/token`,
    method: 'get',
    params: query,
  });
}



/**
 * 查找当前用户的临时任务  当前用户为审核人、负责人 或者临时任务创建人
 * taskUserList
 * @returns {Promise<Response>}
 */
export function _listTemporaryTask() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task-user/list`,
    method: 'get',
  });
}


/**
 * 保存临时任务
 * taskUser
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateTemporaryTask(taskUser) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task-user/`,
    method: 'post',
    data: QS.stringify(taskUser),
  });
}


/**
 * 保存临时任务
 * taskUser
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _getTaskByProjectIdAndTaskName(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/getTaskByProjectIdAndTaskName/`,
    method: 'get',
    params: query,
  });
}



/**
 * 我的任务
 * taskUser
 * @returns {Promise<*|undefined>}
 * @private
 */
 export function _getMyTasks(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task/getMyTasks`,
    method: 'get',
    params: query,
  });
}

export default {};
