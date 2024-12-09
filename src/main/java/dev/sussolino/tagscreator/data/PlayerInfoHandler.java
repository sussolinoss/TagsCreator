package dev.sussolino.tagscreator.data;

import dev.sussolino.tagscreator.tags.impl.IHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInfoHandler implements Listener, IHandler {

    private final JavaPlugin plugin;
    private final PlayerInfoManager manager;

    public PlayerInfoHandler(JavaPlugin plugin, PlayerInfoManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onInject(PlayerJoinEvent event) {
        manager.inject(event.getPlayer());
    }
    @EventHandler
    public void onEject(PlayerQuitEvent event) {
        manager.eject(event.getPlayer());
    }


    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
