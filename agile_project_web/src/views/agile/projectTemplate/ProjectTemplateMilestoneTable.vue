<template>
  <div class="table-container project-temple" id="pro-temp-table">
    <div v-for="(item,index) in projectTemplateMilestones" :key="item.id" style="margin-top: 35px">
      <div :style="{width: milestonesWidth}" v-if="'id' in item">
        <div class="milestone">
          <div style="height: 100%;float: left;margin-right: 10px;vertical-align:top;">
            <el-popover v-show="hasEdit || hasDel"
              placement="bottom-start"
              :visible-arrow="false"
              width="200"
              trigger="hover"
              @show='popover(item)'
              @hide='popover(item)'>
              <div>
                <div class="show_style_label">
                  <el-button type="text" icon="el-icon-delete-solid" v-show="hasDel"
                    @click="del(item.id)">
                    删除
                  </el-button>
                </div>
                <div class="show_style_label"
                  v-if="item.id !== '' && index > 0 && hasEdit">
                  <el-button type="text" icon="el-icon-upload2"
                  @click="move(item.id,'up')"
                  >
                  上移
                </el-button>
                </div>
                <div class="show_style_label"
                  v-if="item.id !== '' && index < projectTemplateMilestones.length -1 && hasEdit">
                  <el-button type="text" icon="el-icon-download"
                  @click="move(item.id,'down')"
                  >
                  下移
                </el-button>
                </div>
              </div>
              <i slot="reference" :class="!item.isPopover ? 'el-icon-caret-right' : 'el-icon-caret-bottom'"
                  style="cursor: pointer;height: 100%;line-height: 32px;font-size: 15px;vertical-align:top;"></i>
            </el-popover>




          </div>
          <div style="float: left;height: 100%;float: left;margin-right: 10px;vertical-align:top;">
            <span @click="item.edit.name = true"
                  v-if="!item.edit.name || !hasEdit"
                  style="height: 100%;line-height: 32px;">{{item.name}}</span>
            <el-input type="text"
                  size="small"
                  style="vertical-align:top"
            @blur="save(item)"
            @keyup.enter.native="$event.target.blur"
            v-model="item.name"
            maxlength="12"
            v-focus="item.edit.name"
            v-if="item.edit.name && hasEdit"></el-input>
          </div>
          <div style="float: left;height: 100%;float: left;margin-right: 10px;vertical-align:top;">
            <el-tag type="info"
                    style="width: 200px;
                    text-align: center;
                    overflow: hidden;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                    height: 100%;
                    line-height: 32px;
                    vertical-align:top;
                    "
                    @click="addRole(item,'review','milestone')">
              {{item.reviewProjectRoleId===null?'':item.reviewProjectRole.roleName}}
            </el-tag>
          </div>
          <div style="float: left;height: 100%;float: left;margin-right: 10px;vertical-align:top;">
            <el-tag type="success"
                    style="width: 200px;
                    text-align: center;
                    overflow: hidden;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                    height: 100%;
                    line-height: 32px;
                    vertical-align:top;
                    "
                    @click="addRole(item,'confirm','milestone')">
              {{item.confirmProjectRoleId===null?'':item.confirmProjectRole.roleName}}
            </el-tag>
          </div>
        </div>
      </div>



      <el-table
        ref="taskTable"
        style="width:100%;"
        class="task-table"
        :data="item.taskList"
        row-key="id"
        id="agricultureTable"
        border
        default-expand-all
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
            <el-popover v-show="hasEdit || hasDel"
              placement="bottom-start"
              :visible-arrow="false"
              width="200"
              trigger="click">
              <div>
                <div class="show_style_label"  v-if="hasEdit"
                  v-show="scope.row.parentId === null || scope.row.parentId === ''">
                    <el-button type="text" icon="el-icon-menu"
                    @click="addChildTask(scope.row,item)"
                    >
                    新增子任务
                  </el-button>
                </div>
                <div class="show_style_label" v-if="hasDel">
                  <el-button type="text" icon="el-icon-delete-solid"
                    @click="delTask(scope.row.id)" >
                    删除任务
                  </el-button>
                </div>
                <div class="show_style_label"
                v-if="(scope.row.parentId === null || scope.row.parentId === '') && scope.$index > 0  && hasEdit">
                  <el-button type="text" icon="el-icon-upload2"
                  @click="moveTask(scope.row.id,'up',item.id)"
                  >
                  上移
                </el-button>
                </div>
                <div class="show_style_label"
                v-if="(scope.row.parentId === null || scope.row.parentId === '') && scope.row.index < item.taskList.length - 1 && hasEdit">
                  <el-button type="text" icon="el-icon-download"
                  @click="moveTask(scope.row.id,'down',item.id)"
                  >
                  下移
                </el-button>
                </div>
              </div>
              <i slot="reference" class="el-icon-more"
                  style="cursor: pointer;height: 23px;line-height: 23px;font-size: 15px;"></i>
            </el-popover>
          </template>
        </el-table-column>


        <el-table-column
          class-name="name-cell"
          width="400px"
          property="name"
          label="任务名称">
          <template slot="header">
            <span>{{item.name}}</span>
          </template>
          <template slot-scope="scope">
            <div style="height: 23px;line-height: 23px;display: flex;">
              <div v-if="scope.row.children.length===0&&scope.row.parentId===null" style="height: 23px;width: 16px"></div>
              <div v-show="hasEdit"
                :style="{width: item.isMouse ? '25px' : '5px'}"
                @mouseenter='mouse(item)'
                @mouseleave='mouse(item)'
                style="margin-right: 5px"
                class="is_more">
                <el-checkbox
                  style="margin-left:5px"
                  v-show="item.isMouse"
                  label="scope.row.date"
                  v-model="scope.row.checked"
                  @change="checkChange(item.taskList,scope.row)"
                ></el-checkbox>
              </div>
              {{scope.row.serialNumber}}、
              <div style="flex: 1;">
                <span
                  v-if="!scope.row.edit.name || !hasEdit"
                  style="height: 19px;line-height: 19px;border: 2px solid transparent;">
                  <span v-if="scope.row.name.length <= 20">{{scope.row.name}}</span>
                  <el-tooltip v-else class="item" effect="dark" placement="top-start">
                    <div slot="content">
                      <div style="width: 300px;word-break: break-all;">{{scope.row.name}}</div>
                    </div>
                    <span >{{scope.row.name.substring(0,19)}}....</span>
                  </el-tooltip>
                  <i class="el-icon-edit" v-if="hasEdit"
                   @click="scope.row.edit.name = true"
                   style="cursor: pointer"></i>
                </span>
                <el-input style="min-width: 300px;"
                  show-word-limit
                  v-if="scope.row.edit.name && hasEdit"
                  v-focus="scope.row.edit.name"
                  v-model="scope.row.name"
                  minlength='1'
                  @blur="saveTask(scope.row,item)"
                  @keyup.enter.native="$event.target.blur"></el-input>
              </div>
            </div>
          </template>
        </el-table-column>
        <!-- 审核 -->
        <el-table-column
          align="center"
          width="150px"
          label="责任角色">
          <template slot-scope="scope">
            <el-tag
              type="info"
              style="width: 105px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;"
              @click="scope.row.name === null || scope.row.name === '' ? '' : addRole(scope.row,'review','task')"
              disable-transitions>
              {{
                scope.row.reviewProjectRole === null? ''
                :scope.row.reviewProjectRole.roleName
              }}
            </el-tag>
          </template>
        </el-table-column>


          <!-- 确认 -->
         <el-table-column
          align="center"
          width="150px"
          label="确认角色">
          <template slot-scope="scope">
            <el-tag
              type="info"
              style="width: 105px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;"
              @click="scope.row.name === null || scope.row.name === '' ? '' : addRole(scope.row,'confirm','task')"
              disable-transitions>
              {{
                scope.row.confirmProjectRole === null? ''
                :scope.row.confirmProjectRole.roleName
              }}
            </el-tag>
          </template>
        </el-table-column>



        <!-- 开阀 -->
        <el-table-column
          prop="openStatus"
          align="center"
          label="开阀">
            <el-table-column label="条件" align="center" width="150px">
              <template slot-scope="scope">
                <div style="height: 23px;line-height: 23px;display: flex">
                  <div style="flex: 1;margin-left: 5px">
                    <div
                      v-if="!scope.row.edit.openConditions || !hasEdit"
                      @click="scope.row.name === null || scope.row.name === '' ? scope.row.edit.openConditions = false : scope.row.edit.openConditions = true"
                      class="text-style">
                      <span v-if="scope.row.openConditions === null || scope.row.openConditions.length <= 10">{{scope.row.openConditions}}</span>
                      <el-tooltip v-else class="item" effect="dark" placement="top-start">
                        <div slot="content">
                          <div style="width: 300px;word-break: break-all;">{{scope.row.openConditions}}</div>
                        </div>
                        <span >{{scope.row.openConditions.substring(0,9)}}....</span>
                      </el-tooltip>
                    </div>
                    <el-input
                      v-if="scope.row.edit.openConditions && hasEdit"
                      v-focus="scope.row.edit.openConditions"
                      v-model="scope.row.openConditions"
                      @blur="saveTask(scope.row,item)"
                      @keyup.enter.native="$event.target.blur"></el-input>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column  label="描述" align="center" width="200px">
              <template slot-scope="scope">
                <div style="height: 23px;line-height: 23px;display: flex">
                  <div style="flex: 1;margin-left: 5px">
                     <div
                      v-if="!scope.row.edit.openDescription  || !hasEdit"
                      @click="scope.row.name === null || scope.row.name === '' ? scope.row.edit.openDescription = false : scope.row.edit.openDescription = true"
                      class="text-style">
                      <span v-if="scope.row.openDescription === null || scope.row.openDescription.length <= 10">{{scope.row.openDescription}}</span>
                      <el-tooltip v-else class="item" effect="dark" placement="top-start">
                        <div slot="content">
                          <div style="width: 300px;word-break: break-all;">{{scope.row.openDescription}}</div>
                        </div>
                        <span >{{scope.row.openDescription.substring(0,9)}}....</span>
                      </el-tooltip>
                    </div>
                    <el-input
                      v-if="scope.row.edit.openDescription && hasEdit"
                      v-focus="scope.row.edit.openDescription"
                      v-model="scope.row.openDescription"
                      @blur="saveTask(scope.row,item)"
                      @keyup.enter.native="$event.target.blur"></el-input>
                  </div>
                </div>
              </template>
            </el-table-column>
        </el-table-column>



        <el-table-column
          align="center"
          width="150px"
          label="提前期限">
          <template slot-scope="scope">
            <div style="height: 23px;line-height: 23px;display: flex">
              <div style="flex: 1;margin-left: 5px">
                  <div
                  v-if="!scope.row.edit.aheadDay || !hasEdit"
                  @click="scope.row.name === null || scope.row.name === '' ? scope.row.edit.aheadDay = false : scope.row.edit.aheadDay = true"
                  class="text-style">
                  {{scope.row.aheadDayName === null || scope.row.aheadDayName === '' ? '提前期限' : scope.row.aheadDayName}}
                </div>
                  <el-select
                    class="edit_input_style"
                    style="border: 1px #ccc dashed;height: 19px;font-size: 18px"
                    v-model="scope.row.aheadDay"
                    filterable
                    allow-create
                    default-first-option
                    placeholder="提前期限"
                    v-if="scope.row.edit.aheadDay && hasEdit"
                    v-focus2="scope.row.edit.aheadDay"
                    @change="saveTask(scope.row,item)"
                    >
                    <el-option
                      v-for="item in aheadDays"
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
          align="center"
          width="350px"
          label="类型">
          <template slot-scope="scope">
            <div style="height: 23px;line-height: 23px;display: flex">
              <div style="flex: 1;margin-left: 5px">
                <el-tag
                  type="info"
                  style="min-width: 105px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;"
                  v-if="!scope.row.edit.type  || !hasEdit"
                  @click="scope.row.name === null || scope.row.name === '' ? scope.row.edit.type = false : scope.row.edit.type = true"
                  disable-transitions
                  :disabled="scope.row.name === null || scope.row.name === ''">
                  {{scope.row.typeName === null || scope.row.typeName === '' ? '任务类型' : scope.row.typeName}}
                </el-tag>
                <el-select
                  class="limit-height"
                  style="height: 23px!important;line-height: 23px;width: 290px"
                  v-model="scope.row.type"
                  multiple
                  placeholder="请选择"
                  size="mini"
                  v-if="scope.row.edit.type && hasEdit"
                  @visible-change="visibleChangeTask($event,scope.row,item)"
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
          align="center"
          width="150px"
          label="交付要求">
          <template slot-scope="scope">
            <div style="height: 23px;line-height: 23px;display: flex">
              <div style="flex: 1;margin-left: 5px">
                  <el-checkbox v-model="scope.row.isRequirement" @change="saveTask(scope.row,item)"
                  :disabled="scope.row.name === null || scope.row.name === ''|| !hasEdit"></el-checkbox>
              </div>
            </div>
          </template>
        </el-table-column>





        <template v-for="(item,index) in projectTemplateAttrs">
           <el-table-column :key="item.id"
            show-overflow-tooltip
            align="center"
            width="140px">
            <template slot="header" slot-scope="scope">
              <div :key="item.id" style="max-width:140px;height: 23px;line-height: 23px;">
                <p
                  style="height: 20px;line-height: 20px;margin: 0;"
                  class="text_center is_dashed"
                  v-if="!item.isEditing || !hasEdit"
                  @click="item.isEditing = true" >{{item.name}}
                  <i class="el-icon-circle-close" v-if="hasDel"
                     @click.stop="deleteAttr(item.id)"
                     style="cursor: pointer"></i>
                </p>
                <el-input :key="index"
                  style="padding-left: 0px;padding-right: 0;line-height: 23px;height: 23px"
                  v-if="item.isEditing && hasEdit"
                  v-model="item.name"
                  @blur="updateArry(item)"
                  @keyup.enter.native="$event.target.blur"></el-input>
              </div>
            </template>

            <template slot-scope="scope">
              <div v-for="(taskData,index) in scope.row.taskDataList.filter((i)=>i.projectTemplateAttrId === item.id)"
                   :key="index">
                 <div v-if="taskData.projectTemplateAttrType === 'INPUT' || taskData.projectTemplateAttrType === 'NUMBER' ">
                  <p
                    v-if="!taskData.isEditing || !hasEdit"
                    @click="scope.row.name === null || scope.row.name === '' ? taskData.isEditing = false :taskData.isEditing = true"
                    class="text-style">
                    <span v-if="taskData.value === null || taskData.value.length <= 10">{{taskData.value}}</span>
                    <el-tooltip v-else class="item" effect="dark" placement="top-start">
                      <div slot="content">
                        <div style="width: 300px;word-break: break-all;">{{taskData.isEditing}}</div>
                      </div>
                      <span >{{taskData.value.substring(0,9)}}....</span>
                    </el-tooltip>
                  </p>

                  <el-input
                    :type="taskData.attrType === 'NUMBER'? 'number':'text'"
                    v-model="taskData.value" :key="index"
                    size="mini"
                    v-if="taskData.isEditing && hasEdit"
                    v-focus="taskData.isEditing "
                    @blur="saveTaskData(taskData)"
                    @keyup.enter.native="$event.target.blur"></el-input>

                </div>

                <el-tag
                  v-if="taskData.projectTemplateAttrType === 'PERSON'"
                  type="success"
                  style="min-width: 55px;"
                  @click="scope.row.name === null || scope.row.name === '' ? '' : addRole(taskData,'','taskData')"
                  disable-transitions>
                  {{taskData.value}}
                </el-tag>

                <el-date-picker
                  v-if="taskData.projectTemplateAttrType === 'DATE'"
                  v-model="taskData.value"
                  type="date"
                  format="yy/MM/dd"
                  value-format="yyyy-MM-dd HH:mm"
                  @change="saveTaskData(taskData)"
                  :disabled="scope.row.name === null || scope.row.name === '' || !hasEdit"
                  placeholder="选择日期">
                </el-date-picker>


                <task-status v-if="taskData.projectTemplateAttrType === 'STATUS'" dict-code="TASK_STATUS" v-model="taskData.value"
                  position="bottom" width="150"
                  :hasArrow="true" @update="changeStatusData" :isShow="!hasEdit"
                  :item="taskData"></task-status>

                <el-upload v-if="taskData.projectTemplateAttrType === 'FILE'"
                  action="/api/upload"
                  :headers="{
                    token: token
                  }"
                  :disabled="scope.row.name === null || scope.row.name === '' "
                  :on-success="uploadSuccess">
                    <el-button type="text" icon="el-icon-upload"
                      v-if="(taskData.value === null || taskData.value === '' )  && hasEdit"
                      @click="clickUpload(taskData)">
                    </el-button>
                    <div v-if="taskData.value !== ''" @click="clickUpload(taskData)">
                      {{
                        taskData.value !== null &&  taskData.value !== '' ?
                        taskData.value.substring(taskData.value.lastIndexOf("/") + 1,taskData.value.length)
                        : ''
                      }}
                    </div>
                </el-upload>
              </div>
            </template>
          </el-table-column>
        </template>

        <el-table-column v-if="hasEdit"
          align="center"
          width="45px">
          <template slot="header">
            <add-popover dict-code="TASK_ADD_TYPE"
              v-model="pid"
              position="bottom-end" width="150"
              :authority="true"
              @update="addArry"
              ></add-popover>
         </template>
        </el-table-column>
      </el-table>

      <div :style="{width: milestonesWidth}" v-if="hasEdit">
        <div class="milestone"
             style="font-size: 12px;display: flex;border-bottom: 1px solid #E6E9EF;height: 35px">
          <div style="width: 44px;height: 10px"></div>
          <div
            style="height: 30px;line-height: 30px;width: 100%;background-color: #FFFFFF;z-index: 999;flex: 1;margin-top: 3px">
            <div
              class="edit_input_style is_dashed"
              :style="'margin-top: 3px;' + (item.id === '' ? 'pointer-events: none;' : '')"
              v-if="!item.addTask[0].isAdd"
                @click="item.addTask[0].isAdd = true"><i class="el-icon-plus"></i>Add
            </div>
             <el-input
                v-if="item.addTask[0].isAdd"
                v-focus="item.addTask[0].isAdd"
                v-model="item.addTask[0].name"
                @blur="addTask(item.addTask[0],item)"
                @keyup.enter.native="$event.target.blur"></el-input>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
        title="选择角色"
        @close="closeRole"
        :visible.sync="roleDialogVisible"
        width= "600px">
        <role-transfer
        :rolesProp="roles"
        :isSingle='true'
        @getRoles="getRoles"
        @getCancel="closeRole">
        </role-transfer>
      </el-dialog>


      <div class="batch-actions-menu-wrapper react-boards"
          style="left: 560px;width: 800px"
          v-if="checkedTaskIds.length > 0">
        <div class="num-of-actions_wrapper">
          <div class="num-of-actions">{{checkedTaskIds.length}}</div>
        </div>
        <div class="batch-actions-title-section">
          <div class="title">Task selected</div>
          <div class="pulses_dots">
            <div class="dot" style="background: rgb(253, 171, 61);"></div>
          </div>
        </div>
        <div class="batch-actions-item">
          <i class="el-icon-delete-solid" style="position: relative;top: 9px;font-size: 27px"
            @click="batchDeleteTask"></i>
          <span class="action-name">Delete</span>
        </div>
      </div>
  </div>
