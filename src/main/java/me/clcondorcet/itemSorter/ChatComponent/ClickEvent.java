package me.clcondorcet.itemSorter.ChatComponent;

public class ClickEvent {
	/**
	 * 
	 * @author clcondorcet
	 */
	
	private ClickType type;
	private Object value;
	
	public ClickEvent(ClickType type, Object value){
		this.type = type;
		this.value = value;
	}
	
	public String toJson() throws IllegalArgumentException{
		try{
			String json = ",\"clickEvent\":{\"action\":\"" + type.jsonName + "\",\"value\":\"";
			switch(type){
				case NONE:
					return "";
				case OPEN_URL:
					json += (String) value + "\"}";
					return json;
				case RUN_COMMAND:
					json += (String) value + "\"}";
					return json;
				case SUGGEST_COMMAND:
					json += (String) value + "\"}";
					return json;
				case CHANGE_PAGE:
					json += (int) value + "}";
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
	
	public static enum ClickType{
		NONE(""),
		OPEN_URL("open_url"),
        RUN_COMMAND("run_command"),
        SUGGEST_COMMAND("suggest_command"),
        CHANGE_PAGE("change_page");
		
		public String jsonName;
		
		private ClickType(String jsonName){
			this.jsonName = jsonName;
		}
	}
}
