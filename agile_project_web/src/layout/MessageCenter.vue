<template>

  <div>
    <el-popover
      placement="right-start"
      width="650"
      :visible-arrow="false"
      v-model="visible"
      :offset="10"
    >

      <div>
        <div style="font-size: 18px;font-weight: bold;margin-top: 10px;display: flex;height: 30px;line-height: 30px;">
          <div style="flex: 1">我的消息</div>
          <div style="width: 120px;vertical-align: top" v-if="activeName==='unread'">
            <el-button round style="vertical-align: top" @click="markAsReadAllMessage">全部标为已读</el-button>
          </div>
        </div>
        <el-tabs v-model="activeName" @tab-click="handleClick" class="tab-message">
          <el-tab-pane label="全部" name="all">
            <div>
              <div v-if="messageList.length===0" style="margin-top: 15px;font-size: 16px">无消息</div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="未读" name="unread">
            <div>
              <div v-if="messageList.length===0" style="margin-top: 15px;font-size: 16px">暂无未读消息
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
        <div style="max-height: 500px;overflow-y: auto;">
          <div v-for="message in messageList" :key="message.id" style="border-bottom: 1px #e1e1e1 solid;">

            <div style="width: 100%;display: flex;margin-top:15px">
              <div style="width: 30px;margin-left: 7px;margin-right: 10px;">
                <i :class="message.cen.color"
                   style="font-size: 16px;
                   line-height: 28px;text-align: center;color: #003E89;
                   border-radius: 50%;border: 1px #003E89 solid;width: 28px;height: 28px"></i>
              </div>
              <div style="flex: 1;">

                <span style="font-size: 16px;vertical-align: top;cursor: pointer" @click="toMore(message)">{{message.content}}</span>
              </div>

              <div style="width: 85px;vertical-align: top;margin-left: 7px;" v-if="activeName==='unread'">
                <el-button round style="vertical-align: top" @click="maskRead(message.id)">标为已读</el-button>
              </div>
            </div>

            <div style="width: 100%;display: flex;height: 30px;line-height: 30px;margin-top: 10px;margin-bottom: 10px">
              <div style="flex: 1"></div>
              <div style="width: 115px;vertical-align: top">{{message.updateAt}}</div>
              <div style="width: 80px;vertical-align: top">
                <el-tag size="small"
                        :type="message.tag">{{message.cen.name}}
                </el-tag>
              </div>
            </div>


          </div>

          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
            :current-page="this.tableQuery.page"
            background
            :pager-count="5"
            :page-sizes="[15, 30, 50]"
            :page-size="this.tableQuery.size"
            layout="total, sizes, prev, pager, next, jumper"
            :total="this.tableQuery.total">
          </el-pagination>
        </div>

        <!--    点击显示任务的dialog-->

        <el-dialog
          @close="closeTask"
          :visible.sync="taskDialogVisible"
          width="580px"
          append-to-body>
          <div slot="title" class="header-title">
            <span>{{taskInformation.name}}</span>
          </div>
          <task-edit :task="taskInformation"
                     :taskAttr="cols"
                     @save="closeTask"
                     @update="closeTask"
                     :need="false"
                     :reBeginAuthority="reBeginAuthority"
                     :authority="authority"
                     :rolePlus="rolePlus"></task-edit>
        </el-dialog>

      </div>

      <el-badge :value="newMessageNum" class="bell-container" slot="reference">
        <el-button size="small" type="text" class="fa fa-bell-o icon"></el-button>
      </el-badge>
    </el-popover>
  </div>

</template>

