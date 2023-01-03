package world.ntdi.nperms.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.checkerframework.checker.units.qual.A;
import world.ntdi.nperms.NPerms;
import world.ntdi.nperms.util.Msg;

import java.util.ArrayList;
import java.util.List;

public class Nperms implements TabCompleter, CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) { // help, set
            String first = args[0];
            if (first.equals("help")) return help(sender);
            if (first.equals("reload")) return reload(sender);
        } else if (args.length == 2) { // set group|player

        } else if (args.length == 3) { // set group|player groupName|playerName
            if (args[0].equals("info")) return info(sender, args);
        } else if (args.length == 4) { // set group|player groupName|playerName addperm|removeperm

        } else if (args.length >= 5) {
            if (args[0].equals("set")) return set(sender, args);
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> temp = new ArrayList<>();

        if (args.length == 1) { // 1 Argument
            completions.add("help");
            completions.add("set");
            return StringUtil.copyPartialMatches(args[0], completions, temp);
        } else if (args.length == 2) { // 2 Arguments
            if (args[0].equals("set")) { // If in the set command
                completions.add("group");
                completions.add("player");
            }
            return StringUtil.copyPartialMatches(args[1], completions, temp);
        } else if (args.length == 3) { // 3 Arguments
            if (args[0].equals("set")) { // If in the set command
                if (args[1].equals("group")) { // If in the group subcommand
                    completions.addAll(NPerms.getGroupYAML().getGroups());
                } else if (args[1].equals("player")) { // If in player subcommand
                    Bukkit.getOnlinePlayers().forEach(plr -> completions.add(plr.getName()));
                }
            }
            return StringUtil.copyPartialMatches(args[2], completions, temp);
        } else if (args.length == 4) { // 4 Arguments
            if (args[0].equals("set")) { // If in the set command
                if (args[1].equals("group")) { // If in the group subcommand
                    completions.add("setplacement");
                    completions.add("setdefault");
                } else if (args[1].equals("player")) { // If in the player subcommand
                    completions.add("addgroup");
                    completions.add("removegroup");
                }
                completions.add("addperm");
                completions.add("removeperm");
                completions.add("setprefix");
                completions.add("info");
            }
            return StringUtil.copyPartialMatches(args[3], completions, temp);
        } else if (args.length == 5) {
            if (args[0].equals("set")) {
                if (args[1].equals("group")) {
                    if (args[3].equals("setplacement")) {
                        for (int i = 0; i < 100; i++) {
                            completions.add(i + "");
                        }
                    } else if (args[3].equals("setdefault")) {
                        completions.addAll(List.of("true", "false"));
                    }
                }
            }
            return StringUtil.copyPartialMatches(args[4], completions, temp);
        }


        return temp;
    }

    private boolean help(CommandSender sender) {
        sender.sendMessage("Help!");
        return true;
    }

    private boolean reload(CommandSender sender) {

        return true;
    }

    private boolean info(CommandSender sender, String[] args) {
        String type = args[1]; //group, player
        String gp = args[2]; //group name, player name
        if (type.equals("player")) {
            List<String> listInfo = new ArrayList<>(List.of(Msg.colorize("$S%s$M's Info", gp), Msg.colorize("$MPermissions:")));
            NPerms.getPlayerYAML().readPerms(gp).forEach(perm -> listInfo.add(Msg.colorize(" $M- $S%s", perm)));
            listInfo.add(Msg.colorize("$MGroups:"));
            NPerms.getPlayerYAML().readPlayerGroups(gp).forEach(group -> listInfo.add(Msg.colorize(" $M- $S%s", group)));
            listInfo.add(Msg.colorize("$MPrefix: $S%s", NPerms.getPlayerYAML().getPrefix(gp)));

            listInfo.forEach(sender::sendMessage);
            return true;
        } else if (type.equals("group")) {
            List<String> listInfo = new ArrayList<>(List.of(Msg.colorize("$S%s$M's Info", gp), Msg.colorize("$MPermissions:")));
            NPerms.getGroupYAML().readPerms(gp).forEach(perm -> listInfo.add(Msg.colorize(" $M- $S%s", perm)));
            listInfo.add(Msg.colorize("$MPrefix: $S%s", NPerms.getGroupYAML().getPrefix(gp)));
            listInfo.add(Msg.colorize("$MPlacement: $S%s", NPerms.getGroupYAML().getPlacement(gp) + ""));
            listInfo.add(Msg.colorize("$MDefault: $S%s", NPerms.getGroupYAML().isDefault(gp) + ""));

            listInfo.forEach(sender::sendMessage);
            return true;
        }
        return false;
    }

    private boolean set(CommandSender sender, String[] args) {
        String type = args[1]; //group, player
        String gp = args[2]; //group name, player name
        String command = args[3]; //add perm, remove perm,
        String classify = args[4]; // nrcore.fly ;) e.g.
        if (type.equals("player")) {
            Player p = Bukkit.getPlayer(gp);
            if (p == null) {
                sender.sendMessage(Msg.errorize("$MCannot find player: $S%s", gp));
                return true;
            }
            if (command.equals("addperm")) { // Add perm to group, Will create group if it does not exist
                NPerms.getPlayerYAML().addPermission(p.getUniqueId().toString(), classify);
                sender.sendMessage(Msg.colorize("$MAdded permission $S%s $Mto player $S%s", classify, gp));
                return true;
            }

            if (command.equals("removeperm")) {
                NPerms.getPlayerYAML().removePermission(p.getUniqueId().toString(), classify);
                sender.sendMessage(Msg.colorize("$MRemoved permission $S%s $MFrom player $S%s"), classify, gp);
                return true;
            }

            if (command.equals("setprefix")) {
                String prefix = Msg.argsBuilder(args, 4);
                NPerms.getPlayerYAML().setPrefix(p.getUniqueId().toString(), ChatColor.translateAlternateColorCodes('&', prefix));
                sender.sendMessage(Msg.colorize("$MSet the prefix of $S%s $Mto $S%s", p.getUniqueId(), prefix));
                return true;
            }

            if (command.equals("addgroup")) {
                NPerms.getPlayerYAML().addGroup(p.getUniqueId(), classify);
                sender.sendMessage(Msg.colorize("$MAdded group $S%s %Mto $S%s", classify, p.getName()));
            }

            if (command.equals("removegroup")) {
                NPerms.getPlayerYAML().removeGroup(p.getUniqueId(), classify);
                sender.sendMessage(Msg.colorize("$MRemoved group $S%s %Mfrom $S%s", classify, p.getName()));
            }

        } else if (type.equals("group")) {

            if (command.equals("addperm")) { // Add perm to group, Will create group if it does not exist
                NPerms.getGroupYAML().addPermission(gp, classify);
                sender.sendMessage(Msg.colorize("$MAdded permission $S%s $Mto group $S%s", classify, gp));
                return true;
            }

            if (command.equals("removeperm")) { // Remove perm from group
                if (NPerms.getGroupYAML().exists(gp)) {
                    NPerms.getGroupYAML().removePermission(gp, classify);
                    sender.sendMessage(Msg.colorize("$MRemoved permission $S%s $MFrom group $S%s"), classify, gp);
                    return true;
                }

                sender.sendMessage(Msg.errorize("$MCannot find group: $S%s", gp));
                return true;
            }

            if (command.equals("setplacement")) { // Set placement of group
                NPerms.getGroupYAML().setPlacement(gp, Integer.parseInt(args[4]));
                sender.sendMessage(Msg.colorize("$MSet the placement of group $S%s $Mto $S%s", gp, classify));
                return true;
            }

            if (command.equals("setdefault")) { // Set if the group should be added to all players on join, e.g. default
                NPerms.getGroupYAML().setDefault(gp, Boolean.parseBoolean(classify));
                sender.sendMessage(Msg.colorize("$MSet $S%s$M's default value to $S%s", gp, classify));
                return true;
            }

            if (command.equals("setprefix")) {
                String prefix = Msg.argsBuilder(args, 4);
                NPerms.getGroupYAML().setPrefix(gp, ChatColor.translateAlternateColorCodes('&', prefix));
                sender.sendMessage(Msg.colorize("$MSet the prefix of $S%s $Mto $S%s", gp, prefix));
                return true;
            }

        }
        return false;
    }
}
