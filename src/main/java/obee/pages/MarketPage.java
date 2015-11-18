package obee.pages;

import custom.classes.Market.CardMarket;
import custom.classes.Market.MarketCard;
import custom.components.panels.Market.MarketCardView;
import obee.pages.master.MasterPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.*;

@AuthorizeInstantiation("SHOPPER")
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class MarketPage extends MasterPage {


    private MarketPage self;
    private CardMarket market;

    public MarketPage(final PageParameters params) {
        super(params, "Market");
        this.market = CardMarket.getInstance(getUserName());
        market.sortCards();
        this.self = this;
        List<MarketCard>[] triples = market.getRows();

        ListView rows = new ListView("rows", Arrays.asList(triples)) {
            @Override
            protected void populateItem(ListItem rowItem) {
                List<MarketCard> triple = (List<MarketCard>) rowItem.getModelObject();
                rowItem.add(new ListView("cells", triple) {
                    @Override
                    protected void populateItem(ListItem cellItem) {
                        MarketCard card = (MarketCard) cellItem.getModelObject();
                        cellItem.add(new MarketCardView("cardPanel", card, currentUser, self));
                    }
                });

            }
        };
        add(rows);
        setStatusMessage();
    }

    private void setStatusMessage() {
        info(currentUser.getMarketStatusMessage());
    }


}
