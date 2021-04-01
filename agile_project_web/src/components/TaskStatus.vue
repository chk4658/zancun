<script src="../api/sysMenuApi.js"></script>
<template>
  <el-popover
    :placement="position"
    :width="width"
    :disabled="isShowing"
    :visible-arrow="false"
    v-model="changeVisible"
  >
    <el-row>
      <el-col>

        <div v-for="item in options" :key="item.id"
             @click="changeStyle(item)" class="status-container popover-status" :style="{backgroundColor:item.color}">
          {{item.name}}
        </div>

      </el-col>
    </el-row>

    <div v-if="JSON.stringify(currentValue)!=='{}'"
         slot="reference" class="status-container"
         :style="{backgroundColor:currentValue.color}">
      {{currentValue.name}}
    </div>

    <div v-if="JSON.stringify(currentValue)==='{}'" slot="reference" class="status-container"
         style="background-color: #ccc">
      暂无数据
    </div>

  </el-popover>


</template>

<script>
  function changeStyle(item) {
    this.currentValue = item;
    this.$emit('update', item.code, this.item, this.taskData);
  }

  export default {
    name: 'TaskStatus',
    props: {
      dictCode: String,
      value: String,
      position: String,
      width: String,
      hasArrow: Boolean,
      item: Object,
      taskData: Object,
      isShow: Boolean
    },
    data() {
      return {
        options: [],
        currentValue: {},
        changeVisible: false,
        isShowing: false,
      };
    },
    watch: {
      currentValue(val) {
        this.changeVisible = false;
      },
      value(val) {
        this.currentValue = this.options.filter(x => val !== null && x.code.toString() === val.toString())[0];
      },
      isShow(val) {
        this.isShowing = this.isShow;
      }
    },
    methods: {
      changeStyle,
    },
    created() {
      this.isShowing = this.isShow;
      const dictData = this.$store.getters.getDictionaryByKey(this.dictCode);
      this.options = dictData.sysDictDataList;
      if (this.value !== null && this.value !== '') {
        this.currentValue = this.options.filter(x => this.value !== null && x.code.toString() === this.value.toString())[0];
      }
    },
  };
</script>

<style scoped lang="scss">

  .status-container {
    width: 100%;
    color: #ffffff;
    cursor: pointer;
  }

  .popover-status {
    height: 23px;
    line-height: 23px;
    margin-bottom: 5px;
    text-align: center;
  }

</style>
