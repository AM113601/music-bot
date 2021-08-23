import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import events.*;
import music.Channel;
import music.MusicManager;
import music.RemoveMusic;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import playlist.CreatePlaylist;

import javax.security.auth.login.LoginException;

public class Main {
    public static JDA jda;
    //public static String prefix = "!";

    public static void main(String args[]) throws LoginException {
        //jda = new JDABuilder(AccountType.BOT).setToken("NzA4NDM5MDQ0ODI4MTY4MjMz.XrXXbQ.sK-buHyphRizlrfMI_IXkWGGfqQ").build();
        System.out.println("Finished creating everything");
        //jda = JDABuilder.createDefault("NzA4NDM5MDQ0ODI4MTY4MjMz.XrXXbQ.sK-buHyphRizlrfMI_IXkWGGfqQ").enableIntents(GatewayIntent.GUILD_MEMBERS).build();
        jda = JDABuilder.createDefault("NzA4NDM5MDQ0ODI4MTY4MjMz.XrXXbQ.sK-buHyphRizlrfMI_IXkWGGfqQ").build();
        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        //jda.getPresence().setActivity(Activity.playing("Genshin Impact"));
       // jda.getPresence().setActivity(Activity.playing("\uD83C\uDF6ACookie Clicker\uD83C\uDF6A"));
        //jda.getPresence().setActivity(Activity.watching(""));
       // jda.getPresence().setActivity(Activity.listening("(∪｡∪)｡｡｡zzz"));
        jda.getPresence().setActivity(Activity.watching("マジカルミライ 2020 ^_^"));
        //jda.getPresence().setActivity(Activity.playing("with NullPointerExceptions :')"));
        //jda.addEventListener(new Hello());
        EventWaiter waiter = new EventWaiter();
        jda.addEventListener(new HelloEvent()); //hello, !hi

        jda.addEventListener(new Calculate()); //!calculate
        jda.addEventListener(new Info()); //!info
        jda.addEventListener(new Game()); //game or !game
        jda.addEventListener(new Clear()); //!clear
        jda.addEventListener(new Troll()); //work in progress
        jda.addEventListener(new Print()); //!print
        jda.addEventListener(new Help()); //!help
        jda.addEventListener(new Join());
        jda.addEventListener(new Leave());
        jda.addEventListener(new Channel()); //!join, !leave
        jda.addEventListener(new MusicManager(waiter));
        jda.addEventListener(new Knock(waiter)); //knock knock
        jda.addEventListener(new RemoveMusic(waiter));
        jda.addEventListener(waiter);
        jda.addEventListener(new CreatePlaylist(waiter));
        jda.addEventListener(new Notify());



    }
}
