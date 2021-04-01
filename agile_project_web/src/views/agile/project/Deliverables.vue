<template>
  <div>
    <el-input
      size="medium"
      placeholder="搜索"
      @input="search"
      style="width:60%;margin-bottom:10px"
      v-model="tableQuery.searchName">
      <i slot="prefix" class="el-input__icon el-icon-search"></i>
    </el-input>
    <div style="overflow-y: auto">
      <el-table
        class="maxh-table"
        :data="tableData"
        @filter-change="handleFilterChange"
        style="width: 100%">
        <el-table-column
          prop="task.name"

          label="所属任务">
        </el-table-column>
        <el-table-column
          align="center"
          :filters="filterDeliveryName"
          column-key="deliveryName"
          prop="deliveryName"
          label="文件名">
          <template slot-scope="scope">
            <!-- <a :href="'/api'+scope.row.url" download="">{{scope.row.deliveryName !== null && scope.row.deliveryName !== ''
              ? scope.row.deliveryName : ''}}</a> -->
            <!-- <el-link type="primary" :disabled="pdf.loading" @click="viewFile(scope.row)">{{scope.row.deliveryName !==
              null && scope.row.deliveryName !== ''
              ? scope.row.deliveryName : ''}}
            </el-link> -->
            <!--            <el-button :disabled="pdf.loading" type="text" @click="viewFile(scope.row)">{{scope.row.deliveryName !== null && scope.row.deliveryName !== ''-->
            <!--              ? scope.row.deliveryName : ''}}</el-button>-->
            {{scope.row.deliveryName !== null && scope.row.deliveryName !== ''
            ? scope.row.deliveryName : ''}}
          </template>
        </el-table-column>
        <el-table-column
          prop=".realName"
          align="center"
          label="">
          <template slot-scope="scope">
            <el-button v-if="checkSuffix(scope.row)"
                       style="margin-left:10px" :disabled="pdf.loading" type="text" icon="el-icon-view"
                       @click="viewFile(scope.row)">
            </el-button>
            <el-button type="text" icon="el-icon-download" @click="downFile(scope.row)"></el-button>
          </template>
        </el-table-column>
        <el-table-column
          prop="submitUser.realName"
          align="center"
          label="上传者">
        </el-table-column>
        <el-table-column
          prop="submitAt"

          align="center"
          label="上传时间">
        </el-table-column>
        <el-table-column
          prop="auditStatusName"
          align="center"
          label="审核状态">
        </el-table-column>
        <el-table-column
          prop="confirmUser.realName"
          align="center"
          label="确认人">
        </el-table-column>
        <el-table-column
          align="center"

          prop="auditAt"
          label="审核时间">
        </el-table-column>
      </el-table>
      <el-pagination
        @current-change="handlePageChange"
        :current-page="this.tableQuery.page"
        :page-size="this.tableQuery.size"
        background
        style="text-align: left;
      padding-left: 21px;margin-top: 5px"
        layout="total, prev, pager, next"
        :total="this.tableQuery.total">
      </el-pagination>
    </div>
    <el-dialog :v-loading="true"
               class="abow_dialog"
               :title="pdf.file.deliveryName"
               @close="pdf.dialogVisible = false"
               :visible.sync="pdf.dialogVisible"
               width="80%"
               center
               append-to-body>
      <div>
        <PdfView :sourceSrc="this.pdf.src"></PdfView>
      </div>
    </el-dialog>
    <a id="download-a"></a>

  </div>
</template>
<script>
  import {_listTaskDeliveries} from '@/api/taskDeliveryApi.js';

  import {
    _toPdfFile, _fileCheck
  } from '@/api/fileApi';

  import PdfView from '@/components/PdfView';

  import {downloadFile, urlRemoveSpecial} from '@/api/utils';

  async function listTaskDelivery() {
    const result = await _listTaskDeliveries({
      page: this.tableQuery.page,
      size: this.tableQuery.size,
      projectId: this.projectId,
      deliveryNames: this.currfilters.join(','),
      searchName: this.tableQuery.searchName,
    });

    if (result.code === 1200) {

      const dictData = this.$store.getters.getDictionaryByKey('AUDIT_STATUS');
      var dict = dictData.sysDictDataList;

      const re = result.data.taskDeliveryList;
      re.forEach(r => {
        const d = dict.filter(x => r.auditStatus !== null && x.code.toString() === r.auditStatus.toString())[0];
        this.$set(r, 'auditStatusName', d === undefined ? '' : d.name);
        this.$set(r, 'url', r.deliveryPath != null ? r.deliveryPath : '');
      })


      const ddelivery = []
      result.data.taskDeliveryListAll.forEach(tdla => {
        ddelivery.push(tdla.deliveryName)
      })

      this.filterDeliveryName = Array.from(new Set(ddelivery)).map(x => {
        return {
          text: x,
          value: x,
        }
      })


      console.log(re)
      this.tableData = re;
      this.tableQuery.total = result.data.totalAmount;
    }
  }

  function handlePageChange(val) {
    this.tableQuery.page = val;
    this.listTaskDelivery();
  }

  function handleFilterChange(filters) {


    this.currfilters = filters.deliveryName;

    this.tableQuery.page = 1;
    this.listTaskDelivery();
  }

  function search() {
    this.tableQuery.page = 1;
    this.tableQuery.size = 15;
    this.tableQuery.total = 0;
    this.listTaskDelivery();
  }

  async function viewFile(file) {
    this.pdf.file = file;
    let previewPath = "";
    const _suffix = file.deliveryName.substring(file.deliveryName.lastIndexOf('.') + 1);
    const suffix = _suffix.toLowerCase();
    if (this.checkSuffixIsView(file)) {
      previewPath = file.deliveryPath;
    } else {
      this.pdf.loading = true;
      const result = await _toPdfFile({sourceFile: file.deliveryPath});
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
    downloadFile(file.deliveryPath).then((response1) => {
    });
  }


  function checkSuffix(file) {
    const _suffix = file.deliveryName.substring(file.deliveryName.lastIndexOf('.') + 1);
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
    const _suffix = file.deliveryName.substring(file.deliveryName.lastIndexOf('.') + 1);
    const suffix = _suffix.toLowerCase();
    return suffix === 'pdf'
      || suffix === 'jpg'
      || suffix === 'png'
      || suffix === 'bmp'
  }


  export default {
    name: 'Deliverables',
    props: {
      projectId: String,
      activeName: String,
    },
    components: {PdfView},
    data() {
      return {
        tableData: [],
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
          searchName: "",
        },
        filterDeliveryName: [],
        currfilters: [],
        pdf: {
          file: '',
          src: "",
          loading: false,
          dialogVisible: false,
        }
      };
    },
    methods: {
      listTaskDelivery,
      handlePageChange,
      handleFilterChange,
      search,
      viewFile,
      checkSuffix,
      downFile,
      checkSuffixIsView
    },
    created() {
      this.currfilters = []
      if (this.activeName === 'delivery') {
        this.listTaskDelivery();
      }
    },
    watch: {
      projectId() {
        this.currfilters = []
        this.listTaskDelivery();
      },
      activeName(val) {
        if (val === 'delivery') {
          this.tableQuery = {
            page: 1,
            size: 15,
            total: 0,
            searchName: '',
          };
          this.listTaskDelivery();
        }

      },
    },
  };
</script>

<style>
  .el-table__column-filter-trigger {
    line-height: 23px;
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
