<template>
  <div>
    <div>
      <el-form :model="form" :rules="rules" ref="ruleForm" label-width="150px" style="width:960px">
        <el-form-item label="交付物列表">
          <div style="width: 780px">
            <div v-for="i in fileList" :key="i.id">
              <div
                style="width: 780px; height:37px;line-height:37px;background-color:#eee;margin-bottom: 2px;padding-left: 10px;
                padding-right:3px;box-sizing: border-box;">
                <div
                  style="display: inline-block;max-width: 350px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;">
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
  } from '@/api/taskDeliveryApi';
  import moment from "moment";

  import {
    _toPdfFile,_fileCheck
  } from '@/api/fileApi';


  import PdfView from '@/components/PdfView';


  import { downloadFile,urlRemoveSpecial } from '@/api/utils';

  async function setInfo() {

    const result = await _listTaskDeliveriesByTaskId({taskId: this.taskInfo.id});
    let arr = []
    this.fileList = []
    if (result.code === 1200 && result.data.taskDeliveryList.length > 0) {
      result.data.taskDeliveryList.forEach((i) => {
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
          submitAt: i.submitAt,
          taskId: i.taskId,
          refuseReason: i.refuseReason,
          isReupload: i.isReupload,
          reuploadList: i.reuploadList
        });
      });
    }
    this.fileList = JSON.parse(JSON.stringify(arr))
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
      const fileCheck = await _fileCheck({filePath:previewPath});
      if (!fileCheck.data.check) {
        this.$message.info(this.checkSuffixIsView(file) ? '文件不存在' : `正在全力转换文件格式，请稍后再试！`);
      } else {
        previewPath =  urlRemoveSpecial(previewPath);
        var a = document.createElement('a');
        a.setAttribute('href', "/api" + previewPath);
        a.setAttribute('target', '_blank');
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
      }
      
    } else {
      this.$message.error(`文件不存在`);
    }
    this.pdf.loading = false;
  }

  function downFile(file) {
    downloadFile(file.url).then((response1) => {});
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
    name: 'taskDeliveryOnlyRead',
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

        dialogVisible: {preview: false},
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
        }

      };
    },
    created() {

      const dictData = this.$store.getters.getDictionaryByKey('AUDIT_STATUS');
      this.options = dictData.sysDictDataList;

      this.setInfo();
    },
    methods: {
      setInfo,
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
