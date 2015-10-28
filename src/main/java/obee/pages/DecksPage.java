package obee.pages;

import custom.classes.User;
import custom.components.panels.DeckMissingCardsPanel;
import custom.components.panels.DeckPanel;
import database.MongoHandler;
import obee.pages.master.MasterPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import suport.TappedOut.TODeck;
import suport.TappedOut.TOParser;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;


@AuthorizeInstantiation("ADMIN")
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class DecksPage extends MasterPage {

    MongoHandler mongo =MongoHandler.getInstance();
    User u =currentUser;
	private static final JavaScriptResourceReference MYPAGE_JS = new JavaScriptResourceReference(UserMainPage.class, "UserMainPage.js");

    private DropDownChoice<String> deckChooser;
    private List<String> brojevi;
    private OnChangeAjaxBehavior onChange;
    private DeckPanel deckPanel;
    private DeckMissingCardsPanel missingCardsPanel;

    public DecksPage(PageParameters params) {
		super(params, "Decks");
        initForms();
        intitLists();
        initComponents();
        initBehaviours();

	}

    private void initForms() {
    }

    private void intitLists() {
        brojevi = currentUser.getDecks();
    }

    private void initComponents() {
        deckChooser  = new DropDownChoice<String>("deckChooser",
                new Model(),
                new Model((Serializable) brojevi));
        add(deckChooser);
        deckPanel = new DeckPanel("deckPanel", TODeck.nullDeck(), currentUser);
        add(deckPanel);
        missingCardsPanel = new DeckMissingCardsPanel("missingCardsPanel", TODeck.nullDeck(), currentUser);
        missingCardsPanel.setOutputMarkupId(true);
        add(missingCardsPanel);
    }

    private void initBehaviours() {
        onChange = new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                String selection = deckChooser.getDefaultModelObjectAsString();
                TODeck deck = null;
                try {
                    deck = TOParser.getInstance().getDeck(selection);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ajaxRequestTarget = deckPanel.setDeck(ajaxRequestTarget,deck);
                ajaxRequestTarget = missingCardsPanel.setDeck(ajaxRequestTarget,deck);
//                System.out.println("ovo se dogadja");
            }
        };
        deckChooser.add(onChange);
    }

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(JavaScriptReferenceHeaderItem.forReference(MYPAGE_JS));
	}

}
