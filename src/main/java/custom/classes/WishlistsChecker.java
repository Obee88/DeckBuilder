package custom.classes;

import database.MongoHandler;

import java.util.*;

/**
 * Created by Obee on 30/07/15.
 */
public class WishlistsChecker {

    private StackableTreeMap interestMap = new StackableTreeMap();

    public WishlistsChecker load(List<String> wl, String username) {
        for (String cardName: wl)
            put(cardName, username);
        return this;
    }

    public List<String> getInterestedUserNames(String cardName){
        return interestMap.get(cardName);
    }

    public static WishlistsChecker fromMongo(MongoHandler mongo){
        WishlistsChecker wlc = new WishlistsChecker();
        for (String userName: mongo.getAllUserNames())
            for (Object cardName: mongo.getUserWishlist(userName))
                wlc.put(cardName.toString(), userName);
        return wlc;
    }

    private void put(String cardName, String userName) {
        interestMap.put(cardName, userName);
    }

    public void checkList(List<ShowingCard> tradeList) {
        for (ShowingCard sc: tradeList)
            sc.setInterestList(getInterestedUserNames(sc.name));
    }


    class StackableTreeMap {
        Map<String, List<String>> map = new TreeMap<String, List<String>>();

        public StackableTreeMap put(String cardName, String userName){
            if(map.containsKey(cardName))
                map.get(cardName).add(userName);
            else {
                List<String> l = new ArrayList<String>();
                l.add(userName);
                map.put(cardName, l);
            }
            return this;
        }

        public List<String> get(String cardName){
            return map.get(cardName);
        }
    }
}


