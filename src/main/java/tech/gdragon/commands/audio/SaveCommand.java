package tech.gdragon.commands.audio;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.Utilities;
import tech.gdragon.commands.Command;


public class SaveCommand implements Command {


    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if(args.length > 1)
            throw new IllegalArgumentException("This command require no more than two arguments");

        if(e.getGuild().getAudioManager().getConnectedChannel() == null)
            throw new IllegalArgumentException("I wasn't recording!");


        TextChannel savingChannel = Utilities.findTextChannel((args.length == 0) ? null : args[0], e);

        DiscordBot.writeToFile(e.getGuild(), savingChannel);

    }

    @Override
    public String usage(String prefix) {
        return prefix + "save | " + prefix + "save [text channel output]";
    }

    @Override
    public String description() {
        return "Saves the current recording and outputs it to the current or specified text chats (caps at 16MB)";
    }


}
