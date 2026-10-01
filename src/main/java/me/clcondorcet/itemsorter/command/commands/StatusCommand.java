package me.clcondorcet.itemsorter.command.commands;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.command.ItemSorterCommand;
import me.clcondorcet.itemsorter.utils.SorterStatus;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusCommand extends ItemSorterCommand {

    public static final StatusCommand STATUS_COMMAND = new StatusCommand("status");

    private StatusCommand(String name) {
        super(name);
    }

    @Override
    public void runCommand(CommandSender sender, String label, String[] args) throws CommandReturn {
        checkPerm(sender, "itemsorter.command.status");
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.cmd_status_otherUsage);
                return;
            }

            Player player = (Player) sender;
            sendStatus(sender, SorterStatus.getLore(player, player.hasPermission("itemsorter.command.buy")));
            return;
        }

        checkPerm(sender, "itemsorter.command.status.other");
        if (args.length != 1) {
            sender.sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.cmd_status_otherUsage);
            return;
        }

        Player onlineTarget = Bukkit.getPlayerExact(args[0]);
        if (onlineTarget != null) {
            sendOtherStatus(sender, onlineTarget.getName(), SorterStatus.getOtherLore(
                    onlineTarget, onlineTarget.hasPermission("itemsorter.command.buy")));
            return;
        }

        LuckPermsProvider.get().getUserManager().lookupUniqueId(args[0])
                .whenComplete((uniqueId, lookupError) -> Bukkit.getScheduler().runTask(ItemSorter.getInstance(), () -> {
                    if (lookupError != null || uniqueId == null) {
                        sendNotFound(sender, args[0]);
                        return;
                    }
                    OfflinePlayer target = Bukkit.getOfflinePlayer(uniqueId);
                    LuckPermsProvider.get().getUserManager().loadUser(uniqueId)
                            .whenComplete((user, error) -> Bukkit.getScheduler().runTask(ItemSorter.getInstance(), () -> {
                    if (error != null || user == null) {
                        if (error != null) {
                            ItemSorter.getInstance().getLogger().warning(
                                    "Could not load LuckPerms data for " + args[0] + ": " + error.getMessage());
                        }
                        sender.sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.cmd_status_otherError
                                .replace("%player%", args[0]));
                        return;
                    }
                    boolean includePurchaseDetails = user.getCachedData()
                            .getPermissionData(user.getQueryOptions())
                            .checkPermission("itemsorter.command.buy").asBoolean();
                    sendOtherStatus(sender, target.getName(), SorterStatus.getLore(
                            target, includePurchaseDetails, user));
                            }));
                }));
    }

    private static void sendNotFound(CommandSender sender, String playerName) {
        sender.sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.cmd_status_otherNotFound
                .replace("%player%", playerName));
    }

    private static void sendStatus(CommandSender sender, java.util.List<String> lore) {
        for (String line : lore) {
            sender.sendMessage(ItemSorter.prefix + line);
        }
    }

    private static void sendOtherStatus(CommandSender sender, String playerName, java.util.List<String> lore) {
        String header = ItemSorter.configManager.messages.cmd_status_otherHeader
                .replace("%player%", playerName);
        sender.sendMessage(ItemSorter.prefix + header);
        for (String line : lore) {
            sender.sendMessage(ItemSorter.prefix + line);
        }
    }
}
