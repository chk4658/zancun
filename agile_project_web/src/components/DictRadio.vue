<template>
  <div>
    <el-radio
      :key="item.code"
      v-model="currentValue"
      v-for="item in options" :label="item.code">{{item.name}}
    </el-radio>
  </div>
</template>

<script>
export default {
  name: 'DictRadio',
  props: {
    dictCode: String,
    value: String,
  },
  data() {
    return {
      options: [],
      currentValue: this.value,
    };
  },
  watch: {
    currentValue(val) {
      this.$emit('input', val);
    },
    value(val) {
      this.currentValue = val;
    },
  },
  methods: {
    onValueChange() {
    },
  },
  created() {
    const dictData = this.$store.getters.getDictionaryByKey(this.dictCode);
    this.options = dictData.sysDictDataList;
  },
};
</script>

<style scoped>

</style>
