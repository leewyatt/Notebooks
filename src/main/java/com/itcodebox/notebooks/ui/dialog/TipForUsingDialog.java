package com.itcodebox.notebooks.ui.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBCardLayout;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBSlidingPanel;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class TipForUsingDialog extends DialogWrapper {
    private final JBSlidingPanel slidingPanel = new JBSlidingPanel();
    private static final int DEFAULT_WIDTH = 690;
    private static final int DEFAULT_HEIGHT = 535;

    private final Tip[] tips = new Tip[]{
            //联系方式
            new Tip("Contact",
                    new String[]{
                            "提交Bug和建议 或 交流JavaFX Swing的开发,请加QQ群: <b>715598051</b>",
                            "Email: <b>" + PluginConstant.EMAIL_GMAIL + "</b>",
                            "Github: <b>" + PluginConstant.URL_GITHUB + "</b>",
                    },
                    null),


            new Tip("Switch between full view and compact view",
                    new String[]{
                            "Full View: The operation is more convenient. Suitable for large screens or organize notes",
                            "Compact View: Suitable for small screens",
                            "完整视图: 拥有更多的操作,也更直观; 适合大屏幕或者整理笔记时使用",
                            "精简视图: 适合小屏幕"
                    },
                    "/images/viewChanges.png"
            ),
            new Tip("Clear cache",
                    new String[]{
                            "Usually don't need to clean up manually, it will be automatically cleaned up when you close the IDE normally;",
                            "Only when you force the IDE process to exit and force shutdown, it is possible to manually clean up the cache",
                            "通常不需要手动清理,正常关闭IDE时会自动清理; 只有当强制退出IDE进程,强制关机,才有可能需要手动清理缓存"
                    },
                    "/images/clear_cache.png"
            ),

            new Tip("Open the search pop-up window",
                    new String[]{
                    },
                    "/images/search.png"
            ),

            new Tip("Add image for note",
                    new String[]{
                    },
                    "/images/add_image.png"),

            new Tip("Set the size of the thumbnail",
                    new String[]{
                    },
                    "/images/thumb_size.png"),

            //more 更多操作
            new Tip("Add, delete, and modify operations in Compact view",
                    new String[]{},
                    "/images/moreActions.png"),

            //添加
            new Tip("How to quickly add a note",
                    new String[]{
                            "1. Select text/code in editor.",
                            "2. Click with the right mouse button.",
                            "3. Click the menu item: Add to Notebook."
                    },
                    "/images/addNote.png"),
            //插入代码与使用
            new Tip("How to insert the content of the note into the editor",
                    new String[]{
                            "1. Click with the right mouse button.",
                            "2. Click the menu item: Insert Code Into Editor.",
                            "3. Select note and click Insert."
                    },
                    "/images/insertNote.png"),

            //删除
            new Tip("How to delete an item",
                    new String[]{
                            "1. Select the item in the list.",
                            "2. Click with the right mouse button.",
                            "3. Click the menu item: Delete."
                    },
                    "/images/deleteItem.png"),
            //修改
            new Tip("How to rename an item",
                    new String[]{
                            "1. Select the item in the list.",
                            "2. Click with the right mouse button.",
                            "3. Click the menu item: Rename."
                    },
                    "/images/renameItem.png"),
            //修改顺序
            new Tip("How to change the item order",
                    new String[]{
                            "1. Select the item in the list.",
                            "2. Drag up or down to the new position.",
                            "3. Release the mouse button."
                    },
                    "/images/dragMove.png"),
            //修改Note所属的Chapter
            new Tip("Change the Chapter of a Note",
                    new String[]{
                            "1. Select the note in the list.",
                            "2. Drag on the new chapter.",
                            "3. Release the mouse button.",
                            "* The same operation applies to change the Notebook of a Chapter."
                    },
                    "/images/dragOn.png"),
            //查找
            new Tip("Search Item by title",
                    new String[]{
                            "1. Select the list.",
                            "2. Input the keywords.",
                            "3. You can use the Tab key to jump in the search results."
                    },
                    "/images/searchItem.png"),
            //刷新
            new Tip("Refresh List",
                    new String[]{
                            "1. Usually, the changes between the same application are automatically synchronized<br/>" +
                                    "without clicking refresh button.",
                            "2. Data has been modified between different application(e.g. Idea,Webstorm...)<br/> " +
                                    "If you need to view the update, you need to click refresh button.",
                            "1. 通常你不需要手动点击刷新按钮;同一个IDE 同一版本下会自动同步数据;比如打开多个Idea不需要手动刷新.",
                            "2. 只有当你打开不同的IDE,并对数据进行了增删改操作,才需要手动更新. <br/>例如打开了Idea,Android Studio,Webstorm,Pycharm"
                    },
                    "/images/refresh.png"),

            //Export Import JSON
            new Tip("Export / Import JSON",
                    new String[]{
                            "JSON format can be used to import and export data"
                    },
                    "/images/jsonActions.png"),

            //导出MarkDown笔记
            new Tip("Export the Notebook as a MarkDown file",
                    new String[]{
                            "1. Select the notebook in the list.",
                            "2. Click with the right mouse button.",
                            "3. Click the menu item: Export MarkDown.",
                            "4. You can customize the Markdown template<br/>" +
                                    "File -> Settings -> Tools -> Notebooks[笔记本]->Groovy template"
                    },
                    "/images/exportMD.png"),
            ////控制列表的显示与隐藏
            //new Tip("How to control the visibility of the list",
            //        new String[]{
            //                "Click the corresponding icon button on the toolbar."
            //        },
            //        "/images/showList.png"),

            //方向键控制左右移动和选择
            new Tip("How to use the keys to change the selection",
                    new String[]{
                            "1. Select any list",
                            "2. Left and right keys: jump to other lists",
                            "3. Up and down keys: change the selection"
                    },
                    "/images/arrowKey.png"),

            //改变每个列表的大小
            new Tip("How to change the width of the list",
                    new String[]{
                            "Press and drag on the split line to change the width"
                    },
                    "/images/changeWidth.png"),

            //其他设置File | Settings | Tools | Notebooks
            new Tip("Other settings",
                    new String[]{
                            "Other settings: such as font settings, border,template ...",
                            "File -> Settings -> Tools -> Notebook [笔记本]"
                    },
                    "/images/otherSettings.png"),
            new Tip("Reward",
                    new String[]{
                            "如果您觉得还算满意. 可以使用支付宝/微信支付打赏,可以留言提供您的名字/昵称",
                            "您提供的名字/昵称以及打赏总额将会被记录,将展示在打赏列表里(例如QQ群)。"
                    },
                    "/images/support-pay.png")
    };

    private final AppSettingsState appSettingsState = AppSettingsState.getInstance();
    private final int maxIndex = tips.length;
    private final DialogWrapperAction previousAction = new DialogWrapperAction(message("tipDialog.previous")) {
        @Override
        protected void doAction(ActionEvent actionEvent) {
            appSettingsState.tipIndex = appSettingsState.tipIndex - 1;
            if (appSettingsState.tipIndex < 0) {
                appSettingsState.tipIndex = 0;
            }
            slidingPanel.swipe(appSettingsState.tipIndex + "", JBCardLayout.SwipeDirection.AUTO);
            previousAction.setEnabled(appSettingsState.tipIndex - 1 >= 0);
            nextAction.setEnabled(appSettingsState.tipIndex + 1 < maxIndex);
        }
    };

    private final DialogWrapperAction nextAction = new DialogWrapperAction(message("tipDialog.next")) {
        @Override
        protected void doAction(ActionEvent actionEvent) {
            appSettingsState.tipIndex = appSettingsState.tipIndex + 1;
            if (appSettingsState.tipIndex > maxIndex) {
                appSettingsState.tipIndex = maxIndex;
            }
            slidingPanel.swipe(appSettingsState.tipIndex + "", JBCardLayout.SwipeDirection.AUTO);
            previousAction.setEnabled(appSettingsState.tipIndex - 1 >= 0);
            nextAction.setEnabled(appSettingsState.tipIndex + 1 < maxIndex);
        }
    };

    public TipForUsingDialog() {
        super(true);
        setTitle(message("tipDialog.title"));
        setCancelButtonText(message("tipDialog.button.cancel"));
        getRootPane().setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        getRootPane().setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        init();
    }

    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        for (int i = 0; i < tips.length; i++) {
            slidingPanel.add(i + "", new TipPanel(tips[i]));
        }
        slidingPanel.swipe(appSettingsState.tipIndex + "", JBCardLayout.SwipeDirection.AUTO);
        previousAction.setEnabled(appSettingsState.tipIndex - 1 >= 0);
        nextAction.setEnabled(appSettingsState.tipIndex + 1 < maxIndex);
        return new JBScrollPane(slidingPanel);
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{
                previousAction,
                nextAction,
                getCancelAction()
        };
    }
}
