package custom.classes;

import java.util.ArrayList;
import java.util.List;

import database.MongoHandler;
import suport.MailSender;

public class CardGenerator {
	public static List<Card> generateBooster(int size, String owner){
		List<User> allUsrs =MongoHandler.getInstance().getAllUsers();
		List<Card> ret = new ArrayList<Card>();
		while(ret.size()<size){
			Card c = Card.generateCard(owner);
			ShowingCard sc = new ShowingCard(c);
			checkWishlists(sc,allUsrs, owner);
			ret.add(c);
		}
		return ret;
	}

	public static String getRarity(int rand) {
		if(rand<60) return "common";
		if(rand<90) return "uncommon";
		if(rand<99) return "rare";
		return "mythic";
	}

	private static void checkWishlists(ShowingCard sc, List<User> allUsrs, String owner) {
		for(User u: allUsrs){
			if(u.wishList.contains(sc.name)){
				u.addMessage(new UserMessage(u.getNextMessageId(),"Wishlist message",owner + " just gathered #"+sc.name+ "# card that is in your wishlist."));
                if(u.wantsWishlistMail())
                    MailSender.sendWishlistNotification(u,sc, owner);
				u.UPDATE();
			}
		}
	}
}
