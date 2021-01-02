package plugin.thewalls;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TheWallTeam {
    public final String Name;
    public final String DisplayName;
    public final ChatColor Color;
    public final Material Material;

    public Team Team;
    public Location SpawnPoint;
    public int PlayersCount = 0;
    public int LivePlayersCount = 0;
    public boolean IsLost = false;
    public boolean IsSpawned = false;

    public TheWallTeam(String name, ChatColor color, Material material)
    {
        this.Name = name;
        this.DisplayName = color + name;
        this.Color = color;
        this.Material = material;
        this.initialize();

    }

    public TheWallTeam(String name, ChatColor color, Material material, Location spawnPoint)
    {
        this.Name = name;
        this.DisplayName = color + name;
        this.Color = color;
        this.Material = material;
        this.SpawnPoint = spawnPoint;
        this.initialize();
    }

    public void Remove()
    {
        this.Team.unregister();
    }

    public void Check()
    {
        if (!Main.Singleton.RoundManager.Started)
        {
            throw new CommandException("Попытка проверить команду вне игры!");
        }

        if (this.PlayersCount == 0 || this.LivePlayersCount == 0)
        {
            this.IsLost = true;
            Main.Singleton.TeamManager.LostTeamCount++;
        }
    }

    public void Reset()
    {
        this.PlayersCount = 0;
        this.LivePlayersCount = 0;
        this.IsLost = false;
        this.IsSpawned = false;
        this.Team.getEntries().forEach(_entry -> this.Team.removeEntry(_entry));
    }

    public void Spawn()
    {
        if (!Main.Singleton.RoundManager.Started)
        {
            throw new CommandException("Игра ещё не начилась!");
        }

        TheWalls.SendConsoleTheWallsMessage(ChatColor.AQUA, "on spawn team");

        if (this.SpawnPoint == null)
        {
            TheWalls.SendConsoleTheWallsMessage(ChatColor.AQUA, "on spawn return because spawn point is null");
            return;
        }

        this.Team.getEntries().forEach(_entry -> {
            Player player = Bukkit.getPlayer(_entry);

            if (player != null)
            {
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().clear();
                player.teleport(this.SpawnPoint);
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                player.setFoodLevel(20);
                this.LivePlayersCount++;
            }
        });

        this.IsSpawned = true;
    }

    public void Join(String entry)
    {
        if (Main.Singleton.RoundManager.Started)
        {
            throw new CommandException("Невозможно выбрать команду во время игры!");
        }

        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(entry);

        if (team != null)
        {
            TheWallTeam theWallTeam = Main.Singleton.TeamManager.Get(team.getName());

            if (theWallTeam != null)
            {
                theWallTeam.Leave(entry);
            }
        }

        this.Team.addEntry(entry);
        this.PlayersCount++;
    }

    public void Leave(String name)
    {
        if (Main.Singleton.RoundManager.Started)
        {
            throw new CommandException("Невозможно покинуть команду во время игры!");
        }

        this.Team.removeEntry(name);
        this.PlayersCount--;
    }

    private void initialize()
    {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(this.Name);

        if (team == null)
        {
            team = scoreboard.registerNewTeam(this.Name);
        }

        team.setColor(this.Color);
        this.Team = team;
    }
}
