package custom.components.panels;

import custom.classes.User;
import database.MongoHandler;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import suport.TappedOut.TOCard;
import suport.TappedOut.TODeck;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Obee on 28/09/15.
 */
public class CardListInRow extends Panel{
    private User user = null;

    public CardListInRow(String id, TODeck deck, String cat, final User user) {
        super(id);
        this.user = user;
        this.add(new Label("lbl", cat));
        final HashMap<TOCard, Integer> map = deck.getCategory(cat);

        List<TOCard> keyList = new ArrayList<TOCard>();
        keyList.addAll(map.keySet());
        ListView listView = new ListView("cardList", keyList) {
            @Override
            protected void populateItem(ListItem item) {
                final TOCard card = (TOCard) item.getDefaultModelObject();
                final int num = map.get(card);
                final String cardName = card.getName();
                final String url = card.getUrl();
                item.add(new WebComponent("cardListItem"){
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        Response response = getRequestCycle().getResponse();
                        long collectedCardsNum =isLandCard(cardName)?num: MongoHandler.getInstance().numOfCardsPerPlayer(cardName, user.getUserName());
                        response.write("<td>"+collectedCardsNum+"/"+num+" "+"<span style=\"color:#6493D2\" onmouseover=\"showImage('"+url+"',this)\" onmouseout=\"hideImage()\">"+cardName+"</span></td>");
                    }

                    private boolean isLandCard(String cardName) {
                        List<String> lands = Arrays.asList(new String[]{"island","plains","mountain","swamp","forest"});
                        return lands.contains(cardName.toLowerCase());
                    }
                });
            }
        };
        add(listView);
    }
}
