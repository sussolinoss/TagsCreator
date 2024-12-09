package dev.sussolino.tagscreator.file.settings;

import dev.sussolino.tagscreator.Initializer;
import dev.sussolino.tagscreator.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public enum Settings {

    ERROR,
    PREFIX,
    FORMAT,

    ERRORS_TARGET,
    ERRORS_ARGS,
    ERRORS_TAGS,

    TAGS_CREATE,
    TAGS_LIST,
    TAGS_DELETE,
    TAGS_ALREADY__EXIST,
    TAGS_NOT__EXIST,

    GUI_TAGS_TITLE,
    GUI_CONFIRM_TITLE,

    GUI_CONFIRM_PREFIX_NAME,
    GUI_CONFIRM_SUFFIX_NAME,

    GUI_CONFIRM_PREFIX_SLOT,
    GUI_CONFIRM_SUFFIX_SLOT,

    GUI_CONFIRM_PREFIX_LORE,
    GUI_CONFIRM_SUFFIX_LORE,

    TAG_SET,
    TAG_UNSET,

    TAGS_GIVE,
    TAG_NOT__EXIST,
    TAG_ALREADY__HAS;

    private final String path;
    private static final FileConfiguration config = Initializer.INSTANCE.getSettings().getConfig();

    Settings() {
        this.path = name()
                .toLowerCase()
                .replace("__", "-")
                .replace('_', '.');
    }

    public String getAsString() {
        String message = ColorUtils.color(config.getString(path));
        return name().equals("FORMAT") ? message : ColorUtils.color(message.replace("{error}", Objects.requireNonNull(config.getString("error"))).replace("{prefix}", Objects.requireNonNull(config.getString("prefix"))));
    }
    public int getAsInt() {
        return config.getInt(path);
    }

    public void sendMessage(Player player) {
        player.sendMessage(getAsString());
    }
    public void sendMessage(Player player, String placeholder, String replacer) {
        player.sendMessage(getAsString().replace(placeholder, replacer));
    }

    public List<String> getAsList() {
        return ColorUtils.colorList(config.getStringList(path));
    }
}
