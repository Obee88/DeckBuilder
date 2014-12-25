package obee.pages;

import custom.classes.ShowingCard;
import custom.components.panels.CardSelectionPanel;
import database.MongoHandler;
import obee.pages.master.MasterPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

@AuthorizeInstantiation("USER")
@SuppressWarnings({ "unchecked", "rawtypes", "serial"})
public class Test extends MasterPage{

	MongoHandler mongo =MongoHandler.getInstance();

 	private List<ShowingCard> specials;
	CardSelectionPanel specialsPanel;

    public Test(final PageParameters params) {
		super(params,"Test");
        initForm();
//        initLists();
        initComponents();
    }

    private void initForm() {
//        form = new Form<Object>("form");
//        add(form);
    }

    private void initComponents() {
        specialsPanel = new CardSelectionPanel("usingPanel", (ArrayList<ShowingCard>) specials);
        specialsPanel.setOutputMarkupId(true);

    }

}

