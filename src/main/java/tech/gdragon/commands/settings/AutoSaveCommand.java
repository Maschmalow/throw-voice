package tech.gdragon.commands.settings;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.commands.Command;


public class AutoSaveCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e) {
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 0) {
            String prefix = DiscordBot.settings.get(e.getGuild().getId()).prefix;
            DiscordBot.sendMessage(e.getChannel(), usage(prefix));
            return;
        }

        if (DiscordBot.settings.get(e.getGuild().getId()).autoSave) {
            DiscordBot.settings.get(e.getGuild().getId()).autoSave = false;
            DiscordBot.sendMessage(e.getChannel(), "No longer saving at the end of each session!");

        } else {
            DiscordBot.settings.get(e.getGuild().getId()).autoSave = true;
            DiscordBot.sendMessage(e.getChannel(), "Now saving at the end of each session!");
        }
    }

    @Override
    public String usage(String prefix) {
        return prefix + "autosave";
    }

    @Override
    public String descripition() {
        return "Toggles the option to automatically save and send all files at the end of each session - not just saved or clipped files";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e) {

    }
}
