package obee.pages;

import obee.pages.master.MasterPage;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

@SuppressWarnings("serial")
public class SignOut extends MasterPage {
	
	public SignOut(final PageParameters params) {
		super(params,"SignOut");
		add(new BookmarkablePageLink<Object>("link",UserMainPage.class));
		getSession().invalidate();
	}
}
