<template>
  <div>
    <el-form ref="ruleForm" :model="form" :rules="rules" label-position="right" label-width="150px" style="width:580px">
      <el-form-item prop="name">
        <span slot="label">任务名称</span>
        <el-input v-model="taskData.name"
                  :readonly="!authority"
                  size="small"
                  style="width: 300px;font-family: 'Microsoft YaHei';font-size: 13px"
                  maxlength="35"
                  @blur="changeTaskName"></el-input>
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
                         :hasArrow="true" @update="chooseStatus"></task-status>
          </el-tooltip>
          <!--          <task-status dict-code="TASK_STATUS" v-model="taskData.status"-->
          <!--                       position="bottom" width="150" :item="taskData" :isShow="true"-->
          <!--                       :hasArrow="true" @update="chooseStatus"></task-status>-->
        </div>
      </el-form-item>

      <el-form-item label="优先级">
        <div style="width: 300px; text-align: center">
          <task-status dict-code="PRIORITY" v-model="taskData.priority"
                       style="height: 32px;line-height: 32px"
                       position="bottom" width="150" :item="taskData" :isShow="!authority"
                       :hasArrow="true" @update="choosePriority"></task-status>
        </div>
      </el-form-item>

      <el-form-item label="负责人">
        <el-tag
          type="info"
          size="medium"
          style="width: 200px;text-align: center;cursor: pointer"
          disable-transitions
          @click="authority&&changeRole(1)">
          {{taskData.reviewProjectRoleId===null?'-':taskData.reviewProjectRole.roleName}}
        </el-tag>
        <el-tag
          type="success"
          size="medium"
          style="margin-left: 5px;width: 95px;text-align: center;cursor: pointer"
          disable-transitions
          @click="authority&&bindRoleUser(1)">{{taskData.reviewUser===null?'-':taskData.reviewUser.realName}}
        </el-tag>
      </el-form-item>
      <el-form-item label="审核人">
        <el-tag
          type="info"
          size="medium"
          style="width: 200px;text-align: center;cursor: pointer"
          disable-transitions
          @click="authority&&changeRole(2)">
          {{taskData.confirmProjectRoleId===null?'-':taskData.confirmProjectRole.roleName}}
        </el-tag>
        <el-tag
          type="success"
          size="medium"
          style="margin-left: 5px;width: 95px;text-align: center;cursor: pointer"
          disable-transitions
          @click="authority&&bindRoleUser(2)">{{taskData.confirmUser===null?'-':taskData.confirmUser.realName}}
        </el-tag>
      </el-form-item>
      <el-form-item label="截止时间">
        <el-date-picker
          v-model="taskData.estEndTime"
          type="date"
          :clearable="false"
          style="width: 300px;"
          size="small"
          :readonly="!authority"
          align="center"
          @change="updateTaskById(taskData)"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd HH:mm"
          placeholder="选择截止日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="完成时间">

        <!--        <div-->
        <!--          style="background-color:#F2F4F5;color: #626365;text-align: center;width: 300px;height: 30px;line-height: 30px">-->
        <!--          {{taskData.actEndTime}}-->
        <!--        </div>-->
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
                  :readonly="!authority"
                  :rows="3"
                  @blur="authority&&updateTaskById(taskData)"></el-input>
      </el-form-item>
      <el-form-item v-if="taskData.type.indexOf('3')!==-1">
        <span slot="label">开阀描述</span>
        <el-input v-model="taskData.openDescription"
                  type="textarea"
                  :readonly="!authority"
                  :rows="3"
                  style="width: 300px;cursor: pointer;font-size: 13px"
                  @blur="authority&&updateTaskById(taskData)"></el-input>
      </el-form-item>

      <el-form-item>

        <span slot="label">交付要求</span>
        <div style="cursor: pointer;text-align: center;width: 300px;border: 1px #e1e1e1 dashed;"
             class="o-hover"
             @click="authority&&changeRequirement(taskData,taskData.isRequirement)">
          <span v-if="taskData.isRequirement!==1">O</span>
          <i class="el-icon-check" v-if="taskData.isRequirement===1"></i>
        </div>

      </el-form-item>

      <el-form-item>

        <span slot="label">交付物</span>
        <div style="cursor: pointer;text-align: center;width: 300px;border: 1px #e1e1e1 dashed;"
             class="o-hover"
             @click="changeDeliverables(taskData,true)">
          <span>提交/审核交付物...</span>
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
            @click="authority&&changeIsEditType(taskData)"
            disable-transitions>
            {{taskData.typeName}}
          </el-tag>
          <el-select
            style="width: 302px"
            v-model="taskData.type"
            multiple
            size="small"
            placeholder="请选择"
            @remove-tag="updateTaskById(taskData)"
            v-if="taskData.isEdit.type"
            @visible-change="visChange($event,taskData)"
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

