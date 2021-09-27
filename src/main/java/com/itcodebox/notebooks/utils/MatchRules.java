package com.itcodebox.notebooks.utils;

public enum MatchRules {
        /**
         * 把字符串当正则表达式去匹配
         */
        REGEX,
        /**
         * 把字符串当普通字符串去匹配; 区分大小写
         */
        MATCH_CASE,
        /**
         *把字符串当成普通字符串匹配,不区分大小写
         */
        IGNORE_CASE
    }