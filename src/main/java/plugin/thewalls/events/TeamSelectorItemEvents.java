package plugin.thewalls.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import plugin.thewalls.Main;
import plugin.thewalls.TheWallTeam;
import plugin.thewalls.TheWalls;
import plugin.thewalls.inventory.TeamsInventory;
import plugin.thewalls.items.TeamSelector;

public class TeamSelectorItemEvents implements Listener {

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            ItemStack itemStack = event.getItem();

            if (itemStack != null)
            {
                if (itemStack.getItemMeta().getDisplayName().equals(TeamSelector.TeamSelectorItemName))
                {
                    player.openInventory(new TeamsInventory().gui);
                }
            }

        }
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event)
    {
        Item item = event.getItemDrop();

        if (item.getItemStack().getItemMeta().getDisplayName().equals(TeamSelector.TeamSelectorItemName))
        {
            if (!Main.Singleton.RoundManager.Started)
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventoryView = event.getView();

        if (inventoryView.getTitle().equals(TeamsInventory.TeamGuiName))
        {
            ItemStack itemStack = event.getCurrentItem();

            if (itemStack != null)
            {
                String itemDisplayName = itemStack.getItemMeta().getDisplayName();
                TheWallTeam theWallTeam = Main.Singleton.TeamManager.GetByDisplayName(itemDisplayName);

                TheWalls.SendConsoleTheWallsMessage(ChatColor.DARK_AQUA, "itemDisplayName: " + itemDisplayName);

                if (theWallTeam != null)
                {
                    theWallTeam.Join(player.getName());
                    TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Вы успешно вступили в команду " + itemDisplayName);
                }
            }

            event.setCancelled(true);
        }
    }
}
