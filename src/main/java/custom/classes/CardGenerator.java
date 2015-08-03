package custom.classes;

import com.mongodb.DBObject;
import database.MongoHandler;
import suport.MailSender;

import java.util.ArrayList;
import java.util.List;

public class CardGenerator {
	public static List<Card> generateBooster(int size, String owner){
		List<User> allUsrs =MongoHandler.getInstance().getAllUsers();
		List<Card> ret = new ArrayList<Card>();
		while(ret.size()<size){
            Card c = null;
            while (c==null || c.isBasicLand())
                c= ret.size()%48==13
                    ? Card.generateCard(owner,null,"land", ret.size()==31)
                    : Card.generateCard(owner, ret.size()==31);
			ShowingCard sc = new ShowingCard(c);
			checkWishlists(sc,allUsrs, owner);
			ret.add(c);
		}
        DBObject[] retObjs = null;
        boolean success = false;
        while (!success){
            try {
                retObjs = new DBObject[ret.size()];
                int i=0;
                for (Card c : ret){
                    retObjs[i] = c.toDBObject();
                    i++;
                }
                MongoHandler.getInstance().cardsCollection.insert(retObjs);
                success = true;
            } catch(Exception e){
                System.out.println("nije uspjelo");
            }
        }
		return ret;
	}

	public static String getRarity(int rand) {
		if(rand<53) return "common";
		if(rand<80) return "uncommon";
		if(rand<99) return "rare";
		return "mythic";
	}

    public static Card generateOneCard(String owner, String rarity){
        List<User> allUsrs =MongoHandler.getInstance().getAllUsers();
        Card c = Card.generateCard(owner, rarity, false);
        ShowingCard sc = new ShowingCard(c);
        checkWishlists(sc, allUsrs, owner);
        boolean success = false;
        while (!success){
            try {
                MongoHandler.getInstance().cardsCollection.insert(c.toDBObject());
                success = true;
            } catch(Exception e){}
        }
        return c;
    }

    public static Card generateOneCard(String owner, String rarity, String type){
        List<User> allUsrs =MongoHandler.getInstance().getAllUsers();
        Card c = Card.generateCard(owner, rarity, type, false);
        ShowingCard sc = new ShowingCard(c);
        checkWishlists(sc, allUsrs, owner);
        return c;
    }

	private static void checkWishlists(ShowingCard sc, List<User> allUsrs, String owner) {
		for(User u: allUsrs){
            u.setWishlists();
			if(u.wishList.contains(sc.name)){
				u.addMessage(new UserMessage(u.getNextMessageId(),"Wishlist message",owner + " just gathered #"+sc.name+ "# card that is in your wishlist."));
                if(u.wantsWishlistMail())
                    MailSender.sendWishlistNotification(u,sc, owner);
				u.UPDATE();
			}
		}
	}
}
