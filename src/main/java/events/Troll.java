package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Troll  extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String messageSent = event.getMessage().getContentRaw();
        if(messageSent.equals("Owen")){
            event.getChannel().sendMessage("@Owen" + "\uD83E\uDD70" +  "\uD83D\uDC96" + "\uD83C\uDF75").queue();
        }
        /*String messageSent = event.getMessage().getContentRaw();
        String name = event.getMember().getUser().getName();
        if(name.equals("Iven")){
           // messageSent.contains(".Hello")
            event.getChannel().sendTyping().queue();
            //event.getChannel().sendMessage("Rip Iven is not working \uD83D\uDE3F").queue();
            //event.getChannel().sendMessage("Yay Iven is working \uD83D\uDE38").queue();
            event.getChannel().sendMessage(name).queue();
        }*/
    }
}
