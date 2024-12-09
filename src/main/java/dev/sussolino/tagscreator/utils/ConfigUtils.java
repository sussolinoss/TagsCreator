package dev.sussolino.tagscreator.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigUtils {

    public static <T extends ConfigurationSerializable> List<T> getList(FileConfiguration config, String path, Class<T> clazz) {
        List<?> list = config.getList(path);
        if (list == null) return new ArrayList<>(0);

        return list.stream().map(object -> {
            if (clazz.isInstance(object)) {
                return (T) object;
            } else if (object instanceof Map) {
                try {
                    return clazz.getConstructor(Map.class).newInstance(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
