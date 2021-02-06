package com.nikyoff.thewalls.items;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.utils.Localization;
import com.nikyoff.thewalls.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TeamSelector {
    public static ItemStack Item()
    {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add(Localization.GetLocalizedString("teamSelectorItemDescription"));

        meta.setDisplayName(Localization.GetLocalizedString("teamSelectorItemName"));
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public static void AddItem(Player player)
    {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Add TeamSelector Item to player: " + player.getName());

        PlayerInventory inventory = player.getInventory();

        for (ItemStack inventoryItem : inventory.getContents())
        {
            if (inventoryItem != null && inventoryItem.getItemMeta().getDisplayName().equals(Localization.GetLocalizedString("teamSelectorItemName"))) {
                return;
            }
        }

        ItemStack itemStack = Item();
        inventory.setItemInMainHand(itemStack);
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "TeamSelector added to player");
    }
}