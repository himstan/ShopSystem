package hu.stan.shopsystem.strifeplugin.commands;

import hu.stan.shopsystem.strifeplugin.DreamPlugin;
import hu.stan.shopsystem.strifeplugin.commands.subcommands.HelpCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.logging.Logger;

public class MainCommand implements CommandExecutor, TabCompleter {

    private DreamPlugin plugin;
    private Logger logger;
    private SubCommand defaultCommand;
    private String mainCommandName;
    private Map<String, SubCommand> dreamCommands = new LinkedHashMap<>();
    private Map<String, SubCommand> dreamCommandAliases = new LinkedHashMap<>();

    public MainCommand(DreamPlugin plugin, String mainCommandName) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.mainCommandName = mainCommandName;
        setupDefaultCommand();
    }

    private void setupDefaultCommand() {
        this.defaultCommand = new HelpCommand("help")
                .setCommandDescription("Displays help")
                .setCommandUsage("help <page>");
        addSubCommand(defaultCommand);
    }

    /**
     * Registers a subcommand under the managed main command.
     * @param command A Command that needs to extend the SubCommand class.
     */

    public void addSubCommand(SubCommand command) {
        String commandName = command.getCommandName();
        List<String> commandAliases = command.getCommandAliases();
        if (checkIfCommandExists(commandName)) {
            logger.warning("Can't register subcommand: [" + commandName + "] Reason: Command already exists!");
            return;
        }
        command.setCommandManager(this);
        dreamCommands.put(commandName,command);
        logger.info("Registering subcommand: [" + commandName + "]");
        if (commandAliases != null && !commandAliases.isEmpty()) {
            addAliases(command);
        }
    }

    /**
     * Registers an array of subcommands.
     * @param subCommands A collection of subcommands that will be registered under the managed main command.
     */

    public void addSubCommands(SubCommand... subCommands) {
        Arrays.stream(subCommands).forEach(this::addSubCommand);
    }

    /**
     * Registers the given SubCommand's aliases under the managed main command.
     * @param command The SubCommand that will be registered under the given aliases.
     */

    private void addAliases(SubCommand command) {
        List<String> aliases = command.getCommandAliases();
        for (String alias : aliases) {
            if (checkIfCommandExists(alias)) {
                logger.warning("Can't register alias: [" + alias + "] under command: [" + command.getCommandName() + "] Reason: A command/alias is already registered with this name! \n" +
                        " Skipping registering alias...");
                continue;
            }
            logger.info("Registering alias: [" + alias + "] under command: [" + command.getCommandName() + "]");
            dreamCommandAliases.put(alias,command);
        }
    }

    /**
     * Checks if there is a SubCommand under the specified command name.
     * @param commandName The command you want to check for.
     * @return Returns true if there is a command, else false.
     */

    private boolean checkIfCommandExists(String commandName) {
        return (dreamCommands.containsKey(commandName) || dreamCommandAliases.containsKey(commandName));
    }

    /**
     * Returns the command that is under the specified command name.
     * @param commandName The command name or alias you want to get the command with.
     * @return Returns the command if the command exists, returns null if there was no command.
     */

    private SubCommand getCommand(String commandName) {
        SubCommand command = dreamCommands.get(commandName);
        if (command != null) {
            return command;
        }
        return dreamCommandAliases.get(commandName);
    }

    /**
     * @return Returns a list of SubCommand commands under the managed main command. (This will not include aliases)
     */

    public List<String> getCommandNames() {
        return new ArrayList<>(dreamCommands.keySet());
    }

    /**
     * Returns a list of SubCommand commands under the managed main command that is available for the given player.
     * @param player The player you want to check availiable commands for.
     * @return Returns a List of SubCommand commands for the given player.
     */

    public List<String> getPlayerCommandNames(Player player) {
        List<String> commandNames = new ArrayList<>();
        for (SubCommand command : dreamCommands.values()) {
            if (command.getPermission() == null || player.hasPermission(command.getPermission())) {
                commandNames.add(command.getCommandName());
            }
        }
        return commandNames;
    }

    /**
     * Returns a list of SubCommands under the managed main command that is available for the given player.
     * @param player The player you want to check availiable commands for.
     * @return Returns a List of SubCommands for the given player.
     */

    public List<SubCommand> getPlayerCommands(Player player) {
        List<SubCommand> commands = new ArrayList<>();
        for (SubCommand command : dreamCommands.values()) {
            if (command.getPermission() == null || player.hasPermission(command.getPermission())) {
                commands.add(command);
            }
        }
        commands.remove(defaultCommand);
        return commands;
    }

    /**
     * @return Returns the default command for this main command. This will usually be the help command.
     */

    public SubCommand getDefaultCommand() {
        return defaultCommand;
    }

    /**
     * @return Returns the main command's name.
     */

    public String getMainCommandName() {
        return mainCommandName;
    }

    public DreamPlugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String[] newArgs = new String[0];
            if (args.length > 0) {
                String subCommandName = args[0];
                SubCommand dCommand = getCommand(subCommandName);
                if (dCommand == null) {
                    player.sendRawMessage(ChatColor.RED + "This command doesn't exist.");
                    return true;
                }
                if (dCommand.getPermission() == null || player.hasPermission(dCommand.getPermission())) {
                    if (args.length > 1) {
                        newArgs = Arrays.copyOfRange(args, 1, args.length);
                    }
                    dCommand.execute(player,subCommandName,label,newArgs);
                } else {
                    player.sendRawMessage(ChatColor.RED + "You don't have permission to use this command!");
                }
            } else {
                defaultCommand.execute(player, defaultCommand.getCommandName(), label, args);
            }
        } else {
            sender.sendMessage("Only players can use commands!");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String[] newArgs = new String[0];
            if (args.length > 0) {
                String subCommandName = args[0];
                SubCommand dCommand = getCommand(subCommandName);
                if (dCommand != null) {
                    if (dCommand.getPermission() == null || player.hasPermission(dCommand.getPermission())) {
                        if (args.length > 1) {
                            newArgs = Arrays.copyOfRange(args, 1, args.length);
                        }
                        return dCommand.tabComplete(player,subCommandName,newArgs);
                    }
                } else {
                    return StringUtil.copyPartialMatches(subCommandName, getPlayerCommandNames(player), new ArrayList<>());
                }
            }
        }
        return Collections.emptyList();
    }
}
