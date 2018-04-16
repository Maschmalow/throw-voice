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

import static java.lang.Thread.sleep;


public class EventListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        DiscordBot.settings.put(e.getGuild().getId(), new GuildSettings(e.getGuild()));
        GuildSettings.writeSettingsJson();
        System.out.format("Joined new server '%s', connected to %s guilds\n", e.getGuild().getName(), e.getJDA().getGuilds().size());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        DiscordBot.settings.remove(e.getGuild().getId());
        GuildSettings.writeSettingsJson();
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
            GuildSettings settings = DiscordBot.settings.get(e.getGuild().getId());
            int min = settings.autoJoinSettings.get(e.getChannelJoined().getId());

            if (newSize >= min && botSize < newSize) {  //check for tie with old server
                if (DiscordBot.settings.get(e.getGuild().getId()).autoSave)
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

        int min = DiscordBot.settings.get(e.getGuild().getId()).autoLeaveSettings.get(e.getChannelLeft().getId());
        int size = DiscordBot.voiceChannelSize(e.getChannelLeft());

        if (size <= min && e.getGuild().getAudioManager().getConnectedChannel() == e.getChannelLeft()) {

            if (DiscordBot.settings.get(e.getGuild().getId()).autoSave)
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
            GuildSettings settings = DiscordBot.settings.get(e.getGuild().getId());
            int min = settings.autoJoinSettings.get(e.getChannelJoined().getId());

            if (newSize >= min && botSize < newSize) {  //check for tie with old server
                if (DiscordBot.settings.get(e.getGuild().getId()).autoSave)
                    DiscordBot.writeToFile(e.getGuild());  //write data from voice channel it is leaving

                DiscordBot.joinVoiceChannel(e.getChannelJoined(), false);
            }

        } else {
            if (biggestChannel != null) {
                DiscordBot.joinVoiceChannel(biggestChannel, false);
            }
        }

        //Check if bot needs to leave old channel
        int min = DiscordBot.settings.get(e.getGuild().getId()).autoLeaveSettings.get(e.getChannelLeft().getId());
        int size = DiscordBot.voiceChannelSize(e.getChannelLeft());

        if (size <= min && e.getGuild().getAudioManager().getConnectedChannel() == e.getChannelLeft()) {

            if (DiscordBot.settings.get(e.getGuild().getId()).autoSave)
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

        String prefix = DiscordBot.settings.get(e.getGuild().getId()).prefix;
        //force help to always work with "!" prefix
        if (e.getMessage().getContent().startsWith(prefix) || e.getMessage().getContent().startsWith("!help")) {
            CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContent().toLowerCase(), e));
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (e.getAuthor() == null || e.getAuthor().isBot())
            return;

        if (e.getMessage().getContent().startsWith("!alerts")) {
            if (e.getMessage().getContent().endsWith("off")) {
                for (Guild g : e.getJDA().getGuilds()) {
                    if (g.getMember(e.getAuthor()) != null) {
                        DiscordBot.settings.get(g.getId()).alertBlackList.add(e.getAuthor().getId());
                    }
                }
                e.getChannel().sendMessage("Alerts now off, message `!alerts on` to re-enable at any time").queue();
                GuildSettings.writeSettingsJson();

            } else if (e.getMessage().getContent().endsWith("on")) {
                for (Guild g : e.getJDA().getGuilds()) {
                    if (g.getMember(e.getAuthor()) != null) {
                        DiscordBot.settings.get(g.getId()).alertBlackList.remove(e.getAuthor().getId());
                    }
                }
                e.getChannel().sendMessage("Alerts now on, message `!alerts off` to disable at any time").queue();
                GuildSettings.writeSettingsJson();
            } else {
                e.getChannel().sendMessage("!alerts [on | off]").queue();
            }

        /* removed because prefix and aliases are dependent on guild, which cannot be assumed without a message sent from guild
        } else if (e.getMessage().getContent().startsWith("!help")) {

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("Discord Echo", "http://DiscordEcho.com/", e.getJDA().getSelfUser().getAvatarUrl());
            embed.setColor(Color.RED);
            embed.setTitle("Currently in beta, being actively developed and tested. Expect bugs.");
            embed.setDescription("Join my guild for updates - https://discord.gg/JWNFSZJ");
            embed.setThumbnail("http://www.freeiconspng.com/uploads/information-icon-5.png");
            embed.setFooter("Replace brackets [] with item specified. Vertical bar | means 'or', either side of bar is valid choice.", "http://www.niceme.me");
            embed.addBlankField(false);

            Object[] cmds = CommandHandler.commands.keySet().toArray();
            Arrays.sort(cmds);
            for (Object command : cmds) {
                if (command == "help") continue;
                embed.addField(CommandHandler.commands.get(command).usage("!"), CommandHandler.commands.get(command).descripition(), true);
            }

            e.getChannel().sendMessage(embed.build()).queue();
        */
        } else {
            e.getChannel().sendMessage("DM commands unsupported, send `!help` in your guild chat for more info.").queue();
        }
    }

    @Override
    public void onReady(ReadyEvent e) {

        e.getJDA().getPresence().setGame(new Game() {
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

        GuildSettings.readSettings();

    }
}
