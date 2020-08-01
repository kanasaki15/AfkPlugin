package xyz.n7mn.dev.afkplugin;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AfkCommandEvent  extends Event implements Cancellable {

    private boolean isCancel = false;
    private static final HandlerList handlerList = new HandlerList();
    private boolean isSetPlayer;
    private Player player;

    public AfkCommandEvent(boolean isSetPlayer, Player p){
        this.isSetPlayer = isSetPlayer;
        this.player = p;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return isCancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancel = cancel;
    }


    public boolean isSetPlayer() {
        return isSetPlayer;
    }

    public Player getExePlayer(){
        return player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
