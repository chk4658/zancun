<template>
  <div>
    <el-checkbox-group v-model="currentValue">
      <el-checkbox
        :key="item.code"
        v-model="currentValue"
        v-for="item in options" :label="item.code">{{item.name}}</el-checkbox>
    </el-checkbox-group>
  </div>
</template>

<script>
export default {
  name: 'DictCheckbox',
  props: {
    dictCode: String,
    value: Array,
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
