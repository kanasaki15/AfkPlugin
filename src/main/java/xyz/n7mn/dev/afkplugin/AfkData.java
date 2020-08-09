package xyz.n7mn.dev.afkplugin;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class AfkData {
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("AfkPlugin");
    private final String pass = "./" + plugin.getDataFolder().getPath() + "/AfkData.json";

    private List<AfkResult> list = new ArrayList<>();
    private final boolean isMySQL = plugin.getConfig().getBoolean("UseMySQL");

    public AfkData(){
        if (isMySQL){

        } else {
            list = new Gson().fromJson(fileRead(pass), new TypeToken<Collection<AfkResult>>(){}.getType());
        }
    }

    public List<AfkResult> getAllList(){
        return list;
    }

    public boolean updateList(List<AfkResult> list){
        if (isMySQL){
            return true;
        }else{
            return fileWrite(pass, new Gson().toJson(list));
        }
    }


    private String fileRead(String pass){

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

    private boolean fileWrite(String pass, String content){
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
