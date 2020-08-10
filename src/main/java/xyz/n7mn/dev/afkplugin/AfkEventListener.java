package xyz.n7mn.dev.afkplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

class AfkEventListener implements Listener {

    private AfkFunction afk;
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("AfkPlugin");
    private Player tpCommandExePlayer = null;
    private List<TimerTask> taskList = new ArrayList<>();

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
        if (afk.isAfk(e.getPlayer().getUniqueId())){
            if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()){
                if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND) && tpCommandExePlayer != null){
                    tpCommandExePlayer.sendMessage(ChatColor.YELLOW + afk.GetMessage("tpTarget").replaceAll("\\[user\\]",tpCommandExePlayer.getName()));
                }
                // e.getPlayer().sendMessage(ChatColor.YELLOW + afk.GetMessage("afkMove"));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerMoveEvent (PlayerMoveEvent e){
        if (afk.isAfk(e.getPlayer().getUniqueId())){
            if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()){
                e.getPlayer().sendMessage(ChatColor.YELLOW + afk.GetMessage("afkMove"));
                e.setCancelled(true);
            }
        } else {
            afk.SetAfk(e.getPlayer().getUniqueId(), false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerJoinEvent (PlayerJoinEvent e){
        new AfkFunction().SetInitAfkByUser(e.getPlayer().getUniqueId());
        taskList.add(new TimerTask(e.getPlayer(), new AfkTimer(e.getPlayer(), afk).runTaskLater(plugin, 20L)));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerQuitEvent (PlayerQuitEvent e){
        int i = 0;
        for (TimerTask task : taskList){
            if (task.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())){
                task.cancel();
                break;
            }
            i++;
        }
        taskList.remove(i);
        new AfkFunction().DeleteUser(e.getPlayer().getUniqueId());
    }
}

class TimerTask{
    private Player player;
    private BukkitTask task;

    TimerTask(Player player, BukkitTask task){
        this.player = player;
        this.task = task;
    }

    public void cancel(){
        this.task.cancel();
    }

    public Player getPlayer() {
        return player;
    }
}