package custom.components.panels;

import com.mongodb.BasicDBObject;
import custom.components.IEventListener;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

@SuppressWarnings("serial")
public class puPanel extends Panel{
    public puPanel(String printUnprintPanel) {
        super(printUnprintPanel);
    }
}
