package custom.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import custom.classes.abstractClasses.MongoObject;

@SuppressWarnings("serial")
public class WishListItem extends MongoObject implements Serializable, Comparable<WishListItem> {

	String name;
	List<String> booster, using, trading;
	
	public WishListItem(String name, BasicDBList cards) {
		initLists();
        for (Object o: match(name,cards)){
            DBObject obj = (DBObject) o;
            String status = obj.get("status").toString();
            String owner = obj.get("owner").toString();
            if(status.equals("boosters")|| status.equals("booster"))
                booster.add(owner);
            else if (status.equals("trading"))
                trading.add(owner);
            else if (status.equals("using"))
                using.add(owner);
        }
//		List<Integer> CIids =new ArrayList<Integer>();
//		DBCursor cur = mongo.cardInfoCollection.find(new BasicDBObject("name",name));
//		while(cur.hasNext())
//			CIids.add((Integer)cur.next().get("id"));
//		List<Integer> Cids = new ArrayList<Integer>();
//		for(Integer id : CIids){
//			DBCursor c = mongo.cardsCollection.find(new BasicDBObject("cardInfoId",id));
//			while(c.hasNext())
//				Cids.add((Integer) c.next().get("id"));
//		}
//		for(Integer id : Cids){
//			for(User u: allUsrs){
//				if(u.hasCardIdInBoosters(id))
//					booster.add(u);
//				if(u.hasCardIdInTrading(id))
//					trading.add(u);
//				if(u.hasCardIdInUsing(id))
//					using.add(u);
//			}
//		}
		this.name=name;
	}

    private BasicDBList match(String name, BasicDBList cards) {
        BasicDBList ret = new BasicDBList();
        for (Object o : cards){
            DBObject obj = (DBObject) o;
            DBObject info = (DBObject) obj.get("info");
            if(info.get("name").toString().equals(name))
                ret.add(obj);
        }
        return ret;
    }


    private void initLists() {
		using = new ArrayList<String>();
		trading= new ArrayList<String>();
		booster= new ArrayList<String>();
	}

	@Override
	public DBObject toDBObject() {
		return null;
	}

	@Override
	public void UPDATE() {

	}

	@Override
	public BasicDBObject getQ() {
		return null;
	}

	public static ArrayList<String> fromDBList(BasicDBList dbl) {
		ArrayList<String> ret = new ArrayList<String>();
		if(dbl==null) return ret;
		for(Object o: dbl){
			String s = (String) o;
			ret.add(s);
		}
		return ret;
	}

	public static Object toDBL(List<String> list) {
		BasicDBList dbl = new BasicDBList();
		if(list==null) return dbl;
		for(String li: list){
			dbl.add(li);
		}
		return dbl;
	}

	public String getName() {
		return name;
	}
	
	public String getBoosterString(){
		StringBuilder sb = new StringBuilder();
		boolean isFirst =true;
		for(String u : booster){
			String s = u;
			if(!isFirst)
				sb.append(", ");
			else 
				isFirst=false;
			sb.append(s);
		}
		return sb.toString();
	}
	
	public String getUsingString(){
		StringBuilder sb = new StringBuilder();
		boolean isFirst =true;
		for(String s : using){
			if(!isFirst)
				sb.append(", ");
			else 
				isFirst=false;
			sb.append(s);
		}
		return sb.toString();
	}
	
	public String getTradingString(){
		StringBuilder sb = new StringBuilder();
		boolean isFirst =true;
		for(String s : trading){
			if(!isFirst)
				sb.append(", ");
			else 
				isFirst=false;
			sb.append(s);
		}
		return sb.toString();
	}

	@Override
	public int compareTo(WishListItem o) {
		//return Integer.compare(o.getQuantity(),this.getQuantity());
		if(o.getQuantity()>getQuantity()) return 1;
		if(o.getQuantity()<getQuantity()) return -1;
		return 0;
		
	}

	public int getQuantity(){
		return 2*booster.size()+using.size()+3*trading.size();
	}

    public boolean containsUser(String uName){
        if(uName.equals( "all")) return true;
        return booster.contains(uName)
                || using.contains(uName)
                || trading.contains(uName);
    }
}
