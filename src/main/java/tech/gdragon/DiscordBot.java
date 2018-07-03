package tech.gdragon;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import tech.gdragon.configuration.ServerSettings;

import javax.security.auth.login.LoginException;

public class DiscordBot {
    //contains the id of every guild that we are connected to and their corresponding GuildSettings object
    public static ServerSettings settings;
    public static JDA jda;


    public DiscordBot() {

        ServerSettings.read();
        ServerSettings.updateGuilds(); //in case we were kicked while offline


        try {
            DiscordBot.jda = new JDABuilder(AccountType.BOT)
                    .setToken(ServerSettings.getBotToken())
                    .addEventListener(new EventListener())
                    .buildBlocking();
        } catch(LoginException | InterruptedException e) {
            throw new RuntimeException("Could not login", e);
        }

    }




}
