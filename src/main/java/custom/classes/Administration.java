package custom.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import database.MongoHandler;

public class Administration  {

	static MongoHandler mongo = MongoHandler.getInstance();
	
	public static void addToAuthorizationList(User user) {
		DBObject obj = mongo.getAdminObject();
		Object o = obj.get("authList");
		BasicDBList list = o==null?new BasicDBList():(BasicDBList)o;
		list.add(user.toDBObject());
		obj.put("authList", list);
		UPDATE(obj);
	}
	
	public static void removeFromAuthorizationList(User user) {
		DBObject obj = mongo.getAdminObject();
		Object o = obj.get("authList");
		BasicDBList list = o==null?new BasicDBList():(BasicDBList)o;
		Object rem=null;
		for(Object lo: list)
			if(((DBObject)lo).get("userName").toString().equals(user.userName))
				rem = lo;
		list.remove(rem);
		obj.put("authList", list);
		UPDATE(obj);
	}

	public static List<User> getAuthorizationList() {
		DBObject obj = mongo.getAdminObject();
		Object o = obj.get("authList");
		BasicDBList list = o==null?new BasicDBList():(BasicDBList)o;
		return BDL2UsrL(list);
	}

	private static DBObject getQ() {
		return new BasicDBObject("q","q");
	}

	private static List<User> BDL2UsrL(BasicDBList list) {
		List<User> ret =  new ArrayList<User>();
		if(list==null) return ret;
		for(Object o: list){
			DBObject obj = (DBObject)o;
			ret.add(new User(obj));
		}
		return ret;
	}

	public static void insertUsers(ArrayList<User> users) {
		for(User u: users){
			u.UPDATE();
			removeFromAuthorizationList(u);
		}
	}

	public static int getNextCardId() {
		DBObject obj = mongo.getAdminObject();
		Object o = obj.get("maxCardId");
		Integer id = o==null? 1 : ((Integer)o)+1;
		obj.put("maxCardId",id);
		UPDATE(obj);
		return id;
	}

	private static void UPDATE(DBObject obj) {
		mongo.adminCollection.update(getQ(), obj);
	}
	

	public static void sendToPrinter(ShowingCard sc){
		sendToPrinter(Arrays.asList(new ShowingCard[]{sc}));
	}

	public static void sendToPrinter(List<ShowingCard> list) {
		List<Integer> idList = new ArrayList<Integer>();
		for(ShowingCard sc : list){
			idList.add(sc.cardId);
			sc.printed="pending";
			sc.UPDATE();
		}
		DBObject obj = mongo.getAdminObject();
		BasicDBList DBlist = (BasicDBList)obj.get("printList");
		if(DBlist==null) DBlist = new BasicDBList();
		Set<Integer> set = DBL2IntSet(DBlist);
		set.addAll(idList);
		BasicDBList dbl = IntSet2DBL(set);
		obj.put("printList", dbl);
		UPDATE(obj);
	}

	public static BasicDBList IntSet2DBL(Set<Integer> set) {
		BasicDBList ret = new BasicDBList();
		for(Integer i: set)
			ret.add(i);
		return ret;
	}

	public static Set<Integer> DBL2IntSet(Object object) {
		Set<Integer> ret = new HashSet<Integer>();
		if(object == null) 
			return ret;
		BasicDBList list = (BasicDBList) object;
		for(Object o: list){
			Integer i = (Integer) o;
			ret.add(i);
		}
		return ret;
	}

	public static void removeFromPrinter(List<ShowingCard> prepared) {
		
		DBObject obj =  mongo.getAdminObject();
		BasicDBList list = (BasicDBList)obj.get("printList");
		if(list==null) return;
		Set<Integer> s = new HashSet<Integer>();
		for(Object o: list)
			s.add((Integer)o);
		for(ShowingCard sc : prepared)
			s.remove(sc.cardId);
		BasicDBList ret = new BasicDBList();
		for(Object i : s)
			ret.add(i);
		obj.put("printList", ret);
		UPDATE(obj);
	}
	
	public static List<ShowingCard> getPrintingReadyList(){
		BasicDBList ids = (BasicDBList) mongo.getAdminObject().get("printList");
		if(ids==null) return new ArrayList<ShowingCard>();
		List<ShowingCard> ret = new ArrayList<ShowingCard>();
		for(Object obj : ids){
			Integer scid  =(Integer)obj;
			ShowingCard sc = mongo.getShowingCard(scid);
			ret.add(sc);
		}
		return ret;
	}
	
	public static Integer getPrintingReadyNum(){
		BasicDBList ids = (BasicDBList) mongo.getAdminObject().get("printList");
		if(ids==null) return 0;
		return ids.size();
	}
	
	public static Integer getNextTradeProposalId(){
		DBObject obj = mongo.getAdminObject();
		Integer x = (Integer)obj.get("maxTradeProposalId");
		if(x==null) x=0;
		x++;
		obj.put("maxTradeProposalId", x);
		UPDATE(obj);
		return x;
	}

