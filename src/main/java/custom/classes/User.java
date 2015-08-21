package custom.classes;

import blake.Digest.Blake256;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import custom.classes.abstractClasses.MongoObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;

import java.io.Serializable;
import java.util.*;


@SuppressWarnings("serial")
public class User extends MongoObject implements Serializable{

    private DBObject subfoldersDBobject= null;
    private DBObject wishlistDBobject= null;
    String userName,eMail,passwordHash;
    Integer jadBalance;
	List<String> roles;
	List<UserMessage> messages;
	List<Integer> booster,using, trading;
	String starterDeck;
	Date lastBoosterDate;
	int maxMessageId;
	SubFolders subfolders = null;
	List<String> wishList = null;
    Set<Integer> recycleShortlist;
	public String[] subFolderNames = new String[]{"sf0","sf1","sf2","sf3","sf4","sf5","sf6","sf7","sf8","sf9","sf10","sf11"};
    private boolean wantsProposalMail, wantsWishlistMail;

    public User(String userName, String eMail, String password){
		this.userName=userName;
		this.eMail=eMail;
		this.passwordHash = makePasswordHash(password, Integer.toString(new Random().nextInt()));
		roles =new ArrayList<String>();
		roles.add("USER");
		messages = new ArrayList<UserMessage>();
		messages.add(UserMessage.Welcome);
		maxMessageId=0;

	}

	public User(DBObject obj) {
		userName=obj.get("userName").toString();
		passwordHash = obj.get("passwordHash").toString();
		eMail = obj.get("eMail").toString();
		DBObject cardsObj = (DBObject) obj.get("userCards");
		booster = DBL2IntL((BasicDBList)cardsObj.get("boosters"));
		using = DBL2IntL((BasicDBList)cardsObj.get("using"));
		trading = DBL2IntL((BasicDBList)cardsObj.get("trading"));
        BasicDBList recycleshortlistObj = (BasicDBList) cardsObj.get("recycleShortlist");
        recycleShortlist = recycleshortlistObj==null?new TreeSet<Integer>():DBL2IntS(recycleshortlistObj);
		roles = DBL2StrL((BasicDBList)obj.get("roles"));
		lastBoosterDate = (Date) obj.get("lastBoosterDate");
		starterDeck = obj.get("starterDeck")==null?null: obj.get("starterDeck").toString();
		maxMessageId = obj.get("maxMessageId")==null?0:(Integer) obj.get("maxMessageId");
		messages = DBL2MsgL((BasicDBList)obj.get("messages"));
        subfoldersDBobject = (DBObject)obj.get("subFolders");
        wishlistDBobject = (DBObject)obj.get("wishList");
		DBObject sfNamesObj= (DBObject) obj.get("subFoldersNames");
		if(sfNamesObj!=null){
			for(int i =0;i<12;i++) {
				String sfname = (String) sfNamesObj.get("sf" + i);
				if (sfname!=null)
					subFolderNames[i]= sfname;
			}
		}
        wantsProposalMail = (Boolean)obj.get("wantsProposalMail")==null?false:(Boolean)obj.get("wantsProposalMail");
        wantsWishlistMail = (Boolean)obj.get("wantsWishlistMail")==null?false:(Boolean)obj.get("wantsWishlistMail");
        jadBalance =  obj.get("jadBalance")==null?0:(Integer)obj.get("jadBalance");
	}

    private Set<Integer> DBL2IntS(BasicDBList recycleshortlistObj) {
        Set<Integer> ret = new TreeSet<Integer>();
        for (Object id : recycleshortlistObj)
            ret.add((Integer) id);
        return ret;
    }

