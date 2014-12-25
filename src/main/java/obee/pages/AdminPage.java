package obee.pages;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import custom.classes.*;
import obee.pages.master.MasterPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import suport.MailSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@AuthorizeInstantiation("ADMIN")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AdminPage extends MasterPage {
	private static final long serialVersionUID = 1L;
    private final Form<Object> resetPasswordsForm;
    private final Form<Object> setDatesForm;
    private final Form<Object> fixForm;
    private final TextField<String> oldNameTbx, newNameTbx, newMailTbx;
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
				setResponsePage(AdminPage.class);
			}
			
		};
		pendingUsers=(ArrayList)Administration.getAuthorizationList();
		authForm.setVisible(!pendingUsers.isEmpty());
		manyChoice = new ListMultipleChoice("authList",
				new Model(selectedUsers),
				new Model(pendingUsers)).setMaxRows(5);
		authForm.add(manyChoice);
		add(authForm);
		//rename user form
        Form<?> renameUserForm = new Form<Void>("renameUserForm") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                String oldName = oldNameTbx.getDefaultModelObjectAsString();
                String newName = newNameTbx.getDefaultModelObjectAsString();
                String newMail = newMailTbx.getDefaultModelObjectAsString();
                mongo.renameUser(oldName,newName,newMail);
                setResponsePage(AdminPage.class);
            }

        };
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
		        setResponsePage(AdminPage.class);
			}
			
		};
		userList = mongo.getAllUsers();
		selectedUser = userList.get(0);
		dropDown = new DropDownChoice<User>("userSelector", 
				new Model(selectedUser),
				new Model(userList));
		
		rolesForm.add(dropDown);
        oldNameTbx = new TextField<String>("oldNameTbx", new Model<String>());
        newNameTbx = new TextField<String>("newNameTbx", new Model<String>());
        newMailTbx = new TextField<String>("newMailTbx", new Model<String>());
        renameUserForm.add(oldNameTbx); renameUserForm.add(newNameTbx); renameUserForm.add(newMailTbx);
        add(renameUserForm);
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
						setResponsePage(AdminPage.class);
					}
				} else{
					User u = mongo.getUser(uName);
					UserMessage msg = new UserMessage(u.getNextMessageId(),subject,text);
					u.addMessage(msg);
					u.UPDATE();
					setResponsePage(AdminPage.class);
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
				setResponsePage(AdminPage.class);
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
        setDatesForm= new Form<Object>("quickfix"){
            @Override
            protected void onSubmit() {

                User u = mongo.getUser("Bubi");
                BasicDBList ids = new BasicDBList();
                List<ShowingCard> boosterSC = u.getBoosterShowingCards();
                for(ShowingCard sc:boosterSC){
                    ids.add(sc.cardId);
                    u.removeFromBooster(sc.cardId);
                    u.UPDATE();
                }
                mongo.cardsCollection.remove(new BasicDBObject("id",new BasicDBObject("$in",ids)));
            }
        };
        add(setDatesForm);
        fixForm = new Form<Object>("fixForm"){
            @Override
            protected void onSubmit() {
                for (User u : mongo.getAllUsers()){
                    u.setLastBoosterDate(new DateTime(u.getLastBoosterDate()).minusWeeks(1).toDate());
                    u.UPDATE();
                }

            }

        };
        add(fixForm);
	}

    private void setLastBoosterDates() {

        DBCursor usersCur = mongo.usersCollection.find(new BasicDBObject(), new BasicDBObject("userName",1));
        while(usersCur.hasNext()){
            DBObject obj = usersCur.next();
            String username = obj.get("userName").toString();
            int id = mongo.getHighestCardId(username);
            Card c = mongo.getCard(id);
            mongo.usersCollection.update(new BasicDBObject("userName",username),new BasicDBObject("$set", new BasicDBObject("lastBoosterDate",c.getCreationDate())));
        }
    }
}
