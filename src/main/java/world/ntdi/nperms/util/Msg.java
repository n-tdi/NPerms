package world.ntdi.nperms.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.util.Formatter;

@UtilityClass
public class Msg {
    public String colorize(String message) {
        return message.replaceAll("\\$M", ChatColor.AQUA.toString()).replaceAll("\\$S", ChatColor.DARK_AQUA.toString());
    }

    public String colorize(String message, Object... args) {
        return colorize(format(message, args));
    }

    public String errorize(String message) {
        return message.replaceAll("\\$M", ChatColor.AQUA.toString()).replaceAll("\\$S", ChatColor.DARK_AQUA.toString());
    }

    public String errorize(String message, Object... args) {
        return errorize(format(message, args));
    }

    public String format(String format, Object... args) {
        return new Formatter().format(format, args).toString();
    }

    public String argsBuilder(String[] args, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString();
    }
}
