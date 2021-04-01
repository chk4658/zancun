import moment from "moment";
import {
  _listInRedis,
  _putProjectInformation,
  _saveOrUpdateProjectMilestone,
  _getProjectInformation
} from "../api/projectApi";
import de from "element-ui/src/locale/lang/de";
import {_listTemporaryTask} from "../api/taskApi";


/**
 * 向redis取数据,做处理,put到indexDB里
 * types为任务类型的数组，包含参数code,name
 * @param {*} projectId
 */
export async function _getToPut(projectId, searchName, that, types, person) {

  const result = await _listInRedis({
    projectId,
    searchName: '',
  });
  // childTaskList    parentTaskList    projectAttrs   projectMilestoneList


  const cols = [];
  const projectAttrs = result.data.projectAttrs;
  if (projectAttrs !== null && projectAttrs.length > 0) {
    projectAttrs.forEach(item => {
      cols.push({
        projectAttrId: item.id,
        label: item.name,
        type: item.type,
        isEditCols: false,
      });
    });
  }
  var afterFilterPersonP;
  var afterFilterPersonC;


  if (person !== undefined && person.replace(/(^\s*)|(\s*$)/g, '') !== '') {

    afterFilterPersonC = result.data.childTaskList.filter(fct =>
      (fct.reviewUser !== null && fct.reviewUser.realName.toLowerCase().indexOf(person.toLowerCase()) !== -1) ||
      (fct.confirmUser !== null && fct.confirmUser.realName.toLowerCase().indexOf(person.toLowerCase()) !== -1));


    afterFilterPersonP = result.data.parentTaskList.filter(fpt =>
      (fpt.reviewUser !== null && fpt.reviewUser.realName.toLowerCase().indexOf(person.toLowerCase()) !== -1) ||
      (fpt.confirmUser !== null && fpt.confirmUser.realName.toLowerCase().indexOf(person.toLowerCase()) !== -1));

  } else {
    afterFilterPersonP = result.data.parentTaskList;
    afterFilterPersonC = result.data.childTaskList;
  }


  if (searchName.replace(/(^\s*)|(\s*$)/g, '') !== '') {
    const childPids = [];
    var filterChildTaskList = afterFilterPersonC.filter(fct => fct.name.toLowerCase().indexOf(searchName.toLowerCase()) !== -1);

    filterChildTaskList.forEach(fct => childPids.push(fct.parentId));


    var filterParentTaskList = afterFilterPersonP.filter(fpt =>
      fpt.name.toLowerCase().indexOf(searchName.toLowerCase()) !== -1 || childPids.indexOf(fpt.id) !== -1);


    const findMile = filterParentTaskList.concat(filterChildTaskList);
    const milIds = [];
    findMile.forEach(fm => milIds.push(fm.milestoneId));

    var filterProjectMilestones = result.data.projectMilestoneList.filter(fpm =>
      fpm.name.toLowerCase().indexOf(searchName.toLowerCase()) !== -1 || milIds.indexOf(fpm.id) !== -1);
  } else {
    var filterParentTaskList = afterFilterPersonP;
    var filterChildTaskList = afterFilterPersonC;
    var filterProjectMilestones = result.data.projectMilestoneList;
  }


  const projectMilestones = filterProjectMilestones;
  const parentTaskList = filterParentTaskList;
  const childTaskList = filterChildTaskList;

  let isOpen = false;
  // let isDelivery = false;
  parentTaskList.forEach(task => {
    task.isEdit = {
      name: false,
      openConditions: false,
      openDescription: false,
      type: false,
    };
    task.checked = false;

    task.confirmRoleName = task.confirmProjectRole === null ? '' : task.confirmProjectRole.roleName;
    task.reviewRoleName = task.reviewProjectRole === null ? '' : task.reviewProjectRole.roleName;

    // 若有交付要求或无交付要求但有交付物  就显示 交付物这一列
    // if (task.isRequirement === 1 || (task.isRequirement === 0 && task.taskDeliveryList.length !== 0)) {
    //   isDelivery = true
    // }


    task.type = task.type === null ? [] : task.type.split(',');

    if (task.type.length > 0) {

      if (task.type.indexOf('3') !== -1) {
        isOpen = true
      }
      const arrName = [];
      task.type.forEach(i => {
        arrName.push(types.filter(type => type.code === i)[0].name);
      })

      that.$set(task, 'typeName', arrName.join('-'));
    } else {
      that.$set(task, 'typeName', '-');
    }

    if (projectAttrs !== null && projectAttrs.length > 0) {
      projectAttrs.forEach(attr => {
        if (task.taskDataList.filter(taskData => taskData.projectAttrId === attr.id).length === 0) {
          const taskData = {
            attrType: attr.type,
            projectAttrId: attr.id,
            taskId: task.id,
            value: '',
          };
          task.taskDataList.push(taskData);
        }
        Array.from(task.taskDataList, x => x.isEditing = false);
      });
    }


    const flag = childTaskList.some(child => child.parentId === task.id);
    that.$set(task, 'hasChildren', flag);
  });
  childTaskList.forEach(childTask => {
    childTask.isEdit = {
      name: false,
      openConditions: false,
      openDescription: false,
      type: false,
    };
    childTask.checked = false;

    childTask.confirmRoleName = childTask.confirmProjectRole === null ? '' : childTask.confirmProjectRole.roleName;
    childTask.reviewRoleName = childTask.reviewProjectRole === null ? '' : childTask.reviewProjectRole.roleName;

    // if (childTask.isRequirement === 1 || (childTask.isRequirement === 0 && childTask.taskDeliveryList.length !== 0)) {
    //   isDelivery = true
    // }
    childTask.type = childTask.type === null ? [] : childTask.type.split(',');

    if (childTask.type.length > 0) {

      if (childTask.type.indexOf('3') !== -1) {
        isOpen = true
      }
      const arrName = [];
      childTask.type.forEach(i => {
        arrName.push(types.filter(type => type.code === i)[0].name);
      })

      that.$set(childTask, 'typeName', arrName.join(','));
    } else {
      that.$set(childTask, 'typeName', '-');
    }

    if (projectAttrs !== null && projectAttrs.length > 0) {
      projectAttrs.forEach(attr => {
        if (childTask.taskDataList.filter(taskData => taskData.projectAttrId === attr.id).length === 0) {
          const taskData = {
            attrType: attr.type,
            projectAttrId: attr.id,
            taskId: childTask.id,
            value: '',
          };
          childTask.taskDataList.push(taskData);
        }
        Array.from(childTask.taskDataList, x => x.isEditing = false);
      });
    }

  });

  //
  // const aaa = await _getProjectInformation(projectId, that);
  // console.log(aaa.projectInformation)

  // 原地更新保留，路由变化等直接置成false
  const isNewRouter = localStorage.getItem('projectCollapsed')
  // 将展开的情况放在缓存里

  const milestoneArray = [];
  projectMilestones.forEach((i) => {
    i.isEdit = {
      Add: false,
      name: false,
      endTime: false,
    };

    i.confirmRoleName = i.confirmProjectRole === null ? '' : i.confirmProjectRole.roleName;
    i.reviewRoleName = i.reviewProjectRole === null ? '' : i.reviewProjectRole.roleName;

    const afterSort = parentTaskList.filter(task => task.milestoneId != null && i.id === task.milestoneId).sort((a, b) => {
      return a.sort < b.sort ? -1 : 1;
    });


    const pIds = []
    afterSort.forEach((item, index) => {
      that.$set(item, "serialNumber", index + 1);
      pIds.push(item.id)

    })
    if (afterSort.length > 0) {
      that.$set(afterSort[0], 'first', true);
      that.$set(afterSort[afterSort.length - 1], 'end', true);
    }
    const currentMilestone = (childTaskList.filter(childTask => pIds.indexOf(childTask.parentId) !== -1)).concat(afterSort);
    let NullValuePrompt = false
    currentMilestone.forEach(cm => {
      // 有责任角色、审核角色、截止时间就能上线；缺少项提示，*当前里程碑下存在数据缺失！  红框框出
      if (cm.reviewProjectRole === null || cm.confirmProjectRole === null || cm.estEndTime === null) {
        NullValuePrompt = true
      }
    })

    // collapsed: i.collapsed === null || i.collapsed === 1,

    // isNewRouter === null ? true : JSON.parse(isNewRouter).filter(r => r.id === i.id)[0].collapsed
    const milestone = {
      projectMilestone: i,
      taskList: afterSort,
      allSelect: false,
      collapsed: isNewRouter === null ? true : JSON.parse(isNewRouter).filter(r => r.mileId === i.id)[0].collapsed,
      isOpen: isOpen,
      NullValuePrompt: NullValuePrompt,
      // isDelivery: isDelivery,
    };
    milestoneArray.push(milestone);

  });

  milestoneArray.sort((a, b) => {
    return a.projectMilestone.sort < b.projectMilestone.sort ? -1 : 1;
  })

  await _putProjectInformation(that, projectId, childTaskList, milestoneArray, cols);


  return {milestoneArray, childTaskList, cols}


}