</template>

<script>


import RoleTransfer from '@/components/RoleTransfer';

import AddPopover from '@/components/AddPopover';

import TaskStatus from '@/components/TaskStatus';


import { _queryProjectTemplate } from '@/api/projectTempleApi';

/**
 * 工具
 */
function tableRowStyle({ row, rowIndex }) {
  return 'overflow-x: scroll';
}

// 模拟点击任何地方让popover消失(起因: Elementui Popover 嵌套在表格中的用v-model失效)
function cancelPopover() {
  document.getElementById('agricultureTable')
    .click();
}

function delMilestone(id) {

}

function save(item) {
  item.edit.name = false;
  if (item.name.replace(/(^\s*)|(\s*$)/g, "") !== '') {
    this.$emit("save",item);
  } else {
    item.edit.name = true;
    this.$message.error('名称不能为空!')
    // this.$parent.getProjectTemplateMilestones();
  }
}

/**
 * 移动
 */
function move(id,type) {
  this.$emit("move",id,type);
}
/**
 * 添加角色
 */
function addRole(item,roleType,itemType) {
  this.roleType  = roleType;
  this.item = item;
  this.itemType  = itemType;
  if (itemType !== 'taskData') {
    if(this.roleType === 'review') {
      const reviewProjectRole =
          this.item.reviewProjectRole === null ? ''
          : this.item.reviewProjectRole.roleName
      this.roles = [reviewProjectRole];
    } else if (this.roleType === 'confirm') {
      const confirmProjectRole =
        this.item.confirmProjectRole === null ? ''
        : this.item.confirmProjectRole.roleName
      this.roles = [confirmProjectRole]
    }
  } else {
    this.roles = [item.value];
  }
  this.roleDialogVisible = this.hasEdit;
}

