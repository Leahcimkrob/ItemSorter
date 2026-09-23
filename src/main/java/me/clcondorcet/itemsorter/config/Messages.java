package me.clcondorcet.itemsorter.config;

import me.clcondorcet.itemsorter.ItemSorter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author clcondorcet
 */
public class Messages {
    public String msg_prefix;
    public String msg_alreadyExistFilter;
    public String msg_onlyOwnerTrust;
    public String msg_filterInUse;
    public String msg_filterNeedTrust;
    public String msg_baseDeleted;
    public String msg_needOwnerToBreak;
    public String msg_filterDeleted;
    public String msg_needTrustBreakFilter;
    public String msg_depositDeleted;
    public String msg_needTrustBreakDeposit;
    public String msg_onChestOrBarrel;
    public String msg_onEnderChest;
    public String msg_nameIncorrect;
    public String msg_alreadyABase;
    public String msg_tooMuchBase;
    public String msg_nameDoesNotExist;
    public String msg_notTrust;
    public String msg_notInRange;
    public String msg_baseCreated;
    public String msg_alreadyDepositorFilter;
    public String msg_error;
    public String msg_buyUnavailable;
    public String msg_buyNoEconomy;
    public String msg_buyNotEnoughMoney;
    public String msg_buyFailed;
    public String msg_buyComplete;

    public String inv_trustRemove;
    public String inv_trustAdd;
    public String inv_trustName;
    public ArrayList<String> inv_filterLore;
    public ArrayList<String> inv_filterTrashLore;
    public String inv_filterPriority;
    public String inv_filterTrashPriority;
    public String inv_filterName;
    public String inv_prevEnable;
    public String inv_prevDisable;
    public String inv_nextEnable;
    public String inv_nextDisable;
    public String inv_AddFilterName;
    public ArrayList<String> inv_AddFilterLore;
    public String inv_AddTrustName;
    public String inv_backName;
    public String inv_buyName;
    public String inv_buyInfoName;
    public ArrayList<String> inv_buyInfoLore;
    public String inv_buyOfferName;
    public ArrayList<String> inv_buyOfferLore;
    public String inv_buyCloseName;
    public ArrayList<String> inv_buyCloseLore;

    public String sign_prefix_input_is;
    public String sign_prefix_input_isd;
    public String sign_prefix_input_isf;

    public String formatted_sign_prefix_input_is;
    public String formatted_sign_prefix_input_isd;
    public String formatted_sign_prefix_input_isf;

    public String sign_prefix;
    public String sign_loading;
    public String sign_base;
    public String sign_filter;
    public String sign_deposit;

