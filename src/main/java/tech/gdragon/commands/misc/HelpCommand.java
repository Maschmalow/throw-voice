package tech.gdragon.commands.misc;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.commands.Command;
import tech.gdragon.commands.CommandHandler;
import tech.gdragon.configuration.ServerSettings;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class HelpCommand implements Command {



    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Throw Voice", "https://github.com/Maschmalow/throw-voice", e.getJDA().getSelfUser().getAvatarUrl());
        embed.setColor(Color.RED);
        embed.setTitle("Currently in beta, being actively developed and tested. Expect bugs.");
        embed.setDescription("Throw Voice was created from Discord Echo, join their guild for updates - https://discord.gg/JWNFSZJ\nStripped down & reworked version by Maschmalow");
        embed.setThumbnail("http://www.freeiconspng.com/uploads/information-icon-5.png");
        embed.setFooter("Replace brackets [] with item specified. Vertical bar | means 'or', either side of bar is valid choice.", null);
        embed.addBlankField(false);


        for (Command cmd : CommandHandler.commands.values()) {

            String prefix = ServerSettings.get(e.getGuild()).prefix;

            ArrayList<String> aliases = new ArrayList<>();
            for (Map.Entry<String, String> entry : ServerSettings.get(e.getGuild()).aliases.entrySet()) {
                if (entry.getValue().equals(command))
                    aliases.add(entry.getKey());
            }

            if (aliases.size() == 0)
                embed.addField(cmd.usage(prefix), cmd.description(), true);
            else {
                String description = "";
                description += "Aliases: ";
                for (String alias : aliases)
                    description += "`" + alias + "`, ";

                //remove extra comma
                description = description.substring(0, description.lastIndexOf(','));
                description += ". " + cmd.description();
                embed.addField(cmd.usage(prefix), description, true);
            }
        }

        DiscordBot.sendMessage(e.getChannel(), "Check your DM's!");

        e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(embed.build()).queue());
    }

    @Override
    public String usage(String prefix) {
        return prefix + "help";
    }

    @Override
    public String description() {
        return "Shows all commands and their usages";
    }


}
