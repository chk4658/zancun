<template>
  <div style="flex: 1;overflow-x: auto;">


    <div v-for="mts in milestoneTaskStatus" :key="mts.milestone.id">
      <div style="margin-left: 20px;font-size: 22px;margin-top: 24px">{{mts.milestone.name}}</div>
      <div class="table-container">

        <div v-for="task in mts.taskStatus" :key="task.id" class="box-card">

          <el-card style="width: 100%;height: 48px;border: 0;box-shadow: none;-webkit-box-shadow: none;
      border-radius: 0;border-top-left-radius: 5px;border-top-right-radius: 5px;color: white"
                   class="card-top"
                   :style="{backgroundColor: task.color}">{{task.name}} / {{task.taskList.length-1}}
          </el-card>
          <el-card class="card-middle"
                   style="overflow-y: auto;width: 100%;flex: 1;border: 0;box-shadow: none;-webkit-box-shadow: none;border-radius: 0"
                   :style="{backgroundColor: task.color}">
            <draggable :list="task.taskList" :animation='200' group="task" :move="dragAdd" @end="endDo">
              <transition-group>

                <el-card v-for="item in task.taskList" :class="buttCom(item, task.taskList.length)"
                         style="cursor: pointer"
                         :key="item.id" @click.native="item.id!==-100&&editTask(item)">
                  <div v-if="item.id!==-100">
                    <div style="margin-bottom: 15px;font-size: 17px">
                      <i class="el-icon-message-solid"
                         :style="{color:timeStatus(item)}"
                         v-if="item.dateStatus==='6'"
                         style="margin-right: 5px"></i>
                      <span :style="{color:timeStatus(item)}" v-if="item.dateStatus==='6'">{{item.name}}</span>
                      <span v-if="item.dateStatus!=='6'">{{item.name}}</span>
                      <!--                      <el-tooltip placement="top">-->
                      <!--                        <div slot="content">-->
                      <!--                          负责人：{{item.reviewUser===null?'':item.reviewUser.realName}}<br/>-->
                      <!--                          确认人：{{item.confirmUser===null?'':item.confirmUser.realName}}-->
                      <!--                        </div>-->
                      <!--                        <i class="el-icon-s-custom" style="float: right"></i>-->
                      <!--                      </el-tooltip>-->
                    </div>
                    <div style="margin-bottom: 15px;font-size: 10px">
                      负责人：{{item.reviewUser===null?'':item.reviewUser.realName}}&nbsp;&nbsp;
                      确认人：{{item.confirmUser===null?'':item.confirmUser.realName}}
                    </div>
                    <div style="height: 10px;width: 10px;border-radius: 5px;margin-bottom: 15px"
                         :style="{backgroundColor:timeStatus(item)}">
                    </div>
                    <div style="font-size: 15px;text-align: center;width: 105px;">

                      <task-status dict-code="TASK_STATUS" v-model="item.status" v-if="bulletinType==='2'"
                                   position="bottom" width="150" :item="item" :isShow="true"
                                   :hasArrow="true"></task-status>

                      <task-status dict-code="PRIORITY" v-model="item.priority" v-if="bulletinType==='1'"
                                   position="bottom" width="150" :item="item" :isShow="true"
                                   :hasArrow="true"></task-status>

                    </div>
                    <div style="margin-top: 12px;font-size: 13px;">
                      截止：{{item.estEndTime===null?'':item.estEndTime.split(' ')[0]}}
                      &nbsp;&nbsp;
                      完成：{{item.actEndTime===null?'':item.actEndTime.split(' ')[0]}}

                      <el-popover
                        placement="right"
                        :visible-arrow="false"
                        width="200"
                        trigger="click">
                        <div v-for="ch in item.childTask" :key="ch.id">
                          <div class="show_style_label" @click="editTask(ch)">
                            {{ch.name}}
                          </div>
                        </div>

                        <span slot="reference"
                              style="float: right;cursor: pointer;
                  -webkit-user-select: none;
                  -moz-user-select: none;
                  -ms-user-select: none;
                  user-select: none;" v-if="item.childTask.length!==0" @click.stop="">
            <i class="el-icon-s-data"></i>/{{item.childTask.length}}
          </span>
                      </el-popover>


                    </div>
                  </div>

                </el-card>
              </transition-group>
            </draggable>
          </el-card>

          <el-card class="card-end"
                   style="width: 100%;height: 52px;border: 0;box-shadow: none;-webkit-box-shadow: none;
      border-radius: 0;border-bottom-left-radius: 5px;border-bottom-right-radius: 5px;color: white"
                   :style="{backgroundColor: task.color}">

            <div v-if="bulletinType==='1'&&!task.isAdd&&task.code==='1'&&authority&&mSize!==0"
                 style="color: #fff;user-select: none;cursor:pointer;height: 34px;line-height: 34px;"
                 @click="changeIsAdd(task)"><i
              class="el-icon-plus"></i>添加
            </div>
            <div style="position: relative"
                 v-if="bulletinType==='1'&&task.isAdd&&task.code==='1'&&mSize!==0">
              <input type="text" class="cancel_input_style"
                     v-model="addValue"
                     v-focus2="task.isAdd"
                     @blur="task.isAdd=false"
                     @keyup.enter="addTaskBulletin(task,mts.milestone.id)"
                     style="width: 100%;height: 34px;font-size: 16px;padding-right: 62px;
               box-sizing: border-box;padding-left: 8px;border-radius: 5px">
              <div style="position: absolute;right: 1px;top: 1px;
             height: 32px;line-height: 32px;border-radius: 5px;
             text-align: center;width: 60px;user-select: none;cursor: pointer"
                   @mousedown="addTaskBulletin(task,mts.milestone.id,event)"
                   :style="{backgroundColor: task.color}">+Add
              </div>
            </div>


            <div v-if="bulletinType==='2'&&!task.isAdd&&authority&&mSize!==0"
                 style="color: #fff;user-select: none;cursor:pointer;height: 34px;line-height: 34px;"
                 @click="changeIsAdd(task)"><i
              class="el-icon-plus"></i>添加
            </div>
            <div style="position: relative" v-if="bulletinType==='2'&&task.isAdd&&mSize!==0">
              <input type="text" class="cancel_input_style"
                     v-model="addValue"
                     v-focus2="task.isAdd"
                     @blur="task.isAdd=false"
                     @keyup.enter="addTaskBulletin(task,mts.milestone.id)"
                     style="width: 100%;height: 34px;font-size: 16px;padding-right: 62px;
               box-sizing: border-box;padding-left: 8px;border-radius: 5px">
              <div style="position: absolute;right: 1px;top: 1px;
             height: 32px;line-height: 32px;border-radius: 5px;
             text-align: center;width: 60px;user-select: none;cursor: pointer"
                   @mousedown="addTaskBulletin(task,mts.milestone.id,event)"
                   :style="{backgroundColor: task.color}">+Add
              </div>
            </div>

          </el-card>

          <!--          <el-card v-for="task in mts.taskStatus" :key="task.id" class="box-card" :style="{backgroundColor: task.color}">-->
          <!--            <div style="margin-bottom: 12px">{{task.name}} / {{task.taskList.length-1}}</div>-->

          <!--            <draggable :list="task.taskList" :animation='200' group="task" :move="dragAdd" @end="endDo">-->
          <!--              <transition-group>-->

          <!--                <el-card v-for="item in task.taskList" :class="item.id===-100 ? 'class-a' : 'box-card-child'"-->
          <!--                         style="cursor: pointer"-->
          <!--                         :key="item.id" @click.native="item.id!==-100&&editTask(item)">-->
          <!--                  <div v-if="item.id!==-100">-->
          <!--                    <div style="margin-bottom: 30px;font-size: 17px">-->
          <!--                      {{item.name}}-->
          <!--                      <el-tooltip placement="top">-->
          <!--                        <div slot="content">-->
          <!--                          负责人：{{item.reviewUser===null?'':item.reviewUser.realName}}<br/>-->
          <!--                          确认人：{{item.confirmUser===null?'':item.confirmUser.realName}}-->
          <!--                        </div>-->
          <!--                        <i class="el-icon-s-custom" style="float: right"></i>-->
          <!--                      </el-tooltip>-->
          <!--                    </div>-->
          <!--                    <div style="font-size: 15px;text-align: center;width: 105px;">-->

          <!--                      <task-status dict-code="TASK_STATUS" v-model="item.status" v-if="bulletinType==='2'"-->
          <!--                                   position="bottom" width="150" :item="item" :isShow="true"-->
          <!--                                   :hasArrow="true"></task-status>-->

          <!--                      <task-status dict-code="PRIORITY" v-model="item.priority" v-if="bulletinType==='1'"-->
          <!--                                   position="bottom" width="150" :item="item" :isShow="true"-->
          <!--                                   :hasArrow="true"></task-status>-->

          <!--                    </div>-->
          <!--                    <div style="margin-top: 12px;font-size: 13px;">-->
          <!--                      {{item.estEndTime}}-->

          <!--                      <el-popover-->
          <!--                        placement="right"-->
          <!--                        :visible-arrow="false"-->
          <!--                        width="200"-->
          <!--                        trigger="click">-->
          <!--                        <div v-for="ch in item.childTask" :key="ch.id">-->
          <!--                          <div class="show_style_label" @click="editTask(ch)">-->
          <!--                            {{ch.name}}-->
          <!--                          </div>-->
          <!--                        </div>-->

          <!--                        <span slot="reference"-->
          <!--                              style="float: right;cursor: pointer;-->
          <!--                  -webkit-user-select: none;-->
          <!--                  -moz-user-select: none;-->
          <!--                  -ms-user-select: none;-->
          <!--                  user-select: none;" v-if="item.childTask.length!==0" @click.stop="">-->
          <!--            <i class="el-icon-s-data"></i>/{{item.childTask.length}}-->
          <!--          </span>-->
          <!--                      </el-popover>-->


          <!--                    </div>-->
          <!--                  </div>-->

          <!--                </el-card>-->
          <!--              </transition-group>-->
          <!--            </draggable>-->

          <!--            <div v-if="bulletinType==='1'&&!task.isAdd&&task.code==='1'&&authority&&mSize!==0"-->
          <!--                 style="color: #fff;margin-top: 12px;user-select: none;cursor:pointer;height: 34px;line-height: 34px;"-->
          <!--                 @click="changeIsAdd(task)"><i-->
          <!--              class="el-icon-plus"></i>添加-->
          <!--            </div>-->
          <!--            <div style="margin-top: 12px;position: relative"-->
          <!--                 v-if="bulletinType==='1'&&task.isAdd&&task.code==='1'&&mSize!==0">-->
          <!--              <input type="text" class="cancel_input_style"-->
          <!--                     v-model="addValue"-->
          <!--                     v-focus2="task.isAdd"-->
          <!--                     @blur="task.isAdd=false"-->
          <!--                     @keyup.enter="addTaskBulletin(task,mts.milestone.id)"-->
          <!--                     style="width: 100%;height: 34px;font-size: 16px;padding-right: 62px;-->
          <!--               box-sizing: border-box;padding-left: 8px;border-radius: 5px">-->
          <!--              <div style="position: absolute;right: 1px;top: 1px;-->
          <!--             height: 32px;line-height: 32px;border-radius: 5px;-->
          <!--             text-align: center;width: 60px;user-select: none;cursor: pointer"-->
          <!--                   @mousedown="addTaskBulletin(task,mts.milestone.id,event)"-->
          <!--                   :style="{backgroundColor: task.color}">+Add-->
          <!--              </div>-->
          <!--            </div>-->


          <!--            <div v-if="bulletinType==='2'&&!task.isAdd&&authority&&mSize!==0"-->
          <!--                 style="color: #fff;margin-top: 12px;user-select: none;cursor:pointer;height: 34px;line-height: 34px;"-->
          <!--                 @click="changeIsAdd(task)"><i-->
          <!--              class="el-icon-plus"></i>添加-->
          <!--            </div>-->
          <!--            <div style="margin-top: 12px;position: relative" v-if="bulletinType==='2'&&task.isAdd&&mSize!==0">-->
          <!--              <input type="text" class="cancel_input_style"-->
          <!--                     v-model="addValue"-->
          <!--                     v-focus2="task.isAdd"-->
          <!--                     @blur="task.isAdd=false"-->
          <!--                     @keyup.enter="addTaskBulletin(task,mts.milestone.id)"-->
          <!--                     style="width: 100%;height: 34px;font-size: 16px;padding-right: 62px;-->
          <!--               box-sizing: border-box;padding-left: 8px;border-radius: 5px">-->
          <!--              <div style="position: absolute;right: 1px;top: 1px;-->
          <!--             height: 32px;line-height: 32px;border-radius: 5px;-->
          <!--             text-align: center;width: 60px;user-select: none;cursor: pointer"-->
          <!--                   @mousedown="addTaskBulletin(task,mts.milestone.id,event)"-->
          <!--                   :style="{backgroundColor: task.color}">+Add-->
          <!--              </div>-->
          <!--            </div>-->
          <!--          </el-card>-->
        </div>

      </div>
    </div>


    <!--    带条件通过和拒绝 填写理由-->
    <el-dialog
      :title="reason.title"
      @close="closeReason"
      :visible.sync="reason.dialogVisible"
      width="600px"
      append-to-body>

      <el-input
        type="textarea"
        :rows="5"
        placeholder="使用Enter保存"
        v-model="reason.value"
        @keydown.native="changeReason($event)"></el-input>

      <span slot="footer" class="dialog-footer">
        <el-button @click="reason.dialogVisible=false">取 消</el-button>
        <el-button type="primary" @click="changeReason($event,'BUTTON')">确 定</el-button>
      </span>

    </el-dialog>
    <!--    点击显示任务的dialog-->
    <el-dialog
      @close="closeTask"
      :visible.sync="taskDialogVisible"
      width="580px"
      center
      append-to-body>
      <div slot="title" class="header-title">
        <span>{{taskInformation.name}}</span>
      </div>
      <task-edit :task="taskInformation"
                 :taskAttr="taskDataAttr"
                 @save="closeTask"
                 @re="refreshDelivery"
                 @update="listTask('')"
                 :reBeginAuthority="reBeginAuthority"
                 :authority="authority"
                 :rolePlus="rolePlus"></task-edit>
    </el-dialog>


  </div>
