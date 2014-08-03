package custom.components.panels;

import com.mongodb.BasicDBObject;
import custom.components.IEventListener;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;

@SuppressWarnings("serial")
public class DatePickerPanel extends Panel implements IEventListener {

    boolean isBefore=false;
    private AjaxLink<Object> before, after;
    private DateTextField dateTextField;
    private Date date;

    public DatePickerPanel(String id) {
		super(id);
		initComponents();
		setOutputMarkupId(true);
        date = new DateTime(DateTimeZone.forID("Asia/Tokyo")).toDate();
	}

	private void initComponents() {
        before = new AjaxLink<Object>("before"){
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                show(after);
                hide(before);
                isBefore=false;
                ajaxRequestTarget.add(after);
                ajaxRequestTarget.add(before);
            }
        };
        before.setOutputMarkupId(true);
        add(before);
        after = new AjaxLink<Object>("after"){
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                show(before);
                hide(after);
                isBefore=true;
                ajaxRequestTarget.add(after);
                ajaxRequestTarget.add(before);
            }
        };
        after.setOutputMarkupId(true);
        add(after);;
        hide(before);
    }

    public BasicDBObject getCondition(){
        return null;
    }

    @Override
    public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender, String eventType) {
        return target;
    }

    private void hide(Component c){
        c.add(new AttributeModifier("style", "display:none;"));
    }

    private void show(Component c){
        c.add(new AttributeModifier("style", "display:inline;"));
    }

}
