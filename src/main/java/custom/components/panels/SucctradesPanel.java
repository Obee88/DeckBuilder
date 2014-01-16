package custom.components.panels;

import custom.classes.ShowingCard;
import custom.components.IEventListener;
import custom.components.ListChooser;
import database.MongoHandler;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SuccTradesPanel extends Panel {


    private final ListView listview;

    public SuccTradesPanel(String id) {
        super(id);

        List list = MongoHandler.getInstance().getSuccessfullTrades("all");
        listview = new ListView("tradesList", list) {
            protected void populateItem(ListItem item) {
                item.add(new Label("label", item.getModel()));
            }
        };
        listview.setOutputMarkupId(true);
        add(listview);
    }

    public void setList(List l){
        listview.setList(l);
    }

}
