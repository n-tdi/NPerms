package world.ntdi.nperms.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import world.ntdi.nperms.NPerms;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerYAML implements YAML {
    private final File file;
    private final YamlConfiguration yamlConfiguration;

    public PlayerYAML() {
        this.file = new File(NPerms.getInstance().getDataFolder(), "players.yml");
        if (!this.file.exists()) try { file.createNewFile(); } catch (Exception e) { e.printStackTrace(); } // Create the file if it doesn't exist
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public List<String> readPlayerGroups(String name) {
        return yamlConfiguration.getStringList("players."+name+".groups");
    }

    public LinkedList<String> getRankedGroups(String name) {
        if (readPlayerGroups(name).isEmpty()) return new LinkedList<>();
        Map<Integer, String> unsorted = new HashMap<>();
        readPlayerGroups(name).forEach(group -> unsorted.put(NPerms.getGroupYAML().getPlacement(group), group));

        SortedSet<Integer> keySet = new TreeSet<>(Collections.reverseOrder());
        keySet.addAll(unsorted.keySet());

        LinkedList<String> sorted = new LinkedList<>();
        keySet.forEach(id -> sorted.add(unsorted.get(id)));
        return sorted;
    }

    public void addGroup(UUID uuid, String group) {
        if (readPlayerGroups(uuid.toString()).isEmpty()) {
            yamlConfiguration.set("players."+uuid+".groups", List.of(group));
        } else {
            yamlConfiguration.set("players." + uuid + ".groups", readPerms(uuid.toString()).add(group));
        }
        saveReload();
    }

    public void removeGroup(UUID uuid, String group) {
        if (readPlayerGroups(uuid.toString()).isEmpty()) return;
        yamlConfiguration.set("players."+uuid+".group", readPlayerGroups(uuid.toString()).remove(group));
        saveReload();
    }

    @Override
    public void addPermission(String name, String perm) {
        if (readPerms(name).isEmpty()) {
            yamlConfiguration.set("players."+name+".perms", List.of(perm));
        } else {
            yamlConfiguration.set("players." + name + ".perms", readPerms(name).add(perm));
        }
        saveReload();
    }

    @Override
    public void removePermission(String name, String perm) {
        if (readPerms(name).isEmpty()) return;
        yamlConfiguration.set("players."+name+".perms", readPerms(name).remove(perm));
        saveReload();
    }

    @Override
    public List<String> readPerms(String name) {
        return yamlConfiguration.getStringList("players."+name+".perms");
    }

    @Override
    public void setPrefix(String name, String prefix) {
        yamlConfiguration.set("players."+name+".prefix", prefix);
        saveReload();
    }

    @Override
    public String getPrefix(String name) {
        String prefix = yamlConfiguration.getString("players."+name+".prefix", "");
        if (prefix.equals("")) {
            if (getRankedGroups(name).isEmpty()) {
                return prefix;
            }

            return NPerms.getGroupYAML().getPrefix(getRankedGroups(name).get(0));
        }
        return prefix;
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
