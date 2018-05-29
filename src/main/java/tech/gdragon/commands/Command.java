package tech.gdragon.commands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public interface Command {

    void action(String[] args, GuildMessageReceivedEvent e) throws IllegalArgumentException;

    String usage(String prefix);

    String description();

}
