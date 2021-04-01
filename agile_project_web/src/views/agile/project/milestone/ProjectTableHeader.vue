<template>
  <div :style="{width: milestonesWidth}">
    <div class="milestone">
      <div class="right-margin">

        <el-popover
          placement="bottom-start"
          :visible-arrow="false"
          width="200"
          v-model="arrowVisible"
          trigger="click">
          <div>
            <div class="show_style_label" @click="deleteM(item)" v-if="authority">
              <i class="el-icon-delete" style="margin-left: 5px;margin-right: 5px"></i>
              删除
            </div>
            <div class="show_style_label" @click="copyM(item)" v-if="authority">
              <i class="el-icon-document-copy" style="margin-left: 5px;margin-right: 5px"></i>
              复制
            </div>
            <div class="show_style_label"
                 v-if="authority"
                 @click="updateToForbiddenOrNotEnabled({id:item.id,type: 'PROJECT_MILESTONE' },'enabled')">
              <i class="el-icon-turn-off" style="margin-left: 5px;margin-right: 5px"></i>
              不启用
            </div>
            <div class="show_style_label"
                 v-if="authority"
                 @click="updateToForbiddenOrNotEnabled({id:item.id,type: 'PROJECT_MILESTONE' },'forbidden')">
              <i class="el-icon-close" style="margin-left: 5px;margin-right: 5px"></i>
              禁用
            </div>
            <div class="show_style_label" @click="moveM(item,'up')" v-if="item.id!==first&&authority">
              <i class="el-icon-top" style="margin-left: 5px;margin-right: 5px"></i>
              上移
            </div>
            <div class="show_style_label" @click="moveM(item,'down')" v-if="item.id!==end&&authority">
              <i class="el-icon-bottom" style="margin-left: 5px;margin-right: 5px"></i>
              下移
            </div>
            <div class="show_style_label" @click="cuttle(item,'FOLD','ONE')" v-show="fold">
              <i class="el-icon-folder" style="margin-left: 5px;margin-right: 5px"></i>
              收起
            </div>
            <div class="show_style_label" @click="cuttle(item,'UNFOLD','ONE')" v-show="!fold">
              <i class="el-icon-folder-opened" style="margin-left: 5px;margin-right: 5px"></i>
              展开
            </div>
            <div class="show_style_label" @click="showMilestoneLog(item)">
              <i class="el-icon-notebook-1" style="margin-left: 5px;margin-right: 5px"></i>
              里程碑日志
            </div>
            <div class="show_style_label" @click="cuttle(item,'FOLD','ALL')" v-show="!foldAll">
              <i class="el-icon-folder-remove" style="margin-left: 5px;margin-right: 5px"></i>
              全部收起
            </div>
            <div class="show_style_label" @click="cuttle(item,'UNFOLD','ALL')" v-show="!foldupAll">
              <i class="el-icon-folder-add" style="margin-left: 5px;margin-right: 5px"></i>
              全部展开
            </div>
          </div>
          <i slot="reference" class="el-icon-caret-right" v-if="!arrowVisible"
             style="cursor: pointer;height: 23px;line-height: 23px;font-size: 15px;"></i>
          <i slot="reference" class="el-icon-caret-bottom" v-if="arrowVisible"
             style="cursor: pointer;height: 23px;line-height: 23px;font-size: 15px;"></i>
        </el-popover>


      </div>

      <div class="right-margin">
        <div style="height: 16px;width: 16px;border-radius: 8px;margin-top: 8px"
             :style="{backgroundColor:timeStatus(item)}"></div>
      </div>
      <div class="right-margin">
        <span style="line-height: 32px;vertical-align:top;cursor: pointer" @click="authority&&changeIsEdit(item)"
              v-if="!item.isEdit.name">{{item.name}}</span>
        <el-input type="text"
                  size="small"
                  style="vertical-align:top"
                  @blur="childSaveProjectMilestone(item)"
                  @keyup.enter.native="$event.target.blur"
                  v-model="item.name"
                  v-focus="item.isEdit.name"
                  v-if="item.isEdit.name"/>
      </div>
      <div class="right-margin">
        <el-date-picker
          size="small"
          style="width: 158px;vertical-align:top"
          v-model="item.estEndTime"
          type="date"
          :readonly="!authority"
          format="yyyy-MM-dd"
          @change="changEstEndTime(item)"
          :clearable="false"
          value-format="yyyy-MM-dd"
          placeholder="选择日期">
        </el-date-picker>
      </div>
      <div class="right-margin">
        <el-tag type="info"
                size="middle"
                style="width: 200px;text-align: center;cursor: pointer;
                    overflow: hidden;white-space: nowrap;text-overflow: ellipsis;vertical-align:top"
                @click="authority&&childAddRole(item,'review','milestone')">
          {{item.reviewProjectRoleId===null || item.reviewProjectRoleId === undefined || item.reviewProjectRole===null
          ?'':item.reviewProjectRole.roleName}}
          <span v-if="item.reviewProjectRoleId===null || item.reviewProjectRoleId === undefined"
                style="color: #CDD0D7">请选择负责人角色</span>
        </el-tag>
      </div>
      <div class="right-margin">
        <el-tag type="info"
                size="middle"
                style="width: 200px;text-align: center;cursor: pointer;overflow:
                    hidden;white-space: nowrap;text-overflow: ellipsis;vertical-align:top"
                @click="authority&&childAddRole(item,'confirm','milestone')">
          {{item.confirmProjectRoleId===null || item.confirmProjectRoleId===undefined||item.confirmProjectRole===null
          ?'':item.confirmProjectRole.roleName}}
          <span v-if="item.confirmProjectRoleId===null || item.confirmProjectRoleId===undefined"
                style="color: #CDD0D7">请选择审核人角色</span>
        </el-tag>
      </div>
      <div class="right-margin">
        <i class="el-icon-more"
           v-show="!fold"
           @click="cuttle(item,'UNFOLD','ONE')"
           style="cursor: pointer;height: 23px;line-height: 23px;font-size: 15px;"></i>
      </div>

      <div class="right-margin">
        <div v-show="NullValuePrompt"
             style="cursor: pointer;height: 32px;line-height: 32px;font-size: 15px;color: red">*当前里程碑下存在数据缺失！</div>
      </div>
    </div>
  </div>
