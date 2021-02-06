package com.nikyoff.thewalls.utils;

import com.nikyoff.thewalls.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Messages
{
    public static void SendConsoleMessage(ChatColor color, String message)  {
        Main.Singleton.getServer().getConsoleSender().sendMessage(ChatColor.WHITE + "[" + color + "TheWalls" + ChatColor.WHITE + "]: " + message);
    }

    public static void SendMessage(Player player, ChatColor color, String message) {
        player.sendMessage(ChatColor.WHITE + "[" + color + "TheWalls" + ChatColor.WHITE + "]: " + message);
    }

    public static void BroadcastMessage(ChatColor color, String message)
    {
        Bukkit.broadcastMessage(ChatColor.WHITE + "[" + color + "TheWalls" + ChatColor.WHITE + "]: " + message);
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

    public static void BroadcastTimer(int from, int to, String text, boolean hasTitle)
    {
        new BukkitRunnable() {
            int counter = from;

            @Override
            public void run() {
                if (from > to) {
                    if (counter <= to) {
                        this.cancel();
                    }

                    if (hasTitle) {
                        BroadcastTitle(String.valueOf(counter), "", 0, 20, 0);
                    }

                    BroadcastMessage(ChatColor.GREEN, counter + " - " + text);

                    counter--;
                }
                else {
                    this.cancel();
                }
            }

        }.runTaskTimer(Main.Singleton, 0, 20);
    }
}
