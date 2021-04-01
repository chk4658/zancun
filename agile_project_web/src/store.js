import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token'),
    userName: localStorage.getItem('userName'),
    id: localStorage.getItem('id'),
    collapsed: localStorage.getItem('collapsed') === 'true',
    dictionary: new Map(),
    dictionaryData: new Map(),
    languages: [],
    language: localStorage.getItem('language') || 'zh-CN',
    collection: localStorage.getItem('collection') === 'true',
    projectEditOrDel: localStorage.getItem('projectEditOrDel') === 'true',
  },
  getters: {
    getDictionaryByKey: state => key => state.dictionary.get(key),
    getDictionaryDataByKey: state => key => state.dictionaryData.get(key),
    getLanguages: state => () => state.languages,
    getLanguage: state => () => state.language,
  },
  mutations: {
    changeCollapsed(state, collapsed) {
      state.collapsed = collapsed;
      localStorage.setItem('collapsed', collapsed);
    },
    changeCollection(state, collection) {
      state.collection = collection;
      localStorage.setItem('collection', collection);
    },

    changeProjectEditOrDel(state, projectEditOrDel) {
      state.projectEditOrDel = projectEditOrDel;
      localStorage.setItem('projectEditOrDel', projectEditOrDel);
    },

    setLoginInfo(state, { userName, token, id }) {
      state.token = token;
      state.userName = userName;
      state.id = id;
      localStorage.setItem('token', token);
      localStorage.setItem('userName', userName);
      localStorage.setItem('id', id);
    },
    clearLoginInfo(state) {
      localStorage.removeItem('token');
      localStorage.removeItem('userName');
      localStorage.removeItem('id');
      localStorage.removeItem('roles');
      localStorage.removeItem('MENUS');
      localStorage.removeItem('BUTTONS');
      localStorage.removeItem('active');
      state.token = null;
      state.userName = null;
      state.id = null;
    },
    addDictionary(state, { key, value }) {
      state.dictionary.set(key, value);
    },
    addDictionaryData(state, { key, value }) {
      state.dictionaryData.set(key, value);
    },
    addLanguage(state, language) {
      state.languages.push(language);
    },
  },
});
