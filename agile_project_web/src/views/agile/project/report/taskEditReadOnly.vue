<template>
  <div>
    <el-form ref="ruleForm" :model="form" :rules="rules" label-position="right" label-width="150px" style="width:580px">
      <el-form-item prop="name">
        <span slot="label">任务名称</span>
        <el-input v-model="taskData.name"
                  readonly
                  size="small"
                  style="width: 300px;font-family: 'Microsoft YaHei';font-size: 13px"
                  maxlength="35"></el-input>
      </el-form-item>
      <el-form-item label="状态">
        <div style="width: 300px; text-align: center">

          <el-tooltip placement="top" :disabled="!(taskData.status==='4'||taskData.status==='5')">
            <div slot="content">

              <div
                v-if="taskData.passReason!==''&&taskData.passReason!==null && taskData.passReason!==undefined">
                带条件通过理由：<br/>
                <div v-for="(pr,index) in taskData.passReason.split('||')" :key="index">
                  {{index+1}}、<span>{{pr}}</span><br/>
                </div>
              </div>

              <div
                v-if="taskData.refuseReason!==''&&taskData.refuseReason!==null&& taskData.passReason!==undefined">
                拒绝理由：<br/>
                <div v-for="(rr,index2) in taskData.refuseReason.split('||')" :key="index2">
                  {{index2+1}}、<span>{{rr}}</span><br/>
                </div>
              </div>
            </div>
            <task-status dict-code="TASK_STATUS" v-model="taskData.status"
                         style="height: 32px;line-height: 32px"
                         position="bottom" width="150" :item="taskData" :isShow="true"
                         :hasArrow="true"></task-status>
          </el-tooltip>
        </div>
      </el-form-item>

      <el-form-item label="优先级">
        <div style="width: 300px; text-align: center">
          <task-status dict-code="PRIORITY" v-model="taskData.priority"
                       style="height: 32px;line-height: 32px"
                       position="bottom" width="150" :item="taskData" :isShow="true"
                       :hasArrow="true"></task-status>
        </div>
      </el-form-item>

      <el-form-item label="负责人">
        <el-tag
          type="info"
          size="medium"
          style="width: 200px;text-align: center;cursor: pointer"
          disable-transitions>
          {{taskData.reviewProjectRoleId===null?'-':taskData.reviewProjectRole.roleName}}
        </el-tag>
        <el-tag
          type="success"
          size="medium"
          style="margin-left: 5px;width: 95px;text-align: center;cursor: pointer"
          disable-transitions>{{taskData.reviewUser===null?'-':taskData.reviewUser.realName}}
        </el-tag>
      </el-form-item>
      <el-form-item label="审核人">
        <el-tag
          type="info"
          size="medium"
          style="width: 200px;text-align: center;cursor: pointer"
          disable-transitions>
          {{taskData.confirmProjectRoleId===null?'-':taskData.confirmProjectRole.roleName}}
        </el-tag>
        <el-tag
          type="success"
          size="medium"
          style="margin-left: 5px;width: 95px;text-align: center;cursor: pointer"
          disable-transitions>{{taskData.confirmUser===null?'-':taskData.confirmUser.realName}}
        </el-tag>
      </el-form-item>
      <el-form-item label="截止时间">
        <el-date-picker
          v-model="taskData.estEndTime"
          type="date"
          :clearable="false"
          style="width: 300px;"
          size="small"
          readonly
          align="center"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd HH:mm"
          placeholder="选择截止日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="完成时间">
        <el-date-picker
          v-model="taskData.actEndTime"
          type="date"
          :clearable="false"
          style="width: 300px;"
          size="small"
          readonly
          align="center"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd HH:mm"
          placeholder="尚未完成">
        </el-date-picker>
      </el-form-item>


      <el-form-item v-if="taskData.type.indexOf('3')!==-1">
        <span slot="label">开阀条件</span>
        <el-input v-model="taskData.openConditions"
                  class=""
                  style="width: 300px;cursor: pointer;font-size: 13px"
                  type="textarea"
                  readonly
                  :rows="3"></el-input>
      </el-form-item>
      <el-form-item v-if="taskData.type.indexOf('3')!==-1">
        <span slot="label">开阀描述</span>
        <el-input v-model="taskData.openDescription"
                  type="textarea"
                  readonly
                  :rows="3"
                  style="width: 300px;cursor: pointer;font-size: 13px"></el-input>
      </el-form-item>

      <el-form-item>

        <span slot="label">交付要求</span>
        <div style="cursor: pointer;text-align: center;width: 300px;border: 1px #e1e1e1 dashed;"
             class="o-hover">
          <span v-if="taskData.isRequirement!==1">O</span>
          <i class="el-icon-check" v-if="taskData.isRequirement===1"></i>
        </div>

      </el-form-item>

      <el-form-item v-if="taskData.isRequirement===1||(taskData.isRequirement===0&&taskData.taskDeliveryList.length!==0)">

        <span slot="label">交付物</span>
        <div style="cursor: pointer;text-align: center;width: 300px;border: 1px #e1e1e1 dashed;"
             class="o-hover"
             @click="changeDeliverables(taskData)">
          <span>查看交付物...</span>
        </div>

      </el-form-item>


      <el-form-item>
        <span slot="label">任务类型</span>
        <div>
          <el-tag
            type="info"
            size="medium"
            style="width: 302px;text-align: center;cursor: pointer;"
            v-if="!taskData.isEdit.type"
            disable-transitions>
            {{taskData.typeName}}
          </el-tag>
          <el-select
            style="width: 302px"
            v-model="taskData.type"
            multiple
            size="small"
            placeholder="请选择"
            v-if="taskData.isEdit.type"
          >
            <el-option
              v-for="item in types"
              :key="item.code"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
        </div>
      </el-form-item>

      <el-form-item v-if="'childTask' in taskData">
        <span slot="label">子任务</span>
        <div style="width: 300px;border: 1px #e1e1e1 dashed;">
          <div v-if="taskData.childTask.length===0"
               style="border: 1px #DBDEE5 dashed;color: #DBDEE5;text-align: center;height: 50px;line-height: 50px;font-family: 'Microsoft YaHei';font-size: 13px">
            暂无子任务
          </div>
          <div v-else v-for="(cl,index) in taskData.childTask" :key="cl.id">
            <div class="show_style_label" @click="editChildTask(cl)">
              <div
                style="float: left;max-width: 262px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;margin-left: 8px;font-family: 'Microsoft YaHei';font-size: 13px;">
                {{index+1}}、{{cl.name}}
              </div>

              <div style="float: right;align-content: center;">
                <i class="el-icon-s-promotion" :style="{color:statusFilChild(cl)}"
                   style="font-size: 15px;margin-right: 10px"></i>
              </div>


            </div>

          </div>

        </div>
      </el-form-item>


      <div v-for="taskExtraData in attrs"
           :key="taskExtraData.projectAttrId">

        <div v-if="taskExtraData.attrType === 'INPUT' || taskExtraData.attrType === 'NUMBER' ">
          <el-form-item>
            <span slot="label">{{taskExtraData.name}}</span>
            <el-input v-model="taskExtraData.value"
                      style="width: 300px;font-family: 'Microsoft YaHei';font-size: 13px"
                      size="small"
                      readonly></el-input>
          </el-form-item>
        </div>

        <div v-if="taskExtraData.attrType === 'PERSON'">
          <el-form-item>
            <span slot="label">{{taskExtraData.name}}</span>
            <el-tag
              type="success"
              size="medium"
              style="width: 300px;text-align: center;cursor: pointer"
              disable-transitions>
              {{taskExtraData.value}}
            </el-tag>
          </el-form-item>
        </div>
        <div v-if="taskExtraData.attrType === 'DATE'">
          <el-form-item>
            <span slot="label">{{taskExtraData.name}}</span>
            <el-date-picker
              v-model="taskExtraData.value"
              style="width: 300px"
              size="small"
              type="date"
              format="yyyy-MM-dd"
              readonly
              :clearable="false"
              value-format="yyyy-MM-dd HH:mm"
              placeholder="选择日期">
            </el-date-picker>
          </el-form-item>
        </div>
        <div v-if="taskExtraData.attrType === 'STATUS'">
          <el-form-item>
            <span slot="label">{{taskExtraData.name}}</span>
            <div style="width: 300px; text-align: center">
              <task-status dict-code="TASK_STATUS" v-model="taskExtraData.value"
                           position="bottom" width="150" :item="taskExtraData"
                           style="height: 32px;line-height: 32px"
                           :isShow="true"
                           :hasArrow="true"></task-status>
            </div>
          </el-form-item>
        </div>

        <div v-if="taskExtraData.attrType === 'FILE'">
          <el-form-item>
            <span slot="label">{{taskExtraData.name}}</span>
            <div style="width: 300px; text-align: center;border: 1px #DBDEE5 dashed;">


              <i class="el-icon-upload" style="font-size: 15px"
                 v-if="taskExtraData.attrType === 'FILE'&&(taskExtraData.value === null || taskExtraData.value === '')"></i>

              <el-link type="primary"
                       :href="'/api'+taskExtraData.value.split('|||')[taskExtraData.value.split('|||').length-1]"
                       download=""
                       v-if="taskExtraData.attrType === 'FILE'&&!(taskExtraData.value === null || taskExtraData.value === '')">
                {{
                taskExtraData.value !== null && taskExtraData.value !== '' ?
                taskExtraData.value.split('|||')[0] : ''
                }}
              </el-link>


            </div>
          </el-form-item>
        </div>

      </div>

    </el-form>
    <div style="text-align: center;margin-bottom: 10px">
      <el-button type="text" @click="openDrawer('taskBlog')">日志</el-button>
      <span style="margin: 0 8px 0px 8px">|</span>
      <el-button type="text" @click="openDrawer('taskChat')">评论</el-button>
    </div>

    <el-drawer
      title=""
      :visible.sync="dialogVisible.taskDrawer"
      append-to-body
      :with-header="false"
      size="50%"
    >
      <task-drawer :task="taskData" :active="activeName" :refresh="dialogVisible.taskDrawer"></task-drawer>
    </el-drawer>

    <!--      选择交付物相关-->
    <el-dialog
      :title="taskDeliveryTitle"

      :visible.sync="dialogVisible.delivery"
      width="1048px"
      center
      append-to-body>

      <task-delivery-only-read :taskInfo="taskData"></task-delivery-only-read>
    </el-dialog>

    <el-dialog
      @close="closeChildTask"
      :visible.sync="taskChildDialogVisible"
      width="580px"
      top="28vh"
      center
      append-to-body>
      <div slot="title" class="header-title">
        <span>{{taskChild.name}}</span>
      </div>
      <task-edit-read-only :task="taskChild"
                           :taskAttr="taskAttr"
                           :rolePlus="rolePlus"></task-edit-read-only>
    </el-dialog>


  </div>
