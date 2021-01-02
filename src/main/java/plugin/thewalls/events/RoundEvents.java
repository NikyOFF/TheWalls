package plugin.thewalls.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Team;
import plugin.thewalls.Main;
import plugin.thewalls.TheWallTeam;
import plugin.thewalls.items.TeamSelector;

public class RoundEvents implements Listener {

    @EventHandler
    public void OnPortalCreateEvent(PortalCreateEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerJoinEvent(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        String playerName = player.getName();
        Team team = player.getScoreboard().getEntryTeam(playerName);

        if (Main.Singleton.RoundManager.Started)
        {
            if (team != null)
            {
                TheWallTeam theWallTeam = Main.Singleton.TeamManager.Get(team.getName());

                if (theWallTeam != null)
                {
                    if (Main.Singleton.RoundManager.DeadPlayers.get(playerName) != null)
                    {
                        player.setGameMode(GameMode.SPECTATOR);
                    }
                }
            }
        }
        else
        {
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().addItem(TeamSelector.TeamSelectorItem());
        }
    }

    @EventHandler
    public void FoodLevelChangeEvent(FoodLevelChangeEvent event)
    {
        if (!Main.Singleton.RoundManager.Started)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event)
    {
        if (!Main.Singleton.RoundManager.Started)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerDeathEvent (PlayerDeathEvent event)
    {
        if (!Main.Singleton.RoundManager.Started)
        {
            Player player = event.getEntity();
            String playerName = player.getName();
            Team team = player.getScoreboard().getEntryTeam(playerName);

            if (team != null)
            {
                TheWallTeam theWallTeam = Main.Singleton.TeamManager.Get(team.getName());

                if (theWallTeam != null && !theWallTeam.IsLost && theWallTeam.IsSpawned)
                {
                    if (Main.Singleton.RoundManager.DeadPlayers.get(playerName) == null)
                    {
                        theWallTeam.LivePlayersCount--;
                        Main.Singleton.RoundManager.DeadPlayers.put(playerName, event.getDeathMessage());
                        player.setGameMode(GameMode.SPECTATOR);
                    }
                }
            }
        }
    }
}
