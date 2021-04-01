package com.touchspring.ailge.utils;

import com.touchspring.ailge.dao.agile.CircleDao;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.agile.ProjectMilestone;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.enums.BaseEnums;
import com.touchspring.ailge.enums.OpenStatusEnum;
import com.touchspring.ailge.enums.TaskStatusEnum;
import com.touchspring.ailge.enums.TaskTypeEnum;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 对象变更工具类
 * @param <T>
 */
@Service
public class BeanChangeUtilService<T> {

    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private CircleDao circleDao;

    private final static String CIRCLE_ID = "circleId";
    private final static String PROJECT_USER_ID = "projectUserId";
    private final static String OWNER_U_ID = "ownerUid";
    private final static String STATUS = "status";
    private final static String OPEN_STATUS = "openStatus";
    private final static String TYPE = "type";
    private final static String IS_REQUIREMENT = "isRequirement";
    private final static String ENABLED = "enabled";
    private final static String FORBIDDEN = "forbidden";

    public String contrastObj(Object oldBean, Object newBean) {
        // 创建字符串拼接对象
        StringBuilder str = new StringBuilder();
        // 转换为传入的泛型T
        T pojo1 = (T) oldBean;
        T pojo2 = (T) newBean;
        // 通过反射获取类的Class对象
        Class clazz = pojo1.getClass();
        // 获取类型及字段属性
        Field[] fields = clazz.getDeclaredFields();
        return jdk8Before(fields, pojo1, pojo2, str,clazz);
//        return jdk8OrAfter(fields, pojo1, pojo2, str,clazz);
    }

    // jdk8 普通循环方式
    public String jdk8Before(Field[] fields,T pojo1,T pojo2,StringBuilder str,Class clazz){
        try {
            for (Field field : fields) {
                if(field.isAnnotationPresent(PropertyMsg.class)){
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                    // 获取对应属性值
                    Method getMethod = pd.getReadMethod();
                    Object o1 = getMethod.invoke(pojo1);
                    Object o2 = getMethod.invoke(pojo2);
                    if (o1 == null || o2 == null || StringUtils.isBlank(o1.toString()) || StringUtils.isBlank(o2.toString())) {
                        continue;
                    }
                    if (!o1.toString().equals(o2.toString())) {
                        if (field.getName().equals(CIRCLE_ID))
                            str.append("Changed " + field.getAnnotation(PropertyMsg.class).value() + ", From \"" + circleDao.selectById(o1.toString()).getName()
                                    + "\", To \"" + circleDao.selectById(o2.toString()).getName() + "\";");
                        else if (field.getName().equals(PROJECT_USER_ID) || field.getName().equals(OWNER_U_ID))
                            str.append("Changed " + field.getAnnotation(PropertyMsg.class).value() + ", From \"" + sysUserDao.selectById(o1.toString()).getRealName()
                                    + "\", To \"" + sysUserDao.selectById(o2.toString()).getRealName() + "\";");
                        else if (field.getName().equals(STATUS) && clazz.equals(Task.class))
                            str.append("Changed " + field.getAnnotation(PropertyMsg.class).value() + ", From \"" + TaskStatusEnum.getMessage(o1.toString()) + "\", To \"" + TaskStatusEnum.getMessage(o2.toString()) + "\";");
                        else if (field.getName().equals(OPEN_STATUS) && clazz.equals(Task.class))
                            str.append("Changed " + field.getAnnotation(PropertyMsg.class).value() + ", From \"" + OpenStatusEnum.getMessage(Integer.valueOf(o1.toString())) + "\", To \"" + OpenStatusEnum.getMessage(Integer.valueOf(o2.toString())) + "\";");
                        else if (field.getName().equals(TYPE) && clazz.equals(Task.class)) {
                            String[] type1 = o1.toString().split(",");
                            StringBuilder typeStr1 = new StringBuilder();
                            for (int i = 0; i < type1.length; i++){
                                typeStr1.append(TaskTypeEnum.getMessage(Integer.valueOf(type1[i]))).append(",");
                            }
                            typeStr1.replace(typeStr1.length()-1, typeStr1.length(), "");
                            String[] type2 = o2.toString().split(",");
                            StringBuilder typeStr2 = new StringBuilder();
                            for (int i = 0; i < type2.length; i++){
                                typeStr2.append(TaskTypeEnum.getMessage(Integer.valueOf(type2[i]))).append(",");
                            }
                            typeStr2.replace(typeStr2.length()-1, typeStr2.length(), "");
                            str.append("Changed " + field.getAnnotation(PropertyMsg.class).value() + ", From \"" + typeStr1.toString() + "\", To \"" + typeStr2.toString() + "\";");//
                        }
                        else if ((field.getName().equals(IS_REQUIREMENT) || field.getName().equals(ENABLED) || field.getName().equals(FORBIDDEN)) && (clazz.equals(Task.class) || clazz.equals(ProjectMilestone.class)))
                            str.append("Changed " + field.getAnnotation(PropertyMsg.class).value() + ", From \"" + BaseEnums.getMessage(Integer.valueOf(o1.toString())) + "\", To \"" + BaseEnums.getMessage(Integer.valueOf(o2.toString())) + "\";");
                        else
                            str.append("Changed " + field.getAnnotation(PropertyMsg.class).value() + ", From \"" + o1 + "\", To \"" + o2 + "\";");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(str)) str.replace(str.length() - 1, str.length(), "");
        return str.toString();
    }

    // lambda表达式，表达式内部的变量都是final修饰，需要传入需要传入final类型的数组
    public String jdk8OrAfter(Field[] fields, T pojo1, T pojo2, StringBuilder str, Class clazz){
        final int[] i = {1};
        Arrays.asList(fields).forEach(f -> {
            if(f.isAnnotationPresent(PropertyMsg.class)){
                try {
                    PropertyDescriptor pd = new PropertyDescriptor(f.getName(), clazz);
                    // 获取对应属性值
                    Method getMethod = pd.getReadMethod();
                    Object o1 = getMethod.invoke(pojo1);
                    Object o2 = getMethod.invoke(pojo2);
                    if (o1 == null || o2 == null) {
                        return;
                    }
                    if (!o1.toString().equals(o2.toString())) {
                        str.append(i[0] + "、" + f.getAnnotation(PropertyMsg.class).value() + ":" + "修改前=>" + o1 + "\t修改后=>" + o2 + ";");
                        i[0]++;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return str.toString();
    }
}
