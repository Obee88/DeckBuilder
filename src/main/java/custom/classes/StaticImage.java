package custom.classes;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;

/**
 * Created by Obee on 03/12/15.
 */
public class StaticImage extends WebComponent {

    public StaticImage(String id, IModel model) {
        super(id, model);
    }

    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "img");
        tag.put("src", getDefaultModelObjectAsString());
        // since Wicket 1.4 you need to use getDefaultModelObjectAsString() instead of getModelObjectAsString()
    }

}