package com.touchspring.ailge.utils;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 切分数量大于1000的in集合
 */
public class GetWhereInStatementUtil {

    /**
     * 获取where in语句
     * @param id 列名
     * @param list in中的条件集合
     * @return
     */
    public static String getString(String id, List<String> list) {
        StringBuffer sb = new StringBuffer();
        String returnString = "";
        if (CollectionUtils.isEmpty(list)) {
            returnString = sb.append(id).append("=''").toString();
        }
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                sb.append(id);
                sb.append(" in (");
            }
            sb.append("'");
            sb.append(list.get(i));
            sb.append("'");
            if (i >= 900 && i < list.size() - 1) {
                if (i % 900 == 0) {
                    sb.append(") or ");
                    sb.append(id);
                    sb.append(" in (");
                } else {
                    sb.append(",");
                }
            } else {
                if (i < list.size() - 1) {
                    sb.append(",");
                }
            }
            if (i == list.size() - 1) {
                sb.append(")");
            }
        }
        returnString = sb.toString();
        return returnString;
    }
}
