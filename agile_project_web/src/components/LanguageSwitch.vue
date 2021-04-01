<template>
  <div class="language-switch">
    <div class="current-language">
      <span :class="currentLanguageClass"></span>
    </div>
    <ul class="language-wrap">
      <li class="language-item"
          @click="changeLanguage(language)"
          :key="language.id"
          v-for="language in languages">
        <span :class="language.icon"></span>
      </li>
    </ul>
  </div>
</template>

<script>
import { Loading } from 'element-ui';

export default {
  name: 'LanguageSwitch',
  data() {
    return {
      currentLanguage: this.$store.getters.getLanguage(),
      currentLanguageClass: 'flag-icon flag-icon-cn',
    };
  },
  computed: {
    languages() {
      return this.$store.getters.getLanguages().filter(x => x.code !== this.currentLanguage);
    },
  },
  mounted() {
    const currentLanguageObj = this.$store.getters.getLanguages()
      .filter(x => x.code === this.currentLanguage);
    this.currentLanguageClass = currentLanguageObj[0] ? currentLanguageObj[0].icon : 'flag-icon flag-icon-cn';
  },
  methods: {
    changeLanguage(language) {
      localStorage.setItem('language', language.code);
      Loading.service({ fullscreen: true });
      setTimeout(() => {
        window.location.reload();
      }, 500);
    },
  },
};
</script>

<style scoped>
  ul, li {
    list-style: none;
    margin: 0 auto;
    padding: 0;
    width: 100%;
  }

  li:hover {
    background: rgba(235, 237, 241, 0.9);
  }

  .language-switch {
    width: 100px;
    position: relative;
    text-align: center;
    z-index: 99999;
  }

  .current-language, .language-item {
    cursor: pointer;
  }

  .language-wrap {
    display: none;
    position: absolute;
  }

  .language-switch:hover .language-wrap,.language-switch:hover .current-language {
    display: block;
    background: rgba(235, 237, 241, 0.52);
  }

</style>