function getRoles(data) {
  if (this.itemType !== 'taskData') {
    if(this.roleType === 'review') {
      this.item.reviewProjectRole = {
        roleName: data[0]
      };
      this.item.reviewRoleName = data[0];
    } else if (this.roleType === 'confirm') {
      this.item.confirmProjectRole = {
        roleName: data[0]
      };
      this.item.confirmRoleName = data[0];
    }
    this.roles = [];
    this.roleDialogVisible = false;
    if (this.itemType === 'milestone') {
      this.$emit("save",this.item);
    } else if(this.itemType === 'task') {
      this.$emit("saveTask",this.item, {
        projectTemplateId: this.item.projectTemplateId,
        projectTemplateMilestoneId: this.item.id,
      })
    };
  } else {
    this.item.value = data[0];
    this.saveTaskData(this.item);

  }
}

/**
 * 关闭选择
 */
function closeRole() {
  this.roles = [];
  this.roleDialogVisible = false;
}

function del(id) {
  this.$confirm('这个操作将永久删除里程碑信息，您想继续吗？', '删除模板里程碑', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    // if (id !== '') {
      
    // } else {
    //   this.$parent.getProjectTemplateMilestones();
    // }
    this.$emit("del",id);
  }).catch(() => {
    this.$message({
      type: 'info',
      message: '执行失败！',
    });
  });
}

