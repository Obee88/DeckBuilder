package obee;

import obee.pages.SignInPage;
import obee.pages.UserMainPage;

import obee.SignInSession;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see obee.MagicStarter#main(String[])
 */
public class WicketApplication extends AuthenticatedWebApplication
{    	
	public final String CURRENT_VERSION= "1.22";
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return UserMainPage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();
        getDebugSettings().setDevelopmentUtilitiesEnabled(true);
	}
	
	@Override
    public Session newSession(Request request, Response response)
    {
        return new SignInSession(request);
    }
	
	@Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass()
    {
        return SignInSession.class;
    }

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return SignInPage.class;
	}
}
