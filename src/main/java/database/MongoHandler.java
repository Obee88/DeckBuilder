package database;

import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;

import com.mongodb.*;

import custom.classes.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class MongoHandler {
	private static MongoHandler instance;
	
	private final String HOST = "localhost";
	private final Integer PORT = 27017;
	private final String DATABASE_NAME = "Magic";
	private final String USERS_COLLECTION_NAME = "users";
	private final String CARDS_COLLECTION_NAME = "cards";
	private final String CARDIFNO_COLLECTION_NAME = "cardInfo";
	private final String ADMIN_COLLECTION_NAME = "administration";
    private final String STATISTICS_COLLECTION_NAME = "statistics";
    private final String SUCESSFULL_PROPOSALS_COLLECTION_NAME = "statistics";
	
	private MongoClient client;
	private DB base;
	public DBCollection usersCollection, cardsCollection, cardInfoCollection, adminCollection, statisticsCollection, sucessfullProposalsCollections;

    private MongoHandler(){
		try {
			client = new MongoClient(HOST,PORT);
			base = client.getDB(DATABASE_NAME);
            base.authenticate("Deck","Builder".toCharArray());
			usersCollection=base.getCollection(USERS_COLLECTION_NAME);
			cardsCollection=base.getCollection(CARDS_COLLECTION_NAME);
			cardInfoCollection=base.getCollection(CARDIFNO_COLLECTION_NAME);
			adminCollection=base.getCollection(ADMIN_COLLECTION_NAME);
            statisticsCollection=base.getCollection(STATISTICS_COLLECTION_NAME);
            sucessfullProposalsCollections = base.getCollection(SUCESSFULL_PROPOSALS_COLLECTION_NAME);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public static MongoHandler getInstance(){
		if(instance==null)
			instance = new MongoHandler();
		return instance;
	}
	
	public User getUser(String userName){
		DBObject q =new BasicDBObject("userName", userName);
		DBObject obj = usersCollection.findOne(q);
		if(obj==null) return null;
		return new User(obj);
	}

    public void removeUser(String userName){
        usersCollection.remove(new BasicDBObject("userName",userName));
    }
	
	public boolean userExist(String userName){
		return getUser(userName)!=null;
	}

	public DBObject getAdminObject() {
		return adminCollection.findOne();
		
	}

	public int getNumberOfUsers() {
		return (int) usersCollection.count();
	}

	public ArrayList<User> getAllUsers() {
		ArrayList<User> ret = new ArrayList<User>();
		DBCursor cur = usersCollection.find();
		while(cur.hasNext())
			ret.add(new User(cur.next()));
		return ret;
	}

	public DBObject getRandomOne(DBCursor cur) {
		int len = cur.size();
		if(len==0)
			System.out.println("omg");
		int num = new Random().nextInt(100000);
		int index = num%len;
		for(int i=0;i<index;i++) cur.next();
		return cur.next();
	}

	public int getCardInfoId(String name) {

		DBCursor cur = cardInfoCollection.find(
                new BasicDBObject("name", Pattern.compile("^"+name.trim()+"$", Pattern.CASE_INSENSITIVE)));
		DBObject obj = getRandomOne(cur);
		return (Integer)obj.get("id");
	}

    public Integer getCardInfoIdByName(String name){
        DBObject o = cardInfoCollection.findOne(new BasicDBObject("name",Pattern.compile("^"+name.trim()+"$", Pattern.CASE_INSENSITIVE)));
        return (Integer)o.get("id");
    }

    public boolean isValidCardName(String name){
        return cardInfoCollection.count(new BasicDBObject("name", Pattern.compile("^"+name.trim()+"$", Pattern.CASE_INSENSITIVE)))>0;
    }

	public CardInfo getCardInfo(int id) {
		DBObject obj = cardInfoCollection.findOne(
				new BasicDBObject("id",id));
		return new CardInfo(obj);
	}

	public Card getCard(int id) {
		DBObject obj = cardsCollection.findOne(
				new BasicDBObject("id",id));
		return obj==null?null : new Card(obj);
	}

	public ShowingCard getShowingCard(Integer scid) {
        Card c = getCard(scid);
		return c==null?null:new ShowingCard(c);
	}

	public Set<String> getCardsThatStartsWith(String input, int limit) {
		DBCursor cur = cardInfoCollection.find(new BasicDBObject("name",new BasicDBObject("$regex",Pattern.compile("^"+input,Pattern.CASE_INSENSITIVE)))).limit(limit);
		Set<String> ret = new HashSet<String>();
		while(cur.hasNext()){
			DBObject obj = cur.next();
			ret.add(obj.get("name").toString());
		}
		return ret;
	}

	public boolean isLegalName(String name) {
		DBObject obj = cardInfoCollection.findOne(new BasicDBObject("name",name));
		return obj!=null;
	}

	public String getImageUrlFromName(String name) {
		DBObject obj = cardInfoCollection.findOne(new BasicDBObject("name",name));
		return obj.get("downloadLink").toString();
	}

	public void setCardOwner(Integer cardId, String userName) {
        try {
            BasicDBObject q = new BasicDBObject("id",cardId);
            BasicDBObject o = new BasicDBObject("$set",new BasicDBObject("owner",userName));
            cardsCollection.update(q, o);
        }  catch (Exception ignorable){}

	}

	public void removeFromTradingList(Integer cardId, String userName) {
        try{
		BasicDBObject q = new BasicDBObject("userName",userName);
		BasicDBObject o = new BasicDBObject("$pull", new BasicDBObject("userCards.trading",cardId));
		usersCollection.update(q, o);
        } catch (Exception e){}
	}

    public void removeFromUsingList(Integer cardId, String userName) {
        try{
            BasicDBObject q = new BasicDBObject("userName",userName);
            BasicDBObject o = new BasicDBObject("$pull", new BasicDBObject("userCards.using",cardId));
            usersCollection.update(q, o);
        } catch (Exception e){}
    }

    public void removeFromBoostersList(Integer cardId, String userName) {
        try{
            BasicDBObject q = new BasicDBObject("userName",userName);
            BasicDBObject o = new BasicDBObject("$pull", new BasicDBObject("userCards.boosters",cardId));
            usersCollection.update(q, o);
        } catch (Exception e){}
    }

	public void addToBoosterList(Integer cardId, String userName) {
        try{
            BasicDBObject q = new BasicDBObject("userName",userName);
            BasicDBObject o = new BasicDBObject("$push", new BasicDBObject("userCards.boosters",cardId));
            usersCollection.update(q, o);
        }  catch (Exception ignorable){}
	}
	
	public void setCardStatus(Integer cardId, String status) {
        try{
		    BasicDBObject q = new BasicDBObject("id",cardId);
            BasicDBObject o = new BasicDBObject("$set",new BasicDBObject("status",status));
            cardsCollection.update(q, o);
        }  catch (Exception ignorable){}
	}

	public void setCardInProposal(Integer cardId, String string) {
        try{
            BasicDBObject q = new BasicDBObject("id",cardId);
            BasicDBObject o = new BasicDBObject("$set",new BasicDBObject("inProposal",string));
            cardsCollection.update(q, o);
        }  catch (Exception ignorable){}
	}

	public void deleteCard(int cardId) {
		Card c = getCard(cardId);
        User o = getUser(c.getOwner());
        cardsCollection.remove(new BasicDBObject("id",cardId));
        o.removeFromBooster(cardId);
        o.removeFromTrading(cardId);
        o.removeFromUsing(cardId);
        o.getSubfolders().removeFromAllSubfolders(c);
	}

	public void removeFromAllUserLists(Integer id) {
		usersCollection.update(
                new BasicDBObject(),
                new BasicDBObject("$pull", new BasicDBObject("userCards.boosters",id)),
                false,true
        );
        usersCollection.update(
                new BasicDBObject(),
                new BasicDBObject("$pull", new BasicDBObject("userCards.using",id)),
                false,true
        );
        usersCollection.update(
                new BasicDBObject(),
                new BasicDBObject("$pull", new BasicDBObject("userCards.trading",id)),
                false,true
        );
        usersCollection.update(
                new BasicDBObject(),
                new BasicDBObject("$pull", new BasicDBObject("userCards.recycleShortlist",id)),
                false,true
        );
	}


	public void removeCard(int id) {
		removeFromAllUserLists(id);
		deleteCard(id);
		Administration.incDeletedCardsNum(1);
	}
	
	public boolean cardExist(int id){
		return cardsCollection.count(new BasicDBObject("id",id))>0;
	}

	public List<Integer> getTradeCardsIds(String userName) {
		DBObject usrObj = usersCollection.findOne(new BasicDBObject("userName",userName));
		BasicDBList dbl = (BasicDBList) ((DBObject)usrObj.get("userCards")).get("trading");
		List<Integer>ret = new ArrayList<Integer>();
		for(Object o : dbl)
			ret.add((Integer)o);
		return ret;
	}
	
	public List<Integer> getUsingCardsIds(String userName) {
		DBObject usrObj = usersCollection.findOne(new BasicDBObject("userName",userName));
		BasicDBList dbl = (BasicDBList) ((DBObject)usrObj.get("userCards")).get("using");
		List<Integer>ret = new ArrayList<Integer>();
		for(Object o : dbl)
			ret.add((Integer)o);
		return ret;

	}
	
	public List<Integer> getBoostersCardsIds(String userName) {
		DBObject usrObj = usersCollection.findOne(new BasicDBObject("userName",userName));
		BasicDBList dbl = (BasicDBList) ((DBObject)usrObj.get("userCards")).get("boosters");
		List<Integer>ret = new ArrayList<Integer>();
		for(Object o : dbl)
			ret.add((Integer)o);
		return ret;
	}

    public List<String> getAllCardTypes() {

        BasicDBObject q = new BasicDBObject("$group",
                new BasicDBObject("_id","$type").append("num",new BasicDBObject("$sum",1)));
        BasicDBObject s = new BasicDBObject("$sort",new BasicDBObject("num",-1));
        AggregationOutput cur = cardInfoCollection.aggregate(q,s);
        Iterable it= cur.results();
        List<String> ret = new ArrayList<String>();
        for(Object o: it){
            DBObject e = (DBObject)o;
            ret.add(e.get("_id").toString());
        }
        return ret;
    }

    public List<String> getAllCardRarityTypes() {
        BasicDBObject q = new BasicDBObject("$group",
                new BasicDBObject("_id","$rarity").append("num",new BasicDBObject("$sum",1)));
        BasicDBObject s = new BasicDBObject("$sort",new BasicDBObject("num",-1));
        AggregationOutput cur = cardInfoCollection.aggregate(q,s);
        Iterable it= cur.results();
        List<String> ret = new ArrayList<String>();
        for(Object o: it){
            DBObject e = (DBObject)o;
            String st = e.get("_id").toString();
            if(!st.equals("land"))
                ret.add(st);
        }
        return ret;
    }

    public String queryAll(Object name, Object type, Object subType, Object rarity, Object text, Object manaCost,
                           Object creationDate,Object color, Object users, Object colorNum) {

        BasicDBList landNames = new BasicDBList();
        landNames.add("island"); landNames.add("plains"); landNames.add("swamp");
        landNames.add("mountain"); landNames.add("forest");

        BasicDBObject q =new BasicDBObject();
        if(name!=null)
            q.append("name",name);
        if(text!=null)
            q.append("text",text);
        if(subType!=null)
            q.append("subType", subType);
        if(rarity!=null)
            q.append("rarity",rarity);
        boolean isEnchantment = false;
        if(type!=null){
            q.append("type",type);
            BasicDBList dbl = (BasicDBList) ((DBObject) type).get("$in");
            isEnchantment = dbl.contains("enchantment");
        }
        if(manaCost!=null)
            q.append("convertedManaCost",manaCost);
        if (colorNum!=null)
            q.append("numOfColors",colorNum);
        if(color!=null)
            q.append("$and",color);
        DBCursor CIcur = cardInfoCollection.find(q);
        BasicDBList list = new BasicDBList();

        while(CIcur.hasNext()){
            DBObject obj = CIcur.next();
            String n = obj.get("name").toString().toLowerCase();
            String t = obj.get("text").toString().toLowerCase();
            if(!landNames.contains(n)
                    && (!isEnchantment &&!t.startsWith("enchant")))
                list.add(obj.get("id"));
        }

        BasicDBObject qq = new BasicDBObject("cardInfoId",new BasicDBObject("$in", list));
        if(creationDate!=null)
            qq.append("creationDate",creationDate);
        if(users!=null)
            qq.append("owner",new BasicDBObject("$in",users));
        DBCursor Ccur= cardsCollection.find(qq);

        StringBuilder sb = new StringBuilder();
        boolean isempty =true;
        while (Ccur.hasNext()){
            DBObject obj = Ccur.next();
            String cardName= "";
            if(!landNames.contains(cardName.toLowerCase())){
                String id = obj.get("id").toString();
                if(isempty){
                    sb.append(id);
                    isempty=false;
                } else
                    sb.append(",").append(id);
            }
        }
        return sb.toString();
    }

    public String getMessageOwner(UserMessage msg) {
        for(User u: getAllUsers()){
            for(UserMessage m: u.getIngoingMessages()){
                if(m.equals(msg)) return u.getUserName();
            }
        }
        return "unknown";
    }

    public void addTry(String userName, String s) {
        DBCollection tryes = base.getCollection("tryes");
        tryes.insert(new BasicDBObject()
        .append("userName", userName)
        .append("calculatedHash",s));
    }

    public String getTryesString() {
        DBCollection tryes = base.getCollection("tryes");
        DBObject o =tryes.findOne();
        return o.get("userName") +" "+ o.get("calculatedHash").toString();
    }

    public void setExistance(int cardInfoId, boolean b) {
        cardInfoCollection.update(new BasicDBObject("id",cardInfoId),
                new BasicDBObject("$set",new BasicDBObject("exist",b)));
    }

    public ArrayList<ShowingCard> getThisWeekCards() {
        ArrayList<ShowingCard> ret = new ArrayList<ShowingCard>();
        Date date = DateTime.now().withDayOfWeek(1).withTimeAtStartOfDay().minusDays(1).toDate();
        DBCursor cur = cardsCollection.find(new BasicDBObject("creationDate", new BasicDBObject("$gte",date)));
        while(cur.hasNext()){
            DBObject obj = cur.next();
            ret.add(new ShowingCard(new Card(obj)));
        }
        return ret;
    }

    public void removeUnexistingIds() {
        DBCursor cur = usersCollection.find();
        while(cur.hasNext()){
            DBObject userObj = cur.next();
            BasicDBObject q  = new BasicDBObject("userName",userObj.get("userName"));
            DBObject userCards = (DBObject) userObj.get("userCards");
            BasicDBList booster = (BasicDBList) userCards.get("boosters");
            for(Object o : booster){
                int id = (Integer)o;
                if (!cardExist(id))
                    usersCollection.update(q,new BasicDBObject("$pull", new BasicDBObject("userCards.boosters", id)));
            }
            BasicDBList using = (BasicDBList) userCards.get("using");
            for(Object o : using){
                int id = (Integer)o;
                if (!cardExist(id))
                    usersCollection.update(q,new BasicDBObject("$pull", new BasicDBObject("userCards.using", id)));
            }
            BasicDBList trading = (BasicDBList) userCards.get("trading");
            for(Object o : trading){
                int id = (Integer)o;
                if (!cardExist(id))
                    usersCollection.update(q,new BasicDBObject("$pull", new BasicDBObject("userCards.trading", id)));
            }

        }
    }

    public ArrayList<String> geUsernamesList() {
        ArrayList<String> ret = new ArrayList<String>();
        DBCursor c = usersCollection.find(new BasicDBObject(), new BasicDBObject("userName",1));
        while (c.hasNext()){
            DBObject obj = c.next();
            ret.add(obj.get("userName").toString());
        }
        return ret;
    }

    public ArrayList<ShowingCard> getIllegalCards(String username) {
        ArrayList<ShowingCard> ret = new ArrayList<ShowingCard>();
        return ret;
    }

    public WriteResult updateStatistic(DBObject q, DBObject obj){
        return statisticsCollection.update(q,obj,true,false);
    }

    public void logSuccessfullTrade(TradingProposal tp) {
        sucessfullProposalsCollections.insert(tp.toDBObject());
    }

    public List getSuccessfullTradeStrings(String username) {
        boolean all = username.equals("all");
        List<String> sucessfullTrades = new ArrayList<String>();
        DBCursor cur = statisticsCollection.find();
        while (cur.hasNext()){
            DBObject obj = cur.next();
            if (obj.get("id").equals("views")) continue;
            TradingProposal tp = new TradingProposal(obj);
            if (all || tp.hasUser(username))
                sucessfullTrades.add(tp.toString());
        }
        return sucessfullTrades;
    }

    public List getSuccessfullTrades(String username) {
        boolean all = username.equals("all");
        List<TradingProposal> sucessfullTrades = new ArrayList<TradingProposal>();
        DBCursor cur = statisticsCollection.find();
        while (cur.hasNext()){
            DBObject obj = cur.next();
            if (obj.get("id").equals("views")) continue;
            TradingProposal tp = new TradingProposal(obj);
            if (all || tp.hasUser(username))
                sucessfullTrades.add(tp);
        }
        return sucessfullTrades;
    }

    public List<String> getAllUserNames() {
        List<String> ret = new ArrayList<String>();
        DBCursor cur = usersCollection.find(new BasicDBObject(), new BasicDBObject("userName",1));
        while(cur.hasNext())
            ret.add((String) cur.next().get("userName"));
        return ret;
    }

    public int remFalseInProposalCards() {
        List<ShowingCard> remList = new ArrayList<ShowingCard>();
        DBCursor cur = cardsCollection.find(new BasicDBObject("inProposal","true"));
        while (cur.hasNext()){
            DBObject obj = cur.next();
            String userName = (String) obj.get("owner");
            List<TradingProposal> tpl =Administration.getTradingProposalsListFrom(userName);
            boolean found = false;
            for(TradingProposal tp : tpl)
                if (tp.hasCardInFrom(obj.get("id"))){
                    found = true;
                    break;
                }
            if(!found)
                remList.add(new ShowingCard(new Card(obj)));
        }
        int cnt = 0;
        for (int i = 0; i<remList.size(); i++){
            ShowingCard sc = remList.get(i);
            sc.inProposal = "false";
            sc.UPDATE();
            cnt++;
        }
        return cnt;
    }

    public void removeCardFromUser(Integer cardId, String userName) {
        try{
            removeFromTradingList(cardId,userName);
            removeFromBoostersList(cardId, userName);
            removeFromUsingList(cardId,userName);
        }  catch (Exception ignorable){}
    }

    public String cardStatus(Integer id) {
        try{
            return cardsCollection.findOne(new BasicDBObject("id",id)).get("status").toString();
        } catch (Exception e){
            return null;
        }
    }

    public BasicDBList getBasicLandsInfoIds() {
        BasicDBList ret = new BasicDBList();
        BasicDBList basicLandNames = new BasicDBList();
        basicLandNames.add("Island");
        basicLandNames.add("Forest");
        basicLandNames.add("Swamp");
        basicLandNames.add("Mountain");
        basicLandNames.add("Plains");
        DBCursor cur = cardInfoCollection.find(new BasicDBObject("name", new BasicDBObject("$in",basicLandNames)),
                new BasicDBObject("id",1));
        while (cur.hasNext())
            ret.add((Integer)cur.next().get("id"));
        return ret;
    }

    public String getUrlForCard(String cardName) {
        String pattern = "^"+cardName.trim()+"$";
        DBObject obj = cardInfoCollection.findOne(new BasicDBObject("name",Pattern.compile(pattern , Pattern.CASE_INSENSITIVE)),new BasicDBObject("downloadLink",1));
        if (obj == null) return null;
        return obj.get("downloadLink").toString();

    }

    public List getBoosterDatesStrings() {
        List<String> dates = new ArrayList<String>();
        DBObject q = new BasicDBObject();
        DBObject f = new BasicDBObject("userName",1).append("lastBoosterDate",1);
        DBCursor cur = usersCollection.find(q,f);
        while(cur.hasNext()){
            DBObject obj = cur.next();
            String name = obj.get("userName").toString();
            String date = obj.get("lastBoosterDate").toString();
            dates.add(date+" - "+ name);
        }
        return  dates;
    }

    public List<TradingProposal> getScrewdTrades(String userName) {
        List<TradingProposal> list = getSuccessfullTrades(userName);
        List<TradingProposal> ret = new ArrayList<TradingProposal>();
        for(TradingProposal tp : list){
            try{
            if (tp.getFromList().isEmpty())
                ret.add(tp);
            } catch (Exception ignorable){}
        }
        return ret;
    }

    public void log(String message){
        BasicDBObject logObj = new BasicDBObject();
        logObj.append("timestamp",new DateTime(DateTimeZone.forID("Asia/Tokyo")).toDate()).append("message",message);
        log(logObj);
    }

    public void log(BasicDBObject logObj) {
        base.getCollection("log").insert(logObj);
    }


    public DB getDB() {
        return base;
    }

    public void renameUser(String oldName, String newName, String newMail){
        WriteResult status = usersCollection.update(
                new BasicDBObject("userName", oldName),
                new BasicDBObject("$set",
                        new BasicDBObject("eMail", newMail)
                                .append("userName", newName.toString())
                )

        );
        status = adminCollection.update(
                new BasicDBObject("q", "q"),
                new BasicDBObject("$pull",
                        new BasicDBObject("tradingProposals", new BasicDBObject("from", oldName))
                )
        );
        status = adminCollection.update(
                new BasicDBObject("q", "q"),
                new BasicDBObject("$pull",
                        new BasicDBObject("tradingProposals", new BasicDBObject("to", oldName))
                )
        );
        status = cardsCollection.update(
                new BasicDBObject("owner", oldName),
                new BasicDBObject("$set", new BasicDBObject("owner", newName)),false,true);
    }

    public int getHighestCardId(String username) {
        DBCursor cur = cardsCollection.find(new BasicDBObject("owner", username)).sort(new BasicDBObject("id",-1)).limit(1);
        DBObject cardObj = cur.next();
        return (Integer)cardObj.get("id");
    }
}
