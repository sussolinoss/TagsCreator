package dev.sussolino.tagscreator.file.cache;

import dev.sussolino.tagscreator.file.impl.BukkitFile;
import dev.sussolino.tagscreator.tags.impl.Tag;
import dev.sussolino.tagscreator.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class CacheYml extends BukkitFile {

    public CacheYml(JavaPlugin plugin) {
        super("cache", plugin);
    }

    /**
     *  Total Functions
     *
     * @return Tag
     */

    public List<Tag> getTags(String playerName) {
        return ConfigUtils.getList(getConfig(), playerName + ".tags", Tag.class);
    }
    public void addTag(Tag tag, String playerName) {
        List<Tag> tags = new ArrayList<>(getTags(playerName));

        tags.add(tag);

        this.getConfig().set(playerName + ".tags", tags);
        this.reload();
    }
    public void removeTag(Tag tag, String playerName) {
        List<Tag> tags = getTags(playerName)
                .stream()
                .filter(t -> !t.getName().equals(tag.getName()))
                .toList();

        this.getConfig().set(playerName + ".tags", tags);
        this.reload();
    }

    /**
     *  Primary Functions
     *
     * @return Tag
     */

    public Tag getSuffix(String playerName) {
        return (Tag) getConfig().get(playerName + ".suffix");
    }
    public Tag getPrefix(String playerName) {
        return (Tag) getConfig().get(playerName + ".prefix");
    }
    public void setPrefix(Tag tag, String playerName) {
        this.getConfig().set(playerName + ".prefix", tag);
        this.reload();
    }

    public void setSuffix(Tag tag, String playerName) {
        this.getConfig().set(playerName + ".suffix", tag);
        this.reload();
    }

}
