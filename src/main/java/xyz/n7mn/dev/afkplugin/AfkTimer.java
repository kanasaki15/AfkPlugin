package xyz.n7mn.dev.afkplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;


class AfkTimer extends BukkitRunnable {
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("AfkPlugin");
    private final AfkFunction AfkAPI;

    private boolean isCancel = false;

    Player player;

    public AfkTimer(Player player, AfkFunction afk){
        this.player = player;
        this.AfkAPI = afk;
    }

    @Override
    public void run() {

        if (!isCancel){
            plugin.reloadConfig();
            int autoTime = plugin.getConfig().getInt("AfkAutoTime");
            int min = autoTime / 60;
            int sec = autoTime - (60 * min);
            // System.out.println(autoTime);


            AfkResult afk = AfkAPI.GetAfkDataByUser(player.getUniqueId());

            if (afk == null){
                isCancel = true;
            }else if (!afk.isAfkFlag()){
                int nowTime = (int)(new Date().getTime() / 1000L);
                int time = (int)(afk.getDate().getTime() / 1000L);
                if (nowTime - time >= autoTime && time > 0){
                    AfkAPI.SetAfk(afk.getUuid());
                    Bukkit.getServer().getPlayer(afk.getUuid()).sendMessage(ChatColor.GREEN + AfkAPI.GetMessage("afkAutoOn").replaceAll("\\[min\\]",""+min).replaceAll("\\[sec\\]",""+sec));
                }
            }

            new AfkTimer(player,AfkAPI).runTaskLater(plugin, 20L);
        }
    }

    @Override
    public void cancel(){
        isCancel = true;
    }

    public void setCancel(boolean cancel){
        isCancel = cancel;
    }

    public boolean isCancel(){
        return isCancel;
    }
}
