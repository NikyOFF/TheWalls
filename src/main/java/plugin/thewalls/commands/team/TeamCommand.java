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
import plugin.thewalls.TheWalls;
import plugin.thewalls.items.TeamSelector;

public class TeamCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            try
            {
                switch (args[0])
                {
                    //region add
                    case "add":
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return false;
                        }

                        if (args.length != 4)
                        {
                            throw new CommandException(Main.Singleton.getConfig().getString("localization.argumentError"));
                        }

                        ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                        Material material = Material.getMaterial(args[3].toUpperCase());

                        if ((!color.isColor()) || (material != null && material.isAir()))
                        {
                            throw new CommandException(Main.Singleton.getConfig().getString("localization.argumentError"));
                        }

                        Main.Singleton.TeamManager.Add(args[1], color, material);

                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Команда " + args[1] + " - была успешно добавлена!");

                        return true;
                    //endregion

                    //region setSpawnPoint
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
                                throw new CommandException(Main.Singleton.getConfig().getString("localization.teamNotFoundError"));
                            }

                            theWallTeam.SpawnPoint = player.getLocation();

                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Точка спавна команды " + args[1] + " - была успешно установлен!");
                        }
                        else if (args.length == 5)
                        {
                            TheWallTeam theWallTeam = Main.Singleton.TeamManager.Get(args[1]);

                            double x = Double.parseDouble(args[2]);
                            double y = Double.parseDouble(args[3]);
                            double z = Double.parseDouble(args[4]);

                            theWallTeam.SpawnPoint = new Location(player.getWorld(), x, y, z);

                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Точка спавна команды " + args[1] + " - была успешно установлен!");
                        }
                        else
                        {
                            throw new CommandException(Main.Singleton.getConfig().getString("localization.argumentError"));
                        }

                        return true;
                        //endregion

                    //region join
                    case "join":
                        if (args.length == 2)
                        {
                            TheWallTeam theWallTeam = Main.Singleton.TeamManager.Get(args[1]);

                            if (theWallTeam == null)
                            {
                                throw new CommandException("Команда не была найдена!");
                            }

                            theWallTeam.Join(player.getName());

                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Вы успешно вступили в команду " + theWallTeam.DisplayName + "!");
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

                            theWallTeam.Join(args[2]);

                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Игрок " + args[2] + " успешно вступил в команду " + theWallTeam.DisplayName + "!");
                        }
                        else
                        {
                            throw new CommandException(Main.Singleton.getConfig().getString("localization.argumentError"));
                        }

                        return true;
                    //endregion

                    //region leave
                    case "leave":
                        if (args.length == 2)
                        {
                            TheWallTeam theWallTeam = Main.Singleton.TeamManager.Get(args[1]);

                            if (theWallTeam == null)
                            {
                                throw new CommandException();
                            }

                            theWallTeam.Leave(player.getName());

                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Вы успешно покинули команду!");
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

                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Игрок " + args[2] + " был исключён из команды!");
                        }
                        else
                        {
                            throw new CommandException(Main.Singleton.getConfig().getString("localization.argumentError"));
                        }

                        return true;
                    //endregion

                    //region remove
                    case "remove":
                        if (args.length != 2)
                        {
                            throw new CommandException(Main.Singleton.getConfig().getString("localization.argumentError"));
                        }

                        Main.Singleton.TeamManager.Remove(args[1]);

                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Команда " + args[1] + " - была успешно удалена!");

                        return true;
                    //endregion

                    //region list
                    case "list":

                        if (args.length != 1)
                        {
                            Main.Singleton.getConfig().getString("localization.argumentError");
                        }

                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN,"Всего команд: " + Main.Singleton.TeamManager.Teams.size());
                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN,"Команд проиграло: " + Main.Singleton.TeamManager.LostTeamCount);

                        for (TheWallTeam _theWallTeam : Main.Singleton.TeamManager.Teams)
                        {
                            TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "- " + _theWallTeam.DisplayName + ChatColor.WHITE + ", участников: " + _theWallTeam.PlayersCount + ", живых игроков: " + _theWallTeam.LivePlayersCount + ", проиграла?: " + _theWallTeam.IsLost + ", заспавнилась?: " + _theWallTeam.IsSpawned);
                        }

                        return true;
                    //endregion

                    //region book
                    case "book":
                        if (Main.Singleton.RoundManager.Started)
                        {
                            throw new CommandException(Main.Singleton.getConfig().getString("localization.roundStartedError"));
                        }

                        player.getInventory().addItem(TeamSelector.TeamSelectorItem());

                        return true;
                    //endregion
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
        }



        return false;
    }
}
