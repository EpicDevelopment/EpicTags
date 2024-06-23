package gg.minecrush.epictags.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public class color {
    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String c(String message) {
        message = message.replace("%nl%", "\n");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, ChatColor.of(color) + "");
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static ArrayList<String> cc(String... texts) {
        ArrayList<String> a = new ArrayList<>();
        for (String text : texts)
            a.add(c(text));
        return a;
    }

    public static String noColor(String message) {
        return ChatColor.stripColor(c(message));
    }

    public static String hex(String hexCode) {
        try {
            return ChatColor.of("#" + hexCode).toString();
        } catch (Exception e) {
            return ""; // or handle the exception as needed
        }
    }

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
