package plugin.thewalls.managers;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
    public boolean DevMode = false;

    public void SetEndTime(int time)
    {
        Configuration configuration = Main.Singleton.getConfig();

        configuration.set("round.endTime", time);

        Main.Singleton.saveConfig();
    }

    public void SetFallTime(int time)
    {
        Configuration configuration = Main.Singleton.getConfig();

        configuration.set("round.fallTime", time);

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

    public void Awake()
    {
        if (this.Started)
        {
            throw new CommandException("Игра уже запущена!");
        }

        TheWalls.SendConsoleTheWallsMessage(ChatColor.DARK_GREEN, "Round - wakes up");

        Main.Scheduler.cancelTasks(Main.Singleton);

        this.Started = true;
        this.Stage = RoundStage.Awake;

        TheWalls.BroadcastTheWallsMessage(ChatColor.GOLD, "Игра скоро начнётся!");

        TheWalls.BroadcastTimer(5, 1, "до начала игры!", true);

        TheWalls.SendConsoleTheWallsMessage(ChatColor.GREEN, "Round - awake");

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, this::Start, 20 * 5);
    }

    public void Start()
    {
        TheWalls.SendConsoleTheWallsMessage(ChatColor.DARK_GREEN, "Round - starts");

        if (Main.Singleton.TeamManager.Teams.size() < 2)
        {
            throw new CommandException("Количество команд меньше двух!");
        }

        Main.Singleton.getServer().getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.SPECTATOR));

        this.Stage = RoundStage.Start;

        Main.Singleton.TeamManager.SpawnAll();

        Bukkit.getServer().getWorld("world").setDifficulty(Difficulty.HARD);

        TheWalls.BroadcastTheWallsMessage(ChatColor.GOLD, "Игра началась!");

        TheWalls.SendConsoleTheWallsMessage(ChatColor.GREEN, "Round - start");

        this.OnStart();
    }

    public void End(TheWallTeam theWallTeam)
    {
        TheWalls.SendConsoleTheWallsMessage(ChatColor.DARK_GREEN, "Round - ends");

        if (theWallTeam == null)
        {
            TheWalls.BroadcastTitle(ChatColor.GRAY + "Никто не победил!", "", 20, 40, 20);
        }
        else
        {
            TheWalls.BroadcastTitle(ChatColor.GOLD + "Команда " + theWallTeam.Team.getName() + " - победила!", "", 20, 40, 20);
        }

        TheWalls.BroadcastTheWallsMessage(ChatColor.GREEN, "Игра закончилась!");
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

        Location location = Main.Singleton.getServer().getWorld("world").getSpawnLocation();

        Bukkit.getOnlinePlayers().forEach(_player -> {
            _player.setHealth(20);
            _player.setFoodLevel(20);
            _player.getInventory().clear();
            _player.setGameMode(GameMode.ADVENTURE);
            _player.teleport(location);
            _player.setBedSpawnLocation(location);

            _player.getInventory().addItem(TeamSelector.TeamSelectorItem());
        });

        this.DeadPlayers.clear();
        this.Started = false;
        this.Stage = RoundStage.WaitingStart;

        TheWalls.SendConsoleTheWallsMessage(ChatColor.GREEN, "Round - stopped");
    }

    public void OnStart()
    {
        if (!DevMode)
        {
            Main.Singleton.TeamManager.CheckTeams();
        }

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            TheWalls.BroadcastTheWallsMessage(ChatColor.GREEN, "Внимание! Скоро упадут стены, возможно пролагивание сервера!");
        }, 20 * 60 * this.GetFallTime() - (20 * 15));

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            TheWalls.BroadcastTimer(10, 4, "до падения стен!", false);
        }, 20 * 60 * this.GetFallTime() - (20 * 11));

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            TheWalls.BroadcastTimer(3, 1, "до падения стен!", true);
        }, 20 * 60 * this.GetFallTime() - (20 * 4));

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            this.Stage = RoundStage.WallsFell;
            Main.Singleton.WallManager.DestroyAll();
            TheWalls.BroadcastTheWallsMessage(ChatColor.GOLD, "Стены упали!");
        }, 20 * 60 * this.GetFallTime());

        Main.Scheduler.scheduleSyncDelayedTask(Main.Singleton, () -> {
            this.End(null);
        }, 20 * 60 * this.GetEndTime());
    }
}
