package xyz.n7mn.dev.afkplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

class AfkTimer extends BukkitRunnable {
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("AfkPlugin");
    private final AfkFunction afk = new AfkFunction();

    @Override
    public void run() {
        plugin.reloadConfig();
        int autoTime = plugin.getConfig().getInt("AfkAutoTime");
        int min = autoTime / 60;
        int sec = autoTime - (60 * min);
        // System.out.println(autoTime);

        List<AfkResult> list = afk.GetAfkDataList();

        for (int i = 0; i < list.size(); i++){
            int nowTime = (int)(new Date().getTime() / 1000L);
            int time = (int)(list.get(i).getDate().getTime() / 1000L);

            if (list.get(i).isAfkFlag()){
                continue;
            }

            if (nowTime - time >= autoTime && time > 0){
                afk.SetAfk(list.get(i).getUuid());
                Bukkit.getServer().getPlayer(list.get(i).getUuid()).sendMessage(ChatColor.GREEN + afk.GetMessage("afkAutoOn").replaceAll("\\[min\\]",""+min).replaceAll("\\[sec\\]",""+sec));
            }
        }

        new AfkTimer().runTaskLater(plugin, 20);
    }
}
