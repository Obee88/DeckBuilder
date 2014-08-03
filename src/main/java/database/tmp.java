package database;



import com.mongodb.*;

import custom.classes.Card;
import custom.classes.User;
import org.joda.time.DateTime;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class tmp {

	public static void main(String[] args) throws UnknownHostException {
        try {
            Mongo doma = new Mongo();
            DBObject obj =doma.getDB("Magic").getCollection("users").findOne(new BasicDBObject("userName","zlof"));
            obj.removeField("_id");
            Mongo server = new MongoClient("185.53.129.19", 27017);
            DB serverdb = server.getDB("Magic");
            DBCursor infos = doma.getDB("Magic").getCollection("cardInfo").find(new BasicDBObject(), new BasicDBObject("id",1).append("manaCost",1));
            while (infos.hasNext()){
                DBObject io = infos.next();
                String manaCost = io.get("manaCost").toString();
                Object id = io.get("id");
                int i = 0;
                int colorless = 0;
                if (id.equals(3768))
                    System.out.println();
                Set<String> colors = new TreeSet<String>();
                while  (i<manaCost.length() && Character.isDigit(manaCost.charAt(i))){
                    colorless *=10;
                    colorless += Character.getNumericValue(manaCost.charAt(i++));
                }
                String colorsLetters = "BUGRW";
                for(;i<manaCost.length();i++){
                    char c = manaCost.charAt(i);
                    String C = Character.toString(c);
                    if (c=='{'){
                        colorless++;
                        C =Character.toString(manaCost.charAt(++i));
                        if (colorsLetters.contains(C))
                            colors.add(C);
                        i++;
                        C =Character.toString(manaCost.charAt(++i));
                        if (colorsLetters.contains(C))
                            colors.add(C);
                        i++;
                    }
                    else {
                        if (colorsLetters.contains(C)){
                            colorless++;
                            colors.add(C);
                        }
                    }
                }
                BasicDBList colorsList = new BasicDBList();
                colorsList.addAll(colors);
                serverdb.authenticate("Deck","Builder".toCharArray());
                serverdb.getCollection("cardInfo").update(new BasicDBObject("id",id),
                        new BasicDBObject("$set", new BasicDBObject("colorsList",colorsList)
                            .append("convertedManaCost",colorless).append("numOfColors",colorsList.size()))


                );


            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
