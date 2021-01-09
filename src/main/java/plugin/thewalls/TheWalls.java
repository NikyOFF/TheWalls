package plugin.thewalls;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TheWalls {
    public static String CorrectMinutesText(int min)
    {
        Configuration configuration = Main.Singleton.getConfig();

        String text = configuration.getString("localization.minutes");

        if (configuration.getString("localization.language") == "RU" && min < 5)
        {
            text = "минуты";
        }

        return min + " " + text;
    }

    public static void SendConsoleTheWallsMessage(ChatColor color, String message)
    {
        Main.Singleton.getServer().getConsoleSender().sendMessage(color + "[TheWalls]: " + ChatColor.WHITE + message);
    }

    public static void BroadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut)
    {
        Bukkit.getServer().getOnlinePlayers().forEach(_player -> {
            _player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        });
    }

    public static void BroadcastSound(Sound sound, float volume, float pitch)
    {
        Bukkit.getServer().getOnlinePlayers().forEach(_player -> {
            _player.playSound(_player.getLocation(), sound, volume, pitch);
        });
    }

    public static void SendTheWallsMessage(Player player, ChatColor color, String message)
    {
        player.sendMessage(color + "[TheWalls]: " + ChatColor.WHITE + message);
    }

    public static void BroadcastTheWallsMessage(ChatColor color, String message)
    {
        Bukkit.broadcastMessage(color + "[TheWalls]: " + ChatColor.WHITE + message);
    }

    public static void BroadcastTimer(int from, int to, String text, boolean hasTitle)
    {
        new BukkitRunnable() {
            //5 //0

            int counter = from;

            @Override
            public void run()
            {
                if (from > to)
                {
                    if (counter <= to)
                    {
                        this.cancel();
                    }

                    if (hasTitle)
                    {
                        BroadcastTitle(String.valueOf(counter), "", 0, 20, 0);
                    }

                    BroadcastTheWallsMessage(ChatColor.GREEN, counter + " - " + text);

                    counter--;
                }
                else
                {
                    this.cancel();
                }
            }

        }.runTaskTimer(Main.Singleton, 0, 20);
    }
}
