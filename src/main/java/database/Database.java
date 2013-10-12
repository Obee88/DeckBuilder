package database;

import com.mongodb.BasicDBObject;

import com.mongodb.DBCursor;
import custom.classes.Card;
import custom.classes.ShowingCard;
import custom.classes.User;
import custom.classes.UserMessage;

import java.util.ArrayList;
import java.util.List;

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
    }

}
