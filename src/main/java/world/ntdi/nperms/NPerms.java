package world.ntdi.nperms;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import world.ntdi.nperms.commands.Nperms;
import world.ntdi.nperms.managers.GroupYAML;
import world.ntdi.nperms.managers.PermissionAttachmentManager;
import world.ntdi.nperms.managers.PlayerYAML;

public final class NPerms extends JavaPlugin {
    @Getter
    private static NPerms instance;
    @Getter
    private static PlayerYAML playerYAML;
    @Getter
    private static GroupYAML groupYAML;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        if (!getDataFolder().exists()) getDataFolder().mkdir();

        getConfig().options().copyDefaults(true);

        getServer().getPluginManager().registerEvents(new PermissionAttachmentManager(), this);

        playerYAML = new PlayerYAML();
        groupYAML = new GroupYAML();

        getCommand("nperms").setExecutor(new Nperms());
        getCommand("nperms").setTabCompleter(new Nperms());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
