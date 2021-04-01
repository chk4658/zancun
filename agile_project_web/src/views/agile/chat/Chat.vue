<template>
  <div class="chat" :style="{maxHeight: visibleHeight + 'px'}">
    <div style="width: 98%;">
      <el-row :gutter="20">
        <el-col :span="20">
          <el-input style="border-radius: 10px"
                    type="textarea"
                    :rows="2"
                    v-model="content"
                    @input="contentInput(content,null)"
                    :readonly="userDialogVisible"
                    v-show="has">
          </el-input>

        </el-col>
        <el-col :span="4">
          <el-button icon="el-icon-pie-chat" class="question-button" plain @click="save(content,'','')"
           v-show="has"
          >发布</el-button>
        </el-col>
      </el-row>
    </div>


    <div :style="'max-height:' + (visibleHeight - 130) + 'px;overflow-y:auto;margin-top:10px;'">
      <div v-for="chat in chats" :key="chat.id">
        <div class="answer">
          <div class="answer-p">
            <div class="answer-p-1">
              <div style="display:flex;">
                <div>
                  <i class="el-icon-user-solid answer-p-icon"></i>
                </div>
                <div style="margin-left:5px;font-size: 15px;">{{ chat.createUser.realName }}</div>
              </div>
              <div style="margin-top:10px;font-size: 14px;">{{ chat.content }}</div>

              <div style="position:absolute;right:30px;bottom:5px;">
                <el-row style="width:180px">
                  <el-col :span="20" style="padding-top:5px">
                    <span style="font-size: 14px;color:#686868;">{{ chat.createAt }}</span>
                  </el-col>
                  <el-col :span="4">
                    <el-button type="text" @click="contentBlur(chat)"
                     v-show="has"
                    >{{ chat.isReply ? '取消回复' : '回复' }}</el-button>
                  </el-col>
                </el-row>
              </div>
            </div>
            <div style="padding:10px;" v-show="chat.isReply">
              <el-row :gutter="10">
                <el-col :span="20">
                  <el-input style="border-radius: 10px"
                            v-focus2="chat.isReply"
                            type="textarea"
                            :rows="1"
                            :placeholder="'回复' + chat.createUser.realName"
                            v-model="chat['content' + chat.id].content"
                            @input="contentInput(chat['content' + chat.id].content,chat)"
                            :readonly="userDialogVisible">
                  </el-input>
                </el-col>
                <el-col :span="4">
                  <el-button icon="el-icon-pie-chat" class="question-button" plain style=""
                             @click="save(chat['content' + chat.id].content,chat.id,'')"
                             v-show="has"
                             >发布
                  </el-button>
                </el-col>
              </el-row>
            </div>


            <div v-for="_chat in chat.children" :key="_chat.id">
              <div style="border-top: 1px #e1e1e1 solid;">
                <div class="answer-p-2">
                  <div style="display:flex;">
                    <div>
                      <i class="el-icon-user-solid answer-p-icon"></i>
                    </div>
                    <div style="margin-left:5px;">
                      <div>
                        {{ _chat.createUser.realName }}
                        <span style="margin:0 3px 0 3px" v-if="_chat.reply !== null">回复</span>
                        <span v-if="_chat.reply !== null">{{ _chat.reply.createUser.realName }}</span>
                      </div>
                    </div>
                  </div>
                  <div style="margin-top:10px;margin-bottom: 20px;font-size: 14px">{{ _chat.content }}</div>
                  <div style="position:absolute;bottom: 5px;right:30px">
                    <el-row style="width:180px">
                      <el-col :span="20" style="padding-top:5px">
                        <span style="font-size: 14px;color:#686868;">{{ _chat.createAt }}</span>
                      </el-col>
                      <el-col :span="4">
                        <el-button type="text" @click="contentBlur(_chat)"
                          v-show="has"
                        >{{ _chat.isReply ? '取消回复' : '回复' }}
                        </el-button>
                      </el-col>
                    </el-row>
                  </div>
                </div>
              </div>
              <div style="width: 95%;padding:10px;" v-show="_chat.isReply">
                <el-row :gutter="20">
                  <el-col :span="20">
                    <el-input style="border-radius: 10px"
                              v-focus2="_chat.isReply"
                              type="textarea"
                              :rows="1"
                              :placeholder="'回复' + _chat.createUser.realName"
                              v-model="_chat['content' + _chat.id].content"
                              @input="contentInput(_chat['content' + _chat.id].content,_chat)"
                              :readonly="userDialogVisible">
                    </el-input>
                  </el-col>
                  <el-col :span="4">
                    <el-button icon="el-icon-pie-chat" class="question-button" plain style=""
                               @click="save(_chat['content' + _chat.id].content,chat.id,_chat.id)"
                               v-show="has"
                               >发布
                    </el-button>
                  </el-col>
                </el-row>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>


    <el-dialog
      title="选择用户"
      @close="closeUser"
      :visible.sync="userDialogVisible"
      width="1200px"
      append-to-body>
      <user-tree
        :isSingle='true'
        :usersProp='users'
        @getUsers="getUsers"
        @getCancel="closeUser">
      </user-tree>
    </el-dialog>

  </div>
