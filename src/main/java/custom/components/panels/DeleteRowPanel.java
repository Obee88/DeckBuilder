package custom.components.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

@SuppressWarnings("serial")
public abstract class DeleteRowPanel extends Panel {

	AjaxLink<Object> deleteBtn;
	private String name;
	
	public DeleteRowPanel(String id, String name) {
		super(id);
		this.name = name;
		deleteBtn = new AjaxLink<Object>("deleteBtn") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				doDelete();
			}
		};
		add(deleteBtn);
	}
	public abstract void doDelete() ;
	public String getName() {
		return name;
	}
}
