package me.clcondorcet.itemSorter.Objects;

import java.util.HashMap;
import java.util.List;

import me.clcondorcet.itemSorter.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;

public class Filter {

	public boolean isActive;
	public Location loc;
	public HashMap<Material, Integer> materials = new HashMap<>();
	public boolean isTrash;
	public int trashPriority;
	public Location sign;
	
	public Filter(Location loc, Location sign, List<String> lists){
		this.loc = loc;
		this.sign = sign;
		for(String sts : lists){
			String[] st = sts.split(":");
			materials.put(Material.valueOf(st[0]), Integer.parseInt(st[1]));
		}
		this.isTrash = false;
		this.trashPriority = -1;
		this.isActive = true;
	}
	
	public Filter(Location loc, Location sign, int trashPriority){
		this.loc = loc;
		this.sign = sign;
		this.isTrash = true;
		this.trashPriority = trashPriority;
		this.isActive = true;
	}

	public int canFilter(Material mat){
		if(!this.isActive){
			return -2;
		}
		if(isTrash){
			return trashPriority;
		}else{
			return materials.getOrDefault(mat, -1);
		}
	}

	public void delete(){
		try{
			Sign sign = (Sign) this.sign.getBlock().getState();
			sign.setLine(0, "§4" + Main.configManager.messages.sign_prefix.replaceAll("&.", ""));
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
			sign.update();
		}catch(Exception ignored){}
	}
}
