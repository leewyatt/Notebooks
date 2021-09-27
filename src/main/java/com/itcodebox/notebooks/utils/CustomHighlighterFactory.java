package com.itcodebox.notebooks.utils;

import com.intellij.ide.highlighter.custom.CustomFileHighlighter;
import com.intellij.ide.highlighter.custom.SyntaxTable;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import org.jetbrains.annotations.NotNull;

/**
 * @author LeeWyatt
 */
public class CustomHighlighterFactory {
    public SyntaxHighlighter createPythonHighlighter() {
        String[] keywords = {"False", "None", "True"
                , "and", "as", "assert", "break", "class", "continue"
                , "def", "del", "elif", "else", "except", "finally"
                , "for", "from", "global", "if", "import", "in"
                , "is", "lambda", "nonlocal", "not", "or", "pass",
                "raise", "return", "try", "while", "with", "yield"
        };
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("#");
        //3个单引号或者双引号
        table.setStartComment("\"\"\"");
        table.setEndComment("\"\"\"");
        table.setHexPrefix("0x");
        //table.setNumPostfixChars("dDlL")
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(false);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createGoHighlighter() {
        String[] keywords = {"break", "default", "func", "interface"
                , "select", "case", "defer", "go", "map", "struct"
                , "chan", "else", "goto", "package", "switch", "const"
                , "fallthrough", "if", "range", "type", "continue"
                , "for", "import", "return", "var"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("//");
        table.setStartComment("/*");
        table.setEndComment("*/");
        table.setHexPrefix("0x");
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createJSHighlighter() {
        String[] keywords = {"abstract ", "arguments ", "boolean ", "break ",
                "byte ", "case ", "catch ", "char ", "class ", "const ",
                "continue ", "debugger ", "default ", "delete ", "do ",
                "double ", "else ", "enum ", "eval ", "export ", "extends ",
                "false ", "final ", "finally ", "float ", "for ", "function ",
                "goto ", "if ", "implements ", "import ", "in ", "instanceof ",
                "int ", "interface ", "let ", "long ", "native ", "new ", "null ",
                "package ", "private ", "protected ", "public ", "return ", "short ",
                "static ", "super ", "switch ", "synchronized ", "this ", "throw ",
                "throws ", "transient ", "true ", "try ", "typeof ", "var ", "void ",
                "volatile ", "while ", "with ", "yield"};

        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("//");
        table.setStartComment("/*");
        table.setEndComment("*/");
        table.setHexPrefix("0x");
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createJavaHighlighter() {
        String[] keywords = {"abstract", "assert", "boolean", "break",
                "byte", "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else", "enum",
                "extends", "final", "finally", "float", "for", "goto",
                "if", "implements", "import", "instanceof", "int", "interface",
                "long", "native", "new", "package", "private", "protected", "public",
                "return", "strictfp", "short", "static", "super", "switch", "synchronized",
                "this", "throw", "throws", "transient", "try", "void", "volatile", "while"};

        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("//");
        table.setStartComment("/*");
        table.setEndComment("*/");
        table.setHexPrefix("0x");
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createKotlinHighlighter() {
        String[] keywords = {"as", "as?", "break", "class", "continue", "do", "else",
                "false", "for", "fun", "if", "in", "!in", "interface", "is", "!is", "null",
                "object", "package", "return", "super", "this", "throw", "true", "try",
                "typealias", "typeof", "val", "var", "when", "while", "by", "catch",
                "constructor", "delegate", "dynamic", "field", "file", "finally", "get",
                "import", "init", "param", "property", "receiver", "set", "setparam", "value",
                "where", "actual", "abstract", "annotation", "companion", "const", "crossinline",
                "data", "enum", "expect", "external", "final", "infix", "inline", "inner", "internal",
                "lateinit", "noinline", "open", "operator", "out", "override", "private", "protected",
                "public", "reified", "sealed", "suspend", "tailrec", "vararg", "it"};

        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("//");
        table.setStartComment("/*");
        table.setEndComment("*/");
        table.setHexPrefix("0x");
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createMySQLHighlighter() {
        return createSQLHighlighter(true);
    }

    public SyntaxHighlighter createSQLHighlighter() {
        return createSQLHighlighter(false);
    }

    private SyntaxHighlighter createSQLHighlighter(boolean isMysql) {
        String[] keywords = {
                "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE",
                "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE",
                "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONNECTION", "CONSTRAINT",
                "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
                "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE",
                "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE",
                "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF",
                "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR",
                "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GOTO", "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY",
                "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER",
                "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL",
                "INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LABEL", "LEADING", "LEAVE", "LEFT", "LIKE",
                "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT",
                "LOOP", "LOW_PRIORITY", "MATCH", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND",
                "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON",
                "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PRECISION", "PRIMARY",
                "PROCEDURE", "PURGE", "RAID0", "RANGE", "READ", "READS", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME",
                "REPEAT", "REPLACE", "REQUIRE", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS",
                "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SMALLINT", "SPATIAL", "SPECIFIC",
                "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS",
                "SQL_SMALL_RESULT", "SSL", "STARTING", "STRAIGHT_JOIN", "TABLE", "TERMINATED", "THEN", "TINYBLOB",
                "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK",
                "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES",
                "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "WHEN", "WHERE", "WHILE", "WITH", "WRITE",
                "X509", "XOR", "YEAR_MONTH", "ZEROFILL"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
            table.addKeyword1(kw.toLowerCase());
        }
        if (isMysql) {
            table.setLineComment("#");
        } else {
            table.setLineComment("--");
        }
        table.setStartComment("/*");
        table.setEndComment("*/");
        //大括号
        table.setHasBraces(false);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        //SQL不区分大小写, 但是加了下面的语句 ,就不能正确识别 ,所以关键字列表里添加大小写关键字
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createSQLiteHighlighter() {
        String[] keywords = {
                "ABORT", "CREATE", "FROM", "NATURAL", "ACTION", "CROSS",
                "FULL", "NO", "ADD", "CURRENT_DATE", "GLOB", "NOT", "AFTER", "CURRENT_TIME",
                "GROUP", "NOTNULL", "ALL", "CURRENT_TIMESTAMP", "HAVING", "NULL", "ALTER",
                "DATABASE", "IF", "OF", "ANALYZE", "DEFAULT", "IGNORE", "OFFSET", "AND",
                "DEFERRABLE", "IMMEDIATE", "ON", "AS", "DEFERRED", "IN", "OR", "ASC",
                "DELETE", "INDEX", "ORDER", "ATTACH", "DESC", "INDEXED", "OUTER",
                "AUTOINCREMENT", "DETACH", "INITIALLY", "PLAN", "BEFORE", "DISTINCT",
                "INNER", "PRAGMA", "BEGIN", "DROP", "INSERT", "PRIMARY", "BETWEEN", "EACH",
                "INSTEAD", "QUERY", "BY", "ELSE", "INTERSECT", "RAISE", "CASCADE", "END",
                "INTO", "RECURSIVE", "CASE", "ESCAPE", "IS", "REFERENCES", "CAST", "EXCEPT",
                "ISNULL", "REGEXP", "CHECK", "EXCLUSIVE", "JOIN", "REINDEX", "COLLATE",
                "EXISTS", "KEY", "RELEASE", "COLUMN", "EXPLAIN", "LEFT", "RENAME", "COMMIT",
                "FAIL", "LIKE", "REPLACE", "CONFLICT", "FOR", "LIMIT", "RESTRICT",
                "CONSTRAINT", "FOREIGN", "MATCH", "RIGHT"};
        return createSQLLighter(keywords);
    }

    public SyntaxHighlighter createMssqlHighlighter() {
        String[] keywords = {
                "ADD", "EXTERNAL", "PROCEDURE", "ALL", "FETCH", "PUBLIC", "ALTER", "FILE", "RAISERROR", "AND", "FILLFACTOR", "READ", "ANY", "FOR", "READTEXT", "AS", "FOREIGN", "RECONFIGURE", "ASC", "FREETEXT", "REFERENCES", "AUTHORIZATION", "FREETEXTTABLE", "REPLICATION", "BACKUP", "FROM", "RESTORE", "BEGIN", "FULL", "RESTRICT", "BETWEEN", "FUNCTION", "RETURN", "BREAK", "GOTO", "REVERT", "BROWSE", "GRANT", "REVOKE", "BULK", "GROUP", "RIGHT", "BY", "HAVING", "ROLLBACK", "CASCADE", "HOLDLOCK", "ROWCOUNT", "CASE", "IDENTITY", "ROWGUIDCOL", "CHECK", "IDENTITY_INSERT", "RULE", "CHECKPOINT", "IDENTITYCOL", "SAVE", "CLOSE", "IF", "SCHEMA", "CLUSTERED", "IN", "SECURITYAUDIT", "COALESCE", "INDEX", "SELECT", "COLLATE", "INNER", "SEMANTICKEYPHRASETABLE", "COLUMN", "INSERT", "SEMANTICSIMILARITYDETAILSTABLE", "COMMIT", "INTERSECT", "SEMANTICSIMILARITYTABLE", "COMPUTE", "INTO", "SESSION_USER", "CONSTRAINT", "IS", "SET", "CONTAINS", "JOIN", "SETUSER", "CONTAINSTABLE", "KEY", "SHUTDOWN", "CONTINUE", "KILL", "SOME", "CONVERT", "LEFT", "STATISTICS", "CREATE", "LIKE", "SYSTEM_USER", "CROSS", "LINENO", "TABLE", "CURRENT", "LOAD", "TABLESAMPLE", "CURRENT_DATE", "MERGE", "TEXTSIZE", "CURRENT_TIME", "NATIONAL", "THEN", "CURRENT_TIMESTAMP", "NOCHECK", "TO", "CURRENT_USER", "NONCLUSTERED", "TOP", "CURSOR", "NOT", "TRAN", "DATABASE", "NULL", "TRANSACTION", "DBCC", "NULLIF", "TRIGGER", "DEALLOCATE", "OF", "TRUNCATE", "DECLARE", "OFF", "TRY_CONVERT", "DEFAULT", "OFFSETS", "TSEQUAL", "DELETE", "ON", "UNION", "DENY", "OPEN", "UNIQUE", "DESC", "OPENDATASOURCE", "UNPIVOT", "DISK", "OPENQUERY", "UPDATE", "DISTINCT", "OPENROWSET", "UPDATETEXT", "DISTRIBUTED", "OPENXML", "USE", "DOUBLE", "OPTION", "USER", "DROP", "OR", "VALUES", "DUMP", "ORDER", "VARYING", "ELSE", "OUTER", "VIEW", "END", "OVER", "WAITFOR", "ERRLVL", "PERCENT", "WHEN", "ESCAPE", "PIVOT", "WHERE", "EXCEPT", "PLAN", "WHILE", "EXEC", "PRECISION", "WITH", "EXECUTE", "PRIMARY", "WITHINGROUP", "EXISTS", "PRINT", "WRITETEXT", "EXIT", "PROC"};
        return createSQLLighter(keywords);
    }

    @NotNull
    private SyntaxHighlighter createSQLLighter(String[] keywords) {
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
            table.addKeyword1(kw.toLowerCase());
        }
        table.setLineComment("--");
        table.setStartComment("/*");
        table.setEndComment("*/");
        //大括号
        table.setHasBraces(false);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        //SQL不区分大小写
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createDartHighlighter() {
        String[] keywords = {"abstract", "dynamic", "implements", "show", "as", "else", "import", "static", "assert", "enum", "in", "super", "async", "export", "in", "super", "await", "extends", "is", "sync", "break", "external", "library", "this", "case", "factory", "mixin", "throw", "catch", "false", "new", "true", "class", "final", "null", "try", "const", "finally", "on", "typedef", "continue", "for", "operator", "var", "covariant", "Function", "part", "void", "default", "get", "rethrow", "while", "deferred", "hide", "return", "with", "do", "if", "set", "yield"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("//");
        table.setStartComment("/*");
        table.setEndComment("*/");
        table.setHexPrefix("0x");
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createRHighlighter() {
        String[] keywords = {"if", "else", "repeat", "while", "function", "for", "next", "break", "TRUE", "FALSE", "NULL", "Inf", "NaN", "NA", "NA_integer_", "NA_real_", "NA_complex_", "NA_character_"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("#");
        table.setHexPrefix("0x");
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createPhpHighlighter() {
        String[] keywords = {"__halt_compiler", "abstract", "and", "array", "as", "break", "callable", "case", "catch", "class", "clone", "const", "continue", "declare", "default", "die", "do", "echo", "else", "elseif", "empty", "enddeclare", "endfor", "endforeach", "endif", "endswitch", "endwhile", "eval", "exit", "extends", "final", "finally", "fn", "for", "foreach", "function", "global", "goto", "if", "implements", "include", "include_once", "instanceof", "insteadof", "interface", "isset", "list", "match", "namespace", "new", "or", "print", "private", "protected", "public", "require", "require_once", "return", "static", "switch", "throw", "trait", "try", "unset", "use", "var", "while", "xor", "yield", "yield from", "__CLASS__", "__DIR__", "__FILE__", "__FUNCTION__", "__LINE__", "__METHOD__", "__NAMESPACE__", "__TRAIT__"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("//");
        table.setStartComment("/*");
        table.setEndComment("*/");
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createGroovyHighlighter() {
        String[] keywords = {"as", "assert", "break", "case", "catch", "class", "const", "continue", "def", "default", "do", "else", "enum", "extends", "false", "finally", "for", "goto", "if", "implements", "import", "in", "instanceof", "interface", "new", "null", "package", "return", "super", "switch", "this", "throw", "throws", "trait", "true", "try", "while"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("//");
        table.setStartComment("/*");
        table.setEndComment("*/");
        table.setHexPrefix("0x");
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createScalaHighlighter() {
        String[] keywords = {"abstract", "case", "catch", "class", "def", "do", "else", "extends", "false", "final", "finally", "for", "forSome", "if", "implicit", "import", "lazy", "match", "new", "null", "object", "override", "package", "private", "protected", "return", "sealed", "super", "this", "throw", "trait", "try", "true", "type", "val", "var", "while", "with", "yield"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("//");
        table.setStartComment("/*");
        table.setEndComment("*/");
        table.setHexPrefix("0x");
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createCHighlighter() {
        String[] keywords = {"auto", "break", "case", "char", "const", "continue", "default", "do", "double", "else", "enum", "extern", "float", "for", "goto", "if", "int", "long", "register", "return", "short", "signed", "sizeof", "static", "struct", "switch", "typedef", "union", "unsigned", "void", "volatile", "while", "inline", "restrict", "_Bool", "_Complex", "_Imaginary", "_Alignas", "_Alignof", "_Atomic", "_Static_assert", "_Noreturn", "_Thread_local", "_Generic"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("//");
        table.setStartComment("/*");
        table.setEndComment("*/");
        table.setHexPrefix("0x");
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    /**
     * cpp c++
     */
    public SyntaxHighlighter createCPlusHighlighter() {
        String[] keywords = {"asm", "do", "if", "return", "typedef", "auto", "double", "inline", "short", "typeid", "bool", "dynamic_cast", "int", "signed", "typename", "break", "else", "long", "sizeof", "union", "case", "enum", "mutable", "static", "unsigned", "catch", "explicit", "namespace", "static_cast", "using", "char", "export", "new", "struct", "virtual", "class", "extern", "operator", "switch", "void", "const", "false", "private", "template", "volatile", "const_cast", "float", "protected", "this", "wchar_t", "continue", "for", "public", "throw", "while", "default", "friend", "register", "true", "delete", "goto", "reinterpret_cast", "try", "alignas", "alignof", "char16_t", "char32_t", "constexpr", "decltype", "noexcept", "nullptr", "static_assert", "thread_local"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("//");
        table.setStartComment("/*");
        table.setEndComment("*/");
        table.setHexPrefix("0x");
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    /**
     * 非托管 这个关键字的英语没有找到
     */
    public SyntaxHighlighter createCSharpHighlighter() {
        String[] keywords = {"abstract", "as", "base", "bool", "break", "byte", "case", "catch", "char", "checked", "class", "const", "continue", "decimal", "default", "delegate", "do", "double", "else", "enum", "event", "explicit", "extern", "false", "finally", "fixed", "float", "for", "foreach", "goto", "if", "implicit", "in", "int", "interface", "internal", "is", "lock", "long", "namespace", "new", "null", "object", "operator", "out", "override", "params", "private", "protected", "public", "readonly", "ref", "return", "sbyte", "sealed", "short", "sizeof", "stackalloc", "static", "string", "struct", "switch", "this", "throw", "true", "try", "typeof", "uint", "ulong", "unchecked", "unsafe", "ushort", "using", "virtual", "void", "volatile", "while", "add", "and", "alias", "ascending", "async", "await", "by", "descending", "dynamic", "equals", "from", "get", "global", "group", "init", "into", "join", "let", "delegate", "nameof", "nint", "not", "notnull", "nuint", "on", "or", "orderby", "partial", "partial", "record", "remove", "select", "set", "unmanaged", "value", "var", "when", "where", "where", "with", "yield"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("//");
        table.setStartComment("/*");
        table.setEndComment("*/");
        table.setHexPrefix("0x");
        table.setHasStringEscapes(true);
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createRubyHighlighter() {
        String[] keywords = {"__ENCODING__", "__LINE__", "__FILE__", "BEGIN", "END", "alias", "and", "begin", "break", "case", "class", "def", "defined?", "do", "else", "elsif", "end", "ensure", "false", "for", "if", "in", "module", "next", "nil", "not", "or", "redo", "rescue", "retry", "return", "self", "super", "then", "true", "undef", "unless", "until", "when", "while", "yield"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("#");
        table.setStartComment("=begin");
        table.setEndComment("=end");
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createRustHighlighter() {
        String[] keywords = {"as", "break", "const", "continue", "crate", "else", "enum", "extern", "false", "fn", "for", "if", "impl", "in", "let", "loop", "match", "mod", "move", "mut", "pub", "ref", "return", "self", "Self", "static", "struct", "super", "trait", "true", "type", "unsafe", "use", "where", "while", "async", "await", "dyn", "abstract", "become", "box", "do", "final", "macro", "override", "priv", "typeof", "unsized", "virtual", "yield", "try", "union"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("//");
        table.setStartComment("/*");
        table.setEndComment("*/");
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createSwiftHighlighter() {
        String[] keywords = {"class", "deinit", "enum", "extension", "func", "import", "init", "internal", "let", "operator", "private", "protocol", "public", "static", "struct", "subscript", "typealias", "var", "break", "case", "continue", "default", "do", "else", "fallthrough", "for", "if", "in", "return", "switch", "where", "while", "as", "dynamicType", "false", "is", "nil", "self", "self", "super", "true", "_column_", "_file_", "_function_", "_line_", "associativity", "convenience", "dynamic", "didSet", "final", "get", "infix", "inout", "lazy", "left", "mutating", "none", "nonmutating", "optional", "override", "postfix", "precedence", "prefix", "protocol", "required", "right", "set", "type", "unowned", "weak", "willSet"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("///");
        table.setStartComment("/*");
        table.setEndComment("*/");
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createHaskellHighlighter() {
        String[] keywords = {"as", "case", "of", "class", "data", "default", "deriving", "do", "forall", "foreign", "hiding", "if", "then", "else", "import", "infix", "infixl", "infixr", "instance", "let", "in", "mdo", "module", "newtype", "proc", "qualified", "rec", "type", "type", "where"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("--");
        table.setStartComment("{-");
        table.setEndComment("-}");
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createObjectiveCHighlighter() {
        String[] keywords = {"auto", "else", "long", "switch", "break", "enum", "register", "typedef", "case", "extern", "return", "union", "char", "float", "short", "unsigned", "const", "for", "signed", "void", "continue", "goto", "sizeof", "volatile", "default", "if", "static", "while", "do", "int", "struct", "_Packed", "double", "protocol", "interface", "implementation", "NSObject", "NSInteger", "NSNumber", "CGFloat", "property", "nonatomic;", "retain", "strong", "weak", "unsafe_unretained;", "readwrite", "readonly"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("///");
        table.setStartComment("/**");
        table.setEndComment("*/");
        //大括号
        table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createVBNetHighlighter() {
        String[] keywords = {"AddHandler", "AddressOf", "Alias", "And", "AndAlso", "As", "Boolean", "ByRef", "Byte", "ByVal", "Call", "Case", "Catch", "CBool", "CByte", "CChar", "CDate", "CDbl", "CDec", "Char", "CInt", "Class", "Statement", "", "CLng", "CObj", "Const", "Continue", "CSByte", "CShort", "CSng", "CStr", "CType", "CUInt", "CULng", "CUShort", "Date", "Decimal", "Declare", "Default", "Delegate", "Dim", "DirectCast", "Do", "Double", "Each", "Else", "ElseIf", "End Statement", "End", "EndIf", "Enum", "Erase", "Error", "Event", "Exit", "False", "Finally", "For", "Each", "Friend", "Function", "Get", "GetType", "GetXMLNamespace", "Global", "GoSub", "GoTo", "Handles", "If", "Implements", "Imports", "In", "Inherits", "Integer", "Interface", "Is", "IsNot", "Let", "Lib", "Like", "Long", "Loop", "Me", "Mod", "Module", "MustInherit", "MustOverride", "MyBase", "MyClass", "NameOf", "Namespace", "Narrowing", "New", "Next", "Next", "Not", "Nothing", "NotInheritable", "NotOverridable", "Object", "Of", "On", "Operator", "Option", "Optional", "Or", "OrElse", "Out", "Overloads", "Overridable", "Overrides", "ParamArray", "Partial", "Private", "Property", "Protected", "Public", "RaiseEvent", "ReadOnly", "ReDim", "REM", "RemoveHandler", "Resume", "Return", "SByte", "Select", "Set", "Shadows", "Shared", "Short", "Single", "Static", "Step", "Stop", "String", "Structure", "Sub", "SyncLock", "Then", "Throw", "To", "True", "Try", "TryCast", "TypeOf", "UInteger", "ULong", "UShort", "Using", "Variant", "Wend", "When", "While", "Widening", "With", "WithEvents", "WriteOnly", "Xor", "#Const", "#Else", "#ElseIf", "#End", "#If"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("'");
        table.setHexPrefix("&H");
        //大括号
        //table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

    public SyntaxHighlighter createVBScriptHighlighter() {
        String[] keywords = {"And", "As", "Boolean", "ByRef", "Byte", "ByVal", "Call", "Case", "Class", "Const", "", "Currency", "Debug", "Dim", "Do", "Double", "Each", "Else", "ElseIf", "Empty", "End", "", "EndIf", "Enum", "Eqv", "Event", "Exit", "False", "For", "Function", "Get", "GoTo", "", "If", "Imp", "Implements", "In", "Integer", "Is", "Let", "Like", "Long", "Loop", "", "LSet", "Me", "Mod", "New", "Next", "Not", "Nothing", "Null", "On", "Option", "", "Optional", "Or", "ParamArray", "Preserve", "Private", "Public", "RaiseEvent", "ReDim", "Rem", "Resume", "", "RSet", "Select", "Set", "Shared", "Single", "Static", "Stop", "Sub", "Then", "To", "", "True", "Type", "TypeOf", "Until", "Variant", "Wend", "While", "With", "Xor"};
        SyntaxTable table = new SyntaxTable();
        for (String kw : keywords) {
            table.addKeyword1(kw);
        }
        table.setLineComment("'");
        //大括号
        //table.setHasBraces(true);
        //中括号
        table.setHasBrackets(true);
        //小括号
        table.setHasParens(true);
        return new CustomFileHighlighter(table);
    }

}