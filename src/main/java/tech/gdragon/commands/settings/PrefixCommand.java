package tech.gdragon.commands.settings;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.commands.Command;
import tech.gdragon.configuration.ServerSettings;


public class PrefixCommand implements Command {

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) throws IllegalArgumentException {
        if (args.length != 1)
            throw new IllegalArgumentException("This command require exactly one argument");
        if(args[0].length() != 1)
            throw new IllegalArgumentException("Prefix must be exactly one character");

        ServerSettings.get(e.getGuild()).prefix = args[0];
        ServerSettings.write();

        DiscordBot.sendMessage(e.getChannel(), "Command prefix now set to " + args[0]);
    }

    @Override
    public String usage(String prefix) {
        return prefix + "prefix [character]";
    }

    @Override
    public String description() {
        return "Sets the prefix for each command to avoid conflict with other bots (Default is '!')";
    }

}
