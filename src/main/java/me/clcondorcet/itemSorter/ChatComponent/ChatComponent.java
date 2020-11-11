package me.clcondorcet.itemSorter.ChatComponent;

import me.clcondorcet.itemSorter.Main;

import java.util.ArrayList;
import java.util.List;

public class ChatComponent {
	/**
	 * 
	 * @author clcondorcet
	 */
	
	private String text;
	private List<Extra> extras = new ArrayList<>();
	
	public ChatComponent(String text){
		this.text = text;
	}
	
	public ChatComponent addExtra(Extra extra){
		extras.add(extra);
		return this;
	}
	
	public String toJson(){
		String json = "{\"text\":\"" + this.text + "\"";
		if(extras.size() > 0){
			json += ",\"extra\":[";
			int i = this.extras.size() - 1;
			for(Extra extra : this.extras){
				json += extra.toJson();
				if(i != 0){
					json += ",";
				}
				i--;
			}
			json += "]";
		}
		json += "}";
		return json;
	}
	 
	public Object buildIchatBaseComponent(){
		try{
			Class<?> classIchatBaseComponent = Class.forName("net.minecraft.server." + Main.version + ".IChatBaseComponent");
			Class<?> classChatSerializer = classIchatBaseComponent.getClasses()[0];
			return classChatSerializer.getMethod("a", String.class).invoke(classChatSerializer.newInstance(), this.toJson());
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
}
