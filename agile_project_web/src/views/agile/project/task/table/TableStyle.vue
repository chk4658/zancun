<template>
  <div class="table-container project-temple"
       ref="mainHeight" id="content">
    <div @click="clearHighlight($event)" id="outside">
      <p style="text-align: center;" v-if="topLoading"><i
        class="el-icon-loading"></i></p>
      <div v-for="item in copyPM" :key="item.id" class="pm-table">
        <project-table-header :milestone="item.projectMilestone"
                              :authority="authority"
                              :first="firstIndex"
                              :end="endIndex"
                              :fold="item.collapsed"
                              :fold-all="projectMilestones.filter(pm=>pm.collapsed).length===0"
                              :foldup-all="projectMilestones.filter(pm=>!pm.collapsed).length===0"
                              :dateStatusList="dateStatusList"
                              :milestonesWidth="milestonesWidth"
                              :hasOnLine="project.hasOnLine"
                              :NullValuePrompt="item.NullValuePrompt"
                              :projectType="project.type"></project-table-header>
        <div id="unvisible"></div>
        <el-table
          ref="taskTable"
          class="task-table"
          :data="item.taskList"
          style="width: 100%"
          row-key="id"
          highlight-current-row
          :header-cell-style="{background:'#F5F7FA'}"
          id="agricultureTable"
          border
          :summary-method="getSummaries"
          show-summary
          lazy
          :span-method="arraySpanMethod"
          v-if="item.collapsed"
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
            <template slot="header">

              <el-checkbox style="color:#CA8830;"
                           v-model="item.allSelect"
                           :disabled="item.taskList.length===0||item.taskList.length===1"
                           @change="changeAllMilestoneChecks(item)">
              </el-checkbox>

            </template>
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
                       v-hasProOperation="'{\'key\': \'hasReBeginTask\',\'reBeginAuthority\':\'' + reBeginAuthority +'\',\'taskStatus\':\'' + scope.row.status +'\'}'">
                    <i class="el-icon-unlock" style="margin-left: 5px;margin-right: 5px"></i>
                    重新开始任务
                  </div>

                  <div class="show_style_label"
                       v-if="reBeginAuthority&&scope.row.status!=='6'"
                       @click="taskSpeed(scope.row)">
                    <i class="el-icon-odometer" style="margin-left: 5px;margin-right: 5px"></i>
                    任务催办
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
                        v-if="scope.row.taskDeliveryList.filter(i=>i.auditStatus===0||(i.auditStatus===2&&i.isReupload!==1)).length>0">
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
                       v-if="scope.row.parentId==null&&authority">
                    <i class="el-icon-menu" style="margin-left: 5px;margin-right: 5px"></i>
                    新增子任务
                  </div>

                  <div class="show_style_label" @click="letTaskToStMark(scope.row.id)"
                       v-if="authority&&scope.row.status==='6'">
                    <i class="el-icon-menu" style="margin-left: 5px;margin-right: 5px"></i>
                    设为归档任务
                  </div>

                  <div class="show_style_label" @click="deleteTask(scope)" v-if="authority">
                    <i class="el-icon-delete-solid" style="margin-left: 5px;margin-right: 5px"></i>
                    删除任务
                  </div>
                  <div
                    class="show_style_label"
                    @click="copy(scope.row.id,'PROJECT_TASK')" v-if="authority">
                    <i class="el-icon-document-copy" style="margin-left: 5px;margin-right: 5px"></i>
                    复制
                  </div>
                  <div class="show_style_label"
                       v-show="authority"
                       @click="updateToForbiddenOrNotEnabled({id:scope.row.id,type: 'PROJECT_TASK' },'enabled')">
                    <i class="el-icon-turn-off" style="margin-left: 5px;margin-right: 5px"></i>
                    不启用
                  </div>
                  <div class="show_style_label"
                       v-show="authority"
                       @click="updateToForbiddenOrNotEnabled({id:scope.row.id,type: 'PROJECT_TASK' },'forbidden')">
                    <i class="el-icon-close" style="margin-left: 5px;margin-right: 5px"></i>
                    禁用
                  </div>

                  <div class="show_style_label"
                       v-if="!('first' in scope.row)&&authority"
                       @click="moveTask({id:scope.row.id},'up')">
                    <i class="el-icon-top" style="margin-left: 5px;margin-right: 5px"></i>
                    上移
                  </div>

                  <div class="show_style_label"
                       v-if="!('end' in scope.row)&&authority"
                       @click="moveTask({id:scope.row.id},'down')">
                    <i class="el-icon-bottom" style="margin-left: 5px;margin-right: 5px"></i>
                    下移
                  </div>

                  <div class="show_style_label"
                       v-hasProOperation="'{\'taskReviewerId\':\'' + (scope.row.reviewUser===null?scope.row.reviewUser:scope.row.reviewUser.id) +'\', ' +
                      '\'key\': \'allNot\',' +
                       '\'taskConfirmerId\':\'' + (scope.row.confirmUser===null?scope.row.confirmUser:scope.row.confirmUser.id) +'\',' +
                        '\'authority\': \'' + authority +'\',' +
                         '\'onlyOne\': \'' + (('end' in scope.row&&'first' in scope.row)||scope.row.parentId!=null) +'\',' +
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
            property="dateStatus"
            label="任务名称"
            :filters="dateStatusList.filter(d => {
              const i = item.taskList.findIndex(t => t.dateStatus === d.code);
              return i > -1;
            }).map(d => {
              return {text:d.name,'value': d.code}
            })"
            :filter-method="filterHandler">
            <template slot="header">

              <span style="margin-left: 10px">任务名称</span>

            </template>
            <template slot-scope="scope">
              <div style="height: 23px;line-height: 23px;display: flex;flex: 1" v-if="scope.row.id!=='+++'">
                <div v-if="!scope.row.hasChildren" style="height: 23px;width: 16px"></div>
                <div style="height: 23px;line-height: 23px;float: left;margin-right: 5px;align-content: center"
                     :style="{backgroundColor:timeStatus(scope.row)}"
                     :class="isSelected">

                  <el-checkbox v-model="scope.row.checked"
                               id="check-box"
                               @change="handleSelectionChange(scope.row,item.projectMilestone.id)"
                               style="color:#CA8830;margin-left: 5px"></el-checkbox>

                </div>
                {{scope.row.serialNumber}}、
                <div style="flex: 1;">

                  <click-input :item="scope.row"
                               :authority="authority"
                               :hasChatTaskIds="hasChatTaskIds"
                               @chat="openDrawer"
                               @pull="pullChildTask"
                               @update="saveTask"></click-input>
                </div>
              </div>
              <div style="height: 23px;line-height: 23px;display: flex;flex: 1" v-else>
                <div style="height: 23px;width: 16px"></div>
                <div
                  class="edit_input_style is_dashed"
                  v-if="!item.projectMilestone.isEdit.Add"
                  style="cursor: pointer"
                  @click="authority&&addNewTask(item.projectMilestone)">+Add
                </div>
                <el-input
                  v-if="item.projectMilestone.isEdit.Add"
                  v-focus="item.projectMilestone.isEdit.Add"
                  v-model="taskAdd.name"
                  maxlength="35"
                  @blur="saveTask(taskAdd,item.projectMilestone)"
                  @keyup.enter.native="$event.target.blur"></el-input>

              </div>
            </template>
          </el-table-column>
          <!--        </el-table-column>-->
          <el-table-column
            align="center"
            width="140px"
            label="状态"
            property="status"
            :filters="taskStatusFilter.filter(d => {
              const i = item.taskList.findIndex(t => t.status === d.value);
              return i > -1;
            })"
            :filter-method="filterHandler">
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
              <div>
                <!--                v-if="scope.row.isRequirement===1||(scope.row.isRequirement===0&&scope.row.taskDeliveryList.length!==0)"-->
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
                <!--                    <i class="el-icon-upload" style="font-size: 15px" v-if="scope.row.taskDeliveryList.length<20"-->
                <!--                       @click="changeDeliverables(scope.row)">-->
                <!--                    </i>-->
                <!--                  </el-upload>-->
                <!--                </div>-->

                <!--                v-if="scope.row.taskDeliveryList.length!==0"-->
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
                           position="bottom" width="150" :item="scope.row" :isShow="!authority"
                           :hasArrow="true" @update="changePriority"></task-status>
            </template>
          </el-table-column>


          <el-table-column
            align="center"
            width="200px"
            label="责任角色"
            property="reviewUser.realName"
            :filters="columnFilters(item.taskList,['reviewUser','realName'])"
            :filter-method="filterHandler">
            <template slot-scope="scope">
              <el-tag
                type="info"
                style="width: 105px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;cursor: pointer"
                :style="(scope.row.reviewProjectRole === null ? 'border: 1px solid red' : '')"
                @click="authority&&addRole(scope.row,'review', 'task')"
                disable-transitions>
                {{scope.row.reviewProjectRole === null ? '' : scope.row.reviewProjectRole.roleName}}
              </el-tag>
              <el-tag
                type="success"
                style="margin-left: 10px;width: 55px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;cursor: pointer"
                @click="authority&&bindRoleUser(scope.row,'review', 'task')"
                disable-transitions>{{scope.row.reviewUser===null?'':scope.row.reviewUser.realName}}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            width="200px"
            label="确认角色"
            property="confirmUser.realName"
            :filters="columnFilters(item.taskList,['confirmUser','realName'])"
            :filter-method="filterHandler">
            <template slot-scope="scope">
              <el-tag
                type="info"
                style="width: 105px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;cursor: pointer"
                :style="(scope.row.confirmProjectRole === null ? 'border: 1px solid red' : '')"
                @click="authority&&addRole(scope.row,'confirm', 'task')"
                disable-transitions>{{scope.row.confirmProjectRole===null?'':scope.row.confirmProjectRole.roleName}}
              </el-tag>
              <el-tag
                type="success"
                style="margin-left: 10px;width: 55px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;cursor: pointer"
                @click="authority&&bindRoleUser(scope.row,'confirm', 'task')"
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
              <div style="height: 23px;line-height: 23px;">
                <el-date-picker
                  style="width: 128px;height: 21px;"
                  :style="(scope.row.estEndTime === null ? 'border: 1px solid red' : 'border: 1px solid #DCDFE6')"
                  v-model="scope.row.estEndTime"
                  type="date"
                  class="date-time-limit"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                  :readonly="!authority"
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
                    @click="authority&&changeRequirement(scope.row,1)">O</span>
                <i class="el-icon-check" v-if="scope.row.isRequirement===1"
                   @click="authority&&changeRequirement(scope.row,0)"></i>
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
                    @click="authority&&changeIsEditType(scope.row)"
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
            v-if="item.isOpen"
            label="开阀条件">
            <template slot-scope="scope">
              <div v-if="scope.row.type.indexOf('3')!==-1">
                <p
                  v-if="!scope.row.isEdit.openConditions"
                  @click="authority&&changeOpenConditions(scope.row)"
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
            v-if="item.isOpen"
            label="开阀描述">
            <template slot-scope="scope">
              <div v-if="scope.row.type.indexOf('3')!==-1">
                <p
                  v-if="!scope.row.isEdit.openDescription"
                  @click="authority&&changeOpenDescription(scope.row)"
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


          <template v-for="(item) in cols">


            <el-table-column
              align="center"
              show-overflow-tooltip
              :key="item.projectAttrId"
              :property="item.type+'---'+item.projectAttrId"
              width="150px">
              <template slot="header" slot-scope="scope">
                <div style="max-width:140px;height: 23px;line-height: 23px;">
                  <p
                    style="height: 20px;line-height: 20px;margin: 0;padding-left: 5px;padding-right: 5px;cursor: pointer"
                    class="text_center is_dashed"
                    v-if="!item.isEditCols"
                    @click="authority&&changeIsEditCols(item)">{{item.label}}
                    <i class="el-icon-circle-close"
                       v-if="authority"
                       @click.stop="deleteRow(item)"
                       style="cursor: pointer"></i>
                  </p>
                  <el-input
                    v-if="item.isEditCols"
                    style="padding-left: 0px;padding-right: 0;line-height: 23px;height: 23px"
                    v-model="item.label"
                    @blur="changeHeaderName(item)"
                    @keyup.enter.native="$event.target.blur"></el-input>
                </div>
              </template>


              <template slot-scope="scope">
                <div v-for="taskData in scope.row.taskDataList.filter((i)=>i.projectAttrId=== item.projectAttrId)"
                     :key="taskData.projectAttrId">

                  <div v-if="taskData.attrType === 'INPUT' || taskData.attrType === 'NUMBER' ">
                    <p
                      v-if="!taskData.isEditing"
                      @click="changeAttrEditIN(scope.row, taskData)"
                      style="width: 126px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;cursor: pointer"
                      class="text-style">

                      {{taskData.value}}
                    </p>

                    <el-input
                      :type="taskData.attrType === 'NUMBER'? 'number':'text'"
                      v-model="taskData.value"
                      size="mini"
                      v-if="taskData.isEditing "
                      v-focus="taskData.isEditing"
                      @blur="changeTextName(scope,item,taskData.value)"
                      @keyup.enter.native="$event.target.blur"/>

                  </div>

                  <el-tag
                    v-if="taskData.attrType === 'PERSON'"
                    type="success"
                    style="width: 55px;cursor: pointer"
                    @click="bindRoleUser(taskData,'','taskData',scope.row)"
                    disable-transitions>
                    {{taskData.value}}
                  </el-tag>


                  <el-date-picker
                    v-if="taskData.attrType === 'DATE'"
                    v-model="taskData.value"
                    style="width: 128px"
                    type="date"
                    :readonly="!(reBeginAuthority||userId===(scope.row.reviewUser===null?scope.row.reviewUser:scope.row.reviewUser.id)||userId===(scope.row.confirmUser===null?scope.row.confirmUser:scope.row.confirmUser.id))"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    @change="addTime(scope,item,taskData)"
                    placeholder="选择日期">
                  </el-date-picker>


                  <task-status v-if="taskData.attrType === 'STATUS'" dict-code="TASK_STATUS" v-model="taskData.value"
                               position="bottom" width="150" :item="scope.row" :taskData="taskData"
                               :isShow="!(reBeginAuthority||userId===(scope.row.reviewUser===null?scope.row.reviewUser:scope.row.reviewUser.id)||userId===(scope.row.confirmUser===null?scope.row.confirmUser:scope.row.confirmUser.id))"
                               :hasArrow="true" @update="changeStatusData"></task-status>

                  <el-upload v-if="taskData.attrType === 'FILE'&&(taskData.value === null || taskData.value === '')"
                             :headers="{
                            token: token
                          }"
                             action="/api/upload"
                             :show-file-list="false"
                             :on-success="uploadSuccess">
                    <i class="el-icon-upload" style="font-size: 15px"
                       v-if="reBeginAuthority||userId===(scope.row.reviewUser===null?scope.row.reviewUser:scope.row.reviewUser.id)||userId===(scope.row.confirmUser===null?scope.row.confirmUser:scope.row.confirmUser.id)"
                       @click="clickUpload(taskData,scope)">
                    </i>
                  </el-upload>
                  <a :href="'/api'+taskData.value.split('|||')[taskData.value.split('|||').length-1]" download=""
                     v-if="taskData.attrType === 'FILE'&&!(taskData.value === null || taskData.value === '')">
                    {{
                    taskData.value !== null && taskData.value !== '' ?
                    taskData.value.split('|||')[0] : ''
                    }}
                  </a>
                  <i class="el-icon-circle-close"
                     style="margin-left: 5px;cursor: pointer"
                     @click="deleteFileAdd(taskData,scope)"
                     v-if="taskData.attrType === 'FILE'&&!(taskData.value === null || taskData.value === '')&&(reBeginAuthority||userId===(scope.row.reviewUser===null?scope.row.reviewUser:scope.row.reviewUser.id)||userId===(scope.row.confirmUser===null?scope.row.confirmUser:scope.row.confirmUser.id))"></i>
                </div>


              </template>


            </el-table-column>

          </template>

          <template v-if="authority">

            <el-table-column
              align="center"
              width="45px">
              <template slot="header">

                <add-popover dict-code="TASK_ADD_TYPE"
                             v-model="current.projectId"
                             position="bottom-end" width="150"
                             @update="addRow"></add-popover>


              </template>
            </el-table-column>

          </template>

        </el-table>

      </div>

      <p style="text-align: center" v-if="bottomLoading"><i
        class="el-icon-loading"></i></p>

    </div>
    <!--      选择人员并绑定角色-->
    <el-dialog
      title="更改人选"
      @close="dialogVisible.user=false"
      :visible.sync="dialogVisible.user"
      width="1200px"
      append-to-body>
      <user-tree
        :isSingle='true'
        :usersProp='users'
        @getUsers="getUsers"
        @getCancel="dialogVisible.user=false">
      </user-tree>
    </el-dialog>

    <!--       选择角色 -->
    <el-dialog
      title="选择角色"
      @close="dialogVisible.role=false"
      :visible.sync="dialogVisible.role"
      width="600px"
      append-to-body>
      <role-transfer
        :rolesProp="roles"
        :isSingle="true"
        :rolePlus="rolePlus"
        @getRoles="getRoles"
        @refreshRole="refreshRole"
        @getCancel="dialogVisible.role=false">
      </role-transfer>
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

    <!--    处理合计-->
    <el-dialog
      :show-close="false"
      :visible.sync="dialogVisible.operation"
      width="515px"
      center
      append-to-body>

      <div v-if="false" slot="title"></div>

      <div>
        <div style="font-size: 18px">Function</div>
        <div style="line-height: 22px;margin-top: 30px;margin-bottom: 70px;cursor: pointer">
          <div style="float: left;border: #cccccc 1px solid;width: 75px;text-align: center;height: 22px;
                      border-top-left-radius: 3px;border-bottom-left-radius: 3px;"
               :style="currentOperation === 'Sum' ? 'background-color:#cccccc;color: #fff' : 'color: #626365'"
               @click="currentOperation='Sum'">Sum
          </div>
          <div style="float: left;border: #cccccc 1px solid;width: 75px;text-align: center;height: 22px;border-left: 0"
               :style="currentOperation === 'Average' ? 'background-color:#cccccc;color: #fff' : 'color: #626365'"
               @click="currentOperation='Average'">Average
          </div>
          <div style="float: left;border: #cccccc 1px solid;width: 75px;text-align: center;height: 22px;border-left: 0"
               :style="currentOperation === 'Median' ? 'background-color:#cccccc;color: #fff' : 'color: #626365'"
               @click="currentOperation='Median'">Median
          </div>
          <div style="float: left;border: #cccccc 1px solid;width: 75px;text-align: center;height: 22px;border-left: 0"
               :style="currentOperation === 'Min' ? 'background-color:#cccccc;color: #fff' : 'color: #626365'"
               @click="currentOperation='Min'">Min
          </div>
          <div style="float: left;border: #cccccc 1px solid;width: 75px;text-align: center;height: 22px;border-left: 0"
               :style="currentOperation === 'Max' ? 'background-color:#cccccc;color: #fff' : 'color: #626365'"
               @click="currentOperation='Max'">Max
          </div>
          <div style="float: left;border: #cccccc 1px solid;width: 75px;text-align: center;height: 22px;border-left: 0;
                      border-bottom-right-radius: 3px;border-top-right-radius: 3px"
               :style="currentOperation === 'Count' ? 'background-color:#cccccc;color: #fff' : 'color: #626365'"
               @click="currentOperation='Count'">Count
          </div>
        </div>
      </div>
    </el-dialog>

    <el-drawer
      title=""
      :visible.sync="dialogVisible.taskDrawer"
      :with-header="false"
      size="50%"
    >

      <task-drawer :task="drawerTask" :active="activeName" :refresh="dialogVisible.taskDrawer"></task-drawer>
    </el-drawer>

    <el-drawer
      title=""
      :visible.sync="dialogVisible.milestoneDrawer"
      :with-header="false"
      size="50%"
    >
      <milestone-drawer :milestone="milestone" :refresh="dialogVisible.milestoneDrawer"></milestone-drawer>

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
      <div class="batch-actions-item" v-if="authority">
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
          <i class="el-icon-copy-document" style="position: relative;top: 9px;font-size: 27px;cursor: pointer;"
             slot="reference"></i>
        </el-popover>
        <span class="action-name">批量更新截止时间</span>
      </div>
      <div class="batch-actions-item" v-if="!authority">
        <i class="el-icon-copy-document" style="position: relative;top: 9px;font-size: 27px;color: #9AA7BA"></i>
        <span class="action-name" style="color: #9AA7BA">批量更新截止时间</span>
      </div>
      <div class="batch-actions-item" v-if="authority">
        <i class="el-icon-delete-solid" style="position: relative;top: 9px;font-size: 27px;cursor: pointer;"
           @click="batchDelete"></i>
        <span class="action-name">删除</span>
      </div>
      <div class="batch-actions-item" v-if="!authority">
        <i class="el-icon-delete-solid" style="position: relative;top: 9px;font-size: 27px;color: #9AA7BA;"></i>
        <span class="action-name" style="color: #9AA7BA">删除</span>
      </div>
      <div class="num-of-actions_wrapper_cancel">
        <div class="num-of-actions"><i class="el-icon-close" @click="cancelSelected"></i></div>
      </div>
    </div>
  </div>
