package com.netease.lib.tasks.util;

public class NamingUtils {
    public static String toLowerCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        // 如果已经是首字母小写，直接返回
        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        }

        // 处理多单词的驼峰命名
        StringBuilder result = new StringBuilder();
        boolean firstWord = true;

        // 分割单词（根据大写字母分割）
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                if (firstWord) {
                    result.append(Character.toLowerCase(c));
                    firstWord = false;
                } else {
                    result.append(c);
                }
            } else {
                result.append(firstWord ? Character.toLowerCase(c) : c);
                firstWord = false;
            }
        }

        return result.toString();
    }
}
