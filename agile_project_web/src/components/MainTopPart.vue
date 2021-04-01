<template>
  <div class="top_part">
    <slot name="nav"></slot>

    <div class="title_search">

      <slot name="title"></slot>

      <slot name="search"></slot>
    </div>
    <slot name="desc"></slot>

    <div class="btn-group">
      <slot name="button"></slot>
      <slot name="btn-search"></slot>
    </div>
  </div>
</template>
<script>
export default {
  name: 'MainTopPart',
  props: {},
  data() {
    return {};
  },
};
</script>
<style scoped>
  .top_part {
    width: 100%;
    border-bottom: 1px #e1e1e1 solid;
    padding: 30px 20px;
    box-sizing: border-box;
  }

  .title_search {
    /*height: 32px;*/
    width: 100%;
    display: flex;
    justify-content: space-between;
    font-size: 22px;
  }

  .btn-group {
    width: 100%;
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
  }

</style>
