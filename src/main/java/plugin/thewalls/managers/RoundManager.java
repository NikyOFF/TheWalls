package plugin.thewalls.managers;

import org.bukkit.*;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.Configuration;
import plugin.thewalls.Main;
import plugin.thewalls.RoundStage;
import plugin.thewalls.TheWallTeam;
import plugin.thewalls.TheWalls;
import plugin.thewalls.items.TeamSelector;

import java.util.HashMap;

public class RoundManager {
    public boolean Started = false;
    public RoundStage Stage = RoundStage.WaitingStart;
    public HashMap<String, String> DeadPlayers = new HashMap<>();
    public boolean DeadLighting = true;
    public boolean DevMode = false;

    public boolean IsDead(String name)
    {
        return this.DeadPlayers.containsKey(name);
    }

    public void Respawn(String name)
    {
        if (IsDead(name))
        {
            this.DeadPlayers.remove(name);
        }
    }

    public void SetEndTime(int time)
    {
        if (time <= this.GetFallTime())
        {
            throw new CommandException("endTime <= fallTime");
        }

        Configuration configuration = Main.Singleton.getConfig();

        configuration.set("round.endTime", time);

        Main.Singleton.saveConfig();
    }

    public void SetFallTime(int time)
    {
        if (time >= this.GetEndTime())
        {
            throw new CommandException("fallTime >= endTime");
        }

        Configuration configuration = Main.Singleton.getConfig();

        configuration.set("round.fallTime", time);

        Main.Singleton.saveConfig();
    }

    public void SetMaxBorderSize(float size)
    {
        if (size < this.GetMinBorderSize())
        {
            throw new CommandException("MaxBorderSize < MinBorderSize");
        }

        Configuration configuration = Main.Singleton.getConfig();

        configuration.set("round.maxBorderSize", size);

        Main.Singleton.saveConfig();
    }

    public void SetMinBorderSize(float size)
    {
        if (size > this.GetMaxBorderSize())
        {
            throw new CommandException("MinBorderSize > MaxBorderSize");
        }

        Configuration configuration = Main.Singleton.getConfig();

        configuration.set("round.minBorderSize", size);

        Main.Singleton.saveConfig();
    }

    public int GetEndTime()
    {
        return Main.Singleton.getConfig().getInt("round.endTime");
    }

    public int GetFallTime()
    {
        return Main.Singleton.getConfig().getInt("round.fallTime");
    }

    public int GetMaxBorderSize()
    {
        return Main.Singleton.getConfig().getInt("round.maxBorderSize");
    }

    public int GetMinBorderSize()
    {
        return Main.Singleton.getConfig().getInt("round.minBorderSize");
    }

