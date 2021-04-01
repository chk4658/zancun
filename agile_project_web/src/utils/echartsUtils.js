import moment from 'moment';

// 填充天数
export function _getDayRange(beginAt, endAt) {

  const dayDiff = moment(endAt).diff(beginAt, 'days');
  const dArr = [];
  for (let i = 0; i <= dayDiff; i += 1) {
    const ds = moment(beginAt).add(i, 'days');
    const year = moment(ds).format('YYYY');
    const month = moment(ds).format('MM');
    const day = moment(ds).format('DD');
    const time = year + '-' + month + '-' + day;
    dArr.push(time);
  }
  return dArr;
}

// 填充月份
export function _getMonthRange(beginAt, endAt) {
  const dateArr = [];
  const mCount = moment(endAt).diff(beginAt, 'months');

  if (mCount > 0) {
    let startM = parseInt(moment(beginAt).format('MM'));
    let startY = parseInt(moment(beginAt).format('YYYY'));
    for (let i = 0; i <= mCount; i++) {
      if (startM < 12) {
        dateArr[i] = startY + '-' + (startM > 9 ? startM : '0' + startM);
        startM += 1;
      } else {
        dateArr[i] = startY + '-' + (startM > 9 ? startM : '0' + startM);
        startM = 1;
        startY += 1;
      }
    }
  }
  return dateArr;
}

export default {};
