package tech.gdragon.commands.settings;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.commands.Command;


public class VolumeCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e) {
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 1) {
            String prefix = DiscordBot.settings.get(e.getGuild().getId()).prefix;
            DiscordBot.sendMessage(e.getChannel(), usage(prefix));
            return;
        }

        try {
            int num = Integer.parseInt(args[0]);

            if (num > 0 && num <= 100) {
                DiscordBot.settings.get(e.getGuild().getId()).volume = (double) num / 100.0;
                DiscordBot.writeSettingsJson();

                DiscordBot.sendMessage(e.getChannel(), "Volume set to " + num + "% for next recording!");

            } else {
                String prefix = DiscordBot.settings.get(e.getGuild().getId()).prefix;
                DiscordBot.sendMessage(e.getChannel(), usage(prefix));
            }

        } catch (Exception ex) {
            String prefix = DiscordBot.settings.get(e.getGuild().getId()).prefix;
            DiscordBot.sendMessage(e.getChannel(), usage(prefix));
        }
    }

    @Override
    public String usage(String prefix) {
        return prefix + "volume [1-100]";
    }

    @Override
    public String descripition() {
        return "Sets the percentage volume to record at, from 1-100%";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e) {

    }
}
