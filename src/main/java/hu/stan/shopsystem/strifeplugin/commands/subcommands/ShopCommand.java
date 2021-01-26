package hu.stan.shopsystem.strifeplugin.commands.subcommands;

import hu.stan.shopsystem.strifeplugin.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class ShopCommand extends SubCommand {
    public ShopCommand(String commandName) {
        super(commandName);
    }

    @Override
    public void execute(Player player, String commandName, String label, String[] args) {

    }

    @Override
    public List<String> tabComplete(Player player, String label, String[] args) {
        return null;
    }
}
