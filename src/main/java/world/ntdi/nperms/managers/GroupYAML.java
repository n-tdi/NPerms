package world.ntdi.nperms.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.K;
import world.ntdi.nperms.NPerms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GroupYAML implements YAML {
    private final File file;
    private final YamlConfiguration yamlConfiguration;

    public GroupYAML() {
        this.file = new File(NPerms.getInstance().getDataFolder(), "groups.yml");
        if (!this.file.exists()) try { file.createNewFile(); } catch (Exception e) { e.printStackTrace(); } // Create the file if it doesn't exist
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        createDefaultGroup();
    }

    @Override
    public void addPermission(String name, String perm) {
        if (readPerms(name).isEmpty()) {
            yamlConfiguration.set("groups."+name+".perms", List.of(perm));
        } else {
            yamlConfiguration.set("groups." + name + ".perms", readPerms(name).add(perm));
        }
        saveReload();
    }

    @Override
    public void removePermission(String name, String perm) {
        if (readPerms(name).isEmpty()) return;
        yamlConfiguration.set("groups."+name+".perms", readPerms(name).remove(perm));
        saveReload();
    }

    @Override
    public List<String> readPerms(String name) {
        return yamlConfiguration.getStringList("groups."+name+".perms");
    }

    public void setDefault(String name, boolean isDefault) {
        yamlConfiguration.set("groups."+name+".default", isDefault);
        saveReload();
    }

    public int getPlacement(String name) {
        return yamlConfiguration.getInt("groups."+name+".placement", 0);
    }

    public void setPlacement(String name, int placement) {
        yamlConfiguration.set("groups."+name+".placement", placement);
        saveReload();
    }

    @Override
    public String getPrefix(String name) {
        return yamlConfiguration.getString("groups."+name+".prefix", "");
    }

    @Override
    public void setPrefix(String name, String prefix) {
        yamlConfiguration.set("groups."+name+".prefix", prefix);
        saveReload();
    }
    public boolean exists(String name) {
        return getGroups().contains(name);
    }

    public Set<String> getGroups() {
        return yamlConfiguration.getConfigurationSection("groups.").getKeys(false);
    }

    public boolean isDefault(String name) {
        return yamlConfiguration.getBoolean("groups."+name+".default", false);
    }

    public List<String> getDefaultGroups() {
        List<String> groups = new ArrayList<>();
        for (String group : yamlConfiguration.getConfigurationSection("groups.").getKeys(false)) {
            if (isDefault(group)) groups.add(group);
        }
        return groups;
    }

    private boolean defaultGroupExists() {
        return yamlConfiguration.contains("groups.default");
    }

    private void createDefaultGroup() {
        if (!defaultGroupExists()) return;
        String name = "default";
        setDefault(name, true);
        addPermission(name, "");
        setPlacement(name, 1);
        setPrefix(name, "");
    }

    @Override
    public void saveReload() {
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
