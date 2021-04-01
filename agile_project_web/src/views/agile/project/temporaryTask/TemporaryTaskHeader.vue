<template>
  <div class="body">
    <div class="header">
      <el-row style="margin-bottom:10px">
        <el-col :span="10">
          <div class="grid-content bg-purple"
               style="height: 40px;font-size: 29px;font-weight: normal;float: left">
            <span>临时任务</span>
          </div>
        </el-col>
      </el-row>

      <el-row
        style="margin-top: 15px;">
        <el-col :span="24">

          <div class="grid-content bg-purple" style="height: 36px;float: right;margin-left: 10px">
            <el-autocomplete
              class="inline-input"
              v-model="state1"
              size="medium"
              clearable
              style="float: left;width: 100px"
              :fetch-suggestions="querySearch"
              placeholder="人员"
              @select="handleSelect"
            ></el-autocomplete>

            <el-input
              size="medium"
              placeholder="请输入内容"
              v-model="searchValue"
              style="width: 328px; float: left"
            ><i slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
          </div>

          <div style="display: flex;justify-content: flex-end;float: right">
            <el-button type="primary"
                       round
                       size="medium"
                       :disabled="currentId!=='1'"
                       @click="addATask">
              新增临时任务
            </el-button>
          </div>
        </el-col>
      </el-row>
    </div>
    <div
      style="width:100%;box-sizing: content-box;margin-right: 20px;display: flex;overflow: hidden">
      <div v-if="currentId==='1'" style="height: 100%;width: 20px;background-color: #fff;z-index: 999">
      </div>
      <temporary-task-table ref="tableStyle"
                            v-if="currentId==='1'"
                            :searchValue="searchValue"
                            :person="state1"

                            :isAddTask="isAddTask"></temporary-task-table>


    </div>
  </div>
</template>
<script>
  import TemporaryTaskTable from "./TemporaryTaskTable";
  import {_listTemporaryTask} from "../../../../api/taskApi";


  /**
   * 初始化,搜索框获取人员
   * @returns {Promise<void>}
   */
  async function getPerson() {
    const result = await _listTemporaryTask();
    this.persons = this.loadAll(result.data.taskUserList)
  }


  /**
   * 新增任务
   */
  function addATask() {

    this.isAddTask = !this.isAddTask;
  }


  export default {
    components: {
      TemporaryTaskTable
    },
    data() {
      return {
        token: localStorage.getItem('token'),
        id: '',

        // 当前呈现形式的id
        currentId: '1',


        searchValue: '',

        dateStatusList: [],
        dateStatusList2: [],


        // 项目操作权限数组
        userId: localStorage.getItem('id'),

        isAddTask: false,
        type: [],
        priority: [],

        persons: [],
        state1: ''

      };
    },
    methods: {

      addATask,
      getPerson,


      querySearch(queryString, cb) {
        const persons = this.persons;

        const results = queryString ? persons.filter(this.createFilter(queryString)) : persons;
        // 调用 callback 返回建议列表的数据
        cb(results);
      },
      createFilter(queryString) {
        return (person) => {
          return (person.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
        };
      },
      loadAll(list) {
        const la = []
        list.forEach(l => {
          if (l.reviewUser !== null) {
            la.push(l.reviewUser.realName)
          }
          if (l.confirmUser !== null) {
            la.push(l.confirmUser.realName)
          }
        })

        const corker = Array.from(new Set(la))

        return corker.map(x => {
          return {value: x}
        })
      },
      handleSelect(item) {
        console.log(item);
      }
    },
    created() {
      const dictData = this.$store.getters.getDictionaryByKey('TASK_STATUS');
      this.dateStatusList = dictData.sysDictDataList;

      const dictData2 = this.$store.getters.getDictionaryByKey('TASK_DATE_STATUS');
      this.dateStatusList2 = dictData2.sysDictDataList;


      const dictDataType = this.$store.getters.getDictionaryByKey('TASK_TYPE');
      this.types = dictDataType.sysDictDataList;

      const dictDataPriority = this.$store.getters.getDictionaryByKey('PRIORITY');
      this.priority = dictDataPriority.sysDictDataList;


      this.getPerson()
    },
    watch: {},
  };
</script>
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
    overflow-y: hidden;
  }

</style>
<style>
  .el-autocomplete-suggestion__wrap, .el-scrollbar__wrap {
    margin-bottom: -5px !important;
  }
</style>
