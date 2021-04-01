import QS from 'qs';
import { fetch } from '../utils/fetch';

/**
 * 获取所有公司及对应部门
 * @returns {Promise<Response>}
 */
export function _listCompanyAndDepartment({}) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-company/get-list`,
    method: 'get',
  });
}

/**
 * 保存/更新
 * @returns {Promise<Response>}
 */
export function _saveOrUpdateCompany(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-company/`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 根据公司Id删除
 * @returns {Promise<Response>}
 */
export function _deleteCompanyById(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-company/${id}`,
    method: 'delete',
  });
}

/**
 * 根据公司/部门名称查找
 * @returns {Promise<Response>}
 */
export function _findByName(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-company/name`,
    method: 'get',
    params: data,
  });
}

export default {};
