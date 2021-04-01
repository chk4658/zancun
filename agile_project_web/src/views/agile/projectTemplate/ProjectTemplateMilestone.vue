<template>
  <div class="body">
    <main-top-part>
      <template v-slot:nav>
        <el-row style="margin-bottom:10px">
          <el-col>
            <el-breadcrumb separator-class="el-icon-arrow-right">
              <el-breadcrumb-item :to="{ path: 'project-template',query: {id: projectTemplateType.id}}">
                {{projectTemplateType.name}}
              </el-breadcrumb-item>
              <el-breadcrumb-item>
                {{projectTemplate.name}}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </el-col>
        </el-row>
      </template>
      <template v-slot:title>
        <div style="display: flex;height: 100px;align-items: center;">
          <el-image :src="'/api'+projectTemplate.imgUrl" style="width: 100px;height: 100px">
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
          <div>
            <div style="display:flex;">
              <span style="min-width:150px">项目模板类型:</span>
              <span v-if="!projectTemplate.edit.projectTemplateTypeId"
                    @click="projectTemplate.edit.projectTemplateTypeId = true">
                {{projectTemplateType.name}}
              </span>
              <el-select v-model="projectTemplate.projectTemplateTypeId"
                         v-if="projectTemplate.edit.projectTemplateTypeId"
                         size="medium"
                         placeholder="请选择"
                         style="min-width:400px"
                         v-focus
                         @visible-change="visibleChangeName($event,projectTemplate)">
                <el-option
                  v-for="item in projectTemplateTypes"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </div>
            <div style="display:flex;"><span style="min-width:150px">项目模板:</span>
              <span v-if="!projectTemplate.edit.name" @click="projectTemplate.edit.name = true">
                {{projectTemplate.name}}
              </span>
              <el-input v-if="projectTemplate.edit.name"
                        v-model="projectTemplate.name"
                        v-focus
                        size="medium"
                        style="min-width:400px"
                        @blur="saveProjectTemplate(projectTemplate)"
                        @keyup.enter.native="$event.target.blur"></el-input>
            </div>
          </div>

        </div>

      </template>
      <template v-slot:button>
        <div>

        </div>
      </template>

      <template v-slot:btn-search>
        <div class="btn-search">
          <el-button type="primary" v-if="hasEdit"
                     style="border-radius: 20px"
                     size="medium"
                     @click="add()"
                     :disabled='loading || projectTemplateMilestones.filter(p => p.name === "").length > 0'>
            新增里程碑
          </el-button>
          <el-input
            size="small"
            placeholder="搜索"
            @input="search"
            style="width: 328px;margin-left: 10px"
            v-model="searchName">
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
        </div>

      </template>
    </main-top-part>
    <div v-infinite-scroll="loadMore" infinite-scroll-disabled="busy"
         style="overflow:auto">
      <div style="width:100%;box-sizing: content-box;margin-right: 20px;display: flex;height：100%;">
        <div style="height: 100%;width: 20px;background-color: #fff;z-index: 999"></div>
        <ProjectTemplateMilestoneTable v-loading="saveLoading"
                                       :projectTemplateMilestones='projectTemplateMilestones'
                                       :projectTemplateAttrs='projectTemplateAttrs'
                                       @del='del'
                                       @save="save"
                                       @saveTask='saveTask'
                                       @delTask='delTask'
                                       @saveArry='saveArry'
                                       @deleteAttr='deleteAttr'
                                       @saveTaskData='saveTaskData'
                                       @move='move'
                                       @moveTask='moveTask'
                                       @batchDeleteTask='batchDeleteTask'>
        </ProjectTemplateMilestoneTable>
      </div>
      <p v-if="loading" style="text-align: center"><i class="el-icon-loading"></i>加载中...</p>
      <p v-if="!loading && this.tableQuery.total === this.projectTemplateMilestones.length" style="text-align: center;">
        没有更多了</p>
    </div>


  </div>
</template>

