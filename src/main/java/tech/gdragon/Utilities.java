package tech.gdragon;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class Utilities {
    
    static public TextChannel findTextChannel(String tc, GuildMessageReceivedEvent event) throws IllegalArgumentException {

        TextChannel ret;
        if(tc == null) {
            ret = event.getChannel();

        } else {
            //cut off # in channel name if they included it
            if(tc.startsWith("#"))
                tc = tc.substring(1);

            List<TextChannel> candidates = event.getGuild().getTextChannelsByName(tc, true);
            if(candidates.size() == 0)
                throw new IllegalArgumentException("Cannot find specified text channel");

            ret = candidates.get(0);
        }
        return ret;
    }

    static public int parseUInt(String arg)throws IllegalArgumentException {
        int num;
        try {
            num = Integer.parseInt(arg);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid entered number", ex);
        }

        if (num <= 0) throw new IllegalArgumentException("Number must be strictly greater than 0!");

        return num
    }
}
