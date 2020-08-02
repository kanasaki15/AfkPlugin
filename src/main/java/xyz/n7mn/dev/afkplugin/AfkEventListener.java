package xyz.n7mn.dev.afkplugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.util.Date;
import java.util.List;

class AfkEventListener implements Listener {

    private final AfkFunction afk = new AfkFunction();
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("AfkPlugin");

    @EventHandler
    public void PlayerTeleportEvent (PlayerTeleportEvent e){
        System.out.println("Player : " + e.getPlayer().getName());
        System.out.println("Cause : " + e.getCause());

        if (afk.isAfk(e.getPlayer())){
            if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()){
                e.getPlayer().sendMessage(ChatColor.YELLOW + afk.GetMessage("afkMove"));
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
