package custom.components.panels;

import custom.classes.User;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import suport.TappedOut.TODeck;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Obee on 28/09/15.
 */
public class DeckPanel extends Panel {
    private final int ROWS_NUM = 4;
    private final User user;
//    private final Label imeDeckaLbl;

    private TODeck deck;
    private ListView rowsView;

    public DeckPanel(String id, TODeck deck, User user) {
        super(id);
        this.user = user;
        this.deck = deck;
        this.initComponents();
//        imeDeckaLbl = new Label("imeDecka", new PropertyModel<String>(this,"deck.name"));
//        add(imeDeckaLbl);
//        imeDeckaLbl.setOutputMarkupId(true);
        setOutputMarkupId(true);
    }

    private void initComponents() {
        if (deck!=null)
            deck.organizeRows(ROWS_NUM);
        rowsView = new ListView("rows", new PropertyModel<List>(this, "deck.rows")) {
            @Override
            protected void populateItem(ListItem item) {
                List<String> cats = (List<String>) item.getModelObject();
                item.add(new ListView("row", cats) {
                    @Override
                    protected void populateItem(ListItem rowitem) {
                        String cat = (String)rowitem.getModelObject();
                        rowitem.add(new CardListInRow("category",deck,cat, user));
                    }
                });
            }
        };
        this.add(rowsView);
        rowsView.setOutputMarkupId(true);
    }

    public AjaxRequestTarget setDeck(AjaxRequestTarget target,TODeck deck) {
        this.deck = deck;
        deck.organizeRows(ROWS_NUM);
//        target.add(imeDeckaLbl);
        target.add(this);
        return target;
    }
}
