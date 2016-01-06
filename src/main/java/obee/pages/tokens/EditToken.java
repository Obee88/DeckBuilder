package obee.pages.tokens;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import custom.classes.StaticImage;
import database.MongoHandler;
import obee.pages.master.MasterPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

@AuthorizeInstantiation("EDITOR")
@SuppressWarnings({ "unchecked", "rawtypes", "serial"})
public class EditToken extends MasterPage{

    private final Form form;
    private final TextField<String> id, url, color, stats, name;
    private final TextField<String> type;
    private final TextField<String> text;

    public EditToken(PageParameters params) {
        super(params, "EditToken");
        DBObject o = mongo.tokensCollection.findOne(new BasicDBObject("color",new BasicDBObject("$exists", false)));
        form = new Form("form"){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                int _id = Integer.parseInt(id.getDefaultModelObjectAsString());
                BasicDBObject obj = new BasicDBObject("id", _id);
                obj.put("name", name.getDefaultModelObjectAsString().toLowerCase());
                obj.put("stats", stats.getDefaultModelObjectAsString().toLowerCase());
                obj.put("color", color.getDefaultModelObjectAsString().toLowerCase());
                obj.put("url", url.getDefaultModelObjectAsString().toLowerCase());
                obj.put("type", type.getDefaultModelObjectAsString().toLowerCase());
                obj.put("text", text.getDefaultModelObjectAsString().toLowerCase());
                MongoHandler.getInstance().tokensCollection.update(new BasicDBObject("id", _id),obj);
                setResponsePage(EditToken.class);
            }
        };
        name = new TextField<String>("name", new Model<String>(getAttrNonNull(o,"name")));
        form.add(name);
        stats = new TextField<String>("stats", new Model<String>(getAttrNonNull(o,"stats")));
        form.add(stats);
        color = new TextField<String>("color", new Model<String>(getAttrNonNull(o,"color")));
        form.add(color);
        url = new TextField<String>("url", new Model<String>(getAttrNonNull(o,"url")));
        form.add(url);
        id = new TextField<String>("id", new Model<String>(getAttrNonNull(o,"id")));
        form.add(id);
        type = new TextField<String>("type", new Model<String>(getAttrNonNull(o,"type")));
        form.add(type);
        text = new TextField<String>("text", new Model<String>(getAttrNonNull(o,"text")));
        form.add(text);
        add(new StaticImage("image", new Model(url.getDefaultModelObjectAsString())));
        add(form);
    }

    private String getAttrNonNull(DBObject o, String key) {
        if (o.get(key)==null)return "";
        return o.get(key).toString();
    }

}

