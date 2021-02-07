package com.nikyoff.thewalls.events;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.*;
import com.nikyoff.thewalls.items.TeamSelector;
import com.nikyoff.thewalls.utils.Localization;
import com.nikyoff.thewalls.utils.Messages;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.HashMap;
import java.util.Objects;

public class RoundEvents implements Listener {
    public HashMap<String, Integer> OfflinePlayers = new HashMap<>();

    public RoundEvents() {
        if (Main.Singleton.Debug) {
            Messages.SendConsoleMessage(ChatColor.LIGHT_PURPLE, "RoundEvents initialized");
        }
    }

    @EventHandler
    public void OnPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        for(Player _player : Bukkit.getOnlinePlayers())
        {
            Main.Singleton.RoundManager.BossBarTimer.addPlayer(_player);
        }

        if (!Main.Singleton.RoundManager.Started) {
            TeamSelector.AddItem(player);
            player.setFoodLevel(20);
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());

            if (Main.Singleton.MapManager.CurrentMap != null && player.getGameMode() != GameMode.CREATIVE) {
                player.teleport(Main.Singleton.MapManager.CurrentMap.SpawnLocation);
            }
        } else {
            if (this.OfflinePlayers.containsKey(playerName)) {
                Bukkit.getScheduler().cancelTask(this.OfflinePlayers.get(playerName));
            }
        }
    }

    @EventHandler
    public void OnPlayerQuitEvent(PlayerQuitEvent event) {
        if (!Main.Singleton.RoundManager.Started) {
            return;
        }

        Player player = event.getPlayer();
        String playerName = player.getName();
        Team team = Main.Singleton.TeamManager.GetByEntry(playerName);
        long delay = (long) ((Main.Singleton.MapManager.CurrentMap.MaxRoundTime * 60) - Main.Singleton.RoundManager.CurrentTimer);

        if (team != null) {
            int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.Singleton, () -> {
                if (!Main.Singleton.RoundManager.Started) {
                    return;
                }

                team.Kill(playerName, "TheWalls", true);
                this.OfflinePlayers.remove(playerName);
            }, 20 * delay);

            OfflinePlayers.put(playerName, taskId);
        }
    }

    @EventHandler
    public void OnPlayerDeathEvent(PlayerDeathEvent event) {
        Messages.SendConsoleMessage(ChatColor.DARK_PURPLE, "PlayerDeathEvent - start");
        if (Main.Singleton.RoundManager.Started) {
            Player player = event.getEntity();
            String playerName = player.getName();
            Player killer = event.getEntity().getKiller();
            Team team = Main.Singleton.TeamManager.GetByEntry(playerName);

            if (Main.Singleton.TeamManager.GetByEntry(player.getName()) != null) {
                Messages.BroadcastMessage(ChatColor.LIGHT_PURPLE, "kill");

                team.Kill(playerName, event.getDeathMessage(), false);

                if (killer != null) {
                    String killerName = killer.getName();
                    Team enemyTeam = Main.Singleton.TeamManager.GetByEntry(killerName);

                    if (enemyTeam != null) {
                        enemyTeam.Points += 5;
                    }
                }

                Main.Singleton.TeamManager.CheckTeams();
            }
        }
        Messages.SendConsoleMessage(ChatColor.DARK_PURPLE, "PlayerDeathEvent - end");
    }

    @EventHandler
    public void OnEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!Main.Singleton.RoundManager.Started) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void OnPlayerTeleportEvent(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && player.getGameMode() != GameMode.CREATIVE) {
            if (Main.Singleton.MapManager.CurrentMap != null && Objects.requireNonNull(event.getTo()).getY() > Main.Singleton.MapManager.CurrentMap.MaxBlockPlaceY) {
                Messages.SendMessage(player, ChatColor.RED, Localization.GetLocalizedString("limitBlockYPlaceError"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void OnPlayerBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (Main.Singleton.RoundManager.Started && player.getGameMode() != GameMode.CREATIVE) {
            if (Main.Singleton.MapManager.CurrentMap != null && event.getBlock().getLocation().getY() > Main.Singleton.MapManager.CurrentMap.MaxBlockPlaceY) {
                Messages.SendMessage(player, ChatColor.RED, Localization.GetLocalizedString("limitBlockYPlaceError"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void OnPlayerFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (!Main.Singleton.RoundManager.Started) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnPortalCreateEvent(PortalCreateEvent event) {
        if (Main.Singleton.MapManager.CurrentMap != null && !Main.Singleton.MapManager.CurrentMap.CanCreatePortal) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();

                if (player.getGameMode() == GameMode.CREATIVE) {
                    return;
                }

                Messages.SendMessage(player, ChatColor.RED, Localization.GetLocalizedString("portalCreateError"));
            }


            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnRoundAwakeEvent(RoundAwakeEvent event) {
        Map currentMap = event.GetCurrentMap();

        Messages.BroadcastMessage(ChatColor.GOLD, Localization.GetLocalizedString("soonGameMessage"));
        Messages.BroadcastTitle(Localization.GetLocalizedString("soonGameMessage"), "", 10, 20, 10);

        Main.Singleton.RoundManager.SetCurrentTimer(currentMap.RoundAwakeTime);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.Singleton, () -> {
            Main.Singleton.RoundManager.UpdateTimer();
        }, 0, 1);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.Singleton, () -> {
            Main.Singleton.RoundManager.Start();
            Main.Singleton.RoundManager.SetCurrentTimer(currentMap.WallsFallenTime * 60);
        }, 20 * currentMap.RoundAwakeTime);
    }

    @EventHandler
    public void OnRoundStartEvent(RoundStartEvent event) {
        Map currentMap = event.GetCurrentMap();
        WorldBorder worldBorder = currentMap.MainWorld.getWorldBorder();

        if (!Main.Singleton.RoundManager.DevMode) {
            Main.Singleton.TeamManager.CheckTeams();
        }

        if (!Main.Singleton.RoundManager.Started)
        {
            return;
        }

        Main.Singleton.TeamManager.SpawnTeams();

        Messages.BroadcastSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1);
        Messages.BroadcastMessage(ChatColor.GOLD, Localization.GetLocalizedString("startGameMessage"));

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.Singleton, () -> {
            //walls logic
            Messages.BroadcastMessage(ChatColor.GOLD, Localization.GetLocalizedString("wallsFallenTitle"));
            Messages.BroadcastTitle(Localization.GetLocalizedString("wallsFallenTitle"), ChatColor.GRAY + Localization.GetLocalizedString("wallsFallenSubtitle"), 10, 40, 20);
            Main.Singleton.WallManager.CreateAll(true);
            Main.Singleton.RoundManager.RoundStage = RoundStage.WallsFallen;
            Main.Singleton.RoundManager.SetCurrentTimer((currentMap.MaxRoundTime - currentMap.WallsFallenTime) * 60);
        }, 20 * 60 * currentMap.WallsFallenTime);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.Singleton, () -> {
            //border logic
            double endTime = currentMap.MaxRoundTime - currentMap.WallsFallenTime;
            worldBorder.setSize(currentMap.WorldBorderMinSize, (long) ((endTime - ((endTime / 100) * 30)) * 60));
        }, 20 * 60 * (currentMap.WallsFallenTime));

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.Singleton, () -> {
            //end logic
            worldBorder.setSize(currentMap.WorldBorderMaxSize);
            Main.Singleton.RoundManager.End(null);
        }, 20 * 60 * currentMap.MaxRoundTime);
    }

    @EventHandler
    public void OnRoundEndEvent(RoundEndEvent event) {
        Messages.SendConsoleMessage(ChatColor.DARK_PURPLE, "RoundEndEvent - start");
        Main.Singleton.RoundManager.Stop();

        Team team = event.GetVictoryTeam();

        if (team != null) {
            Messages.BroadcastTitle(ChatColor.GOLD + Localization.GetLocalizedString("endGameWinMessage"), event.GetVictoryTeam().DisplayName, 10, 40, 20);
        } else {
            Messages.BroadcastTitle(ChatColor.DARK_PURPLE + Localization.GetLocalizedString("endGameNoOneWinMessage"), "", 10, 40, 20);
        }

        Messages.BroadcastSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
        Messages.SendConsoleMessage(ChatColor.DARK_PURPLE, "RoundEndEvent - end");
    }

    @EventHandler
    public void OnRoundStopEvent(RoundStopEvent event) {
        Messages.SendConsoleMessage(ChatColor.DARK_PURPLE, "RoundStopEvent - start");
        Messages.BroadcastMessage(ChatColor.GOLD, Localization.GetLocalizedString("stopGameMessage"));

        Main.Singleton.getServer().getScheduler().cancelTasks(Main.Singleton);
        Main.Singleton.RoundManager.UpdateTimer();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
            player.setFoodLevel(20);
            player.teleport(Main.Singleton.MapManager.CurrentMap.SpawnLocation);
            player.setGameMode(GameMode.ADVENTURE);
            TeamSelector.AddItem(player);
        }

        Main.Singleton.TeamManager.ResetTeams();
        //Main.Singleton.MapManager.CurrentMap.Setup();
        Messages.SendConsoleMessage(ChatColor.DARK_PURPLE, "RoundStopEvent - end");
    }
}