</template>
<script>
  import {_getTaskByProjectId, _updateTaskById} from '@/api/taskApi';
  import TaskEdit from './TaskEdit';
  import {_getToPut, _taskObj} from '@/utils/taskUtils'
  import draggable from 'vuedraggable'
  import TaskStatus from '@/components/TaskStatus';


  function compare(property) {
    return function (a, b) {
      const value1 = a[property];
      const value2 = b[property];
      return value1 - value2;
    };
  }

  function timeStatus(item) {
    const task = this.dateStatusList.filter((dateStatus => dateStatus.code === item.dateStatus))[0];

    return task === undefined ? '#ccc' : task.color;
  }

  async function listTask(searchName) {
    this.refreshTime();
    let dictData;
    if (this.bulletinType === '1') {
      dictData = this.$store.getters.getDictionaryByKey('TASK_STATUS');
    } else if (this.bulletinType === '2') {
      dictData = this.$store.getters.getDictionaryByKey('PRIORITY');
    }
    const taskStatus = dictData.sysDictDataList;

    const result = await _getToPut(this.pid, searchName, this, this.types, this.person);

    this.taskDataAttr = result.cols;
    const childs = result.childTaskList;

    let milestonePlus = [];
    const parents = [];
    result.milestoneArray.forEach(MA => {
      MA.taskList.forEach(p => {
        this.$set(p, 'childTask', []);
        p.childTask = childs.filter(ch => ch.parentId !== null && ch.parentId === p.id).sort((a, b) => {
          return a.sort < b.sort ? -1 : 1;
        });
      })
      const touch = [];
      taskStatus.forEach(ts => touch.push(JSON.parse(JSON.stringify(ts))))


      const status = [];
      touch.forEach(x => {
        x.taskList = [];
        x.isAdd = false;
        if (this.bulletinType === '1') {
          x.taskList = MA.taskList.filter(task => task.status !== null && x.code !== null && task.status.toString() === x.code.toString());

          x.taskList.push({
            id: -100,
            status: x.code,
            childTask: [],
            sort: 0,
            milestoneId: MA.projectMilestone.id
          })

        } else if (this.bulletinType === '2') {
          x.taskList = MA.taskList.filter(task => task.priority !== null && x.code !== null && task.priority.toString() === x.code.toString());

          x.taskList.push({
            id: -100,
            priority: x.code,
            childTask: [],
            sort: 0,
            milestoneId: MA.projectMilestone.id
          })
        }
        status.push(x)
      });

      parents.push(...MA.taskList);
      milestonePlus.push({
        milestone: MA.projectMilestone,
        taskStatus: status.sort(this.compare('code'))
      })


    })

    const ref = parents.concat(childs);
    if (this.currentTaskId !== '' && ref.filter(r => r.id === this.currentTaskId).length !== 0) {
      this.taskInformation = ref.filter(r => r.id === this.currentTaskId)[0]
    }


    this.milestoneTaskStatus = milestonePlus;

  }

  async function updateTaskById(task) {

    const result1 = _taskObj(task, this.gaspRefuse, this.gaspPass);


    const obj = result1;

    await _updateTaskById(obj);

    await this.listTask(this.searchValue)


  }

  async function changeReason(event, button) {
    const code = event.keyCode || event.which
    if ((!event.ctrlKey && code === 13) || (button !== undefined && button === 'BUTTON')) {
      if (this.reason.value.replace(/(^\s*)|(\s*$)/g, "") === '') {
        event.preventDefault();
        this.$message({
          message: '写点什么吧',
          type: 'warning',
          center: 'true',
        });
        return false
      }
      this.reason.dialogVisible = false;
      if (this.reason.reasonStatus === '4') {
        this.reason.reasonTaskObject.refuseReason = this.reason.value;
        this.reason.reasonTaskObject.status = this.reason.reasonStatus;
        this.reason.gaspRefuse = true;
        await this.updateTaskById(this.reason.reasonTaskObject);
        this.reason.gaspRefuse = false;
      } else {
        this.reason.reasonTaskObject.passReason = this.reason.value;
        this.reason.reasonTaskObject.status = this.reason.reasonStatus;
        this.reason.gaspPass = true;
        await this.updateTaskById(this.reason.reasonTaskObject);
        this.reason.gaspPass = false;
      }
    }

  }

  /**
   * 点击 显示任务详细、修改任务
   */
  function editTask(item) {
    this.taskInformation = item;
    this.currentTaskId = item.id;
    this.taskDialogVisible = true;
  }

  function closeTask() {
    this.taskDialogVisible = false;
    this.currentTaskId = '';
    this.listTask(this.searchValue);
  }

  /**
   * 添加对应任务
   * @param task
   */
  function changeIsAdd(task) {
    this.addValue = '';
    task.isAdd = true;
  }

  async function addTaskBulletin(task, milestoneId, event) {
    if (event !== undefined) {
      event.preventDefault();
    }

    if (trim(this.addValue) !== '') {

      let obj;
      if (this.bulletinType === '1') {
        obj = {
          name: this.addValue,
          status: task.code,
          projectId: this.pid,
          milestoneId: milestoneId,
        }
      } else {
        obj = {
          name: this.addValue,
          priority: task.code,
          projectId: this.pid,
          milestoneId: milestoneId,
        }
      }
      const result = await _updateTaskById(obj);
      if (result.code === 1200) {
        this.$message({
          message: '新增成功',
          type: 'success',
          center: 'true',
        });
        await this.listTask(this.searchValue);
      }
    } else {
      task.isAdd = true;
    }
  }


  function trim(s) {
    return s.replace(/(^\s*)|(\s*$)/g, "");
  }

  function refreshTime() {

    this.$parent.refreshEndTime();
  }

  function dragAdd(evt, originalEvent) {


    if (this.bulletinType === '2') {
      // 如果显示的是按优先级划分的

      let a = JSON.parse(JSON.stringify(evt.draggedContext.element));

      if (evt.draggedContext.element.priority !== evt.relatedContext.element.priority) {


        if (this.authority) {

          if (evt.draggedContext.element.milestoneId !== evt.relatedContext.element.milestoneId) {
            a.milestoneId = evt.relatedContext.element.milestoneId
          }

          a.priority = evt.relatedContext.element.priority;

          this.taskAlert = '';
          this.reason.title = '';
          this.taskObj = a;
          this.taskUpdate = true;
        } else {

          this.taskAlert = '';
          this.reason.title = '';
          this.taskObj = {};
          this.taskUpdate = false;
          return false
        }
      } else {


        if (evt.draggedContext.element.milestoneId !== evt.relatedContext.element.milestoneId) {
          if (this.authority) {
            a.milestoneId = evt.relatedContext.element.milestoneId;
            this.taskAlert = '';
            this.reason.title = '';
            this.taskObj = a;
            this.taskUpdate = true;
          } else {
            this.taskAlert = '';
            this.reason.title = '';
            this.taskObj = {};
            this.taskUpdate = false;
            return false
          }
        } else {
          this.taskAlert = '';
          this.reason.title = '';
          this.taskObj = {};
          this.taskUpdate = false;
          return true
        }


      }


    } else {
      // 如果显示的是按状态划分的

      const ever = evt.draggedContext.element.status;
      let a = JSON.parse(JSON.stringify(evt.draggedContext.element));

      let taskReviewerId = evt.draggedContext.element.reviewUser === null ? evt.draggedContext.element.reviewUser : evt.draggedContext.element.reviewUser.id;

      let taskConfirmerId = evt.draggedContext.element.confirmUser === null ? evt.draggedContext.element.confirmUser : evt.draggedContext.element.confirmUser.id;


      if (evt.draggedContext.element.status !== evt.relatedContext.element.status) {

        if (evt.draggedContext.element.milestoneId !== evt.relatedContext.element.milestoneId) {
          if (this.authority) {
            a.milestoneId = evt.relatedContext.element.milestoneId;
            switch (ever) {

              case '1':

                // 负责人具有开始任务的权限
                if (taskReviewerId === this.userId && evt.relatedContext.element.status === '2') {
                  a.status = evt.relatedContext.element.status;
                  this.taskObj = a;
                  this.taskUpdate = true;
                  this.taskAlert = '';
                  this.reason.title = '';
                } else {
                  this.taskObj = {};
                  this.taskUpdate = false;
                  this.taskAlert = '';
                  this.reason.title = '';
                  return false;
                }

                break;
              case '2':
                // 负责人具有提交审核的权限


                if (taskReviewerId === this.userId && evt.relatedContext.element.status === '3') {
                  // 判断是否交付要求
                  if (evt.draggedContext.element.isRequirement === 1 || (evt.draggedContext.element.isRequirement === 0 && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus !== 1).length > 0)) {
                    // 已知当前任务状态为2
                    if (evt.draggedContext.element.status === '2' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0).length > 0 && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 2 && (i.reuploadList === null || i.reuploadList === '')).length === 0) {
                      a.status = evt.relatedContext.element.status;
                      const arr = []
                      a.taskDeliveryList.filter(td => td.auditStatus === 2 && td.reuploadList !== null && td.reuploadList !== '').forEach(list => arr.push(list.id))
                      a.reuploadListIds = arr.join(',');
                      this.taskObj = a;
                      this.taskUpdate = true;
                      this.taskAlert = '';
                      this.reason.title = '';
                    } else {

                      this.taskAlert = '交付要求';

                      this.contentTip = '';

                      let title = ''

                      if (evt.draggedContext.element.status === '2' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0).length === 0) {
                        title = '有交付要求哦，请提交交付物'
                      }

                      this.contentTip = title;


                      this.reason.title = '';
                      this.taskObj = {};
                      this.taskUpdate = false;

                      return false;
                    }
                  } else {
                    a.status = evt.relatedContext.element.status;
                    this.taskObj = a;
                    this.taskUpdate = true;
                    this.taskAlert = '';
                    this.reason.title = '';
                  }
                } else {
                  this.taskObj = {};
                  this.taskUpdate = false;
                  this.taskAlert = '';
                  this.reason.title = '';
                  return false
                }
                break;

              case '3':
                // 任务审核人具有审核任务的权限

                if (taskConfirmerId === this.userId && evt.relatedContext.element.status === '4') {

                  if (evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0 || (i.auditStatus === 2 && i.isReupload !== 1)).length > 0) {
                    // 必须所有交付物都审核通过才能审核任务


                    this.taskAlert = '交付物';
                    this.reason.title = '';
                    this.taskObj = {};
                    this.taskUpdate = false;
                    return false;
                  } else {

                    this.reason.reasonStatus = evt.relatedContext.element.status;
                    this.reason.title = '请填写拒绝理由';
                    this.taskAlert = '';

                    this.reason.reasonTaskObject = JSON.parse(JSON.stringify(a));

                    this.taskObj = {};
                    this.taskUpdate = false;

                    return false;
                  }
                } else if (taskConfirmerId === this.userId && evt.relatedContext.element.status === '5') {

                  if (evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0 || (i.auditStatus === 2 && i.isReupload !== 1)).length > 0) {
                    // 必须所有交付物都审核通过才能审核任务

                    this.taskAlert = '交付物';
                    this.reason.title = '';

                    this.taskObj = {};
                    this.taskUpdate = false;
                    return false;
                  } else {

                    this.reason.reasonStatus = evt.relatedContext.element.status;
                    this.reason.title = '请填写带条件通过理由';
                    this.taskAlert = '';
                    this.reason.reasonTaskObject = JSON.parse(JSON.stringify(a));

                    this.taskObj = {};
                    this.taskUpdate = false;
                    return false;
                  }

                } else if (taskConfirmerId === this.userId && evt.relatedContext.element.status === '6') {
                  if (evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0 || (i.auditStatus === 2 && i.isReupload !== 1)).length > 0) {
                    // 必须所有交付物都审核通过才能审核任务

                    this.taskAlert = '交付物'

                    this.reason.title = '';

                    this.taskObj = {};
                    this.taskUpdate = false;
                    return false;
                  } else {
                    a.status = evt.relatedContext.element.status;
                    this.taskObj = a;
                    this.taskAlert = '';
                    this.reason.title = '';
                    this.taskUpdate = true;
                  }
                } else {
                  this.taskObj = {};
                  this.taskAlert = '';
                  this.reason.title = '';
                  this.taskUpdate = false;
                  return false
                }
                break;
              case '4':
                if (taskReviewerId === this.userId && evt.relatedContext.element.status === '3') {
                  // 判断是否交付要求
                  if (evt.draggedContext.element.isRequirement === 1 || (evt.draggedContext.element.isRequirement === 0 && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus !== 1).length > 0)) {
                    // 已知当前任务状态为4
                    if (evt.draggedContext.element.status === '4' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0).length > 0 && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 2 && (i.reuploadList === null || i.reuploadList === '')).length === 0) {
                      a.status = evt.relatedContext.element.status;
                      const arr = []
                      a.taskDeliveryList.filter(td => td.auditStatus === 2 && td.reuploadList !== null && td.reuploadList !== '').forEach(list => arr.push(list.id))
                      a.reuploadListIds = arr.join(',');
                      this.taskObj = a;
                      this.taskUpdate = true;
                      this.taskAlert = '';
                      this.reason.title = '';
                    } else {

                      this.taskAlert = '交付要求';

                      this.contentTip = '';
                      let title = ''

                      if (evt.draggedContext.element.status === '4' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 2 && (i.reuploadList === null || i.reuploadList === '')).length > 0) {
                        title = '有已拒绝交付物，需点击 重新上传 上传交付物！'
                      }

                      if (evt.draggedContext.element.status === '4' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0).length === 0) {
                        title = '有交付要求哦，请提交交付物'
                      }
                      this.contentTip = title;
                      this.reason.title = '';
                      this.taskObj = {};
                      this.taskUpdate = false;

                      return false;
                    }
                  } else {
                    a.status = evt.relatedContext.element.status;
                    this.taskObj = a;
                    this.taskAlert = '';
                    this.reason.title = '';
                    this.taskUpdate = true;
                  }

                } else {
                  this.taskObj = {};
                  this.taskAlert = '';
                  this.reason.title = '';
                  this.taskUpdate = false;
                  return false;
                }
                break;
              case '5':
                if (taskReviewerId === this.userId && evt.relatedContext.element.status === '3') {


                  if (evt.draggedContext.element.isRequirement === 1 || (evt.draggedContext.element.isRequirement === 0 && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus !== 1).length > 0)) {
                    // 已知当前任务状态为5
                    if (evt.draggedContext.element.status === '5' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0).length !== 0) {
                      a.status = evt.relatedContext.element.status;
                      const arr = []
                      a.taskDeliveryList.filter(td => td.auditStatus === 2 && td.reuploadList !== null && td.reuploadList !== '').forEach(list => arr.push(list.id))
                      a.reuploadListIds = arr.join(',');
                      this.taskObj = a;
                      this.taskUpdate = true;
                      this.taskAlert = '';
                      this.reason.title = '';
                    } else {

                      this.taskAlert = '交付要求';

                      this.contentTip = '';
                      let title = ''
                      if (evt.draggedContext.element.status === '5' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0).length === 0) {
                        title = '有交付要求哦，请提交交付物'
                      }
                      this.contentTip = title;


                      this.reason.title = '';
                      this.taskObj = {};
                      this.taskUpdate = false;

                      return false;
                    }
                  } else {
                    a.status = evt.relatedContext.element.status;
                    this.taskObj = a;
                    this.taskAlert = '';
                    this.reason.title = '';
                    this.taskUpdate = true;
                  }

                } else {
                  this.taskObj = {};
                  this.taskAlert = '';
                  this.reason.title = '';
                  this.taskUpdate = false;
                  return false;
                }
                break;
              case '6':

                if (this.reBeginAuthority && evt.relatedContext.element.status === '2') {
                  a.status = evt.relatedContext.element.status;
                  this.taskObj = a;
                  this.taskAlert = '';
                  this.reason.title = '';
                  this.taskUpdate = true;
                } else {
                  this.taskObj = {};
                  this.taskAlert = '';
                  this.reason.title = '';
                  this.taskUpdate = false;
                  return false;
                }
                break;
            }
          } else {
            this.taskAlert = '';
            this.reason.title = '';
            this.taskObj = {};
            this.taskUpdate = false;
            return false
          }

        } else {
          switch (ever) {

            case '1':

              // 负责人具有开始任务的权限
              if (taskReviewerId === this.userId && evt.relatedContext.element.status === '2') {
                a.status = evt.relatedContext.element.status;
                this.taskObj = a;
                this.taskUpdate = true;
                this.taskAlert = '';
                this.reason.title = '';
              } else {
                this.taskObj = {};
                this.taskUpdate = false;
                this.taskAlert = '';
                this.reason.title = '';
                return false;
              }

              break;
            case '2':
              // 负责人具有提交审核的权限


              if (taskReviewerId === this.userId && evt.relatedContext.element.status === '3') {
                // 判断是否交付要求
                if (evt.draggedContext.element.isRequirement === 1 || (evt.draggedContext.element.isRequirement === 0 && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus !== 1).length > 0)) {
                  // 已知当前任务状态为2
                  if (evt.draggedContext.element.status === '2' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0).length > 0 && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 2 && (i.reuploadList === null || i.reuploadList === '')).length === 0) {
                    a.status = evt.relatedContext.element.status;
                    const arr = []
                    a.taskDeliveryList.filter(td => td.auditStatus === 2 && td.reuploadList !== null && td.reuploadList !== '').forEach(list => arr.push(list.id))
                    a.reuploadListIds = arr.join(',');
                    this.taskObj = a;
                    this.taskUpdate = true;
                    this.taskAlert = '';
                    this.reason.title = '';
                  } else {

                    this.taskAlert = '交付要求';

                    this.contentTip = '';

                    let title = ''

                    if (evt.draggedContext.element.status === '2' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0).length === 0) {
                      title = '有交付要求哦，请提交交付物'
                    }

                    this.contentTip = title;


                    this.reason.title = '';
                    this.taskObj = {};
                    this.taskUpdate = false;

                    return false;
                  }
                } else {
                  a.status = evt.relatedContext.element.status;
                  this.taskObj = a;
                  this.taskUpdate = true;
                  this.taskAlert = '';
                  this.reason.title = '';
                }
              } else {
                this.taskObj = {};
                this.taskUpdate = false;
                this.taskAlert = '';
                this.reason.title = '';
                return false
              }
              break;

            case '3':
              // 任务审核人具有审核任务的权限

              if (taskConfirmerId === this.userId && evt.relatedContext.element.status === '4') {

                if (evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0 || (i.auditStatus === 2 && i.isReupload !== 1)).length > 0) {
                  // 必须所有交付物都审核通过才能审核任务


                  this.taskAlert = '交付物';
                  this.reason.title = '';
                  this.taskObj = {};
                  this.taskUpdate = false;
                  return false;
                } else {

                  this.reason.reasonStatus = evt.relatedContext.element.status;
                  this.reason.title = '请填写拒绝理由';
                  this.taskAlert = '';

                  this.reason.reasonTaskObject = JSON.parse(JSON.stringify(a));

                  this.taskObj = {};
                  this.taskUpdate = false;

                  return false;
                }
              } else if (taskConfirmerId === this.userId && evt.relatedContext.element.status === '5') {

                if (evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0 || (i.auditStatus === 2 && i.isReupload !== 1)).length > 0) {
                  // 必须所有交付物都审核通过才能审核任务

                  this.taskAlert = '交付物';
                  this.reason.title = '';

                  this.taskObj = {};
                  this.taskUpdate = false;
                  return false;
                } else {

                  this.reason.reasonStatus = evt.relatedContext.element.status;
                  this.reason.title = '请填写带条件通过理由';
                  this.taskAlert = '';
                  this.reason.reasonTaskObject = JSON.parse(JSON.stringify(a));

                  this.taskObj = {};
                  this.taskUpdate = false;
                  return false;
                }

              } else if (taskConfirmerId === this.userId && evt.relatedContext.element.status === '6') {
                if (evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0 || (i.auditStatus === 2 && i.isReupload !== 1)).length > 0) {
                  // 必须所有交付物都审核通过才能审核任务

                  this.taskAlert = '交付物'

                  this.reason.title = '';

                  this.taskObj = {};
                  this.taskUpdate = false;
                  return false;
                } else {
                  a.status = evt.relatedContext.element.status;
                  this.taskObj = a;
                  this.taskAlert = '';
                  this.reason.title = '';
                  this.taskUpdate = true;
                }
              } else {
                this.taskObj = {};
                this.taskAlert = '';
                this.reason.title = '';
                this.taskUpdate = false;
                return false
              }
              break;
            case '4':
              if (taskReviewerId === this.userId && evt.relatedContext.element.status === '3') {
                // 判断是否交付要求
                if (evt.draggedContext.element.isRequirement === 1 || (evt.draggedContext.element.isRequirement === 0 && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus !== 1).length > 0)) {
                  // 已知当前任务状态为4
                  if (evt.draggedContext.element.status === '4' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0).length > 0 && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 2 && (i.reuploadList === null || i.reuploadList === '')).length === 0) {
                    a.status = evt.relatedContext.element.status;
                    const arr = []
                    a.taskDeliveryList.filter(td => td.auditStatus === 2 && td.reuploadList !== null && td.reuploadList !== '').forEach(list => arr.push(list.id))
                    a.reuploadListIds = arr.join(',');
                    this.taskObj = a;
                    this.taskUpdate = true;
                    this.taskAlert = '';
                    this.reason.title = '';
                  } else {

                    this.taskAlert = '交付要求';

                    this.contentTip = '';
                    let title = ''

                    if (evt.draggedContext.element.status === '4' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 2 && (i.reuploadList === null || i.reuploadList === '')).length > 0) {
                      title = '有已拒绝交付物，需点击 重新上传 上传交付物！'
                    }
                    if (evt.draggedContext.element.status === '4' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0).length === 0) {
                      title = '有交付要求哦，请提交交付物'
                    }
                    this.contentTip = title;
                    this.reason.title = '';
                    this.taskObj = {};
                    this.taskUpdate = false;

                    return false;
                  }
                } else {
                  a.status = evt.relatedContext.element.status;
                  this.taskObj = a;
                  this.taskAlert = '';
                  this.reason.title = '';
                  this.taskUpdate = true;
                }

              } else {
                this.taskObj = {};
                this.taskAlert = '';
                this.reason.title = '';
                this.taskUpdate = false;
                return false;
              }
              break;
            case '5':
              if (taskReviewerId === this.userId && evt.relatedContext.element.status === '3') {


                if (evt.draggedContext.element.isRequirement === 1 || (evt.draggedContext.element.isRequirement === 0 && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus !== 1).length > 0)) {
                  // 已知当前任务状态为5
                  if (evt.draggedContext.element.status === '5' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0).length !== 0) {
                    a.status = evt.relatedContext.element.status;
                    const arr = []
                    a.taskDeliveryList.filter(td => td.auditStatus === 2 && td.reuploadList !== null && td.reuploadList !== '').forEach(list => arr.push(list.id))
                    a.reuploadListIds = arr.join(',');
                    this.taskObj = a;
                    this.taskUpdate = true;
                    this.taskAlert = '';
                    this.reason.title = '';
                  } else {

                    this.taskAlert = '交付要求';

                    this.contentTip = '';
                    let title = ''
                    if (evt.draggedContext.element.status === '5' && evt.draggedContext.element.taskDeliveryList.filter(i => i.auditStatus === 0).length === 0) {
                      title = '有交付要求哦，请提交交付物'
                    }
                    this.contentTip = title;


                    this.reason.title = '';
                    this.taskObj = {};
                    this.taskUpdate = false;

                    return false;
                  }
                } else {
                  a.status = evt.relatedContext.element.status;
                  this.taskObj = a;
                  this.taskAlert = '';
                  this.reason.title = '';
                  this.taskUpdate = true;
                }

              } else {
                this.taskObj = {};
                this.taskAlert = '';
                this.reason.title = '';
                this.taskUpdate = false;
                return false;
              }
              break;
            case '6':

              if (this.reBeginAuthority && evt.relatedContext.element.status === '2') {
                a.status = evt.relatedContext.element.status;
                this.taskObj = a;
                this.taskAlert = '';
                this.reason.title = '';
                this.taskUpdate = true;
              } else {
                this.taskObj = {};
                this.taskAlert = '';
                this.reason.title = '';
                this.taskUpdate = false;
                return false;
              }
              break;
          }
        }


      } else {

        if (evt.draggedContext.element.milestoneId !== evt.relatedContext.element.milestoneId) {
          if (this.authority) {
            a.milestoneId = evt.relatedContext.element.milestoneId;
            this.taskAlert = '';
            this.reason.title = '';
            this.taskObj = a;
            this.taskUpdate = true;
          } else {
            this.taskAlert = '';
            this.reason.title = '';
            this.taskObj = {};
            this.taskUpdate = false;
            return false
          }
        } else {
          this.taskObj = {};
          this.taskAlert = '';
          this.reason.title = '';
          this.taskUpdate = false;
          return true
        }
      }
    }
  }

  function refreshDelivery() {

    this.$parent.refreshDelivery()

  }

  function endDo() {

    if (this.taskUpdate) {
      this.updateTaskById(this.taskObj)
    } else if (this.reason.title !== '') {
      this.reason.dialogVisible = true;
    } else if (this.taskAlert === '交付要求') {
      this.$alert(this.contentTip, '提交审核失败', {
        center: true,
        showClose: false,
        type: "info",
        closeOnClickModal: true,
        showConfirmButton: false
      }).catch(() => {
        this.taskAlert = ''
      });

    } else if (this.taskAlert === '交付物') {
      this.$alert('还有交付物需要审核哦', '审核失败', {
        center: true,
        showClose: false,
        type: "info",
        closeOnClickModal: true,
        showConfirmButton: false
      }).catch(() => {
        this.taskAlert = ''
      });
    }
  }

  function closeReason() {

    this.reason.dialogVisible = false;
    this.reason.title = ''

  }

  function buttCom(item, len) {
    if (len === 1) {
      // if (item.id === -100) {
      //   return 'class-b'
      // } else {
      //   return 'box-card-child'
      // }
      return 'class-b'
    } else {
      if (item.id === -100) {
        return 'class-a'
      } else {
        return 'box-card-child'
      }
    }
  }


  export default {
    components: {TaskEdit, draggable, TaskStatus},
    props: {
      isAddTask: Boolean,
      searchValue: String,
      rolePlus: Array,
      authority: Boolean,
      mSize: Number,
      reBeginAuthority: Boolean,
      bulletinType: String,
      person: String
    },
    data() {
      return {
        userId: localStorage.getItem('id'),
        pid: '',
        // 单个任务信息
        taskDialogVisible: false,
        taskInformation: '',
        taskDataAttr: [],
        // 添加
        addValue: '',

        types: [],

        pize: [],

        milestoneTaskStatus: [],
        reason: {
          dialogVisible: false,
          title: '',
          value: '',
          reasonStatus: '',
          gaspPass: false,
          gaspRefuse: false,
          reasonTaskObject: {},
        },
        dateStatusList: [],


        taskObj: {},
        taskUpdate: false,
        taskAlert: '',
        contentTip: '',

        currentTaskId: ''

      };
    },
    methods: {
      listTask,
      compare,
      editTask,
      closeTask,
      changeIsAdd,
      refreshDelivery,
      addTaskBulletin,

      trim,
      dragAdd,
      refreshTime,
      updateTaskById,
      endDo,
      changeReason,
      closeReason,
      timeStatus,
      buttCom

    },
    created() {

      const dictDataType = this.$store.getters.getDictionaryByKey('TASK_TYPE');
      this.types = dictDataType.sysDictDataList;

      const dictData = this.$store.getters.getDictionaryByKey('TASK_DATE_STATUS');
      this.dateStatusList = dictData.sysDictDataList;

      this.pid = this.$route.query.id === undefined ? '' : this.$route.query.id;
      if (this.pid !== '') {
        this.listTask(this.searchValue);
      }
    },
    watch: {
      $route() {
        this.pid = this.$route.query.id === undefined ? '' : this.$route.query.id;
        this.listTask(this.searchValue);
      },
      isAddTask(val) {
        this.addValue = '';

        if (this.milestoneTaskStatus.length > 0) {
          this.milestoneTaskStatus[0].taskStatus.filter(ts => ts.code === '1')[0].isAdd = true;
        }

      },
      bulletinType: function (newVal, oldVal) {

        if (newVal !== oldVal) {
          this.listTask(this.searchValue);
        }
      },
      searchValue: function (newVal, oldVal) {

        this.listTask(newVal);
      },
      person: function (newVal, oldVal) {
        this.listTask(this.searchValue);

      }
    },
  };
