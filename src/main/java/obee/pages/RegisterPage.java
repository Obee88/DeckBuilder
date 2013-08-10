package obee.pages;

import obee.pages.master.MasterPage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import custom.classes.Administration;
import custom.classes.User;

import database.MongoHandler;

public class RegisterPage extends MasterPage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String nickName,eMail,password,statusMessage;

	public RegisterPage(final PageParameters parameters) {
		super(parameters,"Register");
		PropertyModel<String> nickModel = new PropertyModel<String>(this, "nickName");
		PropertyModel<String> eMailModel = new PropertyModel<String>(this, "eMail");
		PropertyModel<String> passwordModel = new PropertyModel<String>(this, "password");
		PropertyModel<String> statusMessageModel = new PropertyModel<String>(this, "statusMessage");


        Form<?> form = new RegisterForm("registerForm");
        form.add(new TextField<String>("nickName", nickModel));
        form.add(new TextField<String>("eMail", eMailModel));
        form.add(new PasswordTextField("password", passwordModel));
        form.add(new Label("msg", statusMessageModel));
        add(form);
        
	}

	class RegisterForm extends Form<Object>{
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RegisterForm(String id) {
			super(id);
		}
		
		@Override
		public void onSubmit(){
			MongoHandler mongo;
			mongo = MongoHandler.getInstance();
			String username = RegisterPage.this.nickName;
			String eMail = RegisterPage.this.eMail;
			String password = RegisterPage.this.password;
			if(mongo.userExist(username)){
				info("Username "+nickName+" is not available.");
				return;
			}
			Administration.addToAuthorizationList(
				new User(
						username,
						eMail,
						password
						)
				);
			setResponsePage(getApplication().getHomePage());
		}
		
	}
}