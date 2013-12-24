package obee.pages.master;

import java.util.ArrayList;
import java.util.List;

import obee.SignInSession;
import obee.WicketApplication;
import obee.pages.*;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import custom.classes.Page;
import custom.classes.User;
import database.MongoHandler;

@SuppressWarnings("serial")
public class MasterPage extends WebPage{
	protected String userName = "guest";
	protected Link<Object> logOffButton;
	protected MongoHandler mongo = MongoHandler.getInstance();
	protected String PAGE_NAME;
	protected FeedbackPanel feedback;
	protected SignInSession session;
    protected User currentUser;
	
	public MasterPage(final PageParameters params, String name) {
		PAGE_NAME=name;
		session =(SignInSession)getSession();
		if(session.isSignedIn())
			userName = session.getUserName();
	    currentUser = session.getUser();
		initNavgator();
		initMasterComponents();
	}
	
	
	private void initMasterComponents() {
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		add(new Label("userName",new PropertyModel<String>(this, "userName")));
		add(new Label("versionLabel",((WicketApplication)getApplication()).CURRENT_VERSION));
		add(new Label("pageTitle",PAGE_NAME));
		add(logOffButton=new Link<Object>("logOffButton")
        {
            @Override
            public void onClick()
            {
               setResponsePage(SignOut.class);
            }
        });
		logOffButton.setVisible(session.isSignedIn());
	}


	@SuppressWarnings("rawtypes")
	private void initNavgator() {
		List<Page> list = userName.equals("guest")?new ArrayList<Page>():getPagesList();
		ListView listview = new ListView<Page>("listView",list) {
		    protected void populateItem(ListItem item) {
		    	Page p = (Page)item.getModelObject();
		    	@SuppressWarnings("unchecked")
				BookmarkablePageLink<Object> pl= new BookmarkablePageLink<Object>("pageLink",p.getPageClass());
		    	pl.setVisibilityAllowed(currentUser.getRoles().contains(p.getRole()));
		    	pl.add(new Label("pageName", p.getName()));
		    	item.add(pl);
		    	if(PAGE_NAME.equals(p.getName()))
	    		  item.add(new AttributeAppender("class", "active"));
		    		
		    }
		};
		add(listview);
	}

	private List<Page> getPagesList() {
		List<Page> list = new ArrayList<Page>();
		list.add(new Page("Home", "USER", UserMainPage.class));
		list.add(new Page("Boosters","USER",BoosterPage.class));
		list.add(new Page("Folders","USER",SubFoldersPage.class));
        list.add(new Page("Trade","USER",TradePage.class));
		list.add(new Page("Recycle","USER",RecyclePage.class));
		list.add(new Page("Proposals","USER",TradingProposalsPage.class));
		list.add(new Page("WishList","USER",WishlistPage.class));
        list.add(new Page("Query[BETA]","USER",QueryPage.class));
		list.add(new Page("Printing","USER",PrintingManagerPage.class));
		list.add(new Page("Printer","PRINTER",PrinterPage.class));
		list.add(new Page("Malfunctions","ADMIN",MalfunctionsPage.class));
		list.add(new Page("Admin", "ADMIN", AdminPage.class));
//        list.add(new Page("Test", "ADMIN", Test.class));
        list.add(new Page("thisWeekFix", "ADMIN", CardsThisWeek.class));
        list.add(new Page("Profile","USER",ProfilePage.class));
		return list;
	}

	protected String getUserName(){
		return userName;
	}


    protected void show(Component c){
        c.add(new AttributeModifier("style", "display:inline;"));
    }
    protected void hide(Component c){
        c.add(new AttributeModifier("style", "display:none;"));
    }
}