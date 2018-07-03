package tech.gdragon.commands.audio;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.Utilities;
import tech.gdragon.commands.Command;
import tech.gdragon.commands.CommandHandler;

import static java.lang.Thread.sleep;


public class MessageInABottleCommand implements Command {



    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length < 2)
            throw new IllegalArgumentException("");

        if (e.getGuild().getAudioManager().getConnectedChannel() == null) {
            Utilities.sendMessage(e.getChannel(), "I wasn't recording!");
            return;
        }


        int time = Utilities.parseUInt(args[0]);

        String name = "";
        for (int i = 1; i < args.length; i++) {
            name += args[i] + " ";
        }
        name = name.substring(0, name.length() - 1);

        if (e.getGuild().getVoiceChannelsByName(name, true).size() == 0) {
            Utilities.sendMessage(e.getChannel(), "Cannot find voice channel '" + name + "'.");
            return;
        }

        VoiceChannel originalVC = e.getGuild().getAudioManager().getConnectedChannel();
        VoiceChannel newVC = e.getGuild().getVoiceChannelsByName(name, true).get(0);

        try {
            e.getGuild().getAudioManager().openAudioConnection(newVC);
        } catch (Exception ex) {
            Utilities.sendMessage(e.getChannel(), "I don't have permission to join " + newVC.getName() + "!");
            return;
        }

        CommandHandler.commands.get("echo").action(new String[]{args[0]}, e);

        new Thread(() -> {
            try {
                sleep(1000 * time);
            } catch(InterruptedException exception) {
                throw new RuntimeException(exception);
            }

            try {
                e.getGuild().getAudioManager().openAudioConnection(originalVC);
            } catch (Exception ex) {
                Utilities.sendMessage(e.getChannel(), "I don't have permission to join " + originalVC.getName() + "!");
            }

        }).start();
    }

    @Override
    public String usage(String prefix) {
        return prefix + "miab [seconds] [voice channel]";
    }

    @Override
    public String description() {
        return "Echos back the input number of seconds of the recording into the voice channel specified and then rejoins original channel (max 120 seconds)";
    }


}
