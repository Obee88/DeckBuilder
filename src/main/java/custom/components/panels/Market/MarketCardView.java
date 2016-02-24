package custom.components.panels.Market;

import com.mongodb.DBObject;
import custom.classes.Market.MarketCard;
import custom.classes.User;
import custom.components.ImageWindow;
import obee.pages.MarketPage;
import obee.pages.master.MasterPage;
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
import suport.ImageConverter;

import java.io.IOException;
import java.util.List;


/**
 * Created by Obee on 28/09/15.
 */
public class MarketCardView extends Panel {

    private final MarketCard card;
    private boolean hackerMode;
    private MasterPage masterPage;
    private ImageWindow view;
    private User user;
    private Label biddersNumLbl, hatersNumLbl;
    private Label priceLbl;
    private final int price;
    private AjaxLink bidLink, hateLink;
    private Form hateForm;
    private Label timeLbl;
    private Label nameLbl;
    private ListView bidsList, hatersList;
    private MarketCardView self;

    public MarketCardView(String id, final MarketCard card, User user, MasterPage masterPage) {
        super(id);
        this.hackerMode = false;
        this.card = card;
        this.price = card.getPrice();
        this.user = user;
        this.self = this;
        this.masterPage = masterPage;
        setOutputMarkupId(true);

        add(new AttributeAppender("class", new Model(card.userActionStatus(user.getUserName())), " "));
        if (card.isNewToPlayer(user.getUserName())){
            add(new AttributeAppender("class", new Model("new"), " "));
        }

        initComponents();
        initForms();
        card.userSawCard(user.getUserName());
    }

    public MarketCardView(String id, final MarketCard card, User user, MasterPage masterPage, boolean hackerMode) {
        super(id);
        this.hackerMode = hackerMode;
        this.card = card;
        this.price = card.getPrice();
        this.user = user;
        this.self = this;
        this.masterPage = masterPage;
        setOutputMarkupId(true);

        add(new AttributeAppender("class", new Model(card.userActionStatus(user.getUserName())), " "));
        if (card.isNewToPlayer(user.getUserName())){
            add(new AttributeAppender("class", new Model("new"), " "));
        }

        initComponents();
        initForms();
        card.userSawCard(user.getUserName());
    }

    private void initForms() {
        this.bidLink = new AjaxLink("bidForm"){
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                String message = card.bid(user.getUserName(),price);
                masterPage.info(user.getMarketStatusMessage());
                ajaxRequestTarget.add(masterPage);
            }

            @Override
            public boolean isVisible() {
                String lastBidder = card.getLastBidUserName();
                return !card.listHaters().contains(user.getUserName()) && (lastBidder==null || !lastBidder.equals(user.getUserName())) && user.getJadAvailableForBidding()>=price;
            }
        };
        add(bidLink);

        this.hateForm = new Form("hateForm"){

            @Override
            protected void onSubmit() {
                super.onSubmit();
                card.hate(user.getUserName());
                masterPage.info(user.getMarketStatusMessage());
                setResponsePage(MarketPage.class);
            }

            @Override
            public boolean isVisible() {
                return !card.listHaters().contains(user.getUserName()) && card.bids.isEmpty() && card.listHaters().size()==4;
            }
        };
        add(hateForm);

        this.hateLink = new AjaxLink("hateLink") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                card.hate(user.getUserName());
                masterPage.info(user.getMarketStatusMessage());
                ajaxRequestTarget.add(self);
            }

            @Override
            public boolean isVisible() {
                return !card.listHaters().contains(user.getUserName()) && card.bids.isEmpty() && card.listHaters().size()!=4;
            }
        };
        add(hateLink);
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

        String imgSrc = null;
        try {
            imgSrc = hackerMode ? ImageConverter.getBase64(card.getLinkForDownloading()) : card.getImageUrl();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.view = new ImageWindow("view", new Model<String>(imgSrc), null);
        this.view.setUrl(imgSrc);
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
