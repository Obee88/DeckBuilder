package custom.classes.abstractClasses;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import database.MongoHandler;

public abstract class MongoObject {
	protected static MongoHandler mongo = MongoHandler.getInstance();
	
	public abstract DBObject toDBObject();
	public abstract void UPDATE();
	public abstract BasicDBObject getQ();
	
	public List<Integer> DBL2IntL(BasicDBList list){
		List<Integer> ret = new ArrayList<Integer>();
		if(list==null) return ret;
		for(Object o: list){
			Integer i = (Integer)o;
			ret.add(i);
		}
		return ret;
	}
	
	public BasicDBList IntL2DBL(List<Integer> list){
		BasicDBList ret = new BasicDBList();
		if(list==null) return ret;
		for(Integer i: list)
			ret.add(i);
		return ret;
	}
	
	public BasicDBList StrL2DBL(List<String> list){
		BasicDBList ret = new BasicDBList();
		if(list==null) return ret;
		for(String i: list)
			ret.add(i);
		return ret;
	}
	
	public List<String> DBL2StrL(BasicDBList list){
		List<String> ret = new ArrayList<String>();
		if(list==null) return ret;
		for(Object o: list){
			String i = o.toString();
			ret.add(i);
		}
		return ret;
	}
}
