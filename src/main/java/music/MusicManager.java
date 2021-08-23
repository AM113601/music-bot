package music;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MusicManager extends ListenerAdapter {

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;
    private EventWaiter waiter;

    public MusicManager(EventWaiter waiter) {
        this.musicManagers = new HashMap<>();
        this.waiter = waiter;
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    private boolean checkConnected(GuildMessageReceivedEvent event){
        VoiceChannel connectedChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
        // Checks if the bot is connected to a voice channel.
        if (connectedChannel == null) {
            // Tells user that they aren't in voice channel.
            event.getChannel().sendMessage("I am not connected to a voice channel!").queue();
            return false;
        }
        return true;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ", 3);
        if ("!play".equals(command[0])) {
            if(command.length == 2){ //playing a song once
                if (checkConnected(event)) {
                    loadAndPlay(event.getChannel(), command[1]);
                }
            }
            else if(command.length == 3){ //playing a song multiple times
                try{
                    int x = Integer.parseInt(command[1]);
                    if (checkConnected(event)) {
                        for(int i = 0; i < x; i++){
                            loadAndPlay(event.getChannel(), command[2]);
                        }
                    }
                }catch(NumberFormatException e){
                    event.getChannel().sendMessage("Invalid command.").queue();
                }
            }
        } else if ("!skip".equals(command[0])) {
            if (checkConnected(event)) {
                skipTrack(event.getChannel());

                GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());
                if (musicManager.scheduler.getQueue().isEmpty()) {
                    event.getChannel().sendMessage("No more tracks in queue.").queue();
                } else {
                    event.getChannel().sendMessage("Skipped to next track.").queue();
                }
            }
        } else if ("!clearQ".equals(command[0])) {
            if (checkConnected(event)) {
                GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());
                musicManager.scheduler.getQueue().clear();
                skipTrack(event.getChannel());
                event.getChannel().sendMessage("Cleared Queue.").queue();
            }
        } else if("!showQ".equals(command[0])){ //SHOWS THE QUEUE.
                showQueue(event);
        } else if("!showC".equals(command[0])){ //SHOWS the CURRENT SONG
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());
            EmbedBuilder current = new EmbedBuilder();
            try {
                //AudioTrackInfo info = musicManager.player.getPlayingTrack().getInfo();
                //event.getChannel().sendMessage(musicManager.player.getPlayingTrack().getInfo().title).queue();
                String title = musicManager.player.getPlayingTrack().getInfo().title;
                String uri = musicManager.player.getPlayingTrack().getInfo().uri;

                current.setTitle(title);
                current.setDescription(uri + "\n" + formatTime(musicManager.player.getPlayingTrack().getPosition())
                + " - " + formatTime(musicManager.player.getPlayingTrack().getDuration()));
                current.addField("Made By:", "Turtle2739", false);

                event.getChannel().sendTyping().queue();
                event.getChannel().sendMessage(current.build()).queue();
            }catch(NullPointerException e){
                event.getChannel().sendMessage("Nothing is playing at the moment.").queue();
            }
        } else if("!volume".equals(command[0])){
            try{
                GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());
                musicManager.player.setVolume(Integer.parseInt(command[1]));
                event.getChannel().sendTyping().queue();
                event.getChannel().sendMessage("Changed volume to " + Integer.parseInt(command[1])).queue();
            }catch(NumberFormatException e){
                event.getChannel().sendMessage("Type in a valid Integer.").queue();
            }
        } else if ("!leave".equals(command[0]) && command.length == 1) {
            if(!checkConnected(event)){
                return;
            }
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());
            musicManager.player.stopTrack();
            // Disconnect from the channel.
            event.getGuild().getAudioManager().closeAudioConnection();
            // Notify the user.
            event.getChannel().sendMessage("Disconnected from the voice channel!").queue();
        } else if ("!removeQ".equals(command[0]) && command.length == 1){
            if(getGuildAudioPlayer(event.getChannel().getGuild()).scheduler.getQueue().isEmpty()){
                event.getChannel().sendMessage("No songs in queue.").queue();
                return;
            }
            removeQueue(event);
        }
        super.onGuildMessageReceived(event);
    }

    private void removeQueue(GuildMessageReceivedEvent event){
        String messageSent = event.getMessage().getContentRaw();
        if(messageSent.equalsIgnoreCase("!removeQ")) {
            showQueue(event);
            event.getChannel().sendMessage("Which music would you like to delete? (Enter a number)").queue();

            //GuildMessageReceived event, condition, and action as the parameters
            waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()) //this
                    && e.getChannel().equals(event.getChannel()) && !e.getMessage().getContentRaw().equals(messageSent), e -> {
                String messageReceived = e.getMessage().getContentRaw();
                try{
                    int number = Integer.parseInt(messageReceived);
                    removeQueueHelper(event, number);
                }catch(NumberFormatException e1){
                    event.getChannel().sendMessage("Not a valid integer. Try again.").queue();
                }
            }, 10, TimeUnit.SECONDS, () -> event.getChannel().sendMessage("You waited too long. Try again.").queue());
        }
    }

    private void removeQueueHelper(GuildMessageReceivedEvent event, int number) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());
        BlockingQueue<AudioTrack> Q = musicManager.scheduler.getQueue(); //name for actual queue
        EmbedBuilder queue = new EmbedBuilder(); //displays text
        AudioTrack removedAudio = null;
        if (number - 1 > Q.size()) {
            event.getChannel().sendMessage("Not in valid range. Try again.").queue();
            return;
        }
        Iterator<AudioTrack> i = Q.iterator();

        for (int j = 0; j < number; j++){
            removedAudio = i.next();
        }
        Q.remove(removedAudio);

        queue.setTitle("Song deleted successfully.");
        queue.setColor(0xf45642);
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(queue.build()).queue();
    }


    private void showQueue(GuildMessageReceivedEvent event){
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());
        BlockingQueue<AudioTrack> tempQ = musicManager.scheduler.getQueue(); //name for actual queue
        EmbedBuilder queue = new EmbedBuilder(); //displays text
        String str = ""; //StringBuilder

        //if queue is empty
        if(tempQ.isEmpty()){
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("No songs in queue.").queue();
            return;
        }

        //iterates through queue
        Iterator<AudioTrack> i = tempQ.iterator();
        int k = 0;
        long totalDuration = 0;
        while(i.hasNext()){
            AudioTrack tempAT = i.next();
            String title = tempAT.getInfo().title;
            str += (k + 1) + ". " + title + "   Duration: " + formatTime(tempAT.getDuration()) + "\n";
            k = k + 1;
            totalDuration += tempAT.getDuration(); //adds song length to total duration
        }

        str += "\n Total Duration: " + formatTime(totalDuration) + "\n";
        queue.setTitle("Songs in queue:");
        queue.setDescription(str);
        queue.addField("Made By:", "Turtle2739", false);
        queue.setColor(0xf45642);
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(queue.build()).queue();
    }



    /**
     *  Formats the time for Embed in showC.
     */
    private String formatTime(long timeInMs){
        final long hours = timeInMs / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMs / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMs % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void loadAndPlay(final TextChannel channel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

                play(channel.getGuild(), musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                play(channel.getGuild(), musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());

        musicManager.scheduler.queue(track);
    }

    private void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                audioManager.openAudioConnection(voiceChannel);
                break;
            }
        }
    }
}