    public String cmd_dontHavePermission;
    public String cmd_commandNotFound;
    public String cmd_nav_before;
    public String cmd_nav_previous;
    public String cmd_nav_pageCount;
    public String cmd_nav_next;
    public String cmd_nav_after;
    public String cmd_help_hover;
    public String cmd_help_header;
    public String cmd_help_space1;
    public String cmd_help_help;
    public String cmd_help_list;
    public String cmd_help_base;
    public String cmd_help_filters;
    public String cmd_help_deposits;
    public String cmd_help_glow;
    public String cmd_help_autosign;
    public String cmd_help_reload;
    public String cmd_help_setOwner;
    public String cmd_help_space2;
    public String cmd_help_footer;
    public String cmd_reload_complete;
    public String cmd_list_header;
    public String cmd_list_prefix;
    public String cmd_list_name;
    public String cmd_list_hoverName;
    public String cmd_list_owner;
    public String cmd_list_hoverOwner;
    public String cmd_list_listEmpty;
    public String cmd_list_option;
    public String cmd_list_hoverOption;
    public String cmd_base_header;
    public String cmd_base_name;
    public String cmd_base_owner;
    public String cmd_base_ownerhover;
    public String cmd_base_location;
    public String cmd_base_locationHover;
    public String cmd_base_actions;
    public String cmd_base_actionGlow;
    public String cmd_base_actionGlowHover;
    public String cmd_base_trusts;
    public String cmd_base_trustShem;
    public String cmd_base_trustSeparator;
    public String cmd_base_filters;
    public String cmd_base_filtersHover;
    public String cmd_base_deposits;
    public String cmd_base_depositsHover;
    public String cmd_base_nameNotExist;
    public String cmd_base_noName;
    public String cmd_filters_header;
    public String cmd_filters_option;
    public String cmd_filters_hoverOption;
    public String cmd_filters_actions;
    public String cmd_filters_prefix;
    public String cmd_filters_priority;
    public String cmd_filters_actionGlow;
    public String cmd_filters_actionGlowHover;
    public String cmd_filters_actionGlowAllHover;
    public String cmd_filters_location;
    public String cmd_filters_locationHover;
    public String cmd_filters_baseNotExist;
    public String cmd_filters_optionEmpty;
    public String cmd_filters_noName;
    public String cmd_deposits_header;
    public String cmd_deposits_actions;
    public String cmd_deposits_prefix;
    public String cmd_deposits_actionGlow;
    public String cmd_deposits_actionGlowHover;
    public String cmd_deposits_actionGlowAllHover;
    public String cmd_deposits_location;
    public String cmd_deposits_locationHover;
    public String cmd_deposits_baseNotExist;
    public String cmd_deposits_empty;
    public String cmd_deposits_noName;
    public String cmd_glow_noOption;
    public String cmd_glow_noBase;
    public String cmd_glow_noOptionFilter;
    public String cmd_glow_notTrust;
    public String cmd_glow_notOwner;
    public String cmd_glow_baseNotExist;
    public String cmd_glow_noBlocsFound;
    public String cmd_glow_notInSameWorld;
    public String cmd_glow_notPlayer;
    public String cmd_glow_complete;
    public String cmd_glow_lowerVersion;
    public String cmd_autosign_complete;
    public String cmd_autosign_noBase;
    public String cmd_autosign_noOption;
    public String cmd_autosign_baseNotExist;
    public String cmd_autosign_notTrust;
    public String cmd_autosign_notPlayer;
    public String cmd_autosign_stop;
    public String cmd_setOwner_complete;
    public String cmd_setOwner_noPlayer;
    public String cmd_setOwner_noBase;
    public String cmd_setOwner_baseNotExist;

    public FileConfiguration conf;
    public FileConfiguration source;
    public FileConfiguration default_source;

