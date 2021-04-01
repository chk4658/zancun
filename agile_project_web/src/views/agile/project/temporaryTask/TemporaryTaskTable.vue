<template>
  <div class="table-container project-temple"
       ref="mainHeight" id="content">

    <div v-infinite-scroll="loadMore"
         infinite-scroll-disabled="disabled">
      <div>
        <el-table
          ref="taskTable"
          class="task-table"
          :data="temporaryTaskCopy.taskList"
          row-key="id"
          id="agricultureTable"
          :header-cell-style="{background:'#F5F7FA'}"
          border
          lazy
          :span-method="arraySpanMethod"
          :load="load"
          :indent="0"
          :expand-row-keys='expandRowkeys'
          :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
          <template slot="empty">
            <div></div>
          </template>

          <el-table-column
            class-name="tab-cell"
            align="center"
            type=""
            width="44px">

            <template slot-scope="scope">
              <el-popover
                placement="bottom-start"
                :visible-arrow="false"
                width="200"
                trigger="click">
                <div ref="fan">

                  <div class="show_style_label"
                       @click="changeTaskStatus(scope.row,'2')"
                       v-hasProOperation="'{\'taskReviewerId\':\'' + (scope.row.reviewUser===null?scope.row.reviewUser:scope.row.reviewUser.id) +'\', \'key\': \'hasBeginTask\',\'taskStatus\':\'' + scope.row.status +'\'}'">
                    <i class="el-icon-unlock" style="margin-left: 5px;margin-right: 5px"></i>
                    开始任务
                  </div>


                  <div class="show_style_label"
                       @click="restart(scope.row,'2')"
                       v-hasProOperation="'{\'key\': \'hasReBeginTask\',\'reBeginAuthority\':\'' + (userId===scope.row.createUserId) +'\',\'taskStatus\':\'' + scope.row.status +'\'}'">
                    <i class="el-icon-unlock" style="margin-left: 5px;margin-right: 5px"></i>
                    重新开始任务
                  </div>

                  <div class="show_style_label"
                       @click="pushReview(scope.row)"
                       v-hasProOperation="'{\'taskReviewerId\':\'' + (scope.row.reviewUser===null?scope.row.reviewUser:scope.row.reviewUser.id) +'\', \'key\': \'hasPushReviewTask\',\'taskStatus\':\'' + scope.row.status +'\'}'">
                    <i class="el-icon-s-fold" style="margin-left: 5px;margin-right: 5px"></i>
                    提交审核
                  </div>
                  <el-popover
                    placement="right"
                    width="150"
                    trigger="click">
                    <div>
                      <div
                        v-if="scope.row.taskDeliveryList!==null&&scope.row.taskDeliveryList.filter(i=>i.auditStatus===0||(i.auditStatus===2&&i.isReupload!==1)).length>0">
                        还有交付物需要审核哦

                      </div>
                      <div v-else>

                        <div class="show_style_label"
                             style="padding-left: 6px"
                             @click="reasonTask(scope.row,'CONDITIONAL')">
                          带条件通过
                        </div>
                        <div class="show_style_label"
                             style="padding-left: 6px"
                             @click="changeTaskStatus(scope.row,'6')">
                          通过
                        </div>
                        <div class="show_style_label"
                             style="padding-left: 6px"
                             @click="reasonTask(scope.row,'REFUSE')">
                          拒绝
                        </div>
                      </div>
                    </div>

                    <div class="show_style_label" slot="reference"
                         v-hasProOperation="'{\'taskConfirmerId\':\'' + (scope.row.confirmUser===null?scope.row.confirmUser:scope.row.confirmUser.id) +'\', \'key\': \'hasConfirmTask\',\'taskStatus\':\'' + scope.row.status +'\'}'">
                      <i class="el-icon-document-checked" style="margin-left: 5px;margin-right: 5px"></i>
                      审核完成
                    </div>

                  </el-popover>
                  <div class="show_style_label" @click="addChildTask(scope.row)"
                       v-if="scope.row.parentId==null&&(userId===scope.row.createUserId)">
                    <i class="el-icon-menu" style="margin-left: 5px;margin-right: 5px"></i>
                    新增子任务
                  </div>
                  <div class="show_style_label" @click="deleteTask(scope)" v-if="userId===scope.row.createUserId">
                    <i class="el-icon-delete-solid" style="margin-left: 5px;margin-right: 5px"></i>
                    删除任务
                  </div>

                  <div class="show_style_label"
                       v-hasProOperation="'{\'taskReviewerId\':\'' + (scope.row.reviewUser===null?scope.row.reviewUser:scope.row.reviewUser.id) +'\', ' +
                      '\'key\': \'allTNot\',' +
                       '\'taskConfirmerId\':\'' + (scope.row.confirmUser===null?scope.row.confirmUser:scope.row.confirmUser.id) +'\',' +
                        '\'authority\': \'' + (userId===scope.row.createUserId) +'\',' +
                         '\'taskStatus\':\'' + scope.row.status +'\'}'">
                    当前无任何权限哦
                  </div>
                </div>
                <i slot="reference" class="el-icon-more" v-if="scope.row.id!=='+++'"
                   style="cursor: pointer;height: 23px;line-height: 23px;font-size: 15px;"></i>
              </el-popover>
            </template>
          </el-table-column>

          <el-table-column
            class-name="name-cell"
            width="500px"
            property="name"
            label="任务名称">
            <template slot="header">

              <span style="margin-left: 10px">任务名称</span>

            </template>
            <template slot-scope="scope">
              <div style="height: 23px;line-height: 23px;display: flex;flex: 1" v-if="scope.row.id!=='+++'">
                <div v-if="!scope.row.hasChildren" style="height: 23px;width: 16px"></div>
                <div style="height: 23px;line-height: 23px;float: left;margin-right: 5px"
                     :style="{backgroundColor:timeStatus(scope.row)}"
                     :class="isSelected">

                  <el-checkbox v-model="scope.row.checked"
                               id="check-box"
                               @change="handleSelectionChange(scope.row)"
                               style="color:#CA8830;margin-left: 5px"></el-checkbox>

                </div>
                {{scope.row.serialNumber}}、
                <div style="flex: 1;">
                  <click-input :item="scope.row"
                               :authority="(userId===scope.row.createUserId)"
                               :webScoketChat="webScoketChat"
                               @chat="openDrawer"
                               :hasChatTaskIds="[]"
                               @pull="pullChildTask"
                               @update="saveTask"></click-input>
                </div>
              </div>
              <div style="height: 23px;line-height: 23px;display: flex;flex: 1" v-else>
                <div style="height: 23px;width: 16px"></div>
                <div
                  class="edit_input_style is_dashed"
                  v-if="!temporaryTaskCopy.isEdit.Add"
                  style="cursor: pointer"
                  @click="addNewTask(temporaryTaskCopy)">+Add
                </div>
                <el-input
                  v-if="temporaryTaskCopy.isEdit.Add"
                  v-focus="temporaryTaskCopy.isEdit.Add"
                  v-model="taskAdd.name"
                  maxlength="35"
                  @blur="saveTask(taskAdd,temporaryTaskCopy)"
                  @keyup.enter.native="$event.target.blur"></el-input>

              </div>
            </template>
          </el-table-column>
          <!--        </el-table-column>-->
          <el-table-column
            align="center"
            width="140px"
            label="状态">
            <template slot-scope="scope">


              <el-tooltip placement="top" :disabled="!(scope.row.status==='4'||scope.row.status==='5')">
                <div slot="content">

                  <div
                    v-if="scope.row.passReason!==''&&scope.row.passReason!==null && scope.row.passReason!==undefined">
                    带条件通过理由：<br/>
                    <div v-for="(pr,index) in scope.row.passReason.split('||')" :key="index">
                      {{index+1}}、<span>{{pr}}</span><br/>
                    </div>
                  </div>

                  <div
                    v-if="scope.row.refuseReason!==''&&scope.row.refuseReason!==null&& scope.row.passReason!==undefined">
                    拒绝理由：<br/>
                    <div v-for="(rr,index2) in scope.row.refuseReason.split('||')" :key="index2">
                      {{index2+1}}、<span>{{rr}}</span><br/>
                    </div>
                  </div>
                </div>
                <task-status dict-code="TASK_STATUS" v-model="scope.row.status"
                             position="bottom" width="150" :item="scope.row" :isShow="true"
                             :hasArrow="true" @update="changeStatus"></task-status>
              </el-tooltip>


            </template>
          </el-table-column>
          <el-table-column
            width="140px"
            align="center"
            label="交付物">
            <template slot-scope="scope">
              <div v-if="scope.row.isRequirement===1">
                <!--              <i class="el-icon-upload" style="font-size: 15px;cursor: pointer;margin-right: 3px"-->
                <!--                 @click="changeDeliverables(scope.row)"></i>-->


                <!--                <div style="display: inline-block"-->
                <!--                     v-if="(scope.row.status === '2'||scope.row.status==='4' ||scope.row.status=== '5')&&userId===(scope.row.reviewUser===null?scope.row.reviewUser:scope.row.reviewUser.id)">-->
                <!--                  <el-upload-->
                <!--                    class="upload-demo"-->
                <!--                    action="/api/upload"-->
                <!--                    :headers="{-->
                <!--                    token: token-->
                <!--                  }"-->
                <!--                    :limit="20"-->
                <!--                    :on-exceed="handleExceed"-->
                <!--                    :on-success="uploadSuccessJ"-->
                <!--                    :show-file-list="false">-->
                <!--                    <i class="el-icon-upload" style="font-size: 15px"-->
                <!--                       v-if="scope.row.taskDeliveryList!==null&&scope.row.taskDeliveryList.length<20"-->
                <!--                       @click="changeDeliverables(scope.row)">-->
                <!--                    </i>-->
                <!--                  </el-upload>-->
                <!--                </div>-->

                <!--                v-if="scope.row.taskDeliveryList!==null&&scope.row.taskDeliveryList.length!==0"-->
                <i class="el-icon-more" style="font-size: 15px;cursor: pointer;margin-left: 3px"

                   @click="changeDeliverables(scope.row,true)"></i>
              </div>
            </template>
          </el-table-column>

          <el-table-column
            align="center"
            width="140px"
            label="优先级">
            <template slot-scope="scope">


              <task-status dict-code="PRIORITY" v-model="scope.row.priority"
                           position="bottom" width="150" :item="scope.row" :isShow="!(userId===scope.row.createUserId)"
                           :hasArrow="true" @update="changePriority"></task-status>


            </template>
          </el-table-column>


          <el-table-column
            align="center"
            width="140px"
            label="责任人">
            <template slot-scope="scope">
              <el-tag
                type="success"
                style="width:110px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;cursor: pointer"
                @click="(userId===scope.row.createUserId)&&bindUser(scope.row,'review')"
                disable-transitions>{{scope.row.reviewUser===null?'':scope.row.reviewUser.realName}}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            width="140px"
            label="确认人">
            <template slot-scope="scope">

              <el-tag
                type="success"
                style="width:110px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;cursor: pointer"
                @click="(userId===scope.row.createUserId)&&bindUser(scope.row,'confirm')"
                disable-transitions>{{scope.row.confirmUser===null?'':scope.row.confirmUser.realName}}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="estEndTime"
            format="yy/MM/dd"
            width="150px"
            label="截止时间">
            <template slot-scope="scope">
              <div style="height: 23px;line-height: 23px">
                <el-date-picker
                  style="width: 128px"
                  v-model="scope.row.estEndTime"
                  type="date"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                  :readonly="!(userId===scope.row.createUserId)"
                  @change="saveTask(scope.row)"
                  placeholder="选择日期">
                </el-date-picker>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="actEndTime"
            format="yy/MM/dd"
            width="200px"
            label="完成时间">
            <template slot-scope="scope">
              <div style="height: 23px;line-height: 23px">
                <div style="width: 100%;height: 23px;background-color:#F2F4F5;color: #626365">
                  {{scope.row.actEndTime}}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="isRequirement"
            width="100px"
            label="交付要求">
            <template slot-scope="scope">
              <div style="height: 23px;line-height: 23px;cursor: pointer">
              <span v-if="scope.row.isRequirement!==1"
                    @click="(userId===scope.row.createUserId)&&changeRequirement(scope.row,1)">O</span>
                <i class="el-icon-check" v-if="scope.row.isRequirement===1"
                   @click="(userId===scope.row.createUserId)&&changeRequirement(scope.row,0)"></i>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="type"
            width="310px"
            label="任务类型">
            <template slot-scope="scope">

              <div style="height: 23px;line-height: 23px;display: flex">
                <div>
                  <el-tag
                    type="info"
                    size="small"
                    style="width: 290px;height: 23px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;cursor: pointer"
                    v-if="!scope.row.isEdit.type"
                    @click="(userId===scope.row.createUserId)&&changeIsEditType(scope.row)"
                    disable-transitions>
                    {{scope.row.typeName}}
                  </el-tag>
                  <el-select
                    style="height: 23px!important;line-height: 23px;width: 290px"
                    v-model="scope.row.type"
                    class="limit-height"
                    multiple
                    size="mini"
                    placeholder="请选择"
                    @remove-tag="saveTask(scope.row)"
                    v-if="scope.row.isEdit.type"
                    @visible-change="visChange($event,scope.row)"
                  >
                    <el-option
                      v-for="item in types"
                      :key="item.code"
                      :label="item.name"
                      :value="item.code">
                    </el-option>
                  </el-select>
                </div>
              </div>
            </template>
          </el-table-column>


          <el-table-column
            width="300px"
            property="openConditions"
            align="center"
            v-if="temporaryTaskCopy.isOpen"
            label="开阀条件">
            <template slot-scope="scope">
              <div v-if="scope.row.type.indexOf('3')!==-1">
                <p
                  v-if="!scope.row.isEdit.openConditions"
                  @click="(userId===scope.row.createUserId)&&changeOpenConditions(scope.row)"
                  style="cursor: pointer"
                  class="text-style">

                  <span v-if="scope.row.openConditions === null || scope.row.openConditions.length <= 11">{{scope.row.openConditions}}</span>
                  <el-tooltip v-else class="item" effect="dark" placement="top-start">
                    <div slot="content">
                      <div style="width: 300px;word-break: break-all;">{{scope.row.openConditions}}</div>
                    </div>
                    <span>{{scope.row.openConditions.substring(0,10)}}...</span>
                  </el-tooltip>

                </p>
                <el-input
                  v-model="scope.row.openConditions"
                  size="mini"
                  v-if="scope.row.isEdit.openConditions"
                  v-focus="scope.row.isEdit.openConditions"
                  @blur="saveTask(scope.row)"
                  @keyup.enter.native="$event.target.blur"/>
              </div>
            </template>
          </el-table-column>

          <el-table-column
            width="300px"
            property="openDescription"
            align="center"
            v-if="temporaryTaskCopy.isOpen"
            label="开阀描述">
            <template slot-scope="scope">
              <div v-if="scope.row.type.indexOf('3')!==-1">
                <p
                  v-if="!scope.row.isEdit.openDescription"
                  @click="(userId===scope.row.createUserId)&&changeOpenDescription(scope.row)"
                  style="cursor: pointer"
                  class="text-style">

                  <span v-if="scope.row.openDescription === null || scope.row.openDescription.length <= 11">{{scope.row.openDescription}}</span>
                  <el-tooltip v-else class="item" effect="dark" placement="top-start">
                    <div slot="content">
                      <div style="width: 300px;word-break: break-all;">{{scope.row.openDescription}}</div>
                    </div>
                    <span>{{scope.row.openDescription.substring(0,10)}}...</span>
                  </el-tooltip>

                </p>
                <el-input
                  v-model="scope.row.openDescription"
                  size="mini"
                  v-if="scope.row.isEdit.openDescription"
                  v-focus="scope.row.isEdit.openDescription"
                  @blur="saveTask(scope.row)"
                  @keyup.enter.native="$event.target.blur"/>
              </div>
            </template>
          </el-table-column>


        </el-table>

      </div>

      <p v-if="loading" style="text-align: center"><i class="el-icon-loading"></i>加载中...</p>

    </div>
    <!--      选择人员-->
    <el-dialog
      title="指定人员"
      @close="dialogVisible.user=false"
      :visible.sync="dialogVisible.user"
      width="1200px"
      append-to-body>
      <user-tree
        :isSingle='true'
        :usersProp='current.users'
        @getUsers="getUsers"
        @getCancel="dialogVisible.user=false">
      </user-tree>
    </el-dialog>

    <!--      选择交付物相关-->
    <el-dialog
      :title="taskDeliveryTitle"
      @close="closeDelivery"
      :visible.sync="dialogVisible.delivery"
      width="1048px"
      center
      append-to-body>
      <task-delivery :taskInfo="taskInfo" @refresh="refreshDelivery"></task-delivery>
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


    <el-drawer
      title=""
      :visible.sync="dialogVisible.taskDrawer"
      :with-header="false"
      size="50%"
    >

      <task-drawer :task="drawerTask" :active="activeName" :refresh="dialogVisible.taskDrawer"></task-drawer>
    </el-drawer>


    <div class="batch-actions-menu-wrapper react-boards"
         style="left: 560px;width: 800px"
         v-if="checkeds.length>0">
      <div class="num-of-actions_wrapper">
        <div class="num-of-actions">{{checkeds.length}}</div>
      </div>
      <div class="batch-actions-title-section">
        <div class="title">选中的任务</div>
        <div class="pulses_dots">
          <div class="dot" style="background: rgb(253, 171, 61);"></div>
        </div>
      </div>
      <div class="batch-actions-item">
        <el-popover
          placement="top"
          width="200"
          v-model="batchDateSelector">
          <p>将批量更新所选任务的截止时间,是否确定?</p>
          <div style="text-align: center;margin-bottom: 10px">
            <el-date-picker
              v-model="batchDateSelectorValue"
              type="date"
              style="width: 200px"
              size="large"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
              placeholder="选择日期">
            </el-date-picker>
          </div>
          <div style="text-align: right; margin: 0">
            <el-button size="mini" type="text" @click="batchDateSelector = false">取消</el-button>
            <el-button type="primary" size="mini" @click="batchUpdateEstEndTime">确定</el-button>
          </div>
          <i class="el-icon-copy-document" style="position: relative;top: 9px;font-size: 27px;cursor: pointer;" slot="reference"></i>
        </el-popover>
        <span class="action-name">批量更新截止时间</span>
      </div>
      <div class="batch-actions-item">
        <i class="el-icon-delete-solid" style="position: relative;top: 9px;font-size: 27px;cursor: pointer;"
           @click="batchDelete"></i>
        <span class="action-name">删除</span>
      </div>
      <div class="num-of-actions_wrapper_cancel">
        <div class="num-of-actions"><i class="el-icon-close" @click="cancelSelected"></i></div>
      </div>
    </div>
  </div>
