package com.nikyoff.thewalls.core;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.utils.FileHelper;
import com.nikyoff.thewalls.utils.Messages;
import org.bukkit.*;
import org.bukkit.World;
import org.bukkit.boss.BarColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Map {
    public String Id;
    public World MainWorld;
    public BarColor BarColor;
    public int RoundAwakeTime;
    public int MaxRoundTime;
    public int WallsFallenTime;
    public int MaxBlockPlaceY;
    public double WorldBorderMaxSize;
    public double WorldBorderMinSize;
    public boolean CanCreatePortal;
    public Location SpawnLocation;
    public Location WorldBorderCanterLocation;
    public List<Team> Teams;
    public List<Wall> Walls;

    public Map(String id, World mainWorld, BarColor barColor, int roundAwakeTime, int maxRoundTime, int wallsFallenTime, int maxBlockPlaceY, double worldBorderMaxSize, double worldBorderMinSize, boolean canCreatePortal, Location spawnLocation, Location worldBorderCanterLocation, List<Team> teams, List<Wall> walls) {
        this.Id = id;
        this.MainWorld = mainWorld;
        this.BarColor = barColor;
        this.RoundAwakeTime = roundAwakeTime;
        this.MaxRoundTime = maxRoundTime;
        this.WallsFallenTime = wallsFallenTime;
        this.MaxBlockPlaceY = maxBlockPlaceY;
        this.WorldBorderMaxSize = worldBorderMaxSize;
        this.WorldBorderMinSize = worldBorderMinSize;
        this.SpawnLocation = spawnLocation;
        this.WorldBorderCanterLocation = worldBorderCanterLocation;
        this.Teams = teams;
        this.Walls = walls;
    }

    public void Setup() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to setup map: " + this.Id);

        if (Main.Singleton.RoundManager.Started) {
            if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.DARK_RED, "Can't setup map because round started!");
            return;
        }

        Main.Singleton.RoundManager.BossBarTimer.setColor(this.BarColor);

        Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getTeams().forEach(org.bukkit.scoreboard.Team::unregister);

        this.Teams.forEach(Team::Initialize);
        this.Walls.forEach(wall -> wall.Create(false));

        World world = this.MainWorld;
        world.setSpawnLocation(this.SpawnLocation);

        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setCenter(this.WorldBorderCanterLocation);
        worldBorder.setSize(this.WorldBorderMaxSize);

//        switch (Main.Singleton.RoundManager.RoundType) {
//            case Default:
//
//                break;
//        }

        FileHelper.CreateDir(Main.Singleton.SchematicsPath + "/" + this.Id, true);
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Map setup");
    }
}
