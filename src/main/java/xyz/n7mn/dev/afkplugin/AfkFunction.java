package xyz.n7mn.dev.afkplugin;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;

public class AfkFunction {

    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("AfkPlugin");
    private final String pass = "./" + plugin.getDataFolder().getPath() + "/AfkData.json";

    public void SetAfk(Player player){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<AfkResult> list = GetAfkDataList();
        boolean writeFlag = false;
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getUuid().equals(player.getUniqueId())){
                list.get(i).setDate(new Date());
                list.get(i).setAfkFlag(!list.get(i).isAfkFlag());
                if (new AfkData().updateList(list)){
                    writeFlag = true;
                    break;
                }
            }
        }

        if (!writeFlag){
            AfkResult a = new AfkResult();
            a.setAfkFlag(true);
            a.setUuid(player.getUniqueId());
            a.setDate(new Date());
            list.add(a);
            new AfkData().updateList(list);
        }
    }

    public boolean isAfk(Player player){

        List<AfkResult> list = GetAfkDataList();

        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getUuid().equals(player.getUniqueId())){
                return list.get(i).isAfkFlag();
            }
        }

        return false;
    }


    public List<AfkResult> GetAfkDataList() {
        List<AfkResult> list = new AfkData().getAllList();

        if (list == null){
            return new ArrayList<>();
        }

        if (list != null && list.size() == 0){
            return new ArrayList<>();
        }

        return list;
    }

    public boolean UpdateDataList(UUID uuid,AfkResult data){
        List<AfkResult> list = GetAfkDataList();
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getUuid().equals(uuid)){
                list.get(i).setUuid(data.getUuid());
                list.get(i).setDate(new Date());
                list.get(i).setAfkFlag(data.isAfkFlag());

                return true;
            }
        }
        return false;
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

