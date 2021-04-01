package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_ISSUE_HISTORY")
public class ProjectIssueHistory extends ProjectIssue {

    private static final long serialVersionUID = 1L;

    /**
     * 项目问题id
     */
    @TableField("PROJECT_ISSUE_ID")
    private String projectIssueId;



    public static <T>void fatherToChild(T father,T child) throws Exception {
        if (child.getClass().getSuperclass() != father.getClass()) {
            throw new Exception("child 不是 father 的子类");
        }
        Class<?> fatherClass = father.getClass();
        Field[] declaredFields = fatherClass.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            if (field.getName().equals("serialVersionUID")) {
                continue;
            }
            Method method = fatherClass.getDeclaredMethod("get" + upperHeadChar(field.getName()));
            Object obj = method.invoke(father);
            field.setAccessible(true);
            field.set(child, obj);
        }
    }

    private static String upperHeadChar(String in) {
        String head = in.substring(0, 1);
        return head.toUpperCase() + in.substring(1, in.length());
    }


}
