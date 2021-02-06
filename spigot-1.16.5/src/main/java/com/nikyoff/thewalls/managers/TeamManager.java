package com.nikyoff.thewalls.managers;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.Team;
import com.nikyoff.thewalls.core.TeamJoinEvent;
import com.nikyoff.thewalls.core.TeamLeaveEvent;
import com.nikyoff.thewalls.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TeamManager {
    public int LostTeamCount;

    public Team Get(Team team) {
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return null;
        }

        for (Team _team : Main.Singleton.MapManager.CurrentMap.Teams) {
            if (_team.Name.equals(team.Name)) {
                return _team;
            }
        }

        return null;
    }

    public Team Get(String name) {
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return null;
        }

        for (Team _team : Main.Singleton.MapManager.CurrentMap.Teams) {
            if (_team.Name.equals(name)) {
                return _team;
            }
        }

        return null;
    }

    public Team GetByDisplayName(String name) {
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return null;
        }

        for (Team _team : Main.Singleton.MapManager.CurrentMap.Teams) {
            if (_team.DisplayName.equals(name)) {
                return _team;
            }
        }

        return null;
    }

    public Team GetByEntry(String entry) {
        if (Main.Singleton.MapManager.CurrentMap != null) {
            for (Team team : Main.Singleton.MapManager.CurrentMap.Teams) {
                if (team.ScoreboardTeam.hasEntry(entry)) {
                    return team;
                }
            }
        }

        return null;
    }

    public void Add(Team team) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to add team: " + team.Name);
        if (Main.Singleton.RoundManager.Started) {
            return;
        }

        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        Team possibleTeam = Get(team);

        if (possibleTeam != null) {
            return;
        }

        Main.Singleton.MapManager.CurrentMap.Teams.add(team);
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Added team: " + team.Name);
    }

    public void Remove(Team team) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to remove team: " + team.Name);
        if (Main.Singleton.RoundManager.Started) {
            return;
        }

        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        for (Team _team : Main.Singleton.MapManager.CurrentMap.Teams) {
            if (_team == team) {
                Main.Singleton.MapManager.CurrentMap.Teams.remove(team);
            }
        }
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Removed team");
    }

    public void Remove(String name) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to remove team: " + name);
        if (Main.Singleton.RoundManager.Started) {
            return;
        }

        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        Team possibleTeam = Get(name);

        if (possibleTeam == null) {
            return;
        }

        for (Team _team : Main.Singleton.MapManager.CurrentMap.Teams) {
            if (_team == possibleTeam) {
                Main.Singleton.MapManager.CurrentMap.Teams.remove(possibleTeam);
            }
        }
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Added team");
    }

    public void Join(String entry, Team team) {
        Player player = Bukkit.getPlayer(entry);
        org.bukkit.scoreboard.Team scoreboardTeam = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getEntryTeam(entry);
        Team playerTeam = null;

        if (player == null) {
            return;
        }

        if (scoreboardTeam != null) {
            playerTeam = this.Get(scoreboardTeam.getName());
            Leave(entry, playerTeam);
        }

        team.ScoreboardTeam.addEntry(entry);
        team.PlayersCount++;

        TeamJoinEvent teamJoinEvent = new TeamJoinEvent(player, team);
        Bukkit.getPluginManager().callEvent(teamJoinEvent);
    }

    public void Leave(String entry, Team team) {
        Player player = Bukkit.getPlayer(entry);
        org.bukkit.scoreboard.Team scoreboardTeam = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getEntryTeam(entry);

        if (scoreboardTeam == null || player == null || !team.ScoreboardTeam.getName().equals(scoreboardTeam.getName())) {
            return;
        }

        team.ScoreboardTeam.removeEntry(entry);
        team.PlayersCount--;

        TeamLeaveEvent teamLeaveEvent = new TeamLeaveEvent(player, team);
        Bukkit.getPluginManager().callEvent(teamLeaveEvent);
    }

    public void Leave(String entry) {
        Player player = Bukkit.getPlayer(entry);
        org.bukkit.scoreboard.Team scoreboardTeam = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getEntryTeam(entry);
        Team team;

        if (scoreboardTeam == null || player == null) {
            return;
        }
        else {
            team = this.Get(scoreboardTeam.getName());

            if (team == null) {
                return;
            }
        }

        team.ScoreboardTeam.removeEntry(entry);
        team.PlayersCount--;

        TeamLeaveEvent teamLeaveEvent = new TeamLeaveEvent(player, team);
        Bukkit.getPluginManager().callEvent(teamLeaveEvent);
    }

    public void SpawnTeams() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to spawn teams");
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        Main.Singleton.MapManager.CurrentMap.Teams.forEach(Team::Spawn);
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Spawn teams");
    }

    public void ResetTeams() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to reset teams");
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        Main.Singleton.MapManager.CurrentMap.Teams.forEach(Team::Reset);
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Reset teams");
    }

    public void CheckTeams() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to check teams");
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        if (!Main.Singleton.RoundManager.Started) {
            return;
        }

        if (Main.Singleton.MapManager.CurrentMap.Teams.size() == 0) {
            Main.Singleton.RoundManager.Stop();
        }

        Main.Singleton.MapManager.CurrentMap.Teams.forEach(Team::Check);

        if (LostTeamCount == Main.Singleton.MapManager.CurrentMap.Teams.size() - 1) {
            for (Team team : Main.Singleton.MapManager.CurrentMap.Teams) {
                if (!team.IsLost) {
                    Main.Singleton.RoundManager.End(team);
                }
            }
        } else if (LostTeamCount == Main.Singleton.MapManager.CurrentMap.Teams.size()) {
            Main.Singleton.RoundManager.End(null);
        }
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Check teams");
    }

    public void Respawn(Player player) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to respawn player: " + player.getName());
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        if (!Main.Singleton.RoundManager.Started) {
            return;
        }

        String playerName = player.getName();

        if (!Main.Singleton.RoundManager.DeadPlayers.containsKey(playerName)) {
            return;
        }

        org.bukkit.scoreboard.Team scoreboardTeam = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getEntryTeam(playerName);

        if (scoreboardTeam == null) {
            return;
        }

        Team team = this.Get(scoreboardTeam.getName());

        if (team == null) {
            return;
        }

        team.Respawn(playerName);
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Respawn player");
    }
}