export async function _message(projectId, that, types) {

  const result = await _listInRedis({
    projectId,
    searchName: '',
  });
  // childTaskList    parentTaskList    projectAttrs   projectMilestoneList


  const cols = [];
  const projectAttrs = result.data.projectAttrs;
  if (projectAttrs !== null && projectAttrs.length > 0) {
    projectAttrs.forEach(item => {
      cols.push({
        projectAttrId: item.id,
        label: item.name,
        type: item.type,
        isEditCols: false,
      });
    });
  }

  const allTaskList = result.data.parentTaskList.concat(result.data.childTaskList);

  allTaskList.forEach(task => {
    task.isEdit = {
      name: false,
      openConditions: false,
      openDescription: false,
      type: false,
    };
    task.checked = false;

    task.confirmRoleName = task.confirmProjectRole === null ? '' : task.confirmProjectRole.roleName;
    task.reviewRoleName = task.reviewProjectRole === null ? '' : task.reviewProjectRole.roleName;

    task.type = task.type === null ? [] : task.type.split(',');

    if (task.type.length > 0) {
      const arrName = [];
      task.type.forEach(i => {
        arrName.push(types.filter(type => type.code === i)[0].name);
      })

      that.$set(task, 'typeName', arrName.join('-'));
    } else {
      that.$set(task, 'typeName', '-');
    }

    if (projectAttrs !== null && projectAttrs.length > 0) {
      projectAttrs.forEach(attr => {
        if (task.taskDataList.filter(taskData => taskData.projectAttrId === attr.id).length === 0) {
          const taskData = {
            attrType: attr.type,
            projectAttrId: attr.id,
            taskId: task.id,
            value: '',
          };
          task.taskDataList.push(taskData);
        }
        Array.from(task.taskDataList, x => x.isEditing = false);
      });
    }
  });

  return {allTaskList, cols}


}


