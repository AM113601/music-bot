package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Info extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();
        if(messageSent.equals("!info")){
            EmbedBuilder info = new EmbedBuilder();
            info.setTitle("Information");
            info.setDescription("️\uD83C\uDCCFDog 3 players:\uD83C\uDCCF" +
                    "\n5.1% chance of getting 4+ Trump" +
                    "\n16.6% chance of getting 3 Trump" +
                    "\n33% chance of getting 2 Trump" +
                    "\n32.7% chance of getting 1 Trump" +
                    "\n12.6% chance of getting 0 Trump" +
                    "\nSo on average, you get 1.689 Trump");
            info.addField("Calculated By:", "Turtle2739", false);
            info.setColor(0xf45642);

            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage(info.build()).queue();
        }
    }
}
