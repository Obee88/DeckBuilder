package custom.components.panels;

import com.mongodb.BasicDBObject;
import custom.classes.ShowingCard;
import custom.classes.TradingProposal;
import custom.components.IEventListener;
import custom.components.ImageWindow;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

@SuppressWarnings("serial")
public class PlusMinusPanel extends Panel implements IEventListener {

    private AjaxLink<Object> minusBtn;
    private int number=0;
    private AjaxLink<Object> plusBtn;
    private Label numLbl;
    private AjaxLink<Object> more, less;
    boolean isMore = true;

    public PlusMinusPanel(String id) {
		super(id);
		initComponents();
		setOutputMarkupId(true);
	}

	private void initComponents() {
        minusBtn = new AjaxLink<Object>("minusBtn"){
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                if(number==0) return;
                number--;
                ajaxRequestTarget.add(numLbl);
            }
        };
        add(minusBtn);
        plusBtn = new AjaxLink<Object>("plusBtn"){
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                number++;
                ajaxRequestTarget.add(numLbl);
            }
        };
        add(plusBtn);
        numLbl = new Label("numLbl", new PropertyModel<String>(this, "number"));
        numLbl.setOutputMarkupId(true);
        add(numLbl);
        more = new AjaxLink<Object>("more"){
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                isMore=false;
                hide(more);
                show(less);
                ajaxRequestTarget.add(more);
                ajaxRequestTarget.add(less);
            }
        };
        add(more);
        less = new AjaxLink<Object>("less") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                isMore=true;
                show(more);
                hide(less);
                ajaxRequestTarget.add(more);
                ajaxRequestTarget.add(less);
            }
        };
        hide(less);
        add(less);
    }

    public BasicDBObject getCondition(){
        String s = isMore? "$gte":"$lte";
        return new BasicDBObject(s,number);
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

    public int getNumber() {
        return number;
    }

    public boolean isMore() {
        return isMore;
    }
}
