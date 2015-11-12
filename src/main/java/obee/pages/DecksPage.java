package obee.pages;

import custom.classes.User;
import custom.components.panels.DeckMissingCardsPanel;
import custom.components.panels.DeckPanel;
import custom.test.StopWatch;
import database.MongoHandler;
import obee.pages.master.MasterPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import suport.TappedOut.TODeck;
import suport.TappedOut.TOParser;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;


@AuthorizeInstantiation("USER")
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class DecksPage extends MasterPage {

    MongoHandler mongo =MongoHandler.getInstance();
    User u =currentUser;
	private static final JavaScriptResourceReference MYPAGE_JS = new JavaScriptResourceReference(UserMainPage.class, "UserMainPage.js");

    private DropDownChoice<String> deckChooser;
    private List<String> deckNames;
    private OnChangeAjaxBehavior onChange;
    private DeckPanel deckPanel;
    private DeckMissingCardsPanel missingCardsPanel;
    private Form removeDeckForm;
    private Form addDeckForm;

    public DecksPage(PageParameters params) {
		super(params, "Decks");
        initForms();
        intitLists();
        initComponents();
        initBehaviours();

	}

    private void initForms() {
        removeDeckForm = new Form("removeDeckForm"){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                String deckName = deckChooser.getDefaultModelObjectAsString();
                currentUser.removeDeck(deckName);
                currentUser.UPDATE();
                info("Deck "+deckName+" removed!");
                setResponsePage(DecksPage.class);
            }

        };
        removeDeckForm.setOutputMarkupId(true);
        add(removeDeckForm);

        addDeckForm = new Form("addDeckForm"){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                setResponsePage(AddDecksPage.class);
            }
        };
        add(addDeckForm);
    }

    private void intitLists() {
        deckNames = currentUser.getDeckNames();
    }

    private void initComponents() {
        deckChooser  = new DropDownChoice<String>("deckChooser",
                new Model(),
                new Model((Serializable) deckNames));
        add(deckChooser);
        deckChooser.setVisible(deckNames.size()>0);
        deckPanel = new DeckPanel("deckPanel", TODeck.nullDeck(), currentUser);
        add(deckPanel);
        missingCardsPanel = new DeckMissingCardsPanel("missingCardsPanel", TODeck.nullDeck(), currentUser);
        missingCardsPanel.setOutputMarkupId(true);
        hide(missingCardsPanel);
        add(missingCardsPanel);
    }

    private void initBehaviours() {
        onChange = new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                StopWatch sw =new StopWatch();
                sw.start();
                String selection = deckChooser.getDefaultModelObjectAsString();
                TODeck deck = null;
                try {

                    deck = TOParser.getInstance().getDeck(currentUser.getDeck(selection));
                    sw.checkpoint("deck pulled from TO!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ajaxRequestTarget = deckPanel.setDeck(ajaxRequestTarget,deck);
                if (deck.isEmpty()){
                    hide(removeDeckForm);
                    hide(missingCardsPanel);
//                    removeDeckForm.add(new AttributeModifier("style", "visibility:hidden;"));
                }
                else{
                    show(missingCardsPanel);
                    show(removeDeckForm);
//                    removeDeckForm.add(new AttributeModifier("style", "visibility:visible;"));
                }
                ajaxRequestTarget.add(removeDeckForm);
                sw.checkpoint("deckPanel Initialized");
                ajaxRequestTarget = missingCardsPanel.setDeck(ajaxRequestTarget,deck);
                sw.checkpoint("missing cards Initialized");
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
