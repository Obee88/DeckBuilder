package custom.classes;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import custom.classes.abstractClasses.MongoObject;

@SuppressWarnings("unchecked")
public class SubFolders extends MongoObject {

	private static final int SUBFOLDERS_COUNT = 12;
	List<ShowingCard>[] subFolders = new List[SUBFOLDERS_COUNT];
	User parent ;
	public SubFolders(DBObject obj, User parent){
		if(obj!=null){
			subFolders[0] = obj.get("sf0")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf0"));
			subFolders[1] = obj.get("sf1")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf1"));
			subFolders[2] = obj.get("sf2")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf2"));
			subFolders[3] = obj.get("sf3")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf3"));
			subFolders[4] = obj.get("sf4")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf4"));
			subFolders[5] = obj.get("sf5")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf5"));
			subFolders[6] = obj.get("sf6")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf6"));
			subFolders[7] = obj.get("sf7")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf7"));
			subFolders[8] = obj.get("sf8")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf8"));
			subFolders[9] = obj.get("sf9")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf9"));
			subFolders[10] = obj.get("sf10")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf10"));
			subFolders[11] = obj.get("sf11")==null?new ArrayList<ShowingCard>():DBL2SCL(obj.get("sf11"));
		} else{
			subFolders[0] = new ArrayList<ShowingCard>();
			subFolders[1] = new ArrayList<ShowingCard>();
			subFolders[2] = new ArrayList<ShowingCard>();
			subFolders[3] = new ArrayList<ShowingCard>();
			subFolders[4] = new ArrayList<ShowingCard>();
			subFolders[5] = new ArrayList<ShowingCard>();
			subFolders[6] = new ArrayList<ShowingCard>();
			subFolders[7] = new ArrayList<ShowingCard>();
			subFolders[8] = new ArrayList<ShowingCard>();
			subFolders[9] = new ArrayList<ShowingCard>();
			subFolders[10] = new ArrayList<ShowingCard>();
			subFolders[11] = new ArrayList<ShowingCard>();
		}
		this.parent=parent;
	}

	@Override
	public DBObject toDBObject() {
		BasicDBObject obj = new BasicDBObject()
			.append("sf0", SCL2DBL(subFolders[0]))
			.append("sf1", SCL2DBL(subFolders[1]))
			.append("sf2", SCL2DBL(subFolders[2]))
			.append("sf3", SCL2DBL(subFolders[3]))
			.append("sf4", SCL2DBL(subFolders[4]))
			.append("sf5", SCL2DBL(subFolders[5]))
			.append("sf6", SCL2DBL(subFolders[6]))
			.append("sf7", SCL2DBL(subFolders[7]))
			.append("sf8", SCL2DBL(subFolders[8]))
			.append("sf9", SCL2DBL(subFolders[9]))
			.append("sf10", SCL2DBL(subFolders[10]))
			.append("sf11", SCL2DBL(subFolders[11]));
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
		if (index>SUBFOLDERS_COUNT-1)
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
		for(int i=0;i<SUBFOLDERS_COUNT;i++){
			this.subFolders[i].clear();
			this.subFolders[i].addAll(subFolders[i]);
		}
	}
	
	public List<ShowingCard> getFreeCards(){
		List<ShowingCard> using = parent.getUsingShowingCards();
		List<ShowingCard> free= new ArrayList<ShowingCard>();
		for(ShowingCard sc : using){
			boolean cont = false;
			for (int i=0;i<SUBFOLDERS_COUNT;++i)
				if(subFolders[i].contains(sc)) {
					cont=true;
					break;
				}
			if (cont) continue;;
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
