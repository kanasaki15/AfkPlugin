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

    private final AfkFunction afk = new AfkFunction();
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("AfkPlugin");
    private Player tpCommandExePlayer = null;

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
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void Player (PlayerQuitEvent e){
        new AfkFunction().DeleteUser(e.getPlayer().getUniqueId());
    }
}
