package com.touchspring.ailge.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

/**
 * 解析 ${name}发生了${event}  格式数据为 指定文本字符串
 */
@Slf4j
public class PlaceholderUtil {

    /**
     * Prefix for system property placeholders: "${"
     */
    public static final String PLACEHOLDER_PREFIX = "${";
    /**
     * Suffix for system property placeholders: "}"
     */
    public static final String PLACEHOLDER_SUFFIX = "}";

    public static String resolvePlaceholders(String text, Map<String, String> parameter) {

        return resolveByRule(text, placeholderValue -> String.valueOf(parameter.get(placeholderValue)));


//        if (parameter == null || parameter.isEmpty()) {
//            return text;
//        }
//        StringBuffer buf = new StringBuffer(text);
//        int startIndex = buf.indexOf(PLACEHOLDER_PREFIX);
//        while (startIndex != -1) {
//            int endIndex = buf.indexOf(PLACEHOLDER_SUFFIX, startIndex + PLACEHOLDER_PREFIX.length());
//            if (endIndex != -1) {
//                String placeholder = buf.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);
//                int nextIndex = endIndex + PLACEHOLDER_SUFFIX.length();
//                try {
//                    String propVal = parameter.get(placeholder);
//                    if (propVal != null) {
//                        buf.replace(startIndex, endIndex + PLACEHOLDER_SUFFIX.length(), propVal);
//                        nextIndex = startIndex + propVal.length();
//                    } else {
//                        log.warn("Could not resolve placeholder '" + placeholder + "' in [" + text + "] ");
//                    }
//                } catch (Exception ex) {
//                    log.warn("Could not resolve placeholder '" + placeholder + "' in [" + text + "]: " + ex);
//                }
//                startIndex = buf.indexOf(PLACEHOLDER_PREFIX, nextIndex);
//            } else {
//                startIndex = -1;
//            }
//        }
//        return buf.toString();
    }

    private static String resolveByRule(String content, Function<String, String> rule) {
        int start = content.indexOf(PLACEHOLDER_PREFIX);
        if (start == -1) {
            return content;
        }
        StringBuilder result = new StringBuilder(content);
        while (start != -1) {
            int end = result.indexOf(PLACEHOLDER_SUFFIX, start + 1);
            //获取占位符属性值，如${id}, 即获取id
            String placeholder = result.substring(start + PLACEHOLDER_PREFIX.length(), end);
            //替换整个占位符内容，即将${id}值替换为替换规则回调中的内容
            String replaceContent = placeholder.trim().isEmpty() ? "" : rule.apply(placeholder);
            result.replace(start, end + PLACEHOLDER_SUFFIX.length(), replaceContent);
            start = result.indexOf(PLACEHOLDER_PREFIX, start + replaceContent.length());
        }
        return result.toString();
    }

}
