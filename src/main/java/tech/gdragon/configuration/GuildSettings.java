package tech.gdragon.configuration;

import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;

public class GuildSettings {


    public int autoJoinSettings;
    public int autoLeaveSettings;
    public HashMap<String, String> aliases;
    public boolean autoSave;
    public String prefix;
    public double volume;

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


    }


}