</template>
<script>
  import TaskStatus from '@/components/TaskStatus';
  import {_listTaskDeliveriesByTaskId} from '@/api/taskDeliveryApi';
  import TaskDrawer from "../task/table/TaskDrawer";
  import taskDeliveryOnlyRead from "./taskDeliveryOnlyRead";


  function editChildTask(item) {
    this.taskChild = item
    this.currentChildTaskId = item.id
    this.taskChildDialogVisible = true
  }

  function closeChildTask() {
    this.currentChildTaskId = ''
    this.taskChildDialogVisible = false;

  }

  function changeDeliverables(info) {
    this.taskDeliveryTitle = info.name;
    this.dialogVisible.delivery = true;
  }

  function openDrawer(item) {

    this.activeName = item;
    this.dialogVisible.taskDrawer = true;
  }

  function statusFilChild(item) {
    const task = this.statusList.filter((status => status.code === item.status))[0];

    return task === undefined ? '#ccc' : task.color;
  }

  export default {
    name: 'TaskEditReadOnly',
    props: {
      task: {},
      taskAttr: Array,
      rolePlus: Array,
      need: Boolean
    },
    components: {
      TaskDrawer,
      taskDeliveryOnlyRead,
      TaskStatus,

    },
    data() {
      return {
        taskData: {},
        attrs: [],
        form: {},
        rules: {},
        // 传递
        taskD: {},
        attrId: '',
        fileId: '',
        types: [],

        userId: '',
        statusList: [],


        taskDeliveryTitle: '',
        changePower: false,

        passVisible: false,
        activeName: '',
        dialogVisible: {
          taskDrawer: false,
          delivery: false,
        },

        taskChild: '',
        taskChildDialogVisible: false,
        currentChildTaskId: ''

      };

    },
    methods: {
      editChildTask,
      openDrawer,
      changeDeliverables,
      statusFilChild,
      closeChildTask

    },
    created() {
      this.userId = localStorage.getItem('id');


      const dictDataType = this.$store.getters.getDictionaryByKey('TASK_TYPE');
      this.types = dictDataType.sysDictDataList;

      const dictData = this.$store.getters.getDictionaryByKey('TASK_STATUS');
      this.statusList = dictData.sysDictDataList;

      this.taskData = JSON.parse(JSON.stringify(this.task));
      let attrs = this.taskData.taskDataList.map(x => {
        this.$set(x, 'name', '');


        x.name = this.taskAttr.filter(task => task.projectAttrId !== null && x.projectAttrId !== null && task.projectAttrId === x.projectAttrId)[0].label;
        return x;
      });
      this.attrs = attrs
    },
    watch: {
      task(val) {
        this.taskData = JSON.parse(JSON.stringify(this.task));

        let attrs = this.taskData.taskDataList.map(x => {
          this.$set(x, 'name', '');


          x.name = this.taskAttr.filter(task => task.projectAttrId !== null && x.projectAttrId !== null && task.projectAttrId === x.projectAttrId)[0].label;
          return x;
        });
        this.attrs = attrs

      },
      async changePower(val) {

        const result = await _listTaskDeliveriesByTaskId({taskId: this.taskData.id});
        this.taskData.taskDeliveryList = result.data.taskDeliveryList;


      }
    },
  };
</script>
<style scoped lang="scss">

  .o-hover:hover {
    background-color: #DBE4EE;
    color: #01408B;
  }

  .show_style_label {
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    height: 26px;
    font-size: 13px;
    line-height: 26px;
  }

  .show_style_label:hover {
    background-color: #DBE4EE;
    color: #01408B;
  }
</style>
