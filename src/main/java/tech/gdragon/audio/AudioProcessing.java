package tech.gdragon.audio;

import de.sciss.jump3r.lowlevel.LameEncoder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import tech.gdragon.Utilities;
import tech.gdragon.configuration.ServerSettings;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;

public class AudioProcessing {

    public static void writeToFile(Guild guild, String filename, TextChannel tc, Integer time) {
        if(tc == null) {
            tc = guild.getDefaultChannel();
        }


        AudioReceiveListener ah = (AudioReceiveListener) guild.getAudioManager().getReceiveHandler();
        if(ah == null) {
            Utilities.sendMessage(tc, "I wasn't recording!");
            return;
        }

        if(filename == null)
            filename = "untitled_recording";
        filename = DateFormat.getDateInstance().format(new Date()) + filename + ".mp3";

        File dest = Paths.get(ServerSettings.getRecordingsPath(), filename ).toFile();


        byte[] voiceData;
        if(time != null && time > 0 && time <= AudioReceiveListener.PCM_MINS * 60 * 2)
            voiceData = encodePcmToMp3(ah.getUncompVoice(time));
        else
            voiceData = ah.getVoiceData();

        try {
            FileOutputStream fos = new FileOutputStream(dest);
            fos.write(voiceData);
            fos.close();
        } catch(IOException ex) {
            ex.printStackTrace();
            Utilities.sendMessage(tc, "Error saving file: "+ex.getMessage());
        }

        System.out.format("Saved audio file '%s' from %s on %s of size %f MB\n",
                dest.getName(), guild.getAudioManager().getConnectedChannel().getName(), guild.getName(), (double) dest.length() / 1024 / 1024);

        if(dest.length() / 1024 / 1024 < 8) {
            final TextChannel channel = tc;
            tc.sendFile(dest).queue(message -> {
                        dest.delete();
                        System.out.println("\tDeleting file " + dest.getName() + "...");
                    },
                    (Throwable) -> Utilities.sendMessage(guild.getTextChannelById(ServerSettings.get(guild).defaultTextChannel),
                            "I don't have permissions to send files in " + channel.getName() + "!"));

        } else {
            Utilities.sendMessage(tc, ServerSettings.getRecordingsURL() + dest.getName());
        }


    }


    //encode the passed array of PCM (uncompressed) audio to mp3 audio data
    public static byte[] encodePcmToMp3(byte[] pcm) {
        LameEncoder encoder = new LameEncoder(new AudioFormat(48000.0f, 16, 2, true, true), 128, LameEncoder.CHANNEL_MODE_AUTO, LameEncoder.QUALITY_HIGHEST, false);
        ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
        byte[] buffer = new byte[encoder.getPCMBufferSize()];

        int bytesToTransfer = Math.min(buffer.length, pcm.length);
        int bytesWritten;
        int currentPcmPosition = 0;
        while(0 < (bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer))) {
            currentPcmPosition += bytesToTransfer;
            bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);

            mp3.write(buffer, 0, bytesWritten);
        }

        encoder.close();

        return mp3.toByteArray();
    }

    //kill off the audio handlers and clear their memory for the given guild
    static void killAudioHandlers(Guild g) {
        AudioReceiveListener ah = (AudioReceiveListener) g.getAudioManager().getReceiveHandler();
        if(ah != null) {
            ah.canReceive = false;
            ah.compVoiceData = null;
            g.getAudioManager().setReceivingHandler(null);
        }

        AudioSendListener sh = (AudioSendListener) g.getAudioManager().getSendingHandler();
        if(sh != null) {
            sh.canProvide = false;
            sh.voiceData = null;
            g.getAudioManager().setSendingHandler(null);
        }

        System.out.println("Destroyed audio handlers for " + g.getName());
        System.gc();
    }

    //general purpose function for leaving voice channels
    public static void leaveVoiceChannel(Guild guild) {
        System.out.format("Leaving voice channel in %s\n", guild.getName());

        if(ServerSettings.get(guild).autoSave)
            writeToFile(guild, "autosave", null, null);  //write data from voice channel it is leaving

        guild.getAudioManager().closeAudioConnection();
        killAudioHandlers(guild);
    }

    public static void joinVoiceChannel(VoiceChannel vc) {
        joinVoiceChannel(vc, false);
    }

    //general purpose function for joining voice channels while warning and handling errors
    public static void joinVoiceChannel(VoiceChannel vc, boolean warning) {
        System.out.format("Joining '%s' voice channel in %s\n", vc.getName(), vc.getGuild().getName());

        //don't join afk channels
        if(vc == vc.getGuild().getAfkChannel()) {
            if(warning) {
                TextChannel tc = vc.getGuild().getDefaultChannel();
                Utilities.sendMessage(tc, "I don't join afk channels!");
            }
        }

        //attempt to join channel and warn if permission is not available
        try {
            vc.getGuild().getAudioManager().openAudioConnection(vc);
        } catch(Exception e) {
            if(warning) {
                TextChannel tc = vc.getGuild().getDefaultChannel();
                Utilities.sendMessage(tc, "I don't have permission to join " + vc.getName() + "!");
                return;
            }
        }


        //initalize the audio reciever listener
        double volume = ServerSettings.get(vc.getGuild()).volume;
        vc.getGuild().getAudioManager().setReceivingHandler(new AudioReceiveListener(volume, vc));

    }
}
