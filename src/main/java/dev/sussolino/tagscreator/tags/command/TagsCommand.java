package dev.sussolino.tagscreator.tags.command;

import dev.sussolino.tagscreator.TagsCreator;
import dev.sussolino.tagscreator.data.PlayerInfo;
import dev.sussolino.tagscreator.data.PlayerInfoManager;
import dev.sussolino.tagscreator.file.settings.Settings;
import dev.sussolino.tagscreator.file.tags.TagsYml;
import dev.sussolino.tagscreator.tags.impl.IHandler;
import dev.sussolino.tagscreator.tags.impl.Tag;
import dev.sussolino.tagscreator.utils.AbstractCommand;
import dev.sussolino.tagscreator.utils.SkullUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class TagsCommand extends AbstractCommand implements IHandler {

    private static final String LEFT_ARROW = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdhYWNhZDE5M2UyMjI2OTcxZWQ5NTMwMmRiYTQzMzQzOGJlNDY0NGZiYWI1ZWJmODE4MDU0MDYxNjY3ZmJlMiJ9fX0=";
    private static final String RIGHT_ARROW = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19";

    public static ItemStack LEFT_ARROW_ITEM = SkullUtils.getAsSkull(new SkullUtils.Skin(LEFT_ARROW));
    public static ItemStack RIGHT_ARROW_ITEM = SkullUtils.getAsSkull(new SkullUtils.Skin(RIGHT_ARROW));

    private final JavaPlugin plugin;
    private final Map<UUID, Tag> hashish;
    private final PlayerInfoManager manager;

    public TagsCommand(TagsCreator instance) {
        this.plugin = instance.getPlugin();
        this.manager = instance.getPlayerManager();
        this.hashish = instance.getTagListener().getHashish();
    }

    @Override
    public void init() {
        plugin.getCommand("tags").setExecutor(this);
    }

    @Override
    public void execute(Player player, String[] args) {
        hashish.remove(player.getUniqueId());
        player.openInventory(getPaginatedGui(player));
    }

    public Inventory getPaginatedGui(Player player) {
        Inventory inv = Bukkit.createInventory(player, 54, Settings.GUI_TAGS_TITLE.getAsString());
        PlayerInfo info = manager.get(player);
        List<ItemStack> tags = info.getTagsAsItemStack();

        //----------------------------------------------------------------------
        ItemStack panel = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        List<Integer> panels = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 49, 51, 52, 53));
        panels.forEach(slot -> inv.setItem(slot, panel));
        //----------------------------------------------------------------------
        tags.forEach(inv::addItem);
        //-------------------------
        inv.setItem(48, LEFT_ARROW_ITEM);
        inv.setItem(50, RIGHT_ARROW_ITEM);
        //-------------------------
        return inv;
    }
}
