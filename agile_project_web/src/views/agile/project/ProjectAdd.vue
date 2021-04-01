<template>
  <div>
    <el-form ref="ruleForm" :model="form" :rules="rules" label-width="150px" style="width:700px">
      <el-form-item label="项目名称" prop="name">
        <el-input v-model="form.name" maxlength="50"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input type="textarea" :rows="5" v-model="form.description"></el-input>
      </el-form-item>
       <el-form-item label="项目类型">
         <el-input v-model="form.keyword" maxlength="50"></el-input>
      </el-form-item>
      <el-form-item label="隶属于" prop="circleId">

        <el-input v-model="form.parentName" style="width:300px;" readonly @focus="addCircle"></el-input>

      </el-form-item>
      <el-form-item label="项目负责人" prop="projectUserId">
        <el-input v-model="form.projectUserName" style="width:200px" readonly @focus="addOwner"></el-input>
      </el-form-item>
      <el-form-item label="可见范围">
        <el-radio-group v-model="form.visibleUserId" v-for="(item) in projectVisibility" :key="item.id">
          <el-radio :label="item.code" style="margin-right: 15px">{{item.name}}</el-radio>
        </el-radio-group>
        <div v-if="form.visibleUserId === '2'"
             @click="addSelectedDepartments"
             style="min-width:150px;min-height: 22px;background-color:#F5F7FA;cursor: pointer;margin-top: 5px">
          <el-tag
            v-for="tag in form.departmentNames"
            :key="tag.id">
            {{tag.name}}
          </el-tag>
        </div>
      </el-form-item>
      <el-form-item label="" v-show="addType">
        <el-upload action="/api/upload"
                   :headers="{
              token: token
            }"
                   :show-file-list="true"
                   :limit="1"
                   accept=".xls,.xlsx"
                   :file-list="fileList"
                   :on-success="uploadSuccess"
                   :on-remove="handleRemove"
                   :on-exceed="handleExceed"
        >
          <el-button>上传EXCEL</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">提交</el-button>
      </el-form-item>
    </el-form>
    <!--    选择隶属于的圈子-->
    <el-dialog
      title="选择圈子"
      :visible.sync="circleDialogVisible"
      width="500px"
      append-to-body>
      <div style="max-height: 350px;overflow:auto;">
        <el-tree
          ref="circleTree"
          :data="circleList"
          :props="{
              label: 'name',
              children: 'children'
            }"
          node-key="id"
          :check-strictly="true"
          :default-expanded-keys="[circle.id]"
          :default-checked-keys="[circle.id]"
          @check="handleCheck"
          show-checkbox>
        </el-tree>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="circleDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitCircle(circle)">确 定</el-button>
      </span>
    </el-dialog>
    <!--    选择项目负责人-->
    <el-dialog
      title="指定项目负责人"
      @close="closeOwner"
      :visible.sync="ownerDialogVisible"
      width="1200px"
      append-to-body>
      <user-tree
        :isSingle='true'
        :usersProp='owners'
        @getUsers="getOwners"
        @getCancel="closeOwner">
      </user-tree>
    </el-dialog>
    <!--选定的部门-->
    <el-dialog
      title="选择部门"
      :visible.sync="addSelectedDepartmentsVisible"
      width="1200px"
      append-to-body>
      <div style="max-height: 350px;overflow:auto;">
        <el-tree
          ref="companyDepartmentsTree"
          :data="companyDepartments"
          class="only-department"
          :check-strictly="true"
          :props="{
              label: 'fullName',
              children: 'departments'
            }"
          node-key="id"
          :default-expanded-keys="adda"
          :default-checked-keys="adda"
          @check="handleCheckDepartment"
          show-checkbox>
        </el-tree>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelCompanyDepartments">取 消</el-button>
        <el-button type="primary" @click="submitCompanyDepartments">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
  import {_queryCirclesMenus, _queryCircle} from '@/api/circleApi';
  import {_querySysUser} from '@/api/sysUserApi';
  import UserTree from '@/components/UserTree';
  import {_updateProject, _getProjectById, _findDepartmentsByProjectId} from '@/api/projectApi';
  import {_listCompanyAndDepartment} from '@/api/sysCompanyApi';

  import {downloadFile} from '@/api/utils';

  import {_checkExcel} from '@/api/excelDataApi';

  // 隶属于
  async function getCircles() {
    const result = await _queryCirclesMenus();
    if (result.code === 1200) {
      result.data.circles.forEach(c => {
        this.setDisabled(c);
      });
      this.circleList = result.data.circles;
    }
  }

  function setDisabled(circle) {
    circle.disabled = !circle.operation.hasAddProject;
    if (circle.children !== null) {
      circle.children.forEach(_c => {
        this.setDisabled(_c)
      })
    }
  }

  async function queryCircle(id) {
    const result = await _queryCircle(id);
    return result.data.circle;
  }

  async function addCircle() {
    await this.getCircles();
    this.circleDialogVisible = true;
  }


  function handleCheck(a, b) {
    if (b.checkedKeys.length > 0) {
      this.$refs.circleTree.setCheckedKeys([a.id]);
      this.circle = a;
    }
  }

  function submitCircle(val) {
    this.parentCircle = val;
    this.form.circleId = val.id;
    this.form.parentName = val.name;
    this.circleDialogVisible = false;
  }

  // ------------------------------------------
  // 选择项目负责人相关
  async function addOwner() {
    this.owners = [];
    if (this.form.projectUserId !== '') {
      const owner = await this.getUser(this.form.projectUserId);
      this.owners.push(owner);
    }
    this.ownerDialogVisible = true;
  }

  async function getUser(id) {
    const result = await _querySysUser(id);
    return result.data.sysUser;
  }


  function closeOwner() {
    this.owners = [];
    this.ownerDialogVisible = false;
  }

  function getOwners(data) {
    if (data !== null && data.length > 0) {
      this.form.projectUserName = data[0].realName;
      this.form.projectUserId = data[0].id;
    }
  }

  // ----------------------------------------------
  // 可见范围
  function addSelectedDepartments() {
    this.old = this.form.departmentNames;
    this.addSelectedDepartmentsVisible = true;

    this.$nextTick(() => {
      this.$refs.companyDepartmentsTree.setCheckedKeys(this.adda);

    });
  }

  function submitCompanyDepartments() {

    this.form.departmentIds = this.$refs.companyDepartmentsTree.getCheckedKeys();
    this.addSelectedDepartmentsVisible = false;
  }

  function cancelCompanyDepartments() {

    this.form.departmentNames = this.old;
    this.addSelectedDepartmentsVisible = false;


  }

  function handleCheckDepartment(a, b) {

    this.form.departmentNames = [];
    if (b.checkedKeys.length > 0) {
      b.checkedNodes.forEach((item) => {


        this.form.departmentNames.push({
          id: item.id,
          name: item.fullName,
        });
      });
    }
  }


  // -------------------------------------------------------
  // created刷新
  async function getProject(id) {
    this.fileList = [];
    this.form.filePath = "";
    if (id === '' || id === '-1') {
      this.form.projectUserName = '';
      this.form.projectUserId = '';
      this.form.id = '';
      this.form.name = '';
      this.form.description = '';
      this.form.keyword = '';
      this.owners = [];
      this.circle = this.parentCircle === null ? {
        id: '',
        name: '',
      } : this.parentCircle;
      this.form.circleId = this.circle.id;
      this.form.parentName = this.circle.name;
      this.form.visibleUserId = '0';
      this.departmentIds = '';
      this.departments = {
        id: '',
        fullName: '',
      };
      this.adda = [];
      this.form.departmentNames = [];

      this.form.departmentIds = '';
      this.form.filePath = "";
      this.tip = '创建'
    } else {
      this.tip = '更改'
      const result = await _getProjectById(id);
      if (result.code === 1200) {
        const project = result.data.project;
        this.form.id = project.id;
        this.form.name = project.name;
        this.form.keyword = project.keyword;
        this.form.projectUserId = project.projectUserId;
        this.form.description = project.description;
        this.owners = [];
        const owner = await this.getUser(project.projectUserId);
        this.owners.push(owner);
        this.form.projectUserName = owner.realName;
        if (project.circleId !== '0') {
          const parentCircle = await this.queryCircle(project.circleId);
          this.circle = parentCircle === null ? (
            this.parentCircle === null ? {
              id: '',
              name: '',
            } : this.parentCircle
          ) : parentCircle;
          this.form.circleId = this.circle.id;
          this.form.parentName = this.circle.name;
        }
        this.form.visibleUserId = `${project.visibleUserId}`;

        const result2 = await _findDepartmentsByProjectId({
          projectId: id,
        });
        this.form.departmentNames = [];
        this.adda = [];
        if (result2.data.projectDepartments.length > 0) {
          result2.data.projectDepartments.forEach((i) => {
            this.form.departmentNames.push({
              id: i.sysDepartment.id,
              name: i.sysDepartment.fullName,
            });
            this.adda.push(i.sysDepartment.id);
            this.departments = i.sysDepartment;
          });
        }
      }
    }
  }

  /**
   * 获取所有公司及对应部门
   */
  async function getCompanyAndDepartments() {
    const result = await _listCompanyAndDepartment({});
    if (result.code === 1200) {
      this.companyDepartments = result.data.results;
    }
  }

  // ----------------------------------------------------------------------------------------
  // 保存 表单提交
  function save() {
    this.$refs.ruleForm.validate((valid) => {
      if (valid) {
        this.$confirm('这个操作将' + this.tip + '项目信息，您想继续吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(async () => {
            if (this.addType && this.form.filePath === '') {
              this.$message({
                type: 'error',
                message: '请上传EXCEL!',
              });

            } else {
              if (this.addType && this.form.filePath !== '') {
                const _result = await _checkExcel({
                  path: this.form.filePath,
                  projectId: this.form.id,
                })
                if (_result.code !== 1200) {
                  return false;
                }
              }
              const result = await _updateProject({
                id: this.form.id,
                name: this.form.name,
                circleId: this.form.circleId,
                description: this.form.description,
                visibleUserId: this.form.visibleUserId,
                projectUserId: this.form.projectUserId,
                departmentIds: this.form.departmentIds === '' ? null : this.form.departmentIds,
                type: this.projectType,
                filePath: this.form.filePath,
                keyword: this.form.keyword,
              });
              if (result.code === 1200) {
                this.$store.commit('changeProjectEditOrDel', true);
                this.$message({
                  type: 'success',
                  message: '提交成功!',
                });
                await this.$emit('saveProject');
                await this.$emit('getProject', result.data.project, result.data.project.type);
              }
            }
          })
          .catch((e) => {
            this.$message({
              type: 'info',
              message: '执行失败！',
            });
          });
      } else {
        this.$message.error('有空值');
        return false;
      }
    });
  }


  function downloadExcel() {
    downloadFile('data/project/excelTemplate.xlsx').then((response1) => {
      const fileName = 'excelTemplate.xlsx';
      const fileData = response1;
      if (fileData !== null) {
        const blob = new Blob([fileData]);
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.style.display = 'none';
        link.href = url;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
      }
    });
  }

  function uploadSuccess(response, file, fileList) {
    this.form.filePath = response.data.path;
  }

  function handleRemove(file, fileList) {
    this.form.filePath = '';
  }

  function handleExceed(files, fileList) {
    this.$message.warning(`当前限制选择 1 个文件`);
  }

  // ---------------------------------------------------------

  export default {
    name: 'ProjectAdd',
    props: {
      projectId: String,
      parentCircle: Object,
      projectType: String,
      addType: Boolean,
    },
    components: {UserTree},
    data() {
      return {
        form: {
          id: '',
          name: '',
          description: '',
          circleId: '',
          parentName: '',
          projectUserId: '',
          projectUserName: '',
          visibleUserId: '0',
          departmentIds: '',
          departmentNames: [],
          type: '',
          filePath: '',
          keyword: '',
        },
        // 隶属于
        circleDialogVisible: false,
        circleList: [],
        circle: {},
        // 项目负责人
        owners: [],
        ownerDialogVisible: false,
        // 选定部门相关
        addSelectedDepartmentsVisible: false,
        isSelectedDepartments: [],
        companyDepartments: [],
        departments: {},
        // 枚举类
        projectVisibility: [],
        rules: {
          name: [
            {
              required: true,
              message: '请输入名称',
              trigger: 'blur',
            },
          ],
          description: [
            {
              required: true,
              message: '请输入描述',
              trigger: 'blur',
            },
          ],
          circleId: [
            {
              required: true,
              message: '请选择隶属于哪个圈子',
              trigger: 'manual',
            },
          ],
          projectUserId: [
            {
              required: true,
              message: '请选择项目负责人',
              trigger: 'manual',
            },
          ]
        },
        adda: [],
        old: [],
        tip: '',
        token: localStorage.getItem('token'),
        fileList: [],
      };
    },
    methods: {
      getCircles,
      addCircle,
      handleCheck,
      submitCircle,
      getProject,
      addOwner,
      getUser,

      closeOwner,
      getOwners,
      save,
      addSelectedDepartments,
      queryCircle,
      getCompanyAndDepartments,
      submitCompanyDepartments,
      cancelCompanyDepartments,
      handleCheckDepartment,
      setDisabled,
      downloadExcel,
      uploadSuccess,
      handleRemove,
      handleExceed
    },
    created() {
      const dictData = this.$store.getters.getDictionaryByKey('PROJECT_VISIBILITY');
      this.projectVisibility = dictData.sysDictDataList;
      this.getProject(this.projectId);
      this.getCompanyAndDepartments();
    },
    watch: {
      projectId(val) {
        this.getProject(val);
      },
      projectType(val) {
        this.getProject(this.projectId);
      },
      parentCircle(val) {
        if (this.form.id === '') {
          this.circle = this.parentCircle === null ? {
            id: '',
            name: '',
          } : this.parentCircle;
          this.form.circleId = this.circle.id;
          this.form.parentName = this.circle.name;
        }
      },
    },
  };
</script>
<style lang="scss">
  .only-department > {
    .el-tree-node {
      .el-tree-node__children {
        .is-leaf + .el-checkbox .el-checkbox__inner {
          display: inline-block;
        }
      }


      .el-checkbox .el-checkbox__inner {
        display: none;
      }


    }
  }
</style>
