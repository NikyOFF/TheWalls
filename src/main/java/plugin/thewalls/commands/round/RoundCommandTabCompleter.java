package plugin.thewalls.commands.round;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoundCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (sender instanceof Player)
        {
            if (args.length == 1)
            {
                return Stream.of(RoundCommandActions.values()).map(Enum::name).collect(Collectors.toList());
            }
        }

        return null;
    }
}
