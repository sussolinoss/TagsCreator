package dev.sussolino.tagscreator;

import dev.sussolino.tagscreator.data.PlayerInfoHandler;
import dev.sussolino.tagscreator.data.PlayerInfoManager;
import dev.sussolino.tagscreator.file.cache.CacheYml;
import dev.sussolino.tagscreator.file.settings.SettingsYml;
import dev.sussolino.tagscreator.file.tags.TagsYml;
import dev.sussolino.tagscreator.tags.command.TagCommand;
import dev.sussolino.tagscreator.tags.command.TagsCommand;
import dev.sussolino.tagscreator.tags.impl.IHandler;
import dev.sussolino.tagscreator.tags.listener.TagChatListener;
import dev.sussolino.tagscreator.tags.listener.TagListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class TagsCreator implements IHandler {

    private final JavaPlugin plugin;

    private final TagsYml tags;
    private final CacheYml cache;
    private final SettingsYml settings;

    private final TagCommand tagCommand;
    private final TagsCommand tagsCommand;

    private final TagListener tagListener;
    private final TagChatListener tagChatListener;

    private final PlayerInfoHandler playerHandler;
    private final PlayerInfoManager playerManager;

    public TagsCreator(final JavaPlugin plugin) {
        this.plugin = plugin;
        //--------------------------------------
        this.tags = new TagsYml(this);
        this.cache = new CacheYml(plugin);
        this.settings = new SettingsYml(plugin);
        //--------------------------------------
        this.playerManager = new PlayerInfoManager(cache);
        this.playerHandler = new PlayerInfoHandler(plugin, playerManager);
        //--------------------------------------
        this.tagListener = new TagListener(this);
        this.tagsCommand = new TagsCommand(this);
        this.tagCommand = new TagCommand(this);
        this.tagChatListener = new TagChatListener(this);
        //--------------------------------------
    }

    public void init() {
        //-------------------
        this.tags.init();
        this.cache.init();
        this.settings.init();
        //-------------------
        this.tagListener.init();
        this.tagCommand.init();
        this.tagsCommand.init();
        this.playerHandler.init();
        this.tagChatListener.init();
        //-------------------
    }
}