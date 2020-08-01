package xyz.n7mn.dev.afkplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class AfkCommand implements CommandExecutor {
    AfkFunction afk = new AfkFunction();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){

            if (args.length == 0){
                //sender.sendMessage(isAfk((Player) sender) + " -> " + (!isAfk((Player) sender)));
                AfkCommandEvent event = new AfkCommandEvent(false, (Player) sender);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()){
                    return true;
                }

                afk.SetAfk((Player) sender);
                if (afk.isAfk((Player) sender)){
                    sender.sendMessage(ChatColor.GREEN + afk.GetMessage("afkOn"));
                } else {
                    sender.sendMessage(ChatColor.GREEN + afk.GetMessage("afkOff"));
                }
            } else {
                if (!((Player) sender).isOp()){
                    sender.sendMessage(ChatColor.RED + afk.GetMessage("afkPermError"));
                    return true;
                }

                AfkCommandEvent event = new AfkCommandEvent(true, ((Player) sender));
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()){
                    return true;
                }

                for (int i = 0; i < args.length; i++){
                    Player player = Bukkit.getServer().getPlayer(args[i]);
                    if (player == null){
                        sender.sendMessage(ChatColor.RED + afk.GetMessage("UserOffline"));
                        return true;
                    } else {
                        afk.SetAfk(player);
                        if (afk.isAfk(player)){
                            sender.sendMessage(ChatColor.GREEN + afk.GetMessage("UserAfkOn").replaceAll("\\[user\\]", player.getName()));
                            player.sendMessage(ChatColor.GOLD + afk.GetMessage("UserAfkOnToTarget").replaceAll("\\[user\\]", ((Player) sender).getName()));
                        } else {
                            sender.sendMessage(ChatColor.GREEN + afk.GetMessage("UserAfkOff").replaceAll("\\[user\\]", player.getName()));
                            player.sendMessage(ChatColor.GOLD + afk.GetMessage("UserAfkOffToTarget").replaceAll("\\[user\\]", ((Player) sender).getName()));
                        }
                    }
                }
            }

        } else {
            if (args.length == 1){

                AfkCommandEvent event = new AfkCommandEvent(true, null);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()){
                    return true;
                }

                Player player = Bukkit.getServer().getPlayer(args[0]);
                if (player == null){
                    sender.sendMessage(ChatColor.RED + afk.GetMessage("UserOffline"));
                    return true;
                }
                afk.SetAfk(player);
                if (afk.isAfk(player)){
                    sender.sendMessage(ChatColor.GREEN + afk.GetMessage("UserAfkOn").replaceAll("\\[user\\]", player.getName()));
                    player.sendMessage(ChatColor.GOLD + afk.GetMessage("ConsoleUserAfkOnToTarget"));
                } else {
                    sender.sendMessage(ChatColor.GREEN + afk.GetMessage("UserAfkOff").replaceAll("\\[user\\]", player.getName()));
                    player.sendMessage(ChatColor.GOLD + afk.GetMessage("ConsoleUserAfkOffToTarget"));
                }
            }
        }

        return true;
    }

}
