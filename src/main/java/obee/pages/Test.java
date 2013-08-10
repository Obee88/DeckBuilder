package obee.pages;

import custom.classes.ShowingCard;
import custom.classes.User;
import custom.components.panels.CardSelectionPanel;
import database.MongoHandler;
import obee.pages.master.MasterPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import wicketdnd.DragSource;
import wicketdnd.Operation;
import wicketdnd.Transfer;
import wicketdnd.theme.WindowsTheme;

import java.util.ArrayList;
import java.util.List;

@AuthorizeInstantiation("USER")
@SuppressWarnings({ "unchecked", "rawtypes", "serial"})
public class Test extends MasterPage{

	MongoHandler mongo =MongoHandler.getInstance();
    User user = mongo.getUser(getUserName());

 	private List<ShowingCard> using,trading;
	CardSelectionPanel usingPanel, tradingPanel;
    private Form<Object> form;

    public Test(final PageParameters params) {
		super(params,"Test");
        initForm();
        initLists();
        initComponents();
    }

    private void initForm() {
        form = new Form<Object>("form");
        add(form);
    }

    private void initComponents() {
        usingPanel = new CardSelectionPanel("usingPanel", (ArrayList<ShowingCard>) using);
        usingPanel.setOutputMarkupId(true);
        usingPanel.add(new DragSource(Operation.MOVE) {
            @Override
            public void onAfterDrop(AjaxRequestTarget target, Transfer transfer) {
                super.onAfterDrop(target, transfer);    //To change body of overridden methods use File | Settings | File Templates.
            }
        }.drag("tr"));
        form.add(usingPanel);
        tradingPanel = new CardSelectionPanel("tradingPanel", (ArrayList<ShowingCard>) trading);
        tradingPanel.setOutputMarkupId(true);
        form.add(tradingPanel);
    }

    private void initLists() {
        using = user.getUsingShowingCards();
        trading = user.getTradingShowingCards();
    }
}

