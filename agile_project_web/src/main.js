import Vue from 'vue';
import ElementUI from 'element-ui';
import VueResize from 'vue-resize';
import VueI18n from 'vue-i18n';
import enLocale from 'element-ui/lib/locale/lang/en';
import zhCNLocale from 'element-ui/lib/locale/lang/zh-CN';
import ElementLocale from 'element-ui/lib/locale';
import App from './App.vue';
import store from './store';
import router from './router';
import infiniteScroll from 'vue-infinite-scroll'
import 'vue-resize/dist/vue-resize.css';
import 'font-awesome/scss/font-awesome.scss';
import 'flag-icon-css/css/flag-icon.min.css';
import '../public/dark-theme/index.css';
import '../public/main.scss';
import elTableInfiniteScroll from 'el-table-infinite-scroll';

import globalVariable from '../src/api/globalVariable';

// 不太适配
// import plTable from 'pl-table'
// import 'pl-table/themes/index.css' // 引入样式（必须引入)，vuecli3.0不需要配置，cli2.0请查看webpack是否配置了url-loader对woff，ttf文件的引用,不配置会报错哦
// import 'pl-table/themes/plTableStyle.css' // 默认表格样式很丑 引入这个样式就可以好看啦（如果你不喜欢这个样式，就不要引入，不引入就跟ele表格样式一样）
// Vue.use(plTable);

// import Blob from './vendor/Blob'
// import Export2Excel from './vendor/Export2Excel.js'


import {_queryDictionaries} from './api/sysDictionaryApi';
import {_queryAllLanguages} from './api/languageApi';


Vue.config.productionTip = false;

Vue.use(ElementUI, {size: 'mini'});

Vue.use(VueResize);

Vue.use(infiniteScroll);

Vue.use(elTableInfiniteScroll);

Vue.use(globalVariable);


// Vue.prototype.$player = new Vue();


import _indexedDB from '@/api/IndexedDB';

Vue.prototype.$INDEXED_DB = _indexedDB;//挂载到Vue实例上面

import {_getIndexedCircleOperation} from './api/circleApi';
import {console} from "vuedraggable/src/util/helper";

Vue.prototype.$GET_INDEXED_DB = getIndexedDb();


async function getIndexedDb() {
  return await Vue.prototype.$INDEXED_DB.openDB(
    Vue.prototype.$INDEXED_DB.INDEXED_DB_DBNAME,
    Vue.prototype.$INDEXED_DB.INDEXED_DB_DBVERSION,
    Vue.prototype.$INDEXED_DB.INDEXED_DB_DBSTORES,
  );
}


router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!store.state.token) {
      next({
        path: '/login',
        query: {redirect: to.fullPath},
      });
    } else {
      next();
    }
  } else {
    next(); // 确保一定要调用 next()
  }
});


Vue.directive('has', {
  inserted(el, binding) {
    function permissionJudge(value) {
      const set = new Set(JSON.parse(localStorage.getItem('BUTTONS')));
      return set.has(value);
    }

    if (!permissionJudge(binding.value)) {
      el.parentNode.removeChild(el);
    }
  },
});

// 判断是否有部门管理的popover，如果部门编辑和部门删除权限都没有，就没必要显示了，防止显示一个空白
Vue.directive('hasDepartmentPopover', {
  inserted(el, binding) {
    function permissionJudge(value) {
      const set = new Set(JSON.parse(localStorage.getItem('BUTTONS')));
      return set.has(value.split('+')[0]) && set.has(value.split('+')[1]);
    }

    if (!permissionJudge(binding.value)) {
      el.parentNode.removeChild(el);
    }
  },
});


