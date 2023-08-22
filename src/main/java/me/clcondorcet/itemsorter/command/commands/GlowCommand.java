package me.clcondorcet.itemsorter.command.commands;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.command.ItemSorterCommand;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.Deposit;
import me.clcondorcet.itemsorter.data.Filter;
import me.clcondorcet.itemsorter.data.System;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.clcondorcet.itemsorter.command.MainCommand.msg;

public class GlowCommand extends ItemSorterCommand {

    public static GlowCommand GLOW_COMMAND = new GlowCommand("glow");

    private GlowCommand(String name) {
        super(name);
    }

    public HashMap<Player, ArrayList<String>> glowMap = new HashMap<>();
    public HashMap<Player, BukkitTask> glowTasks = new HashMap<>();

    @Override
    public void runCommand(CommandSender s, String label, String[] args) throws CommandReturn {
        checkPerm(s, "itemsorter.command.glow");

        if (!ItemSorter.versionHandler.isGlowAvailable()) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_glow_lowerVersion);
            return;
        }

        if (!(s instanceof Player)) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_glow_notPlayer);
            return;
        }

        Player p = (Player) s;

        if (args.length == 0) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noOption);
            return;
        }

        if (args.length == 1) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noBase);
            return;
        }

        System system = null;
        for (System sys : DataManager.getSystems()) {
            if (sys.name.equals(args[1])) {
                system = sys;
                break;
            }
        }

        if (system == null) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_glow_baseNotExist.replaceAll("%base%", args[1]));
            return;
        }

        if (!system.baseLoc.sameWorld(p.getLocation())) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_glow_notInSameWorld);
            return;
        }

        if (!s.hasPermission("itemsorter.command.glow.other")) {
            if (system.isOwner(p)) {
                if (!s.hasPermission("itemsorter.command.glow")) {
                    s.sendMessage(ItemSorter.prefix + msg.cmd_dontHavePermission);
                    return;
                }
            } else if (system.canAccess(p)) {
                if (!s.hasPermission("itemsorter.command.glow.trust")) {
                    s.sendMessage(ItemSorter.prefix + msg.cmd_glow_notOwner);
                    return;
                }
            } else {
                s.sendMessage(ItemSorter.prefix + msg.cmd_glow_notTrust);
                return;
            }
        }

        if (args[0].equalsIgnoreCase("base")) {
            try {
                addGlow(p, system.baseLoc.clone().add(0.5d, 0d, 0.5d).build(), true, 0);
            } catch (FutureLocation.WorldNotLoaded ignored) {
            }
            if ((!(args.length > 2) || !args[2].equalsIgnoreCase("false"))) {
                s.sendMessage(ItemSorter.prefix + msg.cmd_glow_complete);
            }


        } else if (args[0].equalsIgnoreCase("filters")) {
            if (args.length == 2) {
                s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noOptionFilter);
                return;
            }

            String filter = args[2];
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
            for (Filter fil : system.getFilters()) {
                if ((filter.equals("Trash") && fil.isTrash()) || (!filter.equals("Trash") && fil.getMaterials().containsKey(Material.valueOf(filter)))) {
                    filters.add(fil);
                }
            }
            if (args.length > 3 && !args[3].equalsIgnoreCase("false")) {
                int id = -1;
                try {
                    id = Integer.parseInt(args[3]);
                    if (id < 0) {
                        id = -1;
                    }
                } catch (Exception ignored) {
                }
                if (id == -1) {
                    s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noBlocsFound);
                    return;
                }
                Filter fil = null;
                for (Filter fils : filters) {
                    int o = 0;
                    if (fils.isTrash()) {
                        o = fils.getTrashPriority();
                    } else {
                        me.clcondorcet.itemsorter.data.Material matObj = fils.getMaterials().get(Material.valueOf(filter));
                        o = -1;
                        if (matObj != null) {
                            o = matObj.getPriority();
                        }
                    }
                    if (o != -1) {
                        if (id == o) {
                            fil = fils;
                            break;
                        }
                    }
                }
                if (fil == null) {
                    s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noBlocsFound);
                    return;
                }
                try {
                    addGlow(p, fil.loc.clone().add(0.5d, 0d, 0.5d).build(), true, 0);
                } catch (FutureLocation.WorldNotLoaded ignored) {
                }
                if ((!(args.length > 4) || !args[4].equalsIgnoreCase("false"))) {
                    s.sendMessage(ItemSorter.prefix + msg.cmd_glow_complete);
                }
                return;
            }
            ArrayList<Location> locs = new ArrayList<>();
            for (Filter fil : filters) {
                try {
                    locs.add(fil.loc.clone().add(0.5d, 0d, 0.5d).build());
                } catch (FutureLocation.WorldNotLoaded ignored) {
                }
            }
            addGlow(p, locs);
            if ((!(args.length > 3) || !args[3].equalsIgnoreCase("false"))) {
                s.sendMessage(ItemSorter.prefix + msg.cmd_glow_complete);
            }

        } else if (args[0].equalsIgnoreCase("deposits")) {
            if (args.length > 2 && !args[2].equalsIgnoreCase("false")) {
                int id = -1;
                try {
                    id = Integer.parseInt(args[2]);
                    if (id < 0) {
                        id = -1;
                    }
                } catch (Exception ignored) {
                }

                if (id == -1) {
                    s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noBlocsFound);
                    return;
                }
                if (system.getDeposits().size() <= id) {
                    s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noBlocsFound);
                    return;
                }
                try {
                    addGlow(p, system.getDeposits().get(id).loc.clone().add(0.5d, 0d, 0.5d).build(), true, 0);
                } catch (FutureLocation.WorldNotLoaded ignored) {
                }
                if ((!(args.length > 3) || !args[3].equalsIgnoreCase("false"))) {
                    s.sendMessage(ItemSorter.prefix + msg.cmd_glow_complete);
                }
            }
            ArrayList<Location> locs = new ArrayList<>();
            for (Deposit dep : system.getDeposits()) {
                try {
                    locs.add(dep.loc.clone().add(0.5d, 0d, 0.5d).build());
                } catch (FutureLocation.WorldNotLoaded ignored) {
                }
            }
            addGlow(p, locs);
            if ((!(args.length > 2) || !args[2].equalsIgnoreCase("false"))) {
                s.sendMessage(ItemSorter.prefix + msg.cmd_glow_complete);
            }
        } else {
            s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noOption);
        }
    }

    public int addGlow(Player p, Location loc, boolean remove, int givenId) {
        if (remove && glowMap.containsKey(p)) {
            glowTasks.get(p).cancel();
            glowTasks.remove(p);
            ArrayList<String> list = glowMap.get(p);
            for (String id : list) {
                ItemSorter.versionHandler.removeGlow(p, Integer.parseInt(id));
            }
            glowMap.remove(p);
        }
        int id = 200;
        if (!remove) {
            id = givenId;
        } else {
            ArrayList<String> list = new ArrayList<>();
            list.add(id + "");
            glowMap.put(p, list);
        }
        ItemSorter.versionHandler.glow(loc, p, id);
        if (remove) {
            int finalId = id;
            BukkitTask task = Bukkit.getScheduler().runTaskLater(ItemSorter.getInstance(), new Runnable() {
                @Override
                public void run() {
                    ItemSorter.versionHandler.removeGlow(p, finalId);
                    glowMap.remove(p);
                    glowTasks.remove(p);
                }
            }, 20 * 10);
            glowTasks.put(p, task);
        }
        return id;
    }

    public void addGlow(Player p, List<Location> locs) {
        if (glowMap.containsKey(p)) {
            glowTasks.get(p).cancel();
            glowTasks.remove(p);
            ArrayList<String> list = glowMap.get(p);
            for (String id : list) {
                ItemSorter.versionHandler.removeGlow(p, Integer.parseInt(id));
            }
            glowMap.remove(p);
        }
        ArrayList<String> ids = new ArrayList<>();
        int id = -1;
        for (Location loc : locs) {
            addGlow(p, loc, false, id);
            ids.add(id + "");
            id--;
        }
        glowMap.put(p, ids);
        BukkitTask task = Bukkit.getScheduler().runTaskLater(ItemSorter.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (String id : ids) {
                    ItemSorter.versionHandler.removeGlow(p, Integer.parseInt(id));
                }
                glowMap.remove(p);
                glowTasks.remove(p);
            }
        }, 20 * 10);
        glowTasks.put(p, task);
    }
}


