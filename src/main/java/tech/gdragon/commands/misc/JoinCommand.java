package tech.gdragon.commands.misc;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.commands.Command;
import tech.gdragon.configuration.ServerSettings;


public class JoinCommand implements Command {



    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 0)
            throw new IllegalArgumentException("This command takes no argument");

        if (e.getGuild().getAudioManager().getConnectedChannel() != null &&
                e.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(e.getMember()))
            throw new IllegalArgumentException("I am already in your channel!");


        VoiceChannel memberChannel = e.getMember().getVoiceState().getChannel();
        if (memberChannel == null)
            throw new IllegalArgumentException("You need to be in a voice channel to use this command!");

        //write out previous channel's audio if autoSave is on
        if (e.getGuild().getAudioManager().isConnected() && ServerSettings.get(e.getGuild()).autoSave)
            DiscordBot.writeToFile(e.getGuild());

        DiscordBot.joinVoiceChannel(memberChannel, true);
    }

    @Override
    public String usage(String prefix) {
        return prefix + "join";
    }

    @Override
    public String description() {
        return "Force the bot to join and record your current channel";
    }


}
