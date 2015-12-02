package custom.classes;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import custom.classes.abstractClasses.MongoObject;
import database.MongoHandler;
import org.joda.time.DateTime;
import sun.misc.BASE64Decoder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Obee on 02/12/15.
 */
public class Invoice extends MongoObject{
    private List<String> userNames;
    private Integer jadAmount, id;
    private Date date;
    private String comment;
    private Integer nextId;


    public Invoice(List<String> userNames, Integer jadAmount, String comment) {
        this.userNames = userNames;
        this.jadAmount = jadAmount;
        this.date = DateTime.now().toDate();
        this.comment = comment;
        this.id = getNextId();
    }

    public Invoice(List<String> userNames, Integer jadAmount, String comment, Date date) {
        this.userNames = userNames;
        this.jadAmount = jadAmount;
        this.date = date;
        this.comment = comment;
        this.id = getNextId();
    }

    public Invoice(DBObject obj){
        this.userNames = DBL2StrL((BasicDBList) obj.get("userNames"));
        this.jadAmount = (Integer) obj.get("jadAmount");
        this.date = (Date) obj.get("date");
        this.comment = (String) obj.get("comment");
        this.id = (Integer) obj.get("id");
    }

    public DBObject toDBObject(){
        BasicDBObject dbo = new BasicDBObject();
        BasicDBList userNamesDbList = this.StrL2DBL(userNames);
        dbo.append("userNames", userNamesDbList);
        dbo.append("jadAmount", jadAmount);
        dbo.append("date", date);
        dbo.append("comment", comment);
        dbo.append("id", id);
        return dbo;
    }

    @Override
    public void UPDATE() {
        MongoHandler.getInstance().invoicesCollection.update(
                getQ(),
                toDBObject(),
                true, false
        );
        return;
    }

    @Override
    public BasicDBObject getQ() {
        return new BasicDBObject("id", id);
    }

    private Integer getNextId() {
        if (MongoHandler.getInstance().invoicesCollection.count()==0) return 1;
        return 1 + (Integer)(MongoHandler.getInstance().invoicesCollection.find().sort(new BasicDBObject("id", -1)).limit(1).next().get("id"));
    }

    public Integer getJadAmount() {
        return jadAmount;
    }

    public String getDateString() {
        DateTime dt = new DateTime(date);
        return String.format("%02d-%02d-%04d",dt.getDayOfMonth(), dt.getMonthOfYear(), dt.getYear());
    }

    public String getUsersString() {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String userName : userNames){
            if (isFirst){
                isFirst=false;
            } else sb.append(", ");
            sb.append(userName);
        }
        return sb.toString();
    }

    public String getComment() {
        return comment;
    }

    public List<String> getUserNames() {
        return userNames;
    }
}