</template>
<script>

  function childAddRole(item, roleType, itemType) {
    this.$parent.addRole(item, roleType, itemType);
  }

  function childSaveProjectMilestone(item) {
    this.$parent.saveProjectMilestone(item);

  }

  function changEstEndTime(item) {
    if (this.hasOnLine === 0) {
      this.$confirm('这个操作将同步该里程碑下所有任务的截止日期，您想继续吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.$parent.setBatchUdateEstEndTime(item);
      }).catch(() => {
        this.item = JSON.parse(JSON.stringify(this.copy));
      });
    }else {
      this.$parent.saveProjectMilestone(item);
    }

  }

  function changeIsEdit(item) {
    item.isEdit.name = true;
  }

  function copyM(item) {

    this.$parent.copy(item.id, 'PROJECT_MILESTONE');
  }

  function deleteM(item) {
    this.arrowVisible = false;

    this.$parent.deleteProjectMilestone(item);
  }

  function moveM(item, direction) {

    this.arrowVisible = false;

    this.$parent.moveProjectMilestone(item, direction);
  }

  function timeStatus(item) {
    const task = this.dateStatusList.filter((dateStatus => dateStatus.code === item.status))[0];

    return task === undefined ? '#ccc' : task.color;
  }

  function cuttle(item, meth, num) {

    this.$parent.cuttleMilestone(item, meth, num);
  }

  function showMilestoneLog(item) {

    this.arrowVisible = false;

    this.$parent.showMilestoneLog(item);
  }


  /**
   * 不启用或禁用
   */
  function updateToForbiddenOrNotEnabled(data, type) {
    this.$parent.updateToForbiddenOrNotEnabled(data, type);
  }


  export default {
    name: 'ProjectTableHeader',
    props: {
      milestone: Object,
      milestonesWidth: String,
      authority: Boolean,
      first: String,
      end: String,
      dateStatusList: Array,
      fold: Boolean,
      foldAll: Boolean,
      foldupAll: Boolean,
      projectType: String,
      hasOnLine: Number,
      NullValuePrompt: Boolean
    },
    data() {
      return {
        item: this.milestone,
        copy: JSON.parse(JSON.stringify(this.milestone)),
        arrowVisible: false,
      };
    },
    methods: {
      childAddRole,
      childSaveProjectMilestone,
      changEstEndTime,
      changeIsEdit,
      deleteM,
      moveM,
      timeStatus,
      cuttle,
      copyM,
      showMilestoneLog,
      updateToForbiddenOrNotEnabled
    },
    created() {
    },
    watch: {
      milestone(val) {
        this.item = this.milestone;
      },
    },
    computed: {},
    mounted() {

    },
  };

</script>
<style scoped lang="scss">
  .milestone {
    z-index: 989;
    font-size: 18px;
    position: sticky;
    width: 1200px;
    left: 0;
    display: flex;
    height: 32px;
    line-height: 32px;
  }


  .right-margin {
    margin-right: 10px;
  }

  .show_style_label {
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    height: 31px;
    font-size: 15px;
    line-height: 31px;
  }

  .show_style_label:hover {
    background-color: #DBE4EE;
    color: #01408B;
  }

</style>
