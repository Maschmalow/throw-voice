package tech.gdragon;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.audio.AudioProcessing;
import tech.gdragon.audio.AudioReceiveListener;
import tech.gdragon.configuration.ServerSettings;

public class Utilities {
    
    static public TextChannel findTextChannel(String tc, GuildMessageReceivedEvent event) throws IllegalArgumentException {
        if(tc == null)
            return event.getChannel();

        if ( tc.startsWith("#"))
            tc = tc.substring(1);
        try{
            return event.getGuild().getTextChannelsByName(tc, true).get(0);
        } catch(IndexOutOfBoundsException exception) {
            throw new IllegalArgumentException("Cannot find specified text channel");
        }
    }

    static public int parseUInt(String tc)throws IllegalArgumentException {
        int num;
        try {
            num = Integer.parseInt(tc);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid entered number", ex);
        }

        if (num <= 0) throw new IllegalArgumentException("Number must be strictly greater than 0!");

        return num;
    }


    //general purpose function that sends a message to the given text channel and handles errors
    public static void sendMessage(TextChannel tc, String message) {
        tc.sendMessage("\u200B" + message).queue(null,
                (Throwable) -> tc.getGuild().getDefaultChannel().sendMessage("\u200BI don't have permissions to send messages in " + tc.getName() + "!").queue());
    }

}
