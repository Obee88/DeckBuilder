package obee.pages;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import custom.classes.ShowingCard;
import custom.components.MyCheckGroup;
import custom.components.panels.*;
import database.MongoHandler;
import obee.pages.master.MasterPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@AuthorizeInstantiation("USER")
@SuppressWarnings({ "unchecked", "rawtypes", "serial"})
public class QueryPage extends MasterPage{

	MongoHandler mongo =MongoHandler.getInstance();
    private Form<Object> form;
    private TextField<String> textTbx,subtypeTbx, nameTbx;
    private CardSelectionPanel resultBox;
    private InfoPanel infoPanel;
    private CardView cardView;
    private List<String> TYPES, typeChoices, RARITY, rarityChoices;
    private CheckBoxMultipleChoice<String> typeCbg;
    private CheckBoxMultipleChoice<String> rarityCbg;
    private AjaxLink<Object> typeInclude, typeExclude;
    private AjaxLink<Object> subtypeInclude, subtypeExclude;
    private AjaxLink<Object> rarityInclude,rarityExclude;
    private AjaxLink<Object> textInclude,textExclude;
    private HidingQueryPanel manaCostPanel, colorPanel, colorNumPanel;
    private HidingQueryTextPanel textPanel;
    private HidingQueryPanel rarityPanel;
    private HidingQueryPanel typePanel;
    private HidingQueryTextPanel subtypePanel;
    private HidingQueryTextPanel namePanel;
    private Form<Object> unprintForm;
    private AjaxButton unprintBtn;
    private PageParameters parameters;
    private AjaxButton querySubmitBtn;
    private HidingQueryPanel datetimePanel;
    private ArrayList<String> COLORS, USERS;
    private ArrayList<String> colorChoices, userchoices;
    private MyCheckGroup<String> usersCheckGroup;
    private HidingQueryPanel userPanel;

    public QueryPage(final PageParameters params) {
		super(params,"Query[BETA]");
        parameters=params;
        intiLists();
        initForms();
        initComponents(params);
    }

    private void intiLists() {
        TYPES= mongo.getAllCardTypes();
        typeChoices= new ArrayList<String>();
        RARITY = mongo.getAllCardRarityTypes();
        rarityChoices = new ArrayList<String>();
        USERS = mongo.geUsernamesList();
        userchoices = new ArrayList<String>();
        COLORS = new ArrayList<String>();
        COLORS.add("Blue");COLORS.add("Red");
        COLORS.add("White");COLORS.add("Green");COLORS.add("Black");
        colorChoices = new ArrayList<String>();
    }

