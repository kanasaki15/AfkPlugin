package xyz.n7mn.dev.afkplugin;

import java.util.Date;
import java.util.UUID;

public class AfkResult {
    private UUID uuid;
    private boolean afkFlag;
    private Date date;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {

        this.uuid = uuid;
    }

    public void setAfkFlag(boolean flag){

        this.afkFlag = flag;
    }

    public boolean isAfkFlag() {

        return afkFlag;
    }

    public Date getDate() {

        return date;
    }

    public void setDate(Date date) {

        this.date = date;
    }
}
