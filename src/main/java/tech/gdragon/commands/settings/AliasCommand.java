package tech.gdragon.commands.settings;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.commands.Command;
import tech.gdragon.commands.CommandHandler;


public class AliasCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e) {
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 2) {
            String prefix = DiscordBot.settings.get(e.getGuild().getId()).prefix;
            DiscordBot.sendMessage(e.getChannel(), usage(prefix));
            return;
        }

        if (!CommandHandler.commands.containsKey(args[0].toLowerCase())) {
            DiscordBot.sendMessage(e.getChannel(), "Command '" + args[0].toLowerCase() + "' not found.");
            return;
        }

        if (CommandHandler.commands.containsValue(args[1].toLowerCase())) {
            DiscordBot.sendMessage(e.getChannel(), "Alias '" + args[1].toLowerCase() + "' already exists.");
            return;
        }

        DiscordBot.settings.get(e.getGuild().getId()).aliases.put(args[1].toLowerCase(), args[0].toLowerCase());
        DiscordBot.writeSettingsJson();
        DiscordBot.sendMessage(e.getChannel(), "New alias '" + args[1].toLowerCase() + "' set for the command '" + args[0].toLowerCase() + "'.");

    }

    @Override
    public String usage(String prefix) {
        return prefix + "alias [command name] [new command alias]";
    }

    @Override
    public String descripition() {
        return "Creates an alias, or alternate name, to a command for customization.";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e) {

    }
}
