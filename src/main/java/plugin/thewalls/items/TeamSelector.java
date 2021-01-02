package plugin.thewalls.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TeamSelector {
    public static String TeamSelectorItemName = ChatColor.BOLD + "Выбрать команду";

    public static ItemStack TeamSelectorItem()
    {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add("Используйте этот предмет для выбора команды");

        meta.setDisplayName(TeamSelectorItemName);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }
}
