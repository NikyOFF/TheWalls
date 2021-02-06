package com.nikyoff.thewalls.commands.round;

import com.nikyoff.thewalls.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoundCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> strings = new ArrayList<>();

        if (Main.Singleton.MapManager.CurrentMap != null) {
            if (args.length == 1) {
                strings = Stream.of(RoundCommandActions.values()).map(Enum::name).collect(Collectors.toList());
            } else if (args.length == 2) {
                if (args[0].equals(RoundCommandActions.respawn.name())) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String playerName = player.getName();

                        if (Main.Singleton.RoundManager.DeadPlayers.containsKey(playerName)) {
                            continue;
                        }

                        strings.add(playerName);
                    }
                }
            }
        }

        return strings;
    }
}
