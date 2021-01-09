package plugin.thewalls.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.Configuration;
import plugin.thewalls.Main;
import plugin.thewalls.TheWallTeam;
import plugin.thewalls.TheWalls;

import java.util.ArrayList;
import java.util.List;

public class TeamManager {
    public List<TheWallTeam> Teams = new ArrayList<>();
    public int LostTeamCount = 0;

    public TeamManager()
    {
        this.Load();
    }

    public void Load()
    {
        Configuration configuration = Main.Singleton.getConfig();

        int teamCount = configuration.getInt("round.teamCount");

        for (int i = 0; i < teamCount; i++)
        {
            String teamPath = "teams." + i;

            if (!configuration.isConfigurationSection(teamPath))
            {
                continue;
            }

            String name = configuration.getString(teamPath + ".name");
            ChatColor color = ChatColor.valueOf(configuration.getString(teamPath + ".color"));
            Material material = Material.valueOf(configuration.getString(teamPath + ".material"));
            Location spawnPoint = configuration.getLocation(teamPath + ".spawnPoint");

            this.Teams.add(new TheWallTeam(name, color, material, spawnPoint));
        }
    }

    public void Save()
    {
        Configuration configuration = Main.Singleton.getConfig();

        int teamCount = this.Teams.size();

        configuration.set("round.teamCount", teamCount);

        for (int i = 0; i < teamCount; i++)
        {
            TheWallTeam theWallTeam = this.Teams.get(i);
            String teamPath = "teams." + i;

            if (!configuration.isConfigurationSection(teamPath))
            {
                configuration.createSection(teamPath);
            }

            configuration.set(teamPath + ".name", theWallTeam.Name);
            configuration.set(teamPath + ".color", theWallTeam.Color.name());
            configuration.set(teamPath + ".material", theWallTeam.Material.toString());
            configuration.set(teamPath + ".spawnPoint", theWallTeam.SpawnPoint);
        }

        Main.Singleton.saveConfig();
    }

    public TheWallTeam Get(String name)
    {
        for (TheWallTeam _theWallTeam : this.Teams)
        {
            if (_theWallTeam.Team.getName().equals(name))
            {
                return  _theWallTeam;
            }
        }

        return null;
    }

    public TheWallTeam GetByDisplayName(String name)
    {
        for (TheWallTeam _theWallTeam : this.Teams)
        {
            if (_theWallTeam.DisplayName.equals(name))
            {
                return  _theWallTeam;
            }
        }

        return null;
    }

    public void Add(String name, ChatColor color, Material material)
    {
        if (Main.Singleton.RoundManager.Started)
        {
            throw new CommandException("Невозможно добавить команду во время игры!");
        }

        this.Teams.add(new TheWallTeam(name, color, material));
    }

    public void Remove(String name)
    {
        if (Main.Singleton.RoundManager.Started)
        {
            throw new CommandException("Невозможно удалить команду во время игры!");
        }

        TheWallTeam theWallTeam = Get(name);

        if (theWallTeam != null)
        {
            theWallTeam.Remove();
            this.Teams.remove(theWallTeam);
        }
    }

    public void ResetAll()
    {
        this.Teams.forEach(TheWallTeam::Reset);
        this.LostTeamCount = 0;
    }

    public void CheckTeams()
    {
        if (!Main.Singleton.RoundManager.Started)
        {
            throw new CommandException("Невозможно проверить команды вне игры!");
        }

        this.Teams.forEach(TheWallTeam::Check);

        if (this.LostTeamCount == this.Teams.size())
        {
            Main.Singleton.RoundManager.End(null);
        }
        else if (this.LostTeamCount == this.Teams.size() - 1)
        {
            for (TheWallTeam _theWallTeam : this.Teams)
            {
                if (!_theWallTeam.IsLost)
                {
                    Main.Singleton.RoundManager.End(_theWallTeam);
                    break;
                }
            }
        }
    }

    public void SpawnAll()
    {
        TheWalls.SendConsoleTheWallsMessage(ChatColor.AQUA, "SpawnAll");

        if (!Main.Singleton.RoundManager.Started)
        {
            throw new CommandException("Невозможно заспавнить команды вне игры!");
        }

        TheWalls.SendConsoleTheWallsMessage(ChatColor.AQUA, "On spawnall check teams");

        this.Teams.forEach(_team -> {
            TheWalls.SendConsoleTheWallsMessage(ChatColor.AQUA, "on spawnall check: " + _team.Name);
            if (!_team.IsLost)
            {
                TheWalls.SendConsoleTheWallsMessage(ChatColor.AQUA, "on spawnall spawn team");
                _team.Spawn();
            }
        });
    }
}
