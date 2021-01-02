package plugin.thewalls.commands.team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import plugin.thewalls.Main;
import plugin.thewalls.TheWallTeam;

public class TeamCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (sender instanceof Player)
        {
            player = (Player) sender;
        }

        try
        {
            switch (args[0])
            {
                case "add":
                    if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                    {
                        return false;
                    }

                    ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                    Material material = Material.getMaterial(args[3].toUpperCase());

                    if (!color.isColor())
                    {
                        throw new CommandException("Цвет " + color + " - не был найден!");
                    }

                    if (material != null && material.isAir())
                    {
                        throw new CommandException("Материал " + material + " - не был найден или является ничем!");
                    }

                    Main.Singleton.TeamManager.Add(args[1], color, material);

                    sender.sendMessage(ChatColor.GREEN + "[TheWalls]: " + ChatColor.WHITE + " Команда " + args[1] + " - была успешно добавлена!");

                    return true;
                case "setSpawnPoint":
                    if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                    {
                        return false;
                    }

                    if (args.length == 2)
                    {
                        TheWallTeam theWallTeam = Main.Singleton.TeamManager.Get(args[1]);

                        if (theWallTeam == null)
                        {
                            throw new CommandException("Команда не была найдена!");
                        }

                        if (player != null)
                        {
                            theWallTeam.SpawnPoint = player.getLocation();
                        }
                        else
                        {
                            throw new CommandException("Исполнитель команды не является игроком!");
                        }

                        sender.sendMessage(ChatColor.GREEN + "[TheWalls]: " + ChatColor.WHITE + " Точка спавна команды " + args[1] + " - была успешно установлен!");
                        return true;
                    }
                    else if (args.length == 5)
                    {
                        TheWallTeam theWallTeam = Main.Singleton.TeamManager.Get(args[1]);

                        double x = Double.parseDouble(args[2]);
                        double y = Double.parseDouble(args[3]);
                        double z = Double.parseDouble(args[4]);

                        if (player != null)
                        {
                            theWallTeam.SpawnPoint = new Location(player.getWorld(), x, y, z);
                            sender.sendMessage(ChatColor.GREEN + "[TheWalls]: " + ChatColor.WHITE + " Точка спавна команды " + args[1] + " - была успешно установлен!");
                            return true;
                        }
                        else
                        {
                            throw new CommandException();
                        }
                    }
                    else
                    {
                        throw new CommandException();
                    }
                case "join":
                    if (args.length == 2)
                    {
                        TheWallTeam theWallTeam = Main.Singleton.TeamManager.Get(args[1]);

                        if (theWallTeam == null)
                        {
                            throw new CommandException("Команда не была найдена!");
                        }

                        if (player != null)
                        {
                            sender.sendMessage(ChatColor.GREEN + "[TheWalls]: " + ChatColor.WHITE + " Вы успешно вступили в команду " + theWallTeam.Color + args[1] + ChatColor.WHITE + "!");
                            theWallTeam.Join(player.getName());
                        }
                    }
                    else if (args.length == 3)
                    {
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return false;
                        }

                        Player entry = Bukkit.getPlayer(args[2]);
                        TheWallTeam theWallTeam = Main.Singleton.TeamManager.Get(args[1]);

                        if (theWallTeam == null)
                        {
                            throw new CommandException();
                        }

                        if (entry == null)
                        {
                            throw new CommandException();
                        }

                        sender.sendMessage(ChatColor.GREEN + "[TheWalls]: " + ChatColor.WHITE + " Игрок " + args[2] + " успешно вступил в команду " + theWallTeam.Color + args[1] + ChatColor.WHITE + "!");
                        theWallTeam.Join(args[2]);
                    }
                    else
                    {
                        throw new CommandException();
                    }

                    return true;
                case "leave":
                    if (args.length == 2)
                    {
                        TheWallTeam theWallTeam = Main.Singleton.TeamManager.Get(args[1]);

                        if (theWallTeam == null)
                        {
                            throw new CommandException();
                        }

                        if (player != null)
                        {
                            theWallTeam.Leave(player.getName());
                        }
                    }
                    else if (args.length == 3)
                    {
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return false;
                        }

                        Player entry = Bukkit.getPlayer(args[2]);
                        TheWallTeam theWallTeam = Main.Singleton.TeamManager.Get(args[1]);

                        if (theWallTeam == null)
                        {
                            throw new CommandException();
                        }

                        if (entry == null)
                        {
                            throw new CommandException();
                        }

                        theWallTeam.Leave(args[2]);
                    }
                    else
                    {
                        throw new CommandException();
                    }

                    return true;
                case "remove":
                    if (args.length != 2)
                    {
                        throw new CommandException();
                    }

                    Main.Singleton.TeamManager.Remove(args[1]);

                    if (player != null)
                    {
                        sender.sendMessage(ChatColor.GREEN + "[TheWalls]: " + ChatColor.WHITE + "Команда " + args[1] + " - была успешно удалена!");
                    }

                    return true;
                case "list":

                    if (args.length != 1)
                    {
                        throw new CommandException();
                    }

                    sender.sendMessage(ChatColor.GREEN + "[TheWalls]: " + ChatColor.WHITE + "Всего команд: " + Main.Singleton.TeamManager.Teams.size());
                    sender.sendMessage(ChatColor.GREEN + "[TheWalls]: " + ChatColor.WHITE + "Команд проиграло: " + Main.Singleton.TeamManager.LostTeamCount);

                    for (TheWallTeam _theWallTeam : Main.Singleton.TeamManager.Teams)
                    {
                        sender.sendMessage("- " + _theWallTeam.DisplayName + ChatColor.WHITE + ", участников: " + _theWallTeam.PlayersCount + ", живых игроков: " + _theWallTeam.LivePlayersCount + ", проиграла?: " + _theWallTeam.IsLost + ", заспавнилась?: " + _theWallTeam.IsSpawned);
                    }

                    return true;
            }
        }
        catch (CommandException error)
        {
            String message = error.getMessage();

            if (!message.isEmpty())
            {
                sender.sendMessage(ChatColor.RED + "[TheWalls]: " + ChatColor.WHITE + message);
            }

            return false;
        }

        return false;
    }
}
