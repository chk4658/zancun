import Vue from 'vue';
import Router from 'vue-router';


Vue.use(Router);


const originalPush = Router.prototype.push;
Router.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err);
};

const indexRouters = [
  {
    path: 'index',
    name: 'Index',
    meta: {requiresAuth: true, title: '主页'},
    component: () => import('@/views/index/Index.vue'),
  },
];


const mainRouters = [
  {
    path: '',
    name: 'circle',
    meta: {
      requiresAuth: true,
      title: '圈子首页',
    },
    component: () => import('@/views/agile/circle/CircleList.vue'),
  },
  {
    path: '/calender-task',
    name: 'CalenderTask',
    meta: {
      requiresAuth: true,
      title: '日历',
    },
    component: () => import('@/views/sys/calendar/CalenderTask.vue'),
  },
];


const circleRouters = [
  {
    path: '/circle',
    name: 'circle',
    meta: {
      requiresAuth: true,
      title: '圈子',
    },
    component: () => import('@/views/agile/circle/CircleList.vue'),
  },
  {
    path: '/circle-dashboard',
    name: 'circleDashboard',
    meta: {
      requiresAuth: true,
      title: '圈子报告',
    },
    component: () => import('@/views/agile/circle/report/CircleDashboard.vue'),
  }
];

const projectRouters = [
  {
    path: '/project',
    name: 'project',
    meta: {
      requiresAuth: true,
      title: '项目',
    },
    component: () => import('@/views/agile/project/ProjectList.vue'),
  },
  {
    path: '/temporaryTask',
    name: 'temporaryTask',
    meta: {
      requiresAuth: true,
      title: '临时任务',
    },
    component: () => import('@/views/agile/project/temporaryTask/TemporaryTaskHeader.vue'),
  },
  {
    path: '/project-dashboard',
    name: 'projectDashboard',
    meta: {
      requiresAuth: true,
      title: '项目报告',
    },
    component: () => import('@/views/agile/project/report/ProjectDashboard.vue'),
  },
  {
    path: '/project-issue',
    name: 'projectIssueList',
    meta: {
      requiresAuth: true,
      title: '项目问题清单',
    },
    component: () => import('@/views/agile/project/projectIssue/ProjectIssueList.vue'),
  }, {
    path: '/my-task',
    name: 'myTask',
    meta: {
      requiresAuth: true,
      title: '我的任务',
    },
    component: () => import('@/views/agile/project/task/MyTask.vue'),
  },
];


const ProjectTemplateMilestoneRouters = [
  {
    path: '/projectTemplateMilestone',
    name: 'projectTemplateMilestone',
    meta: {
      requiresAuth: true,
      title: '项目模板',
    },
    component: () => import('@/views/agile/projectTemplate/ProjectTemplateMilestone.vue'),
  },
];


const projectOnline = [
  {
    path: '/project-online',
    name: 'projectOnline',
    meta: {
      requiresAuth: true,
      title: '项目上线',
    },
    component: () => import('@/views/agile/projectOnline/ProjectOnline.vue'),
  },
]

const projectStMark = [
  {
    path: '/project-st-mark',
    name: 'projectStMark',
    meta: {
      requiresAuth: true,
      title: '项目上线',
    },
    component: () => import('@/views/agile/project/ProjectStMark.vue'),
  },
]


const equivalentList = [
  {
    path: '/equivalent',
    name: 'equivalent',
    meta: {
      requiresAuth: true,
      title: '当量管理',
    },
    component: () => import('@/views/agile/equivalent/EquivalentList.vue'),
  },
];


const projectTempleTypeRouters = [
  {
    path: '/projectTempleType',
    name: 'projectTempleType',
    meta: {
      requiresAuth: true,
      title: '项目模板类型',
    },
    component: () => import('@/views/agile/projectTemplate/ProjectTemplateTypeBar.vue'),
    children: [
      {
        path: '/project-template',
        name: 'projectTemple',
        meta: {
          requiresAuth: true,
          title: '项目模板类型',
        },
        component: () => import('@/views/agile/projectTemplate/ProjectTemplateList.vue'),
      }
    ]
  },
];

const sysRouters = [
  {
    path: '/user',
    name: 'UserList',
    meta: {
      requiresAuth: true,
      title: '人员管理',
    },
    component: () => import('@/views/sys/user/UserList.vue'),
  },
  {
    path: '/roleTemplate',
    name: 'RoleTemplateList',
    meta: {
      requiresAuth: true,
      title: '角色管理',
    },
    component: () => import('@/views/sys/roleTemplate/RoleTemplateList.vue'),
  },
  {
    path: '/menu',
    name: 'MenuList',
    meta: {
      requiresAuth: true,
      title: '菜单管理',
    },
    component: () => import('@/views/sys/menu/MenuList.vue'),
  },
  {
    path: '/role',
    name: 'RoleList',
    meta: {
      requiresAuth: true,
      title: '角色管理',
    },
    component: () => import('@/views/sys/role/RoleList.vue'),
  },
  {
    path: '/dict',
    name: 'DictList',
    meta: {
      requiresAuth: true,
      title: '枚举类',
    },
    component: () => import('@/views/sys/dict/DictList.vue'),
  },
  {
    path: '/question-type',
    name: 'QuestionType',
    meta: {
      requiresAuth: true,
      title: '问题类型管理',
    },
    component: () => import('@/views/sys/questionType/QuestionTypeList.vue'),
  },
  {
    path: '/constructing-page',
    name: 'ConstructingPage',
    meta: {requiresAuth: true, title: '页面建设中'},
    component: () => import('@/layout/ConstructingPage.vue'),
  },
];


const Test = [
  {
    path: '/test',
    name: 'test',
    meta: {
      requiresAuth: true,
      title: '测试',
    },
    component: () => import('@/views/agile/test.vue'),
  },
];

export default new Router({
  routes: [
    {
      path: '',
      meta: {
        requiresAuth: true,
        title: '',
      },
      component: () => import('@/layout/MainFramework.vue'),
      children: [
        ...mainRouters,
        ...sysRouters,
        ...projectRouters,
        ...circleRouters,
        ...projectTempleTypeRouters,
        ...ProjectTemplateMilestoneRouters,
        ...projectOnline,
        ...equivalentList,
        ...Test,
        ...indexRouters,
        ...projectStMark
      ],
    },
    {
      path: '/login',
      name: 'login',
      meta: {
        requiresAuth: false,
        title: '',
      },
      component: () => import('@/views/login/Login.vue'),
    },
    {
      path: '*',
      redirect: {
        name: '404',
      },
    },
  ],
});
