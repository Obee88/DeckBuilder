package custom.classes;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Obee on 02/11/15.
 */
public class Deck {
    private String name, url;

    public Deck(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Deck(DBObject obj) {
        this.name = obj.get("name").toString();
        this.url = obj.get("url").toString();
    }

    public DBObject toDBObject(){
        BasicDBObject obj = new BasicDBObject();
        obj.append("name",this.name);
        obj.append("url", this.url);
        return obj;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public boolean isNameEqual(String inName){
        return this.name.toLowerCase().equals(inName.toLowerCase());
    }

    public static List<Deck> parse(BasicDBList deckObjects) {
        List<Deck> decks = new ArrayList<Deck>();
        if (deckObjects==null) return decks;
        for (Object obj : deckObjects)
            decks.add(new Deck((DBObject)obj));
        return decks;

    }

    public String getTappedOutId() {
        String url = getUrl();
        if (url.endsWith("/")) url = url.substring(0,url.length()-1);
        String[] parts = getUrl().split("/");
        int size = parts.length;
        return parts[size-1];
    }
}
