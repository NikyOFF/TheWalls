package plugin.thewalls.commands.wall;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import plugin.thewalls.Main;
import plugin.thewalls.Wall;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WallCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> strings = new ArrayList<>();

        if (!sender.hasPermission(String.valueOf(Permission.DEFAULT_PERMISSION)))
        {
            return null;
        }

        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            if (args.length == 1)
            {
                return Stream.of(WallCommandActions.values()).map(Enum::name).collect(Collectors.toList());
            }

            if (args.length > 1)
            {
                if (args[0].equals("add"))
                {
                    if (args.length == 3)
                    {
                        strings.add(String.valueOf(player.getTargetBlockExact(4).getX()));
                    }

                    if (args.length == 4)
                    {
                        strings.add(String.valueOf(player.getTargetBlockExact(4).getY()));
                    }

                    if (args.length == 5)
                    {
                        strings.add(String.valueOf(player.getTargetBlockExact(4).getZ()));
                    }

                    if (args.length == 6)
                    {
                        strings.add(String.valueOf(player.getTargetBlockExact(4).getX()));
                    }

                    if (args.length == 7)
                    {
                        strings.add(String.valueOf(player.getTargetBlockExact(4).getY()));
                    }

                    if (args.length == 8)
                    {
                        strings.add(String.valueOf(player.getTargetBlockExact(4).getZ()));
                    }
                }

                if (args[0].equals("save"))
                {
                    if (args.length == 2)
                    {
                        for (Wall _wall : Main.Singleton.WallManager.Walls)
                        {
                            strings.add(_wall.Name);
                        }
                    }
                }

                if (args[0].equals("remove"))
                {
                    if (args.length == 2)
                    {
                        for (Wall _wall : Main.Singleton.WallManager.Walls)
                        {
                            strings.add(_wall.Name);
                        }
                    }
                }

                if (args[0].equals("create"))
                {
                    if (args.length == 2)
                    {
                        for (Wall _wall : Main.Singleton.WallManager.Walls)
                        {
                            strings.add(_wall.Name);
                        }
                    }
                }

                if (args[0].equals("destroy"))
                {
                    if (args.length == 2)
                    {
                        for (Wall _wall : Main.Singleton.WallManager.Walls)
                        {
                            strings.add(_wall.Name);
                        }
                    }
                }

                return strings;
            }
        }

        return null;
    }
}
