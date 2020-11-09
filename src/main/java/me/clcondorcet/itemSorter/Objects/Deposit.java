package me.clcondorcet.itemSorter.Objects;

import me.clcondorcet.itemSorter.Main;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public class Deposit {

	public boolean isActive;
	public Location loc;
	public Location sign;
	
	public Deposit(Location loc, Location sign){
		this.loc = loc;
		this.sign = sign;
		this.isActive = true;
	}

	public void delete(){
		try{
			Sign sign = (Sign) this.sign.getBlock().getState();
			sign.setLine(0, "§4" + Main.configManager.config.sign_prefix.replaceAll("&.", ""));
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
			sign.update();
		}catch(Exception ignored){}
	}
}
