package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface PluginIcons {
    Icon Time = IconLoader.getIcon("/icons/time.svg", PluginIcons.class);
    /**
     * ToolWindow使用的图标(灰色) 尺寸13*13
     */
    //Icon ToolWindowGray = IconLoader.getIcon("/icons/note.svg", PluginIcons.class);
    /**
     *  ToolWindow使用的图标(蓝色渐变) 尺寸13*13
     */
    Icon ToolWindowBlue = IconLoader.getIcon("/icons/tool_window.svg", PluginIcons.class);

    /**
     * 弹出的添加窗口里的Add 按钮的图标（因为默认选择了，所以颜色修改了）
     */
    Icon Add = IconLoader.getIcon("/icons/add.svg", PluginIcons.class);

    /**
     * 右键菜单的图标: 支持明暗两种模式
     */
    Icon AutoWarp = IconLoader.getIcon("/icons/auto_warp.svg", PluginIcons.class);
    Icon Line = IconLoader.getIcon("/icons/line.svg", PluginIcons.class);
    Icon Delete = IconLoader.getIcon("/icons/delete.svg", PluginIcons.class);
    Icon Rename = IconLoader.getIcon("/icons/rename.svg", PluginIcons.class);
    Icon MarkdownFile = IconLoader.getIcon("/icons/markdown_file.svg", PluginIcons.class);

    /**
     * 表格列表使用的图标
     */
    Icon DragToAdd = IconLoader.getIcon("/icons/drag_to_add.svg", PluginIcons.class);
    Icon NotebookCell20 = IconLoader.getIcon("/icons/notebook_cell_20.svg", PluginIcons.class);
    Icon NotebookCell = IconLoader.getIcon("/icons/notebook_cell.svg", PluginIcons.class);
    Icon ChapterCell = IconLoader.getIcon("/icons/chapter_cell.svg", PluginIcons.class);
    Icon NoteCell = IconLoader.getIcon("/icons/note_cell.svg", PluginIcons.class);
    Icon CellSelected = IconLoader.getIcon("/icons/cell_selected.svg", PluginIcons.class);
    //Icon CellSelected20 = IconLoader.getIcon("/icons/cell_selected_20.svg", PluginIcons.class);
    //Icon NotebookCellNotSelected = IconLoader.getIcon("/icons/notebook_cell_not_selected.svg", PluginIcons.class);
    //Icon ChapterCellNotSelected = IconLoader.getIcon("/icons/chapter_cell_not_selected.svg", PluginIcons.class);
    //Icon NoteCellNotSelected = IconLoader.getIcon("/icons/note_cell_not_selected.svg", PluginIcons.class);

    /**
     * 侧边栏工具按钮使用的图标
     */
    Icon Detail = IconLoader.getIcon("/icons/detail.svg", PluginIcons.class);
    Icon ExpandAll = IconLoader.getIcon("/icons/expandall.svg", PluginIcons.class);

    /**
     * 保存更改用的图标
     */
    //Icon SaveChangesRed = IconLoader.getIcon("/icons/save_changes.svg", PluginIcons.class);

    Icon Tip = IconLoader.getIcon("/icons/tip.svg", PluginIcons.class);
    Icon Link = IconLoader.getIcon("/icons/link.svg", PluginIcons.class);

    Icon Code = IconLoader.getIcon("/icons/code.svg", PluginIcons.class);
    Icon Description = IconLoader.getIcon("/icons/description.svg", PluginIcons.class);
    Icon ImageTitle = IconLoader.getIcon("/icons/image_title.svg", PluginIcons.class);
    Icon Insert = IconLoader.getIcon("/icons/insert.svg", PluginIcons.class);
    Icon InsertPopup = IconLoader.getIcon("/icons/insert_color.svg", PluginIcons.class);
    Icon Type = IconLoader.getIcon("/icons/type.svg", PluginIcons.class);
    Icon Save = IconLoader.getIcon("/icons/save.svg", PluginIcons.class);
    Icon SaveRed = IconLoader.getIcon("/icons/save_changes.svg", PluginIcons.class);
    Icon Support = IconLoader.getIcon("/icons/support.svg", PluginIcons.class);
    Icon Email  = IconLoader.getIcon("/icons/email.svg", PluginIcons.class);

    Icon Donate = IconLoader.getIcon("/images/support-pay.png",PluginIcons.class);
    Icon MoreHor = IconLoader.getIcon("/icons/moreHorizontal.svg",PluginIcons.class);
    Icon CodeRange = IconLoader.getIcon("/icons/inSelection.svg",PluginIcons.class);
    Icon Search = IconLoader.getIcon("/icons/search.svg",PluginIcons.class);

    Icon EditColor = IconLoader.getIcon("/icons/edit_color.svg",PluginIcons.class);
    Icon List = IconLoader.getIcon("/icons/list.svg",PluginIcons.class);
    /**
     * 图片相关
     */
    Icon Image = IconLoader.getIcon("/icons/image.svg",PluginIcons.class);
    Icon ImageColorful = IconLoader.getIcon("/icons/image_colorful.svg",PluginIcons.class);
    Icon ImageDefaultBig = IconLoader.getIcon("/icons/image_default_big.svg",PluginIcons.class);
    Icon JPG = IconLoader.getIcon("/icons/jpg.svg",PluginIcons.class);
    Icon PNG = IconLoader.getIcon("/icons/png.svg",PluginIcons.class);
    Icon GIF = IconLoader.getIcon("/icons/gif.svg",PluginIcons.class);
    Icon Unknown = IconLoader.getIcon("/icons/unknown.svg",PluginIcons.class);
    Icon ImageSize = IconLoader.getIcon("/icons/image_size.svg",PluginIcons.class);
    Icon Data = IconLoader.getIcon("/icons/data.svg",PluginIcons.class);
    Icon Paste = IconLoader.getIcon("/icons/paste.svg",PluginIcons.class);
    Icon TextInfo = IconLoader.getIcon("/icons/write_text.svg",PluginIcons.class);
    Icon Show = IconLoader.getIcon("/icons/show.svg",PluginIcons.class);
    Icon Hide = IconLoader.getIcon("/icons/hide.svg",PluginIcons.class);
    Icon Clear = IconLoader.getIcon("/icons/clear.svg",PluginIcons.class);

}