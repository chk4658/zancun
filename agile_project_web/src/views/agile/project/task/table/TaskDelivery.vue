<template>
  <div>
    <div>
      <el-form :model="form" :rules="rules" ref="ruleForm" label-width="150px" style="width:960px">
        <el-form-item label="交付物列表">
          <div style="width: 780px">
            <el-upload
              class="upload-demo"
              action="/api/upload"
              :limit="20"
              :headers="{
                 token: token
               }"
              :on-exceed="handleExceed"
              :on-success="uploadSuccess"
              :show-file-list="false"
              v-hasProOperation="'{\'taskReviewerId\':\'' + (taskInfo.reviewUser===null?taskInfo.reviewUser:taskInfo.reviewUser.id) +'\', \'key\': \'hasAddTaskDelivery\',\'taskStatus\':\'' + taskInfo.status +'\'}'"
              :file-list="fileList">
              <el-button size="small" type="text" icon="el-icon-plus" v-if="fileList.length<20">添加附件</el-button>
              <div style="display: inline-block;margin-left: 15px;margin-right: 15px;font-size: 12px;color: #606266;"
                   @click.stop="">
                (最多上传20个，最大不超过100m)
              </div>
              <div style="display: inline-block">
                <el-button size="small" type="text" icon="el-icon-plus" @click.stop="connect"
                           v-if="fileList.length<20&&(taskInfo.status==='2'||taskInfo.status==='4'||taskInfo.status==='5')">
                  连接交付物
                </el-button>
              </div>
            </el-upload>
            <div v-for="i in fileList" :key="i.id">
              <div
                style="width: 780px; height:37px;line-height:37px;background-color:#eee;margin-bottom: 2px;padding-left: 10px;
                padding-right:3px;box-sizing: border-box;">
                <div
                  style="display: inline-block;max-width: 350px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;"
                  :style="{'text-decoration':i.auditStatusId===2?'line-through': ''}">
                  {{i.name}}
                </div>
                <div style="display: inline-block;vertical-align: top">
                  <el-button v-if="checkSuffix(i)"
                             style="margin-left:10px" :disabled="pdf.loading" type="text" icon="el-icon-view"
                             @click="viewFile(i)">
                  </el-button>
                  <el-button type="text" icon="el-icon-download" @click="downFile(i)"></el-button>
                </div>

                <div
                  style="display: inline-block;max-width: 160px;overflow: hidden;white-space: nowrap;
                  text-overflow: ellipsis;margin-left: 15px;font-size: 12px;color: #bbb">
                  提交于{{i.submitAt}}
                </div>


                <div style="float: right;width: 14px;height: 29px">
                  <i class="el-icon-delete"
                     v-if="i.auditStatusId===0"
                     v-hasProOperation="'{\'taskReviewerId\':\'' + (taskInfo.reviewUser===null?taskInfo.reviewUser:taskInfo.reviewUser.id) +'\', \'key\': \'hasDeleteTaskDelivery\',\'taskDeliveryAuditStatus\':\'' + i.auditStatusId +'\',\'taskStatus\':\'' + taskInfo.status +'\'}'"
                     style="height: 29px;line-height: 29px;cursor: pointer"
                     @click="deleteTaskDelivery(i)"></i></div>


                <div style="float: right;margin-left: 40px;margin-right: 20px;width: 26px;height: 28px">
                  <el-popover
                    placement="left"
                    width="150"
                    v-model="btnContainerVisible[i.id]"
                    trigger="click">
                    <div>
                      <div class="show_style_label" @click="reviewTaskDelivery(i,1)">通过</div>
                      <div class="show_style_label" @click="reviewTaskDelivery(i,2)">拒绝</div>
                      <div class="show_style_label" @click="reviewTaskDelivery(i,0)">取消</div>
                    </div>
                    <el-button type="text"
                               slot="reference"
                               @click="changeVisible(i.id)"
                               v-hasProOperation="'{\'taskConfirmerId\':\'' + (taskInfo.confirmUser===null?taskInfo.confirmUser:taskInfo.confirmUser.id) +'\', \'key\': \'hasReviewTaskDelivery\',\'taskDeliveryAuditStatus\':\'' + i.auditStatusId +'\',\'taskStatus\':\'' + taskInfo.status +'\'}'"
                    >
                      审核
                    </el-button>
                  </el-popover>

                </div>

                <div style="float: right;width: 14px;height: 29px" v-if="i.auditStatusId===2&&i.isReupload!==1">
                  <el-upload
                    class="upload-demo"
                    action="/api/upload"
                    :limit="20"
                    :headers="{token: token}"
                    :on-exceed="handleExceed"
                    :on-success="reUploadSuccess"
                    :show-file-list="false"
                    v-hasProOperation="'{\'taskReviewerId\':\'' + (taskInfo.reviewUser===null?taskInfo.reviewUser:taskInfo.reviewUser.id) +'\', \'key\': \'hasReAddTaskDelivery\',\'taskStatus\':\'' + taskInfo.status +'\'}'"
                    :file-list="fileList">
                    <el-button size="small" type="text" v-if="fileList.length<20" style="margin-left: 10px;"
                               @click="chooseReUploadId(i)">重新上传
                    </el-button>
                  </el-upload>
                </div>
                <div style="float: right">
                  <el-tooltip placement="top" :disabled="!(i.auditStatusId===2)">
                    <div slot="content">
                      <div>
                        拒绝原因：<br/>
                        <div>
                          <span>{{i.refuseReason}}</span><br/>
                        </div>
                      </div>
                    </div>
                    <span :style="{color:i.color}">{{i.auditStatus}}</span>
                  </el-tooltip>

                </div>

              </div>
            </div>
          </div>
        </el-form-item>

        <el-dialog
          title="连接交付物"
          @close="closeConnect"
          :visible.sync="dialogVisible.connect"
          width="700px"
          center
          append-to-body>

          <el-input
            placeholder="请输入交付物关键字"
            maxlength="20"
            show-word-limit

            style="margin: 10px 0 20px"
            v-model="connectValue"
            @input="search"></el-input>

          <div v-if="connectList.length===0"
               style="border: 1px #DBDEE5 dashed;color: #DBDEE5;text-align: center;height: 50px;line-height: 50px">
            暂无数据
          </div>
          <div v-else
               style="border: 1px #DBDEE5 dashed;text-align: center;">
            <div v-for="cl in connectList" :key="cl.id">
              <div class="show_style_label"
                   @click="connectDelivery(cl)">
                <span>{{cl.name}}</span>
              </div>
            </div>
          </div>

          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
            background
            :current-page="this.tableQuery.page"
            :page-sizes="[15, 30, 50]"
            :pager-count="5"
            :page-size="this.tableQuery.size"
            style="text-align: center;margin-top: 20px"
            layout="total, sizes, prev, pager, next, jumper"
            :total="this.tableQuery.total">
          </el-pagination>

        </el-dialog>


        <!--    拒绝 填写理由-->
        <el-dialog
          title="请填写拒绝理由"
          @close="dialogVisible.reason=false"
          :visible.sync="dialogVisible.reason"
          width="600px"
          center
          append-to-body>

          <el-input
            type="textarea"
            :rows="5"
            placeholder="使用Enter保存"
            v-model="reasonValue"
            @keydown.native="changeReason($event)"></el-input>

          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible.reason=false">取 消</el-button>
            <el-button type="primary" @click="changeReason($event,'BUTTON')">确 定</el-button>
          </span>


        </el-dialog>


      </el-form>
      <el-dialog
        class="abow_dialog"
        @close="dialogVisible.preview = false"
        :visible.sync="dialogVisible.preview"
        width="80%"
        center
        append-to-body>
        <div slot="title" class="header-title">
          {{this.pdf.file.name}}
        </div>
        <div>
          <PdfView :sourceSrc="this.pdf.src"></PdfView>
        </div>
      </el-dialog>
      <a id="download-a"></a>
    </div>
  </div>
