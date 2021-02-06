package com.nikyoff.thewalls.commands.map;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.Map;
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> strings = new ArrayList<>();

        if (Main.Singleton.MapManager.CurrentMap != null) {
            if (args.length == 1) {
                strings = Stream.of(MapCommandActions.values()).map(Enum::name).collect(Collectors.toList());
            } else if (args.length == 2) {
                if (args[0].equals(MapCommandActions.remove.name()) || args[0].equals(MapCommandActions.set.name())) {
                    for (Map map : Main.Singleton.MapManager.Maps) {
                        strings.add(map.Id);
                    }
                } else if (args[0].equals(MapCommandActions.settings.name())) {
                    strings = Stream.of(MapCommandSettingsActions.values()).map(Enum::name).collect(Collectors.toList());
                }
            } else if (args.length == 3) {
                if (args[0].equals(MapCommandActions.settings.name())) {
                    if (args[1].equals(MapCommandSettingsActions.barColor.name())) {
                        strings = Stream.of(BarColor.values()).map(Enum::name).collect(Collectors.toList());
                    }
                }
            }
        }

        return strings;
    }
}
