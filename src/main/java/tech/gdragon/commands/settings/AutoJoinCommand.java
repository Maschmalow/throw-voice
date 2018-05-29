package tech.gdragon.commands.settings;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.Utilities;
import tech.gdragon.commands.Command;
import tech.gdragon.configuration.ServerSettings;


public class AutoJoinCommand implements Command {


    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if(args.length != 1)
            throw new IllegalArgumentException("This command require exactly one argument");

        int num = Utilities.parseUInt(args[0]);
        if(num == 0)
            num = Integer.MAX_VALUE;

        ServerSettings.get(e.getGuild()).autoJoinSettings = num;
        ServerSettings.write();

        if(num != Integer.MAX_VALUE)
            DiscordBot.sendMessage(e.getChannel(), "Will now automatically join any voice channel with " + num + " people");
        else
            DiscordBot.sendMessage(e.getChannel(), "Will no longer automatically join any channel");

    }

    @Override
    public String usage(String prefix) {
        return prefix + "autojoin [number]";
    }

    @Override
    public String description() {
        return "Sets the number of players for the bot to auto-join a voice channel. Set to 0 to disable entirely.";
    }


}
