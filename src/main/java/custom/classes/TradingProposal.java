package custom.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import custom.classes.abstractClasses.MongoObject;

@SuppressWarnings("serial")
public class TradingProposal extends MongoObject implements Serializable{

	int id;
	String from, to;
	Date expireDate;
	Set<Integer> fromList, toList;
	
	
	
	public TradingProposal(String from, String to,
			Set<Integer> fromList, Set<Integer> toList) {
		super();
		this.id = Administration.getNextTradeProposalId();
		this.from = from;
		this.to = to;
		this.expireDate = new DateTime().plusDays(5).toDate();
		this.fromList = fromList;
		this.toList = toList;
	}
	
	public TradingProposal(String from, String to,
			List<ShowingCard> fromList,List<ShowingCard> toList) {
		super();
		this.id = Administration.getNextTradeProposalId();
		this.from = from;
		this.to = to;
		this.expireDate = new DateTime().plusDays(10).toDate();
		this.fromList = new HashSet<Integer>();
		this.toList = new HashSet<Integer>();
		for(ShowingCard sc : fromList){
			this.fromList.add(sc.cardId);
		}
		for(ShowingCard sc : toList){
			this.toList.add(sc.cardId);
		}
	}

	public TradingProposal(DBObject obj) {
		id = (Integer)obj.get("id");
		from = obj.get("from").toString();
		to = obj.get("to").toString();
		expireDate = (Date) obj.get("expireDate");
		fromList =  Administration.DBL2IntSet(obj.get("fromList"));
		toList =  Administration.DBL2IntSet(obj.get("toList"));
	}

	@Override
	public DBObject toDBObject() {
		BasicDBObject obj = new BasicDBObject()
			.append("id", id)
		 	.append("from", from)
		 	.append("to", to)
		 	.append("expireDate", expireDate)
		 	.append("fromList", Administration.IntSet2DBL(fromList))
		 	.append("toList", Administration.IntSet2DBL(toList));
		return obj;
	}

	@Override
	public void UPDATE() {
		
	}

	@Override
	public BasicDBObject getQ() {
		return new BasicDBObject("id",id);
	}
	
	public Boolean isValid(){
		DateTime now = new DateTime();
		DateTime expire = new DateTime(expireDate);
		if(!Administration.getTradingProposalsListIds().contains(this.id)){
			return false;
		}
		if (expire.isBefore(now)) 
			return false;
		User fromU = mongo.getUser(this.from);
		if(!stillGot(fromU,fromList))
			return false;
		User toU = mongo.getUser(this.to);
		if(!stillGot(toU,toList))
			return false;
		return true;
	}

	private boolean stillGot(User u, Set<Integer> list) {
		for(Integer id: list){
			Card c =mongo.getCard(id);
            if(c==null){
                Administration.addProblematicId(id);
                return false;
            }
			if(!c.owner.equals(u.userName))
				return false;
		}
		return true;
	}

	public int getId() {
		return id;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public List<ShowingCard> getFromList() {
		return IntSet2SCl(fromList);
	}

	public List<ShowingCard> getToList() {
		return IntSet2SCl(toList);
	}

	private List<ShowingCard> IntSet2SCl(Set<Integer> set) {
		List<ShowingCard> ret = new ArrayList<ShowingCard>();
		for(Integer id: set ){
			Card c = mongo.getCard(id);
			ret.add(new ShowingCard(c));
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return "From "+from +" to " +to;
	}

	public void setCardsInProposal(String str) {
		for(ShowingCard sc : getFromList()){
			sc.inProposal=str;
			sc.UPDATE();
		}
	}
	
	public void setCardsInProposal(Boolean b) {
		setCardsInProposal(b.toString().toLowerCase());
	}

	public boolean doTrade() {
		User from = mongo.getUser(this.from);
		User to = mongo.getUser(this.to);
		if(!isValid()) return false;
		for(ShowingCard sc: getFromList()){
			sc.trade(from, to);
		}
		for(ShowingCard sc: getToList()){
			sc.trade(to, from);
		}
		return true;
	}

    public boolean hasUser(String username){
        return (from.equals(username) || to.equals(username));
    }


    public boolean hasCardInFrom(Object id) {
        return fromList.contains(id);
    }
}
