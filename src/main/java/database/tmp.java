package database;



import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import custom.classes.Card;
import custom.classes.User;
import org.joda.time.DateTime;

public class tmp {

	public static void main(String[] args) {

        DateTime date = DateTime.now().withDayOfWeek(1).withTimeAtStartOfDay().minusDays(1);
        System.out.println(date);
    }
}
