package database;



import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import custom.classes.Card;
import custom.classes.User;

public class tmp {

	public static void main(String[] args) {
		User Obee  = MongoHandler.getInstance().getUser("Obee");
		DBCursor cur = MongoHandler.getInstance().cardInfoCollection.find(
				new BasicDBObject("isTwoSided", true));
		while(cur.hasNext()){
			for(int i=0;i<7;i++)
				if(cur.hasNext())
					cur.next();
			
			DBObject obj;
			if(cur.hasNext())
				obj= cur.next();
			else break;
			String name = obj.get("name").toString();
			Card c = Card.generateFromCardName(name, Obee.getUserName());
			Obee.addToBooster(c.getCardId());
			Obee.UPDATE();
		}
	}
}
