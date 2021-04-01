<template>
  <el-popover
    :placement="position"
    :width="width"
    :visible-arrow="false"
    v-model="changeVisible"
  >
    <el-row>
      <el-col>
        <el-menu
          class="el-menu-vertical-demo">
          <el-menu-item v-for="item in options" :key="item.id"
                        @click="changeStyle(item)">
            <i :class="item.color"></i>
            <span slot="title">{{item.name}}</span>
          </el-menu-item>
        </el-menu>
      </el-col>
    </el-row>

    <el-button type="text" size="small" slot="reference" style="font-size: 16px">
      <i :class="currentValue.color"></i>
      {{currentValue.name}}
      <i v-if="hasArrow" class="el-icon-arrow-down"></i>
    </el-button>

  </el-popover>


</template>

<script>
function changeStyle(item) {
  this.currentValue = item;
}

export default {
  name: 'DictPopover',
  props: {
    dictCode: String,
    value: String,
    position: String,
    width: String,
    hasArrow: Boolean,
  },
  data() {
    return {
      options: [],
      currentValue: {},
      changeVisible: false,
    };
  },
  watch: {
    currentValue(val) {
      this.changeVisible = false;
      this.$emit('update', val);
    },
    value(val) {
      // this.currentValue = this.options.filter(x => x.code.toString() === val.toString());
    },
  },
  methods: {
    changeStyle,
  },
  created() {
    const dictData = this.$store.getters.getDictionaryByKey(this.dictCode);
    this.options = dictData.sysDictDataList;
    this.currentValue = this.options.filter(x => x.code.toString() === this.value.toString())[0];
  },
};
</script>

<style scoped lang="scss">

  .el-menu-item {
    &:hover {
      background-color: #003f8a;
      color: #fff;
    }
  }

</style>
