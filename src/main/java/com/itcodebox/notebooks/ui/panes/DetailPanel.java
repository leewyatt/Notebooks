package com.itcodebox.notebooks.ui.panes;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.DumbAwareToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.openapi.wm.impl.welcomeScreen.BottomLineBorder;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.ui.components.*;
import com.intellij.ui.components.panels.HorizontalBox;
import com.intellij.util.ui.JBEmptyBorder;
import com.itcodebox.notebooks.action.SearchRecordAction;
import com.itcodebox.notebooks.constant.PluginColors;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.entity.Chapter;
import com.itcodebox.notebooks.entity.ImageRecord;
import com.itcodebox.notebooks.entity.Note;
import com.itcodebox.notebooks.entity.Notebook;
import com.itcodebox.notebooks.projectservice.NotebooksUIManager;
import com.itcodebox.notebooks.projectservice.ProjectStorage;
import com.itcodebox.notebooks.projectservice.RecordListener;
import com.itcodebox.notebooks.service.ChapterService;
import com.itcodebox.notebooks.service.NoteService;
import com.itcodebox.notebooks.service.NotebookService;
import com.itcodebox.notebooks.service.impl.ChapterServiceImpl;
import com.itcodebox.notebooks.service.impl.NoteServiceImpl;
import com.itcodebox.notebooks.service.impl.NotebookServiceImpl;
import com.itcodebox.notebooks.ui.component.CodeEditorUtil;
import com.itcodebox.notebooks.ui.component.IconButton;
import com.itcodebox.notebooks.ui.dialog.*;
import com.itcodebox.notebooks.ui.tables.ChapterTable;
import com.itcodebox.notebooks.ui.tables.NoteTable;
import com.itcodebox.notebooks.ui.tables.NotebookTable;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;
import com.itcodebox.notebooks.utils.CustomFileUtil;
import com.itcodebox.notebooks.utils.ImageRecordUtil;
import icons.PluginIcons;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.itcodebox.notebooks.utils.NotebooksBundle.message;

/**
 * @author LeeWyatt
 */
public class DetailPanel extends JPanel {
    private final Border CHANGED_BORDER = BorderFactory.createLineBorder(PluginColors.WARING_BORDER_COLOR, 2);
    private final Border NORMAL_BORDER = new JBEmptyBorder(2);
    //private static final Border ENABLED_FALSE_BORDER = BorderFactory.createLineBorder(JBColor.border());

    private final Project project;
    private final NoteTable noteTable;
    private final NotebookTable notebookTable;
    private final ChapterTable chapterTable;
    protected DefaultComboBoxModel<Notebook>
            notebookComboBoxModel = new DefaultComboBoxModel<>();
    protected ComboBox<Notebook> comboBoxNotebook = new ComboBox<>(notebookComboBoxModel);
    protected DefaultComboBoxModel<Chapter> chapterComboBoxModel = new DefaultComboBoxModel<>();
    protected ComboBox<Chapter> comboBoxChapter = new ComboBox<>(chapterComboBoxModel);
    protected DefaultComboBoxModel<Note> noteComboBoxModel = new DefaultComboBoxModel<>();
    protected ComboBox<Note> comboBoxNote = new ComboBox<>(noteComboBoxModel);
    protected TextFieldWithAutoCompletion<String> fieldFileType;

    private final Editor fieldContent;
    private final JBTextArea fieldDesc = new JBTextArea();

    protected NoteService noteService = NoteServiceImpl.getInstance();
    protected ChapterService chapterService = ChapterServiceImpl.getInstance();
    protected NotebookService notebookService = NotebookServiceImpl.getInstance();

    private final NotebooksUIManager uiManager;
    private final ProjectStorage projectStorage;

    private final String URL_REG = "\\s*((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?)\\s*";
    private final Pattern urlPattern = Pattern.compile(URL_REG);
    private final ItemListener notebookItemListener;
    private final ItemListener chapterItemListener;
    private final ItemListener noteItemListener;
    private final JPanel centerPanel = new JPanel(new BorderLayout());
    private final ImagePanel imagePanel;
    private final JComponent northPanel;
    private final JPanel descScrollPane;
    private final JBCheckBox checkBox = new JBCheckBox();

    public Editor getFieldContent() {
        return fieldContent;
    }

    public JBTextArea getFieldDesc() {
        return fieldDesc;
    }

    public JComponent getNorthPanel() {
        return northPanel;
    }

    public JPanel getDescScrollPane() {
        return descScrollPane;
    }

