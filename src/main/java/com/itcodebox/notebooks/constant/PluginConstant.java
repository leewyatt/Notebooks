package com.itcodebox.notebooks.constant;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author LeeWyatt
 */
public interface PluginConstant {

    String TOOLWINDOW_ID = "Notebook"; 
    
    /**
     * 图片格式
     */
    String JPG = "jpg";
    String JPEG = "jpeg";
    String PNG = "png";
    String GIF = "gif";
    /**
     * 图片文件最大空间
     */
    long MAX_LENGTH = 1024 * 1024 * 10;
    String ThumbExtension = "_mini";
    int ThumbSizeMini = 100;
    int ThumbSizeMax = 2000;
    ArrayList<String> IMG_EXTENSION_LIST = new ArrayList<>(Arrays.asList(JPG, JPEG, PNG, GIF));
    /**
     * 缓存是否正在清理
     */
    AtomicBoolean IsClearing = new AtomicBoolean(false);

    Border FOCUS_LOST_BORDER = BorderFactory.createEtchedBorder();
    Border FOCUS_GAINED_BORDER = BorderFactory.createRaisedBevelBorder();

    String NOTIFICATION_ID_IMPORT_EXPORT = "Notebook Import/Export";
    String NOTIFICATION_CLEAR_CACHE = "Notebook Clear Cache";
    String DEFAULT_NOTIFICATION_GROUP_ID = "Notebook Plugin";


    /**
     * User directory
     *  e.g. Windows OS :
     *      USER_HOME_PATH ==> C:\Users\Administrator(Your User Name)
     */
    String USER_HOME_PATH = System.getProperty("user.home");
    /**
     * Data directory of this plugin:
     * USER_HOME_PATH/.ideaNotebooksFile
     */
    Path PROJECT_DB_DIRECTORY_PATH = Paths.get(USER_HOME_PATH, ".ideaNotebooksFile");
    /**
     * The image data of this plugin is stored here:
     * USER_HOME_PATH/.ideaNotebooksFile/notebook_images
     */
    Path IMAGE_DIRECTORY_PATH = PROJECT_DB_DIRECTORY_PATH.resolve("notebook_images");
    /**
     * Temporary image data of this plugin, stored here:
     * USER_HOME_PATH/.ideaNotebooksFile/notebook_images_temp
     */
    Path TEMP_IMAGE_DIRECTORY_PATH = PROJECT_DB_DIRECTORY_PATH.resolve("notebook_images_temp");
    /**
     * SQLite database file for this plugin:
     * USER_HOME_PATH/.ideaNotebooksFile/notebooks.db
     */
    Path DB_FILE_PATH = PROJECT_DB_DIRECTORY_PATH.resolve("notebooks.db");


    Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();
    int MIN_FONT_SIZE = 8;
    int MAX_FONT_SIZE = 72;

    String QQ_GROUP = "715598051";
    String URL_GITHUB = "https://github.com/leewyatt";
    String EMAIL_GMAIL = "leewyatt7788@gmail.com";
    String EMAIL_163 = "leewyatt@163.com";

    /**
     * Typora Ver 0.9.98 支持的列表如下
     * plain text 是自行添加的,表示普通文本
     */
    List<String> EXTENSION_LIST = Arrays.asList(
            "ABAP", "apl", "asciiarmor", "ASN.1", "asp", "assembly",
            "bash", "basic",
            "c", "c#", "c++", "cassandra", "ceylon", "clike", "clojure", "cmake", "cobol", "coffeescript", "commonlisp", "cpp", "CQL", "crystal", "csharp", "css", "cypher", "cython",
            "D", "dart", "diff", "django", "dockerfile", "dtd", "dylan",
            "ejs", "elixir", "elm", "embeddedjs", "erb", "erlang",
            "F#", "flow", "forth", "fortran", "fsharp",
            "gas", "gfm", "gherkin", "glsl", "go", "groovy",
            "handlebars", "haskell", "haxe", "html", "http", "hxml",
            "idl", "ini",
            "jade", "java", "javascript", "jinja2", "js", "json", "jsp", "jsx", "julia",
            "kotlin",
            "latex", "less", "lisp", "livescript", "lua",
            "makefile", "mariadb", "markdown", "mathematica", "matlab", "mbox", "mermaid", "mssql", "mysql",
            "nginx", "nsis",
            "objc", "objective-c", "ocaml", "octave", "oz",
            "pascal", "perl", "perl6", "pgp", "php", "php+HTML", "plsql", "powershell", "properties", "protobuf", "pseudocode", "python", "plain text",
            "R", "react", "reStructuredText", "rst", "ruby", "rust",
            "SAS", "scala", "scheme", "scss", "sequence", "sh", "shell", "smalltalk", "SPARQL", "spreadsheet", "sql", "sqlite", "squirrel", "stylus", "swift",
            "tcl", "tex", "tiddlywiki", "tiki wiki", "toml", "tsx", "twig", "typescript",
            "v", "vb", "vbscript", "velocity", "verilog", "vhdl", "visual basic", "vue",
            "web-idl", "wiki",
            "xaml", "xml", "xml-dtd", "xquery",
            "yacas", "yaml");

}