<script>
  import {_queryUserMessageList, _markAsReadMessage, _markAsReadAllMessage} from '@/api/messageApi';
  import {_message} from '@/utils/taskUtils';
  import TaskEdit from "../views/agile/project/task/kanban/TaskEdit";

  async function refresh() {
    const dictDataType = this.$store.getters.getDictionaryByKey('MESSAGE_TYPE');
    let mty = dictDataType.sysDictDataList;


    this.tableQuery.flag = !(this.activeName === 'unread')
    const result = await _queryUserMessageList(this.tableQuery);
    if (result.code === 1200) {
      this.tableQuery.total = result.data.totalAmount;
      result.data.messageList.forEach(ml => {
        this.$set(ml, 'cen', {});
        this.$set(ml, 'tag', '');
        switch (ml.type) {
          case 'PROJECT':
            ml.tag = 'success';
            break;
          case 'TASK':
            ml.tag = 'info';
            break;
          case 'CIRCLE':
            ml.tag = 'warning';
            break;
          case 'QA':
            ml.tag = 'danger';
            break;
          default:
            ml.tag = '';
            break

        }
        ml.cen = mty.filter(mt => ml.type !== undefined && ml.type !== null && mt.code === ml.type)[0]
      })
      this.messageList = result.data.messageList;
      this.newMessageNum = result.data.unreadTotalAmount;
    }

  }

  async function getUserMessage() {

    await this.refresh();

    const interval = setInterval(async () => {
      await this.refresh();
    }, 60000);


  }

  async function handleClick(tab, event) {
    this.activeName = tab.paneName;
    this.tableQuery.page = 1;

    await this.refresh();
  }


  // 单个标记为已读
  async function maskRead(messageId) {

    await _markAsReadMessage({messageId});

    await this.refresh();
  }


  // 全部
  async function markAsReadAllMessage() {

    await _markAsReadAllMessage();
    await this.refresh();
  }

  async function toMore(message) {

    switch (message.type) {
      case "TASK":
        this.visible = false;
        if (message.projectId !== null) {
          const result = await _message(message.projectId, this, this.types);
          this.taskInformation = result.allTaskList.filter(at => at.id === message.relationId)[0];
          this.cols = result.cols;
          this.taskDialogVisible = true
        }
        await this.maskRead(message.id);
        break;
      case "CIRCLE":
        this.visible = false
        this.$router.push({path: 'circle', query: {id: message.relationId}});
        await this.maskRead(message.id);
        break;
      case "PROJECT":
        this.visible = false
        this.$router.push({path: 'project', query: {id: message.relationId}});
        await this.maskRead(message.id);
        break;
      case "QA":

        this.visible = false
        await this.maskRead(message.id);
        break

    }

  }

  function closeTask() {

    this.taskDialogVisible = false;
    // this.refresh()
  }

  export default {
    components: {TaskEdit},
    data() {
      return {
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
          flag: false
        },
        newMessageNum: 0,
        visible: false,
        messageList: [],
        websocketUrl: "",
        activeName: 'unread',
        types: [],
        cols: [],
        taskDialogVisible: false,
        taskInformation: '',
        rolePlus: [],
        reBeginAuthority: false,
        authority: false,
      };
    },
    methods: {
      getUserMessage,
      refresh,
      handleClick,
      maskRead,
      markAsReadAllMessage,
      toMore,
      closeTask,
      startWebSocket: function () {
        var scope = this;

        // console.log("start websocket!!!");
        var url = `ws://${location.hostname}:9091/websocket/111`;

        var ws = new WebSocket(url);
        ws.onopen = function () {
          // console.log("连接成功!")
          // ws.send("212121212121")
        }
        ws.onmessage = function (evt) {
          var data1 = evt.data;
          // console.log(data1.toString())
        }
        ws.onclose = function () {
          // console.log("websocket 关闭,30s后重新建立连接");
          // setTimeout(scope.startWebSocket, 1000 * 30);
        }
      },
      handlePageChange(val) {
        this.tableQuery.page = val;
        this.refresh();
      },
      handleSizeChange(val) {
        this.tableQuery.size = val;
        this.refresh();
      },
    },
    created() {
      const dictDataType = this.$store.getters.getDictionaryByKey('TASK_TYPE');
      this.types = dictDataType.sysDictDataList;

      this.getUserMessage();
      this.startWebSocket();
    },
    watch: {
      websocketUrl: function (newValue, oldValue) {
        if (newValue !== "") {

        }
      },
      immediate: true
    }
  };
</script>
<style lang="scss" scoped>
  .icon {
    font-size: 30px;
    color: #D5D7D8;
    width: 100%;
    text-align: center;
    margin-bottom: 20px;
  }

  .bell-container {
    margin-top: 50px;
  }

</style>
<style lang="scss">
  .bell-container > {

    .el-badge__content.is-fixed {
      top: 12px;
      right: 15px;
    }
  }

  .tab-message > {
    .el-tabs__header {
      margin: 0;
    }
  }


</style>
