<template>
  <div>
    <el-row :gutter="20" style="width:100%">
      <el-col  :span="6">
        <div style="max-height: 350px;overflow:auto;">
           <el-tree :data="companyDepartments" :props="defaultProps" @node-click="handleNodeClick"></el-tree>
        </div>
      </el-col>
      <el-col style="border-right:1px #e1e1e1 solid;border-left:1px #e1e1e1 solid;" :span="11">
        <div  style="margin: 0 0 10px 20%; width:300px">
          <el-input
            size="mini"
            placeholder="请输入姓名"
            v-model="tableQuery.name"
            @input="getUsers">
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
        </div>
        <span class="user-tree-span">请选择人员</span>
        <el-table
          ref="refTable"
          :data="table.data"
          height="300"
          style="width: 100%"
          @cell-click="cellClick">
            <el-table-column
              prop="realName"
              label="姓名"
              width="180">
            </el-table-column>
            <el-table-column
              prop="departmentName"
              label="部门"
              show-overflow-tooltip>
            </el-table-column>
        </el-table>
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
          :current-page="this.tableQuery.page"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="this.tableQuery.size"
          background
          :pager-count="5"
          style="text-align: left;
          padding-left: 21px;margin-top: 5px"
          layout="total, prev, pager, next"
          :total="this.tableQuery.total">
        </el-pagination>
      </el-col>
      <el-col :span="6" style="">
        <span  class="user-tree-span" >已选择人员</span>
        <el-tag  style="margin:10px 0px 0px 5px;" @close="tagClose(u)"
          v-for="u in users"
          :key="u.id"
          closable
          type="">
          <span>{{u.realName}}</span>
        </el-tag>
      </el-col>
    </el-row>
    <div style="margin-left: 50%;margin-top: 30px;">
      <el-button type="primary" @click="selectRefTable">选择</el-button>
      <el-button @click="cancelRefTable">取消</el-button>
    </div>
  </div>
</template>

<script>

import { _listCompanyAndDepartment, } from '@/api/sysCompanyApi';

import { _findByCompanyAndDepartment, } from '@/api/sysUserApi';

/**
 * 获取所有公司及对应部门
 */
async function getCompanyAndDepartments() {
   const result = await _listCompanyAndDepartment({});
  if (result.code === 1200) {
    this.companyDepartments = result.data.results;
  }
}


function handlePageChange(val) {
  this.tableQuery.page = val;
  this.tableQuery.companyId,
  this.tableQuery.departmentId,
  this.getUsers();
}

function handleSizeChange(val) {
  this.tableQuery.size = val;
  this.tableQuery.companyId,
  this.tableQuery.departmentId,
  this.getUsers();
}

/**
 *获取人员 根据部门公司
 */
async function getUsers() {
  const result = await _findByCompanyAndDepartment(this.tableQuery);
  if (result.code === 1200) {
    this.table.data = result.data.sysUserRecords;
    this.tableQuery.total = result.data.totalAmount;
    this.$nextTick(() => {
      this.setRefTable();
    })
  }
}

/**
 * 获取人员
 */
function handleNodeClick(data){
  const companyId = data.companyId === undefined ? data.id : data.companyId;
  const departmentId = data.companyId === undefined ? null : data.id;
  this.tableQuery.departmentId = departmentId;
  this.tableQuery.companyId = companyId;
  this.getUsers()
}
/**
 * 单选
 */
function cellClick(vals) {
  if (this.isSingle) {
    this.users = [];
  };
  const users = this.users.filter(item => item.id === vals.id );
  if (users.length === 0 ) this.users.push(vals);
}

function tagClose(val) {
  this.users = this.users.filter(item => item.id !== val.id );
}

/**
 * 确认
 */
function selectRefTable() {
  this.$emit("getUsers",this.users);
  this.$emit("getCancel");
}

/**
 * 取消
 */
function cancelRefTable() {
  this.users = this.usersProp;
  this.$emit("getCancel");
}

/**
 * 初始化值
 */
function setRefTable () {
  this.$refs.refTable.setCurrentRow();
  this.users = this.usersProp;
}

export default {
  name: 'UserTree',
  props: {
    usersProp: Array,
    isSingle: Boolean,
  },
  data () {
    return {
      companyDepartments: [],
      defaultProps: {
        children: 'departments',
        label: 'fullName'
      },
      tableQuery: {
        page: 1,
        size: 20,
        total: 0,
        companyId: "",
        departmentId: "",
        name: "",
      },
      table: {
        data: [],
      },
      users: [],
    }
  },
  computed: {},
  created: function () {
    this.getCompanyAndDepartments();
    this.tableQuery.departmentId = null;
    this.tableQuery.companyId = null;
    this.getUsers();
  },
  beforeMount: function () {},
  mounted: function () {

  },
  beforeDestroy: function () {},
  destroyed: function () {},
  methods: {
    getCompanyAndDepartments,
    getUsers,
    setRefTable,
    handleNodeClick,
    cellClick,
    selectRefTable,
    cancelRefTable,
    handleSizeChange,
    handlePageChange,
    tagClose
  },
  watch: {
    usersProp(vals) {
      this.tableQuery.departmentId = null;
      this.tableQuery.companyId = null;
      this.getUsers();
    }
  },
  directives: {}
}
</script>
<style scoped>
  .user-tree-span{
    text-align: center;
    display:block;
    padding-bottom: 10px;
    font-family:Georgia;
  }
</style>
