<template>
  <div class="container">
    <div class="information">
      <main-top-part>
        <template v-slot:title>
          <div style="display: flex">
            <i :class="menu.icon"
               class="menu-icon"
               style="color: #01408B;
                    font-size: 32px;">
            </i>
            {{ menu.name }}
          </div>

        </template>
        <template v-slot:search>
        </template>

        <template v-slot:button>
        </template>

      </main-top-part>

      <div style="overflow: hidden;">
        <circle-report></circle-report>
      </div>
    </div>
  </div>

</template>

<script>

  import MainTopPart from '@/components/MainTopPart';
  import CircleReport from "./CircleReport";


  export default {
    components: {
      CircleReport,
      MainTopPart,
    },
    data() {
      return {
        menu: [],
      }
    },
    computed: {},
    created: function () {
      const menu = JSON.parse(localStorage.getItem('MENUS'))
        .filter(menu => menu.code === 'CIRCLE_DASHBOARD');
      console.log(JSON.parse(localStorage.getItem('MENUS')))
      this.menu = menu[0];
    },
    beforeMount: function () {
    },
    mounted: function () {
    },
    beforeDestroy: function () {
    },
    destroyed: function () {
    },
    methods: {

    },
    watch: {},
    directives: {}
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>

  .container {
    width: 100%;
    height: 100%;
    display: flex;
    /*overflow-x: hidden;*/
  }

  .information {
    width: 100%;
    flex: 1;
    background-color: #fff;
    float: left;
    display: flex;
    flex-direction: column;
    position: relative;
    border-left: 1px solid #e1e1e1;
  }
</style>
