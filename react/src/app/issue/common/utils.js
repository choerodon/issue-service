
/**
 * 生成指定长度的随机字符串
 * @param len 字符串长度
 * @returns {string}
 */
export function randomString(len = 32) {
  let code = '';
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  const maxPos = chars.length;
  for (let i = 0; i < len; i += 1) {
    code += chars.charAt(Math.floor(Math.random() * (maxPos + 1)));
  }
  return code;
}

/**
 * 根据key从sessionStorage取值
 * @param key
 */
export function getSessionStorage(key) {
  return JSON.parse(sessionStorage.getItem(key));
}

/**
 * 设置或更新sessionStorage
 * @param key
 * @param item
 */
export function setSessionStorage(key, item) {
  return sessionStorage.setItem(key, JSON.stringify(item));
}

/**
 * 根据key从sessionStorage删除
 * @param key
 */
export function removeSessionStorage(key) {
  return sessionStorage.removeItem(key);
}

/**
 * 动态计算名称宽度
 * @param val
 * @returns {number}
 */
export function getByteLen(val) {
  let len = 0;
  for (let i = 0; i < val.length; i += 1) {
    const a = val.charAt(i);
    if (a.match(/[^\x00-\xff]/ig) !== null) { // \x00-\xff→GBK双字节编码范围
      len += 15;
    } else {
      len += 10;
    }
  }
  return len;
}

/**
 * 解析url
 * @param url
 * @returns {{}}
 */
export function getRequest(url) {
  const theRequest = {};
  if (url.indexOf('?') !== -1) {
    const str = url.split('?')[1];
    const strs = str.split('&');
    for (let i = 0; i < strs.length; i += 1) {
      theRequest[strs[i].split('=')[0]] = decodeURI(strs[i].split('=')[1]);
    }
  }
  return theRequest;
}

/**
 * 获取stageList
 * @returns []
 */
export function getStageList() {
  return [
    {
      id: 'prepare',
      code: 'prepare',
      name: '准备',
      colour: '#F67F5A',
    },
    {
      id: 'todo',
      code: 'todo',
      name: '待处理',
      colour: '#ffb100',
    },
    {
      id: 'doing',
      code: 'doing',
      name: '处理中',
      colour: '#4d90fe',
    },
    {
      id: 'done',
      code: 'done',
      name: '完成',
      colour: '#00bfa5',
    },
    {
      id: 'none',
      code: 'none',
      name: '无阶段',
      colour: '#EFEFEF',
    },
  ];
}

/**
 * 获取stageMap
 * @returns {}
 */
export function getStageMap() {
  return {
    prepare: {
      id: 'prepare',
      code: 'prepare',
      name: '准备',
      colour: '#F67F5A',
    },
    todo: {
      id: 'todo',
      code: 'todo',
      name: '待处理',
      colour: '#ffb100',
    },
    doing: {
      id: 'doing',
      code: 'doing',
      name: '处理中',
      colour: '#4d90fe',
    },
    done: {
      id: 'done',
      code: 'done',
      name: '完成',
      colour: '#00bfa5',
    },
    none: {
      id: 'none',
      code: 'none',
      name: '无阶段',
      colour: '#EFEFEF',
    },
  };
}