    public DetailPanel(Project project, NotebookTable notebookTable, ChapterTable chapterTable, NoteTable noteTable) {
        this.project = project;
        this.notebookTable = notebookTable;
        this.chapterTable = chapterTable;
        this.noteTable = noteTable;
        fieldContent = CodeEditorUtil.createCodeEditor(project);

        //设置渲染器
        RecordCllRender renderer = new RecordCllRender();
        comboBoxNotebook.setRenderer(renderer);
        comboBoxChapter.setRenderer(renderer);
        comboBoxNote.setRenderer(renderer);

        //必须显示的释放Editor
        Disposer.register(project, () -> EditorFactory.getInstance().releaseEditor(fieldContent));
        notebookItemListener = e -> notebookTable.selectedRow((Notebook) comboBoxNotebook.getSelectedItem());
        chapterItemListener = e -> chapterTable.selectedRow((Chapter) comboBoxChapter.getSelectedItem());
        noteItemListener = e -> noteTable.selectedRow((Note) comboBoxNote.getSelectedItem());

        uiManager = project.getService(NotebooksUIManager.class);
        projectStorage = project.getService(ProjectStorage.class);
        AppSettingsState appSettingsState = AppSettingsState.getInstance();
        Font textFont = new Font(appSettingsState.customFontName, Font.PLAIN, appSettingsState.customFontSize);
        setLayout(new BorderLayout());

        //1. ------创建顶部的界面-----------
        northPanel = buildTopPanel();
        add(northPanel, BorderLayout.NORTH);
        //2. -------------创建中间的界面-------------
        centerPanel.add(getNoteToolbar(), BorderLayout.NORTH);

        //1. 笔记描述和笔记内容的容器
        JBSplitter textPanel = new JBSplitter(true, 0.15F);
        // 设置笔记描述组件
        fieldDesc.setFont(textFont);
        descScrollPane = new JPanel(new BorderLayout(0, 5));
        descScrollPane.add(initDescriptionPanel(), BorderLayout.NORTH);
        descScrollPane.add(new JBScrollPane(fieldDesc));
        textPanel.setFirstComponent(descScrollPane);
        // 设置笔记的内容组件
        JPanel contentScrollPane = new JPanel(new BorderLayout(0, 0));
        initFieldFileType(project, noteTable);
        contentScrollPane.add(initFileTypePanel(), BorderLayout.NORTH);
        //无需多余的JBScrollPane; 否则滚动会出现问题
        contentScrollPane.add(fieldContent.getComponent());
        textPanel.setSecondComponent(contentScrollPane);

        //2.图片面板
        imagePanel = new ImagePanel(project);

        JBTabbedPane tabbedPane = new JBTabbedPane();
        tabbedPane.addTab(message("detailPanel.tab.content"), PluginIcons.TextInfo, textPanel, "");
        tabbedPane.addTab(message("detailPanel.tab.image"), PluginIcons.ImageColorful, imagePanel, "");
        centerPanel.add(tabbedPane);
        add(centerPanel);
        setBorder(new JBEmptyBorder(0, 5, 5, 5));
        fieldDesc.setLineWrap(true);
        fieldDesc.setEditable(true);

    }

