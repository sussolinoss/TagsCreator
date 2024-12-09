package dev.sussolino.tagscreator.file.settings;

import dev.sussolino.tagscreator.file.impl.BukkitFile;
import org.bukkit.plugin.java.JavaPlugin;

public class SettingsYml extends BukkitFile {

    public SettingsYml(JavaPlugin plugin) {
        super("settings", plugin);
    }
}