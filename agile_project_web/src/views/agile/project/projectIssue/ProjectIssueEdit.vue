<template>
  <div style="display: flex;">

    <div style="flex: 7;">
      <el-form ref="projectIssueForm" label-position="right" label-width="150px" :model="form" style="width:650px">
        <!-- 创建时期，创建完就不需要表单验证且只读 -->
        <div>
          <el-form-item prop="projectId" :rules="[{ required: !projectIssueId, message: '请选择项目', trigger: 'blur' }]">
            <span slot="label">项目名称</span>
            <el-select v-model="form.projectId" filterable placeholder="请选择项目" size="small" clearable
                       :filter-method="filterProject"
                       :disabled="projectIssueId!==''" @change="chooseProject"
                       style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
              <el-option v-for="item in projectOptions" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item prop="projectMilestoneId"
                        :rules="[{ required: !projectIssueId, message: '请选择项目里程碑', trigger: 'blur' }]">
            <span slot="label">项目里程碑</span>
            <el-select v-model="form.projectMilestoneId" filterable placeholder="请选择项目里程碑" size="small" clearable
                       :disabled="projectIssueId!==''"
                       style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
              <el-option v-for="item in projectMilestoneIdList" :key="item.value" :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item prop="source" :rules="[{ required: !projectIssueId, message: '请选择来源', trigger: 'blur' }]">
            <span slot="label">来源</span>
            <el-select v-model="form.source" filterable placeholder="请选择来源" size="small" clearable
                       :disabled="projectIssueId!==''"
                       style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
              <el-option v-for="item in sourceList" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item prop="userName">
            <span slot="label">问题提出人</span>
            <el-input v-model="form.userName" placeholder="请选择问题提出人" size="small" readonly
                      :disabled="projectIssueId!==''"
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
            </el-input>
          </el-form-item>

          <el-form-item prop="createUserDepartmentId">
            <span slot="label">部门</span>
            <el-input v-model="form.departmentName" placeholder="请选择部门" size="small" readonly
                      :disabled="projectIssueId!==''"
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
            </el-input>
          </el-form-item>

          <el-form-item prop="createAt">
            <span slot="label">提出时间</span>
            <el-input v-model="form.createAt" placeholder="请选择提出时间" size="small" readonly
                      :disabled="projectIssueId!==''"
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
            </el-input>
          </el-form-item>


          <el-form-item prop="questionTypeId"
                        :rules="[{ required: !projectIssueId, message: '请选择问题类别', trigger: 'change' }]">
            <span slot="label">问题类别</span>
            <el-cascader @change="chooseQuestionType" clearable placeholder="请选择问题类别" :disabled="projectIssueId!==''"
                         style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px" size="small"
                         :props="{ checkStrictly: true }" v-model="form.questionTypeArr" :options="questionTypeList"/>
          </el-form-item>

          <el-form-item prop="description"
                        :rules="[{ required: !projectIssueId, message: '请输入问题描述', trigger: 'blur' }]">
            <span slot="label">问题描述</span>
            <el-input v-model="form.description" placeholder="请输入问题描述" :disabled="projectIssueId!==''"
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px" type="textarea"
                      :rows="3"></el-input>
          </el-form-item>

          <el-form-item prop="involved">
            <span slot="label">涉及区域</span>
            <el-input v-model="form.involved" placeholder="请输入涉及区域" size="small" :disabled="projectIssueId!==''"
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px"></el-input>
          </el-form-item>


          <el-form-item prop="img">
            <span slot="label">问题图片</span>
            <el-upload class="upload-width" accept="image/png,image/jpg,image/jpeg,image/gif" action="/api/upload"
                       :headers="{
                        token: token
                    }" :limit="5" :on-success="uploadSuccess" :on-exceed="handleExceed" :before-upload="beforeUpload"
                       :on-preview="handlePreviewPic" :before-remove="handleRemove" :file-list="form.fileList"
                       list-type="picture">
              <el-button size="small" type="info" style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px"
                         :disabled="projectIssueId!==''">
                点击上传&nbsp;&nbsp;&nbsp;(最大5张)
              </el-button>
              <div slot="tip" class="el-upload__tip">只能上传jpg/png/gif文件，且不超过1M</div>
            </el-upload>
          </el-form-item>
        </div>


        <!-- 项目经理填写，且项目阶段为0时 -->
        <div v-if="isVisible(0)&&((isManager&&form.stage===0)||form.stage!==0)">


          <el-form-item prop="priority" :rules="[{ required: form.stage===0, message: '请选择问题优先级', trigger: 'blur' }]">
            <span slot="label">问题优先级</span>
            <el-select v-model="form.priority" filterable placeholder="请选择问题优先级" size="small" clearable
                       style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px" :disabled="form.stage!==0">
              <el-option v-for="item in priorityList" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item prop="hasRepeat" :rules="[{ required: form.stage===0, message: '请选择是否重复发生', trigger: 'blur' }]">
            <span slot="label">是否重复发生</span>
            <el-select v-model="form.hasRepeat" filterable placeholder="请选择是否重复发生" size="small" clearable
                       :disabled="form.stage!==0" style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
              <el-option v-for="item in hasRepeatList" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item prop="analysisUserId"
                        :rules="[{ required: form.stage===0, message: '请选择问题分析人', trigger: 'change' }]">
            <span slot="label">问题分析人</span>
            <el-input v-model="form.analysisUserName" placeholder="请选择问题分析人" size="small" @focus="openUser('ANALYSIS')"
                      readonly :disabled="form.stage!==0"
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
            </el-input>
          </el-form-item>
        </div>


        <div v-if="isVisible(1)&&((isAnalysis&&form.stage===1)||form.stage!==1)">

          <el-form-item prop="cause" :rules="[{ required: form.stage===1, message: '请输入原因分析', trigger: 'blur' }]">
            <span slot="label">原因分析</span>
            <el-input v-model="form.cause" placeholder="请输入原因分析" size="small" :disabled="form.stage!==1"
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px"></el-input>


          </el-form-item>
          <el-form-item prop="shortMeasureUserId">
            <span slot="label">短期措施责任人</span>
            <el-input v-model="form.shortMeasureUserName" placeholder="请选择短期措施责任人" size="small" readonly
                      :disabled="form.stage!==1" @focus="openUser('SHORT')"
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
            </el-input>


          </el-form-item>
          <el-form-item prop="longMeasureUserId"
                        :rules="[{ required: form.stage===1, message: '请选择长期措施责任人', trigger: 'change' }]">
            <span slot="label">长期措施责任人</span>
            <el-input v-model="form.longMeasureUserName" placeholder="请选择长期措施责任人" size="small" @focus="openUser('LONG')"
                      :disabled="form.stage!==1" readonly
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
            </el-input>

          </el-form-item>

        </div>


        <div v-if="isVisible(2)&&(((isShort||isLong)&&form.stage===2)||form.stage!==2)">

          <el-form-item prop="shortMeasure"
                        :rules="[{ required: form.shortMeasureUserId!==''&&form.shortMeasureUserId!==null&&form.stage===2, message: '请输入短期措施', trigger: 'blur' }]">
            <span slot="label">短期措施</span>
            <el-input v-model="form.shortMeasure" placeholder="请输入短期措施" size="small" :disabled="form.stage!==2"
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px"></el-input>
          </el-form-item>
          <el-form-item prop="shortMeasureExpectTime"
                        :rules="[{ required: form.shortMeasureUserId!==''&&form.shortMeasureUserId!==null&&form.stage===2, message: '请选择短期措施预计时间', trigger: 'change' }]">
            <span slot="label">短期措施预计时间</span>
            <el-date-picker v-model="form.shortMeasureExpectTime" type="datetime" :clearable="false"
                            :disabled="form.stage!==2"
                            style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px"
                            size="small" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择短期措施预计时间">
            </el-date-picker>

          </el-form-item>
          <el-form-item prop="shortMeasureActualTime"
                        :rules="[{ required: form.shortMeasureUserId!==''&&form.shortMeasureUserId!==null&&form.stage===2, message: '请选择短期措施实际解决时间', trigger: 'change' }]">
            <span slot="label">短期措施实际解决时间</span>
            <!-- <el-date-picker v-model="form.shortMeasureActualTime" type="datetime" :clearable="false"
              style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px" size="small"
              :disabled="form.stage!==2" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择短期措施实际解决时间">
            </el-date-picker> -->
            <el-input v-model="form.shortMeasureActualTime" placeholder="请点击完成自动填入" :disabled="form.stage!==2"
                      style="width: 300px;font-family: 'Microsoft YaHei';font-size: 13px" size="small"
                      readonly></el-input>
            <el-button type="info" @click="quickFill('SHORT')" :disabled="form.stage!==2"
                       style="width: 100px;font-family: 'Microsoft YaHei';font-size: 13px;vertical-align: top;height: 32px;"
                       size="small">完成
            </el-button>

          </el-form-item>

        </div>


        <div v-if="(isVisible(2)&&((form.shortMeasureUserId===''||form.shortMeasureUserId===null)&&isLong&&form.stage===2))
          ||(isVisible(3)&&((form.stage===3&&isLong)||form.stage!==3))">

          <el-form-item prop="longMeasure"
                        :rules="[{ required: form.stage===2||form.stage===3, message: '请输入长期措施', trigger: 'blur' }]">
            <span slot="label">长期措施</span>
            <el-input v-model="form.longMeasure" placeholder="请输入长期措施" size="small"
                      :disabled="form.stage!==3&&form.stage!==2"
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px"></el-input>

          </el-form-item>
          <el-form-item prop="longMeasureExpectTime"
                        :rules="[{ required: form.stage===2||form.stage===3, message: '请选择长期措施预计时间', trigger: 'change' }]">
            <span slot="label">长期措施预计时间</span>
            <el-date-picker v-model="form.longMeasureExpectTime" type="datetime" :clearable="false"
                            :disabled="form.stage!==3&&form.stage!==2"
                            style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px" size="small"
                            value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择长期措施预计时间">
            </el-date-picker>


          </el-form-item>
          <el-form-item prop="longMeasureActualTime"
                        :rules="[{ required: form.stage===2||form.stage===3, message: '请选择长期措施实际解决时间', trigger: 'change' }]">
            <span slot="label">长期措施实际解决时间</span>
            <!-- <el-date-picker v-model="form.longMeasureActualTime" type="datetime" :clearable="false"
              :disabled="form.stage!==3&&form.stage!==2"
              style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px" size="small"
              value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择长期措施实际解决时间">
            </el-date-picker> -->

            <el-input v-model="form.longMeasureActualTime" placeholder="请点击完成自动填入"
                      :disabled="form.stage!==3&&form.stage!==2"
                      style="width: 300px;font-family: 'Microsoft YaHei';font-size: 13px" size="small"
                      readonly></el-input>
            <el-button type="info" @click="quickFill('LONG')" :disabled="form.stage!==3&&form.stage!==2"
                       style="width: 100px;font-family: 'Microsoft YaHei';font-size: 13px;vertical-align: top;height: 32px;"
                       size="small">完成
            </el-button>

          </el-form-item>

        </div>


        <div v-if="isVisible(4)">

          <el-form-item prop="hasClose" :rules="[{ required: form.stage===4, message: '请确定问题是否解决', trigger: 'blur' }]">
            <span slot="label">问题关闭</span>
            <el-select v-model="form.hasClose" filterable placeholder="请确定问题是否解决" size="small" clearable
                       v-if="(isCreate&&form.stage===4)||form.stage!==4" :disabled="form.stage!==4"
                       style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
              <el-option v-for="item in hasCloseList" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
            <el-input v-model="staticValue" placeholder="限问题提出人审查" readonly v-if="!isCreate&&form.stage===4"
                      size="small" style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px"></el-input>
          </el-form-item>
          <el-form-item prop="stageCopy" v-if="form.hasClose==='1'&&isManager&&form.stage===4">
            <span slot="label">返回阶段至</span></span>
            <el-select v-model="form.stageCopy" filterable placeholder="请选择阶段" size="small"
                       style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
              <el-option v-for="item in projectStages" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>

        </div>

        <div v-if="projectIssueId">
          <el-form-item prop="statusShow">
            <span slot="label">状态</span>
            <div
              style="background-color:#F2F4F5;color: #626365;text-align: center;width: 400px;height: 30px;line-height: 30px">
              {{form.statusShow}}
            </div>
          </el-form-item>
        </div>

        <div v-if="isVisible(5)&&((isManager&&form.stage===5)||form.stage!==5)">

          <el-form-item prop="hasLl" :rules="[{ required: form.stage===5, message: '请确定是否输入LL', trigger: 'blur' }]">
            <span slot="label">是否输入LL</span>
            <el-select v-model="form.hasLl" filterable placeholder="请确定是否输入LL" size="small" clearable
                       :disabled="form.stage!==5" style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px">
              <el-option v-for="item in hasLlList" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>

          </el-form-item>


          <el-form-item prop="ll" :rules="[{ required: form.hasLl===0, message: '请输入LL编号', trigger: 'blur' }]">
            <span slot="label">LL编号</span>
            <el-input v-model="form.ll" placeholder="请输入LL编号" size="small" :disabled="form.stage!==5"
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px"></el-input>

          </el-form-item>

          <el-form-item prop="remark">
            <span slot="label">备注</span>
            <el-input v-model="form.remark" placeholder="请填写备注" :disabled="form.stage!==5"
                      style="width: 400px;font-family: 'Microsoft YaHei';font-size: 13px" type="textarea"
                      :rows="3"></el-input>

          </el-form-item>


        </div>


        <el-footer style="text-align: center">
          <div style="display: inline-block"
            v-if="projectIssueId===''||(isManager&&form.stage===0&&projectIssueId!=='')||(isAnalysis&&form.stage===1)||((isShort||isLong)&&form.stage===2)||(form.stage===3&&isLong)||(isCreate&&form.stage===4)||(isManager&&form.stage===5)">
            <el-button type="primary" @click="save" v-if="!(form.hasClose==='1'&&isManager&&form.stage===4)"
                       style="font-family: 'Microsoft YaHei';font-size: 13px;width: 120px;border-radius: 2px">提交
            </el-button>
          </div>

          <el-button type="primary" @click="setProjectIssueStage" v-if="form.hasClose==='1'&&isManager&&form.stage===4"
                     style="font-family: 'Microsoft YaHei';font-size: 13px;width: 120px;border-radius: 2px">确定打回
          </el-button>
          <el-button type="primary" @click="cancel"
                     style="font-family: 'Microsoft YaHei';font-size: 13px;margin-left: 40px;width: 120px;border-radius: 2px">
            取消
          </el-button>
        </el-footer>

      </el-form>


      <el-dialog :title="userTitle" @close="closeOwner" :visible.sync="ownerDialogVisible" width="1200px"
                 append-to-body>
        <user-tree :isSingle='true' :usersProp='owners' @getUsers="getOwners" @getCancel="closeOwner">
        </user-tree>
      </el-dialog>
    </div>


    <div style="flex: 3;">
      <el-timeline :reverse="false" style="margin-left: -67px;">
        <el-timeline-item v-for="(activity, index) in activities" :key="index" :color="activity.color"
                          :timestamp="activity.timestamp">
          {{activity.content}}
        </el-timeline-item>
      </el-timeline>
    </div>

  </div>
