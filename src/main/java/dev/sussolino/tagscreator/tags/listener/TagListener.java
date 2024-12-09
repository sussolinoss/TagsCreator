package dev.sussolino.tagscreator.tags.listener;

import dev.sussolino.tagscreator.TagsCreator;
import dev.sussolino.tagscreator.data.PlayerInfo;
import dev.sussolino.tagscreator.data.PlayerInfoManager;
import dev.sussolino.tagscreator.file.settings.Settings;
import dev.sussolino.tagscreator.file.tags.TagsYml;
import dev.sussolino.tagscreator.tags.command.TagsCommand;
import dev.sussolino.tagscreator.tags.impl.Tag;
import dev.sussolino.tagscreator.utils.ColorUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.ConcurrentHashMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TagListener implements Listener {

    private final TagsYml tags;
    private final TagsCreator instance;
    private final Map<UUID, Integer> pages;
    private final PlayerInfoManager manager;
    @Getter private final Map<UUID, Tag> hashish;

    public TagListener(TagsCreator plugin) {
        this.instance = plugin;
        this.tags = plugin.getTags();
        this.pages = new ConcurrentHashMap<>();
        this.hashish = new ConcurrentHashMap<>();
        this.manager = plugin.getPlayerManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (title.equals(Settings.GUI_TAGS_TITLE.getAsString())) {
            e.setCancelled(true);

            ItemStack clicked = e.getCurrentItem();
            Player player = (Player) e.getWhoClicked();
            PlayerInfo info = manager.get(player);

            if (clicked == null) return;

            if (clicked.getType().equals(Material.PLAYER_HEAD)) {
                PageType type = PageType.NEXT.getSlot() == e.getSlot() ? PageType.NEXT : PageType.PREVIOUS;
                switchPage(e.getInventory(), player, info, type);
                return;
            }

            if (!clicked.getType().equals(Material.NAME_TAG)) return;

            Tag clickedTag = tags.getTagFromDisplay(clicked.getItemMeta().getDisplayName());
            if (clickedTag == null) return;

            boolean isPrefix = info.getPrefix() != null && info.getPrefix().getName().equals(clickedTag.getName());
            boolean isSuffix = info.getSuffix() != null && info.getSuffix().getName().equals(clickedTag.getName());
            boolean alreadySet = isPrefix || isSuffix;

            if (alreadySet) {
                info.unset(isPrefix ? PlayerInfo.TagInfo.PREFIX : PlayerInfo.TagInfo.SUFFIX);
                Settings.TAG_UNSET.sendMessage(player, "{tag}", isPrefix ? "prefix" : "suffix");
                return;
            }

            hashish.put(player.getUniqueId(), clickedTag);
            confirm(player);
        }
        else if (title.equals(Settings.GUI_CONFIRM_TITLE.getAsString())) {
            e.setCancelled(true);

            Player player = (Player) e.getWhoClicked();
            PlayerInfo info = manager.get(player);
            Tag clickedTag = hashish.get(player.getUniqueId());

            ItemStack choice = e.getCurrentItem();
            if (choice == null || !choice.getType().equals(Material.NAME_TAG)) return;

            ItemMeta meta = choice.getItemMeta();
            boolean prefix = ColorUtils.color(meta.getDisplayName()).equals(Settings.GUI_CONFIRM_PREFIX_NAME.getAsString()
                    .replace("{tag}", ColorUtils.color(clickedTag.getDisplay()))
                    .replace("{player}", player.getName()));

            info.set(prefix ? PlayerInfo.TagInfo.PREFIX : PlayerInfo.TagInfo.SUFFIX, clickedTag);
            Settings.TAG_SET.sendMessage(player, "{tag}", prefix ? "prefix" : "suffix");
            player.closeInventory();
        }
    }

    public void switchPage(Inventory inv, Player player, PlayerInfo info, PageType type) {
        List<ItemStack> items = info.getTagsAsItemStack();
        int currentPage = pages.getOrDefault(player.getUniqueId(), 0);
        int totalPages = (int) Math.ceil(items.size() / 28.0);
        int tagsPerPage = 28;

        if (type == PageType.NEXT) {
            if (currentPage < totalPages - 1) currentPage++;
        }
        else if (type == PageType.PREVIOUS) {
            if (currentPage > 0) currentPage--;
        }

        this.pages.put(player.getUniqueId(), currentPage);

        for (int i = 0; i < inv.getSize(); i++) {
            boolean border = isBorderSlot(i) && i != 48 && i != 50;
            inv.setItem(i, border ? new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE) : null);
        }

        int start = currentPage * tagsPerPage;
        int end = Math.min(start + tagsPerPage, items.size());

        int[] center = getCenterSlots();
        for (int i = start; i < end; i++) {
            int slotIndex = i - start;
            if (slotIndex < center.length) {
                inv.setItem(center[slotIndex], items.get(i));
            }
        }

        inv.setItem(48, TagsCommand.LEFT_ARROW_ITEM);
        inv.setItem(50, TagsCommand.RIGHT_ARROW_ITEM);

        player.openInventory(inv);
    }

    private boolean isBorderSlot(int slot) {
        int rows = 6;
        int columns = 9;

        if (slot < columns || slot >= (rows - 1) * columns) {
            return true;
        }

        return slot % columns == 0 || (slot + 1) % columns == 0;
    }

    private int[] getCenterSlots() {
        return new int[]{
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(Settings.GUI_CONFIRM_TITLE.getAsString())) hashish.remove(event.getPlayer().getUniqueId());
    }

    private void confirm(Player player) {
        Inventory confirm = Bukkit.createInventory(player, 54, Settings.GUI_CONFIRM_TITLE.getAsString());
        ItemStack prefixItem = getFromSettings("prefix", player);
        ItemStack suffixItem = getFromSettings("suffix", player);
        confirm.setItem(Settings.GUI_CONFIRM_PREFIX_SLOT.getAsInt(), prefixItem);
        confirm.setItem(Settings.GUI_CONFIRM_SUFFIX_SLOT.getAsInt(), suffixItem);
        player.closeInventory();
        player.openInventory(confirm);
    }

    private ItemStack getFromSettings(String name, Player player) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        boolean prefix = name.equals("prefix");

        String displayName = prefix ? Settings.GUI_CONFIRM_PREFIX_NAME.getAsString() : Settings.GUI_CONFIRM_SUFFIX_NAME.getAsString();
        List<String> lore = prefix ? Settings.GUI_CONFIRM_PREFIX_LORE.getAsList() : Settings.GUI_CONFIRM_SUFFIX_LORE.getAsList();



        displayName = displayName
                .replace("{tag}", hashish.get(player.getUniqueId()).getDisplay())
                .replace("{player}", player.getName());

        meta.setDisplayName(ColorUtils.color(displayName));
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    /**
     *
     *  Registering the event
     *
     */

    public void init() {
        Bukkit.getPluginManager().registerEvents(this, instance.getPlugin());
    }

    @Getter
    public enum PageType {
        NEXT(50),
        PREVIOUS(48);

        private final int slot;

        PageType(int slot) {
            this.slot = slot;
        }
    }
}