    public void Awake()
    {
        Configuration configuration = Main.Singleton.getConfig();

        if (this.Started)
        {
            throw new CommandException(configuration.getString("localization.gameAlreadyStartedError"));
        }

        TheWalls.SendConsoleTheWallsMessage(ChatColor.DARK_GREEN, "Round - wakes up");

        Main.Scheduler.cancelTasks(Main.Singleton);

        this.Started = true;
        this.Stage = RoundStage.Awake;

        TheWalls.BroadcastTheWallsMessage(ChatColor.GOLD, configuration.getString("localization.soonGameMessage"));

        TheWalls.BroadcastTimer(5, 1, configuration.getString("localization.startGameAlert"), true);

        TheWalls.SendConsoleTheWallsMessage(ChatColor.GREEN, "Round - awake");

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, this::Start, 20 * 5);
    }

    public void Start()
    {
        Configuration configuration = Main.Singleton.getConfig();

        TheWalls.SendConsoleTheWallsMessage(ChatColor.DARK_GREEN, "Round - starts");

        if (Main.Singleton.TeamManager.Teams.size() < 2)
        {
            throw new CommandException(configuration.getString("localization.teamSizeError"));
        }

        Main.Singleton.getServer().getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.SPECTATOR));

        this.Stage = RoundStage.Start;

        Main.Singleton.TeamManager.SpawnAll();

        TheWalls.BroadcastTheWallsMessage(ChatColor.GOLD, configuration.getString("localization.startGameMessage"));

        TheWalls.SendConsoleTheWallsMessage(ChatColor.GREEN, "Round - start");

        this.OnStart();
    }

    public void End(TheWallTeam theWallTeam)
    {
        Configuration configuration = Main.Singleton.getConfig();

        TheWalls.SendConsoleTheWallsMessage(ChatColor.DARK_GREEN, "Round - ends");

        if (theWallTeam == null)
        {
            TheWalls.BroadcastTitle(ChatColor.GRAY + configuration.getString("localization.nobodyVictoryMessage"), "", 20, 40, 20);
        }
        else
        {
            TheWalls.BroadcastTitle(ChatColor.GOLD + configuration.getString("localization.victoryTeamMessage") + " " + theWallTeam.Team.getName() + "!", "", 20, 40, 20);
        }

        TheWalls.BroadcastTheWallsMessage(ChatColor.GREEN, configuration.getString("localization.endGameMessage"));
        TheWalls.BroadcastSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);

        TheWalls.SendConsoleTheWallsMessage(ChatColor.GREEN, "Round - ended");

        this.Stop();
    }

    public void Stop()
    {
        TheWalls.SendConsoleTheWallsMessage(ChatColor.DARK_GREEN, "Round - stops");

        Main.Singleton.getServer().getScheduler().cancelTasks(Main.Singleton);

        Main.Singleton.WallManager.CreateAll();
        Main.Singleton.TeamManager.ResetAll();

        Bukkit.getWorld("world").getWorldBorder().setSize(this.GetMaxBorderSize());

        Location location = Main.Singleton.getServer().getWorld("world").getSpawnLocation();

        Bukkit.getOnlinePlayers().forEach(_player -> {
            _player.setHealth(20);
            _player.setFoodLevel(20);
            _player.getInventory().clear();
            _player.setGameMode(GameMode.ADVENTURE);
            _player.teleport(location);

            _player.getInventory().addItem(TeamSelector.TeamSelectorItem());
        });

        this.DeadPlayers.clear();
        this.Started = false;
        this.Stage = RoundStage.WaitingStart;

        TheWalls.SendConsoleTheWallsMessage(ChatColor.GREEN, "Round - stopped");
    }

    public void OnStart()
    {
        Configuration configuration = Main.Singleton.getConfig();

        if (!DevMode)
        {
            Main.Singleton.TeamManager.CheckTeams();
        }

        World world = Bukkit.getServer().getWorld("world");
        world.setDifficulty(Difficulty.HARD);
        world.setTime(0);

        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setCenter(world.getSpawnLocation());
        worldBorder.setSize(this.GetMaxBorderSize());

        //region walls
        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            TheWalls.BroadcastTheWallsMessage(ChatColor.GREEN, configuration.getString("localization.wallFallAlert") + " " + TheWalls.CorrectMinutesText(this.GetFallTime() / 2) + "!");
        }, 20 * 60 * (this.GetFallTime() / 2));

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            TheWalls.BroadcastTheWallsMessage(ChatColor.GREEN, configuration.getString("localization.wallFallAlert") + " " + TheWalls.CorrectMinutesText(1) + "!");
        }, 20 * 60 * this.GetFallTime() - (20 * 60));

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            TheWalls.BroadcastTheWallsMessage(ChatColor.DARK_GREEN, configuration.getString("localization.wallFallWarring"));
        }, 20 * 60 * this.GetFallTime() - (20 * 15));

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            TheWalls.BroadcastTimer(10, 4, configuration.getString("localization.wallFallTimer"), false);
        }, 20 * 60 * this.GetFallTime() - (20 * 11));

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            TheWalls.BroadcastTimer(3, 1, configuration.getString("localization.wallFallTimer"), true);
        }, 20 * 60 * this.GetFallTime() - (20 * 4));

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            this.Stage = RoundStage.WallsFell;
            Main.Singleton.WallManager.DestroyAll();
            TheWalls.BroadcastTheWallsMessage(ChatColor.GOLD, configuration.getString("localization.wallFallMessage"));

            worldBorder.setSize(this.GetMinBorderSize(), (this.GetEndTime() - (this.GetFallTime())) * 60);
        }, 20 * 60 * this.GetFallTime());
        //endregion

        //region endGame
        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            TheWalls.BroadcastTheWallsMessage(ChatColor.GREEN, configuration.getString("localization.roundEndAlert") + " " + TheWalls.CorrectMinutesText(this.GetEndTime() / 2) + "!");
        }, 20 * 60 * (this.GetEndTime() / 2));

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            TheWalls.BroadcastTheWallsMessage(ChatColor.GREEN, configuration.getString("localization.roundEndAlert") + " " + TheWalls.CorrectMinutesText(1) + "!");
        }, 20 * 60 * this.GetEndTime() - (20 * 60));

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            this.End(null);
        }, 20 * 60 * this.GetEndTime());
        //endregion
    }
}
