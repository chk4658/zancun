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
            <!-- <el-button type="primary"
                      style="border-radius: 20px !important;"
                      size="medium"
                      @click='onLine'
                     >
              批量上线
            </el-button> -->
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
            <el-table-column label="项目名称" width="300" prop='name' 
           >
              <template slot-scope="scope">
                <el-link type="primary" style="font-size: 12px" @click="go(scope.row.id)">{{scope.row.name}}</el-link>
              </template>
            </el-table-column>
            <el-table-column prop='statusName' label="状态"
            >
            </el-table-column>
            <el-table-column label="项目负责人" prop='projectUser.realName' 
           >
              <template slot-scope="scope">
                {{scope.row.projectUser !== null ? scope.row.projectUser.realName : ''}}
              </template>
            </el-table-column>
            <el-table-column prop="createAt" label="创建时间"
            ></el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button type="text" @click="onLine(scope.row.id,1)">上线</el-button>
                <el-button type="text" @click="go(scope.row.id)">编辑</el-button>
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
  </div>
</template>

<script>

  import MainTopPart from '@/components/MainTopPart';

  import {
    _queryAllProjectList,
    _queryOwnerNotOnlineProjectList,
    _updateProjectOnLine
  } from '@/api/projectApi';

  import {_listEnumerationByCode} from '@/api/sysDictApi';

  function search() {
    this.tableQuery.page = 1;
    this.getList();
  }

  async function getList() {
    const result = await _queryOwnerNotOnlineProjectList(this.tableQuery);
    const tableData = result.data.curUserNotOnlineProject;
    tableData.forEach(t => {
      const circleNames = [];
      if (t.circles != null) {
        t.circles.forEach(c => circleNames.push(c.name))
      }
      // t['circleNames'] = circleNames.length === 0 ? '' : circleNames.join(' > ');
      if (this.sysDict !== null && this.sysDict.sysDictDataList !== null) {
        if (this.sysDict.sysDictDataList !== undefined) {
          const statusName = this.sysDict.sysDictDataList.filter(d => d.code === (t.hasOnLine + ""));
          t['statusName'] = statusName !== null && statusName.length > 0 ? statusName[0].name : "未上线";
        }
      }
    });
    this.tableData = tableData;
    this.tableQuery.total = result.data.totalAmount;
  }


  function handlePageChange(val) {
    this.tableQuery.page = val;
    this.getList();
  }


  async function getListEnumeration() {
    const result = await _listEnumerationByCode({code: 'PROJECT_ONLINE'});
    this.sysDict = result.data.sysDict;
  }

  function handleSizeChange(val) {
    this.tableQuery.size = val;
    this.getList();
  }

  function onLine(id, hasOnLine) {
    this.$confirm('这个操作修改项目线上状态，您想继续吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      const data = {
        id: id,
        hasOnLine: hasOnLine
      }
      await _updateProjectOnLine(data);
      this.getList();
      this.$root.$emit('getList', '');
    }).catch(() => {
      this.$message({
        type: 'info',
        message: '执行失败！',
      });
    });
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
        sysDict: {},
      }
    },
    computed: {},
    created: function () {
      const menu = JSON.parse(localStorage.getItem('MENUS'))
        .filter(menu => menu.code === 'NOT_ONLINE');
      console.log(menu)
      console.log(JSON.parse(localStorage.getItem('MENUS')))
      this.menu = menu[0];
      this.getListEnumeration();
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
      onLine,
      getListEnumeration,
      go
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
