package com.itcodebox.notebooks.ui.component;

import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import com.itcodebox.notebooks.utils.CustomHighlighterFactory;

import java.util.HashMap;

/**
 * @author LeeWyatt
 */
public class CodeEditorUtil {
    private static final HashMap<String, SyntaxHighlighter> HIGHLIGHTER_MAP = new HashMap<>();
    private static final IElementType TEXT_ELEMENT_TYPE = new IElementType("TEXT", Language.ANY);
    private static final PlainSyntaxHighlighter PLAIN_SYNTAX_HIGHLIGHTER = new PlainSyntaxHighlighter();

    static {

        //try {
        //    HIGHLIGHTER_MAP.put("java", new com.intellij.ide.highlighter.JavaFileHighlighter());
        //} catch (Error e) {
        //    e.printStackTrace();
        //}
        //try {
        //    HIGHLIGHTER_MAP.put("kotlin", new org.jetbrains.kotlin.idea.highlighter.KotlinHighlighter());
        //} catch (Error e) {
        //    e.printStackTrace();
        //}
        try {
            HIGHLIGHTER_MAP.put("html", new com.intellij.ide.highlighter.HtmlFileHighlighter());
        } catch (Error e) {
            e.printStackTrace();
        }
        try {
            HIGHLIGHTER_MAP.put("xml", new com.intellij.ide.highlighter.XmlFileHighlighter());
        } catch (Error e) {
            e.printStackTrace();
        }
        try {
            HIGHLIGHTER_MAP.put("json", new com.intellij.json.highlighting.JsonSyntaxHighlighterFactory().getSyntaxHighlighter(null, null));
        } catch (Error e) {
            e.printStackTrace();
        }
        CustomHighlighterFactory factory = new CustomHighlighterFactory();

        HIGHLIGHTER_MAP.put("python", factory.createPythonHighlighter());
        HIGHLIGHTER_MAP.put("go", factory.createGoHighlighter());
        HIGHLIGHTER_MAP.put("java", factory.createJavaHighlighter());
        HIGHLIGHTER_MAP.put("kotlin", factory.createKotlinHighlighter());
        HIGHLIGHTER_MAP.put("c", factory.createCHighlighter());
        HIGHLIGHTER_MAP.put("ruby", factory.createRubyHighlighter());
        HIGHLIGHTER_MAP.put("rust", factory.createRustHighlighter());
        HIGHLIGHTER_MAP.put("mysql", factory.createMySQLHighlighter());
        HIGHLIGHTER_MAP.put("mssql", factory.createMssqlHighlighter());
        HIGHLIGHTER_MAP.put("sqlite", factory.createSQLiteHighlighter());
        HIGHLIGHTER_MAP.put("haskell", factory.createHaskellHighlighter());
        HIGHLIGHTER_MAP.put("groovy", factory.createGroovyHighlighter());
        HIGHLIGHTER_MAP.put("dart", factory.createDartHighlighter());
        HIGHLIGHTER_MAP.put("swift", factory.createSwiftHighlighter());
        HIGHLIGHTER_MAP.put("vbscript", factory.createVBScriptHighlighter());
        HIGHLIGHTER_MAP.put("php", factory.createPhpHighlighter());
        HIGHLIGHTER_MAP.put("r", factory.createRHighlighter());
        HIGHLIGHTER_MAP.put("scala", factory.createScalaHighlighter());

        SyntaxHighlighter sqlHighlighter = factory.createSQLHighlighter();
        HIGHLIGHTER_MAP.put("sql", sqlHighlighter);
        HIGHLIGHTER_MAP.put("plsql", sqlHighlighter);

        SyntaxHighlighter jsHighlighter = factory.createJSHighlighter();
        HIGHLIGHTER_MAP.put("javascript", jsHighlighter);
        HIGHLIGHTER_MAP.put("js", jsHighlighter);

        SyntaxHighlighter cppHighlighter = factory.createCPlusHighlighter();
        HIGHLIGHTER_MAP.put("c++", cppHighlighter);
        HIGHLIGHTER_MAP.put("cpp", cppHighlighter);

        SyntaxHighlighter objcHighlighter = factory.createObjectiveCHighlighter();
        HIGHLIGHTER_MAP.put("objc", objcHighlighter);
        HIGHLIGHTER_MAP.put("objective-c", objcHighlighter);

        SyntaxHighlighter cSharpHighlighter = factory.createCSharpHighlighter();
        HIGHLIGHTER_MAP.put("c#", cSharpHighlighter);
        HIGHLIGHTER_MAP.put("csharp", cSharpHighlighter);

        SyntaxHighlighter vbNetHighlighter = factory.createVBNetHighlighter();
        HIGHLIGHTER_MAP.put("vb", vbNetHighlighter);
        HIGHLIGHTER_MAP.put("visual basic", vbNetHighlighter);

    }

    private CodeEditorUtil() {
    }

    public static Editor createCodeEditor(Project project) {
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document doc = editorFactory.createDocument("");
        Editor editor = editorFactory.createEditor(doc, project);
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setLineMarkerAreaShown(true);
        editorSettings.setIndentGuidesShown(true);
        editorSettings.setFoldingOutlineShown(true);
        editorSettings.setAdditionalColumnsCount(3);
        editorSettings.setAdditionalLinesCount(3);
        editorSettings.setLineNumbersShown(true);
        editorSettings.setCaretRowShown(true);

        return editor;
    }

    public static void setEditorHighlighter(Editor editor, String type) {
        String languageType = type.trim().toLowerCase();
        SyntaxHighlighter syntaxHighlighter = HIGHLIGHTER_MAP.get(languageType);
        SyntaxHighlighter highlighter = syntaxHighlighter == null ? PLAIN_SYNTAX_HIGHLIGHTER : syntaxHighlighter;
        EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        LayeredLexerEditorHighlighter editorHighlighter = new LayeredLexerEditorHighlighter(highlighter, scheme);
        editorHighlighter.registerLayer(TEXT_ELEMENT_TYPE, new LayerDescriptor(highlighter, ""));
        ((EditorEx) editor).setHighlighter(editorHighlighter);
    }

    public static void setCode(Project project, Editor editor, String code) {
        Document document = editor.getDocument();
        int textLength = document.getTextLength();
        ApplicationManager.getApplication().runWriteAction(() -> WriteCommandAction.runWriteCommandAction(project,
                () -> document.replaceString(0, textLength, code)));
    }
}
