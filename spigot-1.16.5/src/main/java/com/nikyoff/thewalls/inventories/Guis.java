package com.nikyoff.thewalls.inventories;

import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.core.Team;
import com.nikyoff.thewalls.utils.Localization;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Guis {
    public static Inventory GetTeamGui() {
        {
            int size = 9;

            if (Main.Singleton.MapManager.CurrentMap != null) {
                size *= Math.ceil((double) Main.Singleton.MapManager.CurrentMap.Teams.size() / 9);

                if (size < 9) {
                    size = 9;
                }
            }

            Inventory gui = Bukkit.createInventory(null, size, Localization.GetLocalizedString("teamGUIName"));

            if (Main.Singleton.MapManager.CurrentMap != null) {
                for (Team team : Main.Singleton.MapManager.CurrentMap.Teams) {
                    if (team.ScoreboardTeam == null) {
                        continue;
                    }

                    List<String> lore = new ArrayList<>();
                    Set<String> entries = team.ScoreboardTeam.getEntries();

                    if (team.SpawnPointLocation == null)
                    {
                        lore.add(ChatColor.RED + Localization.GetLocalizedString("teamGUINotHaveSpawningPoint"));
                    }

                    if (!entries.isEmpty())
                    {
                        entries.forEach(entry -> lore.add(ChatColor.GRAY + entry));
                    }
                    else
                    {
                        lore.add(ChatColor.RED + Localization.GetLocalizedString("teamGUINotHaveEntries"));
                    }

                    ItemStack item = new ItemStack(team.Material);
                    ItemMeta meta = item.getItemMeta();

                    meta.setLore(lore);
                    meta.setDisplayName(team.DisplayName);

                    item.setItemMeta(meta);
                    gui.addItem(item);
                }
            }

            return gui;
        }
    }
}
