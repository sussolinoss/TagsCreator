package dev.sussolino.tagscreator.file.impl;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public abstract class BukkitFile {

    private final JavaPlugin plugin;
    private final String fileName;

    public BukkitFile(String fileName, JavaPlugin plugin) {
        this.fileName = fileName;
        this.plugin = plugin;
    }

    private File file;
    private FileConfiguration config;

    @SneakyThrows
    public void save() {
        this.config.save(file);
    }

    public void reload() {
        save();
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void init() {
        this.file = new File(this.plugin.getDataFolder(), fileName + ".yml");

        if (!file.exists()) {
            this.plugin.saveResource(fileName + ".yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }
}
