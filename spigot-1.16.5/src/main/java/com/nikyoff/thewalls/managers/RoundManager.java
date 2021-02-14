package com.nikyoff.thewalls.managers;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.*;
import com.nikyoff.thewalls.utils.Localization;
import com.nikyoff.thewalls.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RoundManager {
    public boolean DevMode;
    public boolean Started;
    public RoundStage RoundStage;
    public RoundType RoundType;
    public HashMap<String, String> DeadPlayers = new HashMap<>();
    public BossBar BossBarTimer;
    public double Timer;
    public double CurrentTimer;
    public double Segment;
    public double CurrentSegment;
    public int MaxPlayersInTeam = 0;

    public RoundManager() {
        this.BossBarTimer = Bukkit.createBossBar(this.GetTimerText(), BarColor.WHITE, BarStyle.SOLID);

        for(Player player : Bukkit.getOnlinePlayers())
        {
            this.BossBarTimer.addPlayer(player);
        }
    }

    public void SetCurrentTimer(double timer) {
        this.Timer = timer;
        this.CurrentTimer = this.Timer;
        this.Segment = timer / 100;
        this.CurrentSegment = this.Segment;
        this.BossBarTimer.setProgress(1);
    }

    public void UpdateTimer() {
        Main.Singleton.RoundManager.BossBarTimer.setTitle(Main.Singleton.RoundManager.GetTimerText());

        if (this.Started || this.RoundStage == com.nikyoff.thewalls.core.RoundStage.Awake) {
            this.CurrentTimer -= 0.05f;

            if (this.Timer - this.CurrentTimer > this.CurrentSegment) {
                this.CurrentSegment += this.Segment;
                this.BossBarTimer.setProgress(this.BossBarTimer.getProgress() - 0.01);
            }
        } else {
            this.BossBarTimer.setProgress(1);
        }
    }

    public String GetTimerText() {
        String text = Localization.GetLocalizedString("defaultTimerText");

        if (this.Started || this.RoundStage == com.nikyoff.thewalls.core.RoundStage.Awake) {
            String min = String.valueOf((int)(this.CurrentTimer / 60));
            String sec = String.valueOf((int)(this.CurrentTimer % 60));

            if (this.RoundStage == com.nikyoff.thewalls.core.RoundStage.Awake) {
                text = Localization.GetLocalizedString("roundAwakeTimerText");
            } else if (this.RoundStage == com.nikyoff.thewalls.core.RoundStage.Start) {
                text =  Localization.GetLocalizedString("roundStartTimerText");
            } else if (this.RoundStage == com.nikyoff.thewalls.core.RoundStage.WallsFallen) {
                text =  Localization.GetLocalizedString("roundWallsFallenTimerText");
            }

            if (sec.length() == 1) {
                sec = "0" + sec;
            }

            text = ChatColor.GRAY + text + ": " + ChatColor.WHITE + min + ":" + sec;
        }

        return text;
    }

    public void Awake() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Awake round");

        if (this.Started) {
            return;
        }

        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        if (Main.Singleton.MapManager.CurrentMap.Teams.size() < 2) {
            return;
        }

        this.RoundStage = com.nikyoff.thewalls.core.RoundStage.Awake;

        RoundAwakeEvent roundAwakeEvent = new RoundAwakeEvent(Main.Singleton.MapManager.CurrentMap);
        Bukkit.getPluginManager().callEvent(roundAwakeEvent);
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Round awake");
    }

    public void Start() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Start round");

        if (this.Started) {
            return;
        }

        this.Started = true;
        this.RoundStage = com.nikyoff.thewalls.core.RoundStage.Start;

        RoundStartEvent roundStartEvent = new RoundStartEvent(Main.Singleton.MapManager.CurrentMap);
        Bukkit.getPluginManager().callEvent(roundStartEvent);

        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Round start");
    }

    public void End(Team team) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "End round");

        if (!this.Started) {
            return;
        }

        this.RoundStage = com.nikyoff.thewalls.core.RoundStage.End;

        if (team == null) {
            Team possibleTeam = null;
            boolean equals = true;

            for (Team _team : Main.Singleton.MapManager.CurrentMap.Teams) {
                if (possibleTeam == null) {
                    possibleTeam = _team;
                }

                if (_team.Points == possibleTeam.Points) {
                    continue;
                }

                if (_team.Points > possibleTeam.Points) {
                    possibleTeam = _team;
                    equals = false;
                }
            }

            if (equals && possibleTeam != null) {
                team = null;
            } else {
                team = possibleTeam;
            }

        }

        RoundEndEvent roundEndEvent = new RoundEndEvent(Main.Singleton.MapManager.CurrentMap, team);
        Bukkit.getPluginManager().callEvent(roundEndEvent);
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Round end");
    }

    public void Stop() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Stop round");

        if (!this.Started) {
            return;
        }

        this.RoundStage = com.nikyoff.thewalls.core.RoundStage.WaitingStart;
        this.Started = false;

        RoundStopEvent roundStopEvent = new RoundStopEvent(Main.Singleton.MapManager.CurrentMap);
        Bukkit.getPluginManager().callEvent(roundStopEvent);
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Round stop");
    }
}
