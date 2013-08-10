package custom.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwnerMap {
	Map<String, List<ShowingCard>> map;
	
	public OwnerMap() {
		 map = new HashMap<String, List<ShowingCard>>();
	}
	
	public void add(String owner, ShowingCard sc){
		if(map.containsKey(owner)){
			List<ShowingCard> l = map.get(owner);
			l.add(sc);
			map.put(owner, l);
		} else{
			List<ShowingCard> l = new ArrayList<ShowingCard>();
			l.add(sc);
			map.put(owner, l);
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(String owner: map.keySet()){
			List<ShowingCard> l = map.get(owner);
			sb.append(owner).append(":").append("\r\n");
			for (ShowingCard showingCard : l) {
				sb.append("   ").append(showingCard.name).append("\r\n");
			}
		}
		return sb.toString();
	}

	public void add(ShowingCard sc) {
		add(sc.owner, sc);
	}
}
