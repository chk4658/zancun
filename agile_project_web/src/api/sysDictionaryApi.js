import { fetch } from '../utils/fetch';


/**
 * 查询列表
 * @returns {Promise<Response>}
 */
export function _queryAllDictionaries() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-dict`,
    method: 'get',
  });
}

/**
 * 查询列表
 * @returns {Promise<Response>}
 */
export function _queryDictionaries() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-dict/list`,
    method: 'get',
  });
}

/**
 * 根据 id 查询
 * @returns {Promise<Response>}
 */
export function _queryDictionary(dictionaryId) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/dictionary/${dictionaryId}`,
    method: 'get',
  });
}

/**
 * 新增
 * @param  id
 * @param  name
 * @param  parentId
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _addDictionary(
  {
    id,
    name,
    code,
    isActive,
    remark,
  },
) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/dictionary`,
    method: 'post',
    data: {
      id,
      name,
      code,
      isActive,
      remark,
    },
  });
}

/**
 * 更新
 * @param dictionaryId
 * @param  id
 * @param  createAt
 * @param  name
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateDictionary(dictionaryId, {
  name,
  code,
  isActive,
  remark,
}) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/dictionary/${dictionaryId}`,
    method: 'patch',
    data: {
      name,
      code,
      isActive,
      remark,
    },
  });
}

/**
 * 删除
 * @param dictionaryId
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteDictionary(dictionaryId) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/dictionary/${dictionaryId}`,
    method: 'delete',
  });
}

export default {};
