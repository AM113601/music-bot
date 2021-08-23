package events;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class Knock extends ListenerAdapter {

    private EventWaiter waiter;

    public Knock(EventWaiter waiter){this.waiter = waiter;}

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String messageSent = event.getMessage().getContentRaw();
        if(messageSent.equalsIgnoreCase("Knock knock")) {
            event.getChannel().sendMessage("Who's there?").queue();
            //EventWaiter waiter = new EventWaiter();
            //GuildMessageReceived event, condition, and action as the parameters
            waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()) //this
                    && e.getChannel().equals(event.getChannel()) && !e.getMessage().getContentRaw().equals(messageSent), e -> {
                String messageReceived = e.getMessage().getContentRaw();
                event.getChannel().sendMessage(messageReceived + " what?").queue(); //Gets first response

                waiter.waitForEvent(GuildMessageReceivedEvent.class, e1 -> e1.getAuthor().equals(event.getAuthor()) //this
                        && e1.getChannel().equals(event.getChannel()) && !e1.getMessage().getContentRaw().equals(messageReceived), e1 -> {
                    String nextMessageReceived = e1.getMessage().getContentRaw();
                    event.getChannel().sendMessage(nextMessageReceived + " haha very funny").queue(); //Gets second response

                }, 10, TimeUnit.SECONDS, () -> event.getChannel().sendMessage("You waited too long. Try again.").queue());
            }, 10, TimeUnit.SECONDS, () -> event.getChannel().sendMessage("You waited too long. Try again.").queue());
        }
    }

}
