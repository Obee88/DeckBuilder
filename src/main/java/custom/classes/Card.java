package custom.classes;

import java.util.Date;
import java.util.Random;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import custom.classes.abstractClasses.MongoObject;

import database.MongoHandler;
import org.joda.time.DateTime;

public class Card extends MongoObject{
	Integer cardId;
	Integer cardInfoId;
	String printed, owner, status;
	String inProposal;
    public Date creationDate;

    public static Card generateCard(String owner) {
		MongoHandler mongo;
		mongo = MongoHandler.getInstance();
		int _cardId=Administration.getNextCardId();
		boolean _printed=false;
		int _cardInfoId = generateRandomCardInfo(_cardId);
		DBObject obj = new BasicDBObject()
			.append("id", _cardId)
			.append("printed", _printed)
			.append("owner", owner)
			.append("cardInfoId", _cardInfoId)
			.append("status", "booster")
            .append("creationDate", new DateTime().toDate());
		mongo.setExistance(_cardInfoId,true);
		mongo.cardsCollection.insert(obj);
		return new Card(obj);
	}
	
	private static Integer generateRandomCardInfo(int creatingCardId) {
		String rarity=CardGenerator.getRarity(new Random().nextInt(100));
		Integer ret = null;
		while(ret==null){
			BasicDBObject basObj = new BasicDBObject("rarity",rarity).append("exist",false);
			DBCursor cur = MongoHandler.getInstance().cardInfoCollection.find(
				basObj
				);
			DBObject retObj= mongo.getRandomOne(cur);
			ret = (Integer) retObj.get("id");
		}
		return ret;
	}

	public Card(DBObject obj)  {
		cardId =obj.get("id")==null?null:Integer.parseInt(obj.get("id").toString());
		printed = obj.get("printed").toString();
		cardInfoId = Integer.parseInt(obj.get("cardInfoId").toString());
		owner = obj.get("owner")==null?null:obj.get("owner").toString();
		status = obj.get("status")==null?null:obj.get("status").toString();
		inProposal = obj.get("inProposal")==null?"false":obj.get("inProposal").toString();
        creationDate = (Date)obj.get("creationDate");
	}
	
	public Card(DBObject obj, String owner)  {
		cardId = Integer.parseInt(obj.get("id").toString());
		printed = obj.get("printed").toString();
		cardInfoId = Integer.parseInt(obj.get("cardInfoId").toString());
		status = obj.get("status").toString();
		inProposal = obj.get("inProposal")==null?"false":obj.get("inProposal").toString();
		this.owner = owner;
        creationDate = (Date)obj.get("creationDate");
	}

	public int getCardId() {
		return cardId;
	}

	public String getPrinted() {
		return printed;
	}

	public DBObject toDBObject() {
		BasicDBObject obj = new BasicDBObject();
		obj.append("id", cardId)
		.append("printed", printed)
		.append("owner", owner)
		.append("cardInfoId", cardInfoId)
		.append("status", status)
		.append("inProposal", inProposal)
        .append("creationDate",creationDate);
		return obj;
	}
	
	public BasicDBObject getQ(){
		return new BasicDBObject("id", cardId);
	}
	
	public void UPDATE(){
		MongoHandler.getInstance().cardsCollection.update(getQ(), toDBObject());
	}

	public static Card generateFromCardName(String name, String owner) {
		MongoHandler mongo;
		mongo = MongoHandler.getInstance();
		int _cardId=Administration.getNextCardId();
		boolean _printed=false;
		int _cardInfoId = mongo.getCardInfoId(name);
		DBObject obj = new BasicDBObject()
			.append("id", _cardId)
			.append("printed", _printed)
			.append("owner", owner)
			.append("cardInfoId", _cardInfoId)
			.append("status", "booster")
            .append("creationDate",new DateTime().toDate());
		mongo.cardsCollection.insert(obj);
		return new Card(obj);
	}
	
	public Boolean isInProposal(){
		if(inProposal.equals("true"))
			return true;
		return false;
	}
	
	public void setInProposal(String str){
		inProposal=str;
	}
	
	public void setInProposal(Boolean b){
		setInProposal(b.toString().toLowerCase());
	}

	public String getOwner() {
		return owner;
	}

    public Date getCreationDate() {
        return creationDate;
    }
}
