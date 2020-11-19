package me.clcondorcet.itemSorter.Commands;

import me.clcondorcet.itemSorter.Events.Event;
import me.clcondorcet.itemSorter.Main;
import me.clcondorcet.itemSorter.Objects.CachedItems;
import me.clcondorcet.itemSorter.Objects.Deposit;
import me.clcondorcet.itemSorter.Objects.Filter;
import me.clcondorcet.itemSorter.Objects.System;
import me.clcondorcet.itemSorter.Utilities;
import me.clcondorcet.itemSorter.config.Messages;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Commands implements CommandExecutor, TabCompleter {

	public static HashMap<Player, ArrayList<String>> glowMap = new HashMap<>();
	public static HashMap<Player, BukkitTask> glowTasks = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		Messages msg = Main.configManager.messages;
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			if(s.hasPermission("itemsorter.command.help")){
				sendBase(s, msg.cmd_help_header);
				sendBase(s, msg.cmd_help_space1);
				sendBasePermhs(s, s.hasPermission("itemsorter.command.help"), msg.cmd_help_help.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " help");
				sendBasePermhs(s, s.hasPermission("itemsorter.command.list"), msg.cmd_help_list.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " list ");
				sendBasePermhs(s, s.hasPermission("itemsorter.command.base"), msg.cmd_help_base.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " base ");
				sendBasePermhs(s, s.hasPermission("itemsorter.command.filters"), msg.cmd_help_filters.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " filters ");
				sendBasePermhs(s, s.hasPermission("itemsorter.command.deposits"), msg.cmd_help_deposits.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " deposits ");
				sendBasePermhs(s, s.hasPermission("itemsorter.command.glow") && Main.versionHandler.isGlowAvailable(), msg.cmd_help_glow.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " glow ");
				sendBasePermhs(s, s.hasPermission("itemsorter.command.autosign"), msg.cmd_help_autosign.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " autosign ");
				sendBasePermhs(s, s.hasPermission("itemsorter.command.reload"), msg.cmd_help_reload.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " reload");
				sendBasePermhs(s, s.hasPermission("itemsorter.command.setowner"), msg.cmd_help_setOwner.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " setOwner ");
				sendBase(s, msg.cmd_help_space2);
				sendBase(s, msg.cmd_help_footer);
			}else{
				s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
			}
		}else if(args[0].equalsIgnoreCase("reload")){
			if(s.hasPermission("itemsorter.command.reload")){
				Main.configManager.loadConfigs();
				Main.loadConfigs(s);
				Main.cachedItems = new CachedItems();
				s.sendMessage(Main.prefix + fixColors(msg.cmd_reload_complete));
			}else{
				s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
			}
		}else if(args[0].equalsIgnoreCase("list")){
			if(s.hasPermission("itemsorter.command.list")){
				int page = 0;
				String filter = "";
				if(args.length > 1){
					for(int i = 1; i < args.length; i++){
						if(args[i].toLowerCase().startsWith("p:")){
							try{
								page = Integer.parseInt(args[i].split(":")[1]);
							}catch(Exception ignored){}
						}else{
							filter = args[i];
						}
						if(page != 0 && !filter.equals("")){
							break;
						}
					}
				}
				ArrayList<System> systems = new ArrayList<>();
				if(!filter.equals("")){
					for(System sys : Main.bases){
						if(sys.owner.equalsIgnoreCase(filter)){
							systems.add(sys);
						}
					}
				}else{
					systems = Main.bases;
				}
				if(systems.size() != 0){
					if(page <= 0){
						page = 1;
					}
					int max = ((systems.size() - 1) / 10) + 1;
					if(max < page) {
						page = max;
					}
					sendBase(s, msg.cmd_list_header);
					if(!filter.equals("")){
						TextComponent optionChat = new TextComponent(fixColors(msg.cmd_list_option).replaceAll("%option%", filter));
						HoverEvent optionhover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fixColors(msg.cmd_list_hoverOption))});
						ClickEvent optionclick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " list");
						optionChat.setClickEvent(optionclick);
						optionChat.setHoverEvent(optionhover);
						send(s, optionChat);
					}
					for(int i = (page-1) * 10; i < systems.size() && i < ((page-1) * 10) + 10; i++){
						TextComponent sourceChat = new TextComponent(fixColors(msg.cmd_list_prefix));
						TextComponent baseChat = new TextComponent(fixColors(msg.cmd_list_name).replaceAll("%name%", systems.get(i).name));
						HoverEvent basehover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fixColors(msg.cmd_list_hoverName))});
						ClickEvent baseclick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " base " + systems.get(i).name);
						baseChat.setClickEvent(baseclick);
						baseChat.setHoverEvent(basehover);
						TextComponent ownerChat = new TextComponent(fixColors(msg.cmd_list_owner).replaceAll("%name%", systems.get(i).owner));
						HoverEvent ownerhover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fixColors(msg.cmd_list_hoverOwner))});
						ClickEvent ownerclick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " list " + systems.get(i).owner);
						ownerChat.setHoverEvent(ownerhover);
						ownerChat.setClickEvent(ownerclick);
						sourceChat.addExtra(baseChat);
						sourceChat.addExtra(ownerChat);
						send(s, sourceChat);
					}
					sendNav(s, page, 1, max, "/" + label + " list " + filter + " p:%num%");
				}else{
					s.sendMessage(Main.prefix + fixColors(msg.cmd_list_listEmpty));
				}
			}else{
				s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
			}
		}else if(args[0].equalsIgnoreCase("base")){
			if(s.hasPermission("itemsorter.command.base")){
				if(args.length > 1){
					System system = null;
					for(System sys : Main.bases){
						if(sys.name.equals(args[1])){
							system = sys;
							break;
						}
					}
					if(system != null){
						if(!s.hasPermission("itemsorter.command.base.other")){
							if(s instanceof  Player){
								Player p = (Player)s;
								if(!system.owner.equals(p.getName())){
									if(!system.isTrust(p) || !s.hasPermission("itemsorter.command.base.trust")){
										s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
										return true;
									}
								}
							}
						}
						boolean displayLoc = false;
						if(s instanceof Player){
							if(!s.hasPermission("itemsorter.command.base.coodinates.other")){
								if(system.trusts.contains(((Player) s).getName())){
									if(s.hasPermission("itemsorter.command.base.coodinates.trust")){
										displayLoc = true;
									}
								}else if(system.owner.equals(((Player)s).getName())){
									if(s.hasPermission("itemsorter.command.base.coodinates")){
										displayLoc = true;
									}
								}
							}else{
								displayLoc =  true;
							}
						}
						sendBase(s, msg.cmd_base_header);
						sendBaseNofix(s, fixColors(msg.cmd_base_name).replaceAll("%name%", system.name));
						sendBasehc(s, msg.cmd_base_owner.replaceAll("%owner%", system.owner), msg.cmd_base_ownerhover, "/" + label + " list " + system.owner);
						Location loc = system.sign;
						String msgLoc = msg.cmd_base_location.replaceAll("%world%", loc.getWorld().getName())
								.replaceAll("%x%", loc.getBlockX() + "")
								.replaceAll("%y%", loc.getBlockY() + "")
								.replaceAll("%z%", loc.getBlockZ() + "");
						sendBasePermhs(s, displayLoc, msgLoc, msg.cmd_base_locationHover, loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
						boolean canGlow = false;
						if(s instanceof Player){
							if(!s.hasPermission("itemsorter.command.glow.other")){
								if(s.hasPermission("itemsorter.command.glow.trust") && system.trusts.contains(((Player) s).getName())){
									canGlow = true;
								}else if(s.hasPermission("itemsorter.command.glow") && system.owner.equals(((Player)s).getName())){
									canGlow = true;
								}
							}else{
								canGlow =  true;
							}
						}
						if(Main.versionHandler.isGlowAvailable() && canGlow){
							TextComponent sourceAction = new TextComponent(fixColors(msg.cmd_base_actions));
							TextComponent glowAction = new TextComponent(fixColors(msg.cmd_base_actionGlow));
							HoverEvent glowActionh = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fixColors(msg.cmd_base_actionGlowHover))});
							ClickEvent glowActionc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " glow base " + system.name + " false");
							glowAction.setHoverEvent(glowActionh);
							glowAction.setClickEvent(glowActionc);
							sourceAction.addExtra(glowAction);
							send(s, sourceAction);
						}
						String trust = msg.cmd_base_trusts;
						if(system.trusts.size() > 0){
							trust += msg.cmd_base_trustShem.replaceAll("%trust%", system.trusts.get(0));
							for(int i = 1; i < system.trusts.size(); i++){
								trust += msg.cmd_base_trustSeparator + msg.cmd_base_trustShem.replaceAll("%trust%", system.trusts.get(i));
							}
						}
						sendBase(s, trust);
						sendBasePermhs(s, true, fixColors(msg.cmd_base_filters).replaceAll("%num%", system.filters.size() + ""), msg.cmd_base_filtersHover, "/" + label + " filters " + system.name + " ");
						sendBasehc(s, fixColors(msg.cmd_base_deposits).replaceAll("%num%", system.deposits.size() + ""), msg.cmd_base_depositsHover, "/" + label + " deposits " + system.name);
					}else{
						s.sendMessage(Main.prefix + fixColors(msg.cmd_base_nameNotExist).replaceAll("%base%", args[1]));
					}
				}else{
					s.sendMessage(Main.prefix + fixColors(msg.cmd_base_noName));
				}
			}else{
				s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
			}
		}else if(args[0].equalsIgnoreCase("filters")){
			if(s.hasPermission("itemsorter.command.filters")){
				if(args.length > 1){
					System system = null;
					for(System sys : Main.bases){
						if(sys.name.equals(args[1])){
							system = sys;
							break;
						}
					}
					if(system != null){
						if(!s.hasPermission("itemsorter.command.filters.other")){
							if(s instanceof  Player){
								Player p = (Player)s;
								if(!system.owner.equals(p.getName())){
									if(!system.isTrust(p) || !s.hasPermission("itemsorter.command.filters.trust")){
										s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
										return true;
									}
								}
							}
						}
						int page = 0;
						String filter = "";
						if(args.length > 2){
							for(int i = 2; i < args.length; i++){
								if(args[i].toLowerCase().startsWith("p:")){
									try{
										page = Integer.parseInt(args[i].split(":")[1]);
									}catch(Exception ignored){}
								}else{
									filter = args[i];
								}
								if(page != 0 && !filter.equals("")){
									break;
								}
							}
						}
						if(!filter.equalsIgnoreCase("Trash")){
							if(filter.equalsIgnoreCase("item")){
								if(s instanceof Player){
									try{
										filter = ((Player)s).getInventory().getItemInHand().getType().toString();
									}catch(Exception ignored){}
								}
							}else{
								try{
									filter = Material.valueOf(filter.toUpperCase()).toString();
								}catch(Exception ex){
									filter = "Trash";
								}
							}
						}else{
							filter = "Trash";
						}
						ArrayList<Filter> filters = new ArrayList<>();
						for(Filter fil : system.filters){
							if((filter.equals("Trash") && fil.isTrash) || (!filter.equals("Trash") && fil.materials.containsKey(Material.valueOf(filter)))){
								filters.add(fil);
							}
						}
						if(filters.size() != 0){
							boolean displayLoc = false;
							if(s instanceof Player){
								if(!s.hasPermission("itemsorter.command.filters.coodinates.other")){
									if(system.trusts.contains(((Player) s).getName())){
										if(s.hasPermission("itemsorter.command.filters.coodinates.trust")){
											displayLoc = true;
										}
									}else if(system.owner.equals(((Player)s).getName())){
										if(s.hasPermission("itemsorter.command.filters.coodinates")){
											displayLoc = true;
										}
									}
								}else{
									displayLoc =  true;
								}
							}
							if(page <= 0){
								page = 1;
							}
							int max = ((filters.size() - 1) / 10) + 1;
							if(max < page) {
								page = max;
							}
							Filter[] sortedFilters = new Filter[filters.size()];
							sortedFilters = filters.toArray(sortedFilters);
							Material mat = null;
							if(filter.equals("Trash")){
								Utilities.sort(sortedFilters, (a, b) -> {return ((Filter)a).trashPriority > ((Filter)b).trashPriority;});
							}else{
								mat = Material.valueOf(filter);
								Material finalMat = mat;
								Utilities.sort(sortedFilters, (a, b) -> {return ((Filter)a).materials.get(finalMat) > ((Filter)b).materials.get(finalMat);});
							}
							sendBase(s, msg.cmd_filters_header);
							if(filter.equals("Trash")){
								sendBase(s, msg.cmd_filters_option.replaceAll("%option%", filter));
							}else{
								sendBasehc(s, msg.cmd_filters_option.replaceAll("%option%", filter), msg.cmd_filters_hoverOption, "/" + label + " filters " + system.name + " false");
							}
							boolean canGlow = false;
							if(s instanceof Player){
								if(!s.hasPermission("itemsorter.command.glow.other")){
									if(system.trusts.contains(((Player) s).getName())){
										if(s.hasPermission("itemsorter.command.glow.trust")){
											canGlow = true;
										}
									}else if(system.owner.equals(((Player)s).getName())){
										if(s.hasPermission("itemsorter.command.glow")){
											canGlow = true;
										}
									}
								}else{
									canGlow =  true;
								}
							}
							if(Main.versionHandler.isGlowAvailable() && canGlow){
								TextComponent sourceAction = new TextComponent(fixColors(msg.cmd_filters_actions));
								TextComponent glowAction = new TextComponent(fixColors(msg.cmd_filters_actionGlow));
								HoverEvent glowActionh = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fixColors(msg.cmd_filters_actionGlowAllHover))});
								ClickEvent glowActionc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " glow filters " + system.name + " " + filter + " false");
								glowAction.setHoverEvent(glowActionh);
								glowAction.setClickEvent(glowActionc);
								sourceAction.addExtra(glowAction);
								send(s, sourceAction);
							}
							for(int i = (page-1) * 10; i < sortedFilters.length && i < ((page-1) * 10) + 10; i++){
								TextComponent sourceAction = new TextComponent(fixColors(msg.cmd_filters_prefix) + fixColors(msg.cmd_filters_priority).replaceAll("%num%", (sortedFilters[i].isTrash ? sortedFilters[i].trashPriority : sortedFilters[i].materials.get(mat)) + ""));
								if(displayLoc){
									HoverEvent hoverloc = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fixColors(msg.cmd_filters_locationHover))});
									Location loc = sortedFilters[i].sign;
									ClickEvent clickloc = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
									TextComponent location = new TextComponent(fixColors(msg.cmd_filters_location).replaceAll("%x%", loc.getBlockX() + "").replaceAll("%y%", loc.getBlockY() + "").replaceAll("%z%", loc.getBlockZ() + ""));
									location.setHoverEvent(hoverloc);
									location.setClickEvent(clickloc);
									sourceAction.addExtra(location);
								}
								if(Main.versionHandler.isGlowAvailable() && canGlow){
									TextComponent glowAction = new TextComponent(fixColors(msg.cmd_filters_actionGlow));
									HoverEvent glowActionh = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fixColors(msg.cmd_filters_actionGlowHover))});
									ClickEvent glowActionc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " glow filters " + system.name + " " + filter + " " + (sortedFilters[i].isTrash ? sortedFilters[i].trashPriority : sortedFilters[i].materials.get(mat)) + " false");
									glowAction.setHoverEvent(glowActionh);
									glowAction.setClickEvent(glowActionc);
									sourceAction.addExtra(glowAction);
								}
								send(s, sourceAction);
							}
							sendNav(s, page, 1, max, "/" + label + " filters " + system.name + " " + filter + " p:%num%");
						}else{
							s.sendMessage(Main.prefix + fixColors(msg.cmd_filters_optionEmpty));
						}
					}else{
						s.sendMessage(Main.prefix + fixColors(msg.cmd_filters_baseNotExist).replaceAll("%base%", args[1]));
					}
				}else{
					s.sendMessage(Main.prefix + fixColors(msg.cmd_filters_noName));
				}
			}else{
				s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
			}
		}else if(args[0].equalsIgnoreCase("deposits")) {
			if(s.hasPermission("itemsorter.command.deposits")){
				if(args.length > 1){
					System system = null;
					for(System sys : Main.bases){
						if(sys.name.equals(args[1])){
							system = sys;
							break;
						}
					}
					if(system != null){
						if(!s.hasPermission("itemsorter.command.deposits.other")){
							if(s instanceof  Player){
								Player p = (Player)s;
								if(!system.owner.equals(p.getName())){
									if(!system.isTrust(p) || !s.hasPermission("itemsorter.command.deposits.trust")){
										s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
										return true;
									}
								}
							}
						}
						int page = 0;
						if(args.length > 2){
							for(int i = 2; i < args.length; i++){
								if(args[i].toLowerCase().startsWith("p:")){
									try{
										page = Integer.parseInt(args[i].split(":")[1]);
									}catch(Exception ignored){}
								}
								if(page != 0){
									break;
								}
							}
						}
						ArrayList<Deposit> deposits = system.deposits;
						if(deposits.size() != 0){
							if(page <= 0){
								page = 1;
							}
							int max = ((deposits.size() - 1) / 10) + 1;
							if(max < page) {
								page = max;
							}
							sendBase(s, msg.cmd_deposits_header);
							boolean canGlow = false;
							if(s instanceof Player){
								if(!s.hasPermission("itemsorter.command.glow.other")){
									if(system.trusts.contains(((Player) s).getName())){
										if(s.hasPermission("itemsorter.command.glow.trust")){
											canGlow = true;
										}
									}else if(system.owner.equals(((Player)s).getName())){
										if(s.hasPermission("itemsorter.command.glow")){
											canGlow = true;
										}
									}
								}else{
									canGlow =  true;
								}
							}
							if(Main.versionHandler.isGlowAvailable() && canGlow){
								TextComponent sourceAction = new TextComponent(fixColors(msg.cmd_deposits_actions));
								TextComponent glowAction = new TextComponent(fixColors(msg.cmd_deposits_actionGlow));
								HoverEvent glowActionh = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fixColors(msg.cmd_deposits_actionGlowAllHover))});
								ClickEvent glowActionc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " glow deposits " + system.name + " false");
								glowAction.setHoverEvent(glowActionh);
								glowAction.setClickEvent(glowActionc);
								sourceAction.addExtra(glowAction);
								send(s, sourceAction);
							}
							for(int i = (page-1) * 10; i < deposits.size() && i < ((page-1) * 10) + 10; i++){
								TextComponent sourceAction = new TextComponent(fixColors(msg.cmd_deposits_prefix));
								HoverEvent hoverloc = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fixColors(msg.cmd_deposits_locationHover))});
								Location loc = deposits.get(i).sign;
								ClickEvent clickloc = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
								TextComponent location = new TextComponent(fixColors(msg.cmd_deposits_location).replaceAll("%x%", loc.getBlockX() + "").replaceAll("%y%", loc.getBlockY() + "").replaceAll("%z%", loc.getBlockZ() + ""));
								location.setHoverEvent(hoverloc);
								location.setClickEvent(clickloc);
								sourceAction.addExtra(location);
								if(Main.versionHandler.isGlowAvailable() && canGlow){
									TextComponent glowAction = new TextComponent(fixColors(msg.cmd_deposits_actionGlow));
									HoverEvent glowActionh = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fixColors(msg.cmd_deposits_actionGlowHover))});
									ClickEvent glowActionc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " glow deposits " + system.name + " " + i + " false");
									glowAction.setHoverEvent(glowActionh);
									glowAction.setClickEvent(glowActionc);
									sourceAction.addExtra(glowAction);
								}
								send(s, sourceAction);
							}
							sendNav(s, page, 1, max, "/" + label + " deposits " + system.name + " p:%num%");
						}else{
							s.sendMessage(Main.prefix + fixColors(msg.cmd_deposits_empty));
						}
					}else{
						s.sendMessage(Main.prefix + fixColors(msg.cmd_deposits_baseNotExist).replaceAll("%base%", args[1]));
					}
				}else{
					s.sendMessage(Main.prefix + fixColors(msg.cmd_deposits_noName));
				}
			}else{
				s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
			}
		}else if(args[0].equalsIgnoreCase("glow")) {
			if (s.hasPermission("itemsorter.command.glow")) {
				if (Main.versionHandler.isGlowAvailable()) {
					if (s instanceof Player) {
						Player p = (Player) s;
						if (args.length != 1) {
							if (args.length != 2) {
								System system = null;
								for (System sys : Main.bases) {
									if (sys.name.equals(args[2])) {
										system = sys;
										break;
									}
								}
								if (system != null) {
									if (system.baseLoc.getWorld().getName().equals(p.getLocation().getWorld().getName())) {
										if (!s.hasPermission("itemsorter.command.glow.other")) {
											if (system.owner.equals(p.getName())) {
												if (!s.hasPermission("itemsorter.command.glow")) {
													s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
													return true;
												}
											} else if (system.trusts.contains(p.getName())) {
												if (!s.hasPermission("itemsorter.command.glow.trust")) {
													s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_notOwner));
													return true;
												}
											} else {
												s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_notTrust));
												return true;
											}
										}
										if (args[1].equalsIgnoreCase("base")) {
											addGlow(p, system.baseLoc.clone().add(0.5d, 0d, 0.5d), true, 0);
											if ((!(args.length > 3) || !args[3].equalsIgnoreCase("false"))) {
												s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_complete));
											}
										} else if (args[1].equalsIgnoreCase("filters")) {
											if (args.length != 3) {
												String filter = args[3];
												if (!filter.equalsIgnoreCase("Trash")) {
													try {
														filter = Material.valueOf(filter.toUpperCase()).toString();
													} catch (Exception ex) {
														filter = "Trash";
													}
												} else {
													filter = "Trash";
												}
												ArrayList<Filter> filters = new ArrayList<>();
												for (Filter fil : system.filters) {
													if ((filter.equals("Trash") && fil.isTrash) || (!filter.equals("Trash") && fil.materials.keySet().contains(Material.valueOf(filter)))) {
														filters.add(fil);
													}
												}
												if (args.length > 4 && !args[4].equalsIgnoreCase("false")) {
													int id = -1;
													try {
														id = Integer.parseInt(args[4]);
														if (id < 0) {
															id = -1;
														}
													} catch (Exception ex) {
														id = -1;
													}
													if (id != -1) {
														Filter fil = null;
														for (Filter fils : filters) {
															int o = 0;
															if (fils.isTrash) {
																o = fils.trashPriority;
															} else {
																o = fils.materials.getOrDefault(Material.valueOf(filter), -1);
															}
															if (o != -1) {
																if (id == o) {
																	fil = fils;
																	break;
																}
															}
														}
														if (fil == null) {
															s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_noBlocsFound));
															return true;
														} else {
															addGlow(p, fil.loc.clone().add(0.5d, 0d, 0.5d), true, 0);
															if ((!(args.length > 5) || !args[5].equalsIgnoreCase("false"))) {
																s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_complete));
															}
															return true;
														}
													} else {
														s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_noBlocsFound));
														return true;
													}
												}
												ArrayList<Location> locs = new ArrayList<>();
												for (Filter fil : filters) {
													locs.add(fil.loc.clone().add(0.5d, 0d, 0.5d));
												}
												addGlow(p, locs);
												if ((!(args.length > 4) || !args[4].equalsIgnoreCase("false"))) {
													s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_complete));
												}
											} else {
												s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_noOptionFilter));
											}
										} else if (args[1].equalsIgnoreCase("deposits")) {
											if (args.length > 3 && !args[3].equalsIgnoreCase("false")) {
												int id = -1;
												try {
													id = Integer.parseInt(args[3]);
													if (id < 0) {
														id = -1;
													}
												} catch (Exception ex) {
													id = -1;
												}
												if (id != -1) {
													if (system.deposits.size() <= id) {
														s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_noBlocsFound));
													} else {
														addGlow(p, system.deposits.get(id).loc.clone().add(0.5d, 0d, 0.5d), true, 0);
														if ((!(args.length > 4) || !args[4].equalsIgnoreCase("false"))) {
															s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_complete));
														}
													}
													return true;
												} else {
													s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_noBlocsFound));
													return true;
												}
											}
											ArrayList<Location> locs = new ArrayList<>();
											for (Deposit dep : system.deposits) {
												locs.add(dep.loc.clone().add(0.5d, 0d, 0.5d));
											}
											addGlow(p, locs);
											if ((!(args.length > 3) || !args[3].equalsIgnoreCase("false"))) {
												s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_complete));
											}
										} else {
											s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_noOption));
										}
									} else {
										s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_notInSameWorld));
									}
								} else {
									s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_baseNotExist).replaceAll("%base%", args[2]));
								}
							} else {
								s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_noBase));
							}
						} else {
							s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_noOption));
						}
					} else {
						s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_notPlayer));
					}
				} else {
					s.sendMessage(Main.prefix + fixColors(msg.cmd_glow_lowerVersion));
				}
			} else {
				s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
			}
		}else if(args[0].equalsIgnoreCase("autosign")) {
			if(s instanceof Player){
				if(s.hasPermission("itemsorter.command.autosign")){
					if(args.length > 1){
						if(args[1].equalsIgnoreCase("deposit") || args[1].equalsIgnoreCase("filter")){
							if(args.length > 2){
								System system = null;
								for (System sys : Main.bases) {
									if (sys.name.equals(args[2])) {
										system = sys;
										break;
									}
								}
								if(system != null){
									if(!s.hasPermission("itemsorter.admin")){
										if(!system.owner.equals(((Player)s).getName())){
											if(!system.isTrust((Player)s)){
												s.sendMessage(Main.prefix + fixColors(msg.cmd_autosign_notTrust));
												return true;
											}
										}
									}
									if(args[1].equalsIgnoreCase("deposit")){
										Event.autofilters.remove((Player)s);
										Event.autodeposits.put((Player)s, system);
									}else{
										Event.autodeposits.remove((Player)s);
										Event.autofilters.put((Player)s, system);
									}
									s.sendMessage(Main.prefix + fixColors(msg.cmd_autosign_complete).replaceAll("%cmd%", label));
								}else{
									s.sendMessage(Main.prefix + fixColors(msg.cmd_autosign_baseNotExist).replaceAll("%base%", args[2]));
								}
							}else{
								s.sendMessage(Main.prefix + fixColors(msg.cmd_autosign_noBase));
							}
						}else if(args[1].equalsIgnoreCase("stop")) {
							Event.autofilters.remove((Player)s);
							Event.autodeposits.remove((Player)s);
							s.sendMessage(Main.prefix + fixColors(msg.cmd_autosign_stop));
						}else{
							s.sendMessage(Main.prefix + fixColors(msg.cmd_autosign_noOption));
						}
					}else{
						s.sendMessage(Main.prefix + fixColors(msg.cmd_autosign_noOption));
					}
				}else{
					s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
				}
			}else{
				s.sendMessage(Main.prefix + fixColors(msg.cmd_autosign_notPlayer));
			}
		}else if(args[0].equalsIgnoreCase("setOwner")) {
			if(s.hasPermission("itemsorter.command.setowner")){
				if(args.length > 1){
					if(args.length > 2) {
						System system = null;
						for (System sys : Main.bases) {
							if (sys.name.equals(args[1])) {
								system = sys;
								break;
							}
						}
						if(system != null){
							if(!args[2].equals(system.owner)){
								system.owner = args[2];
								try{
									Sign sign = (Sign) system.sign.getBlock().getState();
									sign.setLine(0, Main.configManager.messages.sign_prefix.replace("&", "§"));
									sign.setLine(1, "§b" + system.name);
									sign.setLine(2, "§b= BASE =");
									sign.setLine(3, "§7(" + system.owner + ")");
									sign.update();
								}catch(Exception ignored){}
								for(Filter fil : system.filters){
									try{
										Sign signf = (Sign) fil.sign.getBlock().getState();
										signf.setLine(0, Main.configManager.messages.sign_prefix.replace("&", "§"));
										signf.setLine(1, "§b" + system.name);
										signf.setLine(2, "§b- Filter -");
										signf.setLine(3, "§7(" + system.owner + ")");
										signf.update();
									}catch(Exception ignored){}
								}
								for(Deposit depo : system.deposits){
									try{
										Sign signd = (Sign) depo.sign.getBlock().getState();
										signd.setLine(0, Main.configManager.messages.sign_prefix.replace("&", "§"));
										signd.setLine(1, "§b" + system.name);
										signd.setLine(2, "§b- Deposit -");
										signd.setLine(3, "§7(" + system.owner + ")");
										signd.update();
									}catch(Exception ignored){}
								}
								system.save();
							}
							s.sendMessage(Main.prefix + fixColors(msg.cmd_setOwner_complete));
						}else{
							s.sendMessage(Main.prefix + fixColors(msg.cmd_setOwner_baseNotExist).replaceAll("%base%", args[1]));
						}
					}else{
						s.sendMessage(Main.prefix + fixColors(msg.cmd_setOwner_noPlayer));
					}
				}else{
					s.sendMessage(Main.prefix + fixColors(msg.cmd_setOwner_noBase));
				}
			}else{
				s.sendMessage(Main.prefix + fixColors(msg.cmd_dontHavePermission));
			}
		}else{
			s.sendMessage(Main.prefix + fixColors(msg.cmd_commandNotFound));
		}
		return true;
	}

	public void glow(Location loc, Player p, int id){
		try{
			Class packetSpawnEntityClass = Class.forName("net.minecraft.server." + Main.version + ".PacketPlayOutSpawnEntity");
			Object packetSpawnEntity = Main.versionHandler.getpacketSpawnEntityFallingBlock(packetSpawnEntityClass, loc, id);
			sendPacket(p, packetSpawnEntity);
			ArrayList<Object> list = new ArrayList<>();
			Class dataWatcherClass = Class.forName("net.minecraft.server." + Main.version + ".DataWatcher");
			Class dataWatcher_ItemClass = dataWatcherClass.getClasses()[0];
			Class dataWatcherObjectClass = Class.forName("net.minecraft.server." + Main.version + ".DataWatcherObject");
			Class DataWatcherRegistryClass = Class.forName("net.minecraft.server." + Main.version + ".DataWatcherRegistry");
			list.add(dataWatcher_ItemClass.getConstructors()[0].newInstance(dataWatcherObjectClass.getConstructors()[0].newInstance(0, DataWatcherRegistryClass.getDeclaredField("a").get(null)), (byte) 64));
			list.add(dataWatcher_ItemClass.getConstructors()[0].newInstance(dataWatcherObjectClass.getConstructors()[0].newInstance(5, DataWatcherRegistryClass.getDeclaredField(Main.versionHandler.getDataWRFBoolean()).get(null)), true));
			Class packetEntityMetadataClass = Class.forName("net.minecraft.server." + Main.version + ".PacketPlayOutEntityMetadata");
			Field a = packetEntityMetadataClass.getDeclaredField("a");
			a.setAccessible(true);
			Field b = packetEntityMetadataClass.getDeclaredField("b");
			b.setAccessible(true);
			Object packetEntityMetadata = packetEntityMetadataClass.newInstance();
			a.set(packetEntityMetadata, id);
			b.set(packetEntityMetadata, list);
			sendPacket(p, packetEntityMetadata);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void removeGlow(Player p, int id){
		try{
			Class packetEntityDestroyClass = Class.forName("net.minecraft.server." + Main.version + ".PacketPlayOutEntityDestroy");
			Field f = packetEntityDestroyClass.getDeclaredField("a");
			f.setAccessible(true);
			Object packet = packetEntityDestroyClass.newInstance();
			int ints[] = new int[1];
			ints[0] = id;
			f.set(packet, ints);
			sendPacket(p, packet);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public int addGlow(Player p, Location loc, boolean remove, int givenId){
		if(remove && glowMap.containsKey(p)){
			glowTasks.get(p).cancel();
			glowTasks.remove(p);
			ArrayList<String> list = glowMap.get(p);
			for(String id : list){
				removeGlow(p, Integer.parseInt(id));
			}
			glowMap.remove(p);
		}
		int id = 200;
		if(!remove){
			id = givenId;
		}else{
			ArrayList<String> list = new ArrayList<>();
			list.add(id + "");
			glowMap.put(p, list);
		}
		glow(loc, p, id);
		if(remove){
			int finalId = id;
			BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
				@Override
				public void run() {
					removeGlow(p, finalId);
					glowMap.remove(p);
					glowTasks.remove(p);
				}
			}, 20 * 10);
			glowTasks.put(p, task);
		}
		return id;
	}

	public void addGlow(Player p, List<Location> locs){
		if(glowMap.containsKey(p)){
			glowTasks.get(p).cancel();
			glowTasks.remove(p);
			ArrayList<String> list = glowMap.get(p);
			for(String id : list){
				removeGlow(p, Integer.parseInt(id));
			}
			glowMap.remove(p);
		}
		ArrayList<String> ids = new ArrayList<>();
		int id = 200;
		for(Location loc : locs){
			addGlow(p, loc, false, id);
			ids.add(id + "");
			id++;
		}
		glowMap.put(p, ids);
		BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				for(String id : ids){
					removeGlow(p, Integer.parseInt(id));
				}
				glowMap.remove(p);
				glowTasks.remove(p);
			}
		}, 20 * 10);
		glowTasks.put(p, task);
	}

	public void sendPacket(Player p, Object packet) {
		try {
			Method handle = p.getClass().getMethod("getHandle");
			Object craftPlayer = handle.invoke(p);
			Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
			Class<?> packetClass = Class.forName("net.minecraft.server." + Main.version + ".Packet");
			playerConnection.getClass().getMethod("sendPacket", packetClass).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args){
		HashMap<String, String> commands = new HashMap<>();
		commands.put("help", "itemsorter.command.help");
		commands.put("list", "itemsorter.command.list");
		commands.put("base", "itemsorter.command.base");
		commands.put("filters", "itemsorter.command.filters");
		commands.put("deposits", "itemsorter.command.deposits");
		commands.put("glow", "itemsorter.command.glow");
		commands.put("autosign", "itemsorter.command.autosign");
		commands.put("reload", "itemsorter.command.reload");
		commands.put("setOwner", "itemsorter.command.setOwner");
		//  args.lenth == 4
		if(args.length == 4 && args[0].equalsIgnoreCase("filters") && s.hasPermission(commands.get("filters"))){
			ArrayList<String> page = new ArrayList<>();
			page.add("p:");
			return Utilities.searchforsimilarity(args[3], page);
		}else if(args.length == 4 && args[0].equalsIgnoreCase("glow") && s.hasPermission(commands.get("glow")) && Main.versionHandler.isGlowAvailable()){
			if(args[1].equalsIgnoreCase("filters")){
				ArrayList<String> filters = new ArrayList<>();
				filters.add("Trash");
				for(Material mat : Material.values()){
					filters.add(mat.name());
				}
				return Utilities.searchforsimilarity(args[3], filters);
			}
		}

		//  args.lenth == 3
		if(args.length == 3 && args[0].equalsIgnoreCase("list") && s.hasPermission(commands.get("list"))){
			ArrayList<String> page = new ArrayList<>();
			page.add("p:");
			return Utilities.searchforsimilarity(args[2], page);
		}else if(args.length == 3 && args[0].equalsIgnoreCase("filters") && s.hasPermission(commands.get("filters"))){
			ArrayList<String> filters = new ArrayList<>();
			filters.add("item");
			filters.add("Trash");
			for(Material mat : Material.values()){
				filters.add(mat.name());
			}
			return Utilities.searchforsimilarity(args[2], filters);
		}else if(args.length == 3 && args[0].equalsIgnoreCase("deposits") && s.hasPermission(commands.get("deposits"))){
			ArrayList<String> page = new ArrayList<>();
			page.add("p:");
			return Utilities.searchforsimilarity(args[2], page);
		}else if(args.length == 3 && args[0].equalsIgnoreCase("glow") && s.hasPermission(commands.get("glow")) && Main.versionHandler.isGlowAvailable()){
			ArrayList<String> bases = new ArrayList<>();
			for(System sys : Main.bases){
				bases.add(sys.name);
			}
			return Utilities.searchforsimilarity(args[2], bases);
		}else if(args.length == 3 && args[0].equalsIgnoreCase("autosign") && s.hasPermission(commands.get("autosign"))){
			ArrayList<String> bases = new ArrayList<>();
			for(System sys : Main.bases){
				bases.add(sys.name);
			}
			return Utilities.searchforsimilarity(args[2], bases);
		}else if(args.length == 3 && args[0].equalsIgnoreCase("setOwner") && s.hasPermission(commands.get("setOwner"))){
			ArrayList<String> players = new ArrayList<>();
			for(Player p : Bukkit.getOnlinePlayers()){
				players.add(p.getName());
			}
			return Utilities.searchforsimilarity(args[2], players);
		}

		//  args.lenth == 2
		if(args.length == 2 && args[0].equalsIgnoreCase("list") && s.hasPermission(commands.get("list"))){
			ArrayList<String> owners = new ArrayList<>();
			for(System sys : Main.bases){
				owners.add(sys.owner);
			}
			return Utilities.searchforsimilarity(args[1], owners);
		}else if(args.length == 2 && args[0].equalsIgnoreCase("base") && s.hasPermission(commands.get("base"))){
			ArrayList<String> bases = new ArrayList<>();
			for(System sys : Main.bases){
				bases.add(sys.name);
			}
			return Utilities.searchforsimilarity(args[1], bases);
		}else if(args.length == 2 && args[0].equalsIgnoreCase("filters") && s.hasPermission(commands.get("filters"))){
			ArrayList<String> bases = new ArrayList<>();
			for(System sys : Main.bases){
				bases.add(sys.name);
			}
			return Utilities.searchforsimilarity(args[1], bases);
		}else if(args.length == 2 && args[0].equalsIgnoreCase("deposits") && s.hasPermission(commands.get("deposits"))){
			ArrayList<String> bases = new ArrayList<>();
			for(System sys : Main.bases){
				bases.add(sys.name);
			}
			return Utilities.searchforsimilarity(args[1], bases);
		}else if(args.length == 2 && args[0].equalsIgnoreCase("glow") && s.hasPermission(commands.get("glow")) && Main.versionHandler.isGlowAvailable()){
			ArrayList<String> strings = new ArrayList<>();
			strings.add("filters");
			strings.add("base");
			strings.add("deposits");
			return Utilities.searchforsimilarity(args[1], strings);
		}else if(args.length == 2 && args[0].equalsIgnoreCase("autosign") && s.hasPermission(commands.get("autosign"))){
			ArrayList<String> strings = new ArrayList<>();
			strings.add("filter");
			strings.add("deposit");
			strings.add("stop");
			return Utilities.searchforsimilarity(args[1], strings);
		}else if(args.length == 2 && args[0].equalsIgnoreCase("setOwner") && s.hasPermission(commands.get("setOwner"))){
			ArrayList<String> bases = new ArrayList<>();
			for(System sys : Main.bases){
				bases.add(sys.name);
			}
			return Utilities.searchforsimilarity(args[1], bases);
		}

		//  args.lenth == 1
		if(args.length == 1){
			ArrayList<String> list = new ArrayList<>();
			for(String key : commands.keySet()){
				if(s.hasPermission(commands.get(key))){
					list.add(key);
				}
			}
			return Utilities.searchforsimilarity(args[0], list);
		}else{
			return null;
		}
	}

	public void sendBase(CommandSender s, String st){
		if(!st.equals("")){
			send(s, new TextComponent(fixColors(st)));
		}
	}

	public void sendBaseNofix(CommandSender s, String st){
		if(!st.equals("")){
			send(s, new TextComponent(fixColors(st)));
		}
	}

	public void sendBasePermhs(CommandSender s, boolean condition, String st, String hover, String suggest) {
		try {
			if (!st.equals("") && condition) {
				HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fixColors(hover))});
				ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest);
				TextComponent chatComponent = new TextComponent(fixColors(st));
				chatComponent.setHoverEvent(hoverEvent);
				chatComponent.setClickEvent(clickEvent);
				send(s, chatComponent);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void sendBasehc(CommandSender s, String st, String hover, String cmd) {
		try {
			if (!st.equals("")) {
				HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(fixColors(hover))});
				ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd);
				TextComponent chatComponent = new TextComponent(fixColors(st));
				chatComponent.setHoverEvent(hoverEvent);
				chatComponent.setClickEvent(clickEvent);
				send(s, chatComponent);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void sendNav(CommandSender s, int page, int min, int max, String toRun){
		Messages msg = Main.configManager.messages;
		TextComponent source = new TextComponent(fixColors(msg.cmd_nav_before));
		if(page > min){
			TextComponent prev = new TextComponent(fixColors(msg.cmd_nav_previous));
			HoverEvent prevh = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent((page - 1) + "")});
			ClickEvent prevc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, toRun.replaceAll("%num%", (page - 1) + ""));
			prev.setHoverEvent(prevh);
			prev.setClickEvent(prevc);
			source.addExtra(prev);
		}
		TextComponent count = new TextComponent(fixColors(msg.cmd_nav_pageCount).replaceAll("%page%", page + "").replaceAll("%max%", max + ""));
		source.addExtra(count);
		if(page < max){
			TextComponent next = new TextComponent(fixColors(msg.cmd_nav_next));
			HoverEvent nexth = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent((page + 1) + "")});
			ClickEvent nextc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, toRun.replaceAll("%num%", (page + 1) + ""));
			next.setHoverEvent(nexth);
			next.setClickEvent(nextc);
			source.addExtra(next);
		}
		source.addExtra(new TextComponent(fixColors(msg.cmd_nav_after)));
		send(s, source);
	}

	public String fixColors(String st){
		return st.replaceAll("^&(?=[0-9a-fk-orA-FK-OR])|(?<=[^&])&(?=[0-9a-fk-orA-FK-OR])", "§").replaceAll("&&", "&");
	}

	public void send(CommandSender s, BaseComponent comp){
		if(s instanceof Player){
			((Player)s).spigot().sendMessage(comp);
		}else{
			s.sendMessage(comp.toLegacyText());
		}
	}
}
