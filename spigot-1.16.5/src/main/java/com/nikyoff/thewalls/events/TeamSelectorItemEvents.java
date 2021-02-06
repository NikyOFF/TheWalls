package com.nikyoff.thewalls.events;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.Team;
import com.nikyoff.thewalls.core.TeamJoinEvent;
import com.nikyoff.thewalls.core.TeamLeaveEvent;
import com.nikyoff.thewalls.inventories.Guis;
import com.nikyoff.thewalls.utils.Localization;
import com.nikyoff.thewalls.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Objects;

public class TeamSelectorItemEvents implements Listener {
    public HashSet<HumanEntity> Viewers = new HashSet<>();

    public TeamSelectorItemEvents() {
        if (Main.Singleton.Debug) {
            Messages.SendConsoleMessage(ChatColor.LIGHT_PURPLE, "TeamSelectorItemEvents initialized");
        }
    }

    @EventHandler
    public void OnPlayerDropItemEvent(PlayerDropItemEvent event) {
        if (!Main.Singleton.RoundManager.Started && Objects.requireNonNull(event.getItemDrop().getItemStack().getItemMeta()).getDisplayName().equals(Localization.GetLocalizedString("teamSelectorItemName"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && itemStack != null && Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName().equals(Localization.GetLocalizedString("teamSelectorItemName"))) {
            event.getPlayer().openInventory(Guis.GetTeamGui());
        }
    }

    @EventHandler
    public void OnInventoryOpenEvent(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player && event.getView().getTitle().equals(Localization.GetLocalizedString("teamGUIName"))) {
            if (Main.Singleton.Debug) {
                Messages.SendConsoleMessage(ChatColor.LIGHT_PURPLE, "OnInventoryCloseEvent, add " + event.getPlayer().getName() + " to viewers");
            }
            this.Viewers.add(event.getPlayer());
        }
    }

    @EventHandler
    public void OnInventoryCloseEvent(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player && event.getView().getTitle().equals(Localization.GetLocalizedString("teamGUIName"))) {
            if (Main.Singleton.Debug) {
                Messages.SendConsoleMessage(ChatColor.LIGHT_PURPLE, "OnInventoryCloseEvent, remove " + event.getPlayer().getName() + " in viewers");
            }
            this.Viewers.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void OnInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventoryView = event.getView();

        if (inventoryView.getTitle().equals(Localization.GetLocalizedString("teamGUIName")))
        {
            if (!Main.Singleton.RoundManager.Started && Main.Singleton.MapManager.CurrentMap != null)
            {
                ItemStack itemStack = event.getCurrentItem();

                if (itemStack != null)
                {
                    String itemDisplayName = Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName();
                    Team team = Main.Singleton.TeamManager.GetByDisplayName(itemDisplayName);

                    if (team != null)
                    {
                        team.Join(player.getName());
                    }
                }
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnTeamJoinEvent(TeamJoinEvent event) {
        if (this.Viewers == null || this.Viewers.isEmpty()) {
            return;
        }

        for (HumanEntity humanEntity : this.Viewers) {
            humanEntity.openInventory(Guis.GetTeamGui());
        }
    }

    @EventHandler
    public void OnTeamLeaveEvent(TeamLeaveEvent event) {

    }
}
