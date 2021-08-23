package events;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Notify extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String name = event.getMember().getUser().getName();
        TextChannel channel = event.getChannel();
        if(message.toLowerCase().contains("albert")){
            channel.sendMessage("Hey " + name + ", I'm currently busy with some work" +
                    " (cuz I procrastinate too much \uD83D\uDE43), I'll " +
                    "get back to you as soon as possible.").queue();
        }
    }
}
