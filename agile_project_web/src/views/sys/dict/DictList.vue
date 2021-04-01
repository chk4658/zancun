<template>
  <div style="width: 100%;
              height: 100%;
              display: flex;overflow-x: hidden">
    <div class="information">
      <main-top-part>
        <template v-slot:title>
          <div style="display: flex">
            <i :class="userIcon" class="menu-icon" style="color: #01408B;font-size: 32px;">
            </i>枚举类
          </div>
        </template>
      </main-top-part>

      <el-row style="margin: 30px 20px 80px;overflow-y: auto;">
        <el-col :span="24">
          <el-table
            :data="tableData"
            style="width: 100%;"
            row-key="id"
            default-expand-all>
            <el-table-column
              prop="name"
              label="枚举类名称">
            </el-table-column>
            <el-table-column
              prop="code"
              label="编码">
            </el-table-column>

            <el-table-column
              label="状态值">
              <template slot-scope="scope">
                <div slot="reference" class="name-wrapper"
                     v-for="item in scope.row.sysDictDataList"
                     :key="item.id">
                  <el-tag size="min"
                          style="height: 23px;line-height: 23px;float: left;
                                   margin-right: 5px">
                    {{ item.name }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>

            <el-table-column
              prop="createAt"
              label="创建时间">
            </el-table-column>
            <el-table-column
              align="right">
              <template slot="header">
                <i class="el-icon-circle-plus"
                   style="font-size: 20px;position: relative;top: 2px;cursor: pointer;"
                   v-has="'DICT_ADD'"
                   @click="addEnumeration"></i>
              </template>
              <template slot-scope="scope">
                <i class="el-icon-edit" @click="handleEdit(scope.row.id)"
                   v-has="'DICT_EDIT'"
                   style="font-size: 20px; margin-right: 20px; cursor: pointer;"></i>
                <i class="el-icon-delete" @click="handleDelete(scope.$index, scope.row)"
                   v-has="'DICT_DELETE'"
                   style="font-size: 20px; cursor: pointer"></i>
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
            layout="total, sizes, prev, pager, next, jumper"
            :total="this.tableQuery.total">
          </el-pagination>
        </el-col>
      </el-row>
    </div>


    <el-dialog
      @close="closeForm"
      :visible.sync="form.visible"
      :modal-append-to-body=false
      width="900px"
      :title="form.title">
      <dict-edit
        v-if="form.visible"
        :enumeration-id="form.data.id"
        :on-cancel-handler="closeForm"
        :on-submit-handler="closeForm">
      </dict-edit>
    </el-dialog>
  </div>
</template>
<script>
  import MainTopPart from '@/components/MainTopPart.vue';
  import {_listEnumeration, _deleteDict} from '@/api/sysDictApi';
  import DictEdit from './DictEdit.vue';


  async function addEnumeration() {
    this.form.data.id = null;
    this.form.visible = true;
    this.form.title = '创建枚举类';
  }

  async function handleEdit(a) {
    this.form.data.id = a;
    this.form.visible = true;
    this.form.title = '枚举类变更';
  }

  async function handleDelete(index, row) {
    this.$confirm('是否删除?', '枚举类删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        const result = await _deleteDict(row.id);
        if (result.code === 1200) {
          this.tableData.splice(index, 1);
          this.tableQuery.total = this.tableQuery.total - 1;
          this.$message({
            message: '删除成功',
            type: 'success',
            center: true,
          });
        }
      });
  }

  function closeForm() {
    this.form.data.id = null;
    this.form.visible = false;
    this.list();
  }

  async function list() {
    const result = await _listEnumeration({
      page: this.tableQuery.page,
      size: this.tableQuery.size,
    });
    this.tableData = result.data.SysDictList;
    this.tableQuery.total = result.data.totalAmount;
  }

  export default {
    components: {
      MainTopPart,
      DictEdit,
    },
    data() {
      return {
        tableData: [],
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
        },
        form: {
          data: {
            id: null,
          },
          visible: false,
          title: '',
        },
        userIcon: '',
      };
    },
    methods: {
      handlePageChange(val) {
        this.tableQuery.page = val;
        this.list();
      },
      handleSizeChange(val) {
        this.tableQuery.size = val;
        this.list();
      },
      list,
      addEnumeration,
      handleDelete,
      handleEdit,
      closeForm,
    },
    created() {
      this.list();
      const userMenus = JSON.parse(localStorage.getItem('MENUS'))
        .filter(menu => menu.code === 'ENUMERATION');
      this.userIcon = userMenus.length === 0 ? 'el-icon-s-tools' : userMenus[0].icon;
    },
  };
</script>


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
