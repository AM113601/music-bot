package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Help extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();
        if(messageSent.equals("!help")){
            EmbedBuilder info = new EmbedBuilder();
            info.setTitle("Here are a list of commands:");
            info.setDescription(
                    "!calculate - work in progress" +
                    "\n!clear \"number\" - clears the amount of text messages in chat" +
                    "\n!clearQ - clears the queue" +
                    "\n!game \"name\" - says a quote to name" +
                    "\n!help - brings up a list of commands " +
                    "\n!hi (or !hello) \"name\" - says hi to the name" +
                    "\n!info - Gives statistics on Tarot" +
                    "\n!join - joins a voice channel" +
                    "\n!leave - leaves the channel" +
                    "\n!play \"link\" - plays the music inside of link" +
                            "\n!play \"number\" \"link\" - plays the music inside of link the specified number of times" +
                    "\n!playlist - stores a playlist" +
                    "\n!print \"script\" - prints out a script" +
                    "\n!removeQ - removes music from queue" +
                    "\n!showQ - shows the current queue" +
                    "\n!showC - shows the current song title" +
                    "\n!skip - skips the track" +
                    "\n!volume - sets the volume"

                    //USE ARRAY LIST AND COLLECTIONS.SORT?
            );
            info.addField("Made By:", "Turtle2739", false);
            info.setColor(0xff3923);

            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage(info.build()).queue();
        }
    }
}
