# Notebook（笔记）

[![Version](https://img.shields.io/jetbrains/plugin/v/com.itcodebox.leewyatt.notebooks.id?label=version)](https://plugins.jetbrains.com/plugin/16998-notebook)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.itcodebox.leewyatt.notebooks.id)](https://plugins.jetbrains.com/plugin/16998-notebook)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

IntelliJ 系 IDE 内置的**代码感知、本地优先**笔记插件。随手从编辑器收藏代码片段，用三级目录（`笔记本` > `章节` > `笔记`）组织，所有数据落在本机单个 SQLite 文件里 —— **无云端、无账号、无遥测**。

支持 IntelliJ IDEA **2023.3+**（Community & Ultimate 均可）+ Java **17+**。

<a href="https://plugins.jetbrains.com/plugin/16998-notebook"><img src="https://img.shields.io/badge/%E2%AC%87%EF%B8%8F_%E4%BB%8E-JetBrains_Marketplace_%E5%AE%89%E8%A3%85-orange?style=for-the-badge&logo=jetbrains" alt="从 JetBrains Marketplace 安装"></a>

English: [README.md](./README.md)

---

## 是什么

一款**本地优先**的 IDE 内置笔记工具。笔记按三级结构组织：

```
笔记本（Notebook） > 章节（Chapter） > 笔记（Note）
```

每条笔记可以是一段文本或代码，带描述、来源文件路径、原始选区偏移、图片附件。所有数据存在用户目录下的单个 SQLite 文件里——**无云端、无账号、无遥测**。

## 功能

- **从编辑器收藏** — 选中代码/文字 → 右键 *添加到笔记本*（或 `Ctrl+\`）。来源文件路径和选区偏移会一并保存。
- **把笔记插回编辑器** — `Ctrl+Shift+\` 或右键 *插入笔记内容*，在光标位置粘贴已保存的笔记。
- **三级目录组织** — 表格内行可拖拽重排；章节可拖到其他笔记本下；笔记可拖到其他章节下。
- **搜索** — 编辑器 `Alt+S` 打开搜索框，支持仅标题 / 标题+正文 / 标题+描述 / 全文。
- **图片附件** — 支持 JPG/PNG/GIF，单张 ≤ 10 MB，自动生成 100×100 缩略图。
- **JSON 导入/导出** — 所有笔记本、章节、笔记、引用图片一并 round-trip 备份。
- **Markdown 导出（单个笔记本）** — 基于 Groovy 模板生成扁平的 Markdown 文件，模板可在设置中自定义。
- **Markdown 文件树导出（1.41 新增）** — 每条笔记独立写入一个 `.md` 文件，按 `笔记本/章节/笔记.md` 组织，每个笔记本单独一份 `_assets/` 图片目录；含 YAML frontmatter。**可直接拖进 Obsidian 或 Typora**。
- **升级自动备份（1.41 新增）** — 每次安装新版本首次启动，会在任何数据库迁移之前把 SQLite 文件备份到 `~/.ideaNotebooksFile/backups/`，保留最近 5 份。
- **双语 UI** — 英文 & 简体中文。

## 安装

### 从 JetBrains Marketplace

1. `File → Settings → Plugins → Marketplace`
2. 搜索 `Notebook`
3. 安装 → 重启 IDE

### 从本地 ZIP

1. 从 [JetBrains Marketplace](https://plugins.jetbrains.com/) 或本仓库 Releases 下载 ZIP
2. `File → Settings → Plugins → ⚙️ → Install Plugin from Disk…`

## 数据存放位置

| 路径 | 内容 |
|---|---|
| `~/.ideaNotebooksFile/notebooks.db` | SQLite 数据库：所有笔记本、章节、笔记 |
| `~/.ideaNotebooksFile/notebook_images/` | 图片附件和缩略图 |
| `~/.ideaNotebooksFile/backups/` | 升级时自动创建的备份 |

数据库是**机器级共享**的——所有 IDE 项目看到同一份笔记。只有 UI 状态（上次选中项、折叠面板状态）按项目独立保存。

## 快捷键

| 快捷键 | 操作 |
|---|---|
| `Ctrl + \` | 添加选中内容到笔记本 |
| `Ctrl + Shift + \` | 插入笔记内容到编辑器 |
| `Alt + S` | 搜索笔记 |
| `Alt + O` | 激活笔记本工具窗口 |
| `Shift + Alt + S` | 打开笔记搜索栏 |

## 设置

`File → Settings → Tools → 笔记本`

- 代码区字体和字号
- 缩略图最大尺寸
- 启动时是否恢复上次选中项
- 自定义 Markdown 导出模板（Groovy）
- 无选中文字时是否显示"添加到笔记本"菜单项

## 导出你的数据

如果某天你想离开这个插件，或者只是想有一份便携备份，用笔记本工具窗口的齿轮菜单（⚙️）：

- **Export JSON** — 完整数据库 + 图片，可以再次 round-trip 导入。
- **Export as Markdown Tree** — 每条笔记一个 `.md`，结构是 `<笔记本>/<章节>/<笔记>.md`，每个笔记本有一份 `_assets/` 图片目录。**直接扔进 Obsidian 就能用**。

数据始终是你的，不会被锁定。

## 从源码构建

需要 JDK 21（推荐 JBR 21，已在 `gradle.properties` 的 `org.gradle.java.home` 钉住）。

```bash
# 编译 + 打包
./gradlew build            
# 启动一个 sandbox IDE，已装本插件
./gradlew runIde 
 # 跑 JetBrains Plugin Verifier 多版本兼容检查          
./gradlew verifyPlugin    
```

构建产物在 `build/distributions/`。

## 工程结构（速查）

| 包 | 职责 |
|---|---|
| `ui/` | Swing 面板、表格、对话框、设置界面 |
| `action/` | 编辑器动作（`Ctrl+\` 等） |
| `service/` / `service/impl/` | 业务服务（依赖 DB） |
| `dao/` / `dao/impl/` | 基于 Apache DBUtils 的 SQL 访问 |
| `entity/` | 数据模型：`Notebook`、`Chapter`、`Note` 等 |
| `utils/` | 导入导出、文件工具、i18n |
| `projectservice/` | 项目级 UI 状态 + 消息总线 |

更详细的架构参考文档不随发布一起发布，留在仓库的开发者目录中。

## 许可协议

本项目使用 [Apache License, Version 2.0](./LICENSE)。

```
Copyright (c) LeeWyatt

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
```

## 反馈与支持

- **Issue / 需求**: [GitHub Issues](https://github.com/leewyatt/)
- **邮箱**: leewyatt7788@gmail.com
- **QQ 群**: 715598051

## 致谢

感谢 `@Yii.Guxing`、`@albert flex`、`@因为许多.许多！`、`@来日方长` 以及这些年所有提交 bug 和建议的用户。