// 任务名称修改
function  saveTask(scope, item) {
  if (scope.name !== null && scope.name.replace(/(^\s*)|(\s*$)/g, "") !== '')  {
    this.$emit("saveTask",scope, item);
  } else {
    // item.edit.name = true;
    this.$message.error('名称不能为空!')
    // this.$parent.getProjectTemplateMilestones();
  }
}
/**
 * 添加任务
 */
function addTask(scope, item) {
  scope.isAdd = false,
  this.saveTask(scope, item);
}

/**
 * 移动
 */
function moveTask(id,type,mId) {
  this.$emit("moveTask",id,type,mId);
}

async function delTask(id) {
  this.$confirm('这个操作将永久删除任务信息，您想继续吗？', '删除模板任务', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    this.cancelPopover();
    this.$emit("delTask",id);
  }).catch(() => {
    this.$message({
      type: 'info',
      message: '执行失败！',
    });
  });
}

function addChildTask(parent,item) {
  this.$confirm('这个操作将新增子任务信息，您想继续吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    this.cancelPopover();
    const num = parent.children.length;
    parent.children.push({
      id: '',
      projectTemplateId: '',
      projectTemplateMilestoneId: '',
      name: '',
      reviewRoleName: '',
      confirmRoleName: '',
      reviewProjectRole: null,
      confirmProjectRole: null,
      openConditions: '',
      openDescription: '',
      parentId: parent.id,
      aheadDay: '',
      type: [],
      isRequirement: 0,
      children: [],
      serialNumber: num + 1,
      taskDataList: parent.taskDataList,
      edit: {
        name: true,
        openConditions: false,
        openDescription: false,
        aheadDay: false,
        type: false,
      },
    });
  }).catch(() => {
    this.$message({
      type: 'info',
      message: '执行失败！',
    });
  });
}
/**
 * 添加属性
 */
