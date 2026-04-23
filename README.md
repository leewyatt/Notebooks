# Notebook

[![Version](https://img.shields.io/jetbrains/plugin/v/com.itcodebox.leewyatt.notebooks.id?label=version)](https://plugins.jetbrains.com/plugin/16998-notebook)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.itcodebox.leewyatt.notebooks.id)](https://plugins.jetbrains.com/plugin/16998-notebook)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

Code-aware, local-first note-taking for IntelliJ-based IDEs. Capture snippets while you code, organize them in a three-level hierarchy (`Notebook` > `Chapter` > `Note`), and keep everything in a single SQLite file on your own machine — **no cloud, no account, no telemetry**.

Works with both **Community** and **Ultimate** editions. Requires IntelliJ IDEA **2023.3+** and Java **17+**.

<a href="https://plugins.jetbrains.com/plugin/16998-notebook"><img src="https://img.shields.io/badge/%E2%AC%87%EF%B8%8F_Install_from-JetBrains_Marketplace-orange?style=for-the-badge&logo=jetbrains" alt="Install from JetBrains Marketplace"></a>

中文文档：[README.zh-CN.md](./README.zh-CN.md)

---

## What it is

A local-first notebook that lives inside your IDE. Notes are organized in a three-level hierarchy:

```
Notebook  >  Chapter  >  Note
```

Each note is a piece of text or code with optional description, source path, original offsets, and image attachments. Everything is stored in a single SQLite file under your home directory — no cloud, no account, no telemetry.

## Features

- **Capture from the editor** — Select text or code → right-click *Add to Notebook* (or `Ctrl+\`). Source file path and offset range are captured automatically.
- **Insert back into the editor** — `Ctrl+Shift+\` or right-click *Insert note into editor* pastes a saved note at the caret.
- **Three-level organization** — drag rows within a table to reorder, drag a chapter onto another notebook to move it across, same for notes across chapters.
- **Search** — `Alt+S` in the editor opens a search dialog. Scopes: titles only, titles + content, titles + description, or full-text.
- **Image attachments** — JPG/PNG/GIF up to 10 MB. Thumbnails generated at 100×100 automatically.
- **Import / Export JSON** — Full round-trip backup of all notebooks, chapters, notes, and referenced images.
- **Export as Markdown (single notebook)** — Flat markdown file generated from a customizable Groovy template.
- **Export as Markdown Tree (new in 1.41)** — Every notebook / chapter / note written to a portable folder tree, one `.md` per note, with YAML frontmatter and `_assets/` image folder. Ready to drop into Obsidian or Typora.
- **Automatic database backup (new in 1.41)** — Each plugin version upgrade copies your SQLite file to `~/.ideaNotebooksFile/backups/` before any schema migration. Last 5 backups are kept.
- **Bilingual UI** — English & Simplified Chinese.

## Installation

### From JetBrains Marketplace

1. `File → Settings → Plugins → Marketplace`
2. Search for `Notebook`
3. Install → restart IDE

### From a pre-built ZIP

1. Download the latest ZIP from [JetBrains Marketplace](https://plugins.jetbrains.com/) or this repo's Releases page.
2. `File → Settings → Plugins → ⚙️ → Install Plugin from Disk…`

## Data location

| Path | Contents |
|---|---|
| `~/.ideaNotebooksFile/notebooks.db` | SQLite database: notebooks, chapters, notes |
| `~/.ideaNotebooksFile/notebook_images/` | Attached images and thumbnails |
| `~/.ideaNotebooksFile/backups/` | Automatic version-upgrade backups |

The database is shared across all IDE projects on the same machine — you always see the same notes. Only UI state (last selected item, pane visibility) is stored per-project.

## Keyboard shortcuts

| Shortcut | Action |
|---|---|
| `Ctrl + \` | Add selection to Notebook |
| `Ctrl + Shift + \` | Insert note into editor |
| `Alt + S` | Search notes |
| `Alt + O` | Activate Notebook tool window |
| `Shift + Alt + S` | Open notebook search bar |

## Configuration

`File → Settings → Tools → Notebook`

- Font & font size for code editor
- Thumbnail max size
- Restore last selection on startup
- Markdown export template (Groovy)
- Show "Add to Notebook" for empty selection

## Exporting your data

If you ever want to leave this plugin — or just want a portable backup — use the gear menu (⚙️) on the Notebook tool window:

- **Export JSON** — full database + images, for round-trip re-import later.
- **Export as Markdown Tree** — one `.md` per note, organized as `<notebook>/<chapter>/<note>.md` with an `_assets/` image folder per notebook. Obsidian-friendly.

Nothing is locked in.

## Building from source

Requires JDK 21 (JBR 21 recommended; pinned by `gradle.properties` → `org.gradle.java.home`).

```bash
# compile + package
./gradlew build
# launch a sandbox IDE with the plugin installed            
./gradlew runIde
# run JetBrains Plugin Verifier (multi-IDE compat)           
./gradlew verifyPlugin     
```

The built plugin ZIP lands in `build/distributions/`.

## Project layout (quick map)

| Package | Responsibility |
|---|---|
| `ui/` | Swing panels, tables, dialogs, settings |
| `action/` | Editor actions (`Ctrl+\` etc.) |
| `service/` / `service/impl/` | Business services (DB-backed) |
| `dao/` / `dao/impl/` | SQL access via Apache DBUtils |
| `entity/` | Data model: `Notebook`, `Chapter`, `Note`, … |
| `utils/` | Import / export, file helpers, i18n |
| `projectservice/` | Project-scoped UI state & message-bus listeners |

For a detailed architecture reference, see internal dev docs (not shipped in releases).

## License

Licensed under the [Apache License, Version 2.0](./LICENSE).

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

## Contact & support

- **Issues / feature requests**: [GitHub Issues](https://github.com/leewyatt/)
- **Email**: leewyatt7788@gmail.com
- **QQ group**: 715598051

## Credits

Thanks to `@Yii.Guxing`, `@albert flex`, `@因为许多.许多！`, `@来日方长` and every user who has reported bugs and shared feedback over the years.
