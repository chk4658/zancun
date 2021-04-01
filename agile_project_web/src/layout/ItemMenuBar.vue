<template>


  <div class="item_menu_bar" :style="{'width': isCollapse ?  '64px' : '283px' }">
    <div class="menu_list_header">
      <!--      <el-popover-->
      <!--        placement="bottom-start"-->
      <!--        width="150"-->
      <!--        :visible-arrow="false"-->
      <!--        v-model="visible"-->
      <!--      >-->
      <!--        <el-row>-->
      <!--          <el-col>-->
      <!--            <el-menu-->
      <!--              :default-active="active.code"-->
      <!--              class="el-menu-vertical-demo">-->
      <!--              <el-menu-item v-for="(item,index) in list" :index="item.code"-->
      <!--                            @click="goPath(item.code)" :key="index">-->
      <!--                <i :class="item.icon"></i>-->
      <!--                <span slot="title">{{item.name}}</span>-->
      <!--              </el-menu-item>-->
      <!--            </el-menu>-->
      <!--          </el-col>-->
      <!--        </el-row>-->
      <!--        <el-button type="text" class="fa fa-bars icon" slot="reference"></el-button>-->
      <!--      </el-popover>-->

      <div v-for="(item,index) in list" :key="index" :index="item.code">

        <div @click="goPath(item.code)"
             :style="{'background-color': active.code === item.code ?  '#292F4C' : '#FFFFFF',
              'color': active.code === item.code ?  '#FFFFFF' : '#292F4C'}"
             style="width: 95px;height: 36px;border-radius: 18px;background-color:#cccccc;
                    text-align: center;line-height: 36px;
                    cursor: pointer;-webkit-user-select: none;-moz-user-select: none;-ms-user-select: none;user-select: none;">
          <i :class="item.icon" style="margin-right: 3px;"></i>
          <span style="font-size: 15px;">{{ item.name }}</span>
        </div>

      </div>

    </div>
    <el-menu :default-active="$route.fullPath"
             :default-openeds="active.openeds"
             router
             active-text-color="#ffffff"
             :unique-opened="true"
             :collapse="isCollapse">
      <circle-parent-list ref="circle" v-show="active.code==='CIRCLE'"></circle-parent-list>
      <project-parent-list ref="project" v-show="active.code==='PROJECT'"></project-parent-list>
    </el-menu>

  </div>
</template>

<script>
import CircleParentList from '@/views/agile/circle/CircleParentList.vue';
import ProjectParentList from '@/views/agile/project/ProjectParentList.vue';
import {_treeGenerator} from '@/api/sysMenuApi';


async function goPath(code) {
  this.active.code = code;
  this.visible = false;
  if (code === 'CIRCLE') {
    await this.$refs.circle.getCircleList();
    this.$refs.circle.goCirclePush();
    this.active.openeds = ['/circle'];
  } else if (code === 'PROJECT') {
    await this.$refs.project.getList();
    this.$refs.project.goPush();
    this.active.openeds = ['/project1']
    console.log(this.active.openeds)
  }

  localStorage.setItem('active', JSON.stringify(this.active));
}


function listenHistory() {

  if (this.active.path.includes('circle') || this.active.path === '/') {
    this.active.code = 'CIRCLE';
    localStorage.setItem('active', JSON.stringify(this.active));
  } else if (this.active.path.includes('project')) {
    this.active.code = 'PROJECT';
    localStorage.setItem('active', JSON.stringify(this.active));
  }

}

export default {
  components: {
    CircleParentList,
    ProjectParentList,
  },
  props: {
    isCollapse: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      changeBarListShow: false,
      list: [
        {
          name: '项目',
          icon: 'fa fa-list-alt',
          code: 'PROJECT',
        },
        {
          name: '圈子',
          icon: 'fa fa-circle-o-notch',
          code: 'CIRCLE',
        },
      ],
      visible: false,

      active: {
        code: '',
        path: this.$route.path,
        id: this.$route.query.id,
        openeds: [],
      },
    };
  },
  methods: {
    goPath,
    listenHistory,
  },
  created() {
    const result = JSON.parse(localStorage.getItem('MENUS'));
    const projectMenus = result
      .filter(menu => menu.groups === 'PROJECT' && !menu.parentId)
      .map(x => _treeGenerator(x, result));
    console.log(projectMenus)
  },
  async mounted() {
    if (this.active.path.includes('circle')) {
      this.active.code = 'CIRCLE';
      if (this.active.path === '/circle') {
        this.active.openeds = ['/circle'];
      }
      localStorage.setItem('active', JSON.stringify(this.active));
    } else if (this.active.path.includes('project')) {
      this.active.code = 'PROJECT';
      if (this.active.path === '/project') {
        this.active.openeds = ['/project1']
      }

      localStorage.setItem('active', JSON.stringify(this.active));
    } else if (this.active.path === '/') {
      this.active.code = 'CIRCLE';
      await this.$refs.circle.getCircleList();
      this.$refs.circle.goCirclePush();
      this.active.openeds = ['/circle'];

      localStorage.setItem('active', JSON.stringify(this.active));
    } else {
      this.active = {
        code: JSON.parse(localStorage.getItem('active')).code,
        openeds: JSON.parse(localStorage.getItem('active')).openeds,
      };
    }


    if (window.history && window.history.pushState) {
      history.pushState(null, null, document.URL);
      window.addEventListener('popstate', this.listenHistory, false);
    }
  },
  watch: {
    async $route(to, from) {
      this.active.id = to.query.id;
      const type = to.query.type;
      if ((to.path.includes('circle') || to.path === '/')) {
        this.active.code === 'CIRCLE';
        if (to.path === '/circle') {
          // 判罚是否需要重新刷新列表
          if (type !== undefined && type == '1') {
            await this.$refs.circle.getCircleList();
          }
          this.$refs.circle.goCirclePush(this.active.id);
          this.active.openeds = ['/circle'];
        }
        localStorage.setItem('active', JSON.stringify(this.active));
      } else if (to.path.includes('project')) {
        this.active.code = 'PROJECT';

        if (to.path === '/project') {
          if (type !== undefined && type == '1') {
            await this.$refs.project.getList();
          }
          this.$refs.project.goPush(this.active.id);
          this.active.openeds = ['/project1']
        }

        localStorage.setItem('active', JSON.stringify(this.active));
      }
    }
  },
};
</script>
<style lang="scss" scoped>
.item_menu_bar {
  height: 100%;
  background-color: #ffffff;
  position: fixed;
  left: 76px;
  top: 0;
  border-top-left-radius: 20px;
  z-index: 0;
  border-right: 1px #e1e1e1 solid;
  transition: width 0.3s, transform 2s;
}

.menu_list_header {
  height: 73px;
  display: flex;
  padding-left: 25px;
  align-items: center;
  border-top-left-radius: 20px;
  border-bottom: 1px #e1e1e1 solid;
  overflow-x: hidden;
}

.icon {
  font-size: 22px;
  margin-left: 20px;
}

.el-menu-vertical-demo {

  li {
    height: 35px;
    line-height: 35px;
    border-radius: 4px;
  }

  .el-popper[x-placement^=bottom] {
    margin-top: 0;
  }
}
</style>
