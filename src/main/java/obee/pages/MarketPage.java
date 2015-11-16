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


    public MarketPage(final PageParameters params) {
        super(params, "Market");
        CardMarket market = new CardMarket(getUserName());
        List<MarketCard>[] triples = market.getRows();
        int biddingTotal = market.biddingTotal(getUserName());
        final int jadAvailableForBidding = (currentUser.getJadBalance()-biddingTotal);

        final int[] newCardsCount = {0};
        ListView rows = new ListView("rows", Arrays.asList(triples)) {
            @Override
            protected void populateItem(ListItem rowItem) {
                List<MarketCard> triple = (List<MarketCard>) rowItem.getModelObject();
                rowItem.add(new ListView("cells", triple) {
                    @Override
                    protected void populateItem(ListItem cellItem) {
                        MarketCard card = (MarketCard) cellItem.getModelObject();
                        cellItem.add(new MarketCardView("cardPanel", card, getUserName(), jadAvailableForBidding));
                        if (card.isNewToPlayer(userName)) newCardsCount[0]++;
                    }
                });

            }
        };
        add(rows);
        String statusMessage = "Jad in bids: "+biddingTotal+" / Jad available: "+ jadAvailableForBidding;
        if (newCardsCount[0]>0)
            statusMessage = newCardsCount+" new cards in market / "+ statusMessage;
        info(statusMessage);
    }


}
