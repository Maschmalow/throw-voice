package tech.gdragon.commands.settings;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.commands.Command;
import tech.gdragon.configuration.ServerSettings;

import java.util.Map;


public class RemoveAliasCommand implements Command {


    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) throws IllegalArgumentException {
        if (args.length != 1)
            throw new IllegalArgumentException("This command requires exactly one argument");

        String alias = args[0].toLowerCase();
        Map<String, String> aliases = ServerSettings.get(e.getGuild()).aliases;

        if (!aliases.containsKey(alias))
            throw new IllegalArgumentException("Alias " + alias + " does not exist.");

        aliases.remove(alias);
        ServerSettings.write();
        DiscordBot.sendMessage(e.getChannel(), "Alias '" + alias + "' has been removed.");

    }

    @Override
    public String usage(String prefix) {
        return prefix + "removeAlias [alias name]";
    }

    @Override
    public String description() {
        return "Removes an alias from a command.";
    }

}
