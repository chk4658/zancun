import QS from 'qs';
import { fetch } from '../utils/fetch';


/**
 * 项目下的所有交付物 && 搜索 分页查询
 * taskDeliveryList totalAmount
 * @returns {Promise<Response>}
 */
export function _listTaskDeliveries(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task-delivery/delivery-all`,
    method: 'get',
    params: data,
  });
}

/**
 * 获取  根据任务id
 * taskDeliveryList
 * taskId
 * @returns {Promise<Response>}
 */
export function _listTaskDeliveriesByTaskId(data) {

  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task-delivery/get`,
    method: 'get',
    params: data,
  });
}


/**
 * 新增 交付物已存在则不新增 只新增关系
 * String taskId, TaskDelivery taskDelivery
 * @returns {Promise<Response>}
 */
export function _addTaskDelivery(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task-delivery/`,
    method: 'post',
    data: QS.stringify(data),
  });
}


/**
 * 删除任务绑定的交付物
 * String taskId, String deliveryId
 * @returns {Promise<Response>}
 */
export function _deleteByIdAndReferId(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/task-delivery/delete`,
    method: 'delete',
    params: data,
  });
}


/**
 * 连接交付物
 * String deliveryName
 * deliveryList, totalAmount
 * @returns {Promise<Response>}
 */
export function _findAllDelivery(data) {
  return fetch({
    url: `${process.env.VUE_APP_BASE_URL}/delivery/all`,
    method: 'get',
    params: data,
  });
}

export default {};
