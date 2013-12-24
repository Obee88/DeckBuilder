package custom.classes;

import java.util.Date;

import org.joda.time.DateTime;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import custom.classes.abstractClasses.MongoObject;

public class UserMessage extends MongoObject {
	int id;
	String subject, text;
	Date date;
	
	public UserMessage(int id, String subject, String text) {
		super();
		this.id = id;
		this.subject = subject;
		this.text = text;
		this.date = new DateTime().plusHours(2).toDate();
	}
	
	public UserMessage(DBObject obj){
		id = (Integer)obj.get("id");
		subject = obj.get("subject").toString();
		text = obj.get("text").toString();
		date = (Date) obj.get("date");
	}

	@Override
	public DBObject toDBObject() {
		BasicDBObject obj = new BasicDBObject()
		.append("id", id)
		.append("subject",subject)
		.append("text", text)
		.append("date", date);
		return obj;
	}

	@Override
	public void UPDATE() {
		
	}

	@Override
	public BasicDBObject getQ() {
		return new BasicDBObject("id",id);
	}
	
	public String getSubject() {
		return subject;
	}

	public String getText() {
		return text;
	}
	
	public int getId() {
		return id;
	}
	
	public Date getDate() {
		return date;
	}

	public String getDateString() {
		DateTime dt =new DateTime(date);
		String s = dt.toLocalDateTime().toString();
		int len = s.length();
		s=s.substring(0,len-7);
		s=s.replace("T", "    ");
		return s;
	}

    @Override
    public boolean equals(Object obj) {
        if(obj==null) return false;
        if(!(obj instanceof UserMessage)) return false;
        UserMessage o=(UserMessage)obj;
        if(o.subject.equals(subject) &&
                o.date.equals(date) &&
                o.id==id &&
                o.text.equals(text))
            return true;
        return false;
    }

    public static final UserMessage Welcome = new UserMessage(0,"Welcome","This is your first message. \nObee wish you welcome.");


}