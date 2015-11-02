package obee.pages;

import custom.classes.Deck;
import custom.classes.User;
import custom.components.panels.DeckMissingCardsPanel;
import custom.components.panels.DeckPanel;
import custom.test.StopWatch;
import database.MongoHandler;
import obee.pages.master.MasterPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.IPageLink;
import org.apache.wicket.markup.html.link.InlineFrame;
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
public class AddDecksPage extends MasterPage {

    MongoHandler mongo = MongoHandler.getInstance();
    User u = currentUser;
    private Form addDeckForm;
    private TextField<String> deckUrlTbx, deckNameTbx;

    public AddDecksPage(PageParameters params) {
		super(params, "AddDecks");
        initForms();
        intitLists();
        initComponents();
        initBehaviours();
	}

    private void initBehaviours() {

    }

    private void initComponents() {
        deckNameTbx = new TextField<String>("deckNameTbx", new Model<String>());
        addDeckForm.add(deckNameTbx);
        deckUrlTbx = new TextField<String>("deckUrlTbx", new Model<String>());
        addDeckForm.add(deckUrlTbx);

    }

    private void intitLists() {

    }

    private void initForms() {
        addDeckForm = new Form("addDeckForm"){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                String deckName = deckNameTbx.getDefaultModelObjectAsString();
                if (currentUser.hasDeck(deckName)) {
                    info("Deck name already exist!");
                    setResponsePage(AddDecksPage.class);
                } else {
                    String deckUrl = deckUrlTbx.getDefaultModelObjectAsString();
                    currentUser.addDeck(new Deck(deckName, deckUrl));
                    currentUser.UPDATE();
                    info("Deck Successfuly added!");
                    setResponsePage(DecksPage.class);
                }

            }
        };
        add(addDeckForm);
    }


}