    private void initFieldFileType(Project project, NoteTable noteTable) {
        fieldFileType = new TextFieldWithAutoCompletion<String>(
                project,
                new TextFieldWithAutoCompletion.StringsCompletionProvider(PluginConstant.EXTENSION_LIST, null),
                true,
                ""
        );
        fieldFileType.setPreferredWidth(120);

        fieldFileType.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                //先获取当前Note!null的类型,如果改变了就保存, 如果没有改变就放弃
                Note note = noteTable.getSelectedObject();
                String fileType = fieldFileType.getText().trim();
                if (note == null || Objects.equals(fileType, note.getType())) {
                    return;
                }
                note.setType(fileType);
                note.setUpdateTime(System.currentTimeMillis());
                noteService.update(note);
                ApplicationManager.getApplication().getMessageBus()
                        .syncPublisher(RecordListener.TOPIC)
                        .onNoteUpdated(project, new Note[]{note});
            }
        });

        fieldFileType.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                String fileTye = fieldFileType.getText().trim();
                Note note = noteTable.getSelectedObject();
                if (note == null || Objects.equals(fileTye, note.getType())) {
                    return;
                }
                if (PluginConstant.EXTENSION_LIST.contains(fileTye)) {
                    note.setType(fileTye);
                    note.setUpdateTime(System.currentTimeMillis());
                    noteService.update(note);
                    ApplicationManager.getApplication().getMessageBus()
                            .syncPublisher(RecordListener.TOPIC)
                            .onNoteUpdated(project, new Note[]{note});
                }
            }
        });
    }

    @NotNull
    private HorizontalBox initFileTypePanel() {
        HorizontalBox typeBox = new HorizontalBox();
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(new JBLabel(message("addNoteDialog.label.language"), PluginIcons.Type, JBLabel.LEFT));
        typeBox.add(labelPanel, BorderLayout.WEST);
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fieldPanel.add(fieldFileType);
        typeBox.add(fieldPanel);
        return typeBox;
    }

    @NotNull
    private HorizontalBox initDescriptionPanel() {
        HorizontalBox typeBox = new HorizontalBox();
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(new JBLabel(message("detailPanel.label.description"), PluginIcons.Description, JBLabel.LEFT));
        typeBox.add(labelPanel, BorderLayout.WEST);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkBox.setText(message("menu.item.autoWarp"));
        checkBox.setSelected(true);
        panel.add(checkBox);
        typeBox.add(panel);
        checkBox.addActionListener(e->{
            fieldDesc.setLineWrap(checkBox.isSelected());
        });
        return typeBox;
    }

    private JComponent buildTopPanel() {
        //导航面板
        JPanel navPanel = new JPanel(new BorderLayout());
        JPanel boxWest = new JPanel(new GridLayout(4, 1));
        boxWest.add(new JBLabel(message("detailPanel.showPanel.text"), PluginIcons.List, JBLabel.LEFT));
        boxWest.add(new JBLabel(message("simple.text.notebook"), PluginIcons.NotebookCell, JBLabel.LEFT));
        boxWest.add(new JBLabel(message("simple.text.chapter"), PluginIcons.ChapterCell, JBLabel.LEFT));
        boxWest.add(new JBLabel(message("simple.text.note"), PluginIcons.NoteCell, JBLabel.LEFT));
        boxWest.setBorder(new JBEmptyBorder(0, 0, 0, 5));
        navPanel.add(boxWest, BorderLayout.WEST);

        JPanel box = new JPanel(new GridLayout(4, 1));
        box.add(getVisiblePanelToolbar());
        box.add(comboBoxNotebook);
        box.add(comboBoxChapter);
        box.add(comboBoxNote);
        navPanel.add(box);

        JPanel boxEast = new JPanel(new GridLayout(4, 1));
        boxEast.add(new JPanel());
        boxEast.add(new IconButton(message("detailPanel.iconButton.more.text"), PluginIcons.MoreHor, showMorePopupMenuForNotebook()));
        boxEast.add(new IconButton(message("detailPanel.iconButton.more.text"), PluginIcons.MoreHor, showMorePopupMenuForChapter()));
        boxEast.add(new IconButton(message("detailPanel.iconButton.more.text"), PluginIcons.MoreHor, showMorePopupMenuForNote()));
        navPanel.add(boxEast, BorderLayout.EAST);

        navPanel.setBorder(BorderFactory.createLineBorder(JBColor.border(), 1));
        //首先增加监听器
        addItemSelectedListener();
        return navPanel;
    }

    private void addItemSelectedListener() {
        if (!isContain(comboBoxNotebook.getItemListeners(), notebookItemListener)) {
            comboBoxNotebook.addItemListener(notebookItemListener);
        }
        if (!isContain(comboBoxChapter.getItemListeners(), chapterItemListener)) {
            comboBoxChapter.addItemListener(chapterItemListener);
        }
        if (!isContain(comboBoxNote.getItemListeners(), noteItemListener)) {
            comboBoxNote.addItemListener(noteItemListener);
        }
    }

    /**
     * 是否包含指定的listener
     */
    private boolean isContain(ItemListener[] itemListeners, ItemListener listener) {
        if (itemListeners == null || itemListeners.length == 0) {
            return false;
        }
        for (ItemListener itemListener : itemListeners) {
            if (itemListener == listener) {
                return true;
            }
        }
        return false;
    }

    private void removeItemSelectedListener() {
        if (isContain(comboBoxNotebook.getItemListeners(), notebookItemListener)) {
            comboBoxNotebook.removeItemListener(notebookItemListener);
        }
        if (isContain(comboBoxChapter.getItemListeners(), chapterItemListener)) {
            comboBoxChapter.removeItemListener(chapterItemListener);
        }
        if (isContain(comboBoxNote.getItemListeners(), noteItemListener)) {
            comboBoxNote.removeItemListener(noteItemListener);
        }
    }

    private String getSelectedNotebookTitle() {
        Notebook notebook = (Notebook) comboBoxNotebook.getSelectedItem();
        return notebook == null ? null : notebook.getTitle();
    }

    private String getSelectedChapterTitle() {
        Chapter chapter = (Chapter) comboBoxChapter.getSelectedItem();
        return chapter == null ? null : chapter.getTitle();
    }

    private String getSelectedNoteTitle() {
        Note note = (Note) comboBoxNote.getSelectedItem();
        return note == null ? null : note.getTitle();
    }

    @NotNull
    private Function1<JComponent, Unit> showMorePopupMenuForNote() {
        return component -> {
            String selectedNoteTitle = getSelectedNoteTitle();
            createPopupMenu(
                    e -> new AddNoteDialog(project, new Note()).show(),
                    e -> {
                        Note note = noteTable.getSelectedObject();
                        if (note != null) {
                            new EditNoteDialog(project, note).show();
                        }
                    },
                    e -> {
                        Note note = noteTable.getSelectedObject();
                        if (note == null) {
                            return;
                        }
                        int result = Messages.showOkCancelDialog(message("messagebox.deleteNote.message"),
                                message("messagebox.deleteNote.title"),
                                message("messagebox.ok.delete"),
                                message("messagebox.cancel"),
                                Messages.getWarningIcon());
                        if (result == Messages.OK) {
                            // 准备工作: 获取相关图片资料
                            List<String> records = noteService.getImageRecordsByNoteId(note.getId());
                            // 数据库里删除
                            noteService.delete(note.getId());
                            ApplicationManager
                                    .getApplication()
                                    .getMessageBus()
                                    .syncPublisher(RecordListener.TOPIC)
                                    .onNoteRemoved(project, note);
                            // 收尾工作: 删除相关图片
                            CustomFileUtil.deleteImages(records);
                        }
                    }, selectedNoteTitle != null)
                    .show(component, 8, 8);
            return null;
        };
    }

    @NotNull
    private Function1<JComponent, Unit> showMorePopupMenuForChapter() {
        return component -> {

            String selectedChapterTitle = getSelectedChapterTitle();
            String selectedNotebookTitle = getSelectedNotebookTitle();
            createPopupMenu(
                    e -> new AddChapterDialog(project, selectedNotebookTitle).show(),
                    e -> {
                        if (selectedNotebookTitle != null && selectedChapterTitle != null) {
                            new EditChapterDialog(project, selectedNotebookTitle, selectedChapterTitle, getSelectedNoteTitle()).show();
                        }
                    },
                    e -> {
                        int result = Messages.showOkCancelDialog(
                                message("messagebox.deleteChapter.message"),
                                message("messagebox.deleteChapter.title"),
                                message("messagebox.ok.delete"),
                                message("messagebox.cancel"),
                                Messages.getWarningIcon());
                        Chapter chapter = chapterTable.getSelectedObject();
                        if (result == Messages.OK && chapter != null) {
                            // 准备工作: 获取相关图片
                            List<String> records = noteService.getImageRecordsByChapterId(chapter.getId());
                            //一. 从数据库删除
                            chapterService.delete(chapter.getId());
                            //二. 通知UI删除
                            ApplicationManager
                                    .getApplication()
                                    .getMessageBus()
                                    .syncPublisher(RecordListener.TOPIC)
                                    .onChapterRemoved(project, chapter);
                            // 收尾工作: 删除相关图片
                            CustomFileUtil.deleteImages(records);
                        }
                    }, selectedChapterTitle != null)
                    .show(component, 8, 8);
            return null;
        };
    }

    /**
     * [Notebook] More按钮点击后创建菜单并显示
     *
     * @return null 不需要做返回
     */
    @NotNull
    private Function1<JComponent, Unit> showMorePopupMenuForNotebook() {
        return component -> {
            String selectedNotebookTitle = getSelectedNotebookTitle();
            JBPopupMenu popupMenu = createPopupMenu(
                    e -> new AddNotebookDialog(project).show(),
                    e -> {
                        if (selectedNotebookTitle != null) {
                            new EditNotebookDialog(project, selectedNotebookTitle).show();
                        }
                    },
                    e -> {
                        int result = Messages.showOkCancelDialog(
                                message("messagebox.deleteNotebook.message"),
                                message("messagebox.deleteNotebook.title"),
                                message("messagebox.ok.delete"),
                                message("messagebox.cancel"),
                                Messages.getWarningIcon());
                        Notebook notebook = notebookTable.getSelectedObject();
                        if (result == Messages.OK && notebook != null) {
                            // 准备工作: 获取相关图片
                            List<String> records = noteService.getImageRecordsByNotebookId(notebook.getId());
                            //1. 数据库里删除
                            notebookService.delete(notebook.getId());
                            //2. UI更新
                            ApplicationManager
                                    .getApplication()
                                    .getMessageBus()
                                    .syncPublisher(RecordListener.TOPIC)
                                    .onNotebookRemoved(project, notebook);
                            // 收尾工作: 删除相关图片
                            CustomFileUtil.deleteImages(records);
                        }
                    }, selectedNotebookTitle != null);
            if (selectedNotebookTitle != null) {
                popupMenu.addSeparator();

                JBMenuItem menuItemJson = new JBMenuItem(message("mainPanel.action.exportJson.text"), AllIcons.ToolbarDecorator.Export);
                menuItemJson.addActionListener(e -> notebookTable.exportSingleJson());
                popupMenu.add(menuItemJson);

                JBMenuItem menuItemExportMd = new JBMenuItem(message("detailPanel.menuItem.exportMd"), PluginIcons.MarkdownFile);
                menuItemExportMd.addActionListener(e -> notebookTable.exportAction());
                popupMenu.add(menuItemExportMd);
            }
            popupMenu.show(component, 8, 8);
            return null;
        };
    }

    public void copyContentText() {
        Note note = noteTable.getSelectedObject();
        if (note == null) {
            return;
        }
        StringSelection contents = new StringSelection(note.getContent());
        PluginConstant.CLIPBOARD.setContents(contents, null);

    }

    public void refreshComboBoxModel(RefreshType type) {
        removeItemSelectedListener();
        if (RefreshType.Notebook == type) {
            //清理
            notebookComboBoxModel.removeAllElements();
            chapterComboBoxModel.removeAllElements();
            noteComboBoxModel.removeAllElements();
            //添加
            notebookComboBoxModel.addAll(notebookTable.getRecordModel().getItems());
            chapterComboBoxModel.addAll(chapterTable.getRecordModel().getItems());
            noteComboBoxModel.addAll(noteTable.getRecordModel().getItems());
            //选择
            notebookComboBoxModel.setSelectedItem(notebookTable.getSelectedObject());
            chapterComboBoxModel.setSelectedItem(chapterTable.getSelectedObject());
            noteComboBoxModel.setSelectedItem(noteTable.getSelectedObject());
        }
        if (RefreshType.Chapter == type) {
            //清理
            chapterComboBoxModel.removeAllElements();
            noteComboBoxModel.removeAllElements();
            //添加
            chapterComboBoxModel.addAll(chapterTable.getRecordModel().getItems());
            noteComboBoxModel.addAll(noteTable.getRecordModel().getItems());
            //选择
            chapterComboBoxModel.setSelectedItem(chapterTable.getSelectedObject());
            noteComboBoxModel.setSelectedItem(noteTable.getSelectedObject());
        }
        if (RefreshType.Note == type) {
            //清理
            noteComboBoxModel.removeAllElements();
            //添加
            noteComboBoxModel.addAll(noteTable.getRecordModel().getItems());
            //选择
            noteComboBoxModel.setSelectedItem(noteTable.getSelectedObject());
        }
        //刷新显示
        refreshDetail();
        addItemSelectedListener();
    }

    /**
     * 刷新各个组件的显示
     */
    public void refreshDetail() {
        Note selectedNote = noteTable.getSelectedObject();
        boolean isNotNull = (selectedNote != null);

        // 更新格式
        String oldType = fieldFileType.getText();
        String newType = isNotNull ? selectedNote.getType() : "";
        if (!oldType.equals(newType)) {
            fieldFileType.setText(newType);
        }
        //1. 更新图片信息
        imagePanel.setNote(selectedNote);
        //2. 更新普通面板数据 以及描述信息和笔记内容
        CodeEditorUtil.setEditorHighlighter(fieldContent, isNotNull ? selectedNote.getType() : "");
        Document document = fieldContent.getDocument();
        String oldContent = document.getText();
        String newContent = isNotNull ? selectedNote.getContent() : "";
        if (!newContent.equals(oldContent)) {
            //fieldContent.getCaretModel().moveToOffset();
            int textLength = document.getTextLength();
            ApplicationManager.getApplication().runWriteAction(() -> WriteCommandAction.runWriteCommandAction(project,
                    () -> document.replaceString(0, textLength, newContent)));
        }
        String oldDesc = fieldDesc.getText();
        String newDesc = isNotNull ? selectedNote.getDescription() : "";
        if (!newDesc.equals(oldDesc)) {
            fieldDesc.setText(newDesc);
            fieldDesc.setCaretPosition(0);
        }

        //btnEdit.setEnabled(isNotNull && !AppSettingsState.getInstance().readOnlyMode);
    }

    /**
     * 控制按钮是否可用,
     */
    //public void setReadOnly(boolean isReadOnly) {
    //    btnEdit.setEnabled((noteTable.getSelectedObject() != null) && !AppSettingsState.getInstance().readOnlyMode);
    //}
    private JBPopupMenu createPopupMenu(ActionListener addListener, ActionListener editListener, ActionListener deleteListener, boolean isSelectedItem) {
        JBPopupMenu popupMenu = new JBPopupMenu();
        JBMenuItem menuItemAdd = new JBMenuItem(message("simple.text.Add"), AllIcons.General.Add);
        menuItemAdd.addActionListener(addListener);
        popupMenu.add(menuItemAdd);
        if (isSelectedItem) {
            JBMenuItem menuItemEdit = new JBMenuItem(message("simple.text.Edit"), AllIcons.Actions.Edit);
            menuItemEdit.addActionListener(editListener);
            popupMenu.add(menuItemEdit);
            popupMenu.addSeparator();
            JBMenuItem menuItemDelete = new JBMenuItem(message("simple.text.Delete"), PluginIcons.Delete);
            menuItemDelete.addActionListener(deleteListener);
            popupMenu.add(menuItemDelete);
        }
        return popupMenu;

    }

    private JComponent getVisiblePanelToolbar() {
        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup actionGroup = new DefaultActionGroup("ACTION_GROUP_VISIBLE_PANEL", false);
        actionGroup.addSeparator();
        actionGroup.add(initNotebookVisibleAction());
        actionGroup.add(initChapterVisibleAction());
        actionGroup.add(initNoteVisibleAction());
        actionGroup.add(initDetailVisibleAction());

        ActionToolbar actionToolbar = actionManager.createActionToolbar("ACTION_GROUP_VISIBLE_PANEL", actionGroup, true);
        return actionToolbar.getComponent();
    }

    private JComponent getNoteToolbar() {
        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup actionGroup = new DefaultActionGroup("ACTION_GROUP_NOTE", false);
        actionGroup.add(initSearchAction());
        actionGroup.add(initActionAddNote());
        //actionGroup.add(initEditAction());
        actionGroup.add(initSaveAction());
        actionGroup.add(initActionCopyContent());
        actionGroup.add(initInsertAction());
        actionGroup.add(initOpenSourceFile());
        actionGroup.addSeparator();
        actionGroup.add(initCollapseAction());
        actionGroup.add(initDescPaneVisible());
        actionGroup.addSeparator();

        ActionToolbar actionToolbar = actionManager.createActionToolbar("ACTION_GROUP_NOTE", actionGroup, true);
        JComponent component = actionToolbar.getComponent();
        component.setBorder(new BottomLineBorder());
        return component;
    }

    private DumbAwareAction initDescPaneVisible() {
        return new DumbAwareAction(message("detailPanel.action.showDesc.hide"), "", PluginIcons.Hide) {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                descScrollPane.setVisible(!descScrollPane.isVisible());
                e.getPresentation().setIcon(descScrollPane.isVisible() ? PluginIcons.Hide : PluginIcons.Show);
                e.getPresentation().setText(descScrollPane.isVisible() ? message("detailPanel.action.showDesc.hide") : message("detailPanel.action.showDesc.show"));
            }
        };
    }

    private DumbAwareAction initCollapseAction() {
        return new DumbAwareAction("Collapse or Expand", "", AllIcons.Actions.Collapseall) {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                northPanel.setVisible(!northPanel.isVisible());
                e.getPresentation().setIcon(northPanel.isVisible() ? AllIcons.Actions.Collapseall : AllIcons.Actions.Expandall);
            }
        };
    }

    private DumbAwareAction initSearchAction() {
        return new SearchRecordAction();
    }

    private DumbAwareAction initActionCopyContent() {
        return new DumbAwareAction(message("menu.item.copyContent"), "", AllIcons.Actions.Copy) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                copyContentText();
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                Note selectedNote = noteTable.getSelectedObject();
                e.getPresentation().setEnabled(selectedNote != null && !selectedNote.getContent().isEmpty());
            }
        };
    }

    private DumbAwareAction initOpenSourceFile() {
        return new DumbAwareAction(message("detailPanel.action.openSource.text"), "", AllIcons.Actions.MenuOpen) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                Note note = noteTable.getSelectedObject();
                if (note == null || note.getSource().isEmpty()) {
                    return;
                }

                String noteSource = note.getSource().trim();
                //如果匹配到网址 , 那么打开网址
                Matcher matcher = urlPattern.matcher(noteSource);
                if (matcher.matches()) {
                    BrowserUtil.browse(noteSource);
                    return;
                }

                VirtualFile vFile = LocalFileSystem.getInstance().findFileByPath(noteSource);
                if (vFile == null) {
                    vFile = JarFileSystem.getInstance().findFileByPath(noteSource);
                }

                if (vFile == null) {
                    Messages.showErrorDialog(project, message("detailPanel.openFile.notFind.msg"), message("detailPanel.openFile.notFind.title"));
                    return;
                }
                if (vFile.isDirectory()) {
                    try {
                        Desktop.getDesktop().open(new File(vFile.getPath()));
                    } catch (IOException ioException) {
                        Messages.showErrorDialog(project, message("detailPanel.openFile.ioException.msg"), message("detailPanel.openFile.ioException.title"));
                    }
                    return;
                }

                //List<FileEditor> fileEditors = FileEditorManager.getInstance(project).openFileEditor(new OpenFileDescriptor(project, vFile), true);

                FileEditor[] fileEditors = FileEditorManager.getInstance(project).openFile(
                        vFile, true, true);
                if (fileEditors.length > 0) {
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    if (editor != null) {
                        Document document = editor.getDocument();
                        int maxLen = document.getTextLength();
                        int offsetStart = note.getOffsetStart();
                        int offsetEnd = note.getOffsetEnd();
                        //内容
                        String content = note.getContent();
                        //代码里的内容
                        String text = document.getText(new TextRange(offsetStart, offsetEnd));
                        boolean contentIsEquals = Objects.equals(content, text);
                        if (!contentIsEquals) {
                            String trimContent = content.trim();
                            int index = document.getText().indexOf(trimContent);
                            if (index >= 0) {
                                int space = offsetEnd - offsetStart;
                                offsetStart = index;
                                offsetEnd = index + (space > 0 ? trimContent.length() : -trimContent.length());
                                contentIsEquals = true;
                            }
                        }

                        //如果内容完全相同, 那么才选中
                        if (contentIsEquals && offsetStart < maxLen && offsetEnd < maxLen) {
                            //移动插入符的位置
                            editor.getCaretModel().moveToOffset(offsetStart, true);
                            //选中代码
                            editor.getSelectionModel().setSelection(offsetStart, offsetEnd);
                            //滚动到插入符的位置
                            editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
                        }
                    }
                } else {
                    Messages.showErrorDialog(project, message("detailPanel.openFile.typeError.msg"), message("detailPanel.openFile.typeError.title"));
                }
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                Note selectedNote = noteTable.getSelectedObject();
                e.getPresentation().setEnabled(selectedNote != null && !selectedNote.getSource().isEmpty());
            }

        };
    }

    private DumbAwareAction initInsertAction() {
        return new DumbAwareAction(message("mainPanel.action.insertIntoEditor.text"), "", PluginIcons.Insert) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                Note selectedNote = noteTable.getSelectedObject();
                Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                // 注意判断笔记是否为空, 当前选择的Editor是否可以写
                if (selectedNote == null || selectedNote.getContent().isEmpty() || selectedTextEditor == null || !selectedTextEditor.getDocument().isWritable()) {
                    return;
                }
                Document document = selectedTextEditor.getDocument();
                int offset = selectedTextEditor.getCaretModel().getPrimaryCaret().getOffset();
                ApplicationManager.getApplication().runWriteAction(() -> WriteCommandAction.runWriteCommandAction(project,
                        () -> document.insertString(offset, selectedNote.getContent())));
            }

            /**
             * 如果选择了一个Note,并且打开的Editor可以编辑, 那么该组件可见可用
             */
            @Override
            public void update(@NotNull AnActionEvent e) {
                Note selectedNote = noteTable.getSelectedObject();
                Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                e.getPresentation().setEnabled(selectedNote != null && !selectedNote.getContent().isEmpty() && selectedTextEditor != null && selectedTextEditor.getDocument().isWritable());
            }
        };
    }

    private DumbAwareAction initActionAddNote() {
        return new DumbAwareAction(message("mainPanel.action.newNote.text"), "", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                getNotePanel().doAddNote();
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                e.getPresentation().setEnabled(!AppSettingsState.getInstance().readOnlyMode);
            }
        };
    }

    private DumbAwareAction initSaveAction() {
        return new DumbAwareAction(message("detailPanel.saveChanges"), "", PluginIcons.SaveRed) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                saveChanged();
            }

            private void saveChanged() {
                Note note = noteTable.getSelectedObject();
                if (note == null) {
                    return;
                }
                note.setDescription(fieldDesc.getText());
                note.setContent(fieldContent.getDocument().getText());
                ImageRecord imageRecord = imagePanel.getImageTable().getSelectedObject();
                int rowIndex = imagePanel.getImageTable().getSelectedRow();
                if (imageRecord != null && isImageInfoChanged()) {
                    imageRecord.setImageTitle(imagePanel.getImageTitleField().getText());
                    imageRecord.setImageDesc(imagePanel.getImageDescTextArea().getText());
                    //通知修改
                    imagePanel.getImageTable().getListTableModel().fireTableCellUpdated(rowIndex, 0);
                    //修改笔记的属性
                    note.setImageRecords(ImageRecordUtil.convertToString(imagePanel.getImageTable().getListTableModel().getItems()));
                }
                note.setUpdateTime(System.currentTimeMillis());
                noteService.update(note);
                //通知图形界面
                ApplicationManager
                        .getApplication()
                        .getMessageBus()
                        .syncPublisher(RecordListener.TOPIC)
                        .onNoteUpdated(project, new Note[]{note});
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                Note note = noteTable.getSelectedObject();
                if (note == null) {
                    e.getPresentation().setEnabled(false);
                    return;
                }

                boolean imageInfoChanged = isImageInfoChanged();
                String descTextNow = fieldDesc.getText();
                String contentTextNow = fieldContent.getDocument().getText();

                boolean noteDescAndCodeChanged = !Objects.equals(descTextNow, note.getDescription()) || !Objects.equals(contentTextNow, note.getContent());
                //setCenterBorder(noteDescAndCodeChanged ? CHANGED_BORDER : NORMAL_BORDER);
                boolean enabled = (noteDescAndCodeChanged || imageInfoChanged) && !AppSettingsState.getInstance().readOnlyMode;
                e.getPresentation().setEnabled(enabled);
                if (enabled) {
                    saveChanged();
                    //思考,完成了保存, 是否需要设置setEnabled(false)
                }
            }

            private boolean isImageInfoChanged() {
                ImageRecord imageRecord = imagePanel.getImageTable().getSelectedObject();
                boolean imageInfoChanged = false;
                if (imageRecord != null) {
                    String imgOldDesc = imageRecord.getImageDesc();
                    String imgNewDesc = imagePanel.getImageDescTextArea().getText();
                    String imgOldTitle = imageRecord.getImageTitle();
                    String imgNewTitle = imagePanel.getImageTitleField().getText();
                    imageInfoChanged = !imgNewDesc.equals(imgOldDesc) || !imgNewTitle.equals(imgOldTitle);
                }

                return imageInfoChanged;
            }
        };
    }

    private void setCenterBorder(Border border) {
        Border borderNow = centerPanel.getBorder();
        if (borderNow != border) {
            centerPanel.setBorder(border);
        }
    }

    //其实只要有DetailPane 就可以计算出其他一切的width, 因为通过获取比例, 一步一步的计算 ,就能计算出来
    // leftPane rightPane contentPane 三个JBSplitter

    private void setToolWindowWidth(int oldSize, int newSize) {
        ToolWindow toolWindow = getMainPanel().getToolWindow();
        ToolWindowEx twEx = (ToolWindowEx) toolWindow;
        int oldWidth = twEx.getDecorator().getWidth();
        int value = computeWidth(oldSize, newSize, oldWidth) - oldWidth;
        twEx.stretchWidth(value);
    }

    private int computeWidth(int oldSize, int newSize, int oldWidth) {
        boolean b1 = getMainPanel().getNotebookPanel().isVisible();
        boolean b2 = getMainPanel().getChapterPanel().isVisible();
        getMainPanel().getLeftPane().setProportion(0.5f);
        getMainPanel().getRightPane().setProportion(0.5f);
        getMainPanel().getContentPane().setProportion(!b1 && b2 ? 0.333333333f : 0.5f);
        return (int) (oldWidth * 1.0 / oldSize * newSize);
    }

    private int getPanelSize() {
        return (getNotebookPanel().isVisible() ? 1 : 0) + (getChapterPanel().isVisible() ? 1 : 0) + (getNotePanel().isVisible() ? 1 : 0) + 1;
    }

    private void controlViewVisible(boolean notebookPanelVisible, boolean chapterPanelVisible, boolean notePanelVisible) {
        int oldSize = getPanelSize();
        getNotebookPanel().setVisible(notebookPanelVisible);
        getChapterPanel().setVisible(chapterPanelVisible);
        getNotePanel().setVisible(notePanelVisible);
        int newSize = getPanelSize();
        setToolWindowWidth(oldSize, newSize);
        projectStorage.notebookPaneVisible = notebookPanelVisible;
        projectStorage.chapterPaneVisible = chapterPanelVisible;
        projectStorage.notePaneVisible = notePanelVisible;
    }

    private DumbAwareToggleAction initNotebookVisibleAction() {
        return new DumbAwareToggleAction(message("mainPanel.action.showNotebook.text"), "", PluginIcons.NotebookCell) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent anActionEvent) {
                return getNotebookPanel().isVisible();
            }

            @Override
            public void setSelected(@NotNull AnActionEvent anActionEvent, boolean b) {
                controlViewVisible(true, true, true);
            }
        };
    }

    private DumbAwareToggleAction initChapterVisibleAction() {
        return new DumbAwareToggleAction(message("mainPanel.action.showChapter.text"), "", PluginIcons.ChapterCell) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent anActionEvent) {
                return getChapterPanel().isVisible();
            }

            @Override
            public void setSelected(@NotNull AnActionEvent anActionEvent, boolean b) {
                controlViewVisible(false, true, true);
            }
        };
    }

    private DumbAwareToggleAction initNoteVisibleAction() {
        return new DumbAwareToggleAction(message("mainPanel.action.showNote.text"), "", PluginIcons.NoteCell) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent anActionEvent) {
                return getNotePanel().isVisible();
            }

            @Override
            public void setSelected(@NotNull AnActionEvent anActionEvent, boolean b) {
                controlViewVisible(false, false, true);
            }
        };
    }

    private DumbAwareToggleAction initDetailVisibleAction() {
        return new DumbAwareToggleAction(message("mainPanel.action.showDetail.text"), "", PluginIcons.Detail) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent anActionEvent) {
                return true;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent anActionEvent, boolean b) {
                controlViewVisible(false, false, false);
            }
        };
    }

    private ChapterPanel getChapterPanel() {
        return getMainPanel().getChapterPanel();
    }

    private NotebookPanel getNotebookPanel() {
        return getMainPanel().getNotebookPanel();
    }

    private NotePanel getNotePanel() {
        return getMainPanel().getNotePanel();
    }

    private DetailPanel getDetailPanel() {
        return getMainPanel().getDetailPanel();
    }

    private MainPanel getMainPanel() {
        return uiManager.getMainPanel();
    }

}
