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
          <div class="btn-search">
            <el-input
              size="small"
              placeholder="项目名称搜索"
              @input="search"
              style="width: 328px;margin-left: 10px"
              v-model="tableQuery.searchName">
              <i slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
          </div>
        </template>
      </main-top-part>

      <el-row style="margin: 30px 20px 80px;overflow-y: auto;">
        <el-col :span="24">
          <el-table
            :data="tableData"
            class="maxh-table"
            style="">
            <el-table-column prop='circleBelongs' label="圈子" width="300"
           >
            </el-table-column>
            <el-table-column label="项目名称" width="300" prop='name' >
              <template slot-scope="scope">
                <el-link type="primary" style="font-size: 12px" @click="go(scope.row.id)">{{scope.row.name}}</el-link>
              </template>
            </el-table-column>
          
            <el-table-column label="项目负责人" prop='projectUser.realName' 
           >
              <template slot-scope="scope">
                {{scope.row.projectUser !== null ? scope.row.projectUser.realName : ''}}
              </template>
            </el-table-column>
            <el-table-column prop="createAt" label="创建时间"
            ></el-table-column>
             <el-table-column label="项目归档人">
              <template slot-scope="scope">
                {{scope.row.stMarkUser !== null ? scope.row.stMarkUser.realName : ''}}
              </template>
            </el-table-column>
             <el-table-column prop="updateAt" label="归档时间"
            ></el-table-column>
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
  </div>
</template>

<script>

  import MainTopPart from '@/components/MainTopPart';

  import {
    _findStMarkProject,
  } from '@/api/projectApi';

  import {_listEnumerationByCode} from '@/api/sysDictApi';

  function search() {
    this.tableQuery.page = 1;
    this.getList();
  }

  async function getList() {
    const result = await _findStMarkProject(this.tableQuery);
    this.tableQuery.total = result.data.total;
    this.tableData = result.data.allStMarkVisibleProject;
    debugger;
    // this.getPageList();
  }



  function handlePageChange(val) {
    this.tableQuery.page = val;
    this.getList();
  }

  function handleSizeChange(val) {
    this.tableQuery.size = val;
    this.getList();
  }

  function go(id) {
    this.$router.push({
      path: 'project',
      query: {id: id},
    });
  }


  export default {
    components: {
      MainTopPart,
    },
    data() {
      return {
        menu: {},
        tableQuery: {
          searchName: '',
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
        .filter(menu => menu.code === 'PROJECT_ST_MARK');
      console.log(menu)
      console.log(JSON.parse(localStorage.getItem('MENUS')))
      this.menu = menu[0];
      // this.getListEnumeration();
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
      search,
      getList,
      handlePageChange,
      handleSizeChange,
      go,
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
<style lang="scss">

  .maxh-table {

    .cell {
      overflow: visible;
      height: 100%;
    }

  }
</style>
