import QS from 'qs';
import { fetch } from '../utils/fetch';

export function _toPdfFile(sourceFile) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/preview`,
      method: 'get',
      params: sourceFile,
    });
  }


export function _preview(sourceFile) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/preview1`,
      method: 'get',
      params: sourceFile,
    });
  }


  export function _fileCheck(sourceFile) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/file/check`,
      method: 'get',
      params: sourceFile,
    });
  }