/**
 * 处理临时任务的工具
 * @param {*}
 */
export async function _dealTemporaryTask(searchName, that, types, person) {

  const result = await _listTemporaryTask();

  // taskUserList

  let afterFilterPersonTaskUserList;


  let allIds = [];
  result.data.taskUserList.forEach(tul => {
    allIds.push(tul.id);
  })


  // 经过人员的筛选
  if (person !== undefined && person.replace(/(^\s*)|(\s*$)/g, '') !== '') {

    const churf = result.data.taskUserList.filter(fpt =>
      (fpt.reviewUser !== null && fpt.reviewUser.realName.toLowerCase().indexOf(person.toLowerCase()) !== -1) ||
      (fpt.confirmUser !== null && fpt.confirmUser.realName.toLowerCase().indexOf(person.toLowerCase()) !== -1));

    const childp = [];
    churf.forEach(ch => {
      if (ch.parentId !== null && ch.parentId !== '' && allIds.indexOf(ch.parentId) !== -1) {
        childp.push(ch.parentId)
      }
    })
    const other = result.data.taskUserList.filter(fpt => childp.indexOf(fpt.id) !== -1)

    afterFilterPersonTaskUserList = churf.concat(other);

  } else {
    afterFilterPersonTaskUserList = result.data.taskUserList;
  }


  // 搜索框再筛选一次
  let afterFilterSearchTaskUserList;
  if (searchName.replace(/(^\s*)|(\s*$)/g, '') !== '') {

    const shef = afterFilterPersonTaskUserList.filter(fpt => fpt.name.toLowerCase().indexOf(searchName.toLowerCase()) !== -1);


    const childs = [];
    shef.forEach(ch => {
      if (ch.parentId !== null && ch.parentId !== '' && allIds.indexOf(ch.parentId) !== -1) {
        childs.push(ch.parentId)
      }
    })
    const other = result.data.taskUserList.filter(fpt => childs.indexOf(fpt.id) !== -1)

    afterFilterSearchTaskUserList = shef.concat(other);


  } else {
    afterFilterSearchTaskUserList = afterFilterPersonTaskUserList;
  }

  /**
   * 只有子任务筛到时会考虑将父任务带上，如果父任务不存在或对当前用户不可见，子任务上升父任务位置，但保留子任务权限
   */
  const parentTaskList = afterFilterSearchTaskUserList.filter(afstul => afstul.parentId === null || !(afstul.parentId !== null && allIds.indexOf(afstul.parentId) !== -1));
  const childTaskList = afterFilterSearchTaskUserList.filter(afstul => afstul.parentId !== null && allIds.indexOf(afstul.parentId) !== -1);

  let isOpen = false;
  // let isDelivery = false;
  parentTaskList.forEach(task => {
    task.isEdit = {
      name: false,
      openConditions: false,
      openDescription: false,
      type: false,
    };
    task.checked = false;

    task.confirmRoleName = task.confirmProjectRole === null ? '' : task.confirmProjectRole.roleName;
    task.reviewRoleName = task.reviewProjectRole === null ? '' : task.reviewProjectRole.roleName;

    // if (task.isRequirement === 1 || (task.isRequirement === 0 && task.taskDeliveryList.length !== 0)) {
    //   isDelivery = true
    // }

    task.type = task.type === null ? [] : task.type.split(',');

    if (task.type.length > 0) {

      if (task.type.indexOf('3') !== -1) {
        isOpen = true
      }
      const arrName = [];
      task.type.forEach(i => {
        arrName.push(types.filter(type => type.code === i)[0].name);
      })

      that.$set(task, 'typeName', arrName.join('-'));
    } else {
      that.$set(task, 'typeName', '-');
    }


    const flag = childTaskList.some(child => child.parentId === task.id);
    that.$set(task, 'hasChildren', flag);
  });
  childTaskList.forEach(childTask => {
    childTask.isEdit = {
      name: false,
      openConditions: false,
      openDescription: false,
      type: false,
    };
    childTask.checked = false;

    childTask.confirmRoleName = childTask.confirmProjectRole === null ? '' : childTask.confirmProjectRole.roleName;
    childTask.reviewRoleName = childTask.reviewProjectRole === null ? '' : childTask.reviewProjectRole.roleName;

    // if (childTask.isRequirement === 1 || (childTask.isRequirement === 0 && childTask.taskDeliveryList.length !== 0)) {
    //   isDelivery = true
    // }

    childTask.type = childTask.type === null ? [] : childTask.type.split(',');

    if (childTask.type.length > 0) {

      if (childTask.type.indexOf('3') !== -1) {
        isOpen = true
      }
      const arrName = [];
      childTask.type.forEach(i => {
        arrName.push(types.filter(type => type.code === i)[0].name);
      })

      that.$set(childTask, 'typeName', arrName.join(','));
    } else {
      that.$set(childTask, 'typeName', '-');
    }

  });

  const afterSort = parentTaskList.sort((a, b) => {
    return a.sort < b.sort ? -1 : 1;
  });

  afterSort.forEach((item, index) => that.$set(item, "serialNumber", index + 1))

  const virtualMilestone = {
    taskList: afterSort,
    isEdit: {
      Add: false,
    },
    isOpen: isOpen,
    // isDelivery: isDelivery,
  };


  return {virtualMilestone, childTaskList}


}


