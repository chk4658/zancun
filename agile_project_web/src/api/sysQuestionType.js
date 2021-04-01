import QS from 'qs';
import { fetch } from '../utils/fetch';

/**
 * 查询
 * @returns {Promise<Response>}
 */
export function _listQuestionType() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/question-type/`,
    method: 'get',
  });
}

/**
 * 查询
 * @returns {Promise<Response>}
 */
export function _listCascade() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/question-type/cascader`,
    method: 'get',
  });
}

/**
 * 查询
 * @returns {Promise<Response>}
 */
export function _getParentList() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/question-type/getParentList`,
    method: 'get',
  });
}


/**
 * 查询
 * @returns {Promise<Response>}
 */
export function _getListByParentId(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/question-type/getListByParentId`,
    method: 'get',
    params: data
  });
}


/**
 * 保存更xin
 * @returns {Promise<Response>}
 */
export function _saveOrUpdate(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/question-type/`,
    method: 'post',
    data: QS.stringify(data)
  });
}


/**
 * huoqu
 * @returns {Promise<Response>}
 */
export function _getListById(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/question-type/${id}`,
    method: 'get',
  });
}


/**
 * shanchu
 * @returns {Promise<Response>}
 */
export function _deleteById(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/question-type/${id}`,
    method: 'delete',
  });
}

export default {};
