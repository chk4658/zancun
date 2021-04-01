import QS from 'qs';
import { fetch } from '../utils/fetch';

/**
 * 查询菜单及子菜单信息 默认正序排列
 * @returns {Promise<Response>}
 */
export function _listMenu(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-menu/`,
    method: 'get',
    params: data,
  });
}

/**
 * 更新
 * @param sysMenu
 * @param  id必需
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateMenu(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-menu/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 新增
 * @param  不写id
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _addMenu(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-menu/`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 删除菜单,同时删除关联子菜单
 * @param id
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteMenu(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-menu/${id}`,
    method: 'delete',
  });
}

/**
 * 菜单上移
 * @param id
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _upMenu(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-menu/menus/${id}/up`,
    method: 'patch',
  });
}


/**
 * 菜单下移
 * @param id
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _downMenu(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-menu/menus/${id}/down`,
    method: 'patch',
  });
}

/**
 * 搜索
 * @param value,,,,NAME LIKE ? OR TYPE LIKE ? OR ICON LIKE ? OR PATH LIKE ?
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _searchMenu(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-menu/search`,
    method: 'post',
    data: QS.stringify(data),
  });
}

/**
 * 根据 id 查询
 * SysMenu
 * @returns {Promise<Response>}
 */
export function _queryMenu(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/sys-menu/${id}`,
    method: 'get',
  });
}

/**
 * 树形 构成
 * @param x
 * @param data
 * @returns {{children: *}}
 * @private
 */
export function _treeGenerator(x, data) {
  const children = data
    .filter(child => child.parentId === x.id)
    .map(child => _treeGenerator(child, data));
  return {
    ...x,
    children,
  };
}

export default {};
