package tech.gdragon.commands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import tech.gdragon.DiscordBot;
import tech.gdragon.configuration.GuildSettings;
import tech.gdragon.configuration.ServerSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    public static Map<String, Command> commands = new HashMap<>();

    public static void handleCommand(GuildMessageReceivedEvent event) {
        CommandContainer cmd = new CommandContainer(event);
        GuildSettings settings = ServerSettings.get(event.getGuild());

        Command command = commands.get(cmd.invoke);
        if(command == null)
            command = commands.get(settings.aliases.get(cmd.invoke));
        if(command == null)
            return;

        try {
            command.action(cmd.args, cmd.e);
        } catch(IllegalArgumentException e) {
            DiscordBot.sendMessage(event.getChannel(), e.getMessage()+"\n"+command.usage(settings.prefix));
        }

    }


    private static class CommandContainer {
        public final String raw;
        public final String beheaded;
        public final String[] splitBeheaded;
        public final String invoke;
        public final String[] args;
        public final GuildMessageReceivedEvent e;

        public CommandContainer(GuildMessageReceivedEvent event) {
            this.raw = event.getMessage().getContentRaw().toLowerCase();
            ArrayList<String> split = new ArrayList<>();
            this.beheaded = raw.substring(1);
            this.splitBeheaded = beheaded.split(" ");
            Collections.addAll(split, splitBeheaded);
            this.invoke = split.get(0);
            this.args = new String[split.size() - 1];
            split.subList(1, split.size()).toArray(args);

            this.e = event;
        }
    }
}

