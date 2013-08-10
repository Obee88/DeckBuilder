package custom.components.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

@SuppressWarnings("serial")
public abstract class HidingQueryTextPanel extends Panel {

    private Component component;
    private AjaxLink<Object> excludeBtn, includeBtn;
    protected Label textLabel;

    private String text ="";
    private boolean shown=false;

    public HidingQueryTextPanel(String id, Component c) {
		super(id);
        component=c;
        hide(component);
        add(component);
		intiComponents();
    }

	private void intiComponents() {
        includeBtn = new AjaxLink<Object>("includeBtn") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                show(excludeBtn);
                show(component);
                shown=true;
                hide(this);
                ajaxRequestTarget.add(this);
                ajaxRequestTarget.add(excludeBtn);
                ajaxRequestTarget.add(component);
                ajaxRequestTarget.add(textLabel);
            }
        };
        includeBtn.setOutputMarkupId(true);
        add(includeBtn);
        excludeBtn = new AjaxLink<Object>("excludeBtn") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                show(includeBtn);
                hide(component);
                shown=false;
                hide(this);
                ajaxRequestTarget.add(this);
                ajaxRequestTarget.add(includeBtn);
                ajaxRequestTarget.add(component);
                ajaxRequestTarget.add(textLabel);
            }
        };
        hide(excludeBtn);
        add(excludeBtn);
        excludeBtn.setOutputMarkupId(true);
        textLabel= new Label("textLabel",new PropertyModel<String>(this,"text"));
        textLabel.setOutputMarkupId(true);
        add(textLabel);
	}

    public abstract Object getCondition();

    private void hide(Component c){
        c.add(new AttributeModifier("style", "display:none;"));
    }

    private void show(Component c){
        c.add(new AttributeModifier("style", "display:inline;"));
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isShown() {
        return shown;
    }

    public Component getComponent() {
        return component;
    }
}
