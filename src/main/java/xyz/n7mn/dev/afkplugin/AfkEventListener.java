package xyz.n7mn.dev.afkplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import java.util.Date;


class AfkEventListener implements Listener {

    private AfkFunction afk;
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("AfkPlugin");
    private Player tpCommandExePlayer = null;

    public AfkEventListener(AfkFunction afk) {
        this.afk = afk;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommandPreprocessEvent (PlayerCommandPreprocessEvent e){
        if (e.getMessage().startsWith("/tp @a") || e.getMessage().startsWith("/tp @e") || e.getMessage().startsWith("/teleport @a") || e.getMessage().startsWith("/teleport @e")){
            tpCommandExePlayer = e.getPlayer();
        }

        Object[] player = Bukkit.getServer().getOnlinePlayers().toArray();
        for (int i = 0; i < player.length; i++){
            Player pp = (Player) player[i];

            if (e.getMessage().startsWith("/tp "+pp.getName()+" ") || e.getMessage().startsWith("/teleport "+pp.getName()+" ")){
                tpCommandExePlayer = e.getPlayer();
            }
        }
    }

    @EventHandler
    public void PlayerTeleportEvent (PlayerTeleportEvent e){

        long start = System.currentTimeMillis();

        long lastMoveTime = afk.GetAfkDataByUser(e.getPlayer().getUniqueId()).getDate().getTime();
        long nowTime = new Date().getTime();

        int time = ((int)(nowTime / 1000L)) - ((int)(lastMoveTime / 1000L));
        int autoTime = plugin.getConfig().getInt("AfkAutoTime");

        int min = autoTime / 60;
        int sec = autoTime - (60 * min);

        if (time > 0 && time >= autoTime && !afk.isAfk(e.getPlayer().getUniqueId())){
            afk.SetAfk(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage(ChatColor.GREEN + afk.GetMessage("afkAutoOn").replaceAll("\\[min\\]",""+min).replaceAll("\\[sec\\]",""+sec));
            e.setCancelled(true);
        }

        if (afk.isAfk(e.getPlayer().getUniqueId())){
            if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()){
                if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND) && tpCommandExePlayer != null){
                    tpCommandExePlayer.sendMessage(ChatColor.YELLOW + afk.GetMessage("tpTarget").replaceAll("\\[user\\]",tpCommandExePlayer.getName()));
                }
                // e.getPlayer().sendMessage(ChatColor.YELLOW + afk.GetMessage("afkMove"));
                e.setCancelled(true);
            }
        }


        long end = System.currentTimeMillis();
        long ms = end - start;

        System.out.println("Event ExeTime : " + ((double)(ms / 1000L)) + " ms");
    }

    @EventHandler
    public void PlayerMoveEvent (PlayerMoveEvent e){

        long start = System.currentTimeMillis();

        long lastMoveTime = afk.GetAfkDataByUser(e.getPlayer().getUniqueId()).getDate().getTime();
        long nowTime = new Date().getTime();

        int time = ((int)(nowTime / 1000L)) - ((int)(lastMoveTime / 1000L));
        int autoTime = plugin.getConfig().getInt("AfkAutoTime");

        int min = autoTime / 60;
        int sec = autoTime - (60 * min);

        if (time > 0 && time >= autoTime && !afk.isAfk(e.getPlayer().getUniqueId())){
            afk.SetAfk(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage(ChatColor.GREEN + afk.GetMessage("afkAutoOn").replaceAll("\\[min\\]",""+min).replaceAll("\\[sec\\]",""+sec));
            e.setCancelled(true);
        }

        if (afk.isAfk(e.getPlayer().getUniqueId())){
            if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()){
                e.getPlayer().sendMessage(ChatColor.YELLOW + afk.GetMessage("afkMove"));
                e.setCancelled(true);
            }
        } else {
            afk.SetAfk(e.getPlayer().getUniqueId(), false);
        }

        long end = System.currentTimeMillis();
        long ms = end - start;

        System.out.println("Event ExeTime : " + ((double)(ms / 1000L)) + " ms");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerJoinEvent (PlayerJoinEvent e){
        afk.SetInitAfkByUser(e.getPlayer().getUniqueId());
        // new AfkTimer(e.getPlayer(), afk).runTaskLater(plugin, 20L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerQuitEvent (PlayerQuitEvent e){
        afk.DeleteUser(e.getPlayer().getUniqueId());
    }
}