</template>
<script>
  import {
    _queryOwnerProjectList,
  } from '@/api/projectApi';
  import {
    _getProjectIssueStage,
    _saveProjectIssue,
    _getByProjectIdAndTime,
    _getById,
    _getProjectIssueStatus,
    _setProjectIssueStage
  } from '@/api/projectIssue';
  import {_findListByProjectIssueId} from '@/api/projectIssueHistory';

  import {_getDepartment} from '@/api/sysDepartmentApi';
  import moment from "moment";
  import {
    _listCascade
  } from '@/api/sysQuestionType.js';
  import UserTree from '@/components/UserTree';
  import {_querySysUsersByIds} from '@/api/sysUserApi';

  async function getContent() {
    if (!this.projectIssueId) {
      // 一些需要自动填充的内容,创建表单时
      const userInfo = JSON.parse(localStorage.getItem('user'));
      // console.log(userInfo)
      this.form.createUserDepartmentId = userInfo.departmentId;
      this.form.createUserId = userInfo.id;
      this.form.projectId = this.projectId === undefined ? '' : this.projectId
      if (this.projectId !== undefined) {
        const result = await _getByProjectIdAndTime({
          projectId: this.form.projectId,
          time: new Date()
        })
        this.projectMilestoneIdList = result.code === 1200 ? result.data.projectMilestones.map(cp => {
          return {
            value: cp.id,
            label: cp.name
          }
        }) : []
      }
      if (userInfo.departmentId !== null) this.getDepartment(userInfo.departmentId);
      this.form.createAt = moment(new Date()).format('YYYY-MM-DD HH:mm:ss');
      this.form.userName = localStorage.getItem('userName');
      this.activities.push({
        content: '提出人:' + this.form.userName + '正在填写项目问题表单',
        timestamp: '',
        color: '#0bbd87'
      })
    } else {
      const result = await _getById(this.projectIssueId)
      // console.log(result.data.projectIssue)
      if (result.code === 1200) {

        this.form.stage = result.data.projectIssue.stage
        this.form.stageCopy = this.form.stage

        const currentUserId = localStorage.getItem('id')

        // 权限
        this.isManager = result.data.projectIssue.project.projectUserId === currentUserId
        this.isAnalysis = result.data.projectIssue.analysisUserId === currentUserId
        this.isShort = result.data.projectIssue.shortMeasureUserId === currentUserId
        this.isLong = result.data.projectIssue.longMeasureUserId === currentUserId
        this.isCreate = result.data.projectIssue.createUser.id === currentUserId
        console.log(this.isManager, this.isShort, this.isLong, this.isAnalysis, this.isCreate)

        if (this.form.stage >= 0) {
          this.form.id = result.data.projectIssue.id;
          this.form.createUserDepartmentId = result.data.projectIssue.createUserDepartment.id;
          this.form.departmentName = result.data.projectIssue.createUserDepartment.fullName;
          this.form.description = result.data.projectIssue.description
          this.form.createAt = moment(result.data.projectIssue.createAt).format('YYYY-MM-DD HH:mm:ss');
          this.form.createUserId = result.data.projectIssue.createUser.id
          this.form.userName = result.data.projectIssue.createUser.realName
          this.form.img = result.data.projectIssue.img === null ? '' : result.data.projectIssue.img
          this.form.involved = result.data.projectIssue.involved
          this.form.statusShow = this.statusList.filter(sl => sl.code === result.data.projectIssue.status)[0].message
          this.projectMilestoneIdList = [];
          this.projectMilestoneIdList.push({
            value: result.data.projectIssue.projectMilestone.id,
            label: result.data.projectIssue.projectMilestone.name
          })
          this.projectOptions = [];
          this.projectOptions.push({
            value: result.data.projectIssue.project.id,
            label: result.data.projectIssue.project.name
          })
          this.form.projectId = result.data.projectIssue.project.id
          this.form.projectMilestoneId = result.data.projectIssue.projectMilestone.id
          this.form.questionTypeId = result.data.projectIssue.questionTypeId
          this.form.questionTypeArr = result.data.projectIssue.questionTypeId
          this.form.source = result.data.projectIssue.source
          if (this.form.img !== '') {
            const arr = result.data.projectIssue.img.split(';')
            this.form.fileList = arr.map(ar => {
              return {
                url: 'api' + ar,
                name: ar.substring(ar.lastIndexOf("\\") + 1)
              }
            })
          }
        }


        let userInfoList = []

        const idsArr = [result.data.projectIssue.analysisUserId, result.data.projectIssue.shortMeasureUserId, result.data.projectIssue.longMeasureUserId].filter(el => {
          return el !== '' && el !== null;
        });
        const ids = idsArr.join(',')
        if (ids !== '') {
          userInfoList = await this.querySysUsersByIds(ids);
        }

        if (this.form.stage >= 1) {
          this.form.hasRepeat = result.data.projectIssue.hasRepeat
          this.form.analysisUserId = result.data.projectIssue.analysisUserId
          this.form.priority = result.data.projectIssue.priority
          this.form.analysisUserName = userInfoList.filter(user => user.id === this.form.analysisUserId).length === 0 ? '' : userInfoList.filter(user => user.id === this.form.analysisUserId)[0].realName;
        }


        if (this.form.stage >= 2) {
          this.form.cause = result.data.projectIssue.cause
          this.form.shortMeasureUserId = result.data.projectIssue.shortMeasureUserId
          this.form.longMeasureUserId = result.data.projectIssue.longMeasureUserId
          this.form.shortMeasureUserName = userInfoList.filter(user => user.id === this.form.shortMeasureUserId).length === 0 ? '' : userInfoList.filter(user => user.id === this.form.shortMeasureUserId)[0].realName;
          this.form.longMeasureUserName = userInfoList.filter(user => user.id === this.form.longMeasureUserId).length === 0 ? '' : userInfoList.filter(user => user.id === this.form.longMeasureUserId)[0].realName;

        }


        if (this.form.stage >= 3) {


          this.form.shortMeasureExpectTime = result.data.projectIssue.shortMeasureExpectTime;
          this.form.shortMeasure = result.data.projectIssue.shortMeasure
          this.form.shortMeasureActualTime = result.data.projectIssue.shortMeasureActualTime === null ? '' : moment(result.data.projectIssue.shortMeasureActualTime).format('YYYY-MM-DD HH:mm:ss');
        }

        if (this.form.stage >= 4) {
          this.form.longMeasureExpectTime = result.data.projectIssue.longMeasureExpectTime;
          this.form.longMeasure = result.data.projectIssue.longMeasure
          this.form.longMeasureActualTime = result.data.projectIssue.longMeasureActualTime === null ? '' : moment(result.data.projectIssue.longMeasureActualTime).format('YYYY-MM-DD HH:mm:ss');
        }

        if (this.form.stage >= 5) {


          this.form.hasClose = result.data.projectIssue.hasClose + '';
          this.form.hasLl = result.data.projectIssue.hasLl;
          this.form.ll = result.data.projectIssue.ll;
          this.form.remark = result.data.projectIssue.remark;
        }


      }
    }
  }


  async function getTimeLine() {
    if (!this.projectIssueId) {
    } else {
      this.activities = []
      const result = await _findListByProjectIssueId({projectIssueId: this.projectIssueId})
      console.log(result)


      let userInfoList = []
      const srt = new Set();
      result.data.projectIssueHistoryList.forEach(element => {
        srt.add(element.shortMeasureUserId)
        srt.add(element.longMeasureUserId)
        srt.add(element.analysisUserId)
      });
      const idsArr = [...srt].filter(el => {
        return el !== '' && el !== null;
      });
      const ids = idsArr.join(',')
      if (ids !== '') {
        userInfoList = await this.querySysUsersByIds(ids);
      }

      this.activities = result.data.projectIssueHistoryList.map(his => {
        switch (his.stage) {
          case 0:
            return {
              content: '提出人:' + his.createUser.realName + '将项目问题表单提交',
              timestamp: '' + moment(his.updateAt).format('YYYY-MM-DD HH:mm'),
            }
            break;
          case 1:
            return his.hasClose === 1 ? {
              content: '项目经理将问题打回到选择分析人阶段',
              timestamp: '' + moment(his.updateAt).format('YYYY-MM-DD HH:mm'),
            } : {
              content: '项目经理选择分析人:' + userInfoList.filter(user => user.id === his.analysisUserId)[0].realName,
              timestamp: '' + moment(his.updateAt).format('YYYY-MM-DD HH:mm'),
            }
            break;
          case 2:
            return his.hasClose === 1 ? {
              content: '项目经理将问题打回到分析问题原因阶段',
              timestamp: '' + moment(his.updateAt).format('YYYY-MM-DD HH:mm'),
            } : {
              content: userInfoList.filter(user => user.id === his.shortMeasureUserId).length === 0 ? ('选择长期责任人:' + userInfoList.filter(user => user.id === his.longMeasureUserId)[0].realName) : ('分析人选择短期责任人:' + userInfoList.filter(user => user.id === his.shortMeasureUserId)[0].realName + ',选择长期责任人:' + userInfoList.filter(user => user.id === his.longMeasureUserId)[0].realName),
              timestamp: '' + moment(his.updateAt).format('YYYY-MM-DD HH:mm'),
            }
            break;
          case 3:
            return his.hasClose === 1 ? {
              content: '项目经理将问题打回到短期措施阶段',
              timestamp: '' + moment(his.updateAt).format('YYYY-MM-DD HH:mm'),
            } : {
              content: '短期责任人:' + userInfoList.filter(user => user.id === his.shortMeasureUserId)[0].realName + ',已处理',
              timestamp: '' + moment(his.updateAt).format('YYYY-MM-DD HH:mm'),
            }
            break;
          case 4:
            return his.hasClose === 1 ? {
              content: '项目经理将问题打回到长期措施阶段',
              timestamp: '' + moment(his.updateAt).format('YYYY-MM-DD HH:mm'),
            } : {
              content: '长期责任人:' + userInfoList.filter(user => user.id === his.longMeasureUserId)[0].realName + ',已处理',
              timestamp: '' + moment(his.updateAt).format('YYYY-MM-DD HH:mm'),
            }
            break;
          case 5:
            return {
              content: '提出人:' + his.createUser.realName + '将问题关闭',
              timestamp: '' + moment(his.updateAt).format('YYYY-MM-DD HH:mm'),
            }
            break;
          default:
            return {
              content: '',
              timestamp: '',
            }
            break
        }
      })
      this.activities[this.activities.length - 1].color = '#0bbd87'
    }

  }

  /**
   * 获取枚举类或者下拉框内容相关
   * @returns {Promise<void>}
   */
  async function getMyProjects() {
    const result = await _queryOwnerProjectList({searchName: ''})
    this.projectOptions = result.code === 1200 ? result.data.curUserProject.map(cp => {
      return {
        value: cp.id,
        label: cp.name
      }
    }) : []
  }

  async function getProjectIssueStatus() {
    const result = await _getProjectIssueStatus();
    this.statusList = result.code === 1200 ? result.data.status : []

  }

  async function getDepartment(id) {
    const result = await _getDepartment(id)
    this.form.departmentName = result.code === 1200 ? result.data.SysDepartment.fullName : ''
  }

  function chooseQuestionType() {
    this.form.questionTypeId = this.form.questionTypeArr.length === 0 ? '' : this.form.questionTypeArr[this.form.questionTypeArr.length - 1]
  }

  async function chooseProject() {
    if (!this.projectIssueId) {
      if (this.form.projectId === '') {
        this.form.projectMilestoneId = ''
        this.projectMilestoneIdList = []
      } else {
        const result = await _getByProjectIdAndTime({
          projectId: this.form.projectId,
          time: new Date()
        })
        this.form.projectMilestoneId = ''
        this.projectMilestoneIdList = result.code === 1200 ? result.data.projectMilestones.map(cp => {
          return {
            value: cp.id,
            label: cp.name
          }
        }) : []
      }
    }


  }

  async function getProjectIssueStage() {
    const result = await _getProjectIssueStage();
    this.projectStages = result.code === 1200 ? result.data.stages.map(cp => {
      return {
        value: cp.code,
        label: cp.message
      }
    }) : []
  }

  async function listCascade() {
    const result = await _listCascade();
    this.questionTypeList = result.code === 1200 ? result.data.cascaderResult : []
  }


  /**
   * 图片上传相关
   */
  function handleRemove(file, fileList) {
    console.log(file, fileList);

    return !this.projectIssueId
  }

  // 缩略图预览
  function handlePreviewPic(file) {
    console.log(file)
    var a = document.createElement('a');
    a.setAttribute('href', file.url);
    a.setAttribute('target', '_blank');
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }


  function uploadSuccess(response, file, fileList) {
    if (this.form.img === '') {
      this.form.img = response.data.path
    } else {
      const arr = this.form.img.split(';');
      arr.push(response.data.path);
      const str = arr.join(';')
      this.form.img = str
    }
    console.log(this.form.img)
  }


  function handleExceed(files, fileList) {
    this.$message.warning(`最大限制 5 个文件，当前选择了 5 个文件`);
  }

  function beforeUpload(file) {
    const is1M = file.size / 1024 / 1024 > 1; // 限制小于1M
    if (is1M) {
      this.$message.warning(`大小最大限制 1 M`);
      return false
    } else {
      return true
    }
  }

  /**
   * 人员选择的dialog
   */
  function closeOwner() {
    this.owners = [];
    this.userFlag = '';
    this.userTitle = ''
    this.ownerDialogVisible = false;
  }

  function openUser(a) {
    this.userFlag = a
    switch (a) {
      case 'ANALYSIS':
        this.userTitle = '选择原因分析人'
        break;
      case 'LONG':
        this.userTitle = '选择长期措施责任人'
        break;
      case 'SHORT':
        this.userTitle = '选择短期措施负责人'
        break;
    }
    this.ownerDialogVisible = true;

  }

  function getOwners(data) {
    if (data !== null && data.length > 0) {
      switch (this.userFlag) {
        case 'ANALYSIS':
          this.form.analysisUserName = data[0].realName;
          this.form.analysisUserId = data[0].id;
          break;
        case 'LONG':
          this.form.longMeasureUserName = data[0].realName;
          this.form.longMeasureUserId = data[0].id;
          break;
        case 'SHORT':
          this.form.shortMeasureUserName = data[0].realName;
          this.form.shortMeasureUserId = data[0].id;
          break;
      }

    }
  }


  async function querySysUsersByIds(ids) {
    const result = await _querySysUsersByIds({ids});
    return result.code === 1200 ? result.data.sysUserList : []
  }

  function quickFill(name) {
    if (name === 'SHORT') {
      this.form.shortMeasureActualTime = moment(new Date()).format('YYYY-MM-DD HH:mm:ss');
    } else {
      this.form.longMeasureActualTime = moment(new Date()).format('YYYY-MM-DD HH:mm:ss');
    }
  }

  /**
   * 控制权限
   */
  function isVisible(flag) {
    return flag <= this.form.stage && this.projectIssueId
  }

  /**
   * 表单提交
   * @returns {Promise<void>}
   */
  async function save() {
    this.$refs['projectIssueForm'].validate(async (valid) => {
      if (valid) {
        // 跳转阶段
        // if (this.form.stageCopy !== this.form.stage) {
        //   this.form.stage = this.form.stageCopy
        // }
        const result = await _saveProjectIssue(this.form);
        if (result.code === 1200) {
          this.$message.success('提交成功')
        } else {
          this.$message.warning('提交失败')
        }
        console.log(this.form)
        this.resetForm()
        this.$emit('end', 'refresh')
      } else {
        console.log(this.form);
        console.log('1212');
      }
    });
  }

  async function filterProject(searchName) {
    const result = await _queryOwnerProjectList({searchName})
    this.projectOptions = result.code === 1200 ? result.data.curUserProject.map(cp => {
      return {
        value: cp.id,
        label: cp.name
      }
    }) : []
  }


  async function setProjectIssueStage() {
    const result = await _setProjectIssueStage({
      stageCode: this.form.stageCopy,
      id: this.projectIssueId
    });
    if (result.code === 1200) {
      this.resetForm()
      this.$emit('end', 'refresh')
    }
  }

  function resetForm() {
    this.$refs['projectIssueForm'].resetFields();
    this.form.fileList = [];
    this.activities = [];
    this.form.analysisUserName = ''
    this.form.id = '';
    this.form.questionTypeArr = [''];
    this.form.createUserId = '';
    this.form.departmentName = '';
    this.form.shortMeasureUserName = '';
    this.form.longMeasureUserName = ''
  }

  function cancel() {

    this.$emit('end')
  }

  export default {
    name: 'ProjectIssueEdit',
    props: {
      projectIssueFlag: Boolean,
      projectIssueId: String,
      projectId: String
    },
    components: {UserTree},
    data() {
      return {
        token: localStorage.getItem('token'),
        form: {
          id: '',
          projectId: '',
          projectMilestoneId: '',
          source: '',
          createUserDepartmentId: '',
          userName: '',
          departmentName: '',
          createAt: '',
          questionTypeId: '',
          questionTypeArr: [''],
          description: '',
          involved: '',
          img: '',
          fileList: [],
          createUserId: '',
          priority: '',
          hasRepeat: '',
          analysisUserId: '',
          analysisUserName: '',
          cause: '',
          stage: 0,
          stageCopy: 0,
          shortMeasureUserId: '',
          longMeasureUserId: '',
          shortMeasureUserName: '',
          longMeasureUserName: '',
          shortMeasureActualTime: '',
          shortMeasureExpectTime: '',
          shortMeasure: '',
          longMeasureActualTime: '',
          longMeasureExpectTime: '',
          longMeasure: '',
          hasClose: '',
          statusShow: '',
          ll: '',
          hasLl: '',
          remark: ''
        },
        // 时间线
        activities: [],


        // 选择人员的组件
        owners: [],
        ownerDialogVisible: false,
        userFlag: '',
        userTitle: '',


        // 下拉框等数组
        projectOptions: [],
        projectMilestoneIdList: [],
        projectStages: [],
        statusList: [],
        questionTypeList: [],
        sourceList: [{
          value: '客户',
          label: '客户'
        }, {
          value: '公司内部',
          label: '公司内部'
        }],
        priorityList: [{
          value: 'H',
          label: '高'
        }, {
          value: 'M',
          label: '一般'
        }],
        hasRepeatList: [{
          value: '0',
          label: '是'
        }, {
          value: '1',
          label: '否'
        }],
        hasCloseList: [{
          value: '0',
          label: '已解决'
        }, {
          value: '1',
          label: '未解决'
        }],
        hasLlList: [{
          value: 0,
          label: '输入LL'
        }, {
          value: 1,
          label: '不输入LL'
        }],


        // 权限控制相关
        isManager: false,
        isShort: false,
        isLong: false,
        isAnalysis: false,
        isCreate: false,
        // 仅限xxx填写
        staticValue: ''
      };

    },
    methods: {
      getContent,
      getTimeLine,
      getMyProjects,
      save,
      chooseQuestionType,
      resetForm,
      getProjectIssueStage,
      chooseProject,
      getDepartment,
      listCascade,
      uploadSuccess,
      handleRemove,
      handleExceed,
      beforeUpload,
      cancel,
      closeOwner,
      getOwners,
      openUser,
      isVisible,
      quickFill,
      querySysUsersByIds,
      getProjectIssueStatus,
      setProjectIssueStage,
      handlePreviewPic,
      filterProject

    },
    created() {

    },
    mounted() {
      if (!this.projectIssueId) {
        this.getMyProjects();
      }
      this.getProjectIssueStatus()
      this.getProjectIssueStage();
      this.listCascade()

    },
    watch: {
      projectIssueFlag(val) {
        if (val) {
          this.getContent();
          this.getTimeLine()
        } else {
          this.resetForm();
          console.log(this.form)
        }
      }
    },
  };
</script>
<style lang="scss">
  .upload-width > {
    .el-upload-list {
      width: 400px;
    }
  }
</style>