<!--      <el-form-item v-if="'childTask' in taskData">-->
<!--        <span slot="label">子任务</span>-->
<!--        <div style="width: 300px;border: 1px #e1e1e1 dashed;">-->
<!--          <div v-if="taskData.childTask.length===0"-->
<!--               style="border: 1px #DBDEE5 dashed;color: #DBDEE5;text-align: center;height: 50px;line-height: 50px;font-family: 'Microsoft YaHei';font-size: 13px">-->
<!--            暂无子任务-->
<!--          </div>-->
<!--          <div v-else v-for="(cl,index) in taskData.childTask" :key="cl.id">-->
<!--            <div class="show_style_label" @click="editChildTask(cl)">-->
<!--              <div style="float: left;max-width: 262px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;">-->
<!--                <span-->
<!--                  style="margin-left: 8px;font-family: 'Microsoft YaHei';font-size: 13px;">{{index+1}}、{{cl.name}}</span>-->
<!--              </div>-->

<!--              <div style="float: right;align-content: center;">-->
<!--                <i class="el-icon-s-promotion" :style="{color:statusFilChild(cl)}"-->
<!--                   style="font-size: 15px;margin-right: 10px"></i>-->
<!--              </div>-->


<!--            </div>-->

<!--          </div>-->

<!--        </div>-->
<!--      </el-form-item>-->


      <div v-for="taskExtraData in attrs"
           :key="taskExtraData.projectAttrId">

        <div v-if="taskExtraData.attrType === 'INPUT' || taskExtraData.attrType === 'NUMBER' ">
          <el-form-item>
            <span slot="label">{{taskExtraData.name}}</span>
            <el-input v-model="taskExtraData.value"
                      style="width: 300px;font-family: 'Microsoft YaHei';font-size: 13px"
                      size="small"
                      :readonly="!(reBeginAuthority||userId===(taskData.reviewUser===null?taskData.reviewUser:taskData.reviewUser.id)||userId===(taskData.confirmUser===null?taskData.confirmUser:taskData.confirmUser.id))"
                      @blur="changeTextName(taskExtraData,taskData)"></el-input>
          </el-form-item>
        </div>

        <div v-if="taskExtraData.attrType === 'PERSON'">
          <el-form-item>
            <span slot="label">{{taskExtraData.name}}</span>
            <el-tag
              type="success"
              size="medium"
              style="width: 300px;text-align: center;cursor: pointer"
              @click="changePerson(taskExtraData,taskData)"
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
              type="date"
              size="small"
              format="yyyy-MM-dd"
              :readonly="!(reBeginAuthority||userId===(taskData.reviewUser===null?taskData.reviewUser:taskData.reviewUser.id)||userId===(taskData.confirmUser===null?taskData.confirmUser:taskData.confirmUser.id))"
              :clearable="false"
              value-format="yyyy-MM-dd HH:mm"
              @change="changeDate(taskExtraData,taskData)"
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
                           :isShow="!(reBeginAuthority||userId===(taskData.reviewUser===null?taskData.reviewUser:taskData.reviewUser.id)||userId===(taskData.confirmUser===null?taskData.confirmUser:taskData.confirmUser.id))"
                           :hasArrow="true" @update="changeStatusData"></task-status>
            </div>
          </el-form-item>
        </div>

        <div v-if="taskExtraData.attrType === 'FILE'">
          <el-form-item>
            <span slot="label">{{taskExtraData.name}}</span>
            <div style="width: 300px; text-align: center;border: 1px #DBDEE5 dashed;">


              <el-upload
                v-if="taskExtraData.attrType === 'FILE'&&(taskExtraData.value === null || taskExtraData.value === '')"
                :headers="{
                            token: token
                          }"
                action="/api/upload"
                :show-file-list="false"
                :on-success="uploadSuccess">
                <i class="el-icon-upload" style="font-size: 15px"
                   v-if="reBeginAuthority||userId===(taskData.reviewUser===null?taskData.reviewUser:taskData.reviewUser.id)||userId===(taskData.confirmUser===null?taskData.confirmUser:taskData.confirmUser.id)"
                   @click="clickUpload(taskExtraData)">
                </i>
              </el-upload>

              <!--              <a :href="'/api'+taskExtraData.value.split('|||')[taskExtraData.value.split('|||').length-1]"-->
              <!--                 download=""-->
              <!--                 v-if="taskExtraData.attrType === 'FILE'&&!(taskExtraData.value === null || taskExtraData.value === '')">-->
              <!--                {{-->
              <!--                taskExtraData.value !== null && taskExtraData.value !== '' ?-->
              <!--                taskExtraData.value.split('|||')[0] : ''-->
              <!--                }}-->
              <!--              </a>-->
              <el-link type="primary"
                       :href="'/api'+taskExtraData.value.split('|||')[taskExtraData.value.split('|||').length-1]"
                       download=""
                       v-if="taskExtraData.attrType === 'FILE'&&!(taskExtraData.value === null || taskExtraData.value === '')">
                {{
                taskExtraData.value !== null && taskExtraData.value !== '' ?
                taskExtraData.value.split('|||')[0] : ''
                }}
              </el-link>
              <i class="el-icon-circle-close"
                 style="margin-left: 5px;cursor: pointer"
                 @click="deleteFileAdd(taskExtraData)"
                 v-if="taskExtraData.attrType === 'FILE'&&!(taskExtraData.value === null || taskExtraData.value === '')&&(reBeginAuthority||userId===(taskData.reviewUser===null?taskData.reviewUser:taskData.reviewUser.id)||userId===(taskData.confirmUser===null?taskData.confirmUser:taskData.confirmUser.id))"></i>


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

    <span slot="footer" class="dialog-footer">

      <el-row class="el-dialog--center">
        <el-button @click="taskSpeed(taskData)"
                   style="margin-right: 5px"
                   type="primary" plain
                   v-if="reBeginAuthority&&taskData.status!=='6'"
        >任务催办</el-button>

        <el-button @click="chooseStatus('2',taskData,'POWER')"
                   type="primary" plain
                   v-hasProOperation="'{\'taskReviewerId\':\''
                   + (taskData.reviewUser===null?taskData.reviewUser:taskData.reviewUser.id)
                   +'\', \'key\': \'hasBeginTask\',\'taskStatus\':\'' + taskData.status +'\'}'">开始任务</el-button>

        <el-button @click="pushReview(taskData)"
                   type="primary" plain
                   v-hasProOperation="'{\'taskReviewerId\':\''
                    + (taskData.reviewUser===null?taskData.reviewUser:taskData.reviewUser.id)
                    +'\', \'key\': \'hasPushReviewTask\',\'taskStatus\':\'' + taskData.status +'\'}'">提交审核</el-button>


        <el-popover
          placement="right"
          width="150"
          v-model="passVisible"
          trigger="click">
                  <div>
                    <div
                      v-if="taskData.taskDeliveryList.filter(i=>i.auditStatus===0||(i.auditStatus===2&&i.isReupload!==1)).length>0">
                      还有交付物需要审核哦

                    </div>
                    <div v-else>

                      <div class="show_style_label"
                           style="padding-left: 6px"
                           @click="reasonTask(taskData,'CONDITIONAL')">
                        带条件通过
                      </div>
                      <div class="show_style_label"
                           style="padding-left: 6px"
                           @click="chooseStatus('6',taskData,'POWER')">
                        通过
                      </div>
                      <div class="show_style_label"
                           style="padding-left: 6px"
                           @click="reasonTask(taskData,'REFUSE')">
                        拒绝
                      </div>
                    </div>
                  </div>


          <el-button type="primary" plain slot="reference"
                     v-hasProOperation="'{\'taskConfirmerId\':\''
                     + (taskData.confirmUser===null?taskData.confirmUser:taskData.confirmUser.id)
                     +'\', \'key\': \'hasConfirmTask\',\'taskStatus\':\'' + taskData.status +'\'}'">审核完成</el-button>

                </el-popover>

        <el-button type="primary" plain
                   @click="restart('2',taskData)"
                   v-hasProOperation="'{\'key\': \'hasReBeginTask\',\'reBeginAuthority\':\'' + reBeginAuthority
                   +'\',\'taskStatus\':\'' + taskData.status +'\'}'">重新开始任务</el-button>
      </el-row>

  </span>
    <!--      更换角色 -->
    <el-dialog
      title="更换角色"
      @close="closeRole"
      :visible.sync="roleDialogVisible"
      width="600px"
      append-to-body>
      <role-transfer
        :rolesProp="roles"
        :isSingle="true"
        :rolePlus="rolePlus"
        @getRoles="getRoles"
        @getCancel="closeRole">
      </role-transfer>
    </el-dialog>
    <!--      选择人员并绑定角色-->
    <el-dialog
      title="更改人选"
      @close="closeOwner"
      :visible.sync="ownerDialogVisible"
      width="1200px"
      append-to-body>
      <user-tree
        :isSingle='true'
        :usersProp='owners'
        @getUsers="getOwnersId"
        @getCancel="closeOwner">
      </user-tree>
    </el-dialog>
    <!--    新增的人员-->
    <el-dialog
      title="添加成员"
      @close="closeOwner2"
      :visible.sync="ownerDialogVisible2"
      width="1200px"
      append-to-body>
      <user-tree
        :isSingle='true'
        :usersProp='owners'
        @getUsers="getOwnersId2"
        @getCancel="closeOwner2">
      </user-tree>
    </el-dialog>


    <!--    带条件通过和拒绝 填写理由-->
    <el-dialog
      :title="reasonTitle"
      @close="dialogVisible.reason=false"
      :visible.sync="dialogVisible.reason"
      width="600px"
      center
      append-to-body>

      <el-input
        type="textarea"
        :rows="5"
        placeholder="使用Enter保存"
        v-model="reasonValue"
        @keydown.native="changeReason($event)"></el-input>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible.reason=false">取 消</el-button>
        <el-button type="primary" @click="changeReason($event,'BUTTON')">确 定</el-button>
      </span>

    </el-dialog>

    <!--      选择交付物相关-->
    <el-dialog
      :title="taskDeliveryTitle"
      @close="closeDelivery"
      :visible.sync="dialogVisible.delivery"
      width="1048px"
      center
      append-to-body>
      <task-delivery :taskInfo="taskData" @refresh="refreshDelivery"></task-delivery>
    </el-dialog>

    <el-drawer
      title=""
      :visible.sync="dialogVisible.taskDrawer"
      append-to-body
      :with-header="false"
      size="50%"
    >
      <task-drawer :task="taskData" :active="activeName" :refresh="dialogVisible.taskDrawer"></task-drawer>
    </el-drawer>

  </div>
