package com.nikyoff.thewalls.core;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.utils.Localization;
import com.nikyoff.thewalls.utils.Messages;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;

public class Team {
    public String Name;
    public String DisplayName;
    public Location SpawnPointLocation;
    public ChatColor Color;
    public Material Material;
    public org.bukkit.scoreboard.Team ScoreboardTeam;

    public boolean IsLost;
    public boolean IsSpawned;
    public int PlayersCount;
    public int LivePlayersCount;
    public int Points;

    public Team(String name, Location spawnPointLocation, ChatColor color, Material material) {
        this.Name = name;
        this.DisplayName = color + name;
        this.SpawnPointLocation = spawnPointLocation;
        this.Color = color;
        this.Material = material;
        this.Initialize();
    }

    public void Spawn() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to spawn team: " + this.Name);
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        if (!Main.Singleton.RoundManager.Started) {
            return;
        }

        if (this.IsLost) {
            return;
        }

        if (this.SpawnPointLocation == null) {
            this.Lost();
        } else {
            this.ScoreboardTeam.getEntries().forEach(_entry -> {
                if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to spawn team entry: " + _entry);
                Player player = Bukkit.getPlayer(_entry);

                if (player != null)
                {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.getInventory().clear();
                    player.teleport(this.SpawnPointLocation);
                    player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                    player.setFoodLevel(20);
                    this.LivePlayersCount++;
                    if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Entry spawned");
                }
            });

            this.IsSpawned = true;
            if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Team spawn");
        }
    }

    public void Check() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to check team: " + this.Name);
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        if (!Main.Singleton.RoundManager.Started) {
            return;
        }

        if (this.IsLost) {
            return;
        }

        if (!this.IsSpawned || this.PlayersCount < 1 || this.LivePlayersCount < 1) {
            this.Lost();
        }
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Team checked");
    }

    public void Reset() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to reset team: " + this.Name);
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        if (Main.Singleton.RoundManager.Started) {
            this.IsLost = false;
            Main.Singleton.TeamManager.LostTeamCount--;
            this.Spawn();
        } else {
            this.ScoreboardTeam.getEntries().forEach(this::Leave);
            this.IsLost = false;
            this.IsSpawned = false;
            this.PlayersCount = 0;
            this.LivePlayersCount = 0;
            this.Points = 0;
        }
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Team reset");
    }

    public void Lost() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to lost team: " + this.Name);
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        if (!Main.Singleton.RoundManager.Started) {
            return;
        }

        Main.Singleton.TeamManager.LostTeamCount++;
        this.IsLost = true;
        this.LivePlayersCount = 0;
        this.Points = 0;

        this.ScoreboardTeam.getEntries().forEach(_entry -> {
            Player player = Bukkit.getPlayer(_entry);

            if (player != null && !Main.Singleton.RoundManager.DeadPlayers.containsKey(_entry))
            {
                player.setGameMode(GameMode.ADVENTURE);
                player.getInventory().clear();
                player.teleport(Main.Singleton.MapManager.CurrentMap.SpawnLocation);
                player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                player.setFoodLevel(20);
            }
        });
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Team lost");
    }

    public void Join(String entry) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to join team: " + this.Name + ", entry: " + entry);
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        Player player = Bukkit.getPlayer(entry);
        org.bukkit.scoreboard.Team scoreboardTeam = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getEntryTeam(entry);
        Team playerTeam;

        if (player == null) {
            return;
        }

        if (this.PlayersCount >= Main.Singleton.RoundManager.GetMaxPlayersInTeam())
        {
            return;
        }

        if (scoreboardTeam != null) {
            playerTeam = Main.Singleton.TeamManager.Get(scoreboardTeam.getName());

            if (playerTeam != null) {
                Main.Singleton.TeamManager.Leave(entry, playerTeam);
            }
        }

        this.ScoreboardTeam.addEntry(entry);
        this.PlayersCount++;

        TeamJoinEvent teamJoinEvent = new TeamJoinEvent(player, this);
        Bukkit.getPluginManager().callEvent(teamJoinEvent);
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Join");
    }

    public void Leave(String entry) {

        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        org.bukkit.scoreboard.Team scoreboardTeam = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getEntryTeam(entry);

        if (scoreboardTeam == null) {
            return;
        }

        this.ScoreboardTeam.removeEntry(entry);
        this.PlayersCount--;

        TeamLeaveEvent teamLeaveEvent = new TeamLeaveEvent(entry, this);
        Bukkit.getPluginManager().callEvent(teamLeaveEvent);
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Leaving");
    }

    public Player GetAnyLivePlayer() {
        for (String entry : this.ScoreboardTeam.getEntries()) {
            Player player = Bukkit.getPlayer(entry);

            if (player != null && !Main.Singleton.RoundManager.DeadPlayers.containsKey(entry)) {
                return player;
            }
        }

        return null;
    }

    public void Kill(String entry, String deathMessage, boolean killing) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to kill entry: " + entry + ", in team:" + this.Name);
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        if (!Main.Singleton.RoundManager.Started) {
            return;
        }

        if (!this.ScoreboardTeam.hasEntry(entry)) {
            return;
        }

        Player player = Bukkit.getPlayer(entry);

        this.LivePlayersCount--;
        this.Points -= 3;
        Main.Singleton.RoundManager.DeadPlayers.put(entry, deathMessage);

        if (player != null) {
            if (killing) {
                player.setHealth(0.0);
            }
            player.getInventory().clear();
            player.teleport(Main.Singleton.MapManager.CurrentMap.SpawnLocation);
            player.setGameMode(GameMode.SPECTATOR);
        }

        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Killed");
    }

    public void Respawn(String entry) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to respawn entry: " + entry + ", in team:" + this.Name);
        if (Main.Singleton.MapManager.CurrentMap == null) {
            Messages.SendConsoleMessage(ChatColor.BLUE, "Main.Singleton.MapManager.CurrentMap == null");
            return;
        }

        if (!Main.Singleton.RoundManager.Started) {
            Messages.SendConsoleMessage(ChatColor.BLUE, "!Main.Singleton.RoundManager.Started");
            return;
        }

        Main.Singleton.RoundManager.DeadPlayers.remove(entry);

        Player player = Bukkit.getPlayer(entry);
        Player teammate = this.GetAnyLivePlayer();
        Location location = null;

        if (player == null) {
            Messages.SendConsoleMessage(ChatColor.BLUE, "player == null");
            return;
        }

        if (teammate == null) {
            if (Main.Singleton.RoundManager.RoundStage == RoundStage.Start)
            {
                location = this.SpawnPointLocation;
            }
            else
            {
                Messages.SendConsoleMessage(ChatColor.BLUE, "if teammate == null && Main.Singleton.RoundManager.RoundStage != RoundStage.Start");
                return;
            }
        }
        else
        {
            location = teammate.getLocation();
        }

        if (this.IsLost) {
            this.IsLost = false;
            Main.Singleton.TeamManager.LostTeamCount--;
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.teleport(location);
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        player.setFoodLevel(20);
        this.LivePlayersCount++;
        Messages.BroadcastMessage(ChatColor.LIGHT_PURPLE, entry + " " + Localization.GetLocalizedString("respawnPlayer"));
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Respawned");
    }

    public void Initialize() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to initialize team: " + this.Name);
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.getTeam(this.Name);

        if (scoreboardTeam == null) {
            scoreboardTeam = scoreboard.registerNewTeam(this.Name);
        }

        scoreboardTeam.setColor(this.Color);
        this.ScoreboardTeam = scoreboardTeam;
        this.IsLost = false;
        this.IsSpawned = false;
        this.PlayersCount = 0;
        this.LivePlayersCount = 0;
        this.Points = 0;

        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Team initialized");
    }
}