package plugin.thewalls;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import plugin.thewalls.commands.round.RoundCommand;
import plugin.thewalls.commands.round.RoundCommandTabCompleter;
import plugin.thewalls.commands.team.TeamCommand;
import plugin.thewalls.commands.team.TeamCommandTabCompleter;
import plugin.thewalls.commands.wall.WallCommand;
import plugin.thewalls.commands.wall.WallCommandTabCompleter;
import plugin.thewalls.events.RoundEvents;
import plugin.thewalls.events.TeamSelectorItemEvents;
import plugin.thewalls.managers.RoundManager;
import plugin.thewalls.managers.TeamManager;
import plugin.thewalls.managers.WallManager;

public final class Main extends JavaPlugin {
    public static Main Singleton;
    public static BukkitScheduler Scheduler = Bukkit.getScheduler();

    public RoundManager RoundManager;
    public TeamManager TeamManager;
    public WallManager WallManager;

    public Main ()
    {
        Singleton = this;
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        this.RoundManager = new RoundManager();
        this.TeamManager = new TeamManager();
        this.WallManager = new WallManager();

        this.initializeCommands();
        this.initializeEvents();
    }

    @Override
    public void onDisable() {
        this.TeamManager.Save();
        this.WallManager.Save();
    }

    private void initializeCommands()
    {
        PluginCommand teamCommand = this.getCommand("team");
        PluginCommand wallCommand = this.getCommand("wall");
        PluginCommand roundCommand = this.getCommand("round");

        teamCommand.setExecutor(new TeamCommand());
        teamCommand.setTabCompleter(new TeamCommandTabCompleter());

        wallCommand.setExecutor(new WallCommand());
        wallCommand.setTabCompleter(new WallCommandTabCompleter());

        roundCommand.setExecutor(new RoundCommand());
        roundCommand.setTabCompleter(new RoundCommandTabCompleter());
    }

    private void initializeEvents()
    {
        getServer().getPluginManager().registerEvents(new TeamSelectorItemEvents(), this);
        getServer().getPluginManager().registerEvents(new RoundEvents(), this);
    }
}
