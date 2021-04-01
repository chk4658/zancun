<template>
  <div>
      <div v-infinite-scroll="load" infinite-scroll-disabled="disabled" infinite-scroll-distance='10'>
        <el-menu-item v-for="(data,index) in datas"
                    :index="'/circle?id='+data.id" @click="go(data.id)" :key="index">
          <i class="fa fa-circle-o-notch"></i>
          {{ circle.name }}
        </el-menu-item>
      </div>
  </div>
</template>

<script>
export default {
  name: 'MenuScroll',
  props: {
    datas: Array,
  },
  data () {
    return {
    }
  },
  computed: {},
  created: function () {},
  beforeMount: function () {},
  mounted: function () {},
  beforeDestroy: function () {},
  destroyed: function () {},
  methods: {},
  watch: {},
  directives: {}
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
