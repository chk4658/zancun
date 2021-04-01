import QS from 'qs';
import {fetch} from '../utils/fetch';


/**
 * 圈子列表
 * @returns {Promise<Response>}
 */
export function _queryParentList(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/parent-list`,
    method: 'post',
    params: query,
  });
}


/**
 * 我的圈子
 * @returns {Promise<Response>}
 */
export function _queryMyCircles(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/my-circle`,
    method: 'get',
    params: query,
  });
}


/**
 * 根据 id 查询
 * @returns {Promise<Response>}
 */
export function _queryCircle(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/${id}`,
    method: 'get',
  });
}


/**
 * 全部圈子
 * @returns {Promise<Response>}
 */
export function _queryCircles() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/all`,
    method: 'get',
  });
}


/**
 * 保存/更新
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateCircle(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 *
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _queryChildrenCircles(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/children-list`,
    method: 'get',
    params: query,
  });
}


/**
 * 删除
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _deleteCircle(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/${id}`,
    method: 'delete',
  });
}


/**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _queryCirclesMenus() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle`,
    method: 'get',
  });
}

/**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _updateCircleNameAndDescription(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/updateNameAndDescription`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _queryCircleProjectNum(query) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/num`,
    method: 'get',
    params: query,
  });
}

/**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _queryCircleOperation() {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/circleOperation`,
    method: 'get',
  });
}


/**
 * 获取
 * @returns {Promise<*|undefined>}
 * @private
 */
export function _hasByIdAndToken(id) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/circle/has/${id}`,
    method: 'get',
  });
}


/**
 * 插入IndexedDB
 * @returns {Promise<*|undefined>}
 * @private
 */
import indexedDB from '@/api/IndexedDB';

export async function _putIndexedCircleOperation(that) {
  const db = await that.$GET_INDEXED_DB.then(function (r) {
    return new Promise((resolve, reject) => {
      resolve(r);
    })
  });
  const dbStore = that.$INDEXED_DB.INDEXED_DB_DBSTORES[0];
  const result = await _queryCircleOperation();
  if (result && result.code === 1200) {
    const circleOperation = result.data.circleOperation;
    const putDatas = indexedDB.updateData(db, dbStore.name, circleOperation);
  }
}

/**
 * 获取入IndexedDB
 * @param {*} userId
 */
export async function _getIndexedCircleOperation(userId, that) {
  const db = await that.$GET_INDEXED_DB.then(function (r) {
    return new Promise((resolve, reject) => {
      resolve(r);
    })
  });
  const dbStore = that.$INDEXED_DB.INDEXED_DB_DBSTORES[0];
  const circleOperation = await indexedDB.getDataByKey(db, dbStore.name, userId);
  return circleOperation;
}

export default {};
