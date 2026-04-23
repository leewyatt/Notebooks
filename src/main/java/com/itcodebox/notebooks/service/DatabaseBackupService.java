package com.itcodebox.notebooks.service;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.extensions.PluginId;
import com.itcodebox.notebooks.constant.PluginConstant;
import com.itcodebox.notebooks.ui.toolsettings.AppSettingsState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Automatic SQLite backup triggered by plugin version changes. Ensures that
 * a plugin upgrade never leaves old users without a recoverable copy of
 * notebooks.db even if a new version ships with a migration bug.
 *
 * <p>Backups live under {@code ~/.ideaNotebooksFile/backups/} and follow the
 * filename convention {@code notebooks_<yyyyMMdd_HHmmss>_from_<prev>_to_<cur>.db}.
 * The {@link #KEEP_LAST_N_BACKUPS} most recent files are retained; older ones
 * are deleted to keep the directory bounded.
 */
public final class DatabaseBackupService {

    private static final Logger LOG = Logger.getInstance(DatabaseBackupService.class);
    private static final String PLUGIN_ID = "com.itcodebox.leewyatt.notebooks.id";
    private static final String BACKUP_DIR_NAME = "backups";
    private static final String BACKUP_PREFIX = "notebooks_";
    private static final int KEEP_LAST_N_BACKUPS = 5;

    private DatabaseBackupService() {
    }

    /**
     * Call during plugin bootstrap (before any DB write). No-op if the stored
     * {@code lastKnownPluginVersion} already matches the running version.
     */
    public static void backupIfVersionChanged() {
        String currentVersion = resolveCurrentVersion();
        if (currentVersion == null) {
            return;
        }
        AppSettingsState settings = AppSettingsState.getInstance();
        String lastKnown = settings.lastKnownPluginVersion;
        if (currentVersion.equals(lastKnown)) {
            return;
        }
        try {
            doBackup(currentVersion, lastKnown);
        } catch (IOException e) {
            LOG.warn("Failed to backup notebooks.db before upgrade to " + currentVersion, e);
            // Do NOT update lastKnownPluginVersion on failure — next startup will retry.
            return;
        }
        settings.lastKnownPluginVersion = currentVersion;
    }

    private static String resolveCurrentVersion() {
        PluginDescriptor descriptor = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID));
        if (descriptor == null) {
            LOG.warn("Notebook plugin descriptor not found; skipping version-change backup");
            return null;
        }
        return descriptor.getVersion();
    }

    private static void doBackup(String current, String previous) throws IOException {
        Path source = PluginConstant.DB_FILE_PATH;
        if (!Files.exists(source) || Files.size(source) == 0) {
            // No DB yet (fresh install) — nothing to back up.
            return;
        }
        Path backupDir = PluginConstant.PROJECT_DB_DIRECTORY_PATH.resolve(BACKUP_DIR_NAME);
        Files.createDirectories(backupDir);
        String stamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
        String prevLabel = (previous == null || previous.isEmpty()) ? "unknown" : sanitize(previous);
        String filename = BACKUP_PREFIX + stamp + "_from_" + prevLabel + "_to_" + sanitize(current) + ".db";
        Path target = backupDir.resolve(filename);
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        try {
            Files.setLastModifiedTime(target, FileTime.fromMillis(System.currentTimeMillis()));
        } catch (IOException ignored) {
            // Best-effort timestamp; not critical.
        }
        LOG.info("Notebooks DB backup created at " + target);
        pruneOldBackups(backupDir);
    }

    private static void pruneOldBackups(Path dir) throws IOException {
        try (Stream<Path> stream = Files.list(dir)) {
            List<Path> files = stream
                    .filter(p -> p.getFileName().toString().startsWith(BACKUP_PREFIX))
                    .sorted(Comparator.comparing((Path p) -> p.getFileName().toString()).reversed())
                    .toList();
            for (int i = KEEP_LAST_N_BACKUPS; i < files.size(); i++) {
                try {
                    Files.deleteIfExists(files.get(i));
                } catch (IOException e) {
                    LOG.warn("Failed to prune old backup " + files.get(i), e);
                }
            }
        }
    }

    private static String sanitize(String version) {
        return version.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
