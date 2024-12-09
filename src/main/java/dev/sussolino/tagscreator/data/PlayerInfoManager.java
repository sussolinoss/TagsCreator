package dev.sussolino.tagscreator.data;

import dev.sussolino.tagscreator.file.cache.CacheYml;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerInfoManager {

    private final CacheYml cache;

    public PlayerInfoManager(CacheYml cache) {
        this.cache = cache;
    }

    private final Map<String, PlayerInfo> PLAYERS = new ConcurrentHashMap<>();

    public PlayerInfo get(Player player) {
        return PLAYERS.get(player.getName());
    }
    public PlayerInfo get(String player) {
        return PLAYERS.get(player);
    }

    public void inject(Player player) {
        PLAYERS.putIfAbsent(player.getName(), new PlayerInfo(cache, player.getName()));
    }
    public void eject(Player player) {
        PLAYERS.remove(player.getName());
    }

    public Map<String, PlayerInfo> map() {
        return PLAYERS;
    }
}
