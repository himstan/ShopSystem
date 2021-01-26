package hu.stan.shopsystem;

import hu.stan.shopsystem.strifeplugin.DreamPlugin;
import hu.stan.shopsystem.strifeplugin.configs.subconfigs.SubConfig;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MaterialAdapter {

    private DreamPlugin plugin;
    private static Map<String, Material> materialMap;

    public MaterialAdapter(DreamPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    public void init() {
        materialMap = new HashMap<>();
        loadMaterials();
    }

    public static Material getMaterial(String materialName) {
        Material material = materialMap.get(materialName.toLowerCase());
        if (material == null) material = Material.getMaterial(materialName.toUpperCase());
        return material;
    }

    private void loadMaterials() {
        SubConfig config = plugin.getConfigManager().getSubConfig("material_pairs");
        for (String key : config.getConfig().getKeys(false)) {
            String materialName = Objects.requireNonNull(config.getConfig().getString(key)).toUpperCase(Locale.ROOT);
            Material material = Material.getMaterial(materialName);
            if (material != null) {
                materialMap.put(key.toLowerCase(), material);
            }
        }
    }
}
