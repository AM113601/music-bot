package events;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class HelloEvent extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String messageSent = event.getMessage().getContentRaw();
        String name = event.getMember().getUser().getName();
        TextChannel channel = event.getChannel();
        if(messageSent.contains("!hi") || messageSent.contains("!hello")){
            String[] names = messageSent.split(" ");
            if((names[0].equals("!hi") || names[0].equals("!hello")) && names.length > 1){
                if(names.length == 2 && names[1].equalsIgnoreCase("all")){
                    for(int i = 0; i < channel.getMembers().size(); i++){
                        //channel.sendTyping().queue();
                        channel.sendMessage("Hey " + channel.getMembers().get(i).getEffectiveName() + "!").queue();
                    }
                }
                else {
                    for (int i = 1; i < names.length; i++) {
                        //event.getChannel().sendTyping().queue();
                        channel.sendMessage("Hey " + names[i] + "!").queue();
                    }
                }
            }
            else{
                event.getChannel().sendTyping().queue();
                event.getChannel().sendMessage("Hey " + name + ", what's new?").queue();
            }
        }
        /*else if(messageSent.equalsIgnoreCase("hello") || messageSent.equalsIgnoreCase("hi")) {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Hey " + name + ", what's new?").queue();
        }*/
    }

}