/**
 * task对象
 * gapR,gapP用来屏蔽保存带条件理由，拒绝理由
 * @param {*} task
 */
export function _taskObj(task, gapR, gapP) {

  const obj = {
    id: task.id,
    name: task.name,
    milestoneId: task.milestoneId,
    projectId: task.projectId,
    parentId: task.parentId,
    status: task.status,
    priority: task.priority,
    circleId: task.circleId,
    isRequirement: task.isRequirement,
    type: task.type.join(','),
    reviewRoleName:
      task.reviewRoleName === null || task.reviewRoleName === '' ? '' : task.reviewRoleName,
    confirmRoleName:
      task.confirmRoleName === null || task.confirmRoleName === '' ? '' : task.confirmRoleName,
    estEndTime: task.estEndTime === '' || task.estEndTime === undefined || task.estEndTime === null
      ? task.estEndTime : moment(task.estEndTime)
        .format('YYYY-MM-DD HH:mm:ss'),
    openStatus: task.openStatus,
    openConditions: task.openConditions,
    openDescription: task.openDescription,
    refuseReason: gapR ? task.refuseReason : undefined,
    passReason: gapP ? task.passReason : undefined,
    reuploadListIds: task.reuploadListIds === undefined ? "" : task.reuploadListIds,
    restartTaskFlag: task.restartTaskFlag !== undefined && task.restartTaskFlag
  }

  return obj
}


