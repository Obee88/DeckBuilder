package custom.classes;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import database.MongoHandler;

public class CardInfo {
    private String _id = null;
    private int numOfColors;
    public String downloadLink,text,name,type,manaCost,rarity,artist,edition,subType;
	public boolean isTwoSided;
	public int convertedManaCost, id, rarityInt;
	private MongoHandler mongo = MongoHandler.getInstance();
    private boolean hated;

    public CardInfo(DBObject obj) {
        downloadLink = obj.get("downloadLink").toString();
        text = obj.get("text").toString();
        name = obj.get("name").toString();
        type = obj.get("type").toString();
        manaCost = obj.get("manaCost").toString();
        rarity = obj.get("rarity").toString();
        artist = obj.get("artist").toString();
        edition = obj.get("edition").toString();
        id=obj.get("id")==null?this.fixId():(Integer)obj.get("id");
        convertedManaCost = Integer.parseInt(obj.get("convertedManaCost").toString());
        try {
            isTwoSided = getBool(obj.get("isTwoSided").toString());
        } catch (Exception guessNot) {
            isTwoSided = false;
        }
        try {
            numOfColors = Integer.parseInt(obj.get("numOfColors").toString());
        } catch (Exception ignorable) {
            numOfColors = 0;
        }
        try {
            _id = obj.get("_id").toString();
        } catch (Exception ignorable){
            _id = obj.get("string_id").toString();
        }
        rarityInt = obj.get("rarity_int") == null ? calcRarityInt() : Integer.parseInt(obj.get("rarity_int").toString());
    }

    private int fixId() {
        int id = mongo.getNextCardInfoId();
        BasicDBObject q = new BasicDBObject("name",name);
        q.append("id",new BasicDBObject("$exists", false));
        mongo.cardInfoCollection.update(q, toDBObject(),false,false);
        return id;
    }

    private Boolean getBool(String string) {
		if (string.toLowerCase().equals("true"))
			return true;
		return false;
	}

    public DBObject toDBObject(){
        BasicDBObject obj = new BasicDBObject();
        obj.append("downloadLink", downloadLink);
        obj.append("text", text);
        obj.append("name", name);
        obj.append("type", type);
        obj.append("manaCost", manaCost);
        obj.append("rarity", rarity);
        obj.append("artist", artist);
        obj.append("edition", edition);
        obj.append("convertedManaCost", convertedManaCost);
        obj.append("id", id);
        obj.append("isTwoSided", isTwoSided);
        obj.append("rarity_int", rarityInt);
        obj.append("string_id", _id);
        return obj;
    }

    public void UPDATE(){
        mongo.cardInfoCollection.update(getQ(), toDBObject(),true, true);
    }

    public DBObject getQ() {
        return new BasicDBObject("name", name);
    }

    public boolean hasColor(String c) {
        if(manaCost.equals("")){
            return text.contains("{"+c+"}");
        } else if (type.toLowerCase().contains("land")){
            return text.contains("{"+c+"}") || manaCost.contains(c);
        }
        return manaCost.contains(c);
    }


    public int calcRarityInt() {
        String[] rarityes = new String[]{"common","uncommon","rare","mythic"};
        for (int i = 0; i < rarityes.length; i++) {
            if (rarityes[i].equals(rarity))
                return i;
        }
        return 99;
    }

    public String getLinkForDownloading() {
        return "http://127.0.0.1/images/" +_id+".jpg";
    }
}
