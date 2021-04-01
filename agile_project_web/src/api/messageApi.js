import QS from 'qs';
import { fetch } from '../utils/fetch';


/**
 * 用户消息列表
 * @returns {Promise<Response>}
 */
export function _queryUserMessageList(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/message-user/list`,
    method: 'get',
    params: data,
  });
}

/**
 * 标记为已读
 * messageId
 * @returns {Promise<Response>}
 */
export function _markAsReadMessage(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/message-user/mark-as-read`,
    method: 'get',
    params: data,
  });
}


/**
 * 标记为已读  全部
 * messageId
 * @returns {Promise<Response>}
 */
export function _markAsReadAllMessage() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/message-user/markAllAsRead`,
    method: 'get',
    params: {},
  });
}



/**
 * 任务催办
 * @param type 消息类型
 * @param relationId 关联ID
 * @param content 消息内容
 * @param receiveUserIds 相关人员ID
 * @param projectId 项目ID（type为TASK，必传）
 * @return .
 */
export function _taskManager(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/message-user/taskManager`,
    method: 'get',
    params: data,
  });
}


export default {};
