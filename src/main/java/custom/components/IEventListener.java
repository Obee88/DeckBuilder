package custom.components;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface IEventListener {
	public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender, String eventType);
}