function addArry(data) {
  this.saveArry({
    id: '',
    type: data.code,
    name: data.name,
  })
}
/**
 * 编辑属性
 */
function updateArry(data) {
  this.saveArry({
    id: data.id,
    type: data.type,
    name: data.name,
  });
  data.isEditing = false;
}

/**
 * 保存/更新
 */
function saveArry(data) {
  this.cancelPopover();
  if (data.name === '') {
    this.$message({
      type: 'error',
      message: '请输入名称！',
    });
  } else {
    this.$emit("saveArry",data);
  }
}


/**
 * 删除
 */
function deleteAttr(id) {
  this.$confirm('这个操作将删除任务属性，您想继续吗？', '删除模板任务属性', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    this.$emit("deleteAttr",id);
  }).catch(() => {
    this.$message({
      type: 'info',
      message: '执行失败！',
    });
  });
}


function clickUpload(data) {
  this.item = data;
}


/**
 * 上传文件
 */
function uploadSuccess(res, file) {
  if (res.code === 1200) {
    this.item.value = res.data.path;
    this.saveTaskData(this.item);
  }
}

/**
 * 保存
 */
function saveTaskData(taskData) {
  taskData.isEditing = false;
  const data = {
    id: taskData.id,
    projectTemplateTaskId: taskData.projectTemplateTaskId,
    projectTemplateAttrId: taskData.projectTemplateAttrId,
    value: taskData.value,
  }
  this.$emit("saveTaskData",data);
}

