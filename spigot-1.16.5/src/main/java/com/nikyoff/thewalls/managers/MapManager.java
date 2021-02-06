package com.nikyoff.thewalls.managers;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.Map;
import com.nikyoff.thewalls.core.Team;
import com.nikyoff.thewalls.core.Wall;
import com.nikyoff.thewalls.utils.Adapter;
import com.nikyoff.thewalls.utils.Messages;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapManager {
    public List<Map> Maps = new ArrayList<>();
    public Map CurrentMap;

    public MapManager() {
        this.Load();
    }

    public void Save() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying save maps");

        FileConfiguration configuration = Main.Singleton.getConfig();
        int mapsSize = this.Maps.size();
        if (!configuration.isConfigurationSection("maps")) {
            configuration.createSection("maps");
        }

        if (this.CurrentMap != null) {
            configuration.set("currentMap", this.CurrentMap.Id);
        } else {
            configuration.set("currentMap", "null");
        }

        for (int i = 0; i < mapsSize; i++) {
            Map map = this.Maps.get(i);
            int teamsSize = map.Teams.size();
            int wallsSize = map.Walls.size();
            String path = "maps." + i;

            configuration.set("mapsSize", mapsSize);
            configuration.createSection(path);

            configuration.set(path + ".id", map.Id);
            configuration.set(path + ".mainWorld", map.MainWorld.getName());
            configuration.set(path + ".barColor", map.BarColor.name());
            configuration.set(path + ".roundAwakeTime", map.RoundAwakeTime);
            configuration.set(path + ".maxRoundTime", map.MaxRoundTime);
            configuration.set(path + ".wallsFallenTime", map.WallsFallenTime);
            configuration.set(path + ".maxBlockPlaceY", map.MaxBlockPlaceY);
            configuration.set(path + ".worldBorderMaxSize", map.WorldBorderMaxSize);
            configuration.set(path + ".worldBorderMinSize", map.WorldBorderMinSize);
            configuration.set(path + ".canCreatePortal", map.CanCreatePortal);
            configuration.set(path + ".spawnLocation", map.SpawnLocation);
            configuration.set(path + ".worldBorderCanterLocation", map.WorldBorderCanterLocation);
            configuration.set(path + ".teamsCount", teamsSize);
            configuration.createSection(path + ".teams");

            for (int t = 0; t < teamsSize; t++) {
                Team team = map.Teams.get(t);
                String teamPath = path + ".teams." + t;

                configuration.set(teamPath + ".name", team.Name);
                configuration.set(teamPath + ".spawnPointLocation", team.SpawnPointLocation);
                configuration.set(teamPath + ".color", team.Color.name());
                configuration.set(teamPath + ".material", team.Material.name());
            }

            configuration.set(path + ".wallsCount", wallsSize);
            configuration.createSection(path + ".walls");

            for (int w = 0; w < wallsSize; w++) {
                Wall wall = map.Walls.get(w);
                String wallPath = path + ".walls." + w;

                configuration.set(wallPath + ".id", wall.Id);
                configuration.set(wallPath + ".pos0", wall.Pos0);
                configuration.set(wallPath + ".pos1", wall.Pos1);
            }
        }

        Main.Singleton.saveConfig();
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Save maps");
    }

    public void Load() {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying load maps");

        FileConfiguration configuration = Main.Singleton.getConfig();
        int mapsSize = configuration.getInt("mapsSize");

        for (int i = 0; i < mapsSize; i++) {
            String path = "maps." + i;
            String id = configuration.getString(path + ".id");
            World world = Bukkit.getWorld(Objects.requireNonNull(configuration.getString(path + ".mainWorld")));
            BarColor barColor = BarColor.valueOf(configuration.getString(path + ".barColor"));
            int roundAwakeTime = configuration.getInt(path + ".roundAwakeTime");
            int maxRoundTime = configuration.getInt(path + ".maxRoundTime");
            int wallsFallenTime = configuration.getInt(path + ".wallsFallenTime");
            int maxBlockPlaceY = configuration.getInt(path + ".maxBlockPlaceY");
            double worldBorderMaxSize = configuration.getDouble(path + ".worldBorderMaxSize");
            double worldBorderMinSize = configuration.getDouble(path + ".worldBorderMinSize");
            boolean canCreatePortal = configuration.getBoolean(path + ".canCreatePortal");
            Location spawnLocation = configuration.getLocation(path + ".spawnLocation");
            Location worldBorderCanterLocation = configuration.getLocation(path + ".worldBorderCanterLocation");

            int teamsCount = configuration.getInt(path + ".teamsCount");
            List<Team> teams = new ArrayList<>();

            for (int t = 0; t < teamsCount; t++) {
                String teamPath = path + ".teams." + t;

                String name = configuration.getString(teamPath + ".name");
                Location spawnPointLocation = configuration.getLocation(teamPath + ".spawnPointLocation");
                ChatColor color = ChatColor.valueOf(configuration.getString(teamPath + ".color"));
                Material material = Material.valueOf(configuration.getString(teamPath + ".material"));

                teams.add(new Team(name, spawnPointLocation, color, material));
            }

            int wallsCount = configuration.getInt(path + ".wallsCount");
            List<Wall> walls = new ArrayList<>();

            for (int w = 0; w < wallsCount; w++) {
                String wallPath = path + ".walls." + w;

                String wallId = configuration.getString(wallPath + ".id");
                Location pos0 = configuration.getLocation(wallPath + ".pos0");
                Location pos1 = configuration.getLocation(wallPath + ".pos1");

                if (pos0 == null || pos1 == null) {
                    if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.DARK_RED, "Map: " + id + " - has breaking location points!");
                } else {
                    walls.add(new Wall(wallId, pos0, pos1));
                }
            }

            this.Add(new Map(id, world, barColor, roundAwakeTime, maxRoundTime, wallsFallenTime, maxBlockPlaceY, worldBorderMaxSize, worldBorderMinSize, canCreatePortal, spawnLocation, worldBorderCanterLocation, teams, walls));
            if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Map: " + id + " - loaded!");
        }

        this.SelectCurrentMap(configuration.getString("currentMap"));

        if (Main.Singleton.Debug && this.CurrentMap == null) {
            Messages.SendConsoleMessage(ChatColor.DARK_AQUA, "CurrentMap is null");
        }

        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Save maps");
    }

    public void SelectCurrentMap(String id) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying select current map: " + id);

        if (Main.Singleton.RoundManager.Started) {
            if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.DARK_RED, "Can't set current map because round started!");
            return;
        }

        for (Map map : this.Maps) {
            if (map.Id.equals(id)) {
                this.CurrentMap = map;
                this.Setup(this.CurrentMap);
                if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Current map: " + id);
                return;
            }
        }
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Select current map");
    }

    public Map Get(String id) {
        for (Map map : this.Maps) {
            if (map.Id.equals(id)) {
                return map;
            }
        }

        return null;
    }

    public void Add(Map map) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying add new map");

        if (Main.Singleton.RoundManager.Started) {
            if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.DARK_RED, "Can't add map because round started!");
            return;
        }

        if (this.Get(map.Id) != null) {
            if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.DARK_RED, "Can't add map because is id used!");
            return;
        }

        this.Maps.add(map);
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Added new map: " + map.Id);
    }

    public void Remove(String id) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying remove new map");

        if (Main.Singleton.RoundManager.Started) {
            if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.DARK_RED, "Can't remove map because round started!");
            return;
        }

        Map map = this.Get(id);

        if (map != null) {
            this.Maps.remove(map);
            if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Remove map: " + map.Id);
        }
    }

    public void Setup(Map map) {
        map.Setup();
    }

    public void Setup(String id) {
        Map map = this.Get(id);
        map.Setup();
    }
}