</template>
<script>
  import moment from 'moment';
  import {_taskManager} from '@/api/messageApi';
  import elementResizeDetectorMaker from "element-resize-detector";
  import {
    _saveProjectAttr,
    _getProjectInformation,
    _updateToForbiddenProjectMilestone,
    _updateToNotEnabledProjectMilestone,
    _deleteProjectMilestone,
    _upProjectMilestone,
    _downProjectMilestone,
    _copyProjectMilestone,
    _updateReferCollapsed
  } from '@/api/projectApi';
  import {_saveOrUpdateRoleUser} from '@/api/projectRoleUserApi';
  import UserTree from '@/components/UserTree.vue';
  import RoleTransfer from '@/components/RoleTransfer.vue';
  import TaskStatus from '@/components/TaskStatus';
  import {
    _saveTaskData,
    _deleteProjectAttrData,
    _updateTaskById,
    _deleteTask,
    _batchDeleteTask,
    _batchUpdateTaskTime,
    _downTask,
    _upTask,
    _copyTask,
  } from '@/api/taskApi';
  import DictPopover from '@/components/DictPopover';
  import AddPopover from '@/components/AddPopover';
  import ClickInput from '@/components/ClickInput';
  import ProjectTableHeader from '../../milestone/ProjectTableHeader';
  import TaskDelivery from './TaskDelivery';
  import TaskDrawer from "./TaskDrawer";
  import {_addTaskDelivery} from '@/api/taskDeliveryApi';
  import {_getToPut, _taskObj, _projectMilestones} from '@/utils/taskUtils';
  import MilestoneDrawer from "../../milestone/MilestoneDrawer";
  import {_letTaskToStMark} from '@/api/ArchivingTaskApi'
  import {_findHasTaskChatByUser, _updateChatRedisByTaskId} from '@/api/chatApi'




  /**
   * 表头筛选
   */


  function columnFilters(dates,columns) {
    var set = new Set();
    dates.forEach(d =>{
        var _d = d;
        var has = false;
        for (let i = 0; i < columns.length; i++) {
          if (_d != undefined && _d !== null) {
              has = true
              _d = _d[columns[i]];
          }
        }
        if (has && _d !== null) set.add(_d)
    });
    return Array.from(set).map(v => {
        return {text:v,'value': v}
    });
  }


  function filterHandler(value, row, column) {
    const property = column['property'];
    if (property === undefined) return false;
    const propertys = property.split('.');
    var d = row;
    var has = false;
    for (let i = 0; i < propertys.length; i++) {
        if (d !== undefined && d !== null) {
            d = d[propertys[i]];
            has = true;
        }
    }
    return has && d === value;
  }



  /**
   * 初始化
   * @returns {Promise<void>}
   */
  async function listInRedis(projectId, searchName) {
    const result = await _getToPut(projectId, searchName, this, this.types, this.person);
    this.childTaskList = result.childTaskList;

    const MA = result.milestoneArray;
    MA.forEach(ma => {
      ma.taskList.push({
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
        milestoneId: ma.projectMilestone.id,
        name: '',
        openConditions: null,
        openDescription: null,
        openStatus: null,
        parentId: null,
        projectId: ma.projectMilestone.projectId,
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
    })

    this.projectMilestones = MA;
    this.cols = result.cols;


    this.checkeds = [];
    if (this.childTaskList.length > 0) {
      this.childTaskList.forEach(i => i.checked = false);
    }

    this.voidPaging()

    if (this.currentPage >= this.pageArr.length) {
      this.currentPage = (this.pageArr.length - 1 < 1) ? 1 : (this.pageArr.length - 1)
    }
    this.setCopy(this.currentPage);

    // 如果一个里程碑都没有，就不能新增任务
    this.$parent.isHidden(result.milestoneArray.length);
  }

  async function listInRedis2(projectId, searchName) {
    const result = await _getToPut(projectId, searchName, this, this.types, this.person);
    this.childTaskList = result.childTaskList;

    const MA = result.milestoneArray;
    MA.forEach(ma => {
      ma.taskList.push({
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
        milestoneId: ma.projectMilestone.id,
        name: '',
        openConditions: null,
        openDescription: null,
        openStatus: null,
        parentId: null,
        projectId: ma.projectMilestone.projectId,
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
    })

    this.projectMilestones = MA;
    this.cols = result.cols;

    this.checkeds = [];
    this.projectMilestones.forEach(i => {
      i.allSelect = false
      if (i.taskList.length > 0) {
        i.taskList.forEach(j => {
          j.checked = false;
        });
      }
    });
    this.copyPM.forEach(i => {
      i.allSelect = false
      if (i.taskList.length > 0) {
        i.taskList.forEach(j => {
          j.checked = false;
        });
      }
    });
    if (this.childTaskList.length > 0) {
      this.childTaskList.forEach(i => i.checked = false);
    }
    for (let [k, v] of this.loadNodeMap) {
      const {tree, treeNode, resolve} = v;
      this.load(tree, treeNode, resolve);
    }

    this.$parent.isHidden(result.milestoneArray.length);
  }

  async function getProjectInformation(projectId, searchName) {
    const result = await _getProjectInformation(projectId, this);
    if (result === undefined) {
    } else {
      this.cols = result.cols;
      this.projectMilestones = result.projectInformation;
      this.childTaskList = result.childTaskList;
    }

    this.listInRedis(projectId, searchName);
  }


  // 模拟点击任何地方让popover消失(起因: Elementui Popover 嵌套在表格中的用v-model失效)
  function cancelPopover() {
    if (document.getElementById('agricultureTable') !== null) {
      document.getElementById('agricultureTable').click();
    } else {

      document.getElementById('unvisible').click();
    }

  }


  //去除字符串左右空格，防止新增名称为空格的里程碑或任务
  function trim(s) {
    return s.replace(/(^\s*)|(\s*$)/g, '');
  }

  function getSummaries(param) {

    const {columns, data} = param;

    const sums = [];
    columns.forEach((column, index) => {
      if (index === 0) {
        switch (this.currentOperation) {
          case "Sum":
            sums[index] = '合计';
            break;
          case "Average":
            sums[index] = '平均值';
            break;
          case "Median":
            sums[index] = '中位数';
            break;
          case "Max":
            sums[index] = '最大值';
            break;
          case "Min":
            sums[index] = '最小值';
            break;
          case "Count":
            sums[index] = '数量';
            break;
        }
        return;
      }


      if (column.property !== undefined && column.property.split('---')[0] === "NUMBER") {

        const values = data.map(item => {
          if (item.taskDataList.filter(tal => tal.projectAttrId === column.property.split('---')[1]).length === 0) {

            return null
          } else if (item.taskDataList.filter(tal => tal.projectAttrId === column.property.split('---')[1])[0].value !== '') {

            return Number(item.taskDataList.filter(tal => tal.projectAttrId === column.property.split('---')[1])[0].value)
          } else if (item.taskDataList.filter(tal => tal.projectAttrId === column.property.split('---')[1])[0].value === '') {
            return null
          }
        });


        if (!values.every(value => value === null)) {

          var list = values.filter(n => n);
          var amount = [];

          var operation = [];

          var tip = [];

          sums[index] = list.reduce((prev, curr) => {
            if (curr !== null) {
              return prev + curr;
            } else {
              return prev;
            }
          }, 0);

          amount[index] = list.reduce((prev, curr) => {
            if (curr !== null) {
              return prev + 1;
            } else {
              return prev;
            }
          }, 0);


          switch (this.currentOperation) {
            case "Sum":
              operation[index] = sums[index];
              tip[index] = "sum";
              break;
            case "Average":
              operation[index] = (sums[index] / amount[index]).toFixed(2);
              tip[index] = "avg";
              break;
            case "Median":
              operation[index] = '中位数';
              const len = list.length;
              if (len % 2 === 0) {
                operation[index] = (list[len / 2 - 1] + list[len / 2]) / 2;
              } else {
                operation[index] = list[(len - 1) / 2];
              }
              tip[index] = "med";
              break;
            case "Max":
              operation[index] = list.reduce((prev, curr) => {
                if (curr > prev) {
                  return curr;
                } else {
                  return prev;
                }
              }, values[0]);
              tip[index] = "max";
              break;
            case "Min":
              operation[index] = list.reduce((prev, curr) => {
                if (prev > curr) {
                  return curr;
                } else {
                  return prev;
                }
              }, values[0]);
              tip[index] = "min";
              break;
            case "Count":
              operation[index] = amount[index];
              tip[index] = "count";
              break;
          }

          this.$nextTick(() => {
            const _this = this;
            // let a = document.querySelector('.el-table__footer').querySelectorAll('td')[index].querySelector('.cell');

            let a = _this.$refs.taskTable.filter(tab => tab.data === data)[0].$el.children[3].children[0].children[1].children[0].children[index].children[0];

            let html = '<div style="cursor: pointer">' + operation[index] + '&nbsp;<span style="border: solid 1px #aaa">' + tip[index] + '</span></div>';
            a.innerHTML = html;


            a.onclick = function () {
              _this.dialogVisible.operation = true;
            }

          });
        } else {

          sums[index] = '';
        }
      } else {
        this.$nextTick(() => {
          const _this = this;

          let a = _this.$refs.taskTable.filter(tab => tab.data === data)[0].$el.children[3].children[0].children[1].children[0].children[index].children[0];

          let html = '';
          a.innerHTML = html;


        });
      }
    });

    return sums;
  }


  /**
   * 批量删除的div,获取选中多选框变化
   * @returns {Promise<void>}
   */

  function handleSelectionChange(row, projectMilestoneId) {
    // 改的是 选择父任务删除，得把所有子任务默认勾选上，如果子任务有没勾选上的，父任务也不用勾选上
    if (row.parentId !== null) {
      if (row.checked) {
        this.checkeds.push(row.id);
      } else {
        if (this.checkeds.indexOf(row.parentId) !== -1) {
          this.projectMilestones.filter(PM => PM.projectMilestone.id === projectMilestoneId)[0].taskList.filter(ta => ta.id === row.parentId)[0].checked = row.checked;
          this.checkeds.splice((this.checkeds.indexOf(row.parentId)), 1);
        }
        this.checkeds.splice((this.checkeds.indexOf(row.id)), 1);
      }
    } else {
      this.projectMilestones.filter(PM => PM.projectMilestone.id === projectMilestoneId)[0].taskList.filter(ta => ta.id === row.id)[0].checked = row.checked

      if (row.checked) {
        const arrSet = new Set(this.checkeds);
        if (row.hasChildren) {
          this.childTaskList.filter(ch => ch.parentId === row.id).forEach(c => {
            arrSet.add(c.id)
            c.checked = row.checked
          })
        }
        arrSet.add(row.id)
        this.checkeds = Array.from(arrSet)
      } else {
        this.checkeds.splice((this.checkeds.indexOf(row.id)), 1);
      }
    }

  }

  function changeAllMilestoneChecks(proMile) {
    if (proMile.taskList.length > 0) {
      const tem = new Set(this.checkeds);
      const checkSet = new Set();
      proMile.taskList.forEach(tl => {
        if (tl.id !== '+++') {
          tl.checked = proMile.allSelect
          checkSet.add(tl.id)
        }
      })
      this.projectMilestones.filter(PM => PM.projectMilestone.id === proMile.projectMilestone.id)[0].allSelect = proMile.allSelect
      this.projectMilestones.filter(PM => PM.projectMilestone.id === proMile.projectMilestone.id)[0].taskList.forEach(tl => {
        if (tl.id !== '+++') {
          tl.checked = proMile.allSelect
          checkSet.add(tl.id)
        }
      })
      this.childTaskList.forEach(childTask => {
        if (childTask.milestoneId === proMile.projectMilestone.id) {
          childTask.checked = proMile.allSelect
          checkSet.add(childTask.id)
        }
      })

      for (let [k, v] of this.loadNodeMap) {
        const {tree, treeNode, resolve} = v;
        this.load(tree, treeNode, resolve);
      }

      if (proMile.allSelect) {
        this.checkeds = JSON.parse(JSON.stringify(Array.from(new Set([...tem, ...checkSet]))))
      } else {
        this.checkeds = JSON.parse(JSON.stringify(Array.from(new Set([...tem].filter(x => !checkSet.has(x))))))
      }
    }


  }

  function cancelSelected() {
    this.checkeds = [];
    this.projectMilestones.forEach(i => {
      i.allSelect = false
      if (i.taskList.length > 0) {
        i.taskList.forEach(j => {
          j.checked = false;
        });
      }
    });
    this.copyPM.forEach(i => {
      i.allSelect = false
      if (i.taskList.length > 0) {
        i.taskList.forEach(j => {
          j.checked = false;
        });
      }
    });
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
      this.$parent.refreshEndTime();
      await this.listInRedis(this.current.projectId, this.searchValue);
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
        circleId: this.project.circleId,
        milestoneId: task.milestoneId,
        projectId: task.projectId,
        parentId: task.parentId,
        priority: '4',
      };
    } else {
      const result = _taskObj(task, this.gaspRefuse, this.gaspPass);
      obj = result;
    }
    if (task.id === '' && trim(task.name) === '') {
      item.isEdit.Add = false;
    } else {
      const result = await _updateTaskById(obj);
      if (result.code === 1200) {
        this.$parent.refreshEndTime();

        await this.listInRedis(this.current.projectId, this.searchValue);
        if (task.parentId !== null && task.parentId !== undefined) {
          const {tree, treeNode, resolve} = this.loadNodeMap.get(task.parentId);
          this.load(tree, treeNode, resolve);
        }
      } else if (result.code === 4227 || result.code === 4226 || result.code === 4222 || result.code === 4229) {
        // 4227, "子任务未完成（未开始、待审核、拒绝等），主任务不能通过！"
        // 4226, "子任务截止时间不能大于父任务截止时间！"
        // 4222, "父任务截止时间不能小于子任务截止时间！"
        // 4229, "子任务带条件通过时，主任务不能完全通过，可以带条件通过或拒绝！"
        await this.listInRedis(this.current.projectId, this.searchValue);
        if (task.parentId !== null && task.parentId !== undefined) {
          const {tree, treeNode, resolve} = this.loadNodeMap.get(task.parentId);
          this.load(tree, treeNode, resolve);
        }
      }
    }
  }

  function addNewTask(item) {
    item.isEdit.Add = true;
    this.taskAdd = {
      id: '',
      name: '',
      circleId: this.project.circleId,
      milestoneId: item.id,
      projectId: item.projectId,
      passReason: '',
      refuseReason: '',
    };
  }


  async function visChange(callback, task) {
    if (!callback) {
      await this.saveTask(task);
    }
  }

  /**
   * 保存里程碑
   * @returns {Promise<void>}
   */
  async function saveProjectMilestone(milestone, change) {
    milestone.name = trim(milestone.name);

    if (milestone.name === '') {
      if (milestone.type === 'NEW MILESTONE') {
        this.copyPM.splice(0, 1);
      } else {

        this.listInRedis(this.current.projectId, this.searchValue);
      }

    } else {
      milestone.flag = (change !== undefined && change === 'changeChild')
      milestone.rOrC = this.current.roleType
      const result = await _projectMilestones(milestone);


      if (result === 1200) {

        await this.listInRedis(this.current.projectId, this.searchValue);

        for (let [k, v] of this.loadNodeMap) {
          const {tree, treeNode, resolve} = v;
          this.load(tree, treeNode, resolve);
        }
      } else if (result === 4228) {
        // 4228, "里程碑截止时间不能早于任务截止时间！"
        await this.listInRedis(this.current.projectId, this.searchValue);

        for (let [k, v] of this.loadNodeMap) {

          const {tree, treeNode, resolve} = v;
          this.load(tree, treeNode, resolve);
        }
      }
    }

  }

  /**
   * 里程碑删除 上移下移 展开收起
   * @returns {Promise<void>}
   */
  async function deleteProjectMilestone(item) {

    const arr = [];
    this.copyPM.forEach(PM => arr.push(PM.projectMilestone.id));

    this.$confirm('是否删除?', '删除里程碑', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {

        this.projectMilestones.splice(arr.indexOf(item.id), 1);
        this.copyPM.splice(arr.indexOf(item.id), 1);

        const result = await _deleteProjectMilestone(item.id);

        if (result.code === 1200) {
          this.$message({
            message: '里程碑删除成功',
            type: 'success',
            center: true,
          });

          this.$parent.refreshEndTime();
          await this.listInRedis2(this.current.projectId, this.searchValue);

        }
      });


  }

  async function moveProjectMilestone(item, direction) {
    if (direction === 'up') {
      const result = await _upProjectMilestone({
        id: item.id,
      });
      if (result.code === 1200) {
        this.listInRedis(this.current.projectId, this.searchValue);
        this.$message({
          message: '上移成功',
          type: 'success',
          center: true,
        });
      }
    } else {
      const result = await _downProjectMilestone({
        id: item.id,
      });
      if (result.code === 1200) {
        this.listInRedis(this.current.projectId, this.searchValue);
        this.$message({
          message: '下移成功',
          type: 'success',
          center: true,
        });
      }
    }
  }

  async function cuttleMilestone(item, meth, num) {

    this.cancelPopover();

    if (num === 'ONE') {
      this.projectMilestones.filter(PM => PM.projectMilestone.id === item.id)[0].collapsed = (meth === 'UNFOLD');
      const arr = this.projectMilestones.map(pm => {

        return {mileId: pm.projectMilestone.id, collapsed: pm.collapsed}
      })
      localStorage.setItem('projectCollapsed', JSON.stringify(arr))
      this.copyPM.filter(PM => PM.projectMilestone.id === item.id)[0].collapsed = (meth === 'UNFOLD');
      // await _updateReferCollapsed({
      //   proOrMileId: item.id,
      //   single: num,
      //   flag: (meth === 'UNFOLD')
      // })
      if (meth === 'UNFOLD') {
        this.voidPaging();
        let initialNode = document.getElementById("content");
        initialNode.scrollTop = this.currentPage === 1 ? 0 : 1;
        this.setCopy(this.currentPage)
      } else {
        this.voidPaging();
        if (this.currentPage >= this.pageArr.length) {
          this.currentPage = (this.pageArr.length - 1 < 1) ? 1 : (this.pageArr.length - 1)
        }
        let initialNode = document.getElementById("content");
        initialNode.scrollTop = this.currentPage === 1 ? 0 : 1;
        this.setCopy(this.currentPage)
      }


    } else {
      if (meth === 'FOLD') {
        this.projectMilestones.forEach(PM => PM.collapsed = false);
        const arr = this.projectMilestones.map(pm => {
          return {mileId: pm.projectMilestone.id, collapsed: pm.collapsed}

        })
        localStorage.setItem('projectCollapsed', JSON.stringify(arr))
        this.copyPM = JSON.parse(JSON.stringify(this.projectMilestones))
        this.currentPage = 1;
        let initialNode = document.getElementById("content");
        initialNode.scrollTop = 0;
        this.voidPaging()
        this.setCopy(this.currentPage)
      } else {
        this.projectMilestones.forEach(PM => PM.collapsed = true);
        const arr = this.projectMilestones.map(pm => {
          return {mileId: pm.projectMilestone.id, collapsed: pm.collapsed}
        })
        localStorage.setItem('projectCollapsed', JSON.stringify(arr))
        // let initialNode = document.getElementById("content");
        // initialNode.scrollTop = 0;
        // this.currentPage = 1;
        this.voidPaging();
        let initialNode = document.getElementById("content");
        initialNode.scrollTop = this.currentPage === 1 ? 0 : 1;
        this.setCopy(this.currentPage)
      }
      // await _updateReferCollapsed({
      //   proOrMileId: item.projectId,
      //   single: num,
      //   flag: (meth === 'UNFOLD')
      // })

    }

  }


  /**
   * 批量更新截至时间
   */
  async function setBatchUdateEstEndTime(_data) {
    const data = JSON.parse(JSON.stringify(_data));
    const taskList = this.projectMilestones.filter(milestone => milestone.projectMilestone.id === data.id)[0].taskList;
    const childTaskList = this.childTaskList;
    const datas = [];
    taskList.forEach(t => {
      datas.push(t);
      const childTask = childTaskList.filter(ct => ct.parentId === t.id);
      childTask.forEach(_ct => datas.push(_ct));
    });
    const vals = [];
    datas.forEach(d => {
      if (d.id !== null && d.id !== '' && d.id !== '+++') {
        // 里程碑截至时间时间
        const estEndTime = new Date(data.estEndTime);
        // 截至时间
        const aheadDay = d.aheadDay;
        if (aheadDay === undefined || aheadDay === null || aheadDay === -1) {
          d.estEndTime = moment(estEndTime).format('YYYY-MM-DD HH:mm:ss');
        } else {
          d.estEndTime = moment(new Date(estEndTime.getTime() - (aheadDay * 86400000)))
            .format('YYYY-MM-DD HH:mm:ss');
        }
        vals.push({
          id: d.id,
          estEndTime: d.estEndTime
        });
      }
    });
    if (vals.length > 0) {
      await _batchUpdateTaskTime(vals, false);
    }
    _data.estEndTime = _data.estEndTime !== null ? new Date(_data.estEndTime) : null;
    await this.saveProjectMilestone(_data);

    this.$parent.refreshEndTime();
  }

  /**
   * 保存角色用户
   */
  async function saveRoleUser(roleUser) {
    const obj = {
      projectRoleId: roleUser.projectRoleId,
      userId: roleUser.userId,
    };
    const result = await _saveOrUpdateRoleUser(obj);
    if (result.code === 1200) {
      await this.listInRedis(this.current.projectId, this.searchValue);

      for (let [k, v] of this.loadNodeMap) {
        const {tree, treeNode, resolve} = v;
        this.load(tree, treeNode, resolve);
      }

      this.$message({
        message: '更新成功',
        type: 'success',
        center: true,
      });

      this.refreshRole()
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

  async function closeDelivery() {

    this.dialogVisible.delivery = false;
    await this.listInRedis(this.current.projectId, this.searchValue);

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

  /**
   * 显示角色选择框
   */
  async function addRole(item, roleType, itemType) {
    this.roles = [];
    this.current.item = item;
    this.current.type = itemType;
    this.current.roleType = roleType;
    if (roleType === 'review') {
      this.roles = item.reviewProjectRole === null ? [] : item.reviewProjectRole.roleName.split(',');
    }
    if (roleType === 'confirm') {
      this.roles = item.confirmProjectRole === null ? [] : item.confirmProjectRole.roleName.split(',');
    }
    this.dialogVisible.role = true;
  }

  /**
   * 保存角色
   *
   */
  function getRoles(data) {

    if (this.current.roleType === 'review') {
      this.current.item.reviewRoleName = data[0];
      this.current.item.confirmRoleName = this.current.item.confirmProjectRole === null ? '' : this.current.item.confirmProjectRole.roleName;
    }
    if (this.current.roleType === 'confirm') {
      this.current.item.confirmRoleName = data[0];
      this.current.item.reviewRoleName = this.current.item.reviewProjectRole === null ? '' : this.current.item.reviewProjectRole.roleName;
    }
    this.dialogVisible.role = false;
    if (this.current.type === 'task') {
      this.saveTask(this.current.item);
    } else if (this.current.type === 'milestone') {
      const name = (this.current.roleType === 'review') ? '负责' : '审核'
      this.$confirm('这个操作将同步该里程碑下所有任务的' + name + '角色，您想继续吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.saveProjectMilestone(this.current.item, 'changeChild');
      }).catch(() => {
      });
    }

  }


  /**
   * 角色绑定用户
   */
  async function bindRoleUser(item, roleType, itemType, row) {
    this.users = [];
    this.current.item = item;
    this.current.type = itemType;
    this.current.roleType = roleType;
    if (itemType !== 'taskData') {
      if (roleType === 'review') {
        if (item.reviewProjectRole === null) {
          this.$message({
            message: '请先选择责任人角色',
            type: 'warning',
            center: 'true',
          });
        } else if (item.reviewProjectRole !== null && (item.reviewProjectRole.roleName === '圈长' || item.reviewProjectRole.roleName === '项目经理')) {
          this.$message({
            message: '圈长/项目经理不可修改',
            type: 'warning',
            center: 'true',
          });
        } else {
          this.current.roleId = item.reviewProjectRoleId;
          if (item.reviewUser !== null) {
            this.users.push(item.reviewUser);
          }
          this.dialogVisible.user = true;
        }
      } else {
        if (item.confirmProjectRole === null) {
          this.$message({
            message: '请先选择审核人角色',
            type: 'warning',
            center: 'true',
          });
        } else if (item.confirmProjectRole !== null && (item.confirmProjectRole.roleName === '圈长' || item.confirmProjectRole.roleName === '项目经理')) {
          this.$message({
            message: '圈长/项目经理不可修改',
            type: 'warning',
            center: 'true',
          });
        } else {
          this.current.roleId = item.confirmProjectRoleId;
          if (item.confirmUser !== null) {
            this.users.push(item.confirmUser);
          }
          this.dialogVisible.user = true;
        }
      }
    } else {

      const taskReviewerId = row.reviewUser === null ? row.reviewUser : row.reviewUser.id;

      const taskConfirmerId = row.confirmUser === null ? row.confirmUser : row.confirmUser.id;

      if (this.reBeginAuthority || this.userId === taskConfirmerId || this.userId === taskReviewerId) {
        this.users = [item.value];
        this.dialogVisible.user = true;
      }


    }
  }


  // 获得审核人/确认人id  绑定
  async function getUsers(data) {
    if (this.current.type !== 'taskData') {
      if (data !== null && data.length > 0) {
        const obj = {
          projectRoleId: this.current.roleId,
          userId: data[0].id,
        };
        this.saveRoleUser(obj);
      } else {
        const obj = {
          projectRoleId: this.current.roleId,
          userId: null,
        };
        this.saveRoleUser(obj);
      }
    } else {
      if (data !== null && data.length > 0) {
        const obj = {
          taskId: this.current.item.taskId,
          projectAttrId: this.current.item.projectAttrId,
          value: data[0].realName,
        };
        this.saveTaskData(obj);
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
          this.$parent.refreshEndTime();
          this.$message({
            type: 'success',
            message: '删除成功!',
            center: 'true',
          });
          await this.listInRedis(this.current.projectId, this.searchValue);

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

  async function moveTask(data, direction) {
    this.cancelPopover();
    let result;
    let tip;
    if (direction === 'up') {
      tip = '上移成功'
      result = await _upTask(data);
    } else {
      tip = '下移成功'
      result = await _downTask(data);
    }

    if (result.code === 1200) {
      await this.listInRedis(this.current.projectId, this.searchValue);
      for (let [k, v] of this.loadNodeMap) {
        const {tree, treeNode, resolve} = v;
        this.load(tree, treeNode, resolve);
      }
      this.$message({
        message: tip,
        type: 'success',
        center: true,
      });
    }
  }

  // 新增子任务
  function addChildTask(item) {
    this.cancelPopover();
    item.hasChildren = true;
    this.childTaskList.push(this.getNewTask(item));
    const {tree, treeNode, resolve} = this.loadNodeMap.get(item.id);
    this.expandRowkeys = [tree.id];
    console.log({tree, treeNode, resolve})
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
    await this.listInRedis(this.current.projectId, this.searchValue);
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
      if (((task.status === '2' || task.status === '4') && task.taskDeliveryList.filter(i => i.auditStatus === 0).length > 0 && task.taskDeliveryList.filter(i => i.auditStatus === 2 && (i.reuploadList === null || i.reuploadList === '')).length === 0)
        || (task.status === '5' && task.taskDeliveryList.filter(i => i.auditStatus === 0).length !== 0)) {
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

  async function changeHeaderName(item) {
    item.isEditCols = false;
    await _saveProjectAttr({
      id: item.projectAttrId,
      name: item.label,
    });
  }

  async function deleteRow(item) {
    this.$confirm('即将删除此列，您想继续吗？', '删除新增列', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        const result = await _deleteProjectAttrData(item.projectAttrId);
        if (result.code === 1200) {


          this.$message({
            type: 'success',
            message: '删除成功!',
            center: 'true',
          });

          await this.listInRedis(this.current.projectId, this.searchValue);
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

  async function addRow(item) {
    this.cancelPopover();
    // const index = this.cols.filter(c => c.type === item.code).length + 1;
    const result = await _saveProjectAttr({
      projectId: this.current.projectId,
      type: item.code,
      name: item.name,
    });
    if (result.code === 1200) {
      await this.listInRedis(this.current.projectId, this.searchValue);

      for (let [k, v] of this.loadNodeMap) {
        const {tree, treeNode, resolve} = v;
        this.load(tree, treeNode, resolve);
      }

      this.$message({
        message: '成功新增' + item.name + '列',
        type: 'success',
        center: true,
      });
    }
  }


  /**
   * 新增属性
   * 保存TaskData
   */
  async function saveTaskData(taskData) {

    const obj = {
      taskId: taskData.taskId,
      projectAttrId: taskData.projectAttrId,
      value: taskData.value,
    };

    const result = await _saveTaskData(obj);
    if (result.code === 1200) {
      await this.listInRedis(this.current.projectId, this.searchValue);

      for (let [k, v] of this.loadNodeMap) {
        const {tree, treeNode, resolve} = v;
        this.load(tree, treeNode, resolve);
      }
    }
  }

  function changeTextName(scope, item, i) {
    const obj = {
      taskId: scope.row.id,
      projectAttrId: item.projectAttrId,
      value: i === undefined ? this.forText : i,
    };
    this.saveTaskData(obj);
  }

  function changeStatusData(val, a, b) {
    const obj = {
      taskId: a.id,
      projectAttrId: b.projectAttrId,
      value: val,
    };
    this.saveTaskData(obj);
  }

  function addTime(scope, item, taskData) {
    const obj = {
      taskId: scope.row.id,
      projectAttrId: item.projectAttrId,
      value: taskData.value,
    };
    this.saveTaskData(obj);

  }

  function uploadSuccess(res, file) {

    const obj = {
      taskId: this.tId,
      projectAttrId: this.attrId,
      value: file.name + '|||' + res.data.path,
    };
    this.saveTaskData(obj);
  }

  function clickUpload(data, scope) {
    this.attrId = data.projectAttrId;
    this.tId = scope.row.id;
  }

  function deleteFileAdd(_taskData, scope) {
    const obj = {
      taskId: scope.row.id,
      projectAttrId: _taskData.projectAttrId,
      value: '',
    };
    this.saveTaskData(obj);
  }


  function addProjectMilestone() {
    const obj = {
      projectMilestone: {
        name: '',
        projectId: this.current.projectId,
        isEdit: {
          Add: false,
          name: true,
          endTime: false,
        },
        circleId: this.project.circleId,
        reviewProjectRole: null,
        confirmProjectRole: null,
        reviewRoleName: null,
        confirmRoleName: null,
        type: 'NEW MILESTONE',
      },
      taskList: [],
      collapsed: true,
    };
    this.currentPage = 1;
    let initialNode = document.getElementById("content");
    initialNode.scrollTop = 0;
    this.setCopy(this.currentPage);
    this.copyPM.unshift(obj);
  }


  function load(tree, treeNode, resolve) {
    setTimeout(() => {
      tree.hasChildren = true;
      const childTaskList =
        this.childTaskList.filter(childTask => childTask.parentId === tree.id).sort((a, b) => {
          return a.sort < b.sort ? -1 : 1;
        });
      childTaskList.forEach((item, index) => this.$set(item, "serialNumber", index + 1))
      if (childTaskList.length > 0) {
        this.$set(childTaskList[0], 'first', true);
        this.$set(childTaskList[childTaskList.length - 1], 'end', true);
      }
      tree.children = childTaskList;
      this.loadNodeMap.set(tree.id, {
        tree,
        treeNode,
        resolve,
      });

      resolve(tree.children);
    }, 0);
  }


  /**
   * 不启用或禁用
   */
  function updateToForbiddenOrNotEnabled(data, type) {
    this.$confirm('这个操作将' + (
      type === 'forbidden' ? '禁用' : '不启用'
    ) + '该条数据，您想继续吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        if (type === 'forbidden') {
          await _updateToForbiddenProjectMilestone(data);
        } else if (type === 'enabled') {
          await _updateToNotEnabledProjectMilestone(data);
        }
        await this.listInRedis(this.current.projectId, this.searchValue);
        for (let [k, v] of this.loadNodeMap) {
          const {tree, treeNode, resolve} = v;
          this.load(tree, treeNode, resolve);
        }
      })
      .catch(() => {
        this.$message({
          type: 'info',
          message: '执行失败！',
        });
      });
  }

  function treeLoad() {
    for (let [k, v] of this.loadNodeMap) {
      const {tree, treeNode, resolve} = v;
      this.load(tree, treeNode, resolve);
    }
  }

  // 时间状态

  function timeStatus(item) {
    const task = this.dateStatusList.filter((dateStatus => dateStatus.code === item.dateStatus))[0];

    return task === undefined ? '#ccc' : task.color;
  }

  async function openDrawer(data) {

    this.activeName = 'taskChat';
    this.dialogVisible.taskDrawer = true;
    this.drawerTask = data;
    const result = await _updateChatRedisByTaskId({taskId: data.id});
    const i = this.hasChatTaskIds.findIndex(id => id === data.id);
    if (i > -1) this.hasChatTaskIds.splice(i, 1);

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
    this.$parent.refreshDelivery()

  }

  function refreshRole() {
    this.$parent.refreshRole()
  }

  /**
   * 复制
   */
  async function copy(id, type) {
    this.cancelPopover();
    let result
    if (type === 'PROJECT_MILESTONE') {
      result = await _copyProjectMilestone(id);
    } else if (type === 'PROJECT_TASK') {
      result = await _copyTask(id);
    }
    if (result.code === 1200) {
      this.$message({
        message: '复制成功',
        type: 'success',
        center: 'true',
      });
      await this.listInRedis(this.current.projectId, this.searchValue);

      for (let [k, v] of this.loadNodeMap) {
        const {tree, treeNode, resolve} = v;
        this.load(tree, treeNode, resolve);
      }
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
      projectId: this.taskInfo.projectId,
      deliverySize: file.size,
      submitAt: moment(new Date()).format('YYYY-MM-DD HH:mm:ss')
    });
    if (result.code === 1200) {

      this.listInRedis(this.current.projectId, this.searchValue);

      for (let [k, v] of this.loadNodeMap) {
        const {tree, treeNode, resolve} = v;
        this.load(tree, treeNode, resolve);
      }
      this.refreshDelivery();
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

  // 自定义字段的权限，单独拿出来写

  function changeAttrEditIN(item, taskData) {

    const taskReviewerId = item.reviewUser === null ? item.reviewUser : item.reviewUser.id;

    const taskConfirmerId = item.confirmUser === null ? item.confirmUser : item.confirmUser.id;

    if (this.reBeginAuthority || this.userId === taskConfirmerId || this.userId === taskReviewerId) {
      taskData.isEditing = true;
    }

  }

  function showMilestoneLog(milestone) {

    this.milestone = milestone;

    this.dialogVisible.milestoneDrawer = true;
  }

  function voidPaging() {
    const pages = [];
    let page = 1;
    let iTip = 0;
    const num = this.pageSize;
    let temp = 0;
    for (let i = 0; i < this.projectMilestones.length; i++) {
      const isCollLen = this.projectMilestones[i].collapsed ? this.projectMilestones[i].taskList.length : 0;
      temp = temp + isCollLen;
      if (temp >= num) {
        temp = 0;
        const projectMilestoneIds = []
        for (let k = iTip; k <= i; k++) {
          projectMilestoneIds.push(this.projectMilestones[k].projectMilestone.id)
        }
        iTip = i + 1;
        pages.push({
          page: page,
          projectMilestoneIds: projectMilestoneIds
        })
        page++
      } else if (i === this.projectMilestones.length - 1) {
        const projectMilestoneIds = []
        for (let k = iTip; k <= i; k++) {
          projectMilestoneIds.push(this.projectMilestones[k].projectMilestone.id)
        }
        pages.push({
          page: page,
          projectMilestoneIds: projectMilestoneIds
        })
      }
    }
    // 新增：如果最后一页任务太少导致未出现滚动条情况，则将最后一页拼到倒数第二页上,起码2页以上
    if (pages.length > 1) {
      const arr = pages[pages.length - 1].projectMilestoneIds;
      let specLen = 0
      arr.forEach(ar => {
        const isCollLenSpec = this.projectMilestones.filter(pm => pm.projectMilestone.id === ar)[0].collapsed ? this.projectMilestones.filter(pm => pm.projectMilestone.id === ar)[0].taskList.length : 0;
        specLen = specLen + isCollLenSpec
      })
      if (specLen < num) {
        pages[pages.length - 2].projectMilestoneIds = pages[pages.length - 2].projectMilestoneIds.concat(pages[pages.length - 1].projectMilestoneIds)
        this.pageArr = pages.slice(0, pages.length - 1)
      } else {
        this.pageArr = pages
      }
    } else {
      this.pageArr = pages
    }
    console.log(this.pageArr)
    console.log(this.projectMilestones)
  }

  function setCopy(currentPage, direction) {
    console.log(currentPage)
    if (this.pageArr.length === 1) {
      const copy = [];
      const arr = this.pageArr.filter(pa => pa.page === 1)[0].projectMilestoneIds;
      arr.forEach(ar => {
        copy.push(...this.projectMilestones.filter(pm => pm.projectMilestone.id === ar))
      })
      this.copyPM = JSON.parse(JSON.stringify(copy))
    } else if (this.pageArr.length === 0) {
      const copy = [];
      this.copyPM = JSON.parse(JSON.stringify(copy))
    } else {


      if (direction === undefined) {
        // 初始化都是加载两页，停在两页中第一个位置（默认）
        const copy = [];
        let first = this.pageArr.filter(pa => pa.page === currentPage)[0].projectMilestoneIds;
        let second = this.pageArr.filter(pa => pa.page === currentPage + 1)[0].projectMilestoneIds;

        // 假设页码为1，2，3，4，5，6，7，8
        // 组织后能到的页码为1，2，3，4，5，6，7
        const arr = first.concat(second)
        arr.forEach(ar => {
          copy.push(...this.projectMilestones.filter(pm => pm.projectMilestone.id === ar))
        })
        this.copyPM = JSON.parse(JSON.stringify(copy))
      } else if (direction === 'down') {

        // 先切再推，防止滚动条被压到底部触发监听事件

        const first = this.pageArr.filter(pa => pa.page === currentPage)[0].projectMilestoneIds;
        // first.forEach(ar => {
        //   this.copyPM.shift()
        // })

        this.copyPM.splice(0, first.length)

        const second = this.pageArr.filter(pa => pa.page === currentPage + 2)[0].projectMilestoneIds;
        const tem = []
        second.forEach(ar => {
          tem.push(...this.projectMilestones.filter(pm => pm.projectMilestone.id === ar))
        })
        this.copyPM.push(...tem)

      } else {

        const second = this.pageArr.filter(pa => pa.page === currentPage + 1)[0].projectMilestoneIds;
        // second.forEach(ar => {
        //   this.copyPM.pop()
        // })
        this.copyPM.splice(this.copyPM.length - second.length, second.length)
        const first = this.pageArr.filter(pa => pa.page === currentPage - 1)[0].projectMilestoneIds;
        const tem = []
        first.forEach(ar => {
          tem.push(...this.projectMilestones.filter(pm => pm.projectMilestone.id === ar))
        })
        this.copyPM.unshift(...tem)

      }
    }

  }

  // 任务催办
  async function taskSpeed(row) {
    this.cancelPopover();
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
    const result = await _taskManager({
      type: 'TASK',
      relationId: row.id,
      receiveUserIds: receiveUserIds,
      projectId: row.projectId,
      content: '您的\"' + this.project.projectName + '\"项目-\"' + row.name + '\"任务还未完成，' + localStorage.getItem('userName') + '催促您请及时完成!'
    })
    if (result.code === 1200) {
      this.$message.success('已发送催办邮件')
    }

  }

  // 设为归档任务
  async function letTaskToStMark(taskId) {
    this.cancelPopover()
    const result = await _letTaskToStMark({taskId})

    if (result.code === 1200) {
      await this.listInRedis(this.current.projectId, this.searchValue);

      for (let [k, v] of this.loadNodeMap) {
        const {tree, treeNode, resolve} = v;
        this.load(tree, treeNode, resolve);
      }

    }

  }

  async function getHasChatTaskIds() {
    // const result = await _findHasTaskChatByUser({projectId: this.current.projectId});
    // this.hasChatTaskIds = result.data.hasChatTaskIds;
  }


  // 创建websoket
  function createWebSocket() {
    var scope = this;
    var url = `ws://${location.hostname}:9091/websocket/${this.userId}`;
    this.ws = new WebSocket(url);
    this.ws.onopen = function () {
      console.log("连接成功!");
    }
    this.ws.onmessage = function (evt) {
      const hasChatTaskId = evt.data;
      const i = scope.hasChatTaskIds.findIndex(id => id === hasChatTaskId);
      if (i === -1) scope.hasChatTaskIds.push(hasChatTaskId);
    }
    this.ws.onclose = function () {
      console.log(url + '已经关闭');
      scope.createWebSocket();
    }

  }


  async function batchUpdateEstEndTime() {
    this.batchDateSelector = false;
    console.log(this.checkeds)
    const vals = this.checkeds.map(ch => {
      return {
        id: ch,
        estEndTime: this.batchDateSelectorValue === '' ? '' : moment(this.batchDateSelectorValue).format('YYYY-MM-DD HH:mm:ss')
      }
    })
    this.checkeds = [];
    const result = await _batchUpdateTaskTime(vals, true);
    this.batchDateSelectorValue = ''
    if (result.code === 1200) {
      await this.listInRedis(this.current.projectId, this.searchValue);
      for (let [k, v] of this.loadNodeMap) {
        const {tree, treeNode, resolve} = v;
        this.load(tree, treeNode, resolve);
      }
      this.$parent.refreshEndTime();
    }
  }

  function clearHighlight(event) {
    if (event.target === document.getElementById('outside') || Array.from(document.getElementsByClassName('pm-table')).indexOf(event.target) !== -1 || Array.from(document.getElementsByClassName('milestone')).indexOf(event.target) !== -1) {
      this.$refs.taskTable.forEach(t => {
        t.setCurrentRow()
      })
    }

  }

  export default {
    name: 'TableStyle',
    components: {
      ProjectTableHeader,
      ClickInput,
      UserTree,
      RoleTransfer,
      TaskStatus,
      AddPopover,
      TaskDelivery,
      TaskDrawer,
      MilestoneDrawer,
    },
    props: {
      isAddTask: Boolean,
      searchValue: String,
      authority: Boolean,
      reBeginAuthority: Boolean,
      project: Object,
      rolePlus: Array,
      person: String
    },
    data() {
      return {
        taskStatusFilter: this.$store.getters.getDictionaryByKey('TASK_STATUS').sysDictDataList.map(d => {
          return {text:d.name,'value': d.code}
        }),
        ws: null,
        expandRowkeys: [],
        token: localStorage.getItem('token'),
        dialogVisible: {
          role: false,
          user: false,
          delivery: false,
          reason: false,
          taskDrawer: false,
          operation: false,
          milestoneDrawer: false,
        },
        current: {
          item: '',
          projectId: '',
          type: '',
          roleType: '',
          roleId: '',
        },

        projectMilestones: [],

        // 列属性
        cols: [],


        // 传递用
        currentId: '',
        currentTaskId: '',


        // 角色和人员
        roles: [],
        users: [],

        // 新增文本格式属性
        forText: '',
        ownerDialogVisible2: false,
        taskIdTweet: '',
        projectAttrIdTweet: '',
        // 新增任务
        taskAdd: {},
        showHeader: false,
        // 里程碑角色相关
        milestone: {},
        roleDialogVisible2: false,
        addType: 'ADD_PROJECT_ATTR',

        // 新增文件
        attrId: '',
        tId: '',

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

        firstIndex: '',
        endIndex: '',

        drawerTask: {},
        activeName: '',


        gaspPass: false,
        gaspRefuse: false,

        userId: '',


        taskDeliveryTitle: '',

        currentOperation: 'Sum',
        batchDateSelector: false,
        batchDateSelectorValue: '',

        // 无限滚动
        currentPage: 1,
        pageSize: 20,
        copyPM: [],
        pageArr: [],
        hasChatTaskIds: [],
        interval: null,

        topLoading: false,
        bottomLoading: false,

      };
    },
    methods: {

      /**
       * 筛选
       */
      columnFilters,
      filterHandler,

      // webSocketHeartCheck,
      /**
       * 初始化
       */
      listInRedis,
      listInRedis2,
      createWebSocket,

      getProjectInformation,

      /**
       * 工具
       */
      cancelPopover,

      getSummaries,

      trim,
      handleSelectionChange,
      cancelSelected,
      changeAllMilestoneChecks,
      batchDelete,

      /**
       * 保存方法
       */
      saveTask,
      saveProjectMilestone,
      saveRoleUser,


      changeStatus,
      changePriority,

      changeRequirement,
      addRole,
      getRoles,
      bindRoleUser,
      getUsers,
      deleteTask,
      addChildTask,

      changeOpenConditions,
      changeOpenDescription,
      changeIsEditCols,


      changeHeaderName,
      deleteRow,
      addRow,
      deleteProjectMilestone,
      moveProjectMilestone,

      changeDeliverables,
      closeDelivery,

      changeIsEditType,

      load,

      changeTaskStatus,

      pullChildTask,
      arraySpanMethod,


      /**
       * 新增属性
       */
      changeTextName,
      changeStatusData,
      addTime,
      uploadSuccess,
      clickUpload,
      saveTaskData,


      addProjectMilestone,
      addNewTask,
      pushReview,
      reasonTask,
      changeReason,
      timeStatus,
      visChange,
      moveTask,
      openDrawer,
      cuttleMilestone,
      refreshDelivery,
      refreshRole,


      // 不启用或禁用
      updateToForbiddenOrNotEnabled,


      //批量更新截至时间
      setBatchUdateEstEndTime,
      changeAttrEditIN,
      batchUpdateEstEndTime,

      copy,
      uploadSuccessJ,
      handleExceed(files, fileList) {
        this.$message.warning(`当前限制选择 20 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },
      getNewTask,

      showMilestoneLog,

      setCopy,
      voidPaging,
      letTaskToStMark,

      getHasChatTaskIds,
      taskSpeed,
      deleteFileAdd,
      restart,
      treeLoad,
      clearHighlight,

      fnDown() {
        let _this = this;
        const scrollDiv = document.getElementById('content');
        const content = document.getElementById('outside');
        const bottom = Math.round(content.clientHeight - scrollDiv.clientHeight - scrollDiv.scrollTop)
        if (bottom === 0 && this.pageArr.length !== 0 && this.pageArr.length !== 1 && this.currentPage !== this.pageArr.length - 1) {
          console.log('xxxxxxxxxxxxxxxxxxxxxx')
          scrollDiv.removeEventListener('scroll', this.fnUp)
          this.bottomLoading = true;
          // scrollDiv.removeEventListener('scroll', this.fnDown)

          setTimeout(() => {
            this.setCopy(_this.currentPage, 'down');
            this.currentPage++;
            if (scrollDiv.scrollTop === 0) {
              scrollDiv.scrollTop = 1
            }
            this.bottomLoading = false
          }, 200)


          setTimeout(() => {
            scrollDiv.addEventListener('scroll', this.fnUp);
            // scrollDiv.addEventListener('scroll', this.fnDown)
          }, 500)


        }
      },
      fnUp() {
        let _this = this;
        const scrollDiv = document.getElementById('content');
        if (scrollDiv.scrollTop === 0 && this.currentPage !== 1) {
          this.topLoading = true
          setTimeout(() => {
            this.setCopy(_this.currentPage, 'up')
            this.topLoading = false
            this.currentPage--
            scrollDiv.scrollTop = 500;
          }, 200)
        }
      }


    },
    created() {

      localStorage.removeItem('projectCollapsed')

      this.userId = localStorage.getItem('id');

      const dictData = this.$store.getters.getDictionaryByKey('TASK_DATE_STATUS');
      this.dateStatusList = dictData.sysDictDataList;


      const dictDataType = this.$store.getters.getDictionaryByKey('TASK_TYPE');
      this.types = dictDataType.sysDictDataList;


      this.current.projectId = this.$route.query.id === undefined ? '' : this.$route.query.id;
      if (this.current.projectId !== '') {

        this.currentPage = 1

        this.getProjectInformation(this.current.projectId, '');

        if (this.projectMilestones.length > 0) {
          this.firstIndex = this.projectMilestones[0].projectMilestone.id;
          this.endIndex = this.projectMilestones[this.projectMilestones.length - 1].projectMilestone.id;
        }
        const that = this;
        this.interval = setInterval(async () => {
          that.getHasChatTaskIds();
        }, 500);
        this.createWebSocket();
      }

    },
    // destory() {
    //   debugger;
    //   clearTimeout(this.interval)
    // 	this.interval = null;
    // },
    beforeDestroy: function () {
      clearTimeout(this.interval);
      this.interval = null;
      const scrollDiv = document.getElementById('content');
      scrollDiv.removeEventListener('scroll', this.fnDown)
      scrollDiv.removeEventListener('scroll', this.fnUp)
    },
    destroyed: function () {
    },
    watch: {
      $route() {
        localStorage.removeItem('projectCollapsed')
        this.checkeds = []
        const dictData = this.$store.getters.getDictionaryByKey('TASK_DATE_STATUS');
        this.dateStatusList = dictData.sysDictDataList;

        let initialNode = document.getElementById("content");
        initialNode.scrollTop = 0;
        this.currentPage = 1;


        this.current.projectId = this.$route.query.id === undefined ? '' : this.$route.query.id;
        if (this.current.projectId !== '') {

          this.getProjectInformation(this.current.projectId, '');
          const that = this;
          this.interval = setInterval(() => {
            that.getHasChatTaskIds();
          }, 500);
          this.createWebSocket();

        }

      },
      isAddTask: function (newVal, oldVal) {
        if (this.authority) {
          const afterFold = this.copyPM.filter(PM => PM.collapsed === true);
          if (afterFold.length > 0) {
            afterFold[0].projectMilestone.isEdit.Add = true;
            this.taskAdd = {
              id: '',
              name: '',
              milestoneId: afterFold[0].projectMilestone.id,
              projectId: afterFold[0].projectMilestone.projectId,
            };
          }
        }
      },
      searchValue: function (newVal, oldVal) {
        this.currentPage = 1;
        let initialNode = document.getElementById("content");
        initialNode.scrollTop = 0;
        this.listInRedis(this.current.projectId, newVal);
      },
      person: function (newVal, oldVal) {
        this.currentPage = 1;
        let initialNode = document.getElementById("content");
        initialNode.scrollTop = 0;
        this.listInRedis(this.current.projectId, this.searchValue);
      },
      projectMilestones(val) {
        if (this.projectMilestones.length > 0) {
          this.firstIndex = this.projectMilestones[0].projectMilestone.id;
          this.endIndex = this.projectMilestones[this.projectMilestones.length - 1].projectMilestone.id;
        }
      }
    },
    computed: {
      isSelected: function () {
        return {
          is_more: this.checkeds.length === 0,
          is_selected: this.checkeds.length !== 0,
        };
      },

    },
    mounted() {
      // let _this = this
      // //获取节点
      const scrollDiv = document.getElementById('content');
      // var content = document.getElementById('outside')
      //绑定事件
      // scrollDiv.addEventListener('scroll', function () {
      //
      //   const bottom = Math.round(content.clientHeight - scrollDiv.clientHeight - scrollDiv.scrollTop)
      //
      //   if (scrollDiv.scrollTop === 0 && _this.currentPage !== 1) {
      //
      //     _this.topLoading = true
      //     _this.setCopy(_this.currentPage, 'up')
      //     _this.topLoading = false
      //     _this.currentPage--
      //
      //     scrollDiv.scrollTop = 500;
      //   }
      //
      //   if (bottom === 0 && _this.pageArr.length !== 0 && _this.pageArr.length !== 1 && _this.currentPage !== _this.pageArr.length - 1) {
      //
      //     _this.bottomLoading = true
      //     _this.setCopy(_this.currentPage, 'down');
      //     _this.bottomLoading = false
      //     _this.currentPage++
      //     console.log('adadad');
      //
      //   }
      // });
      scrollDiv.addEventListener('scroll', this.fnDown)
      scrollDiv.addEventListener('scroll', this.fnUp)


      this.$nextTick(() => {
        setTimeout(() => {
          console.log(this.$refs.taskTable[0].bodyWidth);

          const width = parseInt(this.$refs.taskTable[0].bodyWidth.replace('px', '')) + 20;
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
    /*padding-top: 30px;*/
    margin-top: 30px;
  }

  .pm-table {
    margin-top: 35px
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

  .date-time-limit > {
    .el-input__inner {
      vertical-align: top;
      height: 19px;
      line-height: 19px;
      border: 0;
    }

    .el-input__inner:focus {
      border: 0;
    }

    .el-input__prefix {
      line-height: 19px;

      .el-input__icon {
        line-height: 19px;
      }
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

  /*.current-row > td {*/
  /*  background: #C7EAFE !important*/
  /*  !*background: rgba(0, 158, 250, 0.219) !important;*!*/
  /*}*/

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
