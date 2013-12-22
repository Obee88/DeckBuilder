package obee.pages;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import custom.classes.*;
import custom.components.panels.CardSelectionPanel;
import custom.components.panels.InfoPanel;
import obee.pages.master.MasterPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import suport.MailSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@AuthorizeInstantiation("ADMIN")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CardsThisWeek extends MasterPage {
    private final TextField<String> toTbx,fromTbx;
    private InfoPanel infoPane;
    CardSelectionPanel cardsPane;
    User u = mongo.getUser(getUserName());

    public CardsThisWeek(PageParameters params) {
        super(params, "CardsThisWeek");
        ArrayList<ShowingCard> a = new ArrayList<ShowingCard>();
        ArrayList<ShowingCard> list = mongo.getThisWeekCards();
        a.add(list.get(0));
        a.add(list.get(list.size()-1));
        for(int i = 1; i<list.size()-1; i++){
            ShowingCard t = list.get(i), p = list.get(i-1), n = list.get(i+1);
            if(!(t.owner.equals(p.owner)&&t.owner.equals(n.owner)))
                a.add(t);
        }
        cardsPane = new CardSelectionPanel("cardsPane",a);
        infoPane = new InfoPanel("infoPane",u.isAdmin());
        cardsPane.listChooser.addEventListener(infoPane);
        add(cardsPane);
        add(infoPane);

        Form form = new Form("deleteRangeForm") {
            @Override
            protected void onSubmit() {
                int f = Integer.parseInt(fromTbx.getDefaultModelObjectAsString());
                int t = Integer.parseInt(toTbx.getDefaultModelObjectAsString());
                for (int i= f; i<=t;i++)
                    mongo.removeCard(i);
                setResponsePage(CardsThisWeek.class);
            }
        };
        Form fixIdsInList = new Form("fixIdsInList") {
            @Override
            protected void onSubmit() {
                mongo.removeUnexistingIds();
                info("DONE fixIdsInList");
                setResponsePage(CardsThisWeek.class);
            }
        };
        add(fixIdsInList);
        fromTbx = new TextField<String>("fromTbx", new Model<String>());
        form.add(fromTbx);
        toTbx = new TextField<String>("toTbx", new Model<String>());
        form.add(toTbx);
        add(form);
    }
}
