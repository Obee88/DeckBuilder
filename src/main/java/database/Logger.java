package database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Obee
 * Date: 09/01/14
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
public class Logger {
    public static void logPageView(String page){
        String dayString = getDayString();
        DBObject q = new BasicDBObject("id","views");
        processMonthlyStats(q);
        DBObject obj = new BasicDBObject("$inc",new BasicDBObject("daily_stats."+page+"."+dayString,1)).append("$set",new BasicDBObject("daily_stats.month",new DateTime(DateTimeZone.forID("Asia/Tokyo")).getMonthOfYear()));
        MongoHandler.getInstance().updateStatistic(q,obj);
    }

    private static void processMonthlyStats(DBObject q) {
        DBObject statsObj =MongoHandler.getInstance().statisticsCollection.findOne(q);
        if (statsObj==null) return;
        DBObject thisMonthDailyStats = (DBObject) statsObj.get("daily_stats");
        if (thisMonthDailyStats==null) return;
        Object mo = thisMonthDailyStats.get("month");
        if (mo==null) return;
        int month = (Integer)mo;
        DateTime now = new DateTime(DateTimeZone.forID("Asia/Tokyo"));
        if (now.getMonthOfYear()==month)
            return;

        for(String key : thisMonthDailyStats.keySet()) if(!key.equals("month")){
            DBObject obj = (DBObject) thisMonthDailyStats.get(key);
            int total = sumDays(obj);
            MongoHandler.getInstance().updateStatistic(q,new BasicDBObject("$set",new BasicDBObject("monthly_stats."+key+"."+getMonthString(),total)));
        }
        MongoHandler.getInstance().statisticsCollection.update(q,new BasicDBObject("$unset", new BasicDBObject("daily_stats",1)));
    }

    private static int sumDays(DBObject obj) {
        if (obj==null) return 0;
        int total = 0;
        for (String key : obj.keySet()){
            total += (Integer) obj.get(key);
        }
        return total;
    }

    private static String getDayString(){
        DateTime now = new DateTime(DateTimeZone.forID("Asia/Tokyo"));
        StringBuilder sb = new StringBuilder();
        sb.append("d_");
        int d = now.getDayOfMonth();
        if (d<10) sb.append("0");
        sb.append(d);
        return sb.toString();
    }
    private static String getMonthString(){
        DateTime now = new DateTime(DateTimeZone.forID("Asia/Tokyo"));
        StringBuilder sb = new StringBuilder();
        sb.append("m_");
        int m = now.getMonthOfYear();
        int y =  now.getYear();
        if (m<10) sb.append("0");
        sb.append(m).append("_").append(y);
        return sb.toString();
    }
}
