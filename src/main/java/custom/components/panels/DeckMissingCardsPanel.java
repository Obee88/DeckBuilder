package custom.components.panels;

import com.sun.xml.internal.xsom.impl.scd.Iterators;
import custom.classes.User;
import database.MongoHandler;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import suport.TappedOut.TOCard;
import suport.TappedOut.TODeck;

import java.util.*;


/**
 * Created by Obee on 28/09/15.
 */
public class DeckMissingCardsPanel extends Panel {
    private final User user;
    private TODeck deck;
    private ListView list;
    private CardMap missingCards;

    public DeckMissingCardsPanel(String id, TODeck deck, User user) {
        super(id);
        this.user = user;
        this.deck = deck;
        setDeck(deck);
        initComponents();
    }

    private void setDeck(TODeck deck) {
        missingCards = new CardMap();
        for (String cardName : deck.getAllCards().keySet()){
            if (isLandCard(cardName)) continue;
            if (MongoHandler.getInstance().numOfCardsPerPlayer(cardName, this.user.getUserName())==0){
                missingCards.put(cardName, MongoHandler.getInstance().getCardOwnersAsString(cardName));
            }
        }
    }

    private boolean isLandCard(String cardName) {
        List<String> lands = Arrays.asList(new String[]{"island","plains","mountain","swamp","forest"});
        return lands.contains(cardName.toLowerCase());
    }

    private void initComponents() {
        list = new ListView("list", new PropertyModel<List>(this, "missingCards.cardNames")) {
            @Override
            protected void populateItem(ListItem item) {
                String cardName = (String) item.getDefaultModelObject();
                item.add(new Label("cardName",cardName));
                item.add(new Label("usersList",missingCards.get(cardName)));
            }
        };
        list.setOutputMarkupId(true);
        add(list);
    }

    public AjaxRequestTarget setDeck(AjaxRequestTarget target,TODeck deck) {
        setDeck(deck);
        target.add(this);
        return target;
    }

//    @Override
//    public boolean isVisible() {
//        return super.isVisible() && !this.deck.isEmpty();
//    }

    class CardMap extends HashMap<String, String> {

        public List<String> getCardNames(){
            List<String> names = new ArrayList<String>();
            names.addAll(keySet());
            return names;
        }
    }
}
