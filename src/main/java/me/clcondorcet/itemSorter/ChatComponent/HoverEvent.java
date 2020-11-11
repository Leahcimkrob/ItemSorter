package me.clcondorcet.itemSorter.ChatComponent;

import java.util.Set;

import me.clcondorcet.itemSorter.Main;
import org.bukkit.inventory.ItemStack;

public class HoverEvent {
	/**
	 * 
	 * @author clcondorcet
	 */
	
	private HoverType type;
	private Object value;
	
	public HoverEvent(HoverType type, Object value){
		this.type = type;
		this.value = value;
	}
	
	public String toJson() throws IllegalArgumentException{
		try{
			String json = ",\"hoverEvent\":{\"action\":\"" + type.jsonName + "\",\"value\":\"";
			switch(type){
				case NONE:
					return "";
				case SHOW_TEXT:
					json += (String) value + "\"}";
					return json;
				case SHOW_ACHIEVEMENT:
					// Fonctionne pas ! : json += "{\\\"display\\\":{\\\"icon\\\":{\\\"item\\\": \\\"minecraft:map\\\"},\\\"title\\\": {\\\"text\\\": \\\"§ctest\\\"},\\\"description\\\": {\\\"text\\\": \\\"§bqsd\\\"},\\\"background\\\": \\\"minecraft:textures/gui/advancements/backgrounds/adventure.png\\\",\\\"show_toast\\\": false,\\\"announce_to_chat\\\": false},\\\"criteria\\\": {\\\"killed_something\\\": {\\\"trigger\\\": \\\"minecraft:player_killed_entity\\\"},\\\"killed_by_something\\\": {\\\"trigger\\\": \\\"minecraft:entity_killed_player\\\"}},\\\"requirements\\\": [[\\\"killed_something\\\", \\\"killed_by_something\\\"]]}";
					json += "" + "\"}";
					return json;
				case SHOW_ITEM:
					if(value instanceof ItemStack){
						ItemStack item = (ItemStack) value;
						try{
							Class<?> classCraftItemStack = Class.forName("org.bukkit.craftbukkit." + Main.version + ".inventory.CraftItemStack");
							Object nmsItem = classCraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(classCraftItemStack, item);
							Class<?> classItem = Class.forName("net.minecraft.server." + Main.version + ".Item");
							String id = "minecraft:";
							Object mapRegistry = classItem.getField("REGISTRY").get(classItem.newInstance());
							Object itemFromSource = nmsItem.getClass().getMethod("getItem", null).invoke(nmsItem);
							String nameSource = (String) itemFromSource.getClass().getMethod("getName", null).invoke(itemFromSource);
							for(Object minecraftKeyObj : (Set<Object>) mapRegistry.getClass().getMethod("keySet", null).invoke(mapRegistry)){
								Object itemFromKey = mapRegistry.getClass().getMethod("get", Object.class).invoke(mapRegistry, minecraftKeyObj);
								String nameItem = (String) itemFromKey.getClass().getMethod("getName", null).invoke(itemFromKey);
								if(nameItem.equals(nameSource)){
									id += (String) minecraftKeyObj.getClass().getMethod("getKey", null).invoke(minecraftKeyObj);
									break;
								}
							}
							if(id.equals("minecraft:")){
								throw new IllegalArgumentException("The does not correspond to anything.");
							}
							int count = (int) nmsItem.getClass().getMethod("getCount", null).invoke(nmsItem);
							int data = (int) nmsItem.getClass().getMethod("getData", null).invoke(nmsItem);
							Object NBTTagCompound = nmsItem.getClass().getMethod("getTag", null).invoke(nmsItem);
							String tags = (String) NBTTagCompound.getClass().getMethod("toString", null).invoke(NBTTagCompound);
							tags = tags.replaceAll("(?<!\\\\)\\\"", "\\\\\"").replaceAll("((?<=[{,]).{0}(?=[a-zA-Z])|(?<=[a-zA-Z]).{0}(?=:))", "\\\\\"");
							json += "{\\\"id\\\":\\\"" + id + "\\\",\\\"Count\\\":" + count + ",\\\"Damage\\\":" + data + ",\\\"tag\\\":" + tags + "}\"}";
							return json;
						}catch(Exception ex){
							ex.printStackTrace();
						}
						return null;
					}else{
						throw new IllegalArgumentException("The value must be a bukkit ItemStack !");
					}
				case SHOW_ENTITY:
					json += "" + "}";
					return json;
				default:
					throw new IllegalArgumentException("ClickType is unknown !");
			}
		}catch(IllegalArgumentException ex){
			throw new IllegalArgumentException(ex.getMessage());
		}catch(Exception ex){
			throw new IllegalArgumentException("Value is not in the right form !");
		}
	}
	
	public static enum HoverType{
		NONE(""),
        SHOW_TEXT("show_text"),
        SHOW_ACHIEVEMENT("show_achivement"),
        SHOW_ITEM("show_item"),
        SHOW_ENTITY("show_entity");
		
		public String jsonName;
		
		private HoverType(String jsonName){
			this.jsonName = jsonName;
		}
	}
}