function changeStatusData(data,item) {
  item.value = data;
  this.saveTaskData(item);
}

function mouse(item) {
  let n = 0;
  item.taskList.forEach( t => {
    if (t.checked) n++;
    if (t.children !== null) {
      t.children.forEach(tc => {
        if (tc.checked) n++;
      })
    }
  });
  if (n > 0) item.isMouse = true;
  else {
    item.isMouse = !item.isMouse
  };
}


function scan(obj, row) {
  let flag = true;
  for (let i = 0; i < obj.length; i++) {
    if(row.id === obj[i].id && !row.checked) {
        return false;
    }
    if (!obj[i].checked) {
      if (obj[i].children.length > 0 && this.scan(obj[i].children, row)) {
        obj[i].checked = true;
      } else {
        flag = false;
      }
    } else {
      if (obj[i].children.length > 0 && !this.scan(obj[i].children, row)) {
        flag = false;
        obj[i].checked = false;
        return flag;
      }
    }
  }
  return flag;
}

function handleCheckAll(row, checked) {
  row.checked = checked;
  if (row.children.length > 0) {
    let that = this;
    row.children.forEach((element, i) => {
      that.handleCheckAll(row.children[i], checked);
    });
  }
}

function checkChange(datas,data) {

  console.log(data)
  if (data.children.length > 0) {
    this.handleCheckAll(data, data.checked);
  }
  this.scan(datas,data);
  this.getChecked(datas);
}





