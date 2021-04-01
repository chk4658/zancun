import { fetch } from '../utils/fetch';

/**
 * 获取所有阶段
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _findListByProjectIssueId(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/project-issue-history/findListByProjectIssueId`,
    method: 'get',
    params: data
  });
}



export default {};
