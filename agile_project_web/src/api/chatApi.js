import QS from 'qs';
import { fetch } from '../utils/fetch';




/**
 * 圈子列表
 * @returns {Promise<Response>}
 */
export function _queryChatListAll(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/chat/all`,
    method: 'get',
    params: query,
  });
}




/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateChat(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/chat/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 圈子列表
 * @returns {Promise<Response>}
 */
export function _findHasTaskChatByUser(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/chat/findHasTaskChatByUser`,
    method: 'get',
    params: query,
  });
}

/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateChatRedisByTaskId(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/chat/updateChatRedisByTaskId`,
    method: 'get',
    params: query,
  });
}