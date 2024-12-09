package dev.sussolino.tagscreator.data;

import dev.sussolino.tagscreator.file.cache.CacheYml;
import dev.sussolino.tagscreator.tags.impl.Tag;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerInfo {

    private final String playerName;
    private final CacheYml cache;

    public PlayerInfo(CacheYml yml, String playerName) {
        this.playerName = playerName.toLowerCase();
        this.cache = yml;
    }

    /**
     *
     * Getters
     *
     */

    public Tag getSuffix() {
        return cache.getSuffix(playerName);
    }
    public Tag getPrefix() {
        return cache.getPrefix(playerName);
    }
    public List<Tag> getTags() {
        return cache.getTags(playerName);
    }
    public List<ItemStack> getTagsAsItemStack() {
        if (getTags() == null || getTags().isEmpty()) return new ArrayList<>();

        List<ItemStack> items = new ArrayList<>();

        getTags().forEach(tag -> items.add(Tag.toItemStack(tag)));

        return items;
    }

    /**
     *
     * Setters
     *
     */

    public void set(TagInfo info, Tag tag) {
        if (info == TagInfo.PREFIX) cache.setPrefix(tag, playerName);
        else if (info == TagInfo.SUFFIX) cache.setSuffix(tag, playerName);
    }
    public void unset(TagInfo info) {
        if (info == TagInfo.PREFIX) cache.setPrefix(null, playerName);
        else if (info == TagInfo.SUFFIX) cache.setSuffix(null, playerName);
    }

    public void addTag(Tag tag) {
        cache.addTag(tag, playerName);
    }
    public void removeTag(Tag tag) {
        cache.removeTag(tag,playerName);

        if (getPrefix() != null && getPrefix().equals(tag)) unset(TagInfo.PREFIX);
        if (getSuffix() != null && getSuffix().equals(tag)) unset(TagInfo.SUFFIX);
    }

    /**
     *
     * Info
     *
     */

    public enum TagInfo {
        ALL,
        PREFIX,
        SUFFIX
    }
}