</template>


<script>

import {
  _queryChatListAll,
  _updateChat
} from '@/api/chatApi';


import UserTree from '@/components/UserTree';


async function getChats() {
  const result = await _queryChatListAll({foreignId: this.foreignId, type: this.type});
  if (result && result.code === 1200) {
    const chats = result.data.chatList;
    chats.forEach(chat => {
      chat['isReply'] = false;

      chat['content' + chat.id] = {
        content: ""
      };
      if (chat.children !== null) {
        chat.children.forEach(c => {
          c['content' + c.id] = {
            content: ""
          };
          c['isReply'] = false
        });
      }
    });
    this.chats = chats;
  }
}


async function save(content, parentId, replyId) {

  if (content === '') {
    this.$message({
      type: 'info',
      message: '内容不能为空',
    });
  } else {
    let user = "";
    let has = false;
    for (let i = 0; i < content.length; i++) {
      const chart = content.charAt(i);
      if (has) user += chart;
      if (chart === "@") has = true;
      else if (chart === " ") has = false;
    }
    const users = user.split(" ");
    const atUserIds = this.atUsers.filter(au => users.indexOf(au.realName) > -1).map(au => au.id).join(',');
    const data = {
      id: '',
      content: content,
      parentId: parentId,
      replyId: replyId,
      foreignId: this.foreignId,
      type: this.type,
      atUserIds: atUserIds
    }
    await _updateChat(data);
    if (parentId === '') this.content = '';
    this.getChats();
  }


}

function contentBlur(chat) {
  chat['content' + chat.id].content = "";
  chat.isReply = !chat.isReply;
}


function contentInput(val, atChat) {
  this.atChat = atChat;
  const c = val.charAt(val.length - 1);
  if (c === '@') {
    this.userDialogVisible = true;
  }
}

function closeUser() {
  this.users = [];
  this.userDialogVisible = false;
}


function getUsers(data) {
  if (data.length > 0) {
    this.users = data;
    if (this.atChat === null) {
      this.content += (data[0].realName + ' ');
    } else {
      this.atChat['content' + this.atChat.id].content += (data[0].realName + ' ');
    }
    const atUsers = this.atUsers.filter(au => au.id === data[0].id);
    if (atUsers.length === 0) {
      this.atUsers.push({
        id: data[0].id,
        realName: data[0].realName,
      });
    }

  }
  this.closeUser();
}


export default {
  name: 'chat',
  components: {UserTree},
  props: {
    foreignId: String,
    type: String,
    has: Boolean,
  },
  data() {
    return {
      chats: [],
      content: '',
      userDialogVisible: false,
      users: [],
      atUsers: [],
      atChat: null,

    }
  },
  computed: {
    visibleHeight: function () {
      const browserHeight = window.innerHeight;
      const headerHeight = 80;
      return (browserHeight - headerHeight);
    },
  },
  created: function () {
    this.getChats();
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
    getChats,
    save,
    contentBlur,
    contentInput,
    closeUser,
    getUsers
  },
  watch: {
    foreignId(val) {
      this.getChats();
    }
  },
  directives: {}
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>


.question-button {
  position: absolute;
  bottom: 0px;
  height: 100%;
  width: 125px;
  border-radius: 10px
}

.answer {
  border-radius: 10px;
  border: 1px #e1e1e1 solid;
  margin-bottom: 5px;


  .answer-p {
    .answer-p-1 {
      position: relative;
      padding: 15px;
      padding-bottom: 35px;

      .answer-p-icon {
        font-size: 15px;
        width: 20px;
        height: 20px;
      }
    }

    .answer-p-2:last-child {
      padding-bottom: 15px;
    }

    .answer-p-2 {
      padding: 15px;
      position: relative;
      

      .answer-p-icon {
        font-size: 15px;
        width: 20px;
        height: 20px;
      }
    }

    .answer-p-icon {
      border: 1px #e1e1e1 solid;
      border-radius: 100px;
      text-align: center;
      display: table-cell;
      vertical-align: middle;
    }

  }
}

</style>
