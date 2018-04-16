package tech.gdragon.configuration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import tech.gdragon.DiscordBot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class GuildSettings {



    public HashMap<String, Integer> autoJoinSettings;
    public HashMap<String, Integer> autoLeaveSettings;
    public HashMap<String, String> aliases;
    public boolean autoSave;
    public ArrayList<String> alertBlackList;
    public String prefix;
    public double volume;
    public String defaultTextChannel;

    public GuildSettings(Guild g) {
        this.autoJoinSettings = new HashMap<>(g.getVoiceChannels().size());
        this.autoLeaveSettings = new HashMap<>(g.getVoiceChannels().size());
        this.aliases = new HashMap<>();

        //assign default aliases
        this.aliases.put("info", "help");
        this.aliases.put("record", "join");
        this.aliases.put("stop", "leave");
        this.aliases.put("symbol", "prefix");

        for (VoiceChannel vc : g.getVoiceChannels()) {
            this.autoJoinSettings.put(vc.getId(), Integer.MAX_VALUE);
            this.autoLeaveSettings.put(vc.getId(), 1);
        }

        this.autoSave = false;
        this.alertBlackList = new ArrayList<>();
        this.prefix = "!";
        this.volume = 0.8;
        this.defaultTextChannel = g.getPublicChannel().getId();


    }



}
