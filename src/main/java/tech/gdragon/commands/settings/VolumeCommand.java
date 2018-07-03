package tech.gdragon.commands.settings;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.commands.Command;
import tech.gdragon.configuration.ServerSettings;


public class VolumeCommand implements Command {

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) throws IllegalArgumentException {
        if(args.length != 1)
            throw new IllegalArgumentException("This command requires exactly one argument");

        int num;
        try {
            num = Integer.parseInt(args[0]);
        } catch(Exception ex) {
            throw new IllegalArgumentException("Invalid volume entered", ex);
        }

        if(num < 1 || num > 100)
            throw new IllegalArgumentException("Volume must be between 1 and 100");


        ServerSettings.get(e.getGuild()).volume = (double) num / 100.0;
        ServerSettings.write();

        Utilities.sendMessage(e.getChannel(), "Volume set to " + num + "% for next recording!");


    }

    @Override
    public String usage(String prefix) {
        return prefix + "volume [1-100]";
    }

    @Override
    public String description() {
        return "Sets the percentage volume to record at, from 1-100%";
    }

}
