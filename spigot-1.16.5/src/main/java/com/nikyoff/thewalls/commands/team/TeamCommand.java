package com.nikyoff.thewalls.commands.team;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.Team;
import com.nikyoff.thewalls.items.TeamSelector;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Main.Singleton.MapManager.CurrentMap != null) {
            if (args[0].equals(TeamCommandActions.add.name())) {
                if (args.length == 4 && sender instanceof Player) {
                    String name = args[1];
                    Location spawnPointLocation = ((Player) sender).getLocation();
                    ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                    Material material = Material.valueOf(args[3].toUpperCase());

                    Main.Singleton.TeamManager.Add(new Team(name, spawnPointLocation, color, material));
                    return true;
                } else if (args.length == 4) {
                    String name = args[1];
                    ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                    Material material = Material.valueOf(args[3].toUpperCase());

                    Main.Singleton.TeamManager.Add(new Team(name, null, color, material));
                }
            } else if (args[0].equals(TeamCommandActions.remove.name())) {
                if (args.length == 2 && sender instanceof Player) {
                    String name = args[1];
                    Main.Singleton.TeamManager.Remove(name);
                    return true;
                }
            } else if (args[0].equals(TeamCommandActions.setSpawnPoint.name())) {
                if (sender instanceof Player) {
                    if (args.length == 2) {
                        String name = args[1];
                        Location location = ((Player) sender).getLocation();

                        Team team = Main.Singleton.TeamManager.Get(name);

                        if (team != null) {
                            team.SpawnPointLocation = location;
                            return true;
                        }
                    } else if (args.length == 5) {
                        String name = args[1];
                        Location location = new Location(((Player) sender).getWorld(), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));

                        Team team = Main.Singleton.TeamManager.Get(name);

                        if (team != null) {
                            team.SpawnPointLocation = location;
                            return true;
                        }
                    }
                }


            } else if (args[0].equals(TeamCommandActions.book.name())) {
                if (sender instanceof Player) {
                    TeamSelector.AddItem(((Player) sender).getPlayer());
                    return true;
                }
            } else if (args[0].equals(TeamCommandActions.join.name())) {
                if (args.length == 2 && sender instanceof Player) {
                    String teamName = args[1];
                    Player player = ((Player) sender).getPlayer();

                    Team team = Main.Singleton.TeamManager.Get(teamName);

                    if (team != null && player != null) {
                        Main.Singleton.TeamManager.Join(player.getName(), team);
                        return true;
                    }
                } else if (args.length == 3) {
                    String teamName = args[1];
                    String name = args[2];

                    Team team = Main.Singleton.TeamManager.Get(teamName);

                    if (team != null) {
                        Main.Singleton.TeamManager.Join(name, team);
                        return true;
                    }
                }
            } else if (args[0].equals(TeamCommandActions.leave.name())) {
                if (args.length == 2) {
                    String name = args[1];

                    Main.Singleton.TeamManager.Leave(name);
                    return true;
                }
            }
        }

        return false;
    }
}