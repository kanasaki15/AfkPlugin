package xyz.n7mn.dev.afkplugin;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

class AfkDataAPI {
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("AfkPlugin");
    private final String pass = "./" + plugin.getDataFolder().getPath() + "/AfkData.json";

    private boolean isMySQL = plugin.getConfig().getBoolean("UseMySQL");

    private final String MySQLServer   = plugin.getConfig().getString("MySQLServer");
    private final String MySQLUsername = plugin.getConfig().getString("MySQLUsername");
    private final String MySQLPassword = plugin.getConfig().getString("MySQLPassword");
    private final String MySQLDatabase = plugin.getConfig().getString("MySQLDatabase");
    private final String MySQLOption   = plugin.getConfig().getString("MySQLOption");

    private Connection con = null;

    public AfkDataAPI() {
        if (isMySQL) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + MySQLServer + "/" + MySQLDatabase + MySQLOption, MySQLUsername, MySQLPassword);
                if (!con.prepareStatement("SHOW TABLES LIKE 'AfkUserTable';").executeQuery().next()) {
                    con.prepareStatement("CREATE TABLE `AfkUserTable` (\n" +
                            "  `UUID` varchar(36) COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL,\n" +
                            "  `AfkFlag` tinyint(1) NOT NULL,\n" +
                            "  `LastMoveDate` datetime NOT NULL\n" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_ja_0900_as_cs_ks;").execute();
                    con.prepareStatement("ALTER TABLE `AfkUserTable` ADD PRIMARY KEY (`UUID`);").execute();
                }
            } catch (SQLException e) {
                isMySQL = false;
                try {
                    con.close();
                    con = null;
                } catch (Exception e2){
                    con = null;
                }
            }
        }
    }

    public List<AfkResult> getAllList(){
        List<AfkResult> list = new ArrayList<>();
        if (isMySQL && con != null){
            try {
                ResultSet resultSet = con.prepareStatement("SELECT * FROM `AfkUserTable`;").executeQuery();
                boolean isNext = resultSet.next();
                while (isNext) {
                    AfkResult afk = new AfkResult();
                    afk.setUuid(UUID.fromString(resultSet.getString("UUID")));
                    afk.setAfkFlag(resultSet.getBoolean("AfkFlag"));
                    afk.setDate(new Date(resultSet.getTimestamp("LastMoveDate").getTime()));
                    list.add(afk);
                    isNext = resultSet.next();
                }
            } catch (SQLException e) {
                isMySQL = false;
                try {
                    con.close();
                    con = null;
                } catch (Exception e1){
                    con = null;
                }
            }
        } else {
            list = new GsonBuilder().setPrettyPrinting().create().fromJson(fileRead(pass), new TypeToken<Collection<AfkResult>>(){}.getType());
        }

        return list;
    }

    public AfkResult getUserResult(UUID uuid){
        if (isMySQL && con != null){
            try {
                PreparedStatement statement = con.prepareStatement("SELECT * FROM `AfkUserTable` WHERE UUID = ?");
                statement.setString(1, uuid.toString());
                ResultSet set = statement.executeQuery();
                if (set.next()){
                    AfkResult afk = new AfkResult();
                    afk.setUuid(UUID.fromString(set.getString("UUID")));
                    afk.setAfkFlag(set.getBoolean("AfkFlag"));
                    afk.setDate(new Date(set.getTimestamp("LastMoveDate").getTime()));
                    return afk;
                }
                return null;
            } catch (SQLException e) {
                // e.printStackTrace();
                isMySQL = false;
                try {
                    con.close();
                    con = null;
                } catch (Exception e1){
                    con = null;
                }
                return null;
            }
        } else {
            List<AfkResult> list = getAllList();
            for (int i = 0; i < list.size(); i++){
                if (list.get(i).getUuid().equals(uuid)){
                    return list.get(i);
                }
            }
            return null;
        }
    }

    public boolean addList(UUID uuid, boolean AfkFlag){
        if (isMySQL && con != null){
            try {
                PreparedStatement statement = con.prepareStatement("INSERT INTO `AfkUserTable`(`UUID`, `AfkFlag`, `LastMoveDate`) VALUES (?,?,NOW())");
                statement.setString(1, uuid.toString());
                statement.setBoolean(2, AfkFlag);
                return statement.execute();
            } catch (SQLException e) {
                // e.printStackTrace();
                try {
                    con.close();
                    con = null;
                } catch (Exception e1){
                    con = null;
                }
                return false;
            }
        } else {
            List<AfkResult> list = getAllList();

            AfkResult afk = new AfkResult();
            afk.setUuid(uuid);
            afk.setAfkFlag(AfkFlag);
            afk.setDate(new Date());
            list.add(afk);

            return fileWrite(pass, new GsonBuilder().setPrettyPrinting().create().toJson(list));
        }
    }

    public boolean updateList(UUID uuid, boolean AfkFlag){
        if (isMySQL && con != null){
            try {
                PreparedStatement statement = con.prepareStatement("UPDATE `AfkUserTable` SET `UUID` = ?,`AfkFlag` = ?,`LastMoveDate` = NOW() WHERE UUID = ?");
                statement.setString(1, uuid.toString());
                statement.setBoolean(2, AfkFlag);
                statement.setString(3, uuid.toString());
                return statement.execute();
            } catch (SQLException e) {
                // e.printStackTrace();
                isMySQL = false;
                try {
                    con.close();
                    con = null;
                } catch (Exception e1){
                    con = null;
                }
                return false;
            }
        } else {

            List<AfkResult> list = getAllList();
            for (int i = 0; i < list.size(); i++){
                if (list.get(i).getUuid().equals(uuid)){
                    list.get(i).setAfkFlag(AfkFlag);
                    list.get(i).setDate(new Date());
                }
            }

            return fileWrite(pass, new GsonBuilder().setPrettyPrinting().create().toJson(list));
        }
    }

    public boolean deleteListByUser(UUID uuid){
        if (isMySQL && con != null){
            try {
                PreparedStatement statement = con.prepareStatement("DELETE FROM `AfkUserTable` WHERE UUID = ?");
                statement.setString(1, uuid.toString());
                return statement.execute();
            } catch (SQLException e) {
                // e.printStackTrace();
                isMySQL = false;
                try {
                    con.close();
                    con = null;
                } catch (Exception e1) {
                    con = null;
                }
                return false;
            }
        } else {
            List<AfkResult> list = getAllList();

            for (int i = 0; i < list.size(); i++){
                if (list.get(i).getUuid().equals(uuid)){
                    list.remove(i);
                    break;
                }
            }
            return fileWrite(pass, new Gson().toJson(list));
        }
    }

    public boolean deleteListByAll(){
        if (isMySQL && con != null){
            try {
                return con.prepareStatement("DELETE FROM `AfkUserTable` WHERE 1 = 1").execute();
            } catch (SQLException e) {
                // e.printStackTrace();
                isMySQL = false;
                try {
                    con.close();
                    con = null;
                } catch (Exception e1){
                    con = null;
                }
                return false;
            }
        } else {
            List<AfkResult> list = new ArrayList<>();
            return fileWrite(pass, new GsonBuilder().setPrettyPrinting().create().toJson(list));
        }
    }

    public void SQLConnectClose(){
        if (con != null){
            new Thread(
                    ()->{
                        try {
                            con.close();
                            con = null;
                        } catch (Exception e){
                            con = null;
                        }
                    }).start();
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