    private void initComponents(PageParameters params) {
        infoPanel=new InfoPanel("panel",currentUser.getRoles().contains("ADMIN"));
        cardView = new CardView("cardView", currentUser.isInHackerMode());
        nameTbx= new TextField<String>("component",new Model<String>());
        nameTbx.setOutputMarkupId(true);
        textTbx= new TextField<String>("component",new Model<String>());
        textTbx.setOutputMarkupId(true);
        ArrayList<ShowingCard> scl = getList(params);
        resultBox = new CardSelectionPanel("resultBox",new ArrayList<ShowingCard>());
        resultBox.setChoices(scl);
        resultBox.setFilterVisible(true);
        resultBox.setPrintCheckBoxVisible(false);
        resultBox.setTitleVisible(false);
        resultBox.listChooser.setMaxRows(18);
        resultBox.listChooser.addEventListener(cardView);
        resultBox.listChooser.addEventListener(infoPanel);
        form.add(infoPanel);
        form.add(cardView);
        form.add(resultBox);
        typeCbg = new MyCheckGroup<String>( "component", new Model((ArrayList<String>)typeChoices), TYPES);
        typeCbg.setOutputMarkupId(true);
        rarityCbg = new MyCheckGroup<String>("component", new Model((ArrayList<String>)rarityChoices), RARITY);
        rarityCbg.setOutputMarkupId(true);
        subtypeTbx = new TextField<String>("component",new Model<String>());
        subtypeTbx.setOutputMarkupId(true);
        namePanel = new HidingQueryTextPanel("namePanel", nameTbx) {
            @Override
            public Object getCondition() {
                String text = getComponent().getDefaultModelObjectAsString().toLowerCase();
                return Pattern.compile(text, Pattern.CASE_INSENSITIVE);
            }
        };
        querySubmitBtn = new AjaxButton("querySubmitBtn"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);    //To change body of overridden methods use File | Settings | File Templates.
            }
        };
        querySubmitBtn.setOutputMarkupId(true);
        form.add(querySubmitBtn);
        namePanel.setText("Name:");
        form.add(namePanel);
        typePanel = new HidingQueryPanel("typePanel", typeCbg) {
            @Override
            public Object getCondition() {
                BasicDBList dbl = strL2DbL((ArrayList<String>)getComponent().getDefaultModelObject());
                return new BasicDBObject("$in",dbl);
            }
        };
        typePanel.setText("Type:");
        form.add(typePanel);
        subtypePanel = new HidingQueryTextPanel("subtypePanel", subtypeTbx) {
            @Override
            public Object getCondition() {
                String text = getComponent().getDefaultModelObjectAsString().toLowerCase();
                return Pattern.compile(text, Pattern.CASE_INSENSITIVE);
            }
        };
        subtypePanel.setText("Subtype:");
        form.add(subtypePanel);
        rarityPanel = new HidingQueryPanel("rarityPanel", rarityCbg) {
            @Override
            public Object getCondition() {
                BasicDBList dbl = strL2DbL((ArrayList<String>)getComponent().getDefaultModelObject());
                return new BasicDBObject("$in",dbl);
            }
        };
        rarityPanel.setText("Rarity:");
        form.add(rarityPanel);
        textPanel= new HidingQueryTextPanel("textPanel", textTbx){
            @Override
            public Object getCondition() {
                String text = getComponent().getDefaultModelObjectAsString().toLowerCase();
                return Pattern.compile(text, Pattern.CASE_INSENSITIVE);
            }
        };
        textPanel.setText("Text:");
        form.add(textPanel);
        manaCostPanel= new HidingQueryPanel("manaCostPanel", new PlusMinusPanel("component")) {
            @Override
            public Object getCondition() {
                PlusMinusPanel comp =(PlusMinusPanel)getComponent();
                return comp.getCondition();
            }
        };
        manaCostPanel.setText("Mana cost:");
        form.add(manaCostPanel);
        colorNumPanel = new HidingQueryPanel("colorNumPanel", new PlusMinusPanel("component")) {
            @Override
            public Object getCondition() {
                PlusMinusPanel comp =(PlusMinusPanel)getComponent();
                return comp.getCondition();
            }

            @Override
            public boolean isVisible() {
                return super.isVisible();
            }
        };
        colorNumPanel.setText("Color num:");
        form.add(colorNumPanel);
        MyCheckGroup clrCb = new MyCheckGroup<String>("component",new Model(colorChoices), COLORS);
        clrCb.setOutputMarkupId(true);
        colorPanel = new HidingQueryPanel("colorPanel", clrCb){
            @Override
            public Object getCondition() {
                BasicDBList dbl = new BasicDBList();
//                for(String choice : (ArrayList<String>)getComponent().getDefaultModelObject()){
//                    if(choice.equals("Blue"))
//                        dbl.add(new BasicDBObject("manaCost",Pattern.compile("U", Pattern.CASE_INSENSITIVE)));
//                    if(choice.equals("Black"))
//                        dbl.add(new BasicDBObject("manaCost",Pattern.compile("B", Pattern.CASE_INSENSITIVE)));
//                    if(choice.equals("Red"))
//                        dbl.add(new BasicDBObject("manaCost",Pattern.compile("R", Pattern.CASE_INSENSITIVE)));
//                    if(choice.equals("White"))
//                        dbl.add(new BasicDBObject("manaCost",Pattern.compile("W", Pattern.CASE_INSENSITIVE)));
//                    if(choice.equals("Green"))
//                        dbl.add(new BasicDBObject("manaCost",Pattern.compile("G", Pattern.CASE_INSENSITIVE)));
//                }
                for(String choice : (ArrayList<String>)getComponent().getDefaultModelObject()) {
                    if(choice.equals("Blue"))
                        dbl.add(new BasicDBObject("colorsList","U"));
                    if(choice.equals("Black"))
                        dbl.add(new BasicDBObject("colorsList","B"));
                    if(choice.equals("Red"))
                        dbl.add(new BasicDBObject("colorsList","R"));
                    if(choice.equals("White"))
                        dbl.add(new BasicDBObject("colorsList","W"));
                    if(choice.equals("Green"))
                        dbl.add(new BasicDBObject("colorsList","G"));
                }
                return dbl;
            }
        };
        colorPanel.setText("Color:");
        form.add(colorPanel);
        form.setDefaultButton(querySubmitBtn);
        datetimePanel = new HidingQueryPanel("datetimePanel",new PlusMinusPanel("component")){
            @Override
            public Object getCondition() {
                PlusMinusPanel comp =(PlusMinusPanel)getComponent();
                int value = comp.getNumber();
                boolean ismore = comp.isMore();
                DateTime dt = new DateTime(DateTimeZone.forID("Asia/Tokyo"));
                if(value>0){
                    dt= dt.withDayOfWeek(DateTimeConstants.MONDAY)
                            .withHourOfDay(0)
                            .withMinuteOfHour(0)
                            .withSecondOfMinute(0)
                            .withMillisOfSecond(0);
                    value--;
                }
                dt = dt.minusWeeks(value);
                BasicDBObject obj = new BasicDBObject(ismore?"$lt":"$gte",dt.toDate());
                return obj;
            }
        };
        datetimePanel.setText("Weeks old:");
        form.add(datetimePanel);
        usersCheckGroup =  new MyCheckGroup<String>("component",new Model(userchoices), USERS);
        usersCheckGroup.setOutputMarkupId(true);
        userPanel = new HidingQueryPanel("userPanel", usersCheckGroup) {
            @Override
            public Object getCondition() {
                BasicDBList dbl = new BasicDBList();
                for(String choice : (ArrayList<String>)getComponent().getDefaultModelObject()){
                    dbl.add(choice);
                }
                return dbl;
            }
        };
        userPanel.setText("Users: ");
        form.add(userPanel);
    }

    private ArrayList<ShowingCard> getList(PageParameters params) {
        ArrayList<ShowingCard> ret = new ArrayList<ShowingCard>();
        String strL = params.get("idList").toString("");
        String[] strs = strL.split(",");
        for (int i = 0; i < strs.length; i++) {
            if(strs[i].equals("")) continue;
            int id = Integer.parseInt(strs[i]);
            ret.add(new ShowingCard(mongo.getCard(id)));
        }
        return ret;
    }

    private void initForms() {
        form = new Form<Object>("form"){
            @Override
            protected void onSubmit() {
                Object color =null, type=null,subtype=null,rarity=null,text=null,manaCost=null,name=null, creationDate=null, users=null, colorNum=null;
                if(colorPanel.isShown())
                    color = colorPanel.getCondition();
                if(datetimePanel.isShown())
                    creationDate = datetimePanel.getCondition();
                if(namePanel.isShown())
                    name = namePanel.getCondition();
                if(textPanel.isShown())
                    text = textPanel.getCondition();
                if(subtypePanel.isShown())
                    subtype = subtypePanel.getCondition();
                if(rarityPanel.isShown())
                    rarity = rarityPanel.getCondition();
                if(typePanel.isShown())
                    type = typePanel.getCondition();
                if(manaCostPanel.isShown())
                    manaCost= manaCostPanel.getCondition();
                if(colorNumPanel.isVisible() && colorNumPanel.isShown())
                    colorNum= colorNumPanel.getCondition();
                if(userPanel.isShown())
                    users = userPanel.getCondition();

                PageParameters pageParameters = new PageParameters();
                pageParameters.add("idList",mongo.queryAll(name,type, subtype, rarity, text,manaCost, creationDate,color, users, colorNum));
                setResponsePage(QueryPage.class, pageParameters);
            }
        };
        add(form);
        unprintForm = new Form<Object>("unprintForm"){
            @Override
            protected void onSubmit() {
                ShowingCard sc = infoPanel.getCard();
                sc.printed = "false";
                sc.UPDATE();

            }
        };
        add(unprintForm);
        unprintBtn = new AjaxButton("unprintBtn"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(QueryPage.class, parameters );
            }
        };
        unprintForm.add(unprintBtn);
        if(!currentUser.hasRole("USER"))
            hide(unprintBtn);

    }

    private BasicDBList strL2DbL(List<String> strL){
        BasicDBList dbl = new BasicDBList();
        for(String s: strL)
            dbl.add(s);
        return dbl;
    }
}

