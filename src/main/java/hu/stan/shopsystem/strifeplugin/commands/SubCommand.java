package hu.stan.shopsystem.strifeplugin.commands;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SubCommand {

    protected MainCommand mainCommand;
    private final String commandName;
    private String commandDescription = "";
    private String commandUsage = "";
    private List<String> commandAliases = new ArrayList<>();
    private Permission permission = null;

    public SubCommand(String commandName) {
        this.commandName = commandName;
    }

    public abstract void execute(Player player, String commandName, String label, String[] args);
    public abstract List<String> tabComplete(Player player, String label, String[] args);

    public String aliasesToString() {
        if (commandAliases == null || commandAliases.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < commandAliases.size(); i++) {
            builder.append(commandAliases.get(i));
            if (i + 1 != commandAliases.size()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public SubCommand setCommandManager(MainCommand mainCommand) {
        this.mainCommand = mainCommand;
        return this;
    }

    public SubCommand setCommandDescription(String commandDescription) {
        this.commandDescription = commandDescription;
        return this;
    }

    public SubCommand setCommandUsage(String commandUsage) {
        this.commandUsage = commandUsage;
        return this;
    }

    public SubCommand setCommandAliases(List<String> commandAliases) {
        this.commandAliases = commandAliases;
        return this;
    }

    public SubCommand addAliases(String... commandAliases) {
        this.commandAliases.addAll(Arrays.asList(commandAliases));
        return this;
    }

    public SubCommand setPermission(Permission permission) {
        this.permission = permission;
        return this;
    }

    public SubCommand setPermission(String permission) {
        this.permission = new Permission(permission);
        return this;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getCommandDescription() {
        return commandDescription;
    }

    public String getCommandUsage() {
        return commandUsage;
    }

    public List<String> getCommandAliases() {
        return commandAliases;
    }

    public Permission getPermission() {
        return permission;
    }
}
