package tech.gdragon.commands.misc;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.commands.Command;


public class LeaveCommand implements Command {



    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 0)
            throw new IllegalArgumentException("This command takes no argument");

        if (!e.getGuild().getAudioManager().isConnected())
            throw new IllegalArgumentException("I am not in a channel!");

        DiscordBot.leaveVoiceChannel(e.getGuild());

    }

    @Override
    public String usage(String prefix) {
        return prefix + "leave";
    }

    @Override
    public String description() {
        return "Force the bot to leave it's current channel";
    }


}
