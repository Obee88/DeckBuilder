package custom.components.panels.Market;

import custom.classes.Market.MarketCard;
import custom.components.ImageWindow;
import obee.pages.MarketPage;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;


/**
 * Created by Obee on 28/09/15.
 */
public class MarketCardView extends Panel {

    private final MarketCard card;
    private final int jadAvailableForBidding;
    private ImageWindow view;
    private final String userName;
    private Label statusLbl;
    private Label priceLbl;
    private final int price;
    private Label hatersCntLbl;
    private Form bidForm, hateForm;
    private Label timeLbl;
    private Label nameLbl;

    public MarketCardView(String id, final MarketCard card, final String userName, int jadAvailableForBidding) {
        super(id);
        this.card = card;
        this.price = card.getPrice();
        this.userName = userName;
        this.jadAvailableForBidding = jadAvailableForBidding;

        add(new AttributeAppender("class", new Model(card.userActionStatus(userName)), " "));
        if (card.isNewToPlayer(userName)){
            add(new AttributeAppender("class", new Model("new"), " "));
        }

        initComponents();
        initForms();
        card.userSawCard(userName);
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
                return !card.listHaters().contains(userName) && (lastBidder==null || !lastBidder.equals(userName)) && jadAvailableForBidding>=price;
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
                return !card.listHaters().contains(userName) && card.bids.isEmpty();
            }
        };
        add(hateForm);
    }

    private void initComponents() {
        this.nameLbl = new Label("nameLbl", card.getCardName());
        add(nameLbl);

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
