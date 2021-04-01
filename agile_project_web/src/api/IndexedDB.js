// indexedDB.js，浏览器本地数据库操作

const INDEXED_DB_DBNAME = 'agileDB';
const INDEXED_DB_DBVERSION = 1;
const INDEXED_DB_DBSTORES = [{
    name: 'circleOperation',
    key: 'userId',
  },{
    name: 'projectInformation',
    key: 'projectId',
  }
];


export default {
  // 初始化 数据库结构
  INDEXED_DB_DBNAME,
  INDEXED_DB_DBVERSION,
  INDEXED_DB_DBSTORES,
  // indexedDB兼容
  indexedDB: window.indexedDB || window.webkitindexedDB || window.msIndexedDB || mozIndexedDB,
  // 打开数据库
  // 新对象储存空间newStore参数：newStore.name、newStore.key
  // 新增对象存储空间要更改数据库版本
  openDB: function (dbname, version, newStores) {
    var version = version || 1;
    var request = this.indexedDB.open(dbname, version);

    // onupgradeneeded，调用创建新的储存空间
    request.onupgradeneeded = function (event) {
      var db = event.target.result;
      if (newStores !== null) {
        newStores.forEach(newStore => {
          if (!db.objectStoreNames.contains(newStore.name)) {
            db.createObjectStore(newStore.name, {
              keyPath: newStore.key, autoIncrement: true
            });
          }
        });
      }
    };
    request.onerror = function (event) {
      console.log('IndexedDB数据库打开错误');
    };
    return new Promise((resolve, reject) => {
      request.onsuccess = function (event) {
        resolve(event.target.result);
      };
    });
  },
  // 删除数据库
  deleteDB: function (dbname) {
    var deleteQuest = this.indexedDB.deleteDatabase(dbname);
    deleteQuest.onerror = function () {
      console.log('删除数据库出错');
    };
    return new Promise((resolve, reject) => {
      deleteQuest.onsuccess = function () {
        resolve();
      };
    });
  },
  // 关闭数据库
  closeDB: function (db) {
    db.close();
    console.log('数据库已关闭');
  },
  // 获取数据
  getData: function (db, storename) {
    var objectStore = db.transaction(storename).objectStore(storename);
    var data = [];
    return new Promise((resolve, reject) => {
      objectStore.openCursor().onsuccess = function (event) {
        var cursor = event.target.result;
        if (cursor) {
          data.push(cursor.value);
          cursor.continue();
        } else {
          console.log('没有更多数据了！-----');
          resolve(data);
        }
      };
    });
  },
  // 通过key获取数据
  getDataByKey: function (db, storeName, keyValue) {
    var objectStore = db.transaction(storeName).objectStore(storeName);
    var request = objectStore.get(keyValue);
    request.onerror = function (event) {
      console.log('事务失败');
    };
    return new Promise((resolve, reject) => {
      request.onsuccess = function (event) {
        resolve(request.result);
      };
    });
  },
  // 更新旧值,针对输入数量
  putData: function (db, storename, dataArr) {
    var store = db.transaction(storename, 'readwrite').objectStore(storename),
      request = store.add(dataArr);
    request.onerror = function (e) {
      console.error(e);
    };
    return new Promise((resolve, reject) => {
      request.onsuccess = function (result) {
        resolve();
      };
    });
  },

  updateData: function (db, storename, dataArr) {
    var request = db.transaction(storename, 'readwrite').objectStore(storename).put(dataArr);   // put()方法自动更新了主键为1的记录(主键key名为 id)。
    request.onsuccess = function (event) {
      console.log('数据更新成功');
    };
    request.onerror = function (event) {
      console.log('数据更新失败');
    };
  },
};
