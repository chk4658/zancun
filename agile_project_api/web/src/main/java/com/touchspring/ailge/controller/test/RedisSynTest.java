package com.touchspring.ailge.controller.test;

import com.alibaba.fastjson.JSON;
import com.touchspring.ailge.dao.agile.ProjectDao;
import com.touchspring.ailge.dao.agile.ProjectMilestoneDao;
import com.touchspring.ailge.dao.agile.TaskDao;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.agile.ProjectMilestone;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.enums.RedisTableName;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("//redis-syn-test")
public class RedisSynTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private ProjectMilestoneDao projectMilestoneDao;
    @Autowired
    private ProjectDao projectDao;

    @GetMapping("synRedis")
    public ResultData synRedis(){

        //删除
        redisTemplate.delete(redisTemplate.keys("*PROJECT*"));

        //里程碑
        this.batchUpdateMilestoneInRedis(projectMilestoneDao.findAll());
        //任务 启用且不是归档任务
        this.batchUpdateTaskInRedis(taskDao.findAll());

        return ResultData.ok();
    }


    private void batchUpdateMilestoneInRedis(List<ProjectMilestone> projectMilestoneList){
        redisTemplate.executePipelined(new RedisCallback<List<ProjectMilestone>>(){
            @Override
            public List<ProjectMilestone> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                for (ProjectMilestone projectMilestone: projectMilestoneList){
                    redisConnection.hSet((RedisTableName.PROJECT.getCode() + projectMilestone.getProjectId()).getBytes(),
                            (RedisTableName.PROJECT_MILESTONE.getCode() + projectMilestone.getId()).getBytes(),
                            JSON.toJSONString(projectMilestone).getBytes());
                }
                return null;
            }
        });
    }


    private void batchUpdateTaskInRedis(List<Task> taskList) {
        redisTemplate.executePipelined(new RedisCallback<List<Task>>() {
            @Override
            public List<Task> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                for (Task task : taskList) {
                    redisConnection.hSet((RedisTableName.PROJECT.getCode() + task.getProjectId()).getBytes(),
                            (StringUtils.isBlank(task.getParentId()) ? RedisTableName.PARENT_TASK.getCode() + task.getId() : RedisTableName.CHILD_TASK.getCode() + task.getId()).getBytes(),
                            JSON.toJSONString(task).getBytes());
                }
                return null;
            }
        });
    }


    /**
     * 单个任务
     * @return
     */
    @GetMapping("synRedisTaskByTaskId")
    public ResultData synRedisTaskByTaskId(String taskId){
//
//        //删除
//        redisTemplate.delete(redisTemplate.keys("PROJECT:" + projectId));
//
//        //里程碑
//        this.batchUpdateMilestoneInRedis(projectMilestoneDao.findAllByProjectId(projectId));
//        //任务 启用且不是归档任务
//        this.batchUpdateTaskInRedis(taskDao.findAll());


        //任务 启用且不是归档任务
        List<Task> taskList = new ArrayList<>();
        taskList.add(taskDao.findAllById(taskId));
        this.batchUpdateTaskInRedis(taskList);
        return ResultData.ok();
    }
}
