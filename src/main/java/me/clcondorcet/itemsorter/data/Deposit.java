package me.clcondorcet.itemsorter.data;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.tools.BlockComparable;
import me.clcondorcet.itemsorter.data.tools.SignRefreshable;
import me.clcondorcet.itemsorter.database.schemas.DepositsTable;
import me.clcondorcet.itemsorter.utils.AsyncAction;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.sql.SQLException;

/**
 * @author clcondorcet
 */
public class Deposit implements SignRefreshable, BlockComparable {

	public final System sys;
	private int depositID;
	public final FutureLocation loc;
	public final FutureLocation sign;
	
	public Deposit(System sys, FutureLocation loc, FutureLocation sign) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.sys = sys;
		this.loc = loc;
		this.sign = sign;
		this.depositID = DepositsTable.insertDeposit(
				sys.systemID,
				loc.getWorldName(),
				loc.getBlockX(),
				loc.getBlockY(),
				loc.getBlockZ(),
				sign.getBlockX(),
				sign.getBlockY(),
				sign.getBlockZ()
		);
		sys.addDeposit(this);
	}

	public Deposit(System sys, FutureLocation loc, FutureLocation sign, AsyncAction.ItemSorterRunnable errorCallBack) {
		this.sys = sys;
		this.loc = loc;
		this.sign = sign;
		sys.addLoadingDeposit(this);
		ItemSorter.getInstance().asyncAction.async(() -> {
			if (!sys.getLoadingDeposits().contains(this)) { // First check waiting for runnable to start
				return;
			}
			this.depositID = DepositsTable.insertDeposit(
					sys.systemID,
					loc.getWorldName(),
					loc.getBlockX(),
					loc.getBlockY(),
					loc.getBlockZ(),
					sign.getBlockX(),
					sign.getBlockY(),
					sign.getBlockZ()
			);
			ItemSorter.getInstance().asyncAction.sync(() -> {
				if (sys.getLoadingDeposits().contains(this)) { // Second check after sql and sync runnable start
					sys.addDeposit(this);
					refreshSign(true);
				} else {
					sys.removeDeposit(this);
					delete(true, false, true);
				}
			});
		}, () -> {
			sys.removeDeposit(this);
			refreshSignAsync(false);
			errorCallBack.run();
		});
	}

	protected Deposit(int depositID, System sys, FutureLocation loc, FutureLocation sign){
		this.depositID = depositID;
		this.sys = sys;
		this.loc = loc;
		this.sign = sign;
		sys.addDeposit(this);
	}

	public void delete(boolean dbUpdate, boolean asyncSignUpdate, boolean disableRefreshSign) {
		sys.removeDeposit(this);
		if (dbUpdate && !sys.removeLoadingDeposit(this)) {
			ItemSorter.getInstance().asyncAction.async(() -> {
				DepositsTable.deleteDeposit(depositID);
			}, () -> {
				ItemSorter.getInstance().getLogger().warning("An error occurred. Normally it won't affect your world. If it did, please try contact clcondorcet (owner of the plugin).");
			});
		}
		if (!disableRefreshSign) {
			if (asyncSignUpdate) refreshSignAsync(false);
			else refreshSign(false);
		}
	}

	@Override
	public void refreshSign(boolean enable) {
		try{
			Sign sign = (Sign) this.sign.build().getBlock().getState();
			if (enable) {
				sign.setLine(0, ItemSorter.configManager.messages.sign_prefix);
				sign.setLine(1, "§b" + sys.name);
				sign.setLine(2, ItemSorter.configManager.messages.sign_deposit);
				sign.setLine(3, "§7(" + sys.getOwnerName() + ")");
			} else {
				sign.setLine(0, "§4" + ItemSorter.configManager.messages.sign_prefix.replaceAll("§.", ""));
				sign.setLine(1, "§4" + sys.name);
				sign.setLine(2, "§4§m" + ItemSorter.configManager.messages.sign_deposit.replaceAll("§.", ""));
				sign.setLine(3, "§4(" + sys.getOwnerName() + ")");
			}
			sign.update();
		}catch(Exception ignored){}
	}

	@Override
	public boolean isSameBlock(FutureLocation other) {
		return loc.sameBlock(other) || sign.sameBlock(other);
	}

	@Override
	public boolean isSameBlock(Location other) {
		return loc.sameBlock(other) || sign.sameBlock(other);
	}
}
