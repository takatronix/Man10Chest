package red.man10.chest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.bukkit.Material.AIR;

/**
 * Created by sho-pc on 2017/03/22.
 */
public class MChestCommand implements CommandExecutor {

    String prefix = "§7§l[§6§lmChest§7§l] §8§l";

    private final MChest plugin;

    public MChestCommand(MChest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("mchest")) {
            Player p = (Player) sender;
            if(!p.hasPermission("mchest.command")){
                p.sendMessage(noPermissionMessage("mchest.command"));
                return true;
            }
            if(args.length == 0 ){
                p.sendMessage(prefix + "コマンドの使い方が間違ってます /mchest help");
            }
            if(args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {//ファイルリスト
                    if(!p.hasPermission("mchest.list")){
                        p.sendMessage(noPermissionMessage("mchest.list"));
                        return true;
                    }
                    File folder = new File(Bukkit.getServer().getPluginManager().getPlugin("MChest").getDataFolder(), File.separator + "Chests");
                    File[] listOfFiles = folder.listFiles();
                    p.sendMessage("§8§l===========§7§l[§6§lmChest Files§7§l]§8§l===========");
                    for (int i = 0; i < listOfFiles.length; i++) {
                        if (listOfFiles[i].isFile()) {
                            p.sendMessage("§7§l" + listOfFiles[i].getName());
                        }
                    }
                    return true;
                }
                if(args[0].equalsIgnoreCase("help")){
                    if(!p.hasPermission("mchest.help")){
                        p.sendMessage(noPermissionMessage("mchest.help"));
                        return true;
                    }
                    help(p);
                    return true;
                }
                p.sendMessage(prefix + "コマンドの使い方が間違ってます /mchest help");
                return true;
            }



            if (args.length == 2) {
                if(args[0].equalsIgnoreCase("preview")){
                    if(!p.hasPermission("mchest.preview")){
                        p.sendMessage(noPermissionMessage("mchest.preview"));
                        return true;
                    }
                    String fileName = args[1];
                    File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("MChest").getDataFolder(), File.separator + "Chests");
                    File f = new File(dataa, File.separator + fileName + ".yml");
                    FileConfiguration data = YamlConfiguration.loadConfiguration(f);
                    if(!f.exists()){
                        p.sendMessage(prefix + "チェストが存在しません");
                        return true;
                    }
                    if(data.getBoolean("isLargeChest") == true){
                        Inventory inv = Bukkit.createInventory(null,54,"§d§l§n" + args[1]);
                        for(int i = 0; i < 54; i++){
                            if(!data.get("item." + i).equals("null")){
                                inv.setItem(i, (ItemStack) data.get("item." + i));
                            }
                        }
                        plugin.playerInMenu = true;
                        plugin.playerInMenuList.add(p.getUniqueId());
                        p.openInventory(inv);
                        return true;
                    }
                    Inventory inv = Bukkit.createInventory(null,27,"§d§l§n" + args[1]);
                    for(int i = 0; i < 27; i++){
                        if(!data.get("item." + i).equals("null")){
                            inv.setItem(i, (ItemStack) data.get("item." + i));
                        }
                    }
                    plugin.playerInMenu = true;
                    plugin.playerInMenuList.add(p.getUniqueId());
                    p.openInventory(inv);
                    return true;
                }
                if (args[0].equalsIgnoreCase("create")) {
                    if(!p.hasPermission("mchest.create")){
                        p.sendMessage(noPermissionMessage("mchest.create"));
                        return true;
                    }
                    Block b = p.getTargetBlock((Set<Material>) null, 100);
                    if(b.getType() != Material.CHEST){
                        p.sendMessage(prefix + "ブロックはチェストのみです");
                        return true;
                    }
                    Chest c = (Chest) b.getState();
                    ItemStack[] contents = c.getInventory().getContents();
                    String fileName = args[1];
                    File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("MChest").getDataFolder(), File.separator + "Chests");
                    File f = new File(dataa, File.separator + fileName + ".yml");
                    FileConfiguration data = YamlConfiguration.loadConfiguration(f);
                    if (f.exists()) {
                        f.delete();
                    }
                        int count = 0;
                        for (int i = 0; i < contents.length; i++) {
                            if (contents[i] != null) {
                                data.set("item." + i, contents[i]);
                                count++;
                            } else {
                                data.set("item." + i, "null");
                            }
                        }
                        try {
                            data.set("count", count);
                            if(plugin.isLargeChest(b.getLocation())){
                                data.set("isLargeChest", true);
                            }else{
                                data.set("isLargeChest", false);
                            }
                            data.save(f);
                            p.sendMessage(prefix + "チェストが保存されました： " + args[1]);
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                }
                if(args[0].equalsIgnoreCase("load")){
                    if(!p.hasPermission("mchest.load")){
                        p.sendMessage(noPermissionMessage("mchest.load"));
                        return true;
                    }
                    Block b = p.getTargetBlock((Set<Material>) null, 100);
                    String fileName = args[1];
                    File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("MChest").getDataFolder(), File.separator + "Chests");
                    File f = new File(dataa, File.separator + fileName + ".yml");
                    FileConfiguration data = YamlConfiguration.loadConfiguration(f);
                    boolean isLarge = data.getBoolean("isLargeChest");
                    if(b.getType() != Material.CHEST){
                        p.sendMessage(prefix + "ロード先はチェストで無ければいけません");
                        return true;
                    }
                    Chest c = (Chest) b.getState();
                    if(isLarge){
                        if(c.getInventory().getSize() == 54){
                            //ロードしたいファイルがラージチェストでロード先もラージだった場合ここ
                            for(int i = 0; i < 54; i++){
                                if(!data.get("item." + i).equals("null")){
                                    c.getInventory().setItem(i, (ItemStack) data.get("item." + i));
                                }
                            }
                            p.sendMessage(prefix + "チェストをロードしました");
                            return true;
                        }
                        p.sendMessage(prefix + "ラージチェストのロード先もラージチェストでなければいけません");
                        return true;
                    }
                    //ロードしたいファイルがラージチェストじゃなかった場合はここ
                    for(int i = 0; i < 27; i++){
                        if(!data.get("item." + i).equals("null")){
                            c.getInventory().setItem(i, (ItemStack) data.get("item." + i));
                        }

                    }
                    p.sendMessage(prefix + "チェストをロードしました");
                    return true;

                }
                if(args[0].equalsIgnoreCase("delete")){
                    if(!p.hasPermission("mchest.delete")){
                        p.sendMessage(noPermissionMessage("mchest.delete"));
                        return true;
                    }
                    String fileName = args[1];
                    File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("MChest").getDataFolder(), File.separator + "Chests");
                    File f = new File(dataa, File.separator + fileName + ".yml");
                    FileConfiguration data = YamlConfiguration.loadConfiguration(f);
                    if (f.exists()) {
                        f.delete();
                        p.sendMessage(prefix + fileName + "を消去しました");
                        return true;
                    }
                    p.sendMessage(prefix + fileName + "は存在しません");
                }
                p.sendMessage(prefix + "コマンドの使い方が間違ってます /mchest help");
                return true;
            }
            if(args.length >= 3){
                p.sendMessage(prefix + "コマンドの使い方が間違ってます /mchest help");
                return true;
            }
        }
        return false;
    }
    public String noPermissionMessage(String s){
        String message = prefix + "§c§lあなたは" + s + "の権限を持っていません";
        return message;
    }
    public void help(Player p){
        p.sendMessage("§8§l===============§7§l[§6§lmChest§7§l]§8§l===============");
        p.sendMessage("§6§l/mchest create <name> §e§lチェストを保存");
        p.sendMessage("§6§l/mchest load <name> §e§lチェストをロード");
        p.sendMessage("§6§l/mchest delete <name> §e§lチェストを消去");
        p.sendMessage("§6§l/mchest preview <name> §e§lチェストを観覧");
        p.sendMessage("§6§l/mchest list §e§l保存されたチェストを見る");
        p.sendMessage("§8§l=====================================");
    }
}
