<template>
  <div>
    <p
      v-if="!obj.isEdit.name"
      style="cursor: pointer"
      @click="openChat"
      class="text-style">

      <el-tooltip class="item" effect="dark" placement="top-start"
                  :disabled="isDisabledTooltip">

        <div slot="content">
          <div style="width: 300px;word-break: break-all;">{{obj.name}}</div>
        </div>
        <div style="float: left;max-width: 420px;">
           <span
             style="max-width: 380px;float: left;display: block;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;cursor: pointer"
             :ref="'name'+obj.id"
             @mouseover="onOverflow('name'+obj.id)">
            {{obj.name}}
          </span>
          <el-badge value="!" :hidden='hasChatTaskIds.findIndex(id => id === obj.id) === -1'></el-badge>
        </div>


      </el-tooltip>

      <i class="el-icon-edit"
         v-if="authority"
         @click.stop="recordName(obj)"
         style="margin-left: 5px;cursor: pointer;float: left;display: block;height: 21px;line-height: 21px"></i>
    </p>
    <el-input
      v-model="obj.name"
      size="mini"
      v-if="obj.isEdit.name"
      v-focus="obj.isEdit.name"
      @blur="change(obj)"
      @keyup.enter.native="$event.target.blur"/>

  </div>

</template>


<script>


  function change(item) {
    item.isEdit.name = false;
    if (trim(item.name) === '' || trim(item.name) === this.nameOld) {
      item.name = this.nameOld;

      if (item.id === '') {

        this.$emit('pull', item);
      }

    } else {
      this.$emit('update', item);
    }
  }

  function recordName(obj) {

    this.nameOld = '';
    obj.isEdit.name = true;
    this.nameOld = obj.name;
  }

  function openChat() {
    this.webScoketChat = "";
    this.$emit('chat', this.item);
  }

  //去除字符串左右空格，防止新增名称为空格的里程碑或任务
  function trim(s) {
    return s.replace(/(^\s*)|(\s*$)/g, "");
  }

  function onOverflow(str) {
    let part = this.$refs[str].offsetWidth;
    let act = this.$refs[str].scrollWidth;
    // 判断是否禁用tooltip功能
    this.isDisabledTooltip = !(part < act);

  }


  export default {
    name: 'ClickInput',
    props: {
      item: Object,
      authority: Boolean,
      hasChatTaskIds: Array,

    },
    data() {
      return {
        obj: '',
        nameOld: '',
        isDisabledTooltip: false,
        webScoketChat: "",
        userId: localStorage.getItem('id')
      };
    },
    watch: {
      item(val) {
        this.obj = this.item;
      }
    },
    methods: {
      change,
      recordName,
      trim,
      openChat,
      onOverflow,
    },
    created() {
      this.obj = this.item;
    },
  };
</script>

<style scoped lang="scss">


  .text-style {
    height: 21px;
    line-height: 21px;
    margin: 0;
    border: 1px dashed transparent;

    &:hover {
      border: 1px dashed #ccc;
    }


  }


</style>
