package me.clcondorcet.itemsorter.utils;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.config.Messages;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.System;
import net.luckperms.api.model.user.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SorterStatus {

    private static final Pattern MAX_BASES_PERMISSION = Pattern.compile("^itemsorter\\.maxsorter\\.(\\d+)$");

    private SorterStatus() {
    }

    public static int getOwnedSorterCount(Player player) {
        return getOwnedSorterCount((OfflinePlayer) player);
    }

    public static int getOwnedSorterCount(OfflinePlayer player) {
        int count = 0;
        for (System system : DataManager.getAllSystems()) {
            if (system.getOwnerUUID() != null
                    ? system.getOwnerUUID().equals(player.getUniqueId())
                    : player.getName() != null && system.getOwnerName() != null
                    && system.getOwnerName().equalsIgnoreCase(player.getName())) {
                count++;
            }
        }
        return count;
    }

    public static ArrayList<String> getLore(Player player, boolean includePurchaseDetails) {
        return getLore(getOwnedSorterCount(player), Utilities.getMaxBases(player),
                player.hasPermission("itemsorter.unlimitedBases"), includePurchaseDetails, true);
    }

    public static ArrayList<String> getOtherLore(Player player, boolean includePurchaseDetails) {
        return getLore(getOwnedSorterCount(player), Utilities.getMaxBases(player),
                player.hasPermission("itemsorter.unlimitedBases"), includePurchaseDetails, false);
    }

    public static ArrayList<String> getLore(OfflinePlayer player, boolean includePurchaseDetails, User user) {
        boolean unlimited = user.getCachedData().getPermissionData(user.getQueryOptions())
                .checkPermission("itemsorter.unlimitedBases").asBoolean();
        int limit = 0;
        for (net.luckperms.api.node.Node node : user.resolveInheritedNodes(user.getQueryOptions())) {
            if (!node.getValue()) {
                continue;
            }
            Matcher matcher = MAX_BASES_PERMISSION.matcher(node.getKey().toLowerCase(Locale.ROOT));
            if (matcher.matches()) {
                limit = Math.max(limit, Integer.parseInt(matcher.group(1)));
            }
        }
        return getLore(getOwnedSorterCount(player), limit, unlimited, includePurchaseDetails, false);
    }

    private static ArrayList<String> getLore(int owned, int limit, boolean unlimited,
                                             boolean includePurchaseDetails, boolean includeHeader) {
        int maximum = ItemSorter.configManager.config.maxSorters;
        int buyable = unlimited ? 0 : maximum < 0 ? -1 : Math.max(0, maximum - limit);
        ArrayList<String> lore = new ArrayList<>();

        for (int index = 0; index < ItemSorter.configManager.messages.cmd_status_lore.size(); index++) {
            if (!includeHeader && index == 0) {
                continue;
            }
            String line = ItemSorter.configManager.messages.cmd_status_lore.get(index);
            boolean purchaseDetail = line.contains("%buyable%") || line.contains("%max%");
            if (purchaseDetail && !includePurchaseDetails) {
                continue;
            }
            lore.add(Messages.replaceColorCode(line)
                    .replace("%owned%", String.valueOf(owned))
                    .replace("%limit%", unlimited ? "∞" : String.valueOf(limit))
                    .replace("%buyable%", buyable < 0 ? "∞" : String.valueOf(buyable))
                    .replace("%max%", unlimited || maximum < 0 ? "∞" : String.valueOf(maximum)));
        }
        return lore;
    }
}
