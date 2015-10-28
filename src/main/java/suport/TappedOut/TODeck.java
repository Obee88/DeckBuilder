package suport.TappedOut;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Obee on 28/09/15.
 */
//public class TODeck extends ArrayList<TOCard> implements Serializable{
//    private final String name;
//
//    public TODeck(String name) {
//        this.name = name;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public static TODeck nullDeck(){
//        return new TODeck("nill");
//    }
//}
//


public class TODeck implements Serializable {
    private HashMap<String,HashMap<TOCard,Integer>> categoryes;
    private List<List<String>> rows = null;
    private String name;

    public TODeck(String name){
        this.name = name;
        this.categoryes = new HashMap<String, HashMap<TOCard,Integer>>();
    }

    public HashMap<TOCard, Integer> getCategory(String category) {
        category = category.toLowerCase();
        if (!categoryes.containsKey(category)){
            categoryes.put(category, new HashMap<TOCard, Integer>());
        }
        return categoryes.get(category);
    }

    public TODeck add(TOCard card){
        return add(card,card.getQuantity());
    }

    public TODeck add(TOCard tcrd, int num) {
        HashMap<TOCard,Integer> c = getCategory(tcrd.getCategory());
        int oldNum = c.containsKey(tcrd)?c.get(tcrd):0;
        int newNum = num+oldNum;
        c.put(tcrd,newNum);
        return this;
    }

    private int getCategorySizeEstimation(String category){
        return 3+getCategory(category).size();
    }

    public void organizeRows(int rowsNum){
        List<List<String>> rows = new ArrayList<List<String>>();
        for (int i=0;i<rowsNum;++i)
            rows.add(new ArrayList<String>());
        if (this.hasCategory("sideboard"))
            rows.get(rowsNum-1).add("sideboard");
        if (this.hasCategory("maybeboard"))
            rows.get(rowsNum-1).add("maybeboard");
        int catNum = categoryes.keySet().size();
        while (totalRowsSize(rows)<catNum){
            String cat = getLongestUnselectedCategory(rows);
            int ind  = getShortestRowIndex(rows);
            rows.get(ind).add(0,cat);
        }
        this.rows = rows;
    }

    private int getShortestRowIndex(List<List<String>> rows) {
        int size = 10000;
        int index = 10000;
        int i=0;
        for (List l : rows){
            int totalRowSize = totalRowSize(l);
            if(totalRowSize<size){
                size = totalRowSize;
                index = i++;
            } else ++i;
        }
        return index;
    }

    private int totalRowSize(List<String> l) {
        int s = 0;
        for (String cat : l)
            s+= getCategorySizeEstimation(cat);
        return s;
    }

    private String getLongestUnselectedCategory(List<List<String>> rows) {
        int size = 0;
        String cat = null;
        for (String category : categoryes.keySet())
            if (!category.equals("sideboard") && !category.equals("maybeboard"))
                if (!isInRow(rows, category))
                    if (getCategorySizeEstimation(category)>size){
                        size = getCategorySizeEstimation(category);
                        cat = category;
                    }
        return cat;
    }

    private boolean isInRow(List<List<String>> rows, String category) {
        for (List l : rows)
            if (l.contains(category))
                return true;
        return false;
    }

    private int totalRowsSize(List<List<String>> rows) {
        int ret = 0;
        for (List<String> l : rows)
            ret+=l.size();
        return ret;
    }

    private boolean hasCategory(String c) {
        return categoryes.containsKey(c);
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public BasicDBObject getAllCardsQueryList(){
        BasicDBList namesList = new BasicDBList();
        for (String categoryName : categoryes.keySet()){
            Set<TOCard> names = getCategory(categoryName).keySet();
            namesList.addAll(names);
        }
        return new BasicDBObject("cardInfo.name",new BasicDBObject("$in",namesList));
    }

    public Map<String,Integer> getAllCards() {
        Map<String, Integer> ret = new HashMap<String, Integer>();
        for (String catName : categoryes.keySet())
            for(TOCard card : getCategory(catName).keySet())
                ret.put(card.getName(), getCategory(catName).get(card));
        return ret;
    }

    public List<TOCard> getAllTOCards(){
        List<TOCard> ret = new ArrayList<TOCard>();
        for (String catName : categoryes.keySet())
            ret.addAll(getCategory(catName).keySet());
        return ret;
    }

    public String getName() {
        return name;
    }

    public static TODeck nullDeck(){
        return new TODeck("nill");
    }

    public boolean isEmpty() {
        return getAllCards().size() == 0;
    }
}
