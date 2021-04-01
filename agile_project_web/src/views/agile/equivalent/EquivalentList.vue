<template>


<div style="width: 100%;
              height: 100%;
              display: flex;overflow-x: hidden">
    <div class="information">
      <main-top-part>
        <template v-slot:title>
          <div style="display: flex">
            <i :class="menu.icon"
               class="menu-icon"
               style="color: #01408B;
                    font-size: 32px;">
            </i>
            {{menu.name}}
          </div>

        </template>
        <template v-slot:search>
          <div style="display: flex;justify-content: flex-end;float: right">
            <el-button type="primary"
                       round
                       size="medium"
                       @click="add('')" >
              新增
            </el-button>
             <el-date-picker
              size="medium"
              v-model="dates"
              type="monthrange"
              range-separator="-"
              style="margin-left:10px;width:300px" @change='search'>
            </el-date-picker>
            <el-input
              size="medium"
              placeholder="请输入人员姓名"
              v-model="tableQuery.searchName"
              style="width: 328px; float: left;margin-left:10px"
              @input="search"
            ><i slot="prefix" class="el-input__icon el-icon-search" ></i>
            </el-input>
          </div>
        </template>
      </main-top-part>

      <el-row style="margin: 30px 20px 80px;overflow-y: auto;">
        <el-col :span="24">
          <el-table
            :data="tableData"
            style="">
            <el-table-column label="部门"  width="250" >
              <template slot-scope="scope">
                {{scope.row.sysUser !== null ? scope.row.sysUser.departmentName : ''}}
              </template>
            </el-table-column>
            <el-table-column label="姓名" >
              <template slot-scope="scope">
                {{scope.row.sysUser !== null ? scope.row.sysUser.realName : ''}}
              </template>
            </el-table-column>
            <el-table-column prop='project' label="项目当量" 
             ></el-table-column>
            <el-table-column prop='task' label="任务当量"
              ></el-table-column>
            <el-table-column prop='other' label="其他当量"
              ></el-table-column>
            <el-table-column prop='deliveryQuality' label="交付质量"
              ></el-table-column>
            <el-table-column prop='knowledge' label="知识"
              ></el-table-column>
            <el-table-column prop='work' label="工作当量"
              ></el-table-column>
            <el-table-column prop="months" label="月份"
              ></el-table-column>
            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button type="text" @click="add(scope.row.id)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>

      <el-row class="page">
        <el-col :span="24">
              <el-pagination
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
            background
            :current-page="this.tableQuery.page"
            :page-sizes="[15, 30, 50, 100]"
            :page-size="this.tableQuery.size"
            style="text-align: left;"
            layout="total, sizes, prev, pager, next, jumper"
            :total="this.tableQuery.total">
          </el-pagination>
        </el-col>
      </el-row>
    </div>


     <el-dialog
      width="40%"
      :title="equivalent.id === '' ? '新增当量' : '编辑当量'"
      @close="close"
      :visible.sync="equivalent.dialogVisible"
      append-to-body>
      <EquivalenEdit @save='save' :equivalentId="equivalent.id"></EquivalenEdit>
    </el-dialog>


  </div>
</template>

<script>

  import moment from "moment";

  import MainTopPart from '@/components/MainTopPart';

  import EquivalenEdit from './EquivalenEdit.vue';

  import {_listEquivalens} from '@/api/equivalenApi';


  function add(id) {
    this.equivalent.id = id;
    this.equivalent.dialogVisible = true;
  }

  function save() {
    this.close();
    this.getList();
  }

  function close() {
    this.equivalent.id = '-1';
    this.equivalent.dialogVisible = false;
  }

  /**
   * 上传文件
   */
  function uploadSuccess(res, file) {
    const path = res.data.path;
  }


  async function getList() {
    this.tableQuery.startDate = this.dates!== null && this.dates.length === 2 ?  moment(this.dates[0])
        .format('YYYY-MM') : '';
    this.tableQuery.endDate = this.dates!== null && this.dates.length === 2 ?  moment(this.dates[1])
        .format('YYYY-MM') : '';
    const result = await _listEquivalens(this.tableQuery);
    if (result.code === 1200) {
      this.tableData = result.data.equivalents;
      this.tableQuery.total = result.data.totalAmount;

      console.log(result)
    }
  }


  function search() {
    this.tableQuery.page = 1;
    this.getList();
  }


  function handlePageChange(val) {
    this.tableQuery.page = val;
    this.getList();
  }

  function handleSizeChange(val) {
    this.tableQuery.size = val;
    this.getList();
  }

  export default {
    components: {
      MainTopPart,
      EquivalenEdit
    },
    data() {
      return {
        token: localStorage.getItem('token'),
        menu: {},
        equivalent: {
          id: '',
          dialogVisible: false,
        },
        addVisible: false,
        dates: [],
        tableQuery: {
          searchName: '',
          startDate: '',
          endDate: '',
          page: 1,
          size: 15,
          total: 0,
        },
        tableData: [],
      }
    },
    computed: {},
    created: function () {
      const menu = JSON.parse(localStorage.getItem('MENUS'))
        .filter(menu => menu.code === 'QUIVALENT');
      this.menu = menu[0];
      this.getList();
    },
    beforeMount: function () {
    },
    mounted: function () {
    },
    beforeDestroy: function () {
    },
    destroyed: function () {
    },
    methods: {
      add,
      uploadSuccess,
      save,
      close,
      getList,
      handlePageChange,
      handleSizeChange,
      search,
    },
    watch: {},
    directives: {}
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  >>> .el-table th {
    background-color: #eee;
  }


  .information {
    width: 100%;
    flex: 1;
    background-color: #fff;
    float: left;
    display: flex;
    flex-direction: column;
    position: relative;
    border-left: 1px solid #e1e1e1;
  }


  .page {
    position: absolute;
    bottom: 20px;
    left: 20px;
  }
</style>
