<template>
    <a @click="handleChange" type="text" :class="['side-trigger-a', collapsed ? 'collapsed' : '']">
        <i class="fa fa-bars"/>
    </a>
</template>
<script>
export default {
  name: 'SideTrigger',
  props: {
    collapsed: Boolean,
  },
  methods: {
    handleChange() {
      this.$emit('on-change', !this.collapsed);
    },
  },
};
</script>
<style lang="scss" scoped>
    .side-trigger-a {
        width: 40px;
        display: inline-block;
        text-align: center;
        color: #ffffff;
        i {
            transition: transform .2s ease;
        }
        &.collapsed i {
            transform: rotateZ(90deg);
            transition: transform .2s ease;
        }
    }
</style>
