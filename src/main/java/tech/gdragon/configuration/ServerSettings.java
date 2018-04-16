package tech.gdragon.configuration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.core.entities.Guild;
import tech.gdragon.DiscordBot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ServerSettings {

    private static ServerSettings instance;
    private static final String SETTINGS_PATH = "settings.json";

    private String recordingsPath;
    private Map<String, GuildSettings> guildsSettings;
    private String botToken;


    public static GuildSettings get(Guild g) {
        return instance.guildsSettings.get(g.getId());
    }

    public static void updateGuilds() {
        boolean update = false; // :(
        for (Guild g : DiscordBot.jda.getGuilds()) {
            if (!instance.guildsSettings.containsKey(g.getId())) {
                instance.guildsSettings.put(g.getId(), new GuildSettings(g));
                update = true;
            }
        }
        if (update)
            ServerSettings.write();
    }


    public static void read() {
        try {
            FileReader fileReader = new FileReader(SETTINGS_PATH);
            instance = new Gson().fromJson(fileReader, ServerSettings.class);
            fileReader.close();
            if (instance == null)
                throw new RuntimeException("Empty configuration file");
        } catch (IOException e) {
            throw new RuntimeException("Could not read configuration file at " + SETTINGS_PATH, e);
        }

        if (Files.notExists(Paths.get(instance.recordingsPath))) {
            try { //try to create recording directory if not existing
                Files.createDirectories(Paths.get(instance.recordingsPath));
            } catch (IOException ignored) {
            }
        }
    }


    //write the current state of all server settings to the settings.json file
    public static void write() {
        try {
            FileWriter fw = new FileWriter(SETTINGS_PATH);
            new Gson().toJson(instance, fw);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write configuration file at " + SETTINGS_PATH, e);
        }
    }

    public static Path getRecordingsPath() {
        return Paths.get(instance.recordingsPath);
    }

    public static String getBotToken() {
        return instance.botToken;
    }
}
