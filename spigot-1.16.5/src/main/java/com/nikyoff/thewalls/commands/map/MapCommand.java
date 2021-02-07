package com.nikyoff.thewalls.commands.map;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.Map;
import com.nikyoff.thewalls.utils.Localization;
import com.nikyoff.thewalls.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MapCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equals(MapCommandActions.add.name())) {
            if (args.length == 2 && sender instanceof Player) {
                Player player = (Player) sender;
                String id = args[1];
                World world = player.getWorld();

                Main.Singleton.MapManager.Add(new Map(
                        id,
                        world,
                        BarColor.WHITE,
                        5,
                        10,
                        5,
                        200,
                        100,
                        1,
                        false,
                        new Location(world, 0, 0, 0),
                        new Location(world, 0, 0, 0),
                        new ArrayList<>(),
                        new ArrayList<>())
                );

                Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandAdd") + " " + id);
                return true;
            }

            if (args.length == 3) {
                String id = args[1];
                World world = Bukkit.getWorld(args[2]);

                Main.Singleton.MapManager.Add(new Map(
                        id,
                        world,
                        BarColor.WHITE,
                        5,
                        10,
                        5,
                        200,
                        100,
                        1,
                        false,
                        new Location(world, 0, 0, 0),
                        new Location(world, 0, 0, 0),
                        new ArrayList<>(),
                        new ArrayList<>())
                );

                Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandAdd") + " " + id);
                return true;
            }

        } else if (args[0].equals(MapCommandActions.remove.name())) {
            if (args.length == 2) {
                String id = args[1];
                Main.Singleton.MapManager.Remove(id);
                Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandRemove") + " " + id);
                return true;
            }
        } else if (args[0].equals(MapCommandActions.set.name())) {
            if (args.length == 2) {
                String id = args[1];
                Main.Singleton.MapManager.SelectCurrentMap(id);
                Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSet") + " " + id);
                return true;
            }
        } else if (args[0].equals(MapCommandActions.save.name()) && Main.Singleton.MapManager.CurrentMap != null) {
            Main.Singleton.MapManager.Save();
            Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSave"));
            return true;
        } else if (args[0].equals(MapCommandActions.setup.name()) && Main.Singleton.MapManager.CurrentMap != null) {
            Main.Singleton.MapManager.CurrentMap.Setup();
            Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSetup"));
            return true;
        } else if (args[0].equals(MapCommandActions.settings.name()) && Main.Singleton.MapManager.CurrentMap != null) {
            if (args[1].equals(MapCommandSettingsActions.barColor.name())) {
                if (args.length == 3) {
                    Main.Singleton.MapManager.CurrentMap.BarColor = BarColor.valueOf(args[2].toUpperCase());
                    Main.Singleton.RoundManager.BossBarTimer.setColor(Main.Singleton.MapManager.CurrentMap.BarColor);
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsBarColor"));
                    return true;
                }
            } else if (args[1].equals(MapCommandSettingsActions.spawnLocation.name())) {
                if (args.length == 2 && sender instanceof Player) {
                    Main.Singleton.MapManager.CurrentMap.SpawnLocation = ((Player) sender).getLocation();
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsSpawnLocation"));
                    return true;
                } else if (args.length == 5 && sender instanceof Player) {
                    Main.Singleton.MapManager.CurrentMap.SpawnLocation = new Location(((Player) sender).getWorld(), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsSpawnLocation"));
                    return true;
                } else if (args.length == 6) {
                    World world = Bukkit.getWorld(args[2]);

                    if (world != null) {
                        Main.Singleton.MapManager.CurrentMap.SpawnLocation = new Location(world, Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
                        Messages.BroadcastMessage(ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsSpawnLocation"));
                        return true;
                    }
                }
            } else if (args[1].equals(MapCommandSettingsActions.roundAwakeTime.name())) {
                if (args.length == 3) {
                    Main.Singleton.MapManager.CurrentMap.RoundAwakeTime = Integer.parseInt(args[2]);
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsRoundAwakeTime"));
                    return true;
                }
            } else if (args[1].equals(MapCommandSettingsActions.maxRoundTime.name())) {
                if (args.length == 3) {
                    Main.Singleton.MapManager.CurrentMap.MaxRoundTime = Integer.parseInt(args[2]);
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsMaxRoundTime"));
                    return true;
                }
            } else if (args[1].equals(MapCommandSettingsActions.wallsFallenTime.name())) {
                if (args.length == 3) {
                    Main.Singleton.MapManager.CurrentMap.WallsFallenTime = Integer.parseInt(args[2]);
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsWallsFallenTime"));
                    return true;
                }
            } else if (args[1].equals(MapCommandSettingsActions.maxBlockPlaceY.name())) {
                if (args.length == 3) {
                    Main.Singleton.MapManager.CurrentMap.MaxBlockPlaceY = Integer.parseInt(args[2]);
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsMaxBlockPlaceY"));
                    return true;
                }
            } else if (args[1].equals(MapCommandSettingsActions.worldBorderMaxSize.name())) {
                if (args.length == 3) {
                    Main.Singleton.MapManager.CurrentMap.WorldBorderMaxSize = Double.parseDouble(args[2]);
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsWorldBorderMaxSize"));
                    return true;
                }
            } else if (args[1].equals(MapCommandSettingsActions.worldBorderMinSize.name())) {
                if (args.length == 3) {
                    Main.Singleton.MapManager.CurrentMap.WorldBorderMinSize = Double.parseDouble(args[2]);
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsWorldBorderMinSize"));
                    return true;
                }
            } else if (args[1].equals(MapCommandSettingsActions.worldBorderCanterLocation.name())) {
                if (args.length == 2 && sender instanceof Player) {
                    Main.Singleton.MapManager.CurrentMap.WorldBorderCanterLocation = ((Player) sender).getLocation();
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsWorldBorderCanterLocation"));
                    return true;
                } else if (args.length == 4 && sender instanceof Player) {
                    Main.Singleton.MapManager.CurrentMap.WorldBorderCanterLocation = new Location(((Player) sender).getWorld(), Double.parseDouble(args[2]), 0, Double.parseDouble(args[3]));
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsWorldBorderCanterLocation"));
                    return true;
                } else if (args.length == 5) {
                    World world = Bukkit.getWorld(args[2]);

                    if (world != null) {
                        Main.Singleton.MapManager.CurrentMap.WorldBorderCanterLocation = new Location(world, Double.parseDouble(args[3]), 0, Double.parseDouble(args[4]));
                        Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsWorldBorderCanterLocation"));
                        return true;
                    }
                }
            } else if (args[1].equals(MapCommandSettingsActions.canCreatePortal.name())) {
                if (args.length == 3) {
                    Main.Singleton.MapManager.CurrentMap.CanCreatePortal = args[2].equals("true");
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, Localization.GetLocalizedString("mapCommandSettingsCanCreatePortal"));
                    return true;
                }
            }
        }

        return false;
    }
}
