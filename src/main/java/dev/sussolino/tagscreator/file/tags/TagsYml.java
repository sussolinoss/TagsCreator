package dev.sussolino.tagscreator.file.tags;

import dev.sussolino.tagscreator.TagsCreator;
import dev.sussolino.tagscreator.file.impl.BukkitFile;
import dev.sussolino.tagscreator.file.settings.Settings;
import dev.sussolino.tagscreator.tags.impl.Tag;
import dev.sussolino.tagscreator.tags.listener.TagListener;
import dev.sussolino.tagscreator.utils.ColorUtils;
import dev.sussolino.tagscreator.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagsYml extends BukkitFile {

    private final TagsCreator instance;

    public TagsYml(TagsCreator plugin) {
        super("tags", plugin.getPlugin());
        instance = plugin;
    }

    public List<Tag> getTags() {
        return ConfigUtils.getList(getConfig(), "tags", Tag.class);
    }

    public Tag getTagFromName(String name) {
        return this.getTags().stream().filter(tag -> tag.getName().equals(name)).findFirst().orElse(null);
    }
    public Tag getTagFromDisplay(String display) {
        return this.getTags().stream().filter(tag -> ColorUtils.color(tag.getDisplay()).equals(display)).findFirst().orElse(null);
    }

    public boolean exist(String name) {
        return this.getTags().stream().anyMatch(tag -> tag.getName().equals(name));
    }

    public void addTag(Tag tag) {
        List<Tag> tags = new ArrayList<>(this.getTags());

        tags.add(tag);

        this.getConfig().set("tags", tags);
        this.reload();
    }

    public void removeTag(Tag tag) {
        List<Tag> tags = new ArrayList<>(this.getTags());

        tags.remove(tag);

        this.getConfig().set("tags", tags);
        this.reload();

        instance.getPlayerManager().map().forEach((s, playerInfo) -> playerInfo.removeTag(tag));
    }
}
