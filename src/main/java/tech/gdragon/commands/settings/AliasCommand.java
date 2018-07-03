package tech.gdragon.commands.settings;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.commands.Command;
import tech.gdragon.commands.CommandHandler;
import tech.gdragon.configuration.ServerSettings;


public class AliasCommand implements Command {



    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 2)
            throw new IllegalArgumentException("This command require exactly two arguments");

        if (!CommandHandler.commands.containsKey(args[0].toLowerCase())) {
            Utilities.sendMessage(e.getChannel(), "Command '" + args[0].toLowerCase() + "' not found.");
            return;
        }

        if (CommandHandler.commands.containsValue(args[1].toLowerCase())) {
            Utilities.sendMessage(e.getChannel(), "Alias '" + args[1].toLowerCase() + "' already exists.");
            return;
        }

        ServerSettings.get(e.getGuild()).aliases.put(args[1].toLowerCase(), args[0].toLowerCase());
        ServerSettings.write();
        Utilities.sendMessage(e.getChannel(), "New alias '" + args[1].toLowerCase() + "' set for the command '" + args[0].toLowerCase() + "'.");

    }

    @Override
    public String usage(String prefix) {
        return prefix + "alias [command name] [new command alias]";
    }

    @Override
    public String description() {
        return "Creates an alias, or alternate name, to a command for customization.";
    }


}
