package obee.pages;

import custom.classes.*;
import custom.components.panels.CardSelectionPanel;
import custom.components.panels.InfoPanel;
import obee.pages.master.MasterPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

@AuthorizeInstantiation("ADMIN")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ForSale extends MasterPage {
    private InfoPanel infoPane;
    CardSelectionPanel cardsPane;
    User u = currentUser;

    public ForSale(PageParameters params) {
        super(params, "ForSale");
        List<ShowingCard> list = new ArrayList<ShowingCard>();//mongo.getInterestedShowingCards();
        cardsPane = new CardSelectionPanel("cardsPane",(ArrayList)list);
        infoPane = new InfoPanel("infoPane",u.isAdmin());
        infoPane.setInterestListlVisible(true);
        cardsPane.listChooser.addEventListener(infoPane);
        cardsPane.showColorByStatus(true);
        add(cardsPane);
        add(infoPane);


    }
}
