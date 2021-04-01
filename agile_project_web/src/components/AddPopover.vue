<template>
  <el-popover
    :placement="position"
    :width="width"
    :visible-arrow="false"
    v-model="changeVisible">
    <div v-for="item in options" :key="item.id">
      <div class="show_style_label" @click="addRow(item)">
        <i :class="item.color" style="margin-left: 5px;margin-right: 5px"></i>
        {{item.name}}
      </div>
    </div>
    <el-button slot="reference" type="text" style="height: 23px;line-height: 23px">
      <i class="el-icon-circle-plus"
         style="font-size: 20px;position: relative;top: -6px;cursor: pointer;"></i>
    </el-button>
  </el-popover>

</template>

<script>

async function addRow(item) {
  this.changeVisible = false;
  this.$emit('update', item);
}

export default {
  name: 'AddPopover',
  props: {
    dictCode: String,
    value: String,
    position: String,
    width: String,
  },
  data() {
    return {
      options: [],
      changeVisible: false,
    };
  },
  watch: {
    value(val) {
    },
  },
  methods: {
    addRow,
  },
  created() {
    const dictData = this.$store.getters.getDictionaryByKey(this.dictCode);
    this.options = dictData.sysDictDataList;
  },
};
</script>

<style scoped lang="scss">

  .show_style_label {
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    height: 31px;
    margin-top: 5px;
    margin-bottom: 5px;
    font-size: 15px;
    line-height: 31px;
  }

  .show_style_label:hover {
    background-color: #DBE4EE;
    color: #01408B;
  }
</style>
