package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Calculate extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String messageSent = event.getMessage().getContentRaw();
        String[] data = messageSent.split(" ");
        if(data.length == 8){
            if(data[0].equals("!calculate")) {
                int sum = 0; //data[1] = num of odlers
                //data[2] = num of trump
                //data[3] = num of major trump
                //data[4] = King, Queen, C
                //data[5] = 5-,6,7+
                //data[6] = suits with 0
                //data[7] = suits with 1
                sum += Integer.parseInt(data[1]) * 9 + Integer.parseInt(data[2]) * 1
                        + Integer.parseInt(data[3]) * 2;
                if (Integer.parseInt(data[4]) == 1) {
                    sum += 6;
                } else if (Integer.parseInt(data[4]) == 2) {
                    sum += 10;
                } else if (Integer.parseInt(data[4]) == 3) {
                    sum += 12;
                }
                if (Integer.parseInt(data[5]) == 5) {
                    sum += 0;
                } else if (Integer.parseInt(data[5]) == 6) {
                    sum += 7;
                } else if (Integer.parseInt(data[5]) == 7) {
                    sum += 9;
                }
                sum += Integer.parseInt(data[6]) * 6 + Integer.parseInt(data[7]) * 3;
                if (sum < 40) {
                    event.getChannel().sendMessage("Pass").queue();
                } else if (sum >= 40 && sum <= 55) {
                    event.getChannel().sendMessage("Take").queue();
                } else if (sum > 55 && sum <= 70) {
                    event.getChannel().sendMessage("Guard").queue();
                } else if (sum > 70 && sum <= 80) {
                        event.getChannel().sendMessage("Guard W/O").queue();
                }
                else{
                    event.getChannel().sendMessage("Guard Against").queue();
                }
            }
        }
    }
}