/**
 * 临时任务对象
 * gapR,gapP用来屏蔽保存带条件理由，拒绝理由
 * @param {*} task
 */
export function _taskTemporaryObj(task, gapR, gapP) {

  const obj = {
    id: task.id,
    name: task.name,
    parentId: task.parentId,

    milestoneId: null,
    projectId: null,
    circleId: null,

    status: task.status,
    priority: task.priority,
    isRequirement: task.isRequirement,
    reviewRoleName: '',
    confirmRoleName: '',
    type: task.type.join(','),
    estEndTime: task.estEndTime === '' || task.estEndTime === undefined || task.estEndTime === null
      ? task.estEndTime : moment(task.estEndTime)
        .format('YYYY-MM-DD HH:mm:ss'),
    openStatus: task.openStatus,
    openConditions: task.openConditions,
    openDescription: task.openDescription,
    refuseReason: gapR ? task.refuseReason : undefined,
    passReason: gapP ? task.passReason : undefined,
    createUserId: task.createUserId === null || task.createUserId === undefined ? localStorage.getItem('id') : task.createUserId,
    isTemporary: 1,
    restartTaskFlag: task.restartTaskFlag !== undefined && task.restartTaskFlag,
  }

  return obj
}


/**
 * 新增或保存项目里程碑
 * @param {*} milestone
 */
export async function _projectMilestones(milestone) {
  const obj = {
    id: milestone.id,
    name: milestone.name,
    flag: milestone.flag,
    circleId: milestone.circleId,
    projectId: milestone.projectId,
    estEndTime:
      milestone.estEndTime === '' || milestone.estEndTime === undefined || milestone.estEndTime === null
        ? milestone.estEndTime : moment(milestone.estEndTime)
          .format('YYYY-MM-DD HH:mm:ss'),
    reviewRoleName:
      milestone.reviewRoleName === null || milestone.reviewRoleName === '' ? '' : milestone.reviewRoleName,
    confirmRoleName:
      milestone.confirmRoleName === null || milestone.confirmRoleName === '' ? '' : milestone.confirmRoleName,

    batchRorC: milestone.rOrC === 'review',
  };

  milestone.isEdit.name = false;
  const result = await _saveOrUpdateProjectMilestone(obj);

  return result.code

}


export default {};
