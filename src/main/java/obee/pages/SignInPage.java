package obee.pages;

import obee.pages.master.MasterPage;

import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import database.Database;
import database.MongoHandler;

@SuppressWarnings("serial")
public class SignInPage extends MasterPage {
	private static final long serialVersionUID = 1L;

	MongoHandler mongo = MongoHandler.getInstance();
	int numOfUsers;
	public SignInPage(final PageParameters params) {
		super(params,"SignIn");
		numOfUsers = mongo.getNumberOfUsers();
		if(numOfUsers<0) numOfUsers=0;
		add(new Label("usersNumber", numOfUsers));
		SignInPanel signInPanel = new SignInPanel("signInPanel",false);
		add(signInPanel);
		add(new BookmarkablePageLink<Object>("registerLink",
	            RegisterPage.class));
		
		Form<Object> form = new Form<Object>("init"){
			@Override
			protected void onSubmit() {
				Database.init();
				setResponsePage(getApplication().getHomePage());
			}
			
			@Override
			public boolean isVisible() {
				return !(numOfUsers>0);
			}
		};
		add(form);
    }
}
