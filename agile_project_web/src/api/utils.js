
function isNotNull(ele) {
  if (typeof ele === 'undefined') {
    return false;
  } else if (ele === null) {
    return false;
  } else if (ele === '') {
    return false;
  }
  return true;
}


export function downloadFile(filePath) {
  const form = document.createElement('form');
  form.action = '/api/file/download';
  form.method = 'get';
  form.style.display = 'none';
  const input1 = document.createElement('input');
  input1.setAttribute('type', 'text');
  input1.setAttribute('name', 'token');
  input1.setAttribute('value', localStorage.getItem('token'));
  form.appendChild(input1);
  const input2 = document.createElement('input');
  input2.setAttribute('type', 'text');
  input2.setAttribute('name', 'filePath');
  input2.setAttribute('value', filePath);
  form.appendChild(input2);
  document.body.appendChild(form);
  console.log(form);
  form.submit();
  document.body.removeChild(form);
}


const specials = [
  {c:'%',v:'%25'},
  {c:'#',v:'%23'},
  // {c:'&',v:'%26'},
  // {c:'+',v:'%2B'},
  // {c:'=',v:'%3D'},
  // {c:' ',v:'%20'},
  // {c:'/',v:'%2F'},
  // {c:'\\',v:'%5C'},
  // {c:'?',v:'%3F'},
  // {c:'.',v:'%2E'},
  // {c:':',v:'%3A'},
]

export function urlRemoveSpecial (str) {
  specials.forEach(s => {
    str=str.replace(s.c,s.v); 
  });
  return str;
}

export default { isNotNull,downloadFile,urlRemoveSpecial };
