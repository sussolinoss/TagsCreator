package dev.sussolino.tagscreator.tags.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import dev.sussolino.tagscreator.TagsCreator;
import dev.sussolino.tagscreator.data.PlayerInfo;
import dev.sussolino.tagscreator.data.PlayerInfoManager;
import dev.sussolino.tagscreator.file.settings.Settings;
import dev.sussolino.tagscreator.tags.impl.IHandler;
import dev.sussolino.tagscreator.utils.ColorUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TagChatListener implements PacketListener, IHandler {

    private final LuckPerms luckPerms;
    private final PlayerInfoManager manager;

    public TagChatListener(final TagsCreator instance) {
        this.luckPerms = LuckPermsProvider.get();
        this.manager = instance.getPlayerManager();
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPlayer() instanceof Player p)) return;
        if (!e.getPacketType().equals(PacketType.Play.Client.CHAT_MESSAGE)) return;

        final WrapperPlayClientChatMessage wrap = new WrapperPlayClientChatMessage(e);
        final String message = wrap.getMessage();
        final PlayerInfo info = manager.get(p);

        final User user = luckPerms.getPlayerAdapter(Player.class).getUser(p);

        final String lPrefix = user.getCachedData().getMetaData().getPrefix() != null ? user.getCachedData().getMetaData().getPrefix().replace("null", "") : "";
        final String lSuffix = user.getCachedData().getMetaData().getSuffix() != null ? user.getCachedData().getMetaData().getSuffix().replace("null", "") : "";

        final String prefix = info.getPrefix() != null ? info.getPrefix().getDisplay() : "";
        final String suffix = info.getSuffix() != null ? info.getSuffix().getDisplay() : lSuffix;

        String format = Settings.FORMAT.getAsString()
                .replace("{suffix}", ColorUtils.color(suffix))
                .replace("{prefix}", ColorUtils.color(prefix))
                .replace("{message}", message)
                .replace("{player}", ColorUtils.color(p.getName()));

        e.setCancelled(true);

        final String msg = ColorUtils.color(lPrefix + format);

        Bukkit.getOnlinePlayers()
                .parallelStream()
                .forEach(player -> player.sendMessage(msg));

        Bukkit.getLogger().info(ColorUtils.strip(msg));
    }



    @Override
    public void init() {
        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.MONITOR);
    }
}