<script>

  import {_queryProjectTemplate, _updateProjectTemplate} from '@/api/projectTempleApi';

  import {_queryProjectTemplateType, _queryProjectTemplateTypes} from '@/api/projectTempleTypeApi';

  import {
    _updateProjectTemplateTask,
    _deleteProjectTemplateTask,
    _upProjectTemplateTask,
    _downProjectTemplateTask,
    _batchDeleteProjectTemplateTask
  } from '@/api/projectTemplateTaskApi';


  import {
    _listProjectTemplateMilestone,
    _updateProjectTemplateMilestone,
    _deleteProjectTemplateMilestone,
    _upProjectTemplateMilestone,
    _downProjectTemplateMilestone,
    _getProjectTemplateMilestoneById,
    _getProjectTemplateAttrsByProjectTemplateId
  } from '@/api/projectTemplateMilestoneApi';

  import {
    _updateProjectTemplateAttr,
    _deleteProjectTemplateAttr,
  } from '@/api/ProjectTemplateAttrApi';


  import {_updateTaskTemplateData} from '@/api/taskTemplateDataApi';

  import MainTopPart from '@/components/MainTopPart';

  import ProjectTemplateMilestoneTable from './ProjectTemplateMilestoneTable';

  async function getProjectTemplateTypes() {
    const result = await _queryProjectTemplateTypes({});
    if (result.code === 1200) {
      this.projectTemplateTypes = result.data.projectTemplateTypes;
    }
  }


  async function getProjectTemplate() {
    const result = await _queryProjectTemplate(this.projectTemplateId);
    if (result.code === 1200) {
      const projectTemplate = result.data.projectTemplate;
      projectTemplate.edit = {
        name: false,
        projectTemplateTypeId: false,
      };
      this.projectTemplate = projectTemplate;
      const result2 = await _queryProjectTemplateType(projectTemplate.projectTemplateTypeId);
      if (result2.code === 1200) {
        const projectTemplateType = result2.data.projectTemplateType;
        this.projectTemplateType = projectTemplateType;
      }

    }
  }

  /**
   * 更新模板名称
   */
  async function saveProjectTemplate(data) {
    await _updateProjectTemplate({
      id: data.id,
      name: data.name,
      projectTemplateTypeId: data.projectTemplateTypeId,
      imgUrl: data.imgUrl,
      enabled: data.enabled,
    });
    this.getProjectTemplate();
  }


  function search() {
    this.tableQuery.page = 0;
    this._projectTemplateMilestones = [];
    this.loadMore();
  }


  function loadMore() {
    this.$nextTick(() => {
      this.busy = true
      const that = this;
      setTimeout(async () => {
        that.tableQuery.page += 1;
        await that.getProjectTemplateMilestones();
        that.projectTemplateMilestones = JSON.parse(JSON.stringify(that._projectTemplateMilestones));
      }, 1000)
    });
  }


  /**
   * 里程碑
   */
  async function getProjectTemplateMilestones() {
    this.loading = true;
    this.tableQuery.page = this.tableQuery.page === 0 ? 1 : this.tableQuery.page;
    const result = await _listProjectTemplateMilestone({
      projectTemplateId: this.projectTemplateId,
      searchName: this.searchName,
      page: this.tableQuery.page,
      size: this.tableQuery.size,
    });
    if (result.code === 1200) {
      const projectTemplateMilestones = result.data.projectTemplateMilestones;
      const projectTemplateAttrs = result.data.projectTemplateAttrs;
      this.tableQuery.total = result.data.totalAmount;
      projectTemplateMilestones.forEach(p => {
        this.fomartData(p, projectTemplateAttrs);
      });
      const page = (this.tableQuery.total % this.tableQuery.size) === 0
        ? (this.tableQuery.total / this.tableQuery.size) :
        (parseInt(this.tableQuery.total / this.tableQuery.size) + 1);
      if (page < this.tableQuery.page) {
        this.busy = true;
      } else {
        this._projectTemplateMilestones.push(...projectTemplateMilestones);
        this.busy = false;
      }
      this.loading = false;

      // 属性
      if (projectTemplateAttrs !== null) {
        this.projectTemplateAttrs = [];
        projectTemplateAttrs.forEach(item => {
          // 初始化列头
          this.projectTemplateAttrs.push({
            id: item.id,
            name: item.name,
            type: item.type,
            isEditing: false,
          });
        });
      }

    }

  }


  function fomartData(p, projectTemplateAttrs) {
    let taskIndex = 0;
    p.isMouse = false;
    p.isPopover = false;
    p.edit = {
      name: false,
    },
      p.reviewRoleName = p.reviewProjectRole === null ? '' : p.reviewProjectRole.roleName;
    p.confirmRoleName = p.confirmProjectRole === null ? '' : p.confirmProjectRole.roleName;
    p.addTask = [{
      isAdd: false,
      id: '',
      projectTemplateId: '',
      projectTemplateMilestoneId: '',
      name: '',
      reviewRoleName: '',
      confirmRoleName: '',
      openConditions: '',
      openDescription: '',
      parentId: '',
      aheadDay: '',
      type: [],
      isRequirement: 0,
    }],
      p.taskList.forEach(t => {
        t.reviewRoleName = t.reviewProjectRole === null ? '' : t.reviewProjectRole.roleName;
        t.confirmRoleName = t.confirmProjectRole === null ? '' : t.confirmProjectRole.roleName;
        t.checked = false;
        t.index = taskIndex;
        t.serialNumber = taskIndex + 1;
        taskIndex++;
        t.edit = {
          name: false,
          openConditions: false,
          openDescription: false,
          aheadDay: false,
          type: false,
        },
          t.children.map((c, _index) => {
            c.serialNumber = _index + 1;
            c.checked = false;
            c.edit = {
              name: false,
              openConditions: false,
              openDescription: false,
              aheadDay: false,
              type: false,
            }
            this.taskDataInit(projectTemplateAttrs, c);
            this.fomartName(c)
          });
        this.taskDataInit(projectTemplateAttrs, t);
        this.fomartName(t)
      });
  }


  /**
   * 初始化任务属性值
   */
  function taskDataInit(projectTemplateAttrs, task) {
    projectTemplateAttrs.forEach(arry => {
      const taskDatas = task.taskDataList.filter(td => td.projectTemplateAttrId === arry.id);
      if (taskDatas.length === 0) {
        task.taskDataList.push({
          id: '',
          value: '',
          projectTemplateTaskId: task.id,
          projectTemplateAttrId: arry.id,
          projectTemplateAttrType: arry.type,
        })
      }
    });
    task.taskDataList.forEach(t => {
      t.isEditing = false;
    });

  }


  function fomartName(task) {
    if (task.type !== null) {
      task.type = task.type.split(',');

      const typeName = [];
      task.type.forEach(tt => {
        let ttname = '';
        switch (tt) {
          case '1':
            ttname = '普通任务'
            break;
          case '2':
            ttname = 'APQP任务'
            break;
          case '3':
            ttname = '质量阀任务'
            break;
        }
        typeName.push(ttname)
      })
      task.typeName = typeName.join(',');
    }
    task.aheadDayName = task.aheadDay === null || task.aheadDay === -1 ? '无' : task.aheadDay;
    task.isRequirement = task.isRequirement === null || task.isRequirement === 0 ? false : true
  }

  function add() {
    const projectTemplateMilestones = this.projectTemplateMilestones.filter(m => m.name === '');
    if (projectTemplateMilestones.length === 0) {
      this.projectTemplateMilestones.splice(0, 0, {
        addTask: [{
          isAdd: false,
          aheadDay: "",
          confirmRoleName: "",
          isRequirement: 0,
          id: "",
          name: "",
          openConditions: "",
          openDescription: "",
          parentId: "",
          projectTemplateId: "",
          projectTemplateMilestoneId: "",
          reviewRoleName: "",
          type: [],
        }],
        confirmProjectRole: null,
        confirmProjectRoleId: null,
        edit: {
          name: true
        },
        id: '',
        isMouse: false,
        isPopover: false,
        name: '',
        parentId: null,
        projectTemplateId: this.projectTemplateId,
        reviewProjectRole: null,
        reviewProjectRoleId: null,
        taskList: [],
      })
    }
  }


  function save(data) {
    if (data.name.replace(/(^\s*)|(\s*$)/g, "") === '') {
      const i_ = this.projectTemplateMilestones.findIndex(_m => _m.name.replace(/(^\s*)|(\s*$)/g, "") === '');
      this.projectTemplateMilestones.splice(i_, 1);
    } else {
      this.saveLoading = true;
      _updateProjectTemplateMilestone({
        id: data.id,
        name: data.name,
        projectTemplateId: data.projectTemplateId,
        reviewRoleName: data.reviewRoleName,
        confirmRoleName: data.confirmRoleName,
        aheadDay: data.aheadDay,
      }).then(async req => {
        const id = req.data.id;
        const _i = this.projectTemplateMilestones.findIndex(_m => _m.id === '');
        if (_i > -1) this.projectTemplateMilestones.splice(_i, 1);
        const _req = await _getProjectTemplateMilestoneById(id);
        const milestone = _req.data.projectTemplateMilestone;
        const _attrs = _req.data.projectTemplateAttrs;
        const i = this.projectTemplateMilestones.findIndex(_m => _m.id === milestone.id);
        this.fomartData(milestone, _attrs);
        if (i > -1) {
          this.projectTemplateMilestones.splice(i, 1);
          this.projectTemplateMilestones.splice(i, 0, milestone);
        } else {
          this.projectTemplateMilestones.push(milestone);
        }
        this.tableQuery.total += 1;
        this.saveLoading = false;
      })
    }

  }


  async function del(id) {
    this.saveLoading = true;
    console.log(id);
    if (id !== null && id !== '') {
      const result = await _deleteProjectTemplateMilestone(id);
    }
    const i = this.projectTemplateMilestones.findIndex(_m => _m.id === null || _m.id === '' || _m.id === id);
    this.projectTemplateMilestones.splice(i, 1);
    this.tableQuery.total -= 1;
    this.saveLoading = false;
  }


  /**
   * 编辑/添加任务
   */
  function saveTask(task, milestone) {
    if (task.name === null || task.name.replace(/(^\s*)|(\s*$)/g, "") === '') {
      this.projectTemplateMilestones.forEach(m => {
        m.taskList.forEach(tc => {
          var i_ = tc.children.findIndex(t => t.name === null || t.name.replace(/(^\s*)|(\s*$)/g, "") === '');
          if (i_ > -1) tc.children.splice(i_, 1);
        })
        var j_ = m.taskList.findIndex(t => t.name === null || t.name.replace(/(^\s*)|(\s*$)/g, "") === '');
        if (j_ > -1) m.taskList.splice(j_, 1);
      })
    } else {
      const data = {
        id: task.id,
        projectTemplateId: milestone.projectTemplateId,
        projectTemplateMilestoneId: milestone.id,
        name: task.name,
        reviewRoleName: task.reviewRoleName === null || task.reviewRoleName === ''
          ? (milestone.reviewProjectRole === null || milestone.reviewProjectRole === undefined ? '' : milestone.reviewProjectRole.roleName)
          : task.reviewRoleName,
        confirmRoleName: task.confirmRoleName === null || task.confirmRoleName === ''
          ? (milestone.confirmProjectRole === null || milestone.confirmProjectRole === undefined ? '' : milestone.confirmProjectRole.roleName)
          : task.confirmRoleName,
        openConditions: task.openConditions,
        openDescription: task.openDescription,
        parentId: task.parentId,
        aheadDay: task.aheadDay,
        type: task.type === null ? '' : task.type.join(','),
        isRequirement: task.isRequirement === null || !task.isRequirement ? 0 : 1
      };
      _updateProjectTemplateTask(data).then(async req => {
        const id = req.data.id;
        task.id = id;
        if (task.type !== null) task.type = task.type.join(",");
        task.isRequirement = !task.isRequirement ? 0 : 1;
        this.fomartName(task);
        for (let key in task.edit) {
          task.edit[key] = false;
        }
        if (task.parentId === "") {
          const _req = await _getProjectTemplateMilestoneById(milestone.id);
          const _milestone = _req.data.projectTemplateMilestone;
          milestone = _milestone;
          const _attrs = _req.data.projectTemplateAttrs;
          const i = this.projectTemplateMilestones.findIndex(_m => _m.id === milestone.id);
          this.fomartData(milestone, _attrs);
          if (i > -1) {
            this.projectTemplateMilestones.splice(i, 1);
            this.projectTemplateMilestones.splice(i, 0, milestone);
          } else {
            this.projectTemplateMilestones.push(milestone);
          }
        }
        this.saveLoading = false;
      })
    }
    //  this.saveLoading = true;

  }

  /**
   * 删除任务
   */
  async function delTask(id) {
    if (id !== null && id !== '') {
      await _deleteProjectTemplateTask(id);
    }
    this.projectTemplateMilestones.forEach(m => {
      m.taskList.forEach(tc => {
        var i = tc.children.findIndex(t => t.id === null || t.id === '' || t.id === id);
        if (i > -1) tc.children.splice(i, 1);
      })
      var j = m.taskList.findIndex(t => t.id === null || t.id === '' || t.id === id);
      if (j > -1) m.taskList.splice(j, 1);
    })
  }


  /**
   * 新增活动属性
   */
  async function saveArry(data) {
    this.saveLoading = true;
    const result = await _updateProjectTemplateAttr({
      id: data.id,
      projectTemplateId: this.projectTemplateId,
      type: data.type,
      name: data.name,
    });
    const projectTemplateAttr = result.data.projectTemplateAttr;
    projectTemplateAttr.isEditing = false;
    const _req = await _getProjectTemplateAttrsByProjectTemplateId(this.projectTemplateId);
    const projectTemplateAttrs = _req.data.projectTemplateAttrs;
    if (projectTemplateAttrs !== null) {
      this.projectTemplateAttrs = [];
      projectTemplateAttrs.forEach(item => {
        // 初始化列头
        this.projectTemplateAttrs.push({
          id: item.id,
          name: item.name,
          type: item.type,
          isEditing: false,
        });
      });
    }
    const _j = projectTemplateAttrs.findIndex(_attr => _attr.id === projectTemplateAttr.id);
    projectTemplateAttrs.splice(_j, 1);
    projectTemplateAttrs.splice(_j, 0, projectTemplateAttr);
    this.projectTemplateMilestones.forEach(p => {
      p.taskList.forEach(t => {
        t.children.forEach(tc => {
          const __i = tc.taskDataList.findIndex(_m => _m.id === projectTemplateAttr.id);
          if (__i === -1) {
            tc.taskDataList.push({
              id: '',
              value: '',
              projectTemplateTaskId: tc.id,
              projectTemplateAttrId: projectTemplateAttr.id,
              projectTemplateAttrType: projectTemplateAttr.type,
              isEditing: false,
            });
          }
          ;
        })
        const _i = t.taskDataList.findIndex(_m => _m.id === projectTemplateAttr.id);
        if (_i === -1) {
          t.taskDataList.push({
            id: '',
            value: '',
            projectTemplateTaskId: t.id,
            projectTemplateAttrId: projectTemplateAttr.id,
            projectTemplateAttrType: projectTemplateAttr.type,
            isEditing: false,
          });
        }
        ;
      });
    })
    this.saveLoading = false;
  }

  /**
   * 删除属性
   */
  async function deleteAttr(data) {
    this.saveLoading = true;
    const result = await _deleteProjectTemplateAttr(data);
    const i = this.projectTemplateAttrs.findIndex(attr => attr.id === data);
    if (i >= -1) this.projectTemplateAttrs.splice(i, 1);
    this.saveLoading = false;
  }

  async function saveTaskData(data) {
    this.saveLoading = true;
    const result = await _updateTaskTemplateData(data);
    const taskTemplateData = result.data.taskTemplateData;
    this.projectTemplateMilestones.forEach(p => {
      p.taskList.forEach(t => {
        t.children.forEach(tc => {
          const __i = tc.taskDataList.findIndex(_m => _m.id === taskTemplateData.id);
          if (__i > -1) {
            tc.taskDataList[__i].value = taskTemplateData.value;
          }
        })
        const _i = t.taskDataList.findIndex(_m => _m.id === taskTemplateData.id);
        if (_i > -1) {
          t.taskDataList[_i].value = taskTemplateData.value;
        }
      });
    })
    this.saveLoading = false;
  }

  /**
   * 移动里程碑
   */
  async function move(id, type) {
    this.saveLoading = true;
    if (type === 'up') {
      await _upProjectTemplateMilestone(id);
    } else if (type === 'down') {
      await _downProjectTemplateMilestone(id);
    }
    const _req = await _getProjectTemplateMilestoneById(id);
    const milestone = _req.data.projectTemplateMilestone;
    const i = this.projectTemplateMilestones.findIndex(_m => _m.id === milestone.id);
    let j = -1;
    if (type === 'up') {
      j = i - 1;
    } else if (type === 'down') {
      j = i + 1;
    }
    this.projectTemplateMilestones[i] = this.projectTemplateMilestones.splice(j, 1, this.projectTemplateMilestones[i])[0];
    this.saveLoading = false;
  }

  /**
   * 移动任务
   */
  async function moveTask(id, type, mId) {
    this.saveLoading = true;
    if (type === 'up') {
      await _upProjectTemplateTask(id);
    } else if (type === 'down') {
      await _downProjectTemplateTask(id);
    }
    const _req = await _getProjectTemplateMilestoneById(mId);
    const milestone = _req.data.projectTemplateMilestone;
    const _attrs = _req.data.projectTemplateAttrs;
    const i = this.projectTemplateMilestones.findIndex(_m => _m.id === milestone.id);
    this.fomartData(milestone, _attrs);
    this.projectTemplateMilestones.splice(i, 1);
    this.projectTemplateMilestones.splice(i, 0, milestone);
    this.projectTemplateMilestones[i] = milestone;
    this.saveLoading = false;
  }


  async function batchDeleteTask(data) {
    this.saveLoading = true;
    var ids = JSON.parse(JSON.stringify(data)).join(',');
    await _batchDeleteProjectTemplateTask(ids)
    this._projectTemplateMilestones = [];
    this.tableQuery.page = 0;
    this.loadMore();
    this.saveLoading = false;
  }

  function visibleChangeName(callback, projectTemplate) {
    if (!callback) {
      this.saveProjectTemplate(projectTemplate)
    }
  }

  export default {
    name: 'ProjectTemplateMilestone',
    components: {
      MainTopPart,
      ProjectTemplateMilestoneTable,
    },
    data() {
      return {
        tableQuery: {
          page: 0,
          size: 2,
          total: 0,
        },
        projectTemplateTypes: [],
        projectTemplateId: '',
        searchName: '',
        projectTemplate: {
          edit: {
            name: false,
            projectTemplateTypeId: false,
          }
        },
        projectTemplateType: {},
        projectTemplateMilestones: [],
        projectTemplateAttrs: [],
        loading: false,
        busy: false,
        // eslint-disable-next-line vue/no-reserved-keys
        _projectTemplateMilestones: [],
        saveLoading: false,
        userId: this.$store.state.id,
        hasEdit: false,

      }
    },
    computed: {},
    created: async function () {
      await this.getProjectTemplateTypes();
      this.projectTemplateId = this.$route.query.id;
      await this.getProjectTemplate();
      this._projectTemplateMilestones = [];
      // this.projectTemplateMilestones = [];
      // this.tableQuery.page = 0;
      const createUserId = this.projectTemplate.createUserId;
      if (createUserId === this.userId) {
        this.hasEdit = true;
      } else {
        const bottoms = JSON.parse(localStorage.getItem('BUTTONS'));
        this.hasEdit = bottoms.filter(b => b === "PROJECT_TEMPLATE_TYPE_EDIT").length > 0;
      }


    },
    beforeMount: function () {
    },
    mounted: function () {
      let div = document.querySelector('.body');
      div.scrollTop = div.scrollHeight;
    },
    beforeDestroy: function () {
      this._projectTemplateMilestones = [];
      this.projectTemplateMilestones = [];
      this.tableQuery.page = 0;
      this.tableQuery.total = 0;
    },
    destroyed: function () {
    },
    methods: {
      getProjectTemplateTypes,
      taskDataInit,
      getProjectTemplate,
      search,
      getProjectTemplateMilestones,
      // 更新模板
      saveProjectTemplate,
      // 里程碑
      add,
      del,
      save,
      // 添加task
      saveTask,
      delTask,
      // 添加arry
      saveArry,
      deleteAttr,
      saveTaskData,

      // 移动
      move,
      moveTask,

      batchDeleteTask,

      visibleChangeName,
      loadMore,
      fomartData,
      fomartName,

    },
    watch: {
      $route: async function () {
        await this.getProjectTemplateTypes();
        this.projectTemplateId = this.$route.query.id;
        await this.getProjectTemplate();
        this.projectTemplateMilestones = [];
        this._projectTemplateMilestones = [];
        this.tableQuery.page = 0;
        const createUserId = this.projectTemplate.createUserId;
        if (createUserId === this.userId) {
          this.hasEdit = true;
        } else {
          const bottoms = JSON.parse(localStorage.getItem('BUTTONS'));
          this.hasEdit = bottoms.filter(b => b === "PROJECT_TEMPLATE_TYPE_EDIT").length > 0;
        }
      },
    },
    directives: {}
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .body {
    height: 100%;
    width: 100%;
    border-left: 1px #e1e1e1 solid;
    float: left;
    display: flex;
    box-sizing: border-box;
    flex-direction: column;
    background-color: #fff;
    overflow-x: hidden;
  }

  .btn-search {
    display: flex;
    justify-content: flex-end;
  }
</style>
