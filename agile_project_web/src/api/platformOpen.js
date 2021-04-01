import QS from 'qs';
import { fetch } from '../utils/fetch';


/**
 * 列表
 * @returns {Promise<Response>}
 */
export function _queryPlatformOpenList(query) {
    return fetch({
        url: `${process.env.VUE_APP_BASE_URL}/platform-open/list`,
        method: 'get',
        params: query,
    });
}


/**
 * 根据项目ID 查找开阀的任务及对应里程碑
 * @returns {Promise<Response>}
 */
export function _queryPlatformOpenListByProjectId(query) {
    return fetch({
        url: `${process.env.VUE_APP_BASE_URL}/platform-open/getOpenTask`,
        method: 'get',
        params: query,
    });
}