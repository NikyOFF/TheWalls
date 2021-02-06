package com.nikyoff.thewalls;

import com.nikyoff.thewalls.commands.map.MapCommand;
import com.nikyoff.thewalls.commands.map.MapCommandTabCompleter;
import com.nikyoff.thewalls.commands.round.RoundCommand;
import com.nikyoff.thewalls.commands.round.RoundCommandTabCompleter;
import com.nikyoff.thewalls.commands.team.TeamCommand;
import com.nikyoff.thewalls.commands.team.TeamCommandTabCompleter;
import com.nikyoff.thewalls.commands.wall.WallCommand;
import com.nikyoff.thewalls.commands.wall.WallCommandTabCompleter;
import com.nikyoff.thewalls.events.RoundEvents;
import com.nikyoff.thewalls.events.TeamSelectorItemEvents;
import com.nikyoff.thewalls.managers.MapManager;
import com.nikyoff.thewalls.managers.RoundManager;
import com.nikyoff.thewalls.managers.TeamManager;
import com.nikyoff.thewalls.managers.WallManager;
import com.nikyoff.thewalls.utils.FileHelper;
import com.nikyoff.thewalls.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static Main Singleton;

    public boolean Debug = true;
    public String SchematicsPath = getDataFolder().getAbsolutePath() + "/schematics";

    /*Managers*/
    public WallManager WallManager;
    public TeamManager TeamManager;
    public RoundManager RoundManager;
    public MapManager MapManager;

    public Main() {
        Singleton = this;
    }

    @Override
    public void onEnable() {
        FileHelper.CreateDir(this.SchematicsPath, true);

        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();

        this.initializeManagers();
        this.initializeEvents();
        this.initializeCommands();
    }

    @Override
    public void onDisable() {
        this.MapManager.Save();
    }

    private void initializeManagers() {
        if (this.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying initialize managers!");
        this.RoundManager = new RoundManager();
        this.WallManager = new WallManager();
        this.TeamManager = new TeamManager();
        this.MapManager = new MapManager();
        if (this.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Managers initialized!");
    }

    private void initializeEvents() {
        if (this.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying initialize events!");
        getServer().getPluginManager().registerEvents(new RoundEvents(), this);
        getServer().getPluginManager().registerEvents(new TeamSelectorItemEvents(), this);
        if (this.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Events initialized!");
    }

    private void initializeCommands() {
        if (this.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying initialize command!");
        PluginCommand mapCommand = this.getCommand("map");
        PluginCommand teamCommand = this.getCommand("team");
        PluginCommand wallCommand = this.getCommand("wall");
        PluginCommand roundCommand = this.getCommand("round");

        mapCommand.setExecutor(new MapCommand());
        mapCommand.setTabCompleter(new MapCommandTabCompleter());

        teamCommand.setExecutor(new TeamCommand());
        teamCommand.setTabCompleter(new TeamCommandTabCompleter());

        wallCommand.setExecutor(new WallCommand());
        wallCommand.setTabCompleter(new WallCommandTabCompleter());

        roundCommand.setExecutor(new RoundCommand());
        roundCommand.setTabCompleter(new RoundCommandTabCompleter());
        if (this.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Commands initialized!");
    }
}
