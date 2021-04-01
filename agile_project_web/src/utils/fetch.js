import axios from 'axios';
import {
  Message,
} from 'element-ui';
import router from '../router';
import store from '../store';

export default function defaultFetch() {
}

export async function fetch(options) {
  const instance = await axios.create({
    timeout: 1000 * 60, // 超时  (设定请求时长 60s)
    headers: {
      token: localStorage.getItem('token'),
    },
  });
  let result = await instance(options);
  result = result.data;
  if (result.code === 16003) {
    store.commit('clearLoginInfo');
    await router.push('/login');
    return result;
  }
  if (result.code === 1200) {
    return result;
  }
  Message({
    message: result.message,
    type: 'error',
    showClose: true,
    duration: 2 * 1000,
  });
  return result;
}


export async function fetchBody(options) {
  const instance = await axios.create({
    timeout: 1000 * 60, // 超时  (设定请求时长 60s)
    headers: {
      "Content-Type": "application/json;",
      token: localStorage.getItem('token'),
    },
  });
  let result = await instance(options);
  result = result.data;
  if (result.code === 16003) {
    store.commit('clearLoginInfo');
    await router.push('/login');
    return result;
  }
  if (result.code === 1200) {
    return result;
  }
  Message({
    message: result.message,
    type: 'error',
    showClose: true,
    duration: 2 * 1000,
  });
  return result;
}
