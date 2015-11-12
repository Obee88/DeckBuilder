package custom.components.panels.Market;

import custom.classes.Market.MarketCard;
import custom.classes.User;
import custom.components.ImageWindow;
import database.MongoHandler;
import obee.pages.MarketPage;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Response;
import suport.TappedOut.TOCard;
import suport.TappedOut.TODeck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Obee on 28/09/15.
 */
public class MarketCardView extends Panel{

    private final MarketCard card;
    private ImageWindow view;
    private final String userName;
    private Label statusLbl;
    private Label priceLbl;
    private final int price;
    private Label hatersCntLbl;
    private Form bidForm, hateForm;
    private Label timeLbl;

    public MarketCardView(String id, final MarketCard card, final String userName) {
        super(id);
        this.card = card;
        this.price = card.getPrice();
        this.userName = userName;

        initComponents();
        initForms();

    }

    private void initForms() {
        this.bidForm = new Form("bidForm"){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                String message = card.bid(userName,price);
                info(message);
                setResponsePage(MarketPage.class);
            }

            @Override
            public boolean isVisible() {
                String lastBidder = card.getLastBidUserName();
                return !card.listHaters().contains(userName) && (lastBidder==null || !lastBidder.equals(userName));
            }
        };
        add(bidForm);

        this.hateForm = new Form("hateForm"){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                card.hate(userName);
                setResponsePage(MarketPage.class);
            }

            @Override
            public boolean isVisible() {
                return !card.listHaters().contains(userName) && !card.bids.contains(userName);
            }
        };
        add(hateForm);
    }

    private void initComponents() {
        this.timeLbl = new Label("timeLbl",card.getTimeToLooseString());
        add(timeLbl);

        String status = card.listHaters().contains(userName)? "You hated this card!" : card.bidingStatus();
        this.statusLbl = new Label("statusLbl", status);
        add(statusLbl);

        this.hatersCntLbl = new Label("hatersCntLbl", "H:"+card.getHatersCnt());
        add(hatersCntLbl);

        this.priceLbl = new Label("priceLbl", "$"+this.price);
        add(priceLbl);

        this.view = new ImageWindow("view", new Model<String>(card.getImageUrl().toString()), null);
        this.view.setUrl(card.getImageUrl());
        add(view);
    }
}
