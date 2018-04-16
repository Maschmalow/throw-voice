package tech.gdragon.listeners;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import tech.gdragon.DiscordBot;
import tech.gdragon.commands.CommandHandler;
import tech.gdragon.configuration.GuildSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.gdragon.configuration.ServerSettings;

import static java.lang.Thread.sleep;


public class EventListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        ServerSettings.updateGuilds();
        System.out.format("Joined new server '%s', connected to %s guilds\n", e.getGuild().getName(), e.getJDA().getGuilds().size());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        ServerSettings.updateGuilds();
        System.out.format("Left server '%s', connected to %s guilds\n", e.getGuild().getName(), e.getJDA().getGuilds().size());
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
        if (e.getMember() == null || e.getMember().getUser() == null || e.getMember().getUser().isBot())
            return;

        VoiceChannel biggestChannel = DiscordBot.biggestChannel(e.getGuild().getVoiceChannels());

        if (e.getGuild().getAudioManager().isConnected()) {

            int newSize = DiscordBot.voiceChannelSize(e.getChannelJoined());
            int botSize = DiscordBot.voiceChannelSize(e.getGuild().getAudioManager().getConnectedChannel());
            GuildSettings settings = ServerSettings.get(e.getGuild());
            int min = settings.autoJoinSettings.get(e.getChannelJoined().getId());

            if (newSize >= min && botSize < newSize) {  //check for tie with old server
                if (ServerSettings.get(e.getGuild()).autoSave)
                    DiscordBot.writeToFile(e.getGuild());  //write data from voice channel it is leaving

                DiscordBot.joinVoiceChannel(e.getChannelJoined(), false);
            }

        } else {
            if (biggestChannel != null) {
                DiscordBot.joinVoiceChannel(e.getChannelJoined(), false);
            }
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
        if (e.getMember() == null || e.getMember().getUser() == null || e.getMember().getUser().isBot())
            return;

        int min = ServerSettings.get(e.getGuild()).autoLeaveSettings.get(e.getChannelLeft().getId());
        int size = DiscordBot.voiceChannelSize(e.getChannelLeft());

        if (size <= min && e.getGuild().getAudioManager().getConnectedChannel() == e.getChannelLeft()) {

            if (ServerSettings.get(e.getGuild()).autoSave)
                DiscordBot.writeToFile(e.getGuild());  //write data from voice channel it is leaving

            DiscordBot.leaveVoiceChannel(e.getGuild().getAudioManager().getConnectedChannel());

            VoiceChannel biggest = DiscordBot.biggestChannel(e.getGuild().getVoiceChannels());
            if (biggest != null) {
                DiscordBot.joinVoiceChannel(biggest, false);
            }
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent e) {
        if (e.getMember() == null || e.getMember().getUser() == null || e.getMember().getUser().isBot())
            return;

        //Check if bot needs to join newly joined channel
        VoiceChannel biggestChannel = DiscordBot.biggestChannel(e.getGuild().getVoiceChannels());

        if (e.getGuild().getAudioManager().isConnected()) {

            int newSize = DiscordBot.voiceChannelSize(e.getChannelJoined());
            int botSize = DiscordBot.voiceChannelSize(e.getGuild().getAudioManager().getConnectedChannel());
            GuildSettings settings = ServerSettings.get(e.getGuild());
            int min = settings.autoJoinSettings.get(e.getChannelJoined().getId());

            if (newSize >= min && botSize < newSize) {  //check for tie with old server
                if (ServerSettings.get(e.getGuild()).autoSave)
                    DiscordBot.writeToFile(e.getGuild());  //write data from voice channel it is leaving

                DiscordBot.joinVoiceChannel(e.getChannelJoined(), false);
            }

        } else {
            if (biggestChannel != null) {
                DiscordBot.joinVoiceChannel(biggestChannel, false);
            }
        }

        //Check if bot needs to leave old channel
        int min = ServerSettings.get(e.getGuild()).autoLeaveSettings.get(e.getChannelLeft().getId());
        int size = DiscordBot.voiceChannelSize(e.getChannelLeft());

        if (size <= min && e.getGuild().getAudioManager().getConnectedChannel() == e.getChannelLeft()) {

            if (ServerSettings.get(e.getGuild()).autoSave)
                DiscordBot.writeToFile(e.getGuild());  //write data from voice channel it is leaving

            DiscordBot.leaveVoiceChannel(e.getGuild().getAudioManager().getConnectedChannel());

            VoiceChannel biggest = DiscordBot.biggestChannel(e.getGuild().getVoiceChannels());
            if (biggest != null) {
                DiscordBot.joinVoiceChannel(e.getChannelJoined(), false);
            }
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getMember() == null || e.getMember().getUser() == null || e.getMember().getUser().isBot())
            return;

        String prefix = ServerSettings.get(e.getGuild()).prefix;

        if (e.getMessage().getContent().startsWith(prefix)) {
            CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContent().toLowerCase(), e));
        }
    }


    @Override
    public void onReady(ReadyEvent e) {

        DiscordBot.jda.getPresence().setGame(new Game() {
            @Override
            public String getName() {
                return "!help | maschmalow.net";
            }

            @Override
            public String getUrl() {
                return "https://maschmalow.net";
            }

            @Override
            public GameType getType() {
                return GameType.DEFAULT;
            }
        });
        System.out.format("ONLINE: Connected to %s guilds!\n", e.getJDA().getGuilds().size());

    }
}
