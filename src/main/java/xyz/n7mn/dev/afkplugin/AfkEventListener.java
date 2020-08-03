package xyz.n7mn.dev.afkplugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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
        if (afk.isAfk(e.getPlayer())){
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
        if (afk.isAfk(e.getPlayer())){
            if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()){
                e.getPlayer().sendMessage(ChatColor.YELLOW + afk.GetMessage("afkMove"));
                e.setCancelled(true);
            }
        }

        List<AfkResult> list = afk.GetAfkDataList();
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getUuid().equals(e.getPlayer().getUniqueId())){
                list.get(i).setDate(new Date());
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        afk.fileWrite(plugin.getDataFolder().getPath() + "/AfkData.json", gson.toJson(list));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerJoinEvent (PlayerJoinEvent e){
        List<AfkResult> list = afk.GetAfkDataList();

        boolean newFlag = true;
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getUuid().equals(e.getPlayer().getUniqueId())){
                newFlag = false;
                break;
            }
        }

        if (newFlag){
            AfkResult result = new AfkResult();
            result.setDate(new Date());
            result.setAfkFlag(false);
            result.setUuid(e.getPlayer().getUniqueId());
            list.add(result);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            afk.fileWrite(plugin.getDataFolder().getPath() + "/AfkData.json", gson.toJson(list));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void Player (PlayerQuitEvent e){
        List<AfkResult> list = afk.GetAfkDataList();

        boolean deleteFlag = false;
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getUuid().equals(e.getPlayer().getUniqueId())){
                list.remove(i);
                deleteFlag = true;
            }
        }

        if (deleteFlag){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            afk.fileWrite(plugin.getDataFolder().getPath() + "/AfkData.json", gson.toJson(list));
        }
    }
}