</template>
<script>
  import moment from 'moment';
  import UserTree from '@/components/UserTree.vue';
  import TaskStatus from '@/components/TaskStatus';
  import {
    _updateTaskById,
    _deleteTask,
    _batchDeleteTask,
    _batchUpdateTaskTime,
  } from '@/api/taskApi';
  import ClickInput from '@/components/ClickInput';
  import TaskDelivery from "../task/table/TaskDelivery";
  import TaskDrawer from "../task/table/TaskDrawer";
  import {_addTaskDelivery} from '@/api/taskDeliveryApi';
  import {_dealTemporaryTask, _taskObj, _taskTemporaryObj} from "../../../../utils/taskUtils";
  import {_updateTemporaryTask} from "../../../../api/taskApi";


  /**
   * 初始化
   * @returns {Promise<void>}
   */
  async function listTemporaryTask(searchName) {
    const result = await _dealTemporaryTask(searchName, this, this.types, this.person);
    this.childTaskList = JSON.parse(JSON.stringify(result.childTaskList));

    const MA = result.virtualMilestone;
    MA.taskList.push({
      id: '+++',
      checked: false,
      confirmProjectRole: null,
      confirmProjectRoleId: null,
      confirmUser: null,
      estEndTime: null,
      isEdit: {
        name: false,
        type: false,
        openConditions: false,
        openDescription: false,
      },
      milestoneId: null,
      name: '',
      openConditions: null,
      openDescription: null,
      openStatus: null,
      parentId: null,
      projectId: null,
      reviewProjectRole: null,
      reviewProjectRoleId: null,
      taskDataList: [],
      taskDeliveryList: [],
      reviewUser: null,
      status: null,
      isRequirement: 0,
      type: [],
      priority: null,
    })

    this.temporaryTask = JSON.parse(JSON.stringify(MA));


    this.setCopy();

  }


  // 模拟点击任何地方让popover消失(起因: Elementui Popover 嵌套在表格中的用v-model失效)
  function cancelPopover() {
    document.getElementById('agricultureTable')
      .click();
  }


  //去除字符串左右空格，防止新增名称为空格的里程碑或任务
  function trim(s) {
    return s.replace(/(^\s*)|(\s*$)/g, '');
  }


  /**
   * 批量删除的div,获取选中多选框变化
   * @returns {Promise<void>}
   */

  function handleSelectionChange(row) {
    if (row.checked) {
      this.checkeds.push(row.id);
    } else {
      this.checkeds.splice((this.checkeds.indexOf(row.id)), 1);
    }

  }

  function cancelSelected() {
    this.checkeds = [];
    if (this.temporaryTaskCopy.taskList.length > 0) {
      this.temporaryTaskCopy.taskList.forEach(i => {
        i.checked = false;
      });
    }

    if (this.childTaskList.length > 0) {
      this.childTaskList.forEach(i => i.checked = false);
    }
  }

  async function batchDelete() {
    const idsArr = this.checkeds;
    const result = await _batchDeleteTask({
      ids: idsArr.join(','),
    });
    this.checkeds = [];
    if (result.code === 1200) {
      await this.listTemporaryTask('');
      //   const {tree, treeNode, resolve} = this.loadNodeMap.get(item.parentId);
      // tree.children.splice(tree.children.indexOf(item), 1)
      for (let [k, v] of this.loadNodeMap) {
        const {tree, treeNode, resolve} = v;
        this.load(tree, treeNode, resolve);
      }
    }
  }

  async function batchUpdateEstEndTime() {
    this.batchDateSelector = false;
    const vals = this.checkeds.map(ch => {
      return {
        id: ch,
        estEndTime: this.batchDateSelectorValue === '' ? '' : moment(this.batchDateSelectorValue).format('YYYY-MM-DD HH:mm:ss')
      }
    })
    this.checkeds = [];
    console.log(vals)
    const result = await _batchUpdateTaskTime(vals, false)
    this.batchDateSelectorValue = ''
    if (result.code === 1200) {
      await this.listTemporaryTask(this.searchValue);
      for (let [k, v] of this.loadNodeMap) {
        const {tree, treeNode, resolve} = v;
        this.load(tree, treeNode, resolve);
      }
    }
  }

  /**
   * 保存Task
   * @returns {Promise<void>}
   */
  async function saveTask(task, item) {
    let obj = {};
    if (task.id === '') {
      obj = {
        name: task.name,
        parentId: task.parentId,
        createUserId: localStorage.getItem('id'),
        isTemporary: 1,
        priority: '4',
      };
      if (trim(task.name) === '') {

        item.isEdit.Add = false;

      } else {
        const result = await _updateTaskById(obj);
        if (result.code === 1200) {
          await this.listTemporaryTask('');
          if (task.parentId !== null && task.parentId !== undefined) {
            const {tree, treeNode, resolve} = this.loadNodeMap.get(task.parentId);
            this.load(tree, treeNode, resolve);
          }
        }
      }

    } else {
      const result = _taskTemporaryObj(task, this.gaspRefuse, this.gaspPass);
      obj = result;

      const res = await _updateTaskById(obj);
      if (res.code === 1200) {
        await this.listTemporaryTask('');
        if (task.parentId !== null && task.parentId !== undefined) {
          const {tree, treeNode, resolve} = this.loadNodeMap.get(task.parentId);
          this.load(tree, treeNode, resolve);
        }
      } else if (res.code === 4227 || res.code === 4226 || res.code === 4222 || res.code === 4229) {
        // 4227, "子任务未完成（未开始、待审核、拒绝等），主任务不能通过！"
        // 4226, "子任务截止时间不能大于父任务截止时间！"
        // 4222, "父任务截止时间不能小于子任务截止时间！"
        // 4229, "子任务带条件通过时，主任务不能完全通过，可以带条件通过或拒绝！"
        await this.listTemporaryTask('');
        if (task.parentId !== null && task.parentId !== undefined) {
          const {tree, treeNode, resolve} = this.loadNodeMap.get(task.parentId);
          this.load(tree, treeNode, resolve);
        }
      }

    }

  }


  function bindUser(row, itemType) {
    this.current.users = [];
    this.current.item = row;
    this.current.type = itemType;

    if (itemType === 'review') {
      if (row.reviewUser !== null) {
        this.current.users.push(row.reviewUser);
      }

    } else {
      if (row.reviewUser !== null) {
        this.current.users.push(row.reviewUser);
      }
    }

    this.dialogVisible.user = true;

  }

  // 获得审核人/确认人id
  async function getUsers(data) {
    let obj
    if (this.current.type === 'review') {
      obj = {
        taskId: this.current.item.id,
        reviewSysUserId: (data !== null && data.length > 0) ? data[0].id : null,
        confirmSysUserId: this.current.item.confirmUser === null ? null : this.current.item.confirmUser.id,
      };
    } else {
      obj = {
        taskId: this.current.item.id,
        reviewSysUserId: this.current.item.reviewUser === null ? null : this.current.item.reviewUser.id,
        confirmSysUserId: (data !== null && data.length > 0) ? data[0].id : null,
      };
    }
    await _updateTemporaryTask(obj);

    await this.listTemporaryTask(this.searchValue);

    if (this.current.item.parentId !== null && this.current.item.parentId !== undefined) {
      const {tree, treeNode, resolve} = this.loadNodeMap.get(this.current.item.parentId);
      this.load(tree, treeNode, resolve);
    }

    this.$parent.getPerson();

  }

  function addNewTask(item) {
    item.isEdit.Add = true;
    this.taskAdd = {
      id: '',
      name: '',
      passReason: '',
      refuseReason: '',
      createUserId: localStorage.getItem('id'),
      isTemporary: 1,
    };
  }


  async function visChange(callback, task) {
    if (!callback) {
      await this.saveTask(task);
    }
  }


  function changeStatus(val, item) {
    item.status = val;
    this.saveTask(item);
  }

  function changePriority(val, item) {
    item.priority = val;
    this.saveTask(item);
  }

  function changeDeliverables(info, bool) {

    this.taskInfo = info;
    if (bool) {
      this.taskDeliveryTitle = info.name;
      this.dialogVisible.delivery = true;

    }

  }

  function closeDelivery() {

    this.dialogVisible.delivery = false;
    this.listTemporaryTask('');

    for (let [k, v] of this.loadNodeMap) {
      const {tree, treeNode, resolve} = v;
      this.load(tree, treeNode, resolve);
    }
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

    this.reasonTaskObject = task;
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
        await this.saveTask(this.reasonTaskObject);
        this.gaspRefuse = false;
      } else {
        this.reasonTaskObject.passReason = trim(this.reasonValue);
        this.reasonTaskObject.status = this.reasonStatus;
        this.gaspPass = true;
        await this.saveTask(this.reasonTaskObject);
        this.gaspPass = false;
      }
    }

  }


  function changeOpenConditions(item) {
    item.isEdit.openConditions = true;
  }

  function changeOpenDescription(item) {
    item.isEdit.openDescription = true;
  }

  function changeIsEditCols(item) {
    item.isEditCols = true;
  }

  function changeIsEditType(item) {

    item.isEdit.type = true;
  }

  // 删除任务
  async function deleteTask(scope) {
    this.cancelPopover();
    this.$confirm('即将删除此任务，您想继续吗？', '删除任务', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        const result = await _deleteTask(scope.row.id);
        if (result.code === 1200) {
          this.$message({
            type: 'success',
            message: '删除成功!',
            center: 'true',
          });
          await this.listTemporaryTask('');

          for (let [k, v] of this.loadNodeMap) {
            const {tree, treeNode, resolve} = v;
            this.load(tree, treeNode, resolve);
          }

        }
      })
      .catch((e) => {
        this.$message({
          type: 'info',
          message: '执行失败！',
          center: 'true',
        });
      });
  }


  // 新增子任务
  function addChildTask(item) {
    this.cancelPopover();

    item.hasChildren = true;
    this.childTaskList.push(this.getNewTask(item));
    const {tree, treeNode, resolve} = this.loadNodeMap.get(item.id);
    this.expandRowkeys = [tree.id];
    tree.children.push(this.getNewTask(item));
    resolve(tree.children);
  }

  function getNewTask(item) {
    return {
      id: '',
      checked: false,
      confirmProjectRole: '',
      confirmProjectRoleId: '',
      confirmUser: '',
      estEndTime: '',
      isEdit: {
        name: true,
        type: false,
        openConditions: false,
        openDescription: false,
      },
      milestoneId: item.milestoneId,
      name: '',
      openConditions: null,
      openDescription: null,
      openStatus: null,
      parentId: item.id,
      projectId: item.projectId,
      reviewProjectRole: null,
      reviewProjectRoleId: null,
      taskDataList: [],
      taskDeliveryList: [],
      reviewUser: null,
      status: null,
      isRequirement: 0,
      type: [],
      priority: null,
      serialNumber: "*",
    };
  }

  async function pullChildTask(item) {
    this.childTaskList.splice(this.childTaskList.indexOf(item), 1)
    const {tree, treeNode, resolve} = this.loadNodeMap.get(item.parentId);
    tree.children.splice(tree.children.indexOf(item), 1)
    await this.listTemporaryTask('');
    resolve(tree.children);
  }

  function changeTaskStatus(task, val) {
    this.cancelPopover();
    if (val === '6') {
      const tta = JSON.parse(JSON.stringify(task));
      tta.status = val;
      this.saveTask(tta);
    } else {
      task.status = val;
      this.saveTask(task);
    }
  }

  // 重新开始任务 需要再传一个标记
  function restart(task, val) {
    this.cancelPopover();
    const tta = JSON.parse(JSON.stringify(task));
    tta.status = val;
    tta.restartTaskFlag = true;
    this.saveTask(tta);
  }

  function pushReview(task) {
    this.cancelPopover();
    if (task.isRequirement === 1 || (task.isRequirement === 0 && task.taskDeliveryList.filter(i => i.auditStatus !== 1).length > 0)) {

      if (((task.status === '2' || task.status === '4') && task.taskDeliveryList !== null && task.taskDeliveryList.filter(i => i.auditStatus === 0).length > 0 && task.taskDeliveryList.filter(i => i.auditStatus === 2 && (i.reuploadList === null || i.reuploadList === '')).length === 0)
        || (task.status === '5' && task.taskDeliveryList !== null && task.taskDeliveryList.filter(i => i.auditStatus === 0).length !== 0)) {
        const taskCopy = JSON.parse(JSON.stringify(task))
        const arr = []
        taskCopy.taskDeliveryList.filter(td => td.auditStatus === 2 && td.reuploadList !== null && td.reuploadList !== '').forEach(list => arr.push(list.id))
        taskCopy.reuploadListIds = arr.join(',');
        this.changeTaskStatus(taskCopy, '3');
      } else {
        let title = ''
        if ((task.status === '2' || task.status === '5' || task.status === '4') && task.taskDeliveryList.filter(i => i.auditStatus === 0).length === 0) {
          title = '有交付要求哦，请提交交付物'
        }
        if (task.status === '4' && task.taskDeliveryList.filter(i => i.auditStatus === 2 && (i.reuploadList === null || i.reuploadList === '')).length > 0) {
          title = '有已拒绝交付物，需点击 重新上传 上传交付物！'
        }

        this.$alert(title, '提交审核失败', {
          center: true,
          showClose: false,
          type: "info",
          closeOnClickModal: true,
          showConfirmButton: false
        });
      }
    } else {
      this.changeTaskStatus(task, '3');
    }
  }


  async function changeRequirement(item, val) {
    item.isRequirement = val;
    await this.saveTask(item);
  }


  function load(tree, treeNode, resolve) {
    setTimeout(() => {

      tree.hasChildren = true;
      const childTaskList =
        this.childTaskList.filter(childTask => childTask.parentId === tree.id).sort((a, b) => {
          return a.sort < b.sort ? -1 : 1;
        });
      childTaskList.forEach((item, index) => this.$set(item, "serialNumber", index + 1))
      tree.children = childTaskList;
      this.loadNodeMap.set(tree.id, {
        tree,
        treeNode,
        resolve,
      });

      resolve(tree.children);
    }, 0);
  }


  // 时间状态

  function timeStatus(item) {
    const task = this.dateStatusList.filter((dateStatus => dateStatus.code === item.dateStatus))[0];

    return task === undefined ? '#ccc' : task.color;
  }

  function openDrawer(data) {

    this.activeName = 'taskChat';
    this.dialogVisible.taskDrawer = true;
    this.drawerTask = data;
  }

  async function refreshDelivery(item, task) {

    if (item === 'REFUSE') {
      this.dialogVisible.delivery = false;
      task.refuseReason = '交付物被拒绝，请去交付物列表查看拒绝原因';
      task.status = '4';
      this.gaspRefuse = true;
      await this.saveTask(task);
      this.gaspRefuse = false;
    }
  }


  async function uploadSuccessJ(res, file) {


    const id = localStorage.getItem('id');

    const result = await _addTaskDelivery({
      taskId: this.taskInfo.id,
      deliveryName: file.name,
      submitUserId: id,
      auditStatus: 0,
      path: res.data.path,
      submitAt: moment(new Date()).format('YYYY-MM-DD HH:mm:ss'),
      // projectId: this.taskInfo.projectId,
      deliverySize: file.size,
    });
    if (result.code === 1200) {

      this.listTemporaryTask('');

      for (let [k, v] of this.loadNodeMap) {
        const {tree, treeNode, resolve} = v;
        this.load(tree, treeNode, resolve);
      }
    }
  }

  function arraySpanMethod({row, column, rowIndex, columnIndex}) {
    if (row.id === '+++') {
      if (columnIndex === 0) {
        return [1, 1];
      } else if (columnIndex === 1) {
        return [1, 6];
      } else {
        return [0, 0];
      }
    }
  }


  function loadMore() {

    this.loading = true
    setTimeout(() => {
      this.loading = false;
      this.currentPage += 1;
      this.setCopy();
    }, 300)
  }


  function setCopy() {

    const num = this.pageSize * this.currentPage;

    this.temporaryTaskCopy = {}
    let len = this.temporaryTask.taskList === undefined ? 0 : this.temporaryTask.taskList.length;
    if (num < len) {
      const aa = JSON.parse(JSON.stringify(this.temporaryTask))
      aa.taskList.splice(num);
      this.temporaryTaskCopy = JSON.parse(JSON.stringify(aa))
    } else {
      this.temporaryTaskCopy = JSON.parse(JSON.stringify(this.temporaryTask))
    }


  }


  // 创建websoket
  function createWebSocket() {
    var scope = this;

    // console.log("start websocket!!!");
    var url = "ws://localhost:9091/websocket/task-chart";

    var ws = new WebSocket(url);
    ws.onopen = function () {
      console.log("连接成功!")
    }
    ws.onmessage = function (evt) {
      scope.webScoketChat = evt.data;

      // console.log(data1.toString())
    }
    ws.onclose = function () {
      // console.log("websocket 关闭,30s后重新建立连接");
      setTimeout(scope.createWebSocket, 1000 * 30);
    }

  }

  export default {
    name: 'TableStyle',
    components: {
      ClickInput,
      UserTree,
      TaskStatus,
      TaskDelivery,
      TaskDrawer,
    },
    props: {
      isAddTask: Boolean,
      searchValue: String,
      project: Object,
      rolePlus: Array,
      person: String
    },
    data() {
      return {
        userId: localStorage.getItem('id'),
        expandRowkeys: [],
        token: localStorage.getItem('token'),
        dialogVisible: {
          user: false,
          delivery: false,
          reason: false,
          taskDrawer: false,
        },
        current: {
          item: '',
          type: '',
          users: []
        },

        temporaryTask: {},

        batchDateSelector: false,
        batchDateSelectorValue: '',


        // 传递用

        currentTaskId: '',


        // 新增任务
        taskAdd: {},
        showHeader: false,


        // 多选
        checkeds: [],
        milestonesWidth: '3000px',

        // 交付物，，传递选中任务对象
        taskInfo: {},


        // 子任务懒加载
        childTaskList: [],
        loadNodeMap: new Map(),

        reasonTitle: '',
        reasonStatus: '',
        reasonTaskObject: {},
        reasonValue: '',

        dateStatusList: [],
        types: [],

        drawerTask: {},
        activeName: '',


        gaspPass: false,
        gaspRefuse: false,


        taskDeliveryTitle: '',


        milestone: '',

        // 无限滚动

        loading: false,
        currentPage: 1,
        pageSize: 20,
        temporaryTaskCopy: {},

        webScoketChat: "",

      };
    },
    methods: {
      /**
       * 初始化
       */
      listTemporaryTask,

      /**
       * 工具
       */
      cancelPopover,


      trim,
      handleSelectionChange,
      cancelSelected,
      batchDelete,
      batchUpdateEstEndTime,

      /**
       * 保存方法
       */
      saveTask,


      changeStatus,
      changePriority,

      changeRequirement,


      getUsers,
      deleteTask,
      addChildTask,

      changeOpenConditions,
      changeOpenDescription,
      changeIsEditCols,


      changeDeliverables,
      closeDelivery,

      changeIsEditType,

      load,

      changeTaskStatus,

      pullChildTask,
      arraySpanMethod,


      addNewTask,
      pushReview,
      reasonTask,
      changeReason,
      timeStatus,
      visChange,
      openDrawer,

      refreshDelivery,

      uploadSuccessJ,
      handleExceed(files, fileList) {
        this.$message.warning(`当前限制选择 20 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },
      getNewTask,


      loadMore,
      setCopy,
      bindUser,
      restart,


      expandChange(row, expandedRows) {
      },
      rowClick(row, event, column) {
      },

      createWebSocket,

    },
    created() {


      this.createWebSocket();


      const dictData = this.$store.getters.getDictionaryByKey('TASK_DATE_STATUS');
      this.dateStatusList = dictData.sysDictDataList;


      const dictDataType = this.$store.getters.getDictionaryByKey('TASK_TYPE');
      this.types = dictDataType.sysDictDataList;


      this.currentPage = 1;

      this.listTemporaryTask(this.searchValue)
    },
    watch: {
      isAddTask: function (newVal, oldVal) {
        this.temporaryTaskCopy.isEdit.Add = true;
        this.taskAdd = {
          id: '',
          name: '',
        };
      },
      searchValue: function (newVal, oldVal) {
        this.currentPage = 1;
        let initialNode = document.getElementById("content");
        initialNode.scrollTop = 0;
        this.listTemporaryTask(newVal);
      },
      person: function (newVal, oldVal) {
        this.currentPage = 1;
        let initialNode = document.getElementById("content");
        initialNode.scrollTop = 0;
        this.listTemporaryTask(this.searchValue);
      },
    },
    computed: {
      isSelected: function () {
        return {
          is_more: this.checkeds.length === 0,
          is_selected: this.checkeds.length !== 0,
        };
      },

      noMore() {
        return this.temporaryTaskCopy.taskList !== undefined && this.temporaryTaskCopy.taskList.length === this.temporaryTask.taskList.length;
      },
      disabled() {
        return this.loading || this.noMore
      }

    },
    mounted() {
      this.$nextTick(() => {
        setTimeout(() => {
          console.log(this.$refs.taskTable.bodyWidth);

          const width = parseInt(this.$refs.taskTable.bodyWidth.replace('px', '')) + 20;
          this.milestonesWidth = width + 'px';
        }, 5000);
      });
    },
  };
</script>
<style>
  div .cell {
    height: 23px;
  }
</style>
<style scoped lang="scss">


  .status_style {
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    height: 31px;
    margin-top: 1px;
    margin-bottom: 1px;
    font-size: 12px;
    line-height: 31px;
  }

  .edit_input_style {
    outline: none;
    -webkit-appearance: none;
    border-radius: 0;
    border: 1px dashed #ccc;
    height: 19px;
    width: 100%;
    justify-content: center;
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
  }

  .show_style_label:hover {
    background-color: #DBE4EE;
    color: #01408B;
  }

  .is_dashed {
    height: 23px;
    line-height: 23px;
    border: 1px dashed transparent;

    &:hover {
      border: 1px dashed #ccc;
    }
  }

  .is_selected {
    width: 24px;
  }

  .is_selected #check-box {
    display: block;
  }

  .is_more {
    width: 5px;

    &:hover {
      width: 24px;
    }

    &:hover #check-box {
      display: block;
    }
  }

  #check-box {
    display: none;
  }


  .text_center {
    text-align: center;
  }

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

  .table-container {
    height: 100%;
    flex: 1;
    overflow: auto;
    position: relative;
    display: flex;
    flex-direction: column;
  }

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
<style lang="scss">
  .el-input--mini .el-input__icon {
    line-height: 23px;
  }

  .el-date-editor.el-input, .el-date-editor.el-input__inner {
    width: 116px;
  }

  .el-input--mini .el-input__inner {
    height: 23px;
    line-height: 23px;
  }

  .task-table {
    overflow: visible !important;
    padding-top: 30px;
    /*margin-top: 30px;*/
  }

  .task-table > {
    .el-table__footer-wrapper, .el-table__header-wrapper {
      overflow: visible;

      .el-table__header {
        padding-right: 20px;
      }


    }

    .el-table__body-wrapper {
      .el-table__empty-block {
        height: 0px !important;
        min-height: 0px !important;
      }
    }

    .el-table--scrollable-x .el-table__body-wrapper {
      overflow: visible;

    }

    .el-table__body-wrapper {
      overflow: visible;

      .el-table__body {
        padding-right: 20px;
      }
    }


  }

  .project-temple {
    .el-table--border, .el-table--group {
      border: 0px solid #EBEEF5;
    }

    .el-table--border::after, .el-table--group::after, .el-table::before {
      background-color: transparent;
    }
  }

  .name-cell > {
    .cell {
      display: flex;
      padding-left: 0px;

      .el-table__expand-icon, .el-table__expand-icon, .el-table__expand-icon--expanded {
        width: 10px;
        margin-right: 7px;
      }
    }
  }

  .limit-height > {
    .el-select__tags {
      max-width: 260px !important;
      height: 23px;

      .el-tag, .el-tag--info, .el-tag--mini, .el-tag--light {
        height: 18px;
        line-height: 18px;
      }
    }

  }

  .name-cell {
    position: sticky !important;
    left: 44px !important;
    background-color: #FFFFFF;
    box-shadow: 0 0 8px -6px;
    z-index: 999;
  }

  .tab-cell {
    position: sticky !important;
    left: 0px !important;
    background-color: #FFFFFF;
    box-shadow: 0 0 8px -6px;
    z-index: 999;
    border-right: none !important;
  }

  .right-margin {
    margin-right: 10px;
  }

  .el-table__body tr:hover > td {
    background-color: #c6cfdf !important;
  }

  .el-table__body tr.current-row > td {
    background-color: #c6cfdf !important;
  }


</style>
<style scoped>
  .batch-actions-menu-wrapper.react-boards {
    position: fixed;
  }

  .batch-actions-menu-wrapper {
    transition: bottom 0.2s ease;
    position: absolute;
    width: 800px;
    height: 63px;
    z-index: 1000001;
    background-color: #ffffff;
    border-radius: 5px;
    box-shadow: 0px 22px 80px 12px #c5c7d0;
    flex-direction: row;
    display: flex;
    bottom: 35px;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
  }

  .batch-actions-menu-wrapper .num-of-actions_wrapper {
    width: 63px;
    color: #ffffff;
    background-color: #0085ff;
    border-radius: 5px 0px 0px 5px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    text-align: center;
    cursor: default;
  }

  .batch-actions-menu-wrapper .num-of-actions {
    font-size: 30px;
  }

  .batch-actions-menu-wrapper .batch-actions-title-section {
    display: flex;
    justify-content: center;
    flex-direction: column;
    flex: 1;
    cursor: default;
  }

  .batch-actions-menu-wrapper .batch-actions-title-section .title {
    color: #323338;
    font-size: 24px;
    padding-left: 20px;
    font-weight: 100;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 300px;
  }

  .batch-actions-title-section .pulses_dots {
    color: #333;
    padding-left: 20px;
    display: flex;
    flex-direction: row;
    align-items: center;
    height: 20px;
  }

  .batch-actions-title-section .pulses_dots .dot {
    width: 8px;
    height: 8px;
    border-radius: 20px;
    margin-right: 5px;
  }

  .num-of-actions_wrapper_cancel {
    width: 63px;
    background-color: #ffffff;
    display: flex;
    flex-direction: column;
    justify-content: center;
    text-align: center;
    cursor: pointer;
    border-radius: 0px 5px 5px 0px;
    border-left: 2px solid #C5C7D0;
  }

  .num-of-actions_wrapper_cancel:hover, .batch-actions-item:hover {
    color: #0085ff;
  }

  .batch-actions-item {
    display: flex;
    flex-direction: column;
    text-align: center;
    margin-right: 20px;
  }

  .batch-actions-item .action-name:not(.disable) {
    color: #323338;
  }

  .batch-actions-item .action-name {
    font-size: 14px;
    position: relative;
    bottom: -13px;
  }
</style>
<style>
  .el-select-dropdown__list {
    padding-bottom: 15px;
  }
</style>