</template>
<script>
  import {_updateTaskById, _saveTaskData, _listByTaskId} from '@/api/taskApi';
  import {_saveOrUpdateRoleUser, _listProjectRoleUser} from '@/api/projectRoleUserApi';
  import RoleTransfer from '@/components/RoleTransfer.vue';
  import UserTree from '@/components/UserTree.vue';
  import TaskStatus from '@/components/TaskStatus';
  import {_taskObj} from '@/utils/taskUtils'
  import moment from "moment";
  import {_taskManager} from '@/api/messageApi';
  import TaskDelivery from "../table/TaskDelivery";
  import {_listTaskDeliveriesByTaskId} from '@/api/taskDeliveryApi';
  import TaskDrawer from "../table/TaskDrawer";
  import {_getProjectById} from '@/api/projectApi';


  /**
   * 总方法
   * @param
   */
  async function updateTaskById(task) {

    const result1 = _taskObj(task, this.gaspRefuse, this.gaspPass);


    const obj = result1;

    const result = await _updateTaskById(obj);

    if (result.code === 1200) {
      this.$emit('update')
    } else if (result.code === 4227 || result.code === 4226 || result.code === 4222 || result.code === 4229) {
      // 4227, "子任务未完成（未开始、待审核、拒绝等），主任务不能通过！"
      // 4226, "子任务截止时间不能大于父任务截止时间！"
      // 4222, "父任务截止时间不能小于子任务截止时间！"
      // 4229, "子任务带条件通过时，主任务不能完全通过，可以带条件通过或拒绝！"
      this.taskData = JSON.parse(JSON.stringify(this.task));
      let attrs = this.taskData.taskDataList.map(x => {
        this.$set(x, 'name', '');
        x.name = this.taskAttr.filter(task => task.projectAttrId !== null && x.projectAttrId !== null && task.projectAttrId === x.projectAttrId)[0].label;
        return x;
      });
      // this.attrs = attrs.filter(attr => attr.value !== '' && attr.value !== null)
      this.attrs = attrs
    }
  }

  async function refreshDelivery(item, task, copyDelivery) {

    if (item === 'REFUSE') {
      this.dialogVisible.delivery = false;
      this.taskData.refuseReason = '交付物被拒绝，请去交付物列表查看拒绝原因';
      this.taskData.status = '4';
      this.gaspRefuse = true;
      await this.updateTaskById(this.taskData);
      this.gaspRefuse = false;
      this.$emit('save')

      // this.chooseStatus('2', item, 'POWER')
    }
    if (copyDelivery !== undefined) {
      this.taskData.taskDeliveryList = JSON.parse(JSON.stringify(copyDelivery))
    }

    if (this.need === undefined) {
      this.$emit('re')
    }


  }

  /**
   * 状态相关
   * @param item
   */
  function chooseStatus(val, a, kack) {
    if (val === '6') {
      this.passVisible = false
      const tta = JSON.parse(JSON.stringify(this.taskData));
      tta.status = val;
      this.updateTaskById(tta);
    } else {
      a.status = val;
      this.updateTaskById(a);
    }
    if (kack === 'POWER') {
      this.$emit('save')
    }

  }


  // 重新开始任务 需要再传一个标记
  function restart(val, a) {
    this.passVisible = false
    const tta = JSON.parse(JSON.stringify(a));
    tta.status = val;
    tta.restartTaskFlag = true;
    this.updateTaskById(tta);
    this.$emit('save')
  }

  function choosePriority(val, a) {
    this.taskData.priority = val;
    this.updateTaskById(this.taskData);
  }

  /**
   * 选择相关角色
   * @param judge
   * @returns {Promise<void>}
   */
  async function changeRole(judge) {
    this.roles = [];
    if (judge === 1) {
      this.roles = this.taskData.reviewProjectRoleId === null ? [] : this.taskData.reviewProjectRole.roleName.split(',');
      this.flag = true;
    }
    if (judge === 2) {
      this.roles = this.taskData.confirmProjectRoleId === null ? [] : this.taskData.confirmProjectRole.roleName.split(',');
      this.flag = false;
    }
    this.roleDialogVisible = true;
  }

  async function getRoles(data) {

    if (this.flag) {
      const result = await _updateTaskById({
        id: this.taskData.id,
        projectId: this.taskData.projectId,
        reviewRoleName: data[0],
        confirmRoleName: this.taskData.confirmProjectRoleId === null ? [] : this.taskData.confirmProjectRole.roleName,
      });
      if (result.code === 1200) {
        this.$emit('update');
      }
    } else {
      const result = await _updateTaskById({
        id: this.taskData.id,
        projectId: this.taskData.projectId,
        reviewRoleName: this.taskData.reviewProjectRoleId === null ? [] : this.taskData.reviewProjectRole.roleName,
        confirmRoleName: data[0],
      });
      if (result.code === 1200) {
        this.$emit('update');
      }

    }
  }

  function closeRole() {
    this.roleDialogVisible = false;
  }

  /**
   * 绑定人员相关
   * @param judge
   * @returns {Promise<void>}
   */
  async function bindRoleUser(judge) {
    this.owners = [];

    if (judge === 1) {
      if (this.taskData.reviewProjectRoleId === null) {
        this.$message({
          message: '请先选择项目角色哦',
          type: 'warning',
          center: 'true',
        });
      } else if (this.taskData.reviewProjectRole !== null && (this.taskData.reviewProjectRole.roleName === '圈长' || this.taskData.reviewProjectRole.roleName === '项目经理')) {
        this.$message({
          message: '圈长/项目经理不可修改',
          type: 'warning',
          center: 'true',
        });
      } else {
        this.flag = true;
        if (this.taskData.reviewUser !== null) {
          this.owners.push(this.taskData.reviewUser);
        }
        this.ownerDialogVisible = true;
      }
    } else {
      if (this.taskData.confirmProjectRoleId === null) {
        this.$message({
          message: '请先选择项目角色哦',
          type: 'warning',
          center: 'true',
        });
      } else if (this.taskData.confirmProjectRole !== null && (this.taskData.confirmProjectRole.roleName === '圈长' || this.taskData.confirmProjectRole.roleName === '项目经理')) {
        this.$message({
          message: '圈长/项目经理不可修改',
          type: 'warning',
          center: 'true',
        });
      } else {
        this.flag = false;
        if (this.taskData.confirmUser !== null) {
          this.owners.push(this.taskData.confirmUser);
        }
        this.ownerDialogVisible = true;
      }
    }
  }

  async function getOwnersId(data) {
    if (data !== null && data.length > 0) {
      const result = await _saveOrUpdateRoleUser({
        projectRoleId: this.taskData.reviewProjectRoleId,
        userId: data[0].id,
      });
      if (result.code === 1200) {
        this.$message({
          message: '绑定成功',
          type: 'success',
          center: true,
        });
        this.$emit('update')
      }
    } else {
      const result = await _saveOrUpdateRoleUser({
        projectRoleId: this.taskData.reviewProjectRoleId,
        userId: null,
      });
      if (result.code === 1200) {
        this.$message({
          message: '解绑成功',
          type: 'success',
          center: true,
        });
        this.$emit('update')
      }
    }
  }

  function closeOwner() {
    this.owners = [];
    this.ownerDialogVisible = false;
  }

  async function changeTaskName() {
    if (this.authority) {
      if (trim(this.taskData.name) === '') {
        this.$message({
          message: '名称不能为空',
          type: 'warning',
          center: 'true',
        });
      } else {
        await this.updateTaskById(this.taskData)
      }
    }

  }

  function trim(s) {
    return s.replace(/(^\s*)|(\s*$)/g, "");
  }


  function reasonTask(task, judge) {
    this.dialogVisible.reason = true;
    this.reasonStatus = '';
    this.reasonTaskObject = {};
    this.reasonValue = '';
    if (judge === 'REFUSE') {
      this.reasonTitle = '请填写拒绝理由';
      this.reasonStatus = '4';

    }
    if (judge === 'CONDITIONAL') {
      this.reasonTitle = '请填写带条件通过理由';
      this.reasonStatus = '5';
    }

    this.reasonTaskObject = this.taskData;
  }


  async function changeReason(event, button) {

    const code = event.keyCode || event.which
    if ((!event.ctrlKey && code === 13) || (button !== undefined && button === 'BUTTON')) {
      if (trim(this.reasonValue) === '') {
        event.preventDefault();
        this.$message({
          message: '写点什么吧',
          type: 'warning',
          center: 'true',
        });
        return false
      }
      this.dialogVisible.reason = false;
      if (this.reasonStatus === '4') {
        this.reasonTaskObject.refuseReason = trim(this.reasonValue);
        this.reasonTaskObject.status = this.reasonStatus;
        this.gaspRefuse = true;
        await this.updateTaskById(this.reasonTaskObject);
        this.gaspRefuse = false;
        this.$emit('save')
      } else {
        this.reasonTaskObject.passReason = trim(this.reasonValue);
        this.reasonTaskObject.status = this.reasonStatus;
        this.gaspPass = true;
        await this.updateTaskById(this.reasonTaskObject);
        this.gaspPass = false;
        this.$emit('save')
      }
    }


  }

  /**
   * 新增属性相关
   */
  async function changeTextName(taskExtraData, row) {
    const taskReviewerId = row.reviewUser === null ? row.reviewUser : row.reviewUser.id;

    const taskConfirmerId = row.confirmUser === null ? row.confirmUser : row.confirmUser.id;

    if (this.reBeginAuthority || this.userId === taskConfirmerId || this.userId === taskReviewerId) {
      await _saveTaskData({
        taskId: this.taskData.id,
        projectAttrId: taskExtraData.projectAttrId,
        value: taskExtraData.value,
      });
    }

  }

  function closeOwner2() {
    this.owners = [];
    this.ownerDialogVisible2 = false;
  }

  async function getOwnersId2(data) {
    this.attrs.filter((i) => i.id === this.taskD.id)[0].value = data[0].realName;
    const result = await _saveTaskData({
      taskId: this.taskD.taskId,
      projectAttrId: this.taskD.projectAttrId,
      value: data[0].realName,
    });
    if (result.code === 1200) {
      this.ownerDialogVisible2 = false;
    }
  }

  function changePerson(taskExtraData, row) {

    const taskReviewerId = row.reviewUser === null ? row.reviewUser : row.reviewUser.id;

    const taskConfirmerId = row.confirmUser === null ? row.confirmUser : row.confirmUser.id;

    if (this.reBeginAuthority || this.userId === taskConfirmerId || this.userId === taskReviewerId) {
      this.taskD = taskExtraData;
      this.ownerDialogVisible2 = true;
    }


  }

  async function changeDate(taskExtraData, row) {
    const taskReviewerId = row.reviewUser === null ? row.reviewUser : row.reviewUser.id;

    const taskConfirmerId = row.confirmUser === null ? row.confirmUser : row.confirmUser.id;

    if (this.reBeginAuthority || this.userId === taskConfirmerId || this.userId === taskReviewerId) {
      await _saveTaskData({
        taskId: this.taskData.id,
        projectAttrId: taskExtraData.projectAttrId,
        value: taskExtraData.value,
      });
    }

  }

  async function changeStatusData(val, a) {
    await _saveTaskData({
      taskId: this.taskData.id,
      projectAttrId: a.projectAttrId,
      value: val,
    });
  }

  async function uploadSuccess(res, file) {
    const val = file.name + '|||' + res.data.path
    this.attrs.filter((i) => i.id === this.fileId)[0].value = val;
    await _saveTaskData({
      taskId: this.taskData.id,
      projectAttrId: this.attrId,
      value: val,
    });
  }

  function clickUpload(taskExtraData) {
    this.fileId = taskExtraData.id;
    this.attrId = taskExtraData.projectAttrId;
  }

  async function deleteFileAdd(_taskData) {
    this.attrs.filter((i) => i.id === _taskData.id)[0].value = '';
    const obj = {
      taskId: this.taskData.id,
      projectAttrId: _taskData.projectAttrId,
      value: '',
    };
    await _saveTaskData(obj);

  }

  async function changeRequirement(item, val) {

    var v;
    if (val !== 1) {
      v = 1;
    } else {
      v = 0;
    }
    this.taskData.isRequirement = v;

    await this.updateTaskById(this.taskData);
  }


  async function visChange(callback, task) {
    if (!callback) {
      await this.updateTaskById(task);
    }
  }

  function changeIsEditType(item) {
    item.isEdit.type = true;
  }


  function pushReview(task) {
    if (task.isRequirement === 1 || (task.isRequirement === 0 && task.taskDeliveryList.filter(i => i.auditStatus !== 1).length > 0)) {
      if (((task.status === '2' || task.status === '4') && task.taskDeliveryList.filter(i => i.auditStatus === 0).length > 0 && task.taskDeliveryList.filter(i => i.auditStatus === 2 && (i.reuploadList === null || i.reuploadList === '')).length === 0)
        || (task.status === '5' && task.taskDeliveryList.filter(i => i.auditStatus === 0).length !== 0)) {
        const taskCopy = JSON.parse(JSON.stringify(task))
        const arr = []
        taskCopy.taskDeliveryList.filter(td => td.auditStatus === 2 && td.reuploadList !== null && td.reuploadList !== '').forEach(list => arr.push(list.id))
        taskCopy.reuploadListIds = arr.join(',');
        this.chooseStatus('3', taskCopy, 'POWER')
      } else {

        let title = ''
        if ((task.status === '2' || task.status === '5' || task.status === '4') && task.taskDeliveryList.filter(i => i.auditStatus === 0).length === 0) {
          title = '有交付要求哦，请提交交付物'
        }
        if (task.status === '4' && task.taskDeliveryList.filter(i => i.auditStatus === 2 && (i.reuploadList === null || i.reuploadList === '')).length > 0) {
          title = '有已拒绝交付物，需点击 重新上传 上传交付物！'
        }
        this.$alert(title, {
          center: true,
          showClose: false,
          type: "info",
          closeOnClickModal: true,
          showConfirmButton: false
        });
      }
    } else {
      this.chooseStatus('3', task, 'POWER')
    }
  }


  function changeDeliverables(info, bool) {
    if (bool) {
      this.taskDeliveryTitle = info.name;
      this.dialogVisible.delivery = true;

    }
  }

  function closeDelivery() {

    this.changePower = !this.changePower;

    this.dialogVisible.delivery = false;
  }

  function openDrawer(item) {

    this.activeName = item;
    this.dialogVisible.taskDrawer = true;
  }

  // 任务催办
  async function taskSpeed(row) {

    let receiveUserIds = ''
    if (row.status === '1' || row.status === '2' || row.status === '4' || row.status === '5') {
      if (row.reviewUser === null) {
        this.$alert('请先指派负责人', '任务催办失败', {
          center: true,
          showClose: false,
          type: "info",
          closeOnClickModal: true,
          showConfirmButton: false
        })
        return
      } else {
        receiveUserIds = row.reviewUser.id
      }
    } else {
      if (row.confirmUser === null) {
        this.$alert('请先指派审核人', '任务催办失败', {
          center: true,
          showClose: false,
          type: "info",
          closeOnClickModal: true,
          showConfirmButton: false
        })
        return
      } else {
        receiveUserIds = row.confirmUser.id
      }
    }
    const resultPro = await _getProjectById(row.projectId);
    let projectName = ''
    if (resultPro.code === 1200) {
      projectName = resultPro.data.project.name;
    }
    // localStorage.getItem('userName') + '催促您加快在项目\"' + this.project.projectName + '\"中\"' + row.name + '\"任务的进度'
    const result = await _taskManager({
      type: 'TASK',
      relationId: row.id,
      receiveUserIds: receiveUserIds,
      projectId: row.projectId,
      content: '您的\"' + projectName + '\"项目-\"' + row.name + '\"任务还未完成，' + localStorage.getItem('userName') + '催促您请及时完成!'
    })
    if (result.code === 1200) {
      this.$message.success('已发送催办邮件')
    }

  }

  function statusFilChild(item) {
    const task = this.statusList.filter((status => status.code === item.status))[0];

    return task === undefined ? '#ccc' : task.color;
  }


  export default {
    name: 'TaskEditChild',
    props: {
      task: {},
      taskAttr: Array,
      rolePlus: Array,
      authority: Boolean,
      reBeginAuthority: Boolean,
      need: Boolean
    },
    components: {
      TaskDrawer,
      RoleTransfer,
      UserTree,
      TaskStatus,
      TaskDelivery,
    },
    data() {
      return {
        token: localStorage.getItem('token'),
        taskData: {},
        attrs: [],
        form: {},
        rules: {},
        // 选择角色dialog
        roles: [],
        roleDialogVisible: false,
        flag: true,
        // 绑定角色相关
        ownerDialogVisible: false,
        owners: [],
        // 新增的人员dialog
        ownerDialogVisible2: false,
        // 传递
        taskD: {},
        attrId: '',
        fileId: '',
        types: [],
        statusList: [],

        userId: '',
        gaspPass: false,
        gaspRefuse: false,

        reasonTitle: '',
        reasonStatus: '',
        reasonTaskObject: {},
        reasonValue: '',
        dialogVisible: {
          reason: false,
          delivery: false,
          taskDrawer: false
        },
        taskDeliveryTitle: '',
        changePower: false,
        passVisible: false,

        activeName: '',

      };

    },
    methods: {
      chooseStatus,
      choosePriority,
      updateTaskById,
      changeRole,
      closeRole,
      getRoles,
      closeOwner,
      closeDelivery,
      bindRoleUser,
      getOwnersId,
      changeTaskName,
      changeTextName,
      changeDeliverables,
      closeOwner2,
      getOwnersId2,
      changePerson,
      changeDate,
      changeStatusData,
      uploadSuccess,
      clickUpload,
      deleteFileAdd,
      pushReview,
      reasonTask,
      refreshDelivery,
      changeReason,

      trim,
      changeRequirement,

      visChange,
      changeIsEditType,
      openDrawer,
      taskSpeed,
      statusFilChild,
      restart
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
      // this.attrs = attrs.filter(attr => attr.value !== '' && attr.value !== null)
      this.attrs = attrs
    },
    watch: {
      task(val) {
        console.log(this.task)
        this.taskData = JSON.parse(JSON.stringify(this.task));

        let attrs = this.taskData.taskDataList.map(x => {
          this.$set(x, 'name', '');


          x.name = this.taskAttr.filter(task => task.projectAttrId !== null && x.projectAttrId !== null && task.projectAttrId === x.projectAttrId)[0].label;
          return x;
        });
        // this.attrs = attrs.filter(attr => attr.value !== '' && attr.value !== null)

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
    height: 30px;
    font-size: 13px;
    line-height: 30px;
  }

  .show_style_label:hover {
    background-color: #DBE4EE;
    color: #01408B;
  }
</style>
