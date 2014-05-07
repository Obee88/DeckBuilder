package custom.classes;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import custom.classes.abstractClasses.MongoObject;

@SuppressWarnings("unchecked")
public class SubFolders extends MongoObject {

	List<ShowingCard>[] subFolders = new List[6];
	User parent ;
	public SubFolders(DBObject obj, User parent){
		if(obj!=null){
			subFolders[0] = obj.get("sf0")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf0"));
			subFolders[1] = obj.get("sf1")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf1"));
			subFolders[2] = obj.get("sf2")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf2"));
			subFolders[3] = obj.get("sf3")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf3"));
			subFolders[4] = obj.get("sf4")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf4"));
			subFolders[5] = obj.get("sf5")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf5"));
		} else{
			subFolders[0] = new ArrayList<ShowingCard>();
			subFolders[1] = new ArrayList<ShowingCard>();
			subFolders[2] = new ArrayList<ShowingCard>();
			subFolders[3] = new ArrayList<ShowingCard>();
			subFolders[4] = new ArrayList<ShowingCard>();
			subFolders[5] = new ArrayList<ShowingCard>();
		}
		this.parent=parent;
		//parent.UPDATE();
	}

	@Override
	public DBObject toDBObject() {
		BasicDBObject obj = new BasicDBObject()
			.append("sf0", SCL2DBL(subFolders[0]))
			.append("sf1", SCL2DBL(subFolders[1]))
			.append("sf2", SCL2DBL(subFolders[2]))
			.append("sf3", SCL2DBL(subFolders[3]))
			.append("sf4", SCL2DBL(subFolders[4]))
			.append("sf5", SCL2DBL(subFolders[5]));
		return obj;
	}

	@Override
	public void UPDATE() {
	}

	@Override
	public BasicDBObject getQ() {
		return null;
	}
	
	private List<ShowingCard> DBL2SCL(Object object) {
		BasicDBList dbl = (BasicDBList)object;
		List<ShowingCard> ret = new ArrayList<ShowingCard>();
		for(Object o: dbl){
			Integer id =(Integer)o;
			ShowingCard sc =mongo.getShowingCard(id);
			ret.add(sc);
		}
		return ret;
	}
	
	private BasicDBList SCL2DBL(List<ShowingCard> list){
		BasicDBList ret = new BasicDBList();
		for(ShowingCard sc : list){
            if (sc==null) continue;
			ret.add(sc.cardId);
		}
		return ret;
	}
	
	public List<ShowingCard> getSubFolder(int index) {
		if (index>5)
			return null;
		return subFolders[index];
	}
	
	public void add(ShowingCard sc, int folder){
		if(!subFolders[folder].contains(sc))
			subFolders[folder].add(sc);
	}

	public void remove(ShowingCard sc, int folder){
		subFolders[folder].remove(sc);
	}
	
	public void validate(){
		List<ShowingCard> using = parent.getUsingShowingCards();
		for(List<ShowingCard> sf : subFolders){
			int len =sf.size();
			List<ShowingCard> remList = new ArrayList<ShowingCard>();
			for(ShowingCard sc : sf){
				if(!using.contains(sc))
					for(int i=0;i<len;i++){
						if(sf.get(i).cardId.equals(sc.cardId)){
							remList.add(sf.get(i));
							break;
						}
					}
			}
			sf.removeAll(remList);
		}
		parent.UPDATE();
	}

	public void setSubFolders(List<ShowingCard>[] subFolders){
		for(int i=0;i<6;i++){
			this.subFolders[i].clear();
			this.subFolders[i].addAll(subFolders[i]);
		}
	}
	
	public List<ShowingCard> getFreeCards(){
		List<ShowingCard> using = parent.getUsingShowingCards();
		List<ShowingCard> free= new ArrayList<ShowingCard>();
		for(ShowingCard sc : using){
			if(subFolders[0].contains(sc)) continue;
			if(subFolders[1].contains(sc)) continue;
			if(subFolders[2].contains(sc)) continue;
			if(subFolders[3].contains(sc)) continue;
			if(subFolders[4].contains(sc)) continue;
			if(subFolders[5].contains(sc)) continue;
			free.add(sc);
		}
		return free;
	}
	
	public boolean contains(ShowingCard sc){
		for(int i=0; i<subFolders.length; i++)
			if(subFolders[i].contains(sc))
				return true;
		return false;
	}

	public List<ShowingCard> getAllCards() {
		List<ShowingCard> ret = new ArrayList<ShowingCard>();
		for(int i=0; i<subFolders.length; i++)
			ret.addAll(subFolders[i]);
		return ret;
	}

	public void clearAll() {
		for(int i=0; i< subFolders.length;i++){
			subFolders[i].clear();
		}
	}

    public void removeFromAllSubfolders(Card c) {
        for(int i=0; i<subFolders.length; i++)
            subFolders[i].remove(new ShowingCard(c));
    }
}
