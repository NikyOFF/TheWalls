package com.nikyoff.thewalls.commands.wall;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.Wall;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WallCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> strings = new ArrayList<>();

        if (Main.Singleton.MapManager.CurrentMap != null) {
            if (args.length == 1) {
                strings = Stream.of(WallCommandActions.values()).map(Enum::name).collect(Collectors.toList());
            }

            if (args[0].equals(WallCommandActions.add.name())) {
                if (sender instanceof Player && ((Player) sender).getTargetBlockExact(16) != null) {
                    if (args.length == 3) {
                        strings.add(String.valueOf(((Player) sender).getTargetBlockExact(16).getX()));
                    } else if (args.length == 4) {
                        strings.add(String.valueOf(((Player) sender).getTargetBlockExact(4).getY()));
                    } else if (args.length == 5) {
                        strings.add(String.valueOf(((Player) sender).getTargetBlockExact(4).getZ()));
                    } else if (args.length == 6) {
                        strings.add(String.valueOf(((Player) sender).getTargetBlockExact(4).getX()));
                    } else if (args.length == 7) {
                        strings.add(String.valueOf(((Player) sender).getTargetBlockExact(4).getY()));
                    } else if (args.length == 8) {
                        strings.add(String.valueOf(((Player) sender).getTargetBlockExact(4).getZ()));
                    }
                }
            } else if (args[0].equals(WallCommandActions.remove.name())) {
                for (Wall wall : Main.Singleton.MapManager.CurrentMap.Walls) {
                    strings.add(wall.Id);
                }
            } else if (args[0].equals(WallCommandActions.create.name())) {
                if (args.length == 2) {
                    for (Wall wall : Main.Singleton.MapManager.CurrentMap.Walls) {
                        strings.add(wall.Id);
                    }
                } else if (args.length == 3) {
                    strings.add("true");
                    strings.add("false");
                }
            } else if (args[0].equals(WallCommandActions.save.name())) {
                if (args.length == 2) {
                    for (Wall wall : Main.Singleton.MapManager.CurrentMap.Walls) {
                        strings.add(wall.Id);
                    }
                } else if (args.length == 3) {
                    strings.add("true");
                    strings.add("false");
                }
            }
        }

        return strings;
    }
}
