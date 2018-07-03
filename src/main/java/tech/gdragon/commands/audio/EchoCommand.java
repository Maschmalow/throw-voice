package tech.gdragon.commands.audio;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.Utilities;
import tech.gdragon.commands.Command;
import tech.gdragon.audio.AudioReceiveListener;
import tech.gdragon.audio.AudioSendListener;


public class EchoCommand implements Command {



    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 1)
            throw new IllegalArgumentException("This command require exactly one argument");

        if (e.getGuild().getAudioManager().getConnectedChannel() == null) {
            Utilities.sendMessage(e.getChannel(), "I wasn't recording!");
            return;
        }

        int time = Utilities.parseUInt(args[0]);

        AudioReceiveListener ah = (AudioReceiveListener) e.getGuild().getAudioManager().getReceiveHandler();
        byte[] voiceData;
        if (ah == null || (voiceData = ah.getUncompVoice(time)) == null) {
            Utilities.sendMessage(e.getChannel(), "I wasn't recording!");
            return;
        }

        AudioSendListener as = new AudioSendListener(voiceData);
        e.getGuild().getAudioManager().setSendingHandler(as);

    }

    @Override
    public String usage(String prefix) {
        return prefix + "echo [seconds]";
    }

    @Override
    public String description() {
        return "Echos back the input number of seconds of the recording into the voice channel (max 120 seconds)";
    }


}
