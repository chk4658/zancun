import QS from 'qs';
import { fetch } from '../utils/fetch';

/**
 * 字典表 分页查询
 * SysDictList, totalAmount
 * @returns {Promise<Response>}
 */
export function _listEnumeration(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-dict/`,
    method: 'get',
    params: data,
  });
}

/**
 * 根据id获取枚举类
 * SysDict
 * @returns {Promise<Response>}
 */
export function _getEnumerationById(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-dict/${id}`,
    method: 'get',
  });
}

/**
 * 保存/更新枚举类
 * sysDict
 * @returns {Promise<Response>}
 */
export function _saveEnumeration(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-dict/`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 保存/更新枚举类中的状态值
 * sysDictData
 * @returns {Promise<Response>}
 */
export function _saveDictData(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-dict-data/`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 删除枚举类中的状态值
 * id
 * @returns {Promise<Response>}
 */
export function _deleteDictData(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-dict-data/${id}`,
    method: 'delete',
  });
}

/**
 * 删除枚举类
 * @returns {Promise<Response>}
 */
export function _deleteDict(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-dict/${id}`,
    method: 'delete',
  });
}

/**
 * 删除枚举类
 * @returns {Promise<Response>}
 */
export function _listEnumerationByCode(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-dict/code-list`,
    method: 'get',
    params:query
  });
}

export default {};
