package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Print extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();
        if (messageSent.contains("!print")) {
            String[] split = messageSent.split(" ");
            String str = "";
            if(split.length > 1){
                for(int i = 1; i < split.length; i++){
                    str += split[i] + " ";
                }
                event.getChannel().sendMessage(str).queue();
            }
        }
    }
}
