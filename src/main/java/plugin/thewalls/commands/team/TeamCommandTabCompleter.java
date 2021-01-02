package plugin.thewalls.commands.team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import plugin.thewalls.Main;
import plugin.thewalls.TheWallTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TeamCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> strings = new ArrayList<>();

        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            if (args.length == 1)
            {
                return Stream.of(TeamCommandActions.values()).map(Enum::name).collect(Collectors.toList());
            }

            if (args.length > 1)
            {
                if (args[0].equals("join"))
                {
                    if (args.length == 2)
                    {
                        for (TheWallTeam _theWallTeam : Main.Singleton.TeamManager.Teams)
                        {
                            strings.add(_theWallTeam.Team.getName());
                        }

                        return strings;
                    }
                    else if (args.length == 3)
                    {
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return null;
                        }

                        for (Player _player : Bukkit.getOnlinePlayers())
                        {
                            strings.add(_player.getName());
                        }
                    }
                }

                if (args[0].equals("setSpawnPoint"))
                {
                    if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                    {
                        return null;
                    }

                    if (args.length == 2)
                    {
                        for (TheWallTeam _theWallTeam : Main.Singleton.TeamManager.Teams)
                        {
                            strings.add(_theWallTeam.Team.getName());
                        }

                        return strings;
                    }

                    if (args.length == 3)
                    {
                        strings.add(String.valueOf(Math.floor(player.getLocation().getX())));
                    }

                    if (args.length == 4)
                    {
                        strings.add(String.valueOf(Math.floor(player.getLocation().getY())));
                    }

                    if (args.length == 5)
                    {
                        strings.add(String.valueOf(Math.floor(player.getLocation().getZ())));
                    }

                    return strings;
                }

                if (args[0].equals("leave"))
                {
                    if (args.length == 2)
                    {
                        for (TheWallTeam _theWallTeam : Main.Singleton.TeamManager.Teams)
                        {
                            strings.add(_theWallTeam.Team.getName());
                        }
                    }

                    if (args.length == 3)
                    {
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return null;
                        }

                        for (Player _player : Bukkit.getOnlinePlayers())
                        {
                            strings.add(_player.getName());
                        }
                    }

                    return strings;
                }

                if (args[0].equals("add"))
                {
                    if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                    {
                        return null;
                    }

                    if (args.length == 3)
                    {
                        return Stream.of(ChatColor.values()).map(Enum::name).collect(Collectors.toList());
                    }
                    else if (args.length == 4)
                    {
                        return Stream.of(Material.values()).map(Enum::name).collect(Collectors.toList());
                    }
                }

                if (args[0].equals("remove"))
                {
                    if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                    {
                        return null;
                    }

                    if (args.length == 2)
                    {
                        for (TheWallTeam _theWallTeam : Main.Singleton.TeamManager.Teams)
                        {
                            strings.add(_theWallTeam.Team.getName());
                        }
                    }

                    return strings;
                }
            }
        }

        return null;
    }
}
