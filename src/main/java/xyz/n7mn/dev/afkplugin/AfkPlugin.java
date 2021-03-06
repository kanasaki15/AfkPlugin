package xyz.n7mn.dev.afkplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.n7mn.dev.bstats.bukkit.afk.Metrics;

import java.io.File;

public final class AfkPlugin extends JavaPlugin {

    private AfkFunction afk;

    @Override
    public void onEnable() {
        // Plugin startup logic
        afk = new AfkFunction();

        saveDefaultConfig();
        getCommand("afk").setExecutor(new AfkCommand(afk));
        getServer().getPluginManager().registerEvents(new AfkEventListener(afk),this);

        String pass = "./" + getDataFolder().getPath() + "/lang_"+getConfig().getString("Lang")+".json";
        String defaultPass = "./" + getDataFolder().getPath() + "/lang_ja.json";
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")){
            pass = pass.replaceAll("/", "\\\\");
            defaultPass = defaultPass.replaceAll("/", "\\\\");
        }

        if (!new File(defaultPass).exists()){
            String json = "{\n" +
                    "  \"afkOn\": \"退席状態になりました。 解除するにはもう一度/afkを実行してください。\",\n" +
                    "  \"afkOff\": \"退席状態を解除しました。\",\n" +
                    "  \"afkMove\": \"/afkをもう一度実行して退席状態を解除してください。\",\n" +
                    "  \"afkPermError\": \"他の人の放置状態を変更するには権限が必要です。\",\n" +
                    "  \"UserOffline\": \"オンラインのプレーヤー名を指定してください！\",\n" +
                    "  \"UserAfkOn\": \"[user]さんを退席状態にしました。\",\n" +
                    "  \"UserAfkOnToTarget\": \"[user]さんから退席状態に指定されました。 解除するには/afkを実行してください。\",\n" +
                    "  \"UserAfkOff\": \"[user]さんの退席状態を解除しました。\",\n" +
                    "  \"UserAfkOffToTarget\": \"[user]さんから退席状態を解除されました。\",\n" +
                    "  \"ConsoleUserAfkOnToTarget\": \"管理者から退席状態に指定されました。 解除するには/afkを実行してください。\",\n" +
                    "  \"ConsoleUserAfkOffToTarget\": \"管理者から退席状態を解除されました。\",\n" +
                    "  \"afkAutoOn\": \"[min]分[sec]秒放置されているのを検知したため退席状態になりました。 \",\n" +
                    "  \"tpTarget\": \"[user]さんは現在退席中のため、テレポートをキャンセルしました。\"\n" +
                    "}";
            afk.fileWrite(defaultPass, json);
        }

        if (!new File(pass).exists()){
            getLogger().info("LangFile NotFound!!");
        }

        Metrics metrics = new Metrics(this, 8475);
        getLogger().info("AfkPlugin Ver " + Bukkit.getPluginManager().getPlugin("AfkPlugin").getDescription().getVersion() + " Started!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // new AfkFunction().fileWrite("./" + getDataFolder().getPath() + "/AfkData.json","[]");

        afk.DeleteAllUser();
        afk.Close();

        getLogger().info("AfkPlugin Ver " + Bukkit.getPluginManager().getPlugin("AfkPlugin").getDescription().getVersion() + " Stoped!!");
    }
}
