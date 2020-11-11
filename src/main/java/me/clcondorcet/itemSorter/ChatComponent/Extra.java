package me.clcondorcet.itemSorter.ChatComponent;

public class Extra {
	/**
	 * 
	 * @author clcondorcet
	 */
	

	private String text;
	private ClickEvent clickEvent = new ClickEvent(ClickEvent.ClickType.NONE, null);
	private HoverEvent hoverEvent = new HoverEvent(HoverEvent.HoverType.NONE, null);
	
	public Extra(String text){
		this.text = text;
	}
	
	public Extra setClickEvent(ClickEvent clickEvent){
		this.clickEvent = clickEvent;
		return this;
	}
	
	public Extra setHoverEvent(HoverEvent hoverEvent){
		this.hoverEvent = hoverEvent;
		return this;
	}
	
	public String toJson(){
		String json = "{\"text\":\"" + text + "\"";
		String clickJson = "";
		try{
			clickJson = clickEvent.toJson();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		json += clickJson;
		String hoverJson = "";
		try{
			hoverJson = hoverEvent.toJson();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		json += hoverJson;
		json += "}";
		return json;
	}
}
