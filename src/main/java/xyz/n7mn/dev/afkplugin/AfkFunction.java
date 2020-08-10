package xyz.n7mn.dev.afkplugin;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;

public class AfkFunction {

    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("AfkPlugin");
    private AfkDataAPI AfkAPI = new AfkDataAPI();

    public List<AfkResult> GetAfkDataList() {
        List<AfkResult> list = AfkAPI.getAllList();
        return list;
    }

    public AfkResult GetAfkDataByUser(UUID uuid){
        return new AfkDataAPI().getUserResult(uuid);
    }

    @Deprecated
    public boolean isAfk(Player player){

        return isAfk(player.getUniqueId());
    }

    public boolean isAfk(UUID uuid){

        AfkResult result = AfkAPI.getUserResult(uuid);
        if (result == null){
            return false;
        }

        return result.isAfkFlag();
    }

    @Deprecated
    public void SetAfk(Player player){

        List<AfkResult> list = GetAfkDataList();
        boolean writeFlag = false;
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getUuid().equals(player.getUniqueId())){
                if (AfkAPI.updateList(player.getUniqueId(), !list.get(i).isAfkFlag())){
                    writeFlag = true;
                    break;
                }
            }
        }

        if (!writeFlag){
            new AfkDataAPI().addList(player.getUniqueId(), true);
        }
    }

    public boolean SetAfk(UUID uuid){
        return AfkAPI.updateList(uuid, !isAfk(uuid));
    }

    public boolean SetAfk(UUID uuid, boolean AfkFlag){
        return AfkAPI.updateList(uuid, AfkFlag);
    }

    public void SetInitAfkByUser(UUID uuid){
        AfkAPI.addList(uuid, false);
    }

    public boolean DeleteUser(UUID uuid){

        return AfkAPI.deleteListByUser(uuid);
    }

    public boolean DeleteAllUser(){

        return AfkAPI.deleteListByAll();
    }

    public void Close(){
        AfkAPI.SQLConnectClose();
    }

    public String GetMessage(String msg){
        String pass = "./" + plugin.getDataFolder().getPath() + "/lang_" + plugin.getConfig().getString("Lang") + ".json";

        String s = fileRead(pass);
        if (s.equals("[]")){
            new File(pass).delete();
            pass = "./" + plugin.getDataFolder().getPath() + "/lang_ja.json";
        }

        MessageList list = new Gson().fromJson(fileRead(pass), MessageList.class);

        if (msg.equals("afkOn")){ return list.getAfkOn(); }
        if (msg.equals("afkOff")){ return list.getAfkOff(); }
        if (msg.equals("afkMove")){ return list.getAfkMove(); }
        if (msg.equals("afkPermError")){ return list.getAfkPermError(); }
        if (msg.equals("UserOffline")){ return list.getUserOffline(); }
        if (msg.equals("UserAfkOn")){ return list.getUserAfkOn(); }
        if (msg.equals("UserAfkOnToTarget")){ return list.getUserAfkOnToTarget(); }
        if (msg.equals("UserAfkOff")){ return list.getUserAfkOff(); }
        if (msg.equals("UserAfkOffToTarget")){ return list.getUserAfkOffToTarget(); }
        if (msg.equals("ConsoleUserAfkOnToTarget")){ return list.getConsoleUserAfkOnToTarget(); }
        if (msg.equals("ConsoleUserAfkOffToTarget")){ return list.getConsoleUserAfkOffToTarget(); }
        if (msg.equals("afkAutoOn")){ return list.getAfkAutoOn(); }
        if (msg.equals("tpTarget")){ return list.getTpTarget(); }


        return "";
    }

    String fileRead(String pass){

        if (System.getProperty("os.name").toLowerCase().startsWith("windows")){
            pass = pass.replaceAll("/", "\\\\");
        }

        File file = new File(pass);
        BufferedReader buffer = null;
        try {
            FileInputStream input = new FileInputStream(file);
            InputStreamReader stream = new InputStreamReader(input, "UTF-8");
            buffer = new BufferedReader(stream);
            StringBuffer sb = new StringBuffer();

            int ch = buffer.read();
            while (ch != -1){
                sb.append((char) ch);
                ch = buffer.read();
            }

            return sb.toString();
        } catch (FileNotFoundException e) {
            try{
                file.createNewFile();
                fileWrite(pass, "[]");
            } catch (IOException ioException) {

            }
            return "[]";
        } catch (IOException e) {
            e.printStackTrace();
            return "[]";
        } finally {
            try {
                if (buffer != null){
                    buffer.close();
                }
            } catch (IOException e) {
                return "[]";
            }
        }
    }

    boolean fileWrite(String pass, String content){
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")){
            pass = pass.replaceAll("/", "\\\\");
        }

        File file = new File(pass);
        PrintWriter p_writer = null;

        try{
            p_writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8")));
            p_writer.print(content);
            p_writer.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (UnsupportedEncodingException e) {
            return false;
        } finally {
            if (p_writer != null){
                p_writer.close();
            }
        }
    }
}

