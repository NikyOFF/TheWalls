package com.nikyoff.thewalls.commands.team;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TeamCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> strings = new ArrayList<>();
        Player player = (Player) sender;

        if (Main.Singleton.MapManager.CurrentMap != null) {
            if (args.length == 1) {
                strings = Stream.of(TeamCommandActions.values()).map(Enum::name).collect(Collectors.toList());
            }

            if (args[0].equals(TeamCommandActions.add.name())) {
                if (args.length == 3) {
                    strings = Stream.of(ChatColor.values()).map(Enum::name).collect(Collectors.toList());
                } else if (args.length == 4) {
                    strings = Stream.of(Material.values()).map(Enum::name).collect(Collectors.toList());
                }
            } else if (args[0].equals(TeamCommandActions.remove.name())) {
                if (args.length == 2) {
                    for (Team team : Main.Singleton.MapManager.CurrentMap.Teams) {
                        strings.add(team.Name);
                    }
                }
            } else if (args[0].equals(TeamCommandActions.setSpawnPoint.name())) {
                if (args.length == 2) {
                    for (Team team : Main.Singleton.MapManager.CurrentMap.Teams) {
                        strings.add(team.Name);
                    }
                } else if (args.length == 3) {
                    strings.add(String.valueOf(player.getLocation().getX()));
                } else if (args.length == 4) {
                    strings.add(String.valueOf(player.getLocation().getY()));
                } else if (args.length == 5) {
                    strings.add(String.valueOf(player.getLocation().getZ()));
                }
            } else if (args[0].equals(TeamCommandActions.join.name())) {
                if (args.length == 2) {
                    for (Team team : Main.Singleton.MapManager.CurrentMap.Teams) {
                        strings.add(team.Name);
                    }
                } else if (args.length == 3) {
                    for (Player _player : Bukkit.getOnlinePlayers()) {
                        strings.add(_player.getName());
                    }
                }
            } else if (args[0].equals(TeamCommandActions.leave.name())) {
                for (Player _player : Bukkit.getOnlinePlayers()) {
                    strings.add(_player.getName());
                }
            }
        }

        return strings;
    }
}