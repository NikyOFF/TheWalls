package com.nikyoff.thewalls.core;

import com.boydti.fawe.util.EditSessionBuilder;
import com.nikyoff.thewalls.Main;
import com.nikyoff.thewalls.utils.Localization;
import com.nikyoff.thewalls.utils.Messages;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import com.sk89q.worldedit.regions.Region;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Wall {
    public String Id;
    public Location Pos0;
    public Location Pos1;
    public Region Region;

    public Wall(String id, Location pos0, Location pos1) {
        this.Id = id;
        this.Pos0 = pos0;
        this.Pos1 = pos1;
        this.Region = new CuboidRegion(
                BukkitAdapter.adapt(pos0.getWorld()),
                BlockVector3.at(pos0.getX(), pos0.getY(),pos0.getZ()),
                BlockVector3.at(pos1.getX(), pos1.getY(), pos1.getZ())
        );
    }

    public void Create(boolean isFallen) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to create wall: " + this.Id + ", isFallen: " + isFallen);

        if (Main.Singleton.MapManager == null || Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        File file;
        String currentPath = Main.Singleton.SchematicsPath + "/" + Main.Singleton.MapManager.CurrentMap.Id;

        if (isFallen) {
            file = new File(currentPath, this.Id + "_fallen.schem");
        }
        else {
            file = new File(currentPath, this.Id + ".schem");
        }

        if (file.exists()) {
            ClipboardFormat format = ClipboardFormats.findByFile(file);

            if (format != null) {
                try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                    Clipboard clipboard = reader.read();

                    try (EditSession editSession = new EditSession(new EditSessionBuilder(BukkitAdapter.adapt(Bukkit.getWorld("world"))))) {
                        Operation operation = new ClipboardHolder(clipboard)
                                .createPaste(editSession)
                                .to(this.Region.getMinimumPoint())
                                .ignoreAirBlocks(false)
                                .build();
                        Operations.complete(operation);
                    }
                } catch (IOException | WorldEditException e) {

                }
            }

        }
        else if (isFallen) {
            try (EditSession editSession = new EditSession(new EditSessionBuilder(BukkitAdapter.adapt(Bukkit.getWorld("world"))))) {
                editSession.setBlocks(this.Region, BlockTypes.AIR);
            } catch (MaxChangedBlocksException e) {

            }
        }

        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Wall created");
    }

    public void Save(boolean isFallen) {
        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.BLUE, "Trying to create wall: " + this.Id + ", isFallen: " + isFallen);

        if (Main.Singleton.MapManager == null || Main.Singleton.MapManager.CurrentMap == null) {
            return;
        }

        File file;
        String currentPath = Main.Singleton.SchematicsPath + "/" + Main.Singleton.MapManager.CurrentMap.Id;

        if (isFallen) {
            file = new File(currentPath, this.Id + "_fallen.schem");
        }
        else {
            file = new File(currentPath, this.Id + ".schem");
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {

            }
        }

        BlockArrayClipboard clipboard = new BlockArrayClipboard(this.Region);

        try (EditSession editSession = new EditSession(new EditSessionBuilder(BukkitAdapter.adapt(Bukkit.getWorld("world"))))) {
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                    editSession, this.Region, clipboard, this.Region.getMinimumPoint()
            );
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {

        }

        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        } catch (IOException e) {

        }

        if (Main.Singleton.Debug) Messages.SendConsoleMessage(ChatColor.GREEN, "Wall saved");
    }
}
