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


    public int autoJoinSettings;
    public int autoLeaveSettings;
    public HashMap<String, String> aliases;
    public boolean autoSave;
    public String prefix;
    public double volume;
    public String defaultTextChannel;

    public GuildSettings(Guild g) {
        this.autoJoinSettings = Integer.MAX_VALUE;
        this.autoLeaveSettings = 1;
        this.aliases = new HashMap<>();

        //assign default aliases
        this.aliases.put("info", "help");
        this.aliases.put("record", "join");
        this.aliases.put("stop", "leave");
        this.aliases.put("symbol", "prefix");


        this.autoSave = false;
        this.prefix = "!";
        this.volume = 0.8;
        this.defaultTextChannel = g.getDefaultChannel().getId();


    }


}