</template>
<script>
  import {
    _listTaskDeliveriesByTaskId,
    _addTaskDelivery,
    _deleteByIdAndReferId,
    _findAllDelivery
  } from '@/api/taskDeliveryApi';
  import moment from "moment";

  import {
    _toPdfFile, _fileCheck
  } from '@/api/fileApi';


  import {downloadFile, urlRemoveSpecial} from '@/api/utils';


  import PdfView from '@/components/PdfView';

  async function setInfo() {

    const result = await _listTaskDeliveriesByTaskId({taskId: this.taskInfo.id});

    let arr = []
    this.fileList = []
    if (result.code === 1200 && result.data.taskDeliveryList.length > 0) {
      this.copyDelivery = result.data.taskDeliveryList
      result.data.taskDeliveryList.forEach((i) => {
        console.log(i)
        if (i.auditStatus !== null && i.auditStatus !== '') {
          this.currentValue = this.options.filter(x => x.code.toString() === i.auditStatus.toString())[0];
        }
        arr.push({
          name: i.deliveryName,
          url: i.deliveryList.length > 0 ? i.deliveryList[0].path : '',
          id: i.id,
          auditStatus: this.currentValue.name,
          color: this.currentValue.color,
          submitUserId: i.submitUserId,
          auditStatusId: i.auditStatus,
          deliveryId: i.deliveryId,
          taskId: i.taskId,
          submitAt: i.submitAt,
          refuseReason: i.refuseReason,
          isReupload: i.isReupload,
          reuploadList: i.reuploadList
        });
      });
    }
    this.fileList = JSON.parse(JSON.stringify(arr))
  }

  function changeVisible(id) {
    Object.keys(this.btnContainerVisible)
      .forEach((key) => {
        if (key !== id && this.btnContainerVisible[key] === true) {
          this.$set(this.btnContainerVisible, key, false);
        }
      });

    this.$set(this.btnContainerVisible, id, this.btnContainerVisible[id]);
  }

  async function uploadSuccess(res, file) {

    const id = localStorage.getItem('id');

    const result = await _addTaskDelivery({

      taskId: this.taskInfo.id,
      deliveryName: file.name,
      submitUserId: id,
      auditStatus: 0,
      path: res.data.path,
      projectId: this.taskInfo.projectId,
      deliverySize: file.size,
      submitAt: moment(new Date()).format('YYYY-MM-DD HH:mm:ss')
    });
    if (result.code === 1200) {
      await this.setInfo();
      this.$emit('refresh', '', '', this.copyDelivery)
    }
  }

  function chooseReUploadId(i) {
    this.reUploadId = i.id
  }


  async function reUploadSuccess(res, file) {
    const id = localStorage.getItem('id');
    const result = await _addTaskDelivery({

      taskId: this.taskInfo.id,
      deliveryName: file.name,
      submitUserId: id,
      auditStatus: 0,
      path: res.data.path,
      projectId: this.taskInfo.projectId,
      deliverySize: file.size,
      reUploadId: this.reUploadId,
      submitAt: moment(new Date()).format('YYYY-MM-DD HH:mm:ss')
    });
    this.reUploadId = ''
    if (result.code === 1200) {
      await this.setInfo();
      this.$emit('refresh', '', '', this.copyDelivery)
    }
  }

  async function connectDelivery(cl) {
    const id = localStorage.getItem('id');
    console.log(cl)

    const result = await _addTaskDelivery({
      taskId: this.taskInfo.id,
      deliveryName: cl.name,
      submitUserId: id,
      path: cl.path,
      auditStatus: 0,
      projectId: this.taskInfo.projectId,
      deliveryId: cl.id,
      submitAt: moment(new Date()).format('YYYY-MM-DD HH:mm:ss')
    });
    if (result.code === 1200) {

      await this.setInfo();

      this.dialogVisible.connect = false;

      this.$emit('refresh', '', '', this.copyDelivery)
    }
  }

  async function deleteTaskDelivery(item) {
    this.$confirm('是否删除?', '删除交付物', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        const referList = this.fileList.filter(fl => {
          if (fl.reuploadList !== null && fl.reuploadList !== '') {
            return fl.reuploadList.indexOf(item.id) !== -1
          }
        })
        let afterReferList = ''
        if (referList.length > 0) {
          const array = referList[0].reuploadList.split('||');
          const index = array.indexOf(item.id)
          array.splice(index, 1)
          afterReferList = array.join("||")
        }
        const result = await _deleteByIdAndReferId({
          id: item.id,
          referId: referList.length > 0 ? referList[0].id : "",
          afterReferList: afterReferList
        });
        // this.fileList.some((it, i) => {
        //   if (it.id === item.id) {
        //     this.fileList.splice(i, 1);
        //     return true;
        //   }
        // });
        if (result.code === 1200) {
          await this.setInfo();
          this.$message({
            message: '删除成功',
            type: 'success',
            center: true,
          });
          this.$emit('refresh', '', '', this.copyDelivery)
        }

      });
  }

  async function reviewTaskDelivery(item, statusValue) {
    if (statusValue === 2) {
      this.dialogVisible.reason = true;
      this.reasonValue = '';
      this.reasonTaskObject = item;
      this.$set(this.btnContainerVisible, item.id, !this.btnContainerVisible[item.id]);
      return
    }
    if (statusValue !== 0) {
      const aData = new Date();
      const id = localStorage.getItem('id');
      const result = await _addTaskDelivery({
        taskId: this.taskInfo.id,
        id: item.id,
        deliveryId: item.deliveryId,
        auditStatus: statusValue,
        confirmUserId: id,
        projectId: this.taskInfo.projectId,
        auditAt: moment(aData).format('YYYY-MM-DD HH:mm:ss'),
      });
      if (result.code === 1200) {
        await this.setInfo();
        this.$emit('refresh', '', '', this.copyDelivery)
      }
    }
    this.$set(this.btnContainerVisible, item.id, !this.btnContainerVisible[item.id]);
  }


  async function changeReason(event, button) {
    const code = event.keyCode || event.which
    if ((!event.ctrlKey && code === 13) || (button !== undefined && button === 'BUTTON')) {
      if (this.reasonValue.replace(/(^\s*)|(\s*$)/g, "") === '') {
        event.preventDefault();
        this.$message({
          message: '写点什么吧',
          type: 'warning',
          center: 'true',
        });
        return false
      }
      this.dialogVisible.reason = false;
      const aData = new Date();
      const id = localStorage.getItem('id');
      const result = await _addTaskDelivery({
        taskId: this.taskInfo.id,
        id: this.reasonTaskObject.id,
        deliveryId: this.reasonTaskObject.deliveryId,
        auditStatus: 2,
        confirmUserId: id,
        projectId: this.taskInfo.projectId,
        auditAt: moment(aData).format('YYYY-MM-DD HH:mm:ss'),
        refuseReason: this.reasonValue.replace(/(^\s*)|(\s*$)/g, ""),
      });
      if (result.code === 1200) {
        Object.keys(this.btnContainerVisible)
          .forEach((key) => {
            this.$set(this.btnContainerVisible, key, false);
          });
        this.$emit('refresh', 'REFUSE', this.taskInfo, this.copyDelivery);
      }
    }


  }


  async function connect() {

    this.connectValue = '';
    this.connectList = [];
    this.dialogVisible.connect = true;
  }

  function closeConnect() {

    this.dialogVisible.connect = false;
  }

  async function search() {


    const val = this.connectValue.replace(/(^\s*)|(\s*$)/g, "");
    if (val !== '') {
      this.tableQuery.deliveryName = val
      this.tableQuery.page = 1;
      const result = await _findAllDelivery(this.tableQuery)
      if (result.code === 1200) {
        this.connectList = result.data.deliveryList;
        this.tableQuery.total = result.data.totalAmount
      }

    } else {
      this.connectList = []
      this.tableQuery.page = 1;
      this.tableQuery.total = 0
    }
  }

  async function viewFile(file) {
    this.pdf.file = file;
    let previewPath = "";
    const _suffix = file.name.substring(file.name.lastIndexOf('.') + 1);
    const suffix = _suffix.toLowerCase();
    if (this.checkSuffixIsView(file)) {
      previewPath = file.url;
    } else {
      this.pdf.loading = true;
      const result = await _toPdfFile({sourceFile: file.url});
      previewPath = result.data.previewPath;
    }
    if (previewPath !== undefined && previewPath !== "") {
      const fileCheck = await _fileCheck({filePath: previewPath});
      if (!fileCheck.data.check) {
        this.$message.info(this.checkSuffixIsView(file) ? '文件不存在' : `正在全力转换文件格式，请稍后再试！`);
      } else {
        previewPath = urlRemoveSpecial(previewPath);
        var a = document.createElement('a');
        a.setAttribute('href', "/api" + previewPath);
        a.setAttribute('target', '_blank');
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
      }
    } else this.$message.error(`文件不存在`);
    this.pdf.loading = false;
  }

  function downFile(file) {
    downloadFile(file.url).then((response1) => {
    });
  }


  function checkSuffix(file) {
    const _suffix = file.name.substring(file.name.lastIndexOf('.') + 1);
    const suffix = _suffix.toLowerCase();
    return suffix === 'xls'
      || suffix === 'xlsx'
      || suffix === 'doc'
      || suffix === 'docx'
      || suffix === 'txt'
      || suffix === 'ppt'
      || suffix === 'pptx'
      || suffix === 'pdf'
      || suffix === 'jpg'
      || suffix === 'png'
      || suffix === 'bmp'
  }


  function checkSuffixIsView(file) {
    const _suffix = file.name.substring(file.name.lastIndexOf('.') + 1);
    const suffix = _suffix.toLowerCase();
    return suffix === 'pdf'
      || suffix === 'jpg'
      || suffix === 'png'
      || suffix === 'bmp'
  }

  export default {
    name: 'TaskDelivery',
    props: {
      taskInfo: Object,
    },
    components: {PdfView},
    data() {
      return {
        token: localStorage.getItem('token'),
        form: {},
        fileList: [],
        currentValue: {},
        rules: {},
        options: [],

        btnContainerVisible: {},

        reUploadId: '',

        reasonTaskObject: {},
        reasonValue: '',

        dialogVisible: {connect: false, reason: false, preview: false},

        connectList: [],
        connectValue: '',
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
          deliveryName: ''
        },
        pdf: {
          file: '',
          src: "",
          loading: false,
        },
        copyDelivery: []

      };
    },
    created() {

      const dictData = this.$store.getters.getDictionaryByKey('AUDIT_STATUS');
      this.options = dictData.sysDictDataList;

      this.setInfo();
    },
    methods: {
      handleExceed(files, fileList) {
        this.$message.warning(`当前限制选择 20 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },
      uploadSuccess,
      setInfo,
      deleteTaskDelivery,
      reviewTaskDelivery,
      changeVisible,
      connect,
      closeConnect,
      search,
      connectDelivery,
      changeReason,
      reUploadSuccess,
      chooseReUploadId,
      async handlePageChange(val) {
        this.tableQuery.page = val;
        const value = this.connectValue.replace(/(^\s*)|(\s*$)/g, "");
        if (value !== '') {
          this.tableQuery.deliveryName = value
          const result = await _findAllDelivery(this.tableQuery)
          if (result.code === 1200) {
            this.connectList = result.data.deliveryList;
            this.tableQuery.total = result.data.totalAmount
          }

        } else {
          this.connectList = []
          this.tableQuery.page = 1;
          this.tableQuery.total = 0
        }
      },
      handleSizeChange(val) {
        this.tableQuery.page = 1;
        this.tableQuery.size = val;
        this.search();
      },
      viewFile,
      checkSuffix,
      downFile,
      checkSuffixIsView
    },
    watch: {
      taskInfo: function (newVal, oldVal) {
        const dictData = this.$store.getters.getDictionaryByKey('AUDIT_STATUS');
        this.options = dictData.sysDictDataList;
        this.setInfo();
      },
    },
  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .show_style_label {
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    height: 28px;
    font-size: 15px;
    line-height: 28px;
    padding-left: 30px;
  }

  .show_style_label:hover {
    background-color: #DBE4EE;
    color: #01408B;
  }

  .pdf-view {
    /* overflow-y: auto; */
  }
</style>
