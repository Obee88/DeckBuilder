package database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import custom.classes.User;
public class Database {

	private static MongoHandler mongo = MongoHandler.getInstance();
	public static void init() {
		User u = new User("Admin", "admin@gmail.com", "admin");
		u.addRole("ADMIN");
		u.addRole("PRINTER");
		u.UPDATE();
		mongo.adminCollection.insert(new BasicDBObject("q","q"));
		System.out.println("updated");
		
	}
	
	public static void test(){

	}

    public static void main(String[] args) {
        DBObject thisMonthObject = MongoHandler.getInstance().statisticsCollection.findOne(new BasicDBObject("id","views"));
        DBObject daily_stats = (DBObject) thisMonthObject.get("daily_stats");
        System.out.println();
    }

}