Vue.directive('hasCirce', {
  inserted: async function (el, binding, vnode, oldVnode) {
    const value = JSON.parse(binding.value.replace(/'/g, '"'));
    const isAdmin = localStorage.getItem("isAdmin");
    let has = false;
    if (value.circleId === '' || isAdmin === "true") {
      has = true;
    } else {
      const id = localStorage.getItem('id');
      const circleOperation = await _getIndexedCircleOperation(id, Vue.prototype);
      if (value.key === 'hasAddCircle') {
        has = circleOperation.operations.filter(o => o.circleId === value.circleId && o.hasAddCircle === value.value).length > 0;
      } else if (value.key === 'hasAddProject') {
        has = circleOperation.operations.filter(o => o.circleId === value.circleId && o.hasAddProject === value.value).length > 0;
      } else if (value.key === 'hasOperate') {
        has = circleOperation.operations.filter(o => o.circleId === value.circleId && o.hasOperate === value.value).length > 0;
      }
    }
    if (!has) {
      el.style.display = 'none';
    } else {
      el.style.display = '';
    }
  },
  update: async function (el, binding, vnode, oldVnode) {
    const value = JSON.parse(binding.value.replace(/'/g, '"'));
    const isAdmin = localStorage.getItem("isAdmin");
    let has = false;
    if (value.circleId === '' || isAdmin === "true") {
      has = true;
    } else {
      const id = localStorage.getItem('id');
      const circleOperation = await _getIndexedCircleOperation(id, Vue.prototype);
      if (value.key === 'hasAddCircle') {
        has = circleOperation.operations.filter(o => o.circleId === value.circleId && o.hasAddCircle === value.value).length > 0;
      } else if (value.key === 'hasAddProject') {
        has = circleOperation.operations.filter(o => o.circleId === value.circleId && o.hasAddProject === value.value).length > 0;
      } else if (value.key === 'hasOperate') {
        has = circleOperation.operations.filter(o => o.circleId === value.circleId && o.hasOperate === value.value).length > 0;
      }
    }
    if (!has) {
      el.style.display = 'none';
    } else {
      el.style.display = '';
    }
  },
});


Vue.directive('hasProOperation', {
  inserted(el, binding, vnode, oldVnode) {
    const value = JSON.parse(binding.value.replace(/'/g, '"'));


    /**
     * 1未开始
     * 2进行中
     * 3待审核
     * 4已拒绝
     * 5带条件通过
     * 6通过
     * @type {boolean}
     */
    let has = false;
    let id = localStorage.getItem('id');

    // 负责人具有开始任务的权限
    if (value.key === 'hasBeginTask') {
      has = value.taskReviewerId === id && value.taskStatus === '1';
    }

    // 负责人具有提交审核的权限
    if (value.key === 'hasPushReviewTask') {
      has = value.taskReviewerId === id && (value.taskStatus === '2' || value.taskStatus === '4' || value.taskStatus === '5');
    }

    // 任务审核人具有审核任务的权限
    if (value.key === 'hasConfirmTask') {
      has = value.taskConfirmerId === id && value.taskStatus === '3';
    }

    // 只有任务的负责人具有删除交付物的权限
    // 新增“”： 只有任务状态在进行中时可以删除交付物，避免出现提交审核之后删除交付物的情况
    // 1.27新增：由于交付物拒绝后的重新上传引起的，重新上传的交付物存在点错导致无法上传问题
    if (value.key === 'hasDeleteTaskDelivery') {
      has = value.taskReviewerId === id && value.taskDeliveryAuditStatus !== '1' && (value.taskStatus === '2' || value.taskStatus === '4' || value.taskStatus === '5');
    }

    // 确认人有审核任务交付物的权限
    if (value.key === 'hasReviewTaskDelivery') {
      has = value.taskConfirmerId === id && value.taskDeliveryAuditStatus === '0' && value.taskStatus === '3';
    }

    // 只有任务的负责人具有添加任务交付物的权限
    if (value.key === 'hasAddTaskDelivery') {
      has = value.taskReviewerId === id && (value.taskStatus === '2' || value.taskStatus === '4' || value.taskStatus === '5');
    }

    // 只有任务的负责人具有重新上传任务交付物的权限
    if (value.key === 'hasReAddTaskDelivery') {
      has = value.taskReviewerId === id && value.taskStatus === '4';
    }

    // 只有圈长和项目经理(项目负责人)有重新开始任务的权限
    if (value.key === 'hasReBeginTask') {

      has = value.reBeginAuthority === 'true' && value.taskStatus === '6';
    }

    // 什么权限都没有
    if (value.key === 'allNot') {
      if (value.taskStatus === '2' || value.taskStatus === '4' || value.taskStatus === '5' || value.taskStatus === '1') {
        has = value.taskReviewerId !== id && value.authority === 'false'
      } else {
        has = value.taskConfirmerId !== id && value.authority === 'false'
      }
    }


    // 什么权限都没有  临时任务
    if (value.key === 'allTNot') {
      if (value.taskStatus === '2' || value.taskStatus === '4' || value.taskStatus === '5' || value.taskStatus === '1') {
        has = value.taskReviewerId !== id && value.authority === 'false'
      } else {
        has = value.taskConfirmerId !== id && value.authority === 'false'
      }

    }

    if (!has) {
      el.style.display = 'none';
    } else {
      el.style.display = '';
    }
  },
  update: async function (el, binding, vnode, oldVnode) {
    const value = JSON.parse(binding.value.replace(/'/g, '"'));


    let has = false;
    let id = localStorage.getItem('id');
    if (value.key === 'hasBeginTask') {
      has = value.taskReviewerId === id && value.taskStatus === '1';
    }
    if (value.key === 'hasPushReviewTask') {
      has = value.taskReviewerId === id && (value.taskStatus === '2' || value.taskStatus === '4' || value.taskStatus === '5');
    }
    if (value.key === 'hasConfirmTask') {
      has = value.taskConfirmerId === id && value.taskStatus === '3';
    }
    if (value.key === 'hasDeleteTaskDelivery') {
      has = value.taskReviewerId === id && value.taskDeliveryAuditStatus !== '1' && (value.taskStatus === '2' || value.taskStatus === '4' || value.taskStatus === '5');
    }
    if (value.key === 'hasReviewTaskDelivery') {
      has = value.taskConfirmerId === id && value.taskDeliveryAuditStatus === '0' && value.taskStatus === '3';
    }
    if (value.key === 'hasAddTaskDelivery') {
      has = value.taskReviewerId === id && (value.taskStatus === '2' || value.taskStatus === '4' || value.taskStatus === '5');
    }
    if (value.key === 'hasReAddTaskDelivery') {
      has = value.taskReviewerId === id && value.taskStatus === '4';
    }
    if (value.key === 'hasReBeginTask') {
      has = value.reBeginAuthority === 'true' && value.taskStatus === '6';
    }
    // if (value.key === 'allNot') {
    //
    //   if (value.taskStatus === '2' || value.taskStatus === '4' || value.taskStatus === '5' || value.taskStatus === '1') {
    //     has = value.taskReviewerId !== id && value.authority === 'false' && value.onlyOne === 'true'
    //   } else {
    //     has = value.taskConfirmerId !== id && value.authority === 'false' && value.onlyOne === 'true'
    //   }
    // }
    if (value.key === 'allNot') {
      if (value.taskStatus === '2' || value.taskStatus === '4' || value.taskStatus === '5' || value.taskStatus === '1') {
        has = value.taskReviewerId !== id && value.authority === 'false'
      } else {
        has = value.taskConfirmerId !== id && value.authority === 'false'
      }
    }
    if (value.key === 'allTNot') {
      if (value.taskStatus === '2' || value.taskStatus === '4' || value.taskStatus === '5' || value.taskStatus === '1') {
        has = value.taskReviewerId !== id && value.authority === 'false'
      } else {
        has = value.taskConfirmerId !== id && value.authority === 'false'
      }

    }

    if (!has) {
      el.style.display = 'none';
    } else {
      el.style.display = '';
    }
  },
});


Vue.directive('focus', {
  inserted(el) {
    setTimeout(() => {
      el.children[0].focus();
    }, 0);
  },

});

Vue.directive('focus2', {
  inserted(el) {
    setTimeout(() => {
      el.focus();
    }, 0);
  },
  update(el) {
    setTimeout(() => {
      el.focus();
    }, 0);
  },
});

_queryDictionaries()
  .then((result) => {
    console.log(result.data.SysDictList)
    result.data.SysDictList.forEach((x) => {
      store.commit('addDictionary', {
        key: x.code,
        value: x,
      });
      x.sysDictDataList.forEach((d) => {
        store.commit('addDictionaryData', {
          key: `${x.code}-${d.code}`,
          value: d,
        });
      });
    });

    new Vue({
      store,
      router,
      // i18n,
      render: h => h(App),
    }).$mount('#app');
  });


// 注册 (指令函数)
Vue.directive('dict', {
  inserted(el, binding) {
    const {parentNode} = el;
    parentNode.removeChild(el);
    parentNode.append(store.getters.getDictionaryDataByKey(binding.value).name);
  },
});

Vue.prototype.$dict = (code, value) => {
  const key = `${code}-${value}`;
  return store.getters.getDictionaryDataByKey(key) ? store.getters.getDictionaryDataByKey(key).name : '';
};


// v-dialogDrag: 弹窗拖拽+水平方向伸缩
Vue.directive('dialogDrag', {
  bind(el, binding, vnode, oldVnode) {
    // 弹框可拉伸最小宽高
    const minWidth = document.body.clientWidth * 0.3;
    const minHeight = document.body.clientWidth * 0.4;
    const maxWidht = Math.max;
    const maxHeight = document.body.clientWidth * 0.9;
    // 初始非全屏
    let isFullScreen = false
    // 当前顶部高度
    let nowMarginTop = 0
    // 获取弹框头部（这部分可双击全屏）
    const dialogHeaderEl = el.querySelector('.el-dialog__header')
    // 弹窗
    const dragDom = el.querySelector('.el-dialog');
    // 给弹窗加上overflow auto；不然缩小时框内的标签可能超出dialog；
    dragDom.style.overflow = 'hidden'
    // 清除选择头部文字效果
    // dialogHeaderEl.onselectstart = new Function("return false");
    // 头部加上可拖动cursor
    dialogHeaderEl.style.cursor = 'move'
    // 获取原有属性 ie dom元素.currentStyle 火狐谷歌 window.getComputedStyle(dom元素, null);
    const sty = dragDom.currentStyle || window.getComputedStyle(dragDom, null)
    const moveDown = e => {
      // 鼠标按下，计算当前元素距离可视区的距离
      const disX = e.clientX - dialogHeaderEl.offsetLeft
      const disY = e.clientY - dialogHeaderEl.offsetTop
      // 获取到的值带px 正则匹配替换
      let styL, styT
      // 注意在ie中 第一次获取到的值为组件自带50% 移动之后赋值为px
      if (sty.left.includes('%')) {
        styL = +document.body.clientWidth * (+sty.left.replace(/\%/g, '') / 100)
        styT = +document.body.clientHeight * (+sty.top.replace(/\%/g, '') / 100)
      } else {
        styL = +sty.left.replace(/\px/g, '')
        styT = +sty.top.replace(/\px/g, '')
      }
      document.onmousemove = function(e) {
        // 通过事件委托，计算移动的距离
        const l = e.clientX - disX
        const t = e.clientY - disY
        // 移动当前元素
        dragDom.style.left = `${l + styL}px`
        dragDom.style.top = `${t + styT}px`
        // 将此时的位置传出去
        // binding.value({x:e.pageX,y:e.pageY})
      }
      document.onmouseup = function(e) {
        document.onmousemove = null
        document.onmouseup = null
      }
    }
    dialogHeaderEl.onmousedown = moveDown
    // 当前宽高
    let nowWidth = 0
    let nowHight = 0
    // 双击头部全屏效果
    dialogHeaderEl.ondblclick = e => {
      if (isFullScreen === false) {
        // nowHight = dragDom.clientHeight
        nowWidth = dragDom.clientWidth
        nowMarginTop = dragDom.style.marginTop
        dragDom.style.left = 0
        dragDom.style.top = 0
        dragDom.style.height = '100VH'
        dragDom.style.width = '100VW'
        dragDom.style.marginTop = 0
        isFullScreen = true
        dialogHeaderEl.style.cursor = 'initial'
        dialogHeaderEl.onmousedown = null
      } else {
        dragDom.style.height = 'auto'
        dragDom.style.width = nowWidth + 'px'
        dragDom.style.marginTop = nowMarginTop
        isFullScreen = false
        dialogHeaderEl.style.cursor = 'move'
        dialogHeaderEl.onmousedown = moveDown
      }
    }
    dragDom.onmousemove = function(e) {
      const dialogBodyEl  =  dragDom.querySelector('.el-dialog__body')
      dialogBodyEl.style.overflow = "hidden";
      if (e.clientX > dragDom.offsetLeft + dragDom.clientWidth - 10 ) {
        dragDom.style.cursor = 'e-resize'
      } else if (el.scrollTop + e.clientY > dragDom.offsetTop + dragDom.clientHeight - 10) {
        dragDom.style.cursor = 's-resize'
      } else {
        dragDom.style.cursor = 'default'
        dragDom.onmousedown = null
      }
      dragDom.onmousedown = e => {
        const clientX = e.clientX
        const clientY = e.clientY
        const elW = dragDom.clientWidth
        const elH = dragDom.clientHeight
        const EloffsetLeft = dragDom.offsetLeft
        const EloffsetTop = dragDom.offsetTop
        dragDom.style.userSelect = 'none'
        const ELscrollTop = el.scrollTop
        // 判断点击的位置是不是为头部
        if (clientX > EloffsetLeft && clientX < EloffsetLeft + elW &&  clientY > EloffsetTop && clientY < EloffsetTop + 100 ) {
          // 如果是头部在此就不做任何动作，以上有绑定dialogHeaderEl.onmousedown = moveDown;
        } else {
          document.onmousemove = function(e) {
            console.log("22222");

            // 移动时禁用默认事件
            e.preventDefault()
            // 左侧鼠标拖拽位置
            // if (clientX > EloffsetLeft && clientX < EloffsetLeft + 10) {
            //   // 往左拖拽
            //   if (clientX > e.clientX) {
            //     if (dragDom.clientWidth < minWidth) {
            //       console.log('111111',dragDom.clientWidth , minWidth);
            //       // dragDom.style.width = minWidth  + 'px'
            //     } else {
            //       const _width = elW - elW + (clientX - e.clientX) * 2;
            //       dragDom.style.width = (_width > maxWidht ? maxWidht : _width)  + 'px'
            //     }

            //   }
            //   // 往右拖拽
            //   if (clientX < e.clientX) {
            //     if (dragDom.clientWidth < minWidth) {
            //       console.log('222',dragDom.clientWidth , minWidth)
            //     } else {
            //       const _width = elW - elW - (e.clientX - clientX) * 2;
            //       dragDom.style.width = (_width > maxWidht ? maxWidht : _width) + 'px'
            //     }
            //   }
            // }
            // 右侧鼠标拖拽位置
            if ( clientX > EloffsetLeft + elW - 10 && clientX < EloffsetLeft + elW) {
              // 往左拖拽
              if (clientX > e.clientX) {
                if (dragDom.clientWidth < minWidth) {
                } else {
                  const _width = elW - (clientX - e.clientX) * 2;
                  dragDom.style.width = (_width > maxWidht ? maxWidht : _width) + 'px'
                }
              }
              // 往右拖拽
              if (clientX < e.clientX) {
                const _width = elW + (e.clientX - clientX) * 2;
                dragDom.style.width = (_width > maxWidht ? maxWidht : _width) + 'px'
              }
            }
            // 底部鼠标拖拽位置
            if ( ELscrollTop + clientY > EloffsetTop + elH - 20 && ELscrollTop + clientY < EloffsetTop + elH ) {
              // 往上拖拽
              if (clientY > e.clientY) {
                if (dragDom.clientHeight < minHeight) {
                  console.log()
                } else {
                  dragDom.style.height = elH - (clientY - e.clientY) * 2 + 'px'
                }
              }
              // 往下拖拽
              if (clientY < e.clientY) {
                dragDom.style.height = elH + (e.clientY - clientY) * 2 + 'px'
              }
            }
          }
          // 拉伸结束
          document.onmouseup = function(e) {
            document.onmousemove = null
            document.onmouseup = null
          }
        }
      }
    }
  }
})

