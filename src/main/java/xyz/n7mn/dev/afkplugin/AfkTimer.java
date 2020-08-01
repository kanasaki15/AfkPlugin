package xyz.n7mn.dev.afkplugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.List;

class AfkTimer extends BukkitRunnable {
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("AfkPlugin");
    private final AfkFunction afk = new AfkFunction();

    @Override
    public void run() {
        plugin.reloadConfig();
        List<AfkResult> list = afk.GetAfkDataList();

        boolean updateFlag = false;
        int autoTime = plugin.getConfig().getInt("AfkAutoTime");
        // System.out.println(autoTime);

        for (int i = 0; i < list.size(); i++){

            int nowTime = (int)(new Date().getTime() / 1000L);
            int time = (int)(list.get(i).getDate().getTime() / 1000L);

            if (nowTime - time >= autoTime && time > 0){
                if (!list.get(i).isAfkFlag()){
                    list.get(i).setAfkFlag(true);

                    Player player = Bukkit.getPlayer(list.get(i).getUuid());
                    if (player == null){
                        continue;
                    }

                    int min = autoTime / 60;
                    int sec = autoTime - (60 * min);

                    player.sendMessage(ChatColor.GREEN + afk.GetMessage("afkAutoOn").replaceAll("\\[min\\]",""+min).replaceAll("\\[sec\\]",""+sec));
                    updateFlag = true;
                }
            }
        }

        if (updateFlag){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            afk.fileWrite(plugin.getDataFolder().getPath() + "/AfkData.json",gson.toJson(list));
        }

        new AfkTimer().runTaskLater(plugin, 20);
    }
}
