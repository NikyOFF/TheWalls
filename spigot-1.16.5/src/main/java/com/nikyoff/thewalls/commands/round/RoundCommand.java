package com.nikyoff.thewalls.commands.round;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.Map;
import com.nikyoff.thewalls.core.Team;
import com.nikyoff.thewalls.core.Wall;
import com.nikyoff.thewalls.utils.Adapter;
import com.nikyoff.thewalls.utils.Localization;
import com.nikyoff.thewalls.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RoundCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Main.Singleton.MapManager.CurrentMap != null) {
            if (args[0].equals(RoundCommandActions.start.name())) {
                Main.Singleton.RoundManager.Awake();
                return true;
            } else if (args[0].equals(RoundCommandActions.stop.name())) {
                Main.Singleton.RoundManager.Stop();
                return true;
            } else if (args[0].equals(RoundCommandActions.check.name())) {
                Main.Singleton.TeamManager.CheckTeams();
                return true;
            } else if (args[0].equals(RoundCommandActions.respawn.name())) {
                if (args.length == 1 && sender instanceof Player) {
                    Main.Singleton.TeamManager.Respawn((Player) sender);
                    return true;
                } else if (args.length == 2) {
                    Player player = Bukkit.getPlayer(args[1]);

                    if (player != null) {
                        Main.Singleton.TeamManager.Respawn(player);
                        return true;
                    }
                }
            } else if (args[0].equals(RoundCommandActions.devMode.name())) {
                Main.Singleton.RoundManager.DevMode = !Main.Singleton.RoundManager.DevMode;
                if (sender instanceof Player) {
                    Messages.SendMessage(((Player) sender), ChatColor.GOLD, Localization.GetLocalizedString("roundCommandDevMove") + " - " + Main.Singleton.RoundManager.DevMode);
                } else {
                    Messages.BroadcastMessage(ChatColor.GOLD, Localization.GetLocalizedString("roundCommandDevMove") + " - " + Main.Singleton.RoundManager.DevMode);
                }
                return true;
            } else if (args[0].equals(RoundCommandActions.debugRound.name())) {
                Messages.SendMessage((Player) sender, ChatColor.GREEN, ChatColor.GOLD + "-----Round-----");
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Dev mode]: " + ChatColor.WHITE + Main.Singleton.RoundManager.DevMode);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Started]: " + ChatColor.WHITE + Main.Singleton.RoundManager.Started);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[RoundStage]: " + ChatColor.WHITE + Main.Singleton.RoundManager.RoundStage);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Timer]: " + ChatColor.WHITE + Main.Singleton.RoundManager.Timer);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[CurrentTimer]: " + ChatColor.WHITE + Main.Singleton.RoundManager.CurrentTimer);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Segment]: " + ChatColor.WHITE + Main.Singleton.RoundManager.Segment);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[CurrentSegment]: " + ChatColor.WHITE + Main.Singleton.RoundManager.CurrentSegment);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[MaxPlayersInTeam]: " + ChatColor.WHITE + Main.Singleton.RoundManager.GetMaxPlayersInTeam());

                return true;
            } else if (args[0].equals(RoundCommandActions.debugMap.name())) {
                Map currentMap = Main.Singleton.MapManager.CurrentMap;
                Messages.SendMessage((Player) sender, ChatColor.GREEN, ChatColor.AQUA + "-----Current map-----");
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "Current map id: " + ChatColor.WHITE + currentMap.Id);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Current main world name]: " + ChatColor.WHITE + currentMap.MainWorld.getName());
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Spawn location]: " + ChatColor.WHITE + Adapter.LocationToString(currentMap.SpawnLocation));
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Can create portal]: " + ChatColor.WHITE + currentMap.CanCreatePortal);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Round awake time]: " + ChatColor.WHITE + currentMap.RoundAwakeTime);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Max round time]: " + ChatColor.WHITE + currentMap.MaxRoundTime);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Walls fallen time]: " + ChatColor.WHITE + currentMap.WallsFallenTime);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Max block place by Y]: " + ChatColor.WHITE + currentMap.MaxBlockPlaceY);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[World border max size]: " + ChatColor.WHITE + currentMap.WorldBorderMaxSize);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[World border min size]: " + ChatColor.WHITE + currentMap.WorldBorderMinSize);
                Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[World border center]: " + ChatColor.WHITE + Adapter.LocationToString(currentMap.WorldBorderCanterLocation));
                return true;
            } else if (args[0].equals(RoundCommandActions.debugTeams.name())) {
                Map currentMap = Main.Singleton.MapManager.CurrentMap;
                Messages.SendMessage((Player) sender, ChatColor.GREEN, ChatColor.GOLD + "-----Teams-----");
                Messages.SendMessage((Player) sender, ChatColor.GREEN, ChatColor.GOLD + "" + Main.Singleton.TeamManager.LostTeamCount + "/" + Main.Singleton.MapManager.CurrentMap.Teams.size());

                for (Team _team : currentMap.Teams) {
                    Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, "Team: " + _team.DisplayName);
                    Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[SpawnPointLocation]: " + ChatColor.WHITE + Adapter.LocationToString(_team.SpawnPointLocation));
                    Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Color]: " + ChatColor.WHITE + " " + _team.Color.name());
                    Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Material]: " + ChatColor.WHITE + _team.Material.toString());
                    Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[ScoreboardTeam]: " + ChatColor.WHITE + _team.ScoreboardTeam.getName());
                    Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[IsLost]: " + ChatColor.WHITE + _team.IsLost);
                    Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[IsSpawned]: " + ChatColor.WHITE + _team.IsSpawned);
                    Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[PlayersCount]: " + ChatColor.WHITE + _team.PlayersCount);
                    Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[LivePlayersCount]: " + ChatColor.WHITE + _team.LivePlayersCount);
                    Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Points]: " + ChatColor.WHITE + _team.Points);
                }
                return true;
            } else if (args[0].equals(RoundCommandActions.debugWalls.name())) {
                Map currentMap = Main.Singleton.MapManager.CurrentMap;
                Messages.SendMessage((Player) sender, ChatColor.GREEN, ChatColor.GOLD + "-----Walls-----");

                for (Wall _wall : currentMap.Walls) {
                    Messages.SendMessage((Player) sender, ChatColor.GREEN, "Wall: " + _wall.Id);
                    Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Pos0]: " + ChatColor.WHITE + Adapter.LocationToString(_wall.Pos0));
                    Messages.SendMessage((Player) sender, ChatColor.DARK_BLUE, ChatColor.GRAY + "[Pos1]: " + ChatColor.WHITE + Adapter.LocationToString(_wall.Pos0));
                }
                return true;
            }
        }

        return false;
    }
}
