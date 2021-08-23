package music;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class RemoveMusic extends ListenerAdapter{
    private EventWaiter waiter;

    public RemoveMusic(EventWaiter waiter){this.waiter = waiter;}

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String messageSent = event.getMessage().getContentRaw();
        if(messageSent.equalsIgnoreCase("!remove")) {
            //showQueue(event);
            event.getChannel().sendMessage("Which music would you like to delete? (Enter a number)").queue();

            //GuildMessageReceived event, condition, and action as the parameters
            waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()) //this
                    && e.getChannel().equals(event.getChannel()) && !e.getMessage().getContentRaw().equals(messageSent), e -> {
                String messageReceived = e.getMessage().getContentRaw();
                event.getChannel().sendMessage(messageReceived + " what?").queue(); //Gets first response

            }, 10, TimeUnit.SECONDS, () -> event.getChannel().sendMessage("You waited too long. Try again.").queue());
        }
    }

}
