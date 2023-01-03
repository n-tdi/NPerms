package world.ntdi.nperms.managers;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import world.ntdi.nperms.NPerms;

import java.util.UUID;
import java.util.WeakHashMap;

public class PermissionAttachmentManager implements Listener {

    private static final WeakHashMap<UUID, PermissionAttachment> permissionAttachmentWeakHashMap = new WeakHashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        addPlayerPermissionAttachment(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        removePlayerPermissionAttachment(e.getPlayer());
    }

    private static void addPlayerPermissionAttachment(Player p) {
        updatePlayerPermissions(p);
    }

    private static void removePlayerPermissionAttachment(Player p) {
        permissionAttachmentWeakHashMap.remove(p.getUniqueId());
    }

    private static PermissionAttachment getPermissionAttachment(UUID uuid) {
        return permissionAttachmentWeakHashMap.get(uuid);
    }

    public static void updatePlayerPermissions(Player p) {
        permissionAttachmentWeakHashMap.put(
                p.getUniqueId(),
                p.addAttachment(NPerms.getInstance()));

        PermissionAttachment permissionAttachment = getPermissionAttachment(p.getUniqueId());
        for (String perm : NPerms.getPlayerYAML().readPerms(p.getUniqueId().toString())) {
            permissionAttachment.setPermission(perm, true);
        }
        for (String group : NPerms.getPlayerYAML().readPlayerGroups(p.getUniqueId().toString())) {
            NPerms.getGroupYAML().readPerms(group).forEach(perm -> permissionAttachment.setPermission(perm, true));
        }
    }
}
