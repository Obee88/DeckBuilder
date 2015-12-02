package custom.classes;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import database.MongoHandler;

/**
 * Created by Obee on 30/11/15.
 */
public class Token {

    private String color;
    private String type, name, url;
    private String att, def;
    private Integer id;

    public Token(String name, String url, String color, String type, String att, String def) {
        this.color = color;
        this.type = type;
        this.url = url;
        this.name = name;
        this.att = att;
        this.def = def;
        this.id = getNextId();
    }

    public Token(String name, String url, String color, String type, String att, String def, Integer id) {
        this.color = color;
        this.type = type;
        this.url = url;
        this.name = name;
        this.att = att;
        this.def = def;
        this.id = id;
    }
    
    public DBObject toDBObject(){
        BasicDBObject r = new BasicDBObject();
        r.append("id",id);
        r.append("name",name);
        r.append("url",url);
        r.append("color",color);
        r.append("type",type);
        r.append("att",att);
        r.append("def",def);
        return r;
    }

    public DBObject q(){
        return new BasicDBObject("id",id);
    }

    public Token UPDATE(){
        MongoHandler.getInstance().tokensCollection.update(
                q(),
                toDBObject(),
                true,
                false
        );
        return this;
    }

    public static Token fromDBObject(DBObject obj){
        return new Token(
                obj.get("name").toString(),
                obj.get("url").toString(),
                obj.get("color").toString(),
                obj.get("type").toString(),
                obj.get("att").toString(),
                obj.get("def").toString(),
                (Integer)obj.get("id")
            );
    }

    public Integer getNextId() {
        return ((Integer) MongoHandler.getInstance().tokensCollection.find().sort(new BasicDBObject("id", -1)).limit(1).next().get("id")) + 1;
    }
}