function getChecked(datas) {
  this.checkedTaskIds = [];
  datas.forEach(d => {
    if (d.checked) this.checkedTaskIds.push(d.id);
    if (d.children != null) {
      d.children.forEach(_d => {
        if (_d.checked) this.checkedTaskIds.push(_d.id);
      })
    }
  });
};

function batchDeleteTask() {
  this.$confirm('这个操作将批量删除任务信息，您想继续吗？', '批量删除模板任务', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    this.$emit("batchDeleteTask",this.checkedTaskIds);
    this.checkedTaskIds = [];
  }).catch((e) => {
    this.$message({
      type: 'info',
      message: '执行失败！',
    });
  });
}


function popover(item) {
  item.isPopover = !item.isPopover;
}

function visibleChangeTask(callback,scope, item) {
  if(!callback) {
   this.saveTask(scope,item)
  }
}


function setScrollBar() {
  this.$nextTick(function () {
      var div = document.getElementById('pro-temp-table');
      // div.style.height = window.innerHeight - 330 + 'px';
      // console.log('mounted: ', div);
      // console.log('mounted: ', window.innerHeight);
  })
}



export default {
  name: 'ProjectTemplateMilestoneTable',
  props: {
    projectTemplateMilestones: Array,
    projectTemplateAttrs: Array,
  },
  components: {
    RoleTransfer,
    AddPopover,
    TaskStatus
  },
  data () {
    return {
      token: localStorage.getItem('token'),
      milestonesWidth: '2500px',
      value: '',
      roleDialogVisible: false,
      roleType: '',
      item: {},
      itemType: '',
      roles: [],
      newTask: '',
      pid: '',
      aheadDays: [{
        code: -1,
        name: '无',
      },{
        code: 30,
        name: '提前一个月',
      },{
        code: 60,
        name: '提前二个月',
      },{
        code: 90,
        name: '提前三个月',
      },{
        code: 120,
        name: '提前四个月',
      },{
        code: 150,
        name: '提前五个月',
      },{
        code: 180,
        name: '提前六个月',
      },{
        code: 0,
        name: '里程碑截止日期',
      }],
      types: [],
      checkedTaskIds: [],
      userId: this.$store.state.id,
      hasDel: false,
      hasEdit: false,
    }
  },
  computed: {},
  created: async function () {
    const dictDataType = this.$store.getters.getDictionaryByKey('TASK_TYPE');
    this.types = dictDataType.sysDictDataList;

    const projectId = this.$route.query.id;
    const result = await _queryProjectTemplate(projectId);
    const createUserId = result.data.projectTemplate.createUserId;
    if (createUserId === this.userId) {
      this.hasDel = true;
      this.hasEdit = true;
    } else {
      const bottoms = JSON.parse(localStorage.getItem('BUTTONS'));
      this.hasDel = bottoms.filter(b => b === "PROJECT_TEMPLATE_TYPE_DEL").length > 0;
      this.hasEdit = bottoms.filter(b => b === "PROJECT_TEMPLATE_TYPE_EDIT").length > 0;
    }
  },
  beforeMount: function () {},
  mounted: function () {
    this.$nextTick(() => {
      setTimeout(() => {
        const width = parseInt(this.$refs.taskTable[0].bodyWidth.replace('px', '')) + 20;
        this.milestonesWidth = width + 'px';
      }, 5000);
    });
    this.setScrollBar();
    window.addEventListener('resize', this.setScrollBar);
  },
  beforeDestroy: function () {},
  destroyed: function () {},
  methods: {
    tableRowStyle,
    cancelPopover,
    save,
    move,
    del,
    addRole,
    closeRole,
    getRoles,
    addArry,
    updateArry,
    saveTask,
    addTask,
    delTask,
    moveTask,
    addChildTask,
    saveArry,
    deleteAttr,
    clickUpload,
    uploadSuccess,
    saveTaskData,
    changeStatusData,
    mouse,
    scan,
    checkChange,
    handleCheckAll,
    getChecked,
    batchDeleteTask,
    popover,
    visibleChangeTask,
    setScrollBar
  },
  watch: {
    projectTemplateMilestones(val) {},
    $route:async function(v) {
    const projectId = this.$route.query.id;
    const result = await _queryProjectTemplate(projectId);
    const createUserId = result.data.projectTemplate.createUserId;
    if (createUserId === this.userId) {
      this.hasDel = true;
      this.hasEdit = true;
    } else {
      const bottoms = JSON.parse(localStorage.getItem('BUTTONS'));
      this.hasDel = bottoms.filter(b => b === "PROJECT_TEMPLATE_TYPE_DEL").length > 0;
      this.hasEdit = bottoms.filter(b => b === "PROJECT_TEMPLATE_TYPE_EDIT").length > 0;
    }
    },
  },
  directives: {}
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
  div .cell {
    height: 23px;
  }
</style>
<style lang="scss" scoped>
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

  .is_hover {
    height: 23px;
    opacity: 0;
  }

  .is_hover:hover {
    opacity: 1;
  }

  .is_dashed {
    height: 23px;
    line-height: 23px;
    border: 1px dashed transparent;
  }

  .is_dashed:hover {
    border: 1px dashed #ccc;
  }

  .is_more {
    height: 23px;
    line-height: 23px;
    background-color: #3c9040;
    float: left;
    overflow: hidden;
  }

  // .is_more:hover {
  //   width: 24px;
  // }

  .text_center {
    text-align: center;
  }

  .milestone {
    z-index: 989;
    font-size: 18px;
    position: sticky;
    width: 1200px;
    left: 0;
    height: 32px;
  }

  .table-container {
    height: 100%;
    flex: 1;
    position: relative;
    display: flex;
    flex-direction: column;
    width: 100%;
  }

  .text-style {
    height: 20px;
    line-height: 20px;
    margin: 0;

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

  .name-cell {
    .cell {
      display: flex;
      padding-left: 0px;
      .el-table__expand-icon, .el-table__expand-icon, .el-table__expand-icon--expanded {
        width: 10px;
        margin-right: 7px;
      }
    }
  }

  .tab-cell {
    position: sticky !important;
    left: 0 !important;
    background-color: #FFFFFF;
    box-shadow: 0 0 8px -6px;
    z-index: 999;
  }

  .project-temple {
    .el-table--border, .el-table--group {
        border: 0px solid #EBEEF5;
    }
    .el-table--border::after, .el-table--group::after, .el-table::before {
      background-color: transparent;
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
    cursor: pointer;
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

