package obee.pages;

import custom.classes.Invoice;
import custom.classes.User;
import custom.components.MyCheckGroup;
import database.MongoHandler;
import obee.pages.master.MasterPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

@AuthorizeInstantiation("USER")
@SuppressWarnings({ "unchecked", "rawtypes", "serial"})
public class CashierPage extends MasterPage{

	MongoHandler mongo =MongoHandler.getInstance();
    private Form<Object> form;
    private MyCheckGroup<String> usersCheckGroup;
    private ArrayList<String> USERS;
    private ArrayList<String> userchoices;
    private TextField<Integer> jadAmountInput;
    private TextField<String> commentTextInput;
    private ListView invoicesView;


    public CashierPage(final PageParameters params) {
		super(params,"Cashier");
        intiLists();
        initForms();
        initComponents();
    }

    private void intiLists() {
        USERS = mongo.geUsernamesList();
        userchoices = new ArrayList<String>();
    }

    private void initComponents() {
        invoicesView = new ListView("invoices", MongoHandler.getInstance().getAllInvoices()){
            @Override
            protected void populateItem(ListItem item) {
                Invoice inv = (Invoice) item.getDefaultModelObject();
                item.add(new Label("dateLbl", inv.getDateString()));
                item.add(new Label("amountLbl", inv.getJadAmount()));
                item.add(new Label("usersLbl", inv.getUsersString()));
                item.add(new Label("commentLbl", inv.getComment()));
            }
        };
        form.add(invoicesView);
        commentTextInput = new TextField<String>("commentTextInput", new Model<String>());
        form.add(commentTextInput);
        jadAmountInput = new NumberTextField<Integer>("jadAmountInput", new Model(0));
        form.add(jadAmountInput);
        usersCheckGroup =  new MyCheckGroup<String>("usersCheckGroup",new Model(userchoices), USERS);
        form.add(usersCheckGroup);
    }

    private void initForms() {
        form = new Form<Object>("form"){
            @Override
            protected void onSubmit() {
                Integer jadAmount = Integer.parseInt(jadAmountInput.getDefaultModelObject().toString());
                List<String> userNames = (ArrayList<String>)usersCheckGroup.getDefaultModelObject();
                String comment = commentTextInput.getDefaultModelObjectAsString();
                Invoice in = new Invoice(userNames, jadAmount, comment);
                for (String userName : in.getUserNames()){
                    User u = MongoHandler.getInstance().getUser(userName);
                    u.increaseJad(jadAmount);
                    u.UPDATE();
                }
                in.UPDATE();
                setResponsePage(CashierPage.class);
            }
        };
        add(form);
    }
}

