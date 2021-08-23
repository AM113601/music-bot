package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Game extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String messageSent = event.getMessage().getContentRaw();
        try {
            String name = event.getMember().getUser().getName();
            String quote = ", the phrase “it’s just a Game” is such a weak mindset. " +
                    "You are ok with what happened, losing, imperfection of a craft." +
                    " When you stop getting angry after losing, you’ve lost twice.\uD83D\uDE38";

            if(messageSent.contains("!game") ){
                String[] split = messageSent.split(" ");
                if(split.length == 2){
                    event.getChannel().sendTyping().queue();
                    event.getChannel().sendMessage(split[1] + quote).queue();
                }
            }
            else if(messageSent.contains(" game ") ){
                event.getChannel().sendTyping().queue();
                event.getChannel().sendMessage(name + quote).queue();
            }
        }
        catch(NullPointerException e){
            return;
            }

    }

}