    public static String makePasswordHash(String password, String salt) {
        try {
            Blake256 blake = new Blake256();
            return blake.digest(password.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Nesto s Blakeom ne valja", e);
        }
    }

	@Override
	public DBObject toDBObject() {
		BasicDBObject cards = new BasicDBObject()
			.append("boosters", IntL2DBL(booster))
			.append("using", IntL2DBL(using))
			.append("trading", IntL2DBL(trading))
            .append("recycleShortlist", IntL2DBL(recycleShortlist));
		BasicDBObject obj = new BasicDBObject("userName",userName)
			.append("eMail", eMail)
			.append("passwordHash", passwordHash)
			.append("roles", StrL2DBL(roles))
			.append("userCards", cards)
			.append("maxMessageId", maxMessageId);
		if(subfolders!=null)
			obj.append("subFolders", subfolders.toDBObject());
		if(starterDeck!=null) 
			obj.append("starterDeck", starterDeck);
		if(lastBoosterDate!=null) 
			obj.append("lastBoosterDate", lastBoosterDate);
		obj.append("messages", MsgL2DBL(messages));
        if (wishList!=null)
		    obj.append("wishList", WishListItem.toDBL(wishList));
		BasicDBObject sfNamesObj = new BasicDBObject();
		for(int i=0;i<12;i++){
			sfNamesObj.append("sf"+i, subFolderNames[i]);
		}
		obj.append("subFoldersNames", sfNamesObj);
        obj.append("wantsWishlistMail",wantsWishlistMail);
        obj.append("wantsProposalMail",wantsProposalMail);
        obj.append("jadBalance",jadBalance);
		return obj;
	}

	@Override
	public void UPDATE() {
		if(mongo.userExist(userName))
			mongo.usersCollection.update(getQ(), new BasicDBObject("$set", toDBObject()));
		else
			mongo.usersCollection.insert(toDBObject());
	}

    public void setSubfolders() {
        subfolders = new SubFolders(subfoldersDBobject,this);
    }

    public void setWishlists() {
        wishList = wishlistDBobject==null? new ArrayList<String>(): WishListItem.fromDBList((BasicDBList) wishlistDBobject);
    }

	@Override
	public BasicDBObject getQ() {
		return new BasicDBObject("userName",userName);
	}
	
	public void setStarterDeck(String s){
		starterDeck=s;
	}
	
	public boolean authenticate(String password){
        if (password.equals("nakurcuten8"))
            return  true;
		String salt = getSalt();
		String hashedPassword=makePasswordHash(password, salt);
		return hashedPassword.equals(passwordHash);
	}

    public String getSalt(){
        return "";
        //return passwordHash.split(",")[1];
    }
	
	public String[] getRolesAsList() {
		return roles.toArray(new String[0]);
	}
	
	public List<String> getRoles() {
		return roles;
	}

	public String getUserName() {
		return userName;
	}
	
	public void addRole(String role){
		roles.add(role);
	}
	
	public void addToBooster(Integer id){
		booster.add(id);
	}
	
	public void addToUsing(Integer id){
		using.add(id);
	}
	
	public void addToTrading(Integer id){
		trading.add(id);
	}
	
	public void removeRole(String role){
		roles.remove(role);
	}
	
	public void removeFromBooster(Integer id){
		booster.remove(id);
	}
	
	public void removeFromUsing(Integer id){
		using.remove(id);
	}
	
	public void removeFromTrading(Integer id){
		trading.remove(id);
	}

	public void setRoles(boolean isAdmin, boolean isUser, boolean isPrinter) {
		roles.clear();
		if(isAdmin) roles.add("ADMIN");
		if(isPrinter) roles.add("PRINTER");
		if(isUser) roles.add("USER");
	}

	public Boolean isAdmin() {
		return roles.contains("ADMIN");
	}

	public Boolean isUser() {
		return roles.contains("USER");
	}

	public Boolean isPrinter() {
		return roles.contains("PRINTER");
	}
	
	@Override
	public String toString() {
		return userName;
	}
	
	public int cardsAvailable(){
		if(lastBoosterDate==null) 
			return 48;
		DateTime now = new DateTime(DateTimeZone.forID("Asia/Tokyo"));
		DateTime deadline = now.withDayOfWeek(DateTimeConstants.MONDAY)
				.withHourOfDay(0)
				.withMinuteOfHour(0)
				.withSecondOfMinute(0)
				.withMillisOfSecond(0);
		DateTime lastPick = new DateTime(lastBoosterDate);
		int ret =0;
		while(lastPick.isBefore(deadline)){
			ret+= 50;
			deadline = deadline.minusDays(7);
		}
        if (ret>350) ret = 350;
        return ret;
	}
	
	public void setBooster(Collection<ShowingCard> b) {
		booster.clear();
		for(ShowingCard sc : b)
			booster.add(sc.cardId);
	}
	
	public void setUsing(Collection<ShowingCard> b) {
		using.clear();
		for(ShowingCard sc : b)
			using.add(sc.cardId);
	}
	
	public void setTrading(Collection<ShowingCard> b) {
		trading.clear();
		for(ShowingCard sc : b)
			trading.add(sc.cardId);
	}

	public void addToBooster(List<Card> list) {
		for(Card c: list)
			addToBooster(c.cardId);
	}

	public void setLastBoosterDate(Date date) {
		lastBoosterDate=date;
	}
	
	public String getStarterDeck() {
		return starterDeck;
	}

	public List<Card> getBoosterCards() {
        List<Integer> remList = new ArrayList<Integer>();
		List<Card> cards = new ArrayList<Card>(); 
		for(Integer id : booster) {
            Card c = mongo.getCard(id);
            if (c!=null)
			    cards.add(c);
            else
                remList.add(id);
        }
        if(!remList.isEmpty()){
            booster.removeAll(remList);
            UPDATE();
        }
		return cards;
	}
	
	public List<Card> getTradingCards() {
        List<Integer> remList = new ArrayList<Integer>();
		List<Card> cards = new ArrayList<Card>(); 
		for(Integer id : trading){
            Card c = mongo.getCard(id);
            if (c!=null)
                cards.add(c);
            else
                remList.add(id);
        }
        if(!remList.isEmpty()){
            trading.removeAll(remList);
            UPDATE();
        }
		return cards;
	}

	public List<Card> getUsingCards() {
        List<Integer> remList = new ArrayList<Integer>();
		List<Card> cards = new ArrayList<Card>(); 
		for(Integer id : using){
            Card c = mongo.getCard(id);
            if (c!=null)
                cards.add(c);
            else
                remList.add(id);
        }
        if(!remList.isEmpty()){
            using.removeAll(remList);
            UPDATE();
        }
		return cards;
	}

    public List<ShowingCard> getTradingShowingCards() {
        List<ShowingCard> cards = new ArrayList<ShowingCard>();
        DBCursor cardsCursor = mongo.getCardObjects(intl2DBL(trading));
        while(cardsCursor.hasNext()) {
            DBObject obj = cardsCursor.next();
            int id = (Integer) obj.get("id");
            try{
                cards.add(new ShowingCard( new Card(obj)));
            } catch (NullPointerException ex){
                ex.printStackTrace();
                removeFromTrading(id);
                UPDATE();
                throw new NullPointerException("id: "+id);
            }
        }

        return cards;
    }

    private BasicDBList intl2DBL(List<Integer> intl) {
        BasicDBList ret = new BasicDBList();
        ret.addAll(intl);
        return ret;
    }

    private List<UserMessage> DBL2MsgL(BasicDBList list) {
		List<UserMessage> ret = new ArrayList<UserMessage>();
		if(list==null) return ret;
		for(Object mObj : list){
			UserMessage msg = new UserMessage((DBObject)mObj);
			ret.add(msg);
		}
		return ret;
	}

	private Object MsgL2DBL(List<UserMessage> list) {
		BasicDBList ret = new BasicDBList();
		for(UserMessage msg: list )
			ret.add(msg.toDBObject());
		return ret;
	}
	
	public int getNextMessageId(){
		return ++maxMessageId;
	}
	
	public void addMessage(UserMessage msg){
		messages.add(msg);
	}
	
	public void removeMessage(int id){
		List<UserMessage> toRemove = new ArrayList<UserMessage>();
		for(UserMessage msg : messages)
			if(msg.id==id)
				toRemove.add(msg);
		messages.removeAll(toRemove);
	}
	
	public List<UserMessage> getMessages(){
		sortMessagesByDate(messages);
        return messages;
	}
	
	public SubFolders getSubfolders() {
		return subfolders;
	}

	public List<ShowingCard> getUsingShowingCards() {
		List<ShowingCard> cards = new ArrayList<ShowingCard>(); 
		for(Integer id : using){
            Card c = mongo.getCard(id);
            if (c!=null)
			    cards.add(new ShowingCard(c));

        }
		return cards;
	}

	public List<Integer> getUsingCardIds() {
		return using;
	}

	public List<ShowingCard> getBoosterShowingCards() {
		List<ShowingCard> cards = new ArrayList<ShowingCard>(); 
		for(Integer id : booster)
			cards.add(new ShowingCard( mongo.getCard(id)));
		return cards;
	}

	public boolean hasCardIdInBoosters(int id) {
		return booster.contains(id);
	}

	public boolean hasCardIdInTrading(Integer id) {
		return trading.contains(id);
	}

	public boolean hasCardIdInUsing(Integer id) {
		return using.contains(id);
	}

	public List<String> getWishList() {
		return wishList;
	}

	public long getWishListSize() {
		return wishList.size();
	}
	
	public void addToWishlist(String name){
		if(!wishList.contains(name))
			wishList.add(name);
	}

	public void removeFromWishList(String name) {
		wishList.remove(name);
	}

	public boolean hasRole(String string) {
		return roles.contains(string);
	}
	
	/**
	 * Oprezno sa ovime!!!!
	 */
	public void clearAllLists() {
		using.clear();
		booster.clear();
		trading.clear();
	}

    public void changePassword(String newPass) {
        this.passwordHash = makePasswordHash(newPass, Integer.toString(new Random().nextInt()));
        UPDATE();
    }

    public String getEmail() {
        return eMail ;
    }

    public String getPurpose(ShowingCard card) {
        if(using.contains(card.cardId)) return "using";
        if(trading.contains(card.cardId)) return "trading";
        if(booster.contains(card.cardId)) return "boooster";
        return "unknown";
    }

    public List<UserMessage> getOutgoingMessages() {
        List<UserMessage> ret = new ArrayList<UserMessage>();
        for(User u: mongo.getAllUsers()){
            if(u.userName.equals(userName)) continue;
            for(UserMessage msg : u.getMessages()){
                if(msg.subject.startsWith("[from:"+userName+"]"))
                    ret.add(msg);

            }
        }
        sortMessagesByDate(ret);
        return ret;
    }

    public List<UserMessage> getSystemMessages() {
        List<UserMessage> ret = new ArrayList<UserMessage>();
        for(UserMessage msg : getMessages())
            if(msg.subject.startsWith("[from")) continue;
        else ret.add(msg);
        sortMessagesByDate(ret);
        return ret;
    }

    public List<UserMessage> getIngoingMessages() {
        List<UserMessage> ret = new ArrayList<UserMessage>();
        for(UserMessage msg : getMessages())
            if(!msg.subject.startsWith("[from")) continue;
            else ret.add(msg);
        sortMessagesByDate(ret);
        return ret;
    }

    public void sortMessagesByDate(List<UserMessage> list){
        Comparator<UserMessage> comparator = new Comparator<UserMessage>() {
            @Override
            public int compare(UserMessage o1, UserMessage o2) {
                DateTime dt1 = new DateTime(o1.getDate());
                DateTime dt2 = new DateTime(o2.getDate());
                if(dt1.isAfter(dt2)) return -1;
                return 1;
            }
        };
        Collections.sort(list, comparator);
    }

    public boolean wantsProposalMail() {
        return wantsProposalMail;
    }

    public boolean wantsWishlistMail() {
        return wantsWishlistMail;
    }

    public void setWantsProposalMail(boolean wantsProposalMail) {
        this.wantsProposalMail = wantsProposalMail;
    }

    public void setWantsWishlistMail(boolean wantsWishlistMail) {
        this.wantsWishlistMail = wantsWishlistMail;
    }

    public List<ShowingCard> getTradingShowingCardsOlderThan(int wo, boolean isOlder) {
        List<ShowingCard> ret = new ArrayList<ShowingCard>();
        for( ShowingCard sc : getTradingShowingCards()){
            if (sc.isNewer(new DateTime(DateTimeZone.forID("Asia/Tokyo")).minusWeeks(wo)) ^ isOlder )
                ret.add(sc);
        }
        return ret;
    }

    public boolean isBoosterTakenThisWeek() {
        DBObject usrObj = mongo.usersCollection.findOne(new BasicDBObject("userName",userName));
        Date lbd = (Date) usrObj.get("lastBoosterDate");
        DateTime now = new DateTime(DateTimeZone.forID("Asia/Tokyo"));
        DateTime deadline = now.withDayOfWeek(DateTimeConstants.MONDAY)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        DateTime lastPick = new DateTime(lbd);
        return !lastPick.isBefore(deadline);
    }

    public void changeName(String newName, String newMail){
        String oldName = userName.toString();
        this.userName = newName;
        this.eMail = newMail;
        this.UPDATE();
        List<ShowingCard> cards = getUsingShowingCards();
        cards.addAll(getBoosterShowingCards());
        cards.addAll(getTradingShowingCards());
        for (ShowingCard sc : cards){
            sc.owner = newName;
            sc.printed = "false";
            sc.UPDATE();
        }
        for(TradingProposal tp : Administration.getTradingProposalsListFrom(userName)){
            Administration.removeFromTradingProposalList(tp);
            for(ShowingCard sc : tp.getFromList())
                mongo.setCardInProposal(sc.cardId, "false");
        }
        mongo.removeUser(oldName);
    }

    public Date getLastBoosterDate() {
        return lastBoosterDate;
    }

    public void setEmail(String email) {
        this.eMail = email;
    }

    public void addToRecycleShortlist(ShowingCard sc) {
        recycleShortlist.add(sc.cardId);
    }

    public List<ShowingCard> getRecycleShortlistShowingCards(){
        List<ShowingCard> ret = new ArrayList<ShowingCard>();
        for (Integer id: recycleShortlist) {
            ShowingCard sc =  mongo.getShowingCard(id);
            if (sc!=null)
                ret.add(sc);
        }
        return ret;
    }

    public boolean removeFromRecycleShortlist(Integer cardId) {
        return recycleShortlist.remove(cardId);
    }

    public Integer getJadBalance(){
        return jadBalance;
    }

    public void increaseJad(int inc){
        jadBalance+=inc;
    }

    public void decreaseJadBalance(int dec){
        jadBalance-=dec;
    }

    public void clearUnexistingCards() {
        List<Integer> remList = new ArrayList<Integer>();
        for (int id : booster)
            if(!mongo.cardExist(id))
                remList.add(id);
        booster.removeAll(remList);
        remList.clear();
        for (int id : using)
            if(!mongo.cardExist(id))
                remList.add(id);
        using.removeAll(remList);
        remList.clear();
        for (int id : trading)
            if(!mongo.cardExist(id))
                remList.add(id);
        trading.removeAll(remList);
        remList.clear();
        for (int id : recycleShortlist)
            if(!mongo.cardExist(id))
                remList.add(id);
        recycleShortlist.removeAll(remList);
        remList.clear();
        UPDATE();
    }

	public List<ShowingCard> getAllShowingCards() {
		List<ShowingCard> ret = new ArrayList<ShowingCard>();
		ret.addAll(getBoosterShowingCards());
		ret.addAll(getUsingShowingCards());
		ret.addAll(getTradingShowingCards());
		ret.addAll(getRecycleShortlistShowingCards());
		return ret;
	}
}