    public Messages(FileConfiguration conf, FileConfiguration source, FileConfiguration default_source) {
        this.conf = conf;
        this.source = source;
        this.default_source = default_source;

        // Messages
        this.msg_prefix = getMessageFromFile("messages.prefix");
        this.msg_alreadyExistFilter = getMessageFromFile("messages.alreadyExistFilter");
        this.msg_onlyOwnerTrust = getMessageFromFile("messages.onlyOwnerTrust");
        this.msg_filterInUse = getMessageFromFile("messages.filterInUse");
        this.msg_filterNeedTrust = getMessageFromFile("messages.filterNeedTrust");
        this.msg_baseDeleted = getMessageFromFile("messages.baseDeleted");
        this.msg_needOwnerToBreak = getMessageFromFile("messages.needOwnerToBreak");
        this.msg_filterDeleted = getMessageFromFile("messages.filterDeleted");
        this.msg_needTrustBreakFilter = getMessageFromFile("messages.needTrustBreakFilter");
        this.msg_depositDeleted = getMessageFromFile("messages.depositDeleted");
        this.msg_needTrustBreakDeposit = getMessageFromFile("messages.needTrustBreakDeposit");
        this.msg_onChestOrBarrel = getMessageFromFile("messages.onChestOrBarrel");
        this.msg_onEnderChest = getMessageFromFile("messages.onEnderChest");
        this.msg_nameIncorrect = getMessageFromFile("messages.nameIncorrect");
        this.msg_alreadyABase = getMessageFromFile("messages.alreadyABase");
        this.msg_tooMuchBase = getMessageFromFile("messages.tooMuchBase");
        this.msg_nameDoesNotExist = getMessageFromFile("messages.nameDoesNotExist");
        this.msg_notTrust = getMessageFromFile("messages.notTrust");
        this.msg_notInRange = getMessageFromFile("messages.notInRange");
        this.msg_baseCreated = getMessageFromFile("messages.baseCreated");
        this.msg_alreadyDepositorFilter = getMessageFromFile("messages.alreadyDepositorFilter");
        this.msg_error = getMessageFromFile("messages.error");
        this.msg_buyUnavailable = getMessageFromFile("messages.buyUnavailable");
        this.msg_buyNoEconomy = getMessageFromFile("messages.buyNoEconomy");
        this.msg_buyNotEnoughMoney = getMessageFromFile("messages.buyNotEnoughMoney");
        this.msg_buyFailed = getMessageFromFile("messages.buyFailed");
        this.msg_buyComplete = getMessageFromFile("messages.buyComplete");


        // Inventory
        this.inv_trustRemove = getMessageFromFile("inventory.trustRemove");
        this.inv_trustAdd = getMessageFromFile("inventory.trustAdd");
        this.inv_trustName = getMessageFromFile("inventory.trustName");
        this.inv_filterLore = (ArrayList<String>) getFromFile("inventory.filterLore");
        this.inv_filterTrashLore = (ArrayList<String>) getFromFile("inventory.filterTrashLore");
        this.inv_filterPriority = getMessageFromFile("inventory.filterPriority");
        this.inv_filterTrashPriority = getMessageFromFile("inventory.filterTrashPriority");
        this.inv_filterName = getMessageFromFile("inventory.filterName");
        this.inv_prevEnable = getMessageFromFile("inventory.prevEnable");
        this.inv_prevDisable = getMessageFromFile("inventory.prevDisable");
        this.inv_nextEnable = getMessageFromFile("inventory.nextEnable");
        this.inv_nextDisable = getMessageFromFile("inventory.nextDisable");
        this.inv_AddFilterName = getMessageFromFile("inventory.AddFilterName");
        this.inv_AddFilterLore = (ArrayList<String>) getFromFile("inventory.AddFilterLore");
        this.inv_AddTrustName = getMessageFromFile("inventory.AddTrustName");
        this.inv_backName = getMessageFromFile("inventory.backName");
        this.inv_buyName = getMessageFromFile("inventory.buyName");
        this.inv_buyInfoName = getMessageFromFile("inventory.buyInfoName");
        this.inv_buyInfoLore = (ArrayList<String>) getFromFile("inventory.buyInfoLore");
        this.inv_buyOfferName = getMessageFromFile("inventory.buyOfferName");
        this.inv_buyOfferLore = (ArrayList<String>) getFromFile("inventory.buyOfferLore");
        this.inv_buyCloseName = getMessageFromFile("inventory.buyCloseName");
        this.inv_buyCloseLore = (ArrayList<String>) getFromFile("inventory.buyCloseLore");
        this.sign_prefix_input_is = getMessageFromFile("sign.input_is");
        this.sign_prefix_input_isd = getMessageFromFile("sign.input_isd");
        this.sign_prefix_input_isf = getMessageFromFile("sign.input_isf");
        this.formatted_sign_prefix_input_is = "[" + sign_prefix_input_is + "]";
        this.formatted_sign_prefix_input_isd = "[" + sign_prefix_input_isd + "]";
        this.formatted_sign_prefix_input_isf = "[" + sign_prefix_input_isf + "]";
        this.sign_prefix = getMessageFromFile("sign.prefix");
        this.sign_loading = getMessageFromFile("sign.loading");
        this.sign_base = getMessageFromFile("sign.base");
        this.sign_deposit = getMessageFromFile("sign.deposit");
        this.sign_filter = getMessageFromFile("sign.filter");


        // Commands
        this.cmd_dontHavePermission = getMessageFromFile("command.dontHavePermission");
        this.cmd_commandNotFound = getMessageFromFile("command.commandNotFound");
        this.cmd_nav_before = getMessageFromFile("command.navigation.before");
        this.cmd_nav_previous = getMessageFromFile("command.navigation.previous");
        this.cmd_nav_pageCount = getMessageFromFile("command.navigation.pageCount");
        this.cmd_nav_next = getMessageFromFile("command.navigation.next");
        this.cmd_nav_after = getMessageFromFile("command.navigation.after");

        this.cmd_help_hover = getMessageFromFile("command.help.hover");
        this.cmd_help_header = getMessageFromFile("command.help.header");
        this.cmd_help_space1 = getMessageFromFile("command.help.space1");
        this.cmd_help_help = getMessageFromFile("command.help.help");
        this.cmd_help_list = getMessageFromFile("command.help.list");
        this.cmd_help_base = getMessageFromFile("command.help.base");
        this.cmd_help_filters = getMessageFromFile("command.help.filters");
        this.cmd_help_deposits = getMessageFromFile("command.help.deposits");
        this.cmd_help_glow = getMessageFromFile("command.help.glow");
        this.cmd_help_autosign = getMessageFromFile("command.help.autosign");
        this.cmd_help_reload = getMessageFromFile("command.help.reload");
        this.cmd_help_setOwner = getMessageFromFile("command.help.setOwner");
        this.cmd_help_space2 = getMessageFromFile("command.help.space2");
        this.cmd_help_footer = getMessageFromFile("command.help.footer");

        this.cmd_reload_complete = getMessageFromFile("command.reload.complete");

        this.cmd_list_header = getMessageFromFile("command.list.header");
        this.cmd_list_prefix = getMessageFromFile("command.list.prefix");
        this.cmd_list_name = getMessageFromFile("command.list.name");
        this.cmd_list_hoverName = getMessageFromFile("command.list.hoverName");
        this.cmd_list_owner = getMessageFromFile("command.list.owner");
        this.cmd_list_hoverOwner = getMessageFromFile("command.list.hoverOwner");
        this.cmd_list_listEmpty = getMessageFromFile("command.list.listEmpty");
        this.cmd_list_option = getMessageFromFile("command.list.option");
        this.cmd_list_hoverOption = getMessageFromFile("command.list.hoverOption");

        this.cmd_base_header = getMessageFromFile("command.base.header");
        this.cmd_base_name = getMessageFromFile("command.base.name");
        this.cmd_base_owner = getMessageFromFile("command.base.owner");
        this.cmd_base_ownerhover = getMessageFromFile("command.base.ownerhover");
        this.cmd_base_location = getMessageFromFile("command.base.location");
        this.cmd_base_locationHover = getMessageFromFile("command.base.locationHover");
        this.cmd_base_actions = getMessageFromFile("command.base.actions");
        this.cmd_base_actionGlow = getMessageFromFile("command.base.actionGlow");
        this.cmd_base_actionGlowHover = getMessageFromFile("command.base.actionGlowHover");
        this.cmd_base_trusts = getMessageFromFile("command.base.trusts");
        this.cmd_base_trustShem = getMessageFromFile("command.base.trustShem");
        this.cmd_base_trustSeparator = getMessageFromFile("command.base.trustSeparator");
        this.cmd_base_filters = getMessageFromFile("command.base.filters");
        this.cmd_base_filtersHover = getMessageFromFile("command.base.filtersHover");
        this.cmd_base_deposits = getMessageFromFile("command.base.deposits");
        this.cmd_base_depositsHover = getMessageFromFile("command.base.depositsHover");
        this.cmd_base_nameNotExist = getMessageFromFile("command.base.nameNotExist");
        this.cmd_base_noName = getMessageFromFile("command.base.noName");

        this.cmd_filters_header = getMessageFromFile("command.filters.header");
        this.cmd_filters_option = getMessageFromFile("command.filters.option");
        this.cmd_filters_hoverOption = getMessageFromFile("command.filters.hoverOption");
        this.cmd_filters_actions = getMessageFromFile("command.filters.actions");
        this.cmd_filters_prefix = getMessageFromFile("command.filters.prefix");
        this.cmd_filters_priority = getMessageFromFile("command.filters.priority");
        this.cmd_filters_actionGlow = getMessageFromFile("command.filters.actionGlow");
        this.cmd_filters_actionGlowHover = getMessageFromFile("command.filters.actionGlowHover");
        this.cmd_filters_actionGlowAllHover = getMessageFromFile("command.filters.actionGlowAllHover");
        this.cmd_filters_location = getMessageFromFile("command.filters.location");
        this.cmd_filters_locationHover = getMessageFromFile("command.filters.locationHover");
        this.cmd_filters_baseNotExist = getMessageFromFile("command.filters.baseNotExist");
        this.cmd_filters_optionEmpty = getMessageFromFile("command.filters.optionEmpty");
        this.cmd_filters_noName = getMessageFromFile("command.filters.noName");

        this.cmd_deposits_header = getMessageFromFile("command.deposits.header");
        this.cmd_deposits_actions = getMessageFromFile("command.deposits.actions");
        this.cmd_deposits_prefix = getMessageFromFile("command.deposits.prefix");
        this.cmd_deposits_actionGlow = getMessageFromFile("command.deposits.actionGlow");
        this.cmd_deposits_actionGlowHover = getMessageFromFile("command.deposits.actionGlowHover");
        this.cmd_deposits_actionGlowAllHover = getMessageFromFile("command.deposits.actionGlowAllHover");
        this.cmd_deposits_location = getMessageFromFile("command.deposits.location");
        this.cmd_deposits_locationHover = getMessageFromFile("command.deposits.locationHover");
        this.cmd_deposits_baseNotExist = getMessageFromFile("command.deposits.baseNotExist");
        this.cmd_deposits_empty = getMessageFromFile("command.deposits.empty");
        this.cmd_deposits_noName = getMessageFromFile("command.deposits.noName");

        this.cmd_glow_noOption = getMessageFromFile("command.glow.noOption");
        this.cmd_glow_noBase = getMessageFromFile("command.glow.noBase");
        this.cmd_glow_noOptionFilter = getMessageFromFile("command.glow.noOptionFilter");
        this.cmd_glow_notTrust = getMessageFromFile("command.glow.notTrust");
        this.cmd_glow_notOwner = getMessageFromFile("command.glow.notOwner");
        this.cmd_glow_noBlocsFound = getMessageFromFile("command.glow.noBlocsFound");
        this.cmd_glow_notInSameWorld = getMessageFromFile("command.glow.notInSameWorld");
        this.cmd_glow_notPlayer = getMessageFromFile("command.glow.notPlayer");
        this.cmd_glow_complete = getMessageFromFile("command.glow.complete");
        this.cmd_glow_baseNotExist = getMessageFromFile("command.glow.baseNotExist");
        this.cmd_glow_lowerVersion =  getMessageFromFile("command.glow.lowerVersion");

        this.cmd_autosign_complete = getMessageFromFile("command.autosign.complete");
        this.cmd_autosign_noBase = getMessageFromFile("command.autosign.noBase");
        this.cmd_autosign_noOption = getMessageFromFile("command.autosign.noOption");
        this.cmd_autosign_baseNotExist = getMessageFromFile("command.autosign.baseNotExist");
        this.cmd_autosign_notTrust = getMessageFromFile("command.autosign.notTrust");
        this.cmd_autosign_notPlayer = getMessageFromFile("command.autosign.notPlayer");
        this.cmd_autosign_stop = getMessageFromFile("command.autosign.stop");

        this.cmd_setOwner_complete = getMessageFromFile("command.setOwner.complete");
        this.cmd_setOwner_noPlayer = getMessageFromFile("command.setOwner.noPlayer");
        this.cmd_setOwner_noBase = getMessageFromFile("command.setOwner.noBase");
        this.cmd_setOwner_baseNotExist = getMessageFromFile("command.setOwner.baseNotExist");

        try {
            ItemSorter.configManager.saveConfig("messages_" + ItemSorter.configManager.config.language.toUpperCase() + ".yml", new File(ItemSorter.getInstance().getDataFolder(), "translations"));
        } catch (Exception ex) {
            ItemSorter.getInstance().getLogger().severe("Error when loading the config file.");
            ex.printStackTrace();
        }
    }

    public String getMessageFromFile(String path) {
        return replaceColorCode((String) getFromFile(path));
    }

    public Object getFromFile(String path){
        Object sourceValue = getSourceValue(path);
        try {
            Object value = conf.get(path);
            if (value == null || value.getClass() != sourceValue.getClass()) {
                conf.set(path, sourceValue);
                value = sourceValue;
            }
            return value;
        } catch (Throwable err) {
            return sourceValue;
        }
    }

    public Object getSourceValue(String path) {
        try {
            return source.get(path);
        } catch (Throwable err) {
            return default_source.get(path);
        }
    }

    public static String replaceColorCode(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}