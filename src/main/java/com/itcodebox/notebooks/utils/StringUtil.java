package com.itcodebox.notebooks.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LeeWyatt
 */
public class StringUtil {

    public static List<Integer> getStringIndex(String str, String key) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < str.length(); i++) {
            if(str.regionMatches(true, i, key, 0, key.length())) {
                list.add(i);
            }
        }
        return list;
    }

    public static boolean isEmptyOrNull(String title) {
        return  title==null||title.trim().isEmpty();
    }

    public static String[] splitKeywords(String keywords) {
        if (keywords == null||keywords.trim().length()==0) {
            return  null;
        }
        String space = " ";
        //StringBuilder builder = new StringBuilder(50);
        //for (int i = 0; i < keywords.length(); i++) {
        //    char c = keywords.charAt(i);
        //    if (Character.isUpperCase(c)) {
        //        builder.append(space).append(Character.toLowerCase(c));
        //    } else {
        //        builder.append(c);
        //    }
        //}
        String regex = "\\s+|_+";
        return removeDuplication(sqliteEscape(keywords).trim().replaceAll(regex, space).trim().split(space));
    }

    /**
     * 处理SQLite的特殊字符, 需要转义;
     * 比如 搜索% 出现了全部的结果, 那么需要用转义字符, 来处理这个%
     * @param keywords 关键字
     * @return 转义特殊字符后的结果
     * 注意,需要在模糊搜索的SQL语句最后加上  escape '/';
     */
    public static String sqliteEscape(String keywords){
        keywords = keywords.replace("/", "//");
        keywords = keywords.replace("'", "''");
        keywords = keywords.replace("[", "/[");
        keywords = keywords.replace("]", "/]");
        keywords = keywords.replace("%", "/%");
        keywords = keywords.replace("&","/&");
        keywords = keywords.replace("_", "/_");
        keywords = keywords.replace("(", "/(");
        keywords = keywords.replace(")", "/)");
        return keywords;
    }

    private  static String[] removeDuplication(String[] arr){
        ArrayList<String> list = new ArrayList<>();
        for (String s : arr) {
            if (!list.contains(s)) {
                list.add(s);
            }
        }
        return list.toArray(new String[0]);
    }


    /**
     * 分解字符串的方法
     * abcdABcd 查找"aB" ,忽略大小写,那么可以分解成 [ab] cd [AB] cd
     *
     * @param text       内容
     * @param keywords   关键字
     * @param ignoreCase 是否忽略大小写 true 忽略, false 不忽略
     * @return
     */
    public static ArrayList<Pair<String, Boolean>> parseText(String text, String keywords, boolean ignoreCase) {
        ArrayList<Pair<String, Boolean>> list = new ArrayList<>();
        if (text == null || text.isEmpty() || keywords == null || keywords.isEmpty() || (!ignoreCase && !text.contains(keywords)) || (ignoreCase && !text.toUpperCase().contains(keywords.toUpperCase()))) {
            list.add(new Pair<String, Boolean>(text, false));
            return list;
        }
        String textTemp = text;
        String kwTemp = keywords;
        if (ignoreCase) {
            textTemp = text.toUpperCase();
            kwTemp = keywords.toUpperCase();
        }
        int start = 0;
        int kwLen = keywords.length();
        int textLen = text.length();
        while (start < textLen) {
            int startIndex = textTemp.indexOf(kwTemp, start);
            int endIndex = (startIndex == -1) ? -1 : startIndex + kwLen;
            // 如果没有查找到关键字
            if (startIndex == -1) {
                // 没有找到,但是还有剩下的文字,还是加入到list
                list.add(new Pair<>(text.substring(start), false));
                break;
            }
            // 第一个字符串,如果不为空,就添加到list.
            String subStr = text.substring(start, startIndex);
            if (!subStr.isEmpty()) {
                list.add(new Pair<>(subStr, false));
            }
            // 如果找到了关键字.那么添加到list
            list.add(new Pair<>(text.substring(startIndex, endIndex), true));
            start = endIndex;
        }
        return list;
    }

    /**
     * 按正则表达式进行匹配
     * 字符串 "123ABC456EDF" 如果按照正在表达式[0-9]+匹配
     * 那么可以分成4个部分: [123] ABC [456] EDF
     * @param text 文本
     * @param reg 正则表达式
     * @return
     */
    public static ArrayList<Pair<String, Boolean>> matchText(String text, String reg) {
        ArrayList<Pair<String, Boolean>> res = new ArrayList<>();
        if(text==null||text.isEmpty()||reg==null||reg.isEmpty()){
            res.add(new Pair<>(text, false));
            return res;
        }

        Matcher m = null;
        try {
            m = Pattern.compile(reg).matcher(text);
        } catch (Exception e) {
            res.add(new Pair<>(text, false));
            return res;
        }
        ArrayList<Integer> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.start());
            list.add(m.end());
        }
        if(list.size()==0){
            res.add(new Pair<>(text, false));
            return res;
        }

        if(list.get(list.size()-1)!=text.length()){
            list.add(text.length());
        }
        boolean flag = true;
        if (list.get(0) != 0) {
            flag = false;
            list.add(0, 0);
        }
        for (int i = 0; i < list.size() - 1; i++) {
            if(list.get(i).equals(list.get(i + 1))){
                flag = !flag;
                continue;
            }
            String sub = text.substring(list.get(i), list.get(i + 1));
            res.add(new Pair<>(sub, flag));
            flag = !flag;
        }
        return res;
    }

    private static final Random RANDOM = new Random();



    public static String random(int count, boolean letters, boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers) {
        return random(count, start, end, letters, numbers, (char[])null, RANDOM);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars, Random random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        } else {
            if (start == 0 && end == 0) {
                end = 123;
                start = 32;
                if (!letters && !numbers) {
                    start = 0;
                    end = 2147483647;
                }
            }

            char[] buffer = new char[count];
            int gap = end - start;

            while(true) {
                while(true) {
                    while(count-- != 0) {
                        char ch;
                        if (chars == null) {
                            ch = (char)(random.nextInt(gap) + start);
                        } else {
                            ch = chars[random.nextInt(gap) + start];
                        }

                        if (letters && Character.isLetter(ch) || numbers && Character.isDigit(ch) || !letters && !numbers) {
                            if (ch >= '\udc00' && ch <= '\udfff') {
                                if (count == 0) {
                                    ++count;
                                } else {
                                    buffer[count] = ch;
                                    --count;
                                    buffer[count] = (char)('\ud800' + random.nextInt(128));
                                }
                            } else if (ch >= '\ud800' && ch <= '\udb7f') {
                                if (count == 0) {
                                    ++count;
                                } else {
                                    buffer[count] = (char)('\udc00' + random.nextInt(128));
                                    --count;
                                    buffer[count] = ch;
                                }
                            } else if (ch >= '\udb80' && ch <= '\udbff') {
                                ++count;
                            } else {
                                buffer[count] = ch;
                            }
                        } else {
                            ++count;
                        }
                    }
                    return new String(buffer);
                }
            }
        }
    }


    public static String formatFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d Byte", size);
        }
    }

    /**
     * 后缀名,没有点.
     */
    @NotNull
    public static String getExtension(@NotNull String path) {
        return path.substring(path.lastIndexOf(".")).replace(".", "").toLowerCase();
    }
}