</script>

<style scoped lang="scss">

  .table-container {
    height: 400px;
    overflow-y: hidden;
    position: relative;
    padding: 20px;
    box-sizing: border-box;
    display: flex;
    margin-right: 20px;
  }

  .box-card {
    /*border-radius: 10px;*/
    /*margin-right: 20px;*/
    /*width: 400px;*/
    /*flex-shrink: 0;*/
    /*height: 300px;*/
    /*overflow-y: auto;*/
    /*color: #FFFFFF;*/
    margin-right: 20px;
    width: 400px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    color: #FFFFFF;
  }

  .box-card-child {
    border-radius: 5px;
    margin-bottom: 5px;
    width: 100%;
  }

  .cancel_input_style {
    appearance: none;
    -webkit-appearance: none;
    -moz-appearance: none;
    -o-appearance: none;
    border: none;
    box-shadow: none;
    outline: none;
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
    text-align: center;
  }

  .show_style_label:hover {
    background-color: #DBE4EE;
    color: #01408B;
  }

  .class-a {
    height: 0px;
    opacity: 0;
  }

  .class-b {
    height: 180px;
    opacity: 0;
  }

</style>

<style lang="scss">
  .card-top > {
    .el-card__body {
      padding-top: 14px;
      padding-bottom: 10px;
    }
  }

  .card-middle > {
    .el-card__body {
      padding-top: 5px;
      padding-bottom: 5px;
    }
  }

  .card-end > {
    .el-card__body {
      padding-top: 10px;
      padding-bottom: 10px;
    }
  }
</style>
