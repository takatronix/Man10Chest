package red.man10.chest;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class MChest extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        getCommand("mchest").setExecutor(new MChestCommand(this));
        this.getServer().getPluginManager().registerEvents(this,this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        Block b = e.getBlock();
        org.bukkit.block.Chest c = (org.bukkit.block.Chest) b.getState();
        ItemStack[] contents = c.getInventory().getContents();
        for(int i = 0;i <= contents.length; i++){
            if(contents[i] != null){
                getConfig().set("item." + i, contents[i]);
            }else{
                getConfig().set("item." + i, "null");
            }
            saveConfig();
        }
        /*for(ItemStack is : c.getInventory().getContents()){
            if(is != null){
                p.sendMessage(is.toString());
            }
        }*/
    }

    //ラージチェストかどうかを見る
    public boolean isLargeChest(Location l){
        if(l.getBlock().getType() != Material.CHEST){
            return false;
        }
        Location[] ll = {new Location(l.getWorld(),l.getX() -1,l.getY(),l.getZ()),new Location(l.getWorld(),l.getX() + 1,l.getY(),l.getZ()),new Location(l.getWorld(),l.getX(),l.getY(),l.getZ() - 1),new Location(l.getWorld(),l.getX(),l.getY(),l.getZ() + 1)};
        for(int i = 0;i < ll.length; i++){
            if(ll[i].getBlock().getType() == Material.CHEST){
                return true;
            }
        }
        return false;
    }
}
