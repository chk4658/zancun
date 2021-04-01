<template>
  <el-select v-model="currentValue" @change="onValueChange" placeholder="请选择">
    <el-option
      v-for="item in options"
      :key="item.code"
      :label="item.name"
      :value="item.code">
    </el-option>
  </el-select>
</template>

<script>
export default {
  name: 'DictSelect',
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
    this.options = dictData.data;
  },
};
</script>

<style scoped>

</style>
