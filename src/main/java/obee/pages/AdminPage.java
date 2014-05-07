package obee.pages;

import java.util.*;

import custom.classes.*;
import obee.pages.master.MasterPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import org.joda.time.DateTime;
import suport.MailSender;

@AuthorizeInstantiation("ADMIN")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AdminPage extends MasterPage {
	private static final long serialVersionUID = 1L;
    private final Form<Object> resetPasswordsForm;
    private final Form<Object> setDatesForm;
    private final Form<Object> fixForm;
    ArrayList<User> selectedUsers = new ArrayList<User>();
	ArrayList<User> pendingUsers;
	ArrayList<User> userList; 
	ArrayList<String> userStringList;
	String message ="";
	CheckBox isAdminBox, isUserBox,isPrinterBox;
	User roleSelectedUser;
	ListMultipleChoice<String> manyChoice;
	Form<?> authForm,msgForm, deleteForm;
	DropDownChoice<User> dropDown;
	DropDownChoice<String> msgUserSelector;
	TextField<String> subjectField, textField;
	User selectedUser;
	String msgSelectedUser;
	AjaxLink<Object> deleteButton;
	private TextField<String> startIdTbx;
	private TextField<String> endIdTbx;
	
	@SuppressWarnings("serial")
	public AdminPage(final PageParameters params) {
		super(params,"Admin");
		//Authorization
		authForm = new Form<Void>("authForm") {

			@Override
			protected void onSubmit() {
				Administration.insertUsers(selectedUsers);
				List<User> tmpUsers = new ArrayList<User>();
				for (User u : selectedUsers) 
					tmpUsers.add(u);
				for (User user : tmpUsers) {
					selectedUsers.remove(user);
					pendingUsers.remove(user);
				}
				setResponsePage(StorePage.class);
			}
			
		};
		pendingUsers=(ArrayList)Administration.getAuthorizationList();
		authForm.setVisible(!pendingUsers.isEmpty());
		manyChoice = new ListMultipleChoice("authList",
				new Model(selectedUsers),
				new Model(pendingUsers)).setMaxRows(5);
		authForm.add(manyChoice);
		add(authForm);
		
		//Roles
		Form<?> rolesForm = new Form<Void>("rolesForm") {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				boolean isAdmin = (Boolean)isAdminBox.getDefaultModelObject();
				boolean isUser = (Boolean)isUserBox.getDefaultModelObject();
				boolean isPrinter = (Boolean)isPrinterBox.getDefaultModelObject();
				roleSelectedUser.setRoles(isAdmin,isUser,isPrinter);
				roleSelectedUser.UPDATE();
		        setResponsePage(StorePage.class);
			}
			
		};
		userList = mongo.getAllUsers();
		selectedUser = userList.get(0);
		dropDown = new DropDownChoice<User>("userSelector", 
				new Model(selectedUser),
				new Model(userList));
		
		rolesForm.add(dropDown);
		isAdminBox = new CheckBox("isAdmin", new Model(selectedUser.isAdmin()));
		isUserBox = new CheckBox("isUser", new Model(selectedUser.isUser()));
		isPrinterBox = new CheckBox("isPrinter", new Model(selectedUser.isPrinter()));
		isAdminBox.setOutputMarkupId(true);
		isUserBox.setOutputMarkupId(true);
		isPrinterBox.setOutputMarkupId(true);
		rolesForm.add(isAdminBox).add(isUserBox).add(isPrinterBox);
		add(rolesForm);
		
		OnChangeAjaxBehavior onChangeAjaxBehavior = new OnChangeAjaxBehavior(){

			@Override
		    protected void onUpdate(AjaxRequestTarget target)
		    {
		        User u =(User) dropDown.getDefaultModelObject();
		        roleSelectedUser=u;
		        isAdminBox.setDefaultModelObject(u.isAdmin());
		        isUserBox.setDefaultModelObject(u.isUser());
				isPrinterBox.setDefaultModelObject(u.isPrinter());
		        target.add(isAdminBox);
		        target.add(isUserBox);
		        target.add(isPrinterBox);
		    }
		};
		dropDown.add(onChangeAjaxBehavior);
		
		msgForm = new Form<Object>("msgForm"){
			@Override
			protected void onSubmit() {
				String subject = subjectField.getModelObject().toString();
				String text = textField.getModelObject().toString();
				String uName = msgUserSelector.getModelObject();
				if(uName.equals("all")){
					for(User u: userList){
						UserMessage msg = new UserMessage(u.getNextMessageId(),subject,text);
						u.addMessage(msg);
						u.UPDATE();
						setResponsePage(StorePage.class);
					}
				} else{
					User u = mongo.getUser(uName);
					UserMessage msg = new UserMessage(u.getNextMessageId(),subject,text);
					u.addMessage(msg);
					u.UPDATE();
					setResponsePage(StorePage.class);
				}
				
			}
		};
		textField = new TextField<String>("textTbx", new Model<String>());
		subjectField = new TextField<String>("subjectTbx", new Model<String>());
		userStringList = new ArrayList<String>();
		userStringList.add("all");
		for(User u : userList)
			userStringList.add(u.getUserName());
		msgSelectedUser=userStringList.get(0);
		msgUserSelector = new DropDownChoice<String>("msgUserSelector", 
				new Model(msgSelectedUser),
				new Model(userStringList));
		msgForm.add(textField);
		msgForm.add(subjectField);
		msgForm.add(msgUserSelector);
		add(msgForm);
		
		deleteForm = new Form<Object>("deleteForm"){
			@Override
			protected void onSubmit() {
				super.onSubmit();
				int startId = Integer.parseInt(startIdTbx.getModelObject().toString());
				int endId = Integer.parseInt(endIdTbx.getModelObject().toString());
				for(int id =startId; id<=endId;id++){
					mongo.removeCard(id);
				}
				setResponsePage(StorePage.class);
			}
		};
		add(deleteForm);
		startIdTbx = new TextField<String>("startIdTbx", new Model<String>());
		endIdTbx = new TextField<String>("endIdTbx", new Model<String>());
		deleteForm.add(startIdTbx);
		deleteForm.add(endIdTbx);
        resetPasswordsForm = new Form<Object>("resetPasswordsForm"){
            @Override
            protected void onSubmit() {
                Random rnd = new Random();
                for(User u:mongo.getAllUsers()){
                    Integer  passnum = rnd.nextInt(100000)+100000;
                    String pass = passnum.toString();
                    u.changePassword(pass);
                    MailSender.send(u.getEmail(),"resetiran ti je password", "novi password ti je: "+pass+"   ...  promjeni ga kad se ulogiras");
                }
            }
        };
        add(resetPasswordsForm);
        setDatesForm= new Form<Object>("setDatesForm"){
            @Override
            protected void onSubmit() {

                int wc = mongo.getNumberOfUsers()*32;
                int twc = 0;
                DateTime date = new DateTime().minusDays(5);
                for(int cardId = Administration.getMaxCardId();cardId>0;cardId--,twc++){
                    if(twc==wc){
                        twc=0;
                        date=date.minusWeeks(1);
                    }
                    if(mongo.cardExist(cardId)){
                        ShowingCard sc = mongo.getShowingCard(cardId);
                        sc.setCreationDate(date.toDate());
                        sc.UPDATE();
                    }
                }
            }
        };
        add(setDatesForm);
        fixForm = new Form<Object>("fixForm"){
            @Override
            protected void onSubmit() {
                for(User u : mongo.getAllUsers()){
                    Date d = u.getLastBoosterDate();
                    DateTime dt = new DateTime(d);
                    u.setLastBoosterDate(dt.plusDays(1).toDate());
                    u.UPDATE();
                }
                info("done");
            }


        };
        add(fixForm);
	}
}
