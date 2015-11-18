package custom.components.panels.Market;

import com.mongodb.DBObject;
import custom.classes.Market.MarketCard;
import custom.components.ImageWindow;
import obee.pages.MarketPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;


/**
 * Created by Obee on 28/09/15.
 */
public class MarketCardView extends Panel {

    private final MarketCard card;
    private final int jadAvailableForBidding;
    private ImageWindow view;
    private final String userName;
    private Label biddersNumLbl, hatersNumLbl;
    private Label priceLbl;
    private final int price;
    private AjaxLink bidForm, hateForm;
    private Label timeLbl;
    private Label nameLbl;
    private ListView bidsList, hatersList;
    private MarketCardView self;

    public MarketCardView(String id, final MarketCard card, final String userName, int jadAvailableForBidding) {
        super(id);
        this.card = card;
        this.price = card.getPrice();
        this.userName = userName;
        this.jadAvailableForBidding = jadAvailableForBidding;
        this.self = this;
        setOutputMarkupId(true);

        add(new AttributeAppender("class", new Model(card.userActionStatus(userName)), " "));
        if (card.isNewToPlayer(userName)){
            add(new AttributeAppender("class", new Model("new"), " "));
        }

        initComponents();
        initForms();
        card.userSawCard(userName);
    }

    private void initForms() {
        this.bidForm = new AjaxLink("bidForm"){
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                String message = card.bid(userName,price);
                info(message);
                ajaxRequestTarget.add(self);
//                ajaxRequestTarget.add(biddersNumLbl);
//                ajaxRequestTarget.add(hatersNumLbl);
//                ajaxRequestTarget.add(priceLbl);
            }

            @Override
            public boolean isVisible() {
                String lastBidder = card.getLastBidUserName();
                return !card.listHaters().contains(userName) && (lastBidder==null || !lastBidder.equals(userName)) && jadAvailableForBidding>=price;
            }
        };
        add(bidForm);

        this.hateForm = new AjaxLink("hateForm"){
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                card.hate(userName);
                ajaxRequestTarget.add(self);
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

        this.biddersNumLbl = new Label("biddersNumLbl", new PropertyModel<String>(this, "card.bidingStatus"));
        this.biddersNumLbl.setOutputMarkupId(true);
        add(biddersNumLbl);

        this.hatersNumLbl= new Label("hatersNumLbl", new PropertyModel<String>(this, "card.hatersStatus"));
        this.hatersNumLbl.setOutputMarkupId(true);
        add(hatersNumLbl);

        this.priceLbl = new Label("priceLbl", new PropertyModel<String>(this, "card.price"));
        this.priceLbl.setOutputMarkupId(true);
        add(priceLbl);

        this.view = new ImageWindow("view", new Model<String>(card.getImageUrl().toString()), null);
        this.view.setUrl(card.getImageUrl());
        add(view);

        this.bidsList = new ListView("bidsList", new PropertyModel<List>(this, "card.bids")) {

            @Override
            protected void populateItem(ListItem item) {
                DBObject bid = (DBObject) item.getModelObject();
                item.add(new Label("bidLbl", bid.get("userName").toString()+" - "+bid.get("value").toString()));
            }
        };
        add(bidsList);

        this.hatersList = new ListView("hatersList", new PropertyModel<List>(this, "card.haters")) {

            @Override
            protected void populateItem(ListItem item) {
                String hater = item.getModelObject().toString();
                item.add(new Label("haterLbl", hater));
            }
        };
        add(hatersList);
    }
}
