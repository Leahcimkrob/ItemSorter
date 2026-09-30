package me.clcondorcet.itemsorter.purchase;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.config.Messages;
import me.clcondorcet.itemsorter.utils.SorterStatus;
import me.clcondorcet.itemsorter.utils.Utilities;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class SorterPurchaseGui {

    private static final String MAX_SORTERS_PERMISSION_PREFIX = "itemsorter.maxsorter.";
    private static final Set<UUID> PENDING_PURCHASES = new HashSet<>();

    private SorterPurchaseGui() {
    }

    public static void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new Holder(), 18, ItemSorter.configManager.messages.inv_buyName);
        refresh(player, inventory);
        player.openInventory(inventory);
    }

    public static boolean isPurchaseInventory(Inventory inventory) {
        return inventory.getHolder() instanceof Holder;
    }

    public static void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (event.getRawSlot() == 8) {
            player.closeInventory();
            return;
        }
        if (event.getRawSlot() >= 9 && event.getRawSlot() <= 17) {
            purchase(player, event.getRawSlot() - 8, event.getInventory());
        }
    }

    private static void refresh(Player player, Inventory inventory) {
        inventory.clear();
        int limit = Utilities.getMaxBases(player);
        int owned = SorterStatus.getOwnedSorterCount(player);
        int buyable = getBuyableAmount(limit);
        boolean unlimited = player.hasPermission("itemsorter.unlimitedBases");

        ItemStack info = new ItemStack(Material.OAK_SIGN);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(ItemSorter.configManager.messages.inv_buyInfoName);
        ArrayList<String> infoLore = new ArrayList<>();
        for (String line : ItemSorter.configManager.messages.inv_buyInfoLore) {
                infoLore.add(format(line, owned, unlimited ? -1 : limit, buyable, 0, 0));
        }
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        inventory.setItem(4, info);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ItemSorter.configManager.messages.inv_buyCloseName);
        ArrayList<String> closeLore = new ArrayList<>();
        for (String line : ItemSorter.configManager.messages.inv_buyCloseLore) {
            closeLore.add(format(line, owned, unlimited ? -1 : limit, buyable, 0, 0));
        }
        closeMeta.setLore(closeLore);
        close.setItemMeta(closeMeta);
        inventory.setItem(8, close);

        if (unlimited) {
                for (int slot = 9; slot < 18; slot++) {
                    ItemStack barrier = new ItemStack(Material.BARRIER);
                    ItemMeta barrierMeta = barrier.getItemMeta();
                    barrierMeta.setDisplayName(ItemSorter.configManager.messages.inv_buyUnlimitedName);
                    ArrayList<String> barrierLore = new ArrayList<>();
                    for (String line : ItemSorter.configManager.messages.inv_buyUnlimitedLore) {
                        barrierLore.add(format(line, owned, -1, 0, 0, 0));
                    }
                    barrierMeta.setLore(barrierLore);
                    barrier.setItemMeta(barrierMeta);
                    inventory.setItem(slot, barrier);
                }
                return;
        }

        int shownOffers = Math.min(9, buyable);
        for (int amount = 1; amount <= shownOffers; amount++) {
                double cost = ItemSorter.configManager.config.sorterPrice * amount;
                ItemStack offer = new ItemStack(Material.ENDER_CHEST);
                offer.setAmount(amount);
            ItemMeta offerMeta = offer.getItemMeta();
            offerMeta.setDisplayName(format(ItemSorter.configManager.messages.inv_buyOfferName, owned, limit, buyable, amount, cost));
            ArrayList<String> offerLore = new ArrayList<>();
            for (String line : ItemSorter.configManager.messages.inv_buyOfferLore) {
                offerLore.add(format(line, owned, limit, buyable, amount, cost));
            }
            offerMeta.setLore(offerLore);
            offer.setItemMeta(offerMeta);
            inventory.setItem(8 + amount, offer);
        }
    }

    private static void purchase(Player player, int amount, Inventory inventory) {
        synchronized (PENDING_PURCHASES) {
            if (!PENDING_PURCHASES.add(player.getUniqueId())) {
                return;
            }
        }

        if (player.hasPermission("itemsorter.unlimitedBases")) {
            finishPurchase(player, inventory, "§cYou already have unlimited sorters.");
            return;
        }

        int currentLimit = Utilities.getMaxBases(player);
        int buyable = getBuyableAmount(currentLimit);
        if (amount > buyable) {
            finishPurchase(player, inventory, "§cThat purchase is no longer available.");
            return;
        }

        Economy economy = getEconomy();
        if (economy == null) {
            finishPurchase(player, inventory, "§cNo Vault economy provider is available.");
            return;
        }

        double cost = ItemSorter.configManager.config.sorterPrice * amount;
        EconomyResponse withdrawal = economy.withdrawPlayer(player, cost);
        if (!withdrawal.transactionSuccess()) {
            finishPurchase(player, inventory, "§cYou do not have enough money.");
            return;
        }

        int newLimit = currentLimit + amount;
        LuckPerms luckPerms = LuckPermsProvider.get();
        luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> updateLimitNode(user, newLimit))
                .whenComplete((ignored, throwable) -> Bukkit.getScheduler().runTask(ItemSorter.getInstance(), () -> {
                    if (throwable != null) {
                        economy.depositPlayer(player, cost);
                        finishPurchase(player, inventory, "§cThe purchase failed. Your money was refunded.");
                        return;
                    }
                    finishPurchase(player, inventory, "§aPurchased " + amount + " additional sorter" + (amount == 1 ? "" : "s") + ".");
                }));
    }

    private static void updateLimitNode(User user, int newLimit) {
        for (Node node : user.getNodes()) {
            if (node.getKey().matches("^itemsorter\\.maxsorter\\.\\d+$")) {
                user.data().remove(node);
            }
        }
        user.data().add(Node.builder(MAX_SORTERS_PERMISSION_PREFIX + newLimit).build());
    }

    private static void finishPurchase(Player player, Inventory inventory, String message) {
        synchronized (PENDING_PURCHASES) {
            PENDING_PURCHASES.remove(player.getUniqueId());
        }
        if (player.isOnline() && player.getOpenInventory().getTopInventory().equals(inventory)) {
            refresh(player, inventory);
        }
        player.sendMessage(ItemSorter.prefix + message);
    }

    private static Economy getEconomy() {
        RegisteredServiceProvider<Economy> registration = Bukkit.getServicesManager().getRegistration(Economy.class);
        return registration == null ? null : registration.getProvider();
    }

    private static int getBuyableAmount(int currentLimit) {
        int configuredMaximum = ItemSorter.configManager.config.maxSorters;
        return configuredMaximum < 0 ? 9 : Math.max(0, configuredMaximum - currentLimit);
    }

    private static String formatPrice(double price) {
        Economy economy = getEconomy();
        return economy == null ? String.format("%.2f", price) : economy.format(price);
    }

    private static String format(String value, int owned, int limit, int buyable, int amount, double total) {
        return Messages.replaceColorCode(value)
                .replace("%owned%", String.valueOf(owned))
                .replace("%limit%", limit < 0 ? "∞" : String.valueOf(limit))
                .replace("%buyable%", String.valueOf(buyable))
                .replace("%max%", getMaximumDisplay())
                .replace("%amount%", String.valueOf(amount))
                .replace("%price%", formatPrice(ItemSorter.configManager.config.sorterPrice))
                .replace("%total%", formatPrice(total));
    }

    private static String getMaximumDisplay() {
        int configuredMaximum = ItemSorter.configManager.config.maxSorters;
        return configuredMaximum < 0 ? "Unlimited" : String.valueOf(configuredMaximum);
    }

    private static final class Holder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
