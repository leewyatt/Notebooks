package com.itcodebox.notebooks.utils;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * @author LeeWyatt
 *
 * 主要用于文字的本地化处理
 *  使用方法.
 *      1. 导入静态的方法 import static com.itcodebox.aciontdemos.utils.TextBundle.message;
 *          这种方法可以直接在IDEA里看到key的值,推荐
 *
 *      2. TextBundle.message(key) 这种方法不能直接看到key的值
 */
public class NotebooksBundle extends AbstractBundle {
    /**
     * 注意并没有后缀.properties
     */
    private static final String NOTEBOOKS_BUNDLE = "messages.NotebooksBundle";
    private static final NotebooksBundle INSTANCE = new NotebooksBundle();
    private NotebooksBundle() {
        super(NOTEBOOKS_BUNDLE);
    }

    @NotNull
    @Contract(pure = true)
    public static String message(@PropertyKey(resourceBundle= NOTEBOOKS_BUNDLE) String key, Object... objs) {
        return INSTANCE.getMessage(key,objs);
    }

}
