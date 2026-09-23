package me.clcondorcet.itemsorter.command.commands;

import me.clcondorcet.itemsorter.command.ItemSorterCommand;
import me.clcondorcet.itemsorter.purchase.SorterPurchaseGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyCommand extends ItemSorterCommand {

    public static final BuyCommand BUY_COMMAND = new BuyCommand("buy");

    private BuyCommand(String name) {
        super(name);
    }

    @Override
    public void runCommand(CommandSender sender, String label, String[] args) throws CommandReturn {
        checkPerm(sender, "itemsorter.command.buy");
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only available to players.");
            return;
        }
        SorterPurchaseGui.open((Player) sender);
    }
}
