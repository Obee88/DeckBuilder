package custom.classes.Market;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import custom.classes.*;
import database.MongoHandler;
import suport.MailSender;

import java.util.*;

/**
 * Created by Obee on 09/11/15.
 */
public class CardMarket {
    public static Integer MARKET_SIZE = 20; // Mora biti djeljivo sa CARDS_PER_ROW!!
    private final String userName;
    List<MarketCard> cards;
    private boolean full;
    private List<String>[] triples;
    private Comparator<MarketCard> bidsUpHatesDown, expirationDesc;

    public CardMarket(final String userName) {
        this.userName = userName;

        fetchCards();

        checkExpirationDates();

        while (!isFull()) {
            addRandomCard();
            fetchCards();
        }

        initComparators();
        Collections.sort(cards, bidsUpHatesDown);
    }

    private void initComparators() {
        this.bidsUpHatesDown = new Comparator<MarketCard>() {
            @Override
            public int compare(MarketCard o1, MarketCard o2) {
                int o2bc = o2.bidsCount(),o1bc = o1.bidsCount();
                // if no bids or same bids count
                if (o2bc==o1bc){
                    if (o2.bids.contains(userName)) return 1;
                    if (o1.bids.contains(userName)) return -1;
                    int result = Integer.compare(o2.bidsCount(),o1.bidsCount());
                    if(result==0) {
                        // one with less haters go first
                        if (o2.listHaters().contains(userName))
                            return -1;
                        if (o1.listHaters().contains(userName))
                            return 1;
                        // if same haters count -> compare expiration dates
                        int expRes = expirationDesc.compare(o1, o2);
                        if (expRes!=0) return expRes;
                        // if expiration date is same compare prices
                        return Integer.compare(o1.getPrice(), o2.getPrice());
                    }
                    return result;
                }
                // cards I bidded comes first
                if(o1bc==0) return 1;
                if(o2bc==0) return -1;
                // if all the same compare num of bids
                return Integer.compare(o2bc, o1bc);
            }
        };
        this.expirationDesc = new Comparator<MarketCard>() {
            @Override
            public int compare(MarketCard o1, MarketCard o2) {
                if (o1.getExpirationDate().isBefore(o2.getExpirationDate())) return -1;
                return 1;
            }
        };
    }

    private void checkExpirationDates() {
        boolean shouldReload = false;
        for (MarketCard c : cards){
            if (c.hasExpired()){
                if (c.bidsCount()>0)
                    onBidWon(c);
                removeCard(c);
                shouldReload = true;
            }
            if (c.hasReachedHateLimit()){
                removeCard(c);
                shouldReload = true;
            }
        }
        if (shouldReload)
            fetchCards();
    }

    private void removeCard(MarketCard c) {
        MongoHandler.getInstance().marketCollection.remove(c.q());
    }

    private void onBidWon(MarketCard c) {
        String winner = c.getLastBidUserName();
        CardInfo ci = MongoHandler.getInstance().getCardInfo(c.getCardName());
        Card newCard = Card.generateCard(winner, ci);
        newCard.UPDATE();
        User u = MongoHandler.getInstance().getUser(winner);
        u.addToBooster(newCard.getCardId());
        u.decreaseJadBalance(c.getLastBidValue());
        String message = "You have won #"+c.getCardName()+"# card for "+c.getLastBidValue()+" jads!";
        u.addMessage(new UserMessage(u.getNextMessageId(),"You won the bid!",message));
        u.UPDATE();
        if (u.wantsProposalMail() || u.wantsWishlistMail())
            MailSender.sendBidWinningMail(u.getEmail(),c);
        CardGenerator.checkWishlists(new ShowingCard(newCard), MongoHandler.getInstance().getAllUsers(), winner);


    }

    private void fetchCards() {
        cards = MongoHandler.getInstance().getMarketCards();
    }

    private void addRandomCard() {
        int id = getBiggestId()+1;
        int cardInfoCount = (int) MongoHandler.getInstance().cardInfoCollection.count();
        CardInfo ci = null;
        while(ci==null || MongoHandler.getInstance().isBasicLandName(ci.name)){
            int cardInfoId= new Random().nextInt(cardInfoCount-1)+1;
            ci = MongoHandler.getInstance().getCardInfo(cardInfoId);
        }
        MarketCard c = new MarketCard(id,ci);
        c.UPDATE();
    }

    public boolean isFull() {
        return size() == MARKET_SIZE;
    }

    private Integer size() {
        return cards.size();
    }

    public Integer getBiggestId(){
        try {
            Integer id = (Integer) MongoHandler.getInstance().marketCollection.find(new BasicDBObject(), new BasicDBObject("id", 1))
                    .sort(new BasicDBObject("id", -1)).limit(1).next().get("id");
            return id;
        } catch (Exception e){
            if (MongoHandler.getInstance().marketCollection.count()==0) return 0;
            else return null;
        }
    }

    public List<MarketCard> getCards() {
        return cards;
    }

    public List<MarketCard>[] getRows() {
        int CARDS_PER_ROW = 5;
        int numOfRows = size()/CARDS_PER_ROW;
        ArrayList<MarketCard>[] row = new ArrayList[numOfRows];
        for(int i=0;i<numOfRows;++i)
            row[i] = new ArrayList<MarketCard>();
        for(int i=0; i<cards.size();++i)
            row[i/CARDS_PER_ROW].add(cards.get(i));
        return row;
    }

    public int biddingTotal(String userName){
        int total = 0;
        for(MarketCard c : getCards())
            if (c.getLastBidUserName()!=null && c.getLastBidUserName().equals(userName))
                total+=c.getLastBidValue();
        return total;
    }
}
