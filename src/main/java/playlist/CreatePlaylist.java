package playlist;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class CreatePlaylist extends ListenerAdapter {

    private EventWaiter waiter;

    public CreatePlaylist(EventWaiter waiter){this.waiter = waiter;}

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if (message.equals("!playlist")) {
            event.getChannel().sendMessage("What option would you like to do? \n 1) Add Playlist \n 2) " +
                    "Add Song to Playlist \n 3) Remove Playlist \n 4) Play Playlist").queue();

            waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor())
                    && e.getChannel().equals(event.getChannel()) && !e.getMessage().getContentRaw().equals(message), e -> {
                String messageReceived = e.getMessage().getContentRaw();
                if(messageReceived.equals("1")){
                    createPlaylist(event);
                }else if(messageReceived.equals("2")){
                    add(event);
                }else if(messageReceived.equals("3")){
                    removePlaylist(event);
                }else if(messageReceived.equals("4")){
                    playPlaylist(event);
                }else{
                    event.getChannel().sendMessage("Not a valid command. Try again.").queue();
                }
            }, 10, TimeUnit.SECONDS, () -> event.getChannel().sendMessage("You waited too long. Try again.").queue());
        }
    }

    /**
     * Plays the Playlist.
     *
     * @param event
     */
    private void playPlaylist(GuildMessageReceivedEvent event) {
        String message = "What is the name of the playlist you want to play? (Don't add extensions/spaces!)";
        event.getChannel().sendMessage(message).queue();
        allPlaylists(event);
        waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()) //this
                && e.getChannel().equals(event.getChannel()) && !e.getMessage().getContentRaw().equals(message), e -> {
            String playlistTitle = e.getMessage().getContentRaw();
            try {
                File file = new File("/Users/shulimen/Desktop/Playlist_bot/" + playlistTitle + ".txt");
                try {
                    Scanner sc = new Scanner(file);
                    while (sc.hasNextLine()) {
                        event.getChannel().sendMessage(sc.nextLine()).queue();
                    }
                    sc.close();
                }
                catch (FileNotFoundException e1) {
                    event.getChannel().sendMessage("No file exists.").queue();
                }
            } catch (Exception e1) {
                event.getChannel().sendMessage("An error occurred.").queue();
            }
        }, 10, TimeUnit.SECONDS, () -> event.getChannel().sendMessage("You waited too long. Try again.").queue());

    }

    /**
     * Deletes a playlist.
     *
     * @param event
     */
    private void removePlaylist(GuildMessageReceivedEvent event) {
        String message = "What is the name of the playlist to be deleted? (Don't add extensions/spaces!)";
        event.getChannel().sendMessage(message).queue();
        allPlaylists(event);
        waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()) //this
                && e.getChannel().equals(event.getChannel()) && !e.getMessage().getContentRaw().equals(message), e -> {
            String messageReceived = e.getMessage().getContentRaw();
            try {
                File myObj = new File("/Users/shulimen/Desktop/Playlist_bot/" + messageReceived + ".txt");
                if (myObj.delete()) {
                    event.getChannel().sendMessage("File deleted: " + myObj.getName()).queue();
                } else{
                    event.getChannel().sendMessage("Failed to delete the file or file does not exist.").queue();
                }
            } catch (Exception e1) {
                event.getChannel().sendMessage("An error occurred.").queue();
            }
        }, 10, TimeUnit.SECONDS, () -> event.getChannel().sendMessage("You waited too long. Try again.").queue());
    }

    /**
     * Adds a song to a playlist.
     *
     * @param event
     */
    private void add(GuildMessageReceivedEvent event) {
        String message = "Provide the name of the playlist. Do not give extension.";
        event.getChannel().sendMessage(message).queue();
        allPlaylists(event);
        waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor())
                && e.getChannel().equals(event.getChannel()) && !e.getMessage().getContentRaw().equals(message), e -> {
            String playListReceived = e.getMessage().getContentRaw(); // gets playlist name
            String song = "Provide a valid link of the song.";
            event.getChannel().sendMessage(song).queue();
            waiter.waitForEvent(GuildMessageReceivedEvent.class, e1 -> e1.getAuthor().equals(event.getAuthor())
                    && e1.getChannel().equals(event.getChannel()) && !e1.getMessage().getContentRaw().equals(song), e1 -> {
                try {
                    String songTitle = e1.getMessage().getContentRaw();
                    Writer myWriter = new BufferedWriter(new FileWriter("/Users/shulimen/Desktop/Playlist_bot/" + playListReceived + ".txt", true));
                    //FileWriter myWriter = new FileWriter("/Users/shulimen/Desktop/Playlist_bot/" + playListReceived + ".txt");
                    myWriter.append("!play " + songTitle + "\n");
                    myWriter.close();
                    event.getChannel().sendMessage("Successfully wrote to the file.").queue();
                }catch(FileNotFoundException e2){
                    event.getChannel().sendMessage("Playlist not found.").queue();
                }catch (IOException e2) {
                    System.out.println("An error occurred.");
                }

            }, 30, TimeUnit.SECONDS, () -> event.getChannel().sendMessage("You waited too long. Try again.").queue());


        }, 30, TimeUnit.SECONDS, () -> event.getChannel().sendMessage("You waited too long. Try again.").queue());
    }

    /**
     * Gets us the name of all of the files in the playlist.
     * @param event
     */
    private void allPlaylists(GuildMessageReceivedEvent event) {
        File folder = new File("/Users/shulimen/Desktop/Playlist_bot/");
        File[] listOfFiles = folder.listFiles();
        event.getChannel().sendMessage("All existing playlists include:").queue();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && !listOfFiles[i].getName().equals(".DS_Store")) {
                event.getChannel().sendMessage(listOfFiles[i].getName()).queue();
            }
        }
    }

    /**
     * Creates a playlist.
     *
     * @param event
     */
    private void createPlaylist(GuildMessageReceivedEvent event) {
        String message = "What is the name of the playlist? (Don't add extensions/spaces!)";
        event.getChannel().sendMessage(message).queue();
        waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()) //this
                && e.getChannel().equals(event.getChannel()) && !e.getMessage().getContentRaw().equals(message), e -> {
            String messageReceived = e.getMessage().getContentRaw();
            try {
                File myObj = new File("/Users/shulimen/Desktop/Playlist_bot/" + messageReceived + ".txt");
                if (myObj.createNewFile()) {
                    event.getChannel().sendMessage("File created: " + myObj.getName()).queue();
                } else {
                    event.getChannel().sendMessage("File already exists.").queue();
                }
            } catch (IOException e1) {
                event.getChannel().sendMessage("An error occurred.").queue();
            }
        }, 10, TimeUnit.SECONDS, () -> event.getChannel().sendMessage("You waited too long. Try again.").queue());

    }
}
