package plugin.thewalls.commands.round;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import plugin.thewalls.Main;
import plugin.thewalls.TheWalls;

public class RoundCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            try {
                switch (args[0])
                {
                    //region start
                    case "start":
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return false;
                        }

                        Main.Singleton.RoundManager.Awake();

                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Раунд был успешно запущен!");
                        return true;
                    //endregion

                    //region stop
                    case "stop":
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return false;
                        }

                        Main.Singleton.RoundManager.Stop();

                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Вы прервали раунд!");
                        return true;
                    //endregion

                    //region info
                    case "info":
                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "На данный момент игра находиться в стадии: " + Main.Singleton.RoundManager.Stage + ", установленное время конца игры: " + Main.Singleton.RoundManager.GetEndTime() + ", установленное время падения стен: " + Main.Singleton.RoundManager.GetFallTime() + ", раунд запущен?: " + Main.Singleton.RoundManager.Started);

                        return true;
                    //endregion

                    //region setEndTime
                    case "setEndTime":
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return false;
                        }

                        if (args.length != 2)
                        {
                            throw new CommandException();
                        }

                        Main.Singleton.RoundManager.SetEndTime(Integer.parseInt(args[1]));

                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Время конца игра было установлено на " + args[1] + "!");

                        return true;
                    //endregion

                    //region setFallTime
                    case "setFallTime":
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return false;
                        }

                        if (args.length != 2)
                        {
                            throw new CommandException();
                        }
                        Main.Singleton.RoundManager.SetFallTime(Integer.parseInt(args[1]));

                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Время падения стен было установлено на " + args[1] + "!");

                         return true;
                    //endregion

                    //region setMaxBorderSize
                    case "setMaxBorderSize":
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return false;
                        }

                        if (args.length != 2)
                        {
                            throw new CommandException();
                        }
                        Main.Singleton.RoundManager.SetMaxBorderSize(Float.parseFloat(args[1]));

                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Максимальный размер барьера установлен на " + args[1] + "!");

                        return true;
                    //endregion

                    //region setMinBorderSize
                    case "setMinBorderSize":
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return false;
                        }

                        if (args.length != 2)
                        {
                            throw new CommandException();
                        }
                        Main.Singleton.RoundManager.SetMinBorderSize(Float.parseFloat(args[1]));

                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Минимальный размер барьера установлен на " + args[1] + "!");

                        return true;
                    //endregion

                    //region devMode
                    case "devMode":
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return false;
                        }

                        boolean devMode = !Main.Singleton.RoundManager.DevMode;

                        Main.Singleton.RoundManager.DevMode = devMode;

                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "DevMode: " + devMode);
                        return true;
                    //endregion

                    //region deadLighting
                    case "deadLighting":
                        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                        {
                            return false;
                        }

                        boolean deadLighting = !Main.Singleton.RoundManager.DeadLighting;

                        Main.Singleton.RoundManager.DeadLighting = deadLighting;

                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "DeadLighting: " + deadLighting);
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
