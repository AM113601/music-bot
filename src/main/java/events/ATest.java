package events;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ATest extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event){
        System.out.println("This is working");
    }
}
