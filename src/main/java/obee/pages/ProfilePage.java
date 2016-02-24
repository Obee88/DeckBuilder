package obee.pages;

import custom.classes.Administration;
import custom.classes.ShowingCard;
import custom.classes.User;
import custom.components.IEventListener;
import custom.components.ListChooser;
import custom.components.panels.CardSelectionPanel;
import custom.components.panels.CardView;
import obee.pages.master.MasterPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.*;

@SuppressWarnings({"serial","unchecked"})
@AuthorizeInstantiation("USER")
public class ProfilePage extends MasterPage {

    private final User user;
    private Form<Object> form;
    private PasswordTextField oldPassTbx;
    private PasswordTextField newPassTbx;
    private AjaxCheckBox wishMail, propMail;
    private AjaxCheckBox hackMode;

    public ProfilePage(PageParameters params) {
		super(params, "Profile");
		user=mongo.getUser(getUserName());
		initForm();
		initComponents();
	
	}

	private void initForm() {
		form = new Form<Object>("form"){
			@Override
			protected void onSubmit() {
                String oldPass = oldPassTbx.getDefaultModelObjectAsString();
                String newPass = newPassTbx.getDefaultModelObjectAsString();
                System.out.println();
                if(user.authenticate(oldPass))   {
                  user.changePassword(newPass);
                    info("Password changed");
                }else
                    info("error! password not changed!");

            }
		};
		add(form);
	}

	@SuppressWarnings("rawtypes")
	private void initComponents() {
        oldPassTbx = new PasswordTextField("oldPassTbx",new Model<String>());
        form.add(oldPassTbx);
        newPassTbx = new PasswordTextField("newPassTbx", new Model<String>());
        form.add(newPassTbx);
        wishMail = new AjaxCheckBox("wishMail",new Model<Boolean>(user.wantsWishlistMail())){
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                Boolean val = (Boolean) getDefaultModelObject();
                user.setWantsWishlistMail(val);
                user.UPDATE();
            }
        };
        add(wishMail);
        propMail = new AjaxCheckBox("propMail",new Model<Boolean>(user.wantsProposalMail())){
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                Boolean val = (Boolean) getDefaultModelObject();
                user.setWantsProposalMail(val);
                user.UPDATE();
            }
        };
        add(propMail);
        hackMode = new AjaxCheckBox("hackMode",new Model<Boolean>(user.isInHackerMode())){
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                Boolean val = (Boolean) getDefaultModelObject();
                user.setHackerMode(val);
                user.UPDATE();
            }
        };
        add(hackMode);

    }
}