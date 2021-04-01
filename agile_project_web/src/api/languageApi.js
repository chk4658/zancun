import { fetch } from '../utils/fetch';

/**
 * 查询列表
 * @returns {Promise<Response>}
 */
export function _queryAllLanguages() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/languages`,
    method: 'get',
    params: {
      scope: 'all',
    },
  });
}


/**
 * 查询列表
 * @returns {Promise<Response>}
 */
export function _queryLanguages(
  {
    page, size, direction, sort,
  },
) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/languages`,
    method: 'get',
    params: {
      page,
      size,
      direction,
      sort,
    },
  });
}

/**
 * 根据 id 查询
 * @returns {Promise<Response>}
 */
export function _queryLanguage(languageId) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/languages/${languageId}`,
    method: 'get',
  });
}

/**
 * 新增
 * @param  name
 * @param  code
 * @param  isActive
 * @param  icon
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _addLanguage(
  {
    name,
    code,
    isActive,
    icon,
  },
) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/languages`,
    method: 'post',
    data: {
      name,
      code,
      isActive,
      icon,
    },
  });
}

/**
 * 更新
 * @param languageId
 * @param  name
 * @param  code
 * @param  isActive
 * @param  icon
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateLanguage(languageId, {
  isActive,
}) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/languages/${languageId}`,
    method: 'patch',
    data: {
      isActive,
    },
  });
}

/**
 * 删除
 * @param languageId
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteLanguage(languageId) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/languages/${languageId}`,
    method: 'delete',
  });
}

export default {};
