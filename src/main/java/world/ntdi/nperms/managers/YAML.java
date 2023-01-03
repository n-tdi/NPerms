package world.ntdi.nperms.managers;

import java.util.List;

public interface YAML {
    void addPermission(String name, String perm);
    void removePermission(String name, String perm);
    List<String> readPerms(String name);
    void setPrefix(String name, String prefix);
    String getPrefix(String name);
    void saveReload();
}
