import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Hello extends ListenerAdapter {
    public Hello() {
        System.out.println("Reached Here");
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String messageSent = event.getMessage().getContentRaw();
        String name = event.getMember().getUser().getName();

        if(messageSent.contains("!hi") || messageSent.contains("!hello")){
            System.out.print("Reached Inside For Loop");
            String[] names = messageSent.split(" ");
            if((names[0].equals("!hi") || names[0].equals("!hello")) && names.length > 1){
                if(names.length == 2 && names[1].equalsIgnoreCase("all")){
                    for(int i = 0; i < event.getChannel().getMembers().size(); i++){

                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage("Hey " + event.getChannel().getMembers().get(i).getEffectiveName() + "!").queue();
                    }
                }
                else {
                    for (int i = 1; i < names.length; i++) {
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage("Hey " + names[i] + "!").queue();
                    }
                }
            }
        }
        else if(messageSent.equalsIgnoreCase("hello") || messageSent.equalsIgnoreCase("hi")) {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Hey " + name + ", what's new?").queue();
        }
    }

}
