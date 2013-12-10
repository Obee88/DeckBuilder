package custom.classes;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import custom.classes.abstractClasses.MongoObject;

@SuppressWarnings("unchecked")
public class SubFolders extends MongoObject {

	List<ShowingCard>[] subFolders = new List[6];
    List<Integer>[] subFoldersIds = new List[6];
	User parent ;
	public SubFolders(DBObject obj, User parent){
		if(obj!=null){
            for(int i = 0;i<6; i++) {
                DBObject o = (DBObject) obj.get("sf"+i);
                subFolders[i] = o==null?new ArrayList<ShowingCard>():DBL2SCL(o);
                subFoldersIds[i] = o==null?new ArrayList<Integer>():DBL2IntL((BasicDBList) o);
            }
		} else{
            for(int i = 0;i<6; i++) {
			    subFolders[i] = new ArrayList<ShowingCard>();
                subFoldersIds[i] = new ArrayList<Integer>();
            }
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
		return mongo.getShowingCards(dbl);
	}
	
	private BasicDBList SCL2DBL(List<ShowingCard> list){
		BasicDBList ret = new BasicDBList();
		for(ShowingCard sc : list){
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
		List<Integer> using = parent.getUsingCardIds();
		BasicDBList free= new BasicDBList();
		for(int sc : using){
			if(subFoldersIds[0].contains(sc)) continue;
			if(subFoldersIds[1].contains(sc)) continue;
			if(subFoldersIds[2].contains(sc)) continue;
			if(subFoldersIds[3].contains(sc)) continue;
			if(subFoldersIds[4].contains(sc)) continue;
			if(subFoldersIds[5].contains(sc)) continue;
			free.add(sc);
		}
		return mongo.getShowingCards(free);
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
