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
        Player player = null;

        if (sender instanceof Player)
        {
            player = (Player) sender;
        }

        try {
            switch (args[0])
            {
                case "start":
                    if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                    {
                        return false;
                    }

                    Main.Singleton.RoundManager.Awake();

                    if (player != null)
                    {
                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Раунд был успешно запущен!");
                        return true;
                    }

                    return false;
                case "stop":
                    if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
                    {
                        return false;
                    }

                    Main.Singleton.RoundManager.Stop();

                    if (player != null)
                    {
                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Вы прервали раунд!");
                        return true;
                    }

                    return false;
                case "info":
                    if (player != null)
                    {
                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "На данный момент игра находиться в стадии: " + Main.Singleton.RoundManager.Stage + ", установленное время конца игры: " + Main.Singleton.RoundManager.GetEndTime() + ", установленное время падения стен: " + Main.Singleton.RoundManager.GetFallTime() + ", раунд запущен?: " + Main.Singleton.RoundManager.Started);
                    }
                    return true;
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
                    if (player != null)
                    {
                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Время конца игра было установлено на " + args[1] + "!");
                    }
                    return true;
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
                    if (player != null)
                    {
                        TheWalls.SendTheWallsMessage(player, ChatColor.GREEN, "Время падения стен было установлено на " + args[1] + "!");
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