/*
if (s.hasPermission("itemsorter.command.glow")) {
					if (ItemSorter.versionHandler.isGlowAvailable()) {
						if (s instanceof Player) {
							Player p = (Player) s;
							if (args.length != 1) {
								if (args.length != 2) {
									System system = null;
									for (System sys : DataManager.getSystems()) {
										if (sys.name.equals(args[2])) {
											system = sys;
											break;
										}
									}
									if (system != null) {
										if (system.baseLoc.sameWorld(p.getLocation())) {
											if (!s.hasPermission("itemsorter.command.glow.other")) {
												if (system.isOwner(p)) {
													if (!s.hasPermission("itemsorter.command.glow")) {
														s.sendMessage(ItemSorter.prefix + msg.cmd_dontHavePermission);
														return true;
													}
												} else if (system.canAccess(p)) {
													if (!s.hasPermission("itemsorter.command.glow.trust")) {
														s.sendMessage(ItemSorter.prefix + msg.cmd_glow_notOwner);
														return true;
													}
												} else {
													s.sendMessage(ItemSorter.prefix + msg.cmd_glow_notTrust);
													return true;
												}
											}
											if (args[1].equalsIgnoreCase("base")) {
												try {
													addGlow(p, system.baseLoc.clone().add(0.5d, 0d, 0.5d).build(), true, 0);
												} catch (FutureLocation.WorldNotLoaded ignored) {}
												if ((!(args.length > 3) || !args[3].equalsIgnoreCase("false"))) {
													s.sendMessage(ItemSorter.prefix + msg.cmd_glow_complete);
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
													for (Filter fil : system.getFilters()) {
														if ((filter.equals("Trash") && fil.isTrash()) || (!filter.equals("Trash") && fil.getMaterials().containsKey(Material.valueOf(filter)))) {
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
																if (fils.isTrash()) {
																	o = fils.getTrashPriority();
																} else {
																	me.clcondorcet.itemsorter.data.Material matObj = fils.getMaterials().get(Material.valueOf(filter));
																	o = -1;
																	if (matObj != null) {
																		o = matObj.getPriority();
																	}
																}
																if (o != -1) {
																	if (id == o) {
																		fil = fils;
																		break;
																	}
																}
															}
															if (fil == null) {
																s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noBlocsFound);
																return true;
															} else {
																try {
																	addGlow(p, fil.loc.clone().add(0.5d, 0d, 0.5d).build(), true, 0);
																} catch (FutureLocation.WorldNotLoaded ignored) {}
																if ((!(args.length > 5) || !args[5].equalsIgnoreCase("false"))) {
																	s.sendMessage(ItemSorter.prefix + msg.cmd_glow_complete);
																}
																return true;
															}
														} else {
															s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noBlocsFound);
															return true;
														}
													}
													ArrayList<Location> locs = new ArrayList<>();
													for (Filter fil : filters) {
														try {
															locs.add(fil.loc.clone().add(0.5d, 0d, 0.5d).build());
														} catch (FutureLocation.WorldNotLoaded ignored) {}
													}
													addGlow(p, locs);
													if ((!(args.length > 4) || !args[4].equalsIgnoreCase("false"))) {
														s.sendMessage(ItemSorter.prefix + msg.cmd_glow_complete);
													}
												} else {
													s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noOptionFilter);
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
														if (system.getDeposits().size() <= id) {
															s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noBlocsFound);
														} else {
															try {
																addGlow(p, system.getDeposits().get(id).loc.clone().add(0.5d, 0d, 0.5d).build(), true, 0);
															} catch (FutureLocation.WorldNotLoaded ignored) {}
															if ((!(args.length > 4) || !args[4].equalsIgnoreCase("false"))) {
																s.sendMessage(ItemSorter.prefix + msg.cmd_glow_complete);
															}
														}
														return true;
													} else {
														s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noBlocsFound);
														return true;
													}
												}
												ArrayList<Location> locs = new ArrayList<>();
												for (Deposit dep : system.getDeposits()) {
													try {
														locs.add(dep.loc.clone().add(0.5d, 0d, 0.5d).build());
													} catch (FutureLocation.WorldNotLoaded ignored) {}
												}
												addGlow(p, locs);
												if ((!(args.length > 3) || !args[3].equalsIgnoreCase("false"))) {
													s.sendMessage(ItemSorter.prefix + msg.cmd_glow_complete);
												}
											} else {
												s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noOption);
											}
										} else {
											s.sendMessage(ItemSorter.prefix + msg.cmd_glow_notInSameWorld);
										}
									} else {
										s.sendMessage(ItemSorter.prefix + msg.cmd_glow_baseNotExist.replaceAll("%base%", args[2]));
									}
								} else {
									s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noBase);
								}
							} else {
								s.sendMessage(ItemSorter.prefix + msg.cmd_glow_noOption);
							}
						} else {
							s.sendMessage(ItemSorter.prefix + msg.cmd_glow_notPlayer);
						}
					} else {
						s.sendMessage(ItemSorter.prefix + msg.cmd_glow_lowerVersion);
					}
				} else {
					s.sendMessage(ItemSorter.prefix + msg.cmd_dontHavePermission);
				}
 */