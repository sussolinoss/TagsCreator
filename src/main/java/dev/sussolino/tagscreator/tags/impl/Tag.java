package dev.sussolino.tagscreator.tags.impl;

import dev.sussolino.tagscreator.file.tags.TagsYml;
import dev.sussolino.tagscreator.utils.ColorUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Tag implements ConfigurationSerializable {

    private String name;
    private String display;

    public Tag(String name, String display) {
        this.name = name;
        this.display = display;
    }

    public Tag(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.display = (String) map.get("display");
    }

    public Tag() {}

    public static Tag valueOf(ItemStack item, TagsYml yml) {
        ItemMeta meta = item.getItemMeta();
        String display = meta.getDisplayName();
        return yml.getTagFromDisplay(ChatColor.stripColor(display));
    }

    public static ItemStack toItemStack(Tag tag) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ColorUtils.color(tag.display));
        item.setItemMeta(meta);

        return item;
    }

    public boolean equals(Tag tag) {
        return this.getName().equals(tag.getName());
    }

    @Override
    @NonNull
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("display", display);
        return map;
    }
}
