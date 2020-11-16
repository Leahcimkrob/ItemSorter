package me.clcondorcet.itemSorter.config;

import me.clcondorcet.itemSorter.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;

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

    public String inv_trustRemove;
    public String inv_trustAdd;
    public String inv_trustName;
    public String inv_filterRemove;
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

    public String sign_prefix_input_is;
    public String sign_prefix_input_isd;
    public String sign_prefix_input_isf;

    public String sign_prefix;

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
    public String cmd_help_reload;
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

    public Messages(FileConfiguration conf, FileConfiguration source) {
        // Messages
        this.msg_prefix = (String) getfromsource(conf, source, "messages.prefix");
        this.msg_alreadyExistFilter = (String) getfromsource(conf, source, "messages.alreadyExistFilter");
        this.msg_onlyOwnerTrust = (String) getfromsource(conf, source, "messages.onlyOwnerTrust");
        this.msg_filterInUse = (String) getfromsource(conf, source, "messages.filterInUse");
        this.msg_filterNeedTrust = (String) getfromsource(conf, source, "messages.filterNeedTrust");
        this.msg_baseDeleted = (String) getfromsource(conf, source, "messages.baseDeleted");
        this.msg_needOwnerToBreak = (String) getfromsource(conf, source, "messages.needOwnerToBreak");
        this.msg_filterDeleted = (String) getfromsource(conf, source, "messages.filterDeleted");
        this.msg_needTrustBreakFilter = (String) getfromsource(conf, source, "messages.needTrustBreakFilter");
        this.msg_depositDeleted = (String) getfromsource(conf, source, "messages.depositDeleted");
        this.msg_needTrustBreakDeposit = (String) getfromsource(conf, source, "messages.needTrustBreakDeposit");
        this.msg_onChestOrBarrel = (String) getfromsource(conf, source, "messages.onChestOrBarrel");
        this.msg_onEnderChest = (String) getfromsource(conf, source, "messages.onEnderChest");
        this.msg_nameIncorrect = (String) getfromsource(conf, source, "messages.nameIncorrect");
        this.msg_alreadyABase = (String) getfromsource(conf, source, "messages.alreadyABase");
        this.msg_tooMuchBase = (String) getfromsource(conf, source, "messages.tooMuchBase");
        this.msg_nameDoesNotExist = (String) getfromsource(conf, source, "messages.nameDoesNotExist");
        this.msg_notTrust = (String) getfromsource(conf, source, "messages.notTrust");
        this.msg_notInRange = (String) getfromsource(conf, source, "messages.notInRange");
        this.msg_baseCreated = (String) getfromsource(conf, source, "messages.baseCreated");
        this.msg_alreadyDepositorFilter = (String) getfromsource(conf, source, "messages.alreadyDepositorFilter");


        // Inventory
        this.inv_trustRemove = (String) getfromsource(conf, source, "inventory.trustRemove");
        this.inv_trustAdd = (String) getfromsource(conf, source, "inventory.trustAdd");
        this.inv_trustName = (String) getfromsource(conf, source, "inventory.trustName");
        this.inv_filterRemove = (String) getfromsource(conf, source, "inventory.filterRemove");
        this.inv_filterPriority = (String) getfromsource(conf, source, "inventory.filterPriority");
        this.inv_filterTrashPriority = (String) getfromsource(conf, source, "inventory.filterTrashPriority");
        this.inv_filterName = (String) getfromsource(conf, source, "inventory.filterName");
        this.inv_prevEnable = (String) getfromsource(conf, source, "inventory.prevEnable");
        this.inv_prevDisable = (String) getfromsource(conf, source, "inventory.prevDisable");
        this.inv_nextEnable = (String) getfromsource(conf, source, "inventory.nextEnable");
        this.inv_nextDisable = (String) getfromsource(conf, source, "inventory.nextDisable");
        this.inv_AddFilterName = (String) getfromsource(conf, source, "inventory.AddFilterName");
        this.inv_AddFilterLore = (ArrayList<String>) getfromsource(conf, source, "inventory.AddFilterLore");
        this.inv_AddTrustName = (String) getfromsource(conf, source, "inventory.AddTrustName");
        this.inv_backName = (String) getfromsource(conf, source, "inventory.backName");
        this.sign_prefix_input_is = (String) getfromsource(conf, source, "sign.input_is");
        this.sign_prefix_input_isd = (String) getfromsource(conf, source, "sign.input_isd");
        this.sign_prefix_input_isf = (String) getfromsource(conf, source, "sign.input_isf");
        this.sign_prefix = (String) getfromsource(conf, source, "sign.prefix");


        // Commands
        this.cmd_dontHavePermission = (String) getfromsource(conf, source, "command.dontHavePermission");
        this.cmd_commandNotFound = (String) getfromsource(conf, source, "command.commandNotFound");
        this.cmd_nav_before = (String) getfromsource(conf, source, "command.navigation.before");
        this.cmd_nav_previous = (String) getfromsource(conf, source, "command.navigation.previous");
        this.cmd_nav_pageCount = (String) getfromsource(conf, source, "command.navigation.pageCount");
        this.cmd_nav_next = (String) getfromsource(conf, source, "command.navigation.next");
        this.cmd_nav_after = (String) getfromsource(conf, source, "command.navigation.after");

        this.cmd_help_hover = (String) getfromsource(conf, source, "command.help.hover");
        this.cmd_help_header = (String) getfromsource(conf, source, "command.help.header");
        this.cmd_help_space1 = (String) getfromsource(conf, source, "command.help.space1");
        this.cmd_help_help = (String) getfromsource(conf, source, "command.help.help");
        this.cmd_help_list = (String) getfromsource(conf, source, "command.help.list");
        this.cmd_help_base = (String) getfromsource(conf, source, "command.help.base");
        this.cmd_help_filters = (String) getfromsource(conf, source, "command.help.filters");
        this.cmd_help_deposits = (String) getfromsource(conf, source, "command.help.deposits");
        this.cmd_help_glow = (String) getfromsource(conf, source, "command.help.glow");
        this.cmd_help_reload = (String) getfromsource(conf, source, "command.help.reload");
        this.cmd_help_space2 = (String) getfromsource(conf, source, "command.help.space2");
        this.cmd_help_footer = (String) getfromsource(conf, source, "command.help.footer");

        this.cmd_reload_complete = (String) getfromsource(conf, source, "command.reload.complete");

        this.cmd_list_header = (String) getfromsource(conf, source, "command.list.header");
        this.cmd_list_prefix = (String) getfromsource(conf, source, "command.list.prefix");
        this.cmd_list_name = (String) getfromsource(conf, source, "command.list.name");
        this.cmd_list_hoverName = (String) getfromsource(conf, source, "command.list.hoverName");
        this.cmd_list_owner = (String) getfromsource(conf, source, "command.list.owner");
        this.cmd_list_hoverOwner = (String) getfromsource(conf, source, "command.list.hoverOwner");
        this.cmd_list_listEmpty = (String) getfromsource(conf, source, "command.list.listEmpty");
        this.cmd_list_option = (String) getfromsource(conf, source, "command.list.option");
        this.cmd_list_hoverOption = (String) getfromsource(conf, source, "command.list.hoverOption");

        this.cmd_base_header = (String) getfromsource(conf, source, "command.base.header");
        this.cmd_base_name = (String) getfromsource(conf, source, "command.base.name");
        this.cmd_base_owner = (String) getfromsource(conf, source, "command.base.owner");
        this.cmd_base_ownerhover = (String) getfromsource(conf, source, "command.base.ownerhover");
        this.cmd_base_location = (String) getfromsource(conf, source, "command.base.location");
        this.cmd_base_locationHover = (String) getfromsource(conf, source, "command.base.locationHover");
        this.cmd_base_actions = (String) getfromsource(conf, source, "command.base.actions");
        this.cmd_base_actionGlow = (String) getfromsource(conf, source, "command.base.actionGlow");
        this.cmd_base_actionGlowHover = (String) getfromsource(conf, source, "command.base.actionGlowHover");
        this.cmd_base_trusts = (String) getfromsource(conf, source, "command.base.trusts");
        this.cmd_base_trustShem = (String) getfromsource(conf, source, "command.base.trustShem");
        this.cmd_base_trustSeparator = (String) getfromsource(conf, source, "command.base.trustSeparator");
        this.cmd_base_filters = (String) getfromsource(conf, source, "command.base.filters");
        this.cmd_base_filtersHover = (String) getfromsource(conf, source, "command.base.filtersHover");
        this.cmd_base_deposits = (String) getfromsource(conf, source, "command.base.deposits");
        this.cmd_base_depositsHover = (String) getfromsource(conf, source, "command.base.depositsHover");
        this.cmd_base_nameNotExist = (String) getfromsource(conf, source, "command.base.nameNotExist");
        this.cmd_base_noName = (String) getfromsource(conf, source, "command.base.noName");

        this.cmd_filters_header = (String) getfromsource(conf, source, "command.filters.header");
        this.cmd_filters_option = (String) getfromsource(conf, source, "command.filters.option");
        this.cmd_filters_hoverOption = (String) getfromsource(conf, source, "command.filters.hoverOption");
        this.cmd_filters_actions = (String) getfromsource(conf, source, "command.filters.actions");
        this.cmd_filters_prefix = (String) getfromsource(conf, source, "command.filters.prefix");
        this.cmd_filters_priority = (String) getfromsource(conf, source, "command.filters.priority");
        this.cmd_filters_actionGlow = (String) getfromsource(conf, source, "command.filters.actionGlow");
        this.cmd_filters_actionGlowHover = (String) getfromsource(conf, source, "command.filters.actionGlowHover");
        this.cmd_filters_actionGlowAllHover = (String) getfromsource(conf, source, "command.filters.actionGlowAllHover");
        this.cmd_filters_location = (String) getfromsource(conf, source, "command.filters.location");
        this.cmd_filters_locationHover = (String) getfromsource(conf, source, "command.filters.locationHover");
        this.cmd_filters_baseNotExist = (String) getfromsource(conf, source, "command.filters.baseNotExist");
        this.cmd_filters_optionEmpty = (String) getfromsource(conf, source, "command.filters.optionEmpty");
        this.cmd_filters_noName = (String) getfromsource(conf, source, "command.filters.noName");

        this.cmd_deposits_header = (String) getfromsource(conf, source, "command.deposits.header");
        this.cmd_deposits_actions = (String) getfromsource(conf, source, "command.deposits.actions");
        this.cmd_deposits_prefix = (String) getfromsource(conf, source, "command.deposits.prefix");
        this.cmd_deposits_actionGlow = (String) getfromsource(conf, source, "command.deposits.actionGlow");
        this.cmd_deposits_actionGlowHover = (String) getfromsource(conf, source, "command.deposits.actionGlowHover");
        this.cmd_deposits_actionGlowAllHover = (String) getfromsource(conf, source, "command.deposits.actionGlowAllHover");
        this.cmd_deposits_location = (String) getfromsource(conf, source, "command.deposits.location");
        this.cmd_deposits_locationHover = (String) getfromsource(conf, source, "command.deposits.locationHover");
        this.cmd_deposits_baseNotExist = (String) getfromsource(conf, source, "command.deposits.baseNotExist");
        this.cmd_deposits_empty = (String) getfromsource(conf, source, "command.deposits.empty");
        this.cmd_deposits_noName = (String) getfromsource(conf, source, "command.deposits.noName");

        this.cmd_glow_noOption = (String) getfromsource(conf, source, "command.glow.noOption");
        this.cmd_glow_noBase = (String) getfromsource(conf, source, "command.glow.noBase");
        this.cmd_glow_noOptionFilter = (String) getfromsource(conf, source, "command.glow.noOptionFilter");
        this.cmd_glow_notTrust = (String) getfromsource(conf, source, "command.glow.notTrust");
        this.cmd_glow_notOwner = (String) getfromsource(conf, source, "command.glow.notOwner");
        this.cmd_glow_noBlocsFound = (String) getfromsource(conf, source, "command.glow.noBlocsFound");
        this.cmd_glow_notInSameWorld = (String) getfromsource(conf, source, "command.glow.notInSameWorld");
        this.cmd_glow_notPlayer = (String) getfromsource(conf, source, "command.glow.notPlayer");
        this.cmd_glow_complete = (String) getfromsource(conf, source, "command.glow.complete");
        this.cmd_glow_baseNotExist = (String) getfromsource(conf, source, "command.glow.baseNotExist");
        this.cmd_glow_lowerVersion =  (String) getfromsource(conf, source, "command.glow.lowerVersion");

        try {
            Main.configManager.saveConfig("messages_" + Main.configManager.config.language.toUpperCase() + ".yml", new File(Main.getInstance().getDataFolder(), "translations"));
        } catch (Exception ex) {
            Main.log.severe("Error when loading the config file.");
            ex.printStackTrace();
        }
    }

    public Object getfromsource(FileConfiguration conf, FileConfiguration source, String path){
        return getOrDefault(conf, path, source.get(path));
    }

    public Object getOrDefault(FileConfiguration conf, String path, Object value) {
        Object a = conf.get(path);
        if (a == null || a.getClass() != value.getClass()) {
            conf.set(path, value);
            a = value;
        }
        return a;
    }
}