package plugin.thewalls;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Wall {
    public final String Name;
    public final Location Pos0;
    public final Location Pos1;

    public List<Material> Materials = new ArrayList<>();
    public List<Block> Blocks;

    public Wall(String name, Location pos0, Location pos1)
    {
        this.Name = name;
        this.Pos0 = pos0;
        this.Pos1 = pos1;

        this.Blocks = this.getBlocks();

        for (int i = 0; i < this.Blocks.size(); i++)
        {
            this.Materials.add(i, this.Blocks.get(i).getType());
        }
    }

    public void Create()
    {
        Bukkit.getScheduler().runTask(Main.Singleton, () -> {
            for (int i = 0; i < Blocks.size(); i++)
            {
                Blocks.get(i).setType(Materials.get(i));
            }
        });
    }

    public void Destroy()
    {
        Bukkit.getScheduler().runTask(Main.Singleton, () -> {
            for (int i = 0; i < Blocks.size(); i++)
            {
                Blocks.get(i).setType(Material.AIR);
            }
        });
    }

    public void Save()
    {
        this.Materials.clear();

        for (int i = 0; i < this.Blocks.size(); i++)
        {
            this.Materials.add(i, this.Blocks.get(i).getType());
        }
    }

    private List<Block> getBlocks()
    {
        List<Block> blocks = new ArrayList<Block>();

        int topBlockX = (Math.max(this.Pos0.getBlockX(), this.Pos1.getBlockX()));
        int bottomBlockX = (Math.min(this.Pos0.getBlockX(), this.Pos1.getBlockX()));

        int topBlockY = (Math.max(this.Pos0.getBlockY(), this.Pos1.getBlockY()));
        int bottomBlockY = (Math.min(this.Pos0.getBlockY(), this.Pos1.getBlockY()));

        int topBlockZ = (Math.max(this.Pos0.getBlockZ(), this.Pos1.getBlockZ()));
        int bottomBlockZ = (Math.min(this.Pos0.getBlockZ(), this.Pos1.getBlockZ()));

        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block = Pos0.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }
}
