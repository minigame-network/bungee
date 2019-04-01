package me.chasertw123.minigames.bungee.loops;

import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.packets.ParadisePacket;
import net.md_5.bungee.BungeeCord;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Loop_QuestCheck {

    private Date nextWeek, nextMonth, nextDay;
    private Calendar currentTime;

    public Loop_QuestCheck() {
        nextWeek = getNextFriday();
        nextMonth = getNextMonth();
        nextDay = getNextDay();
        currentTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        BungeeCord.getInstance().getScheduler().schedule(Main.getInstance(), () -> {
            Date current = currentTime.getTime();

            if(current.after(nextDay)) {

                BungeeCord.getInstance().getServers().values().stream().filter(serverInfo -> serverInfo.getPlayers().size() > 0)
                        .forEach(serverInfo -> new ParadisePacket("QuestReset").addLine("DAILY").queue(serverInfo.getName()));

                nextDay = getNextDay();
            }

            if(current.after(nextWeek)) {

                BungeeCord.getInstance().getServers().values().stream().filter(serverInfo -> serverInfo.getPlayers().size() > 0)
                        .forEach(serverInfo -> new ParadisePacket("QuestReset").addLine("WEEKLY").queue(serverInfo.getName()));

                nextWeek = getNextFriday();
            }

            if(current.after(nextMonth)) {

                BungeeCord.getInstance().getServers().values().stream().filter(serverInfo -> serverInfo.getPlayers().size() > 0)
                        .forEach(serverInfo -> new ParadisePacket("QuestReset").addLine("MONTHLY").queue(serverInfo.getName()));

                nextMonth = getNextMonth();
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    private Date getNextMonth() {
        Calendar currentCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        currentCal.add(Calendar.MONTH, 1);

        currentCal.set(Calendar.DAY_OF_MONTH, currentCal.getActualMinimum(Calendar.DAY_OF_MONTH));
        currentCal.set(Calendar.HOUR, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);

        return currentCal.getTime();
    }

    private Date getNextDay() {
        Calendar currentCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        currentCal.add(Calendar.DAY_OF_MONTH, 1);
        currentCal.set(Calendar.HOUR, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);

        return currentCal.getTime();
    }


    private Date getNextFriday() {
        Calendar currentCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        if(currentCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
            currentCal.add(Calendar.DATE, 1);

        while(currentCal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
            currentCal.add(Calendar.DATE, 1);

        currentCal.set(Calendar.HOUR, 7);
        currentCal.set(Calendar.MINUTE, 59);
        currentCal.set(Calendar.SECOND, 59);

        return currentCal.getTime();
    }

}
