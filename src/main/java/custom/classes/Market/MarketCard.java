package custom.classes.Market;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import custom.classes.CardInfo;
import database.MongoHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Obee on 09/11/15.
 */
public class MarketCard {
    private static final int BID_DURATION_DAYS = 2, DAYS_TO_LIVE = 5, HATES_LIMIT = 3;
    private static final double PRICE_RAISE_RATE = 1.2;
    private Integer rarityInt;
    public BasicDBList bids = new BasicDBList();
    private Integer id;
    private String imageUrl, cardName;
    private BasicDBList haters = new BasicDBList();
    private DateTime creationDate;
    private double[] rates = new double[]{1.0,0.5,0.1};
    private BasicDBList saw = new BasicDBList();

    public MarketCard(DBObject obj) {
        load(obj);
    }

    public MarketCard(int id, CardInfo ci) {
        this.id = id;
        this.imageUrl = ci.downloadLink;
        this.cardName = ci.name;
        this.creationDate = new DateTime(DateTimeZone.forID("Asia/Tokyo"));
        this.rarityInt = ci.rarityInt;
    }

    public DBObject toDBObject(){
        DBObject ret = new BasicDBObject();
        ret.put("id",id);
        ret.put("haters", haters);
        ret.put("imageUrl", imageUrl);
        ret.put("cardName", cardName);
        ret.put("creationDate", creationDate.toDate());
        ret.put("bids", bids);
        ret.put("rarityInt", rarityInt);
        ret.put("saw", saw);
        return ret;
    }

    public void UPDATE(){
        MongoHandler.getInstance().marketCollection.update(
                new BasicDBObject("id",id),
                toDBObject(),
                true,  // upsert
                false  // multi
        );
        this.load();
    };

    public void hate(String userName){
        MongoHandler.getInstance().marketCollection.update(
                q(),
                new BasicDBObject("$push", new BasicDBObject("haters", userName))
        );
        this.load();
    }

    private void load() {
        load(MongoHandler.getInstance().marketCollection.findOne(q()));
    }

    private void load(DBObject obj) {
        this.id = (Integer) obj.get("id");
        this.haters = (BasicDBList)obj.get("haters");
        this.imageUrl = obj.get("imageUrl").toString();
        this.cardName = obj.get("cardName").toString();
        this.creationDate = new DateTime(obj.get("creationDate"));
        this.rarityInt = (Integer)obj.get("rarityInt");
        this.bids = (BasicDBList)obj.get("bids");
        this.saw = obj.get("saw")!=null? (BasicDBList)obj.get("saw"):new BasicDBList();
    }

    public DBObject q() {
        return new BasicDBObject("id", this.id);
    }

    public int getHatersCnt(){
        return haters.size();
    }

    public BasicDBList listHaters() {
        return haters;
    }

    public int getPrice() {
        Integer lastBidValue = MongoHandler.getInstance().getMarketCard(id).getLastBidValue();
        if (lastBidValue == null) {
            int lowerPrice = (int) (this.getStartingPrice() * rates[getHatersCnt()]);
            return lowerPrice<2? 2:lowerPrice;
        }
        int higherPrice = (int) (lastBidValue * PRICE_RAISE_RATE);
        return higherPrice == lastBidValue.intValue()? higherPrice+1:higherPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String bidingStatus() {
        if (getLastBidValue() == null)
            return "No bids for this card yet. ";
        String status = String.format("%s bided %d", getLastBidUserName(), getLastBidValue());
        if (bids.size()>0) status+= String.format(" (%d bids total)", bids.size());
        return status;
    }

    public String bid(String userName, Integer value) {
        if (!value.equals(getPrice()))
            return String.format("Someone bided faster! Try again!", getLastBidUserName());
        BasicDBObject bid =new BasicDBObject();
        bid.put("userName", userName);
        bid.put("value", value);
        bid.put("timestamp", new DateTime(DateTimeZone.forID("Asia/Tokyo")).toDate());
        MongoHandler.getInstance().marketCollection.update(
                q(),
                new BasicDBObject("$push", new BasicDBObject("bids", bid))
        );
        this.load();
        return "You bidded successfuly!";
    }

    public Integer getStartingPrice() {
        int[] prices = new int[]{6,20,50,130};
        return prices[rarityInt];
    }

    public Integer getLastBidValue() {
        DBObject lastBid = getLastBid();
        if (lastBid==null) return null;
        return (Integer)lastBid.get("value");
    }

    public DBObject getLastBid() {
        int numOfBids = bids.size();
        if (numOfBids==0)
            return null;
        DBObject lastBid = (DBObject) bids.get(numOfBids-1);
        return lastBid;
    }

    public String getLastBidUserName() {
        DBObject lastBid = getLastBid();
        if (lastBid==null) return null;
        return lastBid.get("userName").toString();
    }

    public int bidsCount() {
        return bids.size();
    }

    public boolean hasReachedHateLimit(){
        return getHatersCnt()>=HATES_LIMIT;
    }

    public boolean hasExpired(){
        DateTime ed = getExpirationDate();
        return ed.isBefore(new DateTime(DateTimeZone.forID("Asia/Tokyo")));

    }

    public DateTime getExpirationDate() {
        if (bidsCount() == 0)
            return creationDate.plusDays(DAYS_TO_LIVE);
        return new DateTime(getLastBid().get("timestamp")).plusDays(BID_DURATION_DAYS);

    }

    public String getTimeToLooseString(){
        DateTime ed = getExpirationDate();
        DateTime now = new DateTime(DateTimeZone.forID("Asia/Tokyo"));
        long millis = ed.getMillis() - now.getMillis();
        long days = millis / (1000*60*60*24);
        millis = millis % (1000*60*60*24);
        long hours = millis / (1000*60*60);
        millis = millis % (1000*60*60);
        long minutes = millis / (1000*60);
        millis = millis % (1000*60);
        long seconds = millis / 1000;
        return String.format("%dD %dH %dM %dS", days, hours, minutes, seconds);
    }

    public String userActionStatus(String userName) {
        if (haters.contains(userName)) return "hated";
        for(Object bidObj : bids){
            DBObject bid  = (DBObject)bidObj;
            if(bid.get("userName").toString().equals(userName))
                return "bidded";
        }
        return "no-action";
    }

    public boolean isNewToPlayer(String userName){
        return !saw.contains(userName);
    }

    public void userSawCard(String userName){
        if (isNewToPlayer(userName)) {
            saw.add(userName);
            UPDATE();
            load();
        }
    }

    public String getCardName() {
        return cardName;
    }

}
