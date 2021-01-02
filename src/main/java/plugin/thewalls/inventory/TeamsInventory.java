package plugin.thewalls.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugin.thewalls.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TeamsInventory {
    public static String TeamGuiName = ChatColor.BOLD + "Teams";

    public final Inventory gui;

    public TeamsInventory()
    {
        this.gui = this.createTeamsGui();
    }

    protected Inventory createTeamsGui()
    {
        int size = 9;

        if (Main.Singleton.TeamManager.Teams.size() > 9 && Main.Singleton.TeamManager.Teams.size() <= 18)
        {
            size = 18;
        }
        else if (Main.Singleton.TeamManager.Teams.size() > 18)
        {
            size = 27;
        }

        Inventory gui = Bukkit.createInventory(null, size, TeamGuiName);

        Main.Singleton.TeamManager.Teams.forEach(_theWallTeam -> {
            List<String> lore = new ArrayList<>();
            Set<String> entries = _theWallTeam.Team.getEntries();

            if (_theWallTeam.SpawnPoint == null)
            {
                lore.add(ChatColor.RED + "Не имеет точки спавна!");
            }

            if (!entries.isEmpty())
            {
                entries.forEach(entry -> {
                    lore.add(ChatColor.GRAY + entry);
                });
            }

            ItemStack item = new ItemStack(_theWallTeam.Material);
            ItemMeta meta = item.getItemMeta();

            meta.setLore(lore);
            meta.setDisplayName(_theWallTeam.DisplayName);

            item.setItemMeta(meta);
            gui.addItem(item);
        });

        return gui;
    }
}
