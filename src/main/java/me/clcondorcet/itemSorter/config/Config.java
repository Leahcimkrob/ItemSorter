package me.clcondorcet.itemSorter.config;

import me.clcondorcet.itemSorter.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    public int radius;
    public int verticalRadius;
    public int maxBases_default;
    public HashMap<String, Integer> maxBases_others;

    public String msg_prefix;
    public String msg_reload;
    public String msg_dontHavePermission;
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

    public Config(FileConfiguration conf, FileConfiguration source){
        this.radius = (int) getOrDefault(conf, "radius", source.get("radius"));
        this.verticalRadius = (int) getOrDefault(conf, "verticalRadius", source.get("verticalRadius"));
        this.maxBases_default = (int) getOrDefault(conf, "maxBases.default", source.get("maxBases.default"));
        List<Map<?, ?>> map = (List<Map<?, ?>>) getOrDefault(conf, "maxBases.others", source.get("maxBases.others"));
        this.maxBases_others = new HashMap<>();
        try {
            for (Map<?, ?> element : map) {
                for (Object key : element.keySet()) {
                    try {
                        this.maxBases_others.put(key.toString(), (Integer) element.get(key));
                    } catch (Exception ex) {
                        Main.log.severe("There is an error in the config file at: MaxBases.others please try fix it or restore it !");
                        Main.log.severe("It failed at: " + element.toString());
                        throw ex;
                    }
                }
            }
        }catch(Exception ex){
            Main.log.severe("The error:");
            ex.printStackTrace();
        }

        this.msg_prefix = (String) getOrDefault(conf, "messages.prefix", source.get("messages.prefix"));
        this.msg_reload = (String) getOrDefault(conf, "messages.reload", source.get("messages.reload"));
        this.msg_dontHavePermission = (String) getOrDefault(conf, "messages.dontHavePermission", source.get("messages.dontHavePermission"));
        this.msg_alreadyExistFilter = (String) getOrDefault(conf, "messages.alreadyExistFilter", source.get("messages.alreadyExistFilter"));
        this.msg_onlyOwnerTrust = (String) getOrDefault(conf, "messages.onlyOwnerTrust", source.get("messages.onlyOwnerTrust"));
        this.msg_filterInUse = (String) getOrDefault(conf, "messages.filterInUse", source.get("messages.filterInUse"));
        this.msg_filterNeedTrust = (String) getOrDefault(conf, "messages.filterNeedTrust", source.get("messages.filterNeedTrust"));
        this.msg_baseDeleted = (String) getOrDefault(conf, "messages.baseDeleted", source.get("messages.baseDeleted"));
        this.msg_needOwnerToBreak = (String) getOrDefault(conf, "messages.needOwnerToBreak", source.get("messages.needOwnerToBreak"));
        this.msg_filterDeleted = (String) getOrDefault(conf, "messages.filterDeleted", source.get("messages.filterDeleted"));
        this.msg_needTrustBreakFilter = (String) getOrDefault(conf, "messages.needTrustBreakFilter", source.get("messages.needTrustBreakFilter"));
        this.msg_depositDeleted = (String) getOrDefault(conf, "messages.depositDeleted", source.get("messages.depositDeleted"));
        this.msg_needTrustBreakDeposit = (String) getOrDefault(conf, "messages.needTrustBreakDeposit", source.get("messages.needTrustBreakDeposit"));
        this.msg_onChestOrBarrel = (String) getOrDefault(conf, "messages.onChestOrBarrel", source.get("messages.onChestOrBarrel"));
        this.msg_onEnderChest = (String) getOrDefault(conf, "messages.onEnderChest", source.get("messages.onEnderChest"));
        this.msg_nameIncorrect = (String) getOrDefault(conf, "messages.nameIncorrect", source.get("messages.nameIncorrect"));
        this.msg_alreadyABase = (String) getOrDefault(conf, "messages.alreadyABase", source.get("messages.alreadyABase"));
        this.msg_tooMuchBase = (String) getOrDefault(conf, "messages.tooMuchBase", source.get("messages.tooMuchBase"));
        this.msg_nameDoesNotExist = (String) getOrDefault(conf, "messages.nameDoesNotExist", source.get("messages.nameDoesNotExist"));
        this.msg_notTrust = (String) getOrDefault(conf, "messages.notTrust", source.get("messages.notTrust"));
        this.msg_notInRange = (String) getOrDefault(conf, "messages.notInRange", source.get("messages.notInRange"));
        this.msg_baseCreated = (String) getOrDefault(conf, "messages.baseCreated", source.get("messages.baseCreated"));
        this.msg_alreadyDepositorFilter = (String) getOrDefault(conf, "messages.alreadyDepositorFilter", source.get("messages.alreadyDepositorFilter"));

        this.inv_trustRemove = (String) getOrDefault(conf, "inventory.trustRemove", source.get("inventory.trustRemove"));
        this.inv_trustAdd = (String) getOrDefault(conf, "inventory.trustAdd", source.get("inventory.trustAdd"));
        this.inv_trustName = (String) getOrDefault(conf, "inventory.trustName", source.get("inventory.trustName"));
        this.inv_filterRemove = (String) getOrDefault(conf, "inventory.filterRemove", source.get("inventory.filterRemove"));
        this.inv_filterPriority = (String) getOrDefault(conf, "inventory.filterPriority", source.get("inventory.filterPriority"));
        this.inv_filterTrashPriority = (String) getOrDefault(conf, "inventory.filterTrashPriority", source.get("inventory.filterTrashPriority"));
        this.inv_filterName = (String) getOrDefault(conf, "inventory.filterName", source.get("inventory.filterName"));
        this.inv_prevEnable = (String) getOrDefault(conf, "inventory.prevEnable", source.get("inventory.prevEnable"));
        this.inv_prevDisable = (String) getOrDefault(conf, "inventory.prevDisable", source.get("inventory.prevDisable"));
        this.inv_nextEnable = (String) getOrDefault(conf, "inventory.nextEnable", source.get("inventory.nextEnable"));
        this.inv_nextDisable = (String) getOrDefault(conf, "inventory.nextDisable", source.get("inventory.nextDisable"));
        this.inv_AddFilterName = (String) getOrDefault(conf, "inventory.AddFilterName", source.get("inventory.AddFilterName"));
        this.inv_AddFilterLore = (ArrayList<String>) getOrDefault(conf, "inventory.AddFilterLore", source.get("inventory.AddFilterLore"));
        this.inv_AddTrustName = (String) getOrDefault(conf, "inventory.AddTrustName", source.get("inventory.AddTrustName"));
        this.inv_backName = (String) getOrDefault(conf, "inventory.backName", source.get("inventory.backName"));
        this.sign_prefix_input_is = (String) getOrDefault(conf, "sign.input_is", source.get("sign.input_is"));
        this.sign_prefix_input_isd = (String) getOrDefault(conf, "sign.input_isd", source.get("sign.input_isd"));
        this.sign_prefix_input_isf = (String) getOrDefault(conf, "sign.input_isf", source.get("sign.input_isf"));
        this.sign_prefix = (String) getOrDefault(conf, "sign.prefix", source.get("sign.prefix"));

        try{
            Main.configManager.saveConfig("config.yml", Main.getInstance().getDataFolder());
        }catch(Exception ex){
            Main.log.severe("Error when loading the config file.");
            ex.printStackTrace();
        }
    }

    public Object getOrDefault(FileConfiguration conf, String path, Object value){
        Object a = conf.get(path);
        if(a == null || a.getClass() != value.getClass()){
            conf.set(path, value);
            a = value;
        }
        return a;
    }
}