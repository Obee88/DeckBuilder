package custom.classes;

import com.mongodb.DBObject;

public class CardInfo {
	public String downloadLink,text,name,type,manaCost,rarity,artist,edition,subType;
	public boolean isTwoSided;
	public int convertedManaCost, id;
	
	public CardInfo(DBObject obj) {
		downloadLink = obj.get("downloadLink").toString();
		text = obj.get("text").toString();
		name = obj.get("name").toString();
		type = obj.get("type").toString();
		manaCost = obj.get("manaCost").toString();
		rarity = obj.get("rarity").toString();
		artist = obj.get("artist").toString();
		edition = obj.get("edition").toString();
		convertedManaCost = Integer.parseInt(obj.get("convertedManaCost").toString());
		id = Integer.parseInt(obj.get("id").toString());
		isTwoSided = getBool(obj.get("isTwoSided").toString());
	}

	private Boolean getBool(String string) {
		if (string.toLowerCase().equals("true"))
			return true;
		return false;
	}
}
