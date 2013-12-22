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
	List<User> booster, using, trading;
	
	public WishListItem(String name,List<User> allUsrs) {
		initLists();
		List<Integer> CIids =new ArrayList<Integer>();
		DBCursor cur = mongo.cardInfoCollection.find(new BasicDBObject("name",name));
		while(cur.hasNext())
			CIids.add((Integer)cur.next().get("id"));
		List<Integer> Cids = new ArrayList<Integer>();
		for(Integer id : CIids){
			DBCursor c = mongo.cardsCollection.find(new BasicDBObject("cardInfoId",id));
			while(c.hasNext())
				Cids.add((Integer) c.next().get("id"));
		}
		for(Integer id : Cids){
			for(User u: allUsrs){
				if(u.hasCardIdInBoosters(id))
					booster.add(u);
				if(u.hasCardIdInTrading(id))
					trading.add(u);
				if(u.hasCardIdInUsing(id))
					using.add(u);
			}
		}
		this.name=name;
	}
	
	private void initLists() {
		using = new ArrayList<User>();
		trading= new ArrayList<User>();
		booster= new ArrayList<User>();
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
		for(User u : booster){
			String s = u.userName;
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
		for(User u : using){
			String s = u.userName;
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
		for(User u : trading){
			String s = u.userName;
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

    public boolean hasUser(String selectedUserName) {
        for (User u : booster)
            if (u.getUserName().equals(selectedUserName))
                return true;
        for (User u : using)
            if (u.getUserName().equals(selectedUserName))
                return true;
        for (User u : trading)
            if (u.getUserName().equals(selectedUserName))
                return true;
        return false;
    }
}
