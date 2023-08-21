package me.clcondorcet.itemsorter.listeners;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.utils.VersionChecker;
import me.clcondorcet.itemsorter.database.migrations.MigrationsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author clcondorcet
 */
public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if (!MigrationsManager.migrationsDone) {
            e.getPlayer().kickPlayer(ItemSorter.prefix + "§cMigrations are not done yet. Please wait and retry later.");
        }
        if(e.getPlayer().isOp()){
            if(ItemSorter.versionChecker.needUpdate){
                e.getPlayer().sendMessage("§6-------- §eItem§cSorter §6--------"
                        , "§e> §aItemSorter need an update !"
                        , "§eCurrent verion: §c" + VersionChecker.pluginVersion
                        , "§eThe new version is: §a" + ItemSorter.versionChecker.lastVersion
                        , "§6https://www.spigotmc.org/resources/itemsorter.85370/"
                        , "§6----------------------------");

            }
        }
    }

}
