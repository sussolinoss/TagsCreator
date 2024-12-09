package dev.sussolino.tagscreator.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;

public class SkullUtils {

    public static ItemStack getAsSkull(final Skin skin) {
        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        gameProfile.getProperties().put("textures", new Property("textures", skin.value(), ""));
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            Field profileField = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftMetaSkull").getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
            itemStack.setItemMeta(skullMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemStack;
    }

    @Getter
    public static final class Skin {
        private final String value;

        public Skin(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Skin) obj;
            return Objects.equals(this.value, that.value);
        }


        @Override
        public String toString() {
            return "Skin[" + "value=" + value + ']';
        }
    }
}
