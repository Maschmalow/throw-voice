package tech.gdragon.commands;

import tech.gdragon.DiscordBot;
import tech.gdragon.configuration.GuildSettings;
import tech.gdragon.configuration.ServerSettings;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    public static final CommandParser parser = new CommandParser();
    public static Map<String, Command> commands = new HashMap<>();

    public static void handleCommand(CommandParser.CommandContainer cmd) {
        GuildSettings settings = ServerSettings.get(cmd.e.getGuild());

        Command command = commands.get(cmd.invoke);
        if (command == null)
            command = commands.get(settings.aliases.get(cmd.invoke));
        if (command == null)
            return;


        Boolean safe = command.called(cmd.args, cmd.e);

        if (safe)
            command.action(cmd.args, cmd.e);

        command.executed(safe, cmd.e);
    }
}

