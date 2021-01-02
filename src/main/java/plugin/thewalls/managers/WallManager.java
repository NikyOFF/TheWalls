package plugin.thewalls.managers;

import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.Configuration;
import plugin.thewalls.Main;
import plugin.thewalls.Wall;

import java.util.ArrayList;
import java.util.List;

public class WallManager {
    public List<Wall> Walls = new ArrayList<>();
    public boolean WallsFallen = false;

    public WallManager()
    {
        this.Load();
    }

    public void Load()
    {
        Configuration configuration = Main.Singleton.getConfig();

        int wallCount = configuration.getInt("round.wallCount");

        for (int i = 0; i < wallCount; i++)
        {
            String wallPath = "walls." + i;
            String name = configuration.getString(wallPath + ".name");
            Location pos0 = configuration.getLocation(wallPath + ".pos0");
            Location pos1 = configuration.getLocation(wallPath + ".pos1");

            this.Walls.add(new Wall(name, pos0, pos1));
        }
    }

    public void Save()
    {
        if (Main.Singleton.RoundManager.Started)
        {
            throw new CommandException("Невозможно сохранить стену во время игры!");
        }

        Configuration configuration = Main.Singleton.getConfig();

        int wallCount = this.Walls.size();

        configuration.set("round.wallCount", wallCount);

        for (int i = 0; i < wallCount; i++)
        {
            Wall wall = this.Walls.get(i);
            String wallPath = "walls." + i;

            if (!configuration.isConfigurationSection(wallPath))
            {
                configuration.createSection(wallPath);
            }

            configuration.set(wallPath + ".name", wall.Name);
            configuration.set(wallPath + ".pos0", wall.Pos0);
            configuration.set(wallPath + ".pos1", wall.Pos1);
        }

        Main.Singleton.saveConfig();
    }

    public Wall Get(String name)
    {
        for (Wall _wall : this.Walls)
        {
            if (_wall.Name.equals(name))
            {
                return  _wall;
            }
        }

        return null;
    }

    public void Add(String name, Location pos0, Location pos1)
    {
        if (Main.Singleton.RoundManager.Started)
        {
            throw new CommandException("Невозможно добавить стену во время игры!");
        }

        this.Walls.add(new Wall(name, pos0, pos1));
    }

    public void Remove(String name)
    {
        if (Main.Singleton.RoundManager.Started)
        {
            throw new CommandException("Невозможно удалить стену во время игры!");
        }

        Wall wall = Get(name);

        if (wall != null)
        {
            this.Walls.remove(wall);
        }
    }

    public void CreateAll()
    {
        if (!this.WallsFallen)
        {
            return;
        }

        this.WallsFallen = false;

        Main.Singleton.getServer().getConsoleSender().sendMessage("CreateAll");
        Main.Singleton.getServer().getConsoleSender().sendMessage("Wall count: " + this.Walls.size());

        this.Walls.forEach(Wall::Create);
    }

    public void DestroyAll()
    {
        if (this.WallsFallen)
        {
            return;
        }

        this.WallsFallen = true;

        this.Walls.forEach(Wall::Destroy);
    }
}
