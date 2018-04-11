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

    private static final String SETTINGS_PATH = ".\\settings.json";
    private static final String RECORDINGS_PATH = "C:\\www\\maschmalow.net\\DiscordFun\\";
    static {
        Path dir = Paths.get(RECORDINGS_PATH);
        if (Files.notExists(dir))
            Files.createDirectories(Paths.get(RECORDINGS_PATH));
    }

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


    public static void readSettings() {

        Gson gson = new Gson();

        FileReader fileReader = new FileReader(SETTINGS_PATH);

        Type type = new TypeToken<HashMap<String, GuildSettings>>() {
        }.getType();

        DiscordBot.serverSettings = gson.fromJson(fileReader, type);

        if (DiscordBot.serverSettings == null)
            DiscordBot.serverSettings = new HashMap<>();

        fileReader.close();

        boolean update = false; // :(
        for (Guild g : DiscordBot.jda.getGuilds()) {    //validate settings files
            if (!DiscordBot.serverSettings.containsKey(g.getId())) {
                DiscordBot.serverSettings.put(g.getId(), new GuildSettings(g));
                update = true;
            }
        }
        if(update)
            GuildSettings.writeSettingsJson();

    }

    //write the current state of all server settings to the settings.json file
    public static void writeSettingsJson() {
        Gson gson = new Gson();
        String json = gson.toJson(DiscordBot.serverSettings);

        FileWriter fw = new FileWriter(SETTINGS_PATH);
        fw.write(json);
        fw.flush();
        fw.close();

    }

}
