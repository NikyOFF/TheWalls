package plugin.thewalls.commands.wall;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import plugin.thewalls.Main;
import plugin.thewalls.TheWalls;
import plugin.thewalls.Wall;

public class WallCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
            {
                return false;
            }

            try
            {
                if (args.length > 0)
                {
                    switch (args[0])
                    {
                        //region add
                        case "add":
                            if (args.length != 8)
                            {
                                throw new CommandException();
                            }

                            String name = args[1];

                            double x0 = Double.parseDouble(args[2]);
                            double y0 = Double.parseDouble(args[3]);
                            double z0 = Double.parseDouble(args[4]);

                            double x1 = Double.parseDouble(args[5]);
                            double y1 = Double.parseDouble(args[6]);
                            double z1 = Double.parseDouble(args[7]);

                            Location pos0 = new Location(player.getWorld(), x0, y0, z0);
                            Location pos1 = new Location(player.getWorld(), x1, y1, z1);

                            Main.Singleton.WallManager.Add(name, pos0, pos1);
                            sender.sendMessage(ChatColor.GREEN + "[TheWalls]: " + ChatColor.WHITE + "Стена " + name + " - была успешно добавлена!");

                            return true;
                        //endregion

                        //region save
                        case "save":
                            if (args.length != 2)
                            {
                                throw new CommandException();
                            }

                            Wall wall = Main.Singleton.WallManager.Get(args[1]);

                            if (wall == null)
                            {
                                throw new CommandException();
                            }

                            wall.Save();

                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Стена " + args[1] + " - была успешно сохранена!");

                            return true;
                        //endregion

                        //region remove
                        case "remove":
                            if (args.length != 2)
                            {
                                throw new CommandException();
                            }

                            Wall _wall = Main.Singleton.WallManager.Get(args[1]);

                            if (_wall == null)
                            {
                                throw new CommandException();
                            }

                            Main.Singleton.WallManager.Remove(args[1]);

                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Стена " + args[1] + " - была успешно удалена!");

                            return true;
                        //endregion

                        //region create
                        case "create":
                            if (args.length != 2)
                            {
                                throw new CommandException();
                            }

                            Wall __wall = Main.Singleton.WallManager.Get(args[1]);

                            if (__wall == null)
                            {
                                throw new CommandException();
                            }

                            __wall.Create();

                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Стена " + args[1] + " - была успешно создана!");

                            return true;
                        //endregion

                        //region destroy
                        case "destroy":
                            if (args.length != 2)
                            {
                                throw new CommandException();
                            }

                            Wall ___wall = Main.Singleton.WallManager.Get(args[1]);

                            if (___wall == null)
                            {
                                throw new CommandException();
                            }

                            ___wall.Destroy();

                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Стена " + args[1] + " - была успешно уничтожена!");

                            return true;
                        //endregion

                        //region createAll
                        case "createAll":
                            Main.Singleton.WallManager.CreateAll();

                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Все стены были созданы!");

                            return true;
                        //endregion

                        //region destroyAll
                        case "destroyAll":
                            Main.Singleton.WallManager.DestroyAll();

                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Все стены были уничтожены!");

                            return true;
                        //endregion
                    }
                }

            }
            catch (CommandException error)
            {
                String message = error.getMessage();

                if (!message.isEmpty())
                {
                    TheWalls.SendTheWallsMessage(player, ChatColor.RED, message);
                }

                return false;
            }
        }

        return false;
    }
}
