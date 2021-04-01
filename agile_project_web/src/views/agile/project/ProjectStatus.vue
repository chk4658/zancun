<template>
  <div class="project_status">
    <div style="margin:0 0 10px 0;">
      <el-button type="text" icon="el-icon-check" 
      @click="clickStatus(
        status === 'NOT_ENABLE' ? 1 : 0,status
      )"
      >
        {{status === 'NOT_ENABLE' ? '启用' : '解禁'}}
      </el-button>
      <el-button type="text" 
      icon="el-icon-close" 
      style="margin-left: 20px;"
      v-show="status === 'NOT_ENABLE'"
      @click="clickStatus(1,'FORBIDDEN')">禁用</el-button>
    </div>
    <div>
      <el-table
        class="maxh-table"
        :data="tableData"
        row-key="id"
        border
        default-expand-all
        :height="height + 'px'"
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      >
      <el-table-column label="" width="40"></el-table-column>
      <el-table-column label="" width="40">
          <template slot-scope="scope">
            <el-checkbox
              label=""
              v-model="scope.row.checked"
              @change="checkChange(scope)"
              :disabled='
                status === "NOT_ENABLE"
                && scope.row.forbidden !== null 
                && scope.row.forbidden === 1
              '
            ></el-checkbox>
          </template>
        </el-table-column>
        <el-table-column label="名称">
          <template slot-scope="scope">
            <span style="margin-left: 10px;">{{scope.row.name}}</span>
          </template>
        </el-table-column>
        <el-table-column label="禁用状态"  width="200">
          <template slot-scope="scope">
            {{scope.row.forbidden === null || scope.row.forbidden === 0 ? '未禁用' : '禁用'}}
          </template>
        </el-table-column>
      </el-table>
    </div>
    
  </div>
</template>
<script>

import {
   _enableOrForbiddenProjectMilestone,
   _updateForbiddenProjectMilestone,
   _updateEnabledProjectMilestone
 } from '@/api/projectApi';


function scan(obj, row) {
  let flag = true;
  for (let i = 0; i < obj.length; i++) {
    if(row.id === obj[i].id && !row.checked) {
        return false;
    }
    if (!obj[i].checked) {
      if (obj[i].children.length > 0 && this.scan(obj[i].children, row)) {
        obj[i].checked = true;
      } else {
        flag = false;
      }
    } else {
      if (obj[i].children.length > 0 && !this.scan(obj[i].children, row)) {
        flag = false;
        obj[i].checked = false;
        return flag;
      }
    }
  }
  return flag;
}

function checkChange(scope) {
  if (scope.row.children.length > 0) {
    this.handleCheckAll(scope.row, scope.row.checked);
  }
  this.scan(this.tableData,scope.row);
}


function handleCheckAll(row, checked) {
  row.checked = checked;
  if (row.children.length > 0) {
    let that = this;
    row.children.forEach((element, i) => {
      that.handleCheckAll(row.children[i], checked);
    });
  }
}


async function getProjectMilestone() {
  const result = await _enableOrForbiddenProjectMilestone({
    projectId: this.projectId,
    statusName: this.status
  });
  if (result.code === 1200) {
    const milestone = result.data.projectMilestones;
    this.tableData = [];
    milestone.forEach(m => {
      const tableData = {
        id: m.id,
        name: m.name,
        type: 'PROJECT_MILESTONE',
        forbidden: m.forbidden,
        children: [],
        checked: false,
      };
      // 组装里程碑
      this.tableData.push(tableData);
      if (m.taskList !== null) {
        // 任务里程碑
        m.taskList.forEach(t => {
          const _tableData = {
            id: t.id,
            name: t.name,
            type: 'PROJECT_TASK',
            forbidden: t.forbidden,
            children: [],
            checked: false,
          };
          tableData.children.push(_tableData);
            // 子任务
          if (t.children !== null) {
            t.children.forEach(tt => {
              _tableData.children.push({
                id: tt.id,
                name: tt.name,
                type: 'PROJECT_TASK',
                forbidden: tt.forbidden,
                children: [],
                checked: false,
              })
            });
          }
        })
      }
    });
  }
}

async function clickStatus(val,type) {

  // 组装数据
  const map = new Map();
  this.tableData.forEach(t => {
    if (t.checked) {
      map.set(t.id,t.type);
    }
    t.children.forEach(_t => {
      if (_t.checked) {
        map.set(t.id,t.type);
        map.set(_t.id,_t.type);
      }
      _t.children.forEach(__t => {
        if (__t.checked) {
          map.set(t.id,t.type);
          map.set(_t.id,_t.type);
          map.set(__t.id,__t.type);
        }
      })
    })
  });

  const enableOrForbiddens = Array.from(map).map(item => {
                                    return {
                                        id: item[0],
                                        type: item[1],
                                      }
                                    });
  if (type === 'FORBIDDEN') {
    const data = {
      enableOrForbiddens: enableOrForbiddens,
      status: val,
    }
    const result = await _updateForbiddenProjectMilestone(data);
  } else if (type === 'NOT_ENABLE') {
      const data = {
        enableOrForbiddens: enableOrForbiddens,
        status: val,
      }
      const result = await _updateEnabledProjectMilestone(data);
  }
  this.$emit('save');
  this.getProjectMilestone();
}



export default {
  name: "ProjectStatus",
  props: {
    projectId: String,
    status: String,
  },
  data() {
    return {
      tableData: [],
      height: 0,
    };
  },
  created: function () {
    this.getProjectMilestone();
  },
  mounted: function () {
    this.$nextTick(v =>{
      this.height = window.innerHeight * 0.75
    })
  },
  methods: {
    scan,
    checkChange,
    handleCheckAll,
    getProjectMilestone,
    clickStatus
  },
  watch: {
    projectId(val) {
      this.getProjectMilestone();
    },
    status(val) {
      this.getProjectMilestone();
    }
  },
};
</script>