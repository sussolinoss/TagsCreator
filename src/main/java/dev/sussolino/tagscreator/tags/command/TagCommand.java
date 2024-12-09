package dev.sussolino.tagscreator.tags.command;

import dev.sussolino.tagscreator.TagsCreator;
import dev.sussolino.tagscreator.data.PlayerInfo;
import dev.sussolino.tagscreator.data.PlayerInfoManager;
import dev.sussolino.tagscreator.file.settings.Settings;
import dev.sussolino.tagscreator.file.tags.TagsYml;
import dev.sussolino.tagscreator.tags.impl.IHandler;
import dev.sussolino.tagscreator.tags.impl.Tag;
import dev.sussolino.tagscreator.utils.AbstractCommand;
import dev.sussolino.tagscreator.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.print.DocFlavor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TagCommand extends AbstractCommand implements IHandler, TabCompleter {

    private final TagsYml tags;
    private final JavaPlugin plugin;
    private final PlayerInfoManager manager;

    public TagCommand(TagsCreator plugin) {
        this.tags = plugin.getTags();
        this.plugin = plugin.getPlugin();
        this.manager = plugin.getPlayerManager();
    }

    //tag <list>
    //tag <delete/create> <tag> <tagName>
    //tag <give> <tagName> <player>

    @Override
    public void execute(Player p, String[] args) {
        if (args.length < 1) return;
        switch (args[0]) {
            case "create" -> {
                if (args.length < 3) {
                    Settings.ERRORS_ARGS.sendMessage(p);
                    return;
                }
                String tagName = args[1];
                String tagPrefix = args[2];

                Tag tag = new Tag(tagName, tagPrefix);

                if (this.tags.exist(tagName)) {
                    Settings.TAGS_ALREADY__EXIST.sendMessage(p);
                    return;
                }

                this.tags.addTag(tag);

                Settings.TAGS_CREATE.sendMessage(p, "{tag}", tagName);
            }
            case "delete" -> {
                if (args.length < 2) {
                    Settings.ERRORS_ARGS.sendMessage(p);
                    return;
                }
                String tagName = args[1];
                Tag tag = this.tags.getTagFromName(tagName);

                if (tag == null) {
                    Settings.TAG_NOT__EXIST.sendMessage(p);
                    return;
                }

                this.tags.removeTag(tag);

                Settings.TAGS_DELETE.sendMessage(p);
            }
            case "list" -> {
                List<Tag> tags = this.tags.getTags();

                if (tags.isEmpty()) {
                    Settings.ERRORS_TAGS.sendMessage(p);
                    return;
                }

                StringBuilder replacer = new StringBuilder();

                for (Tag tag : tags) {
                    String format = ColorUtils.color(tag.getName() + " ");
                    replacer.append(format);
                }

                Settings.TAGS_LIST.sendMessage(p, "{tags}", replacer.toString());
            }
            case "give" -> {
                String tagName = args[1];
                String playerName = args[2];

                Player target = Bukkit.getPlayerExact(playerName);

                if (target == null) {
                    Settings.ERRORS_TARGET.sendMessage(p);
                    return;
                }

                PlayerInfo info = manager.get(target);

                List<Tag> tags = this.tags.getTags();
                List<Tag> playerTags = info.getTags();

                boolean doesntExist = tags.isEmpty() || !this.tags.exist(tagName);

                if (doesntExist) {
                    Settings.TAG_NOT__EXIST.sendMessage(target);
                    return;
                }

                Tag tag = this.tags.getTagFromName(tagName);

                boolean alreadyHas = playerTags.contains(tag) || playerTags.stream().anyMatch(t -> t.getName().equals(tag.getName()));

                if (alreadyHas) {
                    Settings.TAG_ALREADY__HAS.sendMessage(target, "{tag}", tagName);
                    return;
                }

                info.addTag(tag);
                Settings.TAGS_GIVE.sendMessage(target, "{player}", playerName);
            }
        }
    }

    @Override
    public void init() {
        plugin.getCommand("tag").setExecutor(this);
        plugin.getCommand("tag").setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            return Arrays.asList("create", "delete", "list", "give");
        }
        else if (strings.length == 2) {
            if (strings[0].equals("give") || strings[0].equals("delete")) return tags.getTags().stream().map(Tag::getName).toList();
        }
        else if (strings.length == 3) {
            if (strings[0].equals("give")) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return Collections.emptyList();
    }
}
