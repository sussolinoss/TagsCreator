package dev.sussolino.tagscreator;

import com.github.retrooper.packetevents.PacketEvents;
import dev.sussolino.tagscreator.tags.impl.Tag;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Initializer extends JavaPlugin {

    public static TagsCreator INSTANCE;


    @Override
    public void onLoad(){
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            Bukkit.getLogger().severe("[!] No LuckPerms API found!");
            Bukkit.shutdown();
            return;
        }
        ConfigurationSerialization.registerClass(Tag.class);
        (INSTANCE = new TagsCreator(this)).init();
        PacketEvents.getAPI().init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}
