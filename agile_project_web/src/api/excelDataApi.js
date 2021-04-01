import QS from 'qs';
import { fetch } from '../utils/fetch';



export function _analysisExcel(query) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/excel-data/analysisExcel`,
      method: 'get',
      params: query,
    });
  }

  export function _checkExcel(query) {
    return fetch({
      url: `${process.env.VUE_APP_BASE_URL}/excel-data/checkExcel`,
      method: 'get',
      params: query,
    });
  }