	public static List<Integer> getTradingProposalsListIds() {
		DBObject obj = mongo.getAdminObject();
		BasicDBList dbl = (BasicDBList) obj.get("tradingProposals");
		if(dbl == null)
			dbl = new BasicDBList();
		return DBl2IntLsit(dbl);
	}
	
	private static List<Integer> DBl2IntLsit(BasicDBList dbl) {
		List<Integer> list = new ArrayList<Integer>();
		for(Object o : dbl){
			DBObject obj = (DBObject) o;
			Integer i = (Integer) obj.get("id");
			list.add(i);
		}
		return list;
	}

	@SuppressWarnings("unused")
	private static List<TradingProposal> DBl2TPlTo(BasicDBList dbl) {
		List<TradingProposal> list = new ArrayList<TradingProposal>();
		for(Object o : dbl){
			DBObject obj = (DBObject) o;
			TradingProposal tp = new TradingProposal(obj);
			if(tp.isValid()){
					list.add(tp);
			}
			else 
				removeFromTradingProposalList(tp);
		}
		return list;
	}

	public static List<TradingProposal> getTradingProposalsListTo(String userName){
		DBObject obj = mongo.getAdminObject();
		BasicDBList dbl = (BasicDBList) obj.get("tradingProposals");
		if(dbl == null)
			dbl = new BasicDBList();
		return DBl2TPlTo(dbl, userName); //filtered
	}
	
	public static List<TradingProposal> getTradingProposalsListFrom(String userName){
		DBObject obj = mongo.getAdminObject();
		BasicDBList dbl = (BasicDBList) obj.get("tradingProposals");
		if(dbl == null)
			dbl = new BasicDBList();
		return DBl2TPlFrom(dbl, userName); //filtered
	}

	/**
	 * alseo validate and filter.
	 * @param dbl
	 * @return
	 */
	private static List<TradingProposal> DBl2TPlFrom(BasicDBList dbl,
			String userName) {
		List<TradingProposal> list = new ArrayList<TradingProposal>();
		for(Object o : dbl){
			DBObject obj = (DBObject) o;
			TradingProposal tp = new TradingProposal(obj);
			if(tp.isValid()){
				if(tp.from.equals(userName))
					list.add(tp);
			}
			else 
				removeFromTradingProposalList(tp);
		}
		return list;
	}

	/**
	 * alseo validate and filter.
	 * @param dbl
	 * @return
	 */
	private static List<TradingProposal> DBl2TPlTo(BasicDBList dbl, String userName) {
		List<TradingProposal> list = new ArrayList<TradingProposal>();
		for(Object o : dbl){
			DBObject obj = (DBObject) o;
			TradingProposal tp = new TradingProposal(obj);
			if(tp.isValid()){
				if(tp.to.equals(userName))
					list.add(tp);
			}
			else 
				removeFromTradingProposalList(tp);
		}
		return list;
	}
	
	public static void addToTradingProposalList(TradingProposal tp){
		DBObject obj = mongo.getAdminObject();
		BasicDBList dbl = (BasicDBList) obj.get("tradingProposals");
		if (dbl==null) dbl = new BasicDBList();
		dbl.add(tp.toDBObject());
		obj.put("tradingProposals", dbl);
		UPDATE(obj);
		tp.setCardsInProposal(true);
	}
	public static void removeFromTradingProposalList(TradingProposal tp){
		DBObject obj = mongo.getAdminObject();
		BasicDBList dbl = (BasicDBList) obj.get("tradingProposals");
		DBObject remo=null;
		for(int i = 0 ; i<dbl.size();i++){
			DBObject tpo = (DBObject) dbl.get(i);
			Integer id = (Integer)(tpo.get("id"));
			if(id.equals(tp.id)){
				remo=tpo;
				break;
			}
		}
		if(remo!=null)
			dbl.remove(remo);
		obj.put("tradingProposals", dbl);
		UPDATE(obj);
		tp.setCardsInProposal(false);
		tp.UPDATE();
	}

	public static int getMaxCardId() {
		DBObject obj = mongo.getAdminObject();
		Object o = obj.get("maxCardId");
		Integer id = o==null? 1 : ((Integer)o);
		return id;
	}
	
	public static Integer getDeletedCardsNum(){
		DBObject obj = mongo.getAdminObject();
		Object o = obj.get("deletedCardsNum");
		if(o==null) return 0;
		if(o instanceof Double)
			return ((Double)o).intValue();
		return (Integer)o;
	}

	public static void setDeletedCardsNum(int num){
		mongo.adminCollection.update(getQ(), 
				new BasicDBObject("$set", new BasicDBObject("deletedCardsNum",num)));
	}
	
	public static void incDeletedCardsNum(int inc){
		setDeletedCardsNum(getDeletedCardsNum()+inc);
	}

    public static void addProblematicId(Integer id) {
        mongo.adminCollection.update(getQ(),
                new BasicDBObject("$push", new BasicDBObject("problematicIds",id)));
        User me = mongo.getUser("Obee");
        me.addMessage(new UserMessage(me.getNextMessageId(), "New problematic id", "{id:"+id+"}"));
        me.UPDATE();
    }
}
