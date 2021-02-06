package com.nikyoff.thewalls.managers;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.Wall;
import org.bukkit.command.CommandException;

import java.util.List;

public class WallManager {
    public Wall Get(Wall wall) {
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return null;
        }

        for (Wall _wall : Main.Singleton.MapManager.CurrentMap.Walls) {
            if (_wall.Id.equals(wall.Id)) {
                return  _wall;
            }
        }

        return null;
    }

    public Wall Get(String id) {
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return null;
        }

        for (Wall _wall : Main.Singleton.MapManager.CurrentMap.Walls) {
            if (_wall.Id.equals(id)) {
                return  _wall;
            }
        }

        return null;
    }

    public void Add(Wall wall) {
        if (Main.Singleton.RoundManager.Started) {
            return;
        }

        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        Wall possibleWall = Get(wall);

        if (possibleWall != null) {
            return;
        }

        Main.Singleton.MapManager.CurrentMap.Walls.add(wall);
    }

    public void Remove(String id) {
        if (Main.Singleton.RoundManager.Started) {
            throw new CommandException("Невозможно удалить стену во время игры!");
        }

        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        Wall wall = Get(id);

        if (wall != null) {
            Main.Singleton.MapManager.CurrentMap.Walls.remove(wall);
        }
    }

    public void CreateAll(boolean fallen) {
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        Main.Singleton.MapManager.CurrentMap.Walls.forEach(wall -> wall.Create(fallen));
    }

    public void CreateAllWithIdList(List<String> idList, boolean fallen) {
        if (Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        for (Wall wall : Main.Singleton.MapManager.CurrentMap.Walls) {
            if (idList.contains(wall.Id)) {
                wall.Create(fallen);
            }
        }
    }
}
