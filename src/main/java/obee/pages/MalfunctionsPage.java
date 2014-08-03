package obee.pages;

import java.util.*;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import obee.pages.master.MasterPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import custom.classes.Administration;
import custom.classes.Card;
import custom.classes.ShowingCard;
import custom.classes.User;
import custom.components.IEventListener;
import custom.components.ListChooser;
import custom.components.panels.CardSelectionPanel;
import custom.components.panels.CardView;
import custom.components.panels.InfoPanel;

@SuppressWarnings({"serial"})
@AuthorizeInstantiation("ADMIN")
public class MalfunctionsPage extends MasterPage {

	
	private Form<Object> form, checkDoublesForm;
	private ArrayList<ShowingCard> ownerNotInListList;
	private CardSelectionPanel ownerNotInListPanel;
	private ArrayList<ShowingCard> inListNotOwnerList;
	private CardSelectionPanel inListNotOwnerPanel;
	private InfoPanel infoPanel;
	private CardView image;
	private ShowingCard selectedCard = null;
	private Label cardNameLbl;
	private DropDownChoice<String> userChooser;
	private ArrayList<String> usersStringList;
	private Form<?> allForm;
	private Form<Object> setRightStatuses;
    private AjaxLink<Object> checkOwnerNotInListButton, checkInListNotOwnerButton;
    private Form<Object> clearTradeList;
    private Form<Object> chechInProposalForm;
    private AjaxLink<Object> clearListUserNilBtn;


    public MalfunctionsPage(PageParameters params) {
		super(params, "Malfunctions");
		initLists();
		initForm();
		initComponents();
		initBehaviours();
	
	}

	private void initLists() {
		ownerNotInListList = new ArrayList<ShowingCard>();
		inListNotOwnerList = new ArrayList<ShowingCard>();
		usersStringList = new ArrayList<String>();
		List<User> usrs = mongo.getAllUsers();
		for(User usr : usrs){
			usersStringList.add(usr.getUserName());
		}
//		fillOwnerNotInList();
//      fillInListNotOwner();
	}

    private void fillInListNotOwner(){
        BasicDBObject keys = new BasicDBObject("userName",1);
        keys.append("_id",0);
        keys.append("userCards",1);
        BasicDBObject cardKeys =new BasicDBObject("id",1);
        cardKeys.append("owner",1);
        cardKeys.append("_id",0);
        DBCursor usersCur = mongo.usersCollection.find(new BasicDBObject(),keys);
        while(usersCur.hasNext()){
            DBObject usrObj = usersCur.next();
            String userName = usrObj.get("userName").toString();
            DBObject userCardsObj  = (DBObject)usrObj.get("userCards");
            List<BasicDBList> ll = new ArrayList<BasicDBList>();
            ll.add((BasicDBList) userCardsObj.get("trading"));
            ll.add((BasicDBList) userCardsObj.get("using"));
            ll.add((BasicDBList) userCardsObj.get("boosters"));
            for(BasicDBList dbl : ll){
                for(Object oId : dbl){
                    Integer id = (Integer)oId;
                    DBObject cardObj = mongo.cardsCollection.findOne(new BasicDBObject("id", id), cardKeys);
                    try{
                        String owner = cardObj.get("owner").toString();
                        if(!owner.equals(userName))
                            inListNotOwnerList.add(new ShowingCard(mongo.getCard(id)));
                    } catch (NullPointerException ne){
                        System.out.println(ne.getMessage()+", id:"+id);
                    }
                }
            }
        }
    }

    List<String> basicLandNames = new ArrayList<String>(Arrays.asList(new String[]{"Island","Swamp","Plains","Forest","Mountain"}));
    private void fillOwnerNotInList(){
        BasicDBObject cardKeys =new BasicDBObject("id",1);
        cardKeys.append("owner",1);
//        cardKeys.append("rarity",1);
        cardKeys.append("_id",0);
        BasicDBList basicLandNamesDBL = new BasicDBList();
        DBCursor allIdsCur = mongo.cardsCollection.find(new BasicDBObject(),cardKeys);
        while(allIdsCur.hasNext()){
            DBObject cardObj = allIdsCur.next();
            Integer id = (Integer)cardObj.get("id");
            String owner = cardObj.get("owner").toString();
            DBObject listOwner = mongo.usersCollection.findOne(new BasicDBObject("userCards.trading", id));
            if(listOwner==null)
                listOwner = mongo.usersCollection.findOne(new BasicDBObject("userCards.using",id));
            if(listOwner==null)
                listOwner = mongo.usersCollection.findOne(new BasicDBObject("userCards.boosters",id));
            if(listOwner==null)
                listOwner = mongo.usersCollection.findOne(new BasicDBObject("userCards.recycleShortlist",id));
            if(listOwner==null || !listOwner.get("userName").toString().equals(owner))   {
                ShowingCard sc =new ShowingCard(mongo.getCard(id));
                if(!basicLandNames.contains(sc.name))
                    ownerNotInListList.add(sc);
            }
        }
    }

    private List<Integer> userCardsToList(DBObject obj) {
        List<Integer> ret = new ArrayList<Integer>();
        BasicDBList usingDBL =  (BasicDBList)obj.get("using");
        BasicDBList tradingDBL =  (BasicDBList)obj.get("trading");
        BasicDBList boostersDBL =  (BasicDBList)obj.get("boosters");
        for (Object o : usingDBL)
            ret.add((Integer)o);
        for (Object o : tradingDBL)
            ret.add((Integer)o);
        for (Object o : boostersDBL)
            ret.add((Integer)o);
        return ret;
    }

    private void initForm() {
        chechInProposalForm = new Form<Object>("checkInProposal"){
            @Override
            protected void onSubmit() {
                int cnt = mongo.remFalseInProposalCards();
                info(cnt + " false in proposal cards removed!");
            }
        };
        add(chechInProposalForm);
		form = new Form<Object>("form"){
			@Override
			protected void onSubmit() {
				super.onSubmit();
				User targetUser = mongo.getUser(userChooser.getDefaultModelObjectAsString());
				mongo.removeFromAllUserLists(selectedCard.cardId);
				targetUser.addToBooster(selectedCard.cardId);
				targetUser.UPDATE();
				selectedCard.setOwner(targetUser.getUserName());
				selectedCard.setStatus("booster");
				selectedCard.UPDATE();
                info("1 card repaired");
				setResponsePage(MalfunctionsPage.class);
			}
		};
		add(form);
		allForm = new Form<Object>("allForm"){
			@Override
			protected void onSubmit() {
				super.onSubmit();
				for(ShowingCard sc: ownerNotInListList){
					String targetUser = sc.owner;
                    mongo.removeFromAllUserLists(sc.cardId);
                    mongo.usersCollection.update(
                            new BasicDBObject("userName", targetUser),
                            new BasicDBObject("$push", new BasicDBObject("userCards.boosters", sc.cardId))
                    );
                    sc.setStatus("booster");
                    sc.UPDATE();
				}
                info(ownerNotInListList.size()+ " cards repaired!");
                setResponsePage(MalfunctionsPage.class);
			}
		};
		add(allForm);
		checkDoublesForm = new Form<Object>("checkDoublesForm"){
			@Override
			protected void onSubmit() {
				super.onSubmit();
                int count=0;
                BasicDBList basicLandsIds = mongo.getBasicLandsInfoIds();
				for(User u: mongo.getAllUsers()){
                    Set<ShowingCard> using = new TreeSet<ShowingCard>();
                    Set<ShowingCard> trading = new TreeSet<ShowingCard>();
                    Set<ShowingCard> boosters = new TreeSet<ShowingCard>();
                    DBCursor cur = mongo.cardsCollection.find(new BasicDBObject("owner", u.getUserName())
                            .append("cardInfoId", new BasicDBObject("$nin",basicLandsIds)));
                    int cnt = 0;
                    while(cur.hasNext()){
                        DBObject obj = cur.next();
                        ShowingCard sc = new ShowingCard(new Card(obj));
                        if(!sc.status.equals("removing"))
                            cnt++;
                        else
                            continue;
                        String status = obj.get("status").toString();
                        if(status.equals("using"))
                            using.add(sc);
                        if(status.equals("trading"))
                            trading.add(sc);
                        if(status.equals("booster"))
                            boosters.add(sc);
                    }
                    u.setBooster(boosters);
                    u.setUsing(using);
                    u.setTrading(trading);
                    u.UPDATE();
                    count += cnt- boosters.size()-trading.size()-using.size();
				}
                info(count +" cards removed!");
			}
		};
		add(checkDoublesForm);
		setRightStatuses = new Form<Object>("setRightStatuses"){
			@Override
			protected void onSubmit() {
				super.onSubmit();
                int counter = 0;
				for(User u: mongo.getAllUsers()){
                    for(ShowingCard c : u.getBoosterShowingCards())
                        if (!c.status.equals("booster")){
                            counter++;
                            c.status = "booster";
                            c.UPDATE();
                        }
                    for(ShowingCard c : u.getUsingShowingCards())
                        if (!c.status.equals("using")){
                            counter++;
                            c.status = "using";
                            c.UPDATE();
                        }
                    for(ShowingCard c : u.getTradingShowingCards())
                        if (!c.status.equals("trading")){
                            counter++;
                            c.status = "trading";
                            c.UPDATE();
                        }
				}
                info(counter +" cards changed!");
                setResponsePage(MalfunctionsPage.class);
			}
		};
		add(setRightStatuses);
        clearTradeList = new Form<Object>("clearTradeList"){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                int counter = 0;
                List<ShowingCard> prList = Administration.getPrintingReadyList();
                for(ShowingCard sc : prList){
                    sc.printed  = "false";
                    sc.UPDATE();
                    counter++;
                }
                Administration.removeFromPrinter(prList);
                info(counter +" cards changed!");
                setResponsePage(MalfunctionsPage.class);

            }
        };
        add(clearTradeList);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initComponents() {
		cardNameLbl = new Label("cardNameLbl",new Model());
		cardNameLbl.setOutputMarkupId(true);
		form.add(cardNameLbl);
		userChooser = new DropDownChoice<String>("userChooser",
				new Model(usersStringList.get(0)),
				new Model(usersStringList));
		userChooser.setOutputMarkupId(true);
		form.add(userChooser);
		ownerNotInListPanel = new CardSelectionPanel("ownerNotInListPanel", ownerNotInListList);
		form.add(ownerNotInListPanel);
		inListNotOwnerPanel = new CardSelectionPanel("inListNotOwnerPanel", inListNotOwnerList);
		form.add(inListNotOwnerPanel);
		infoPanel = new InfoPanel("infoPanel",currentUser.getRoles().contains("ADMIN"));
		form.add(infoPanel);
		image = new CardView("image");
		form.add(image);
        checkOwnerNotInListButton = new AjaxLink<Object>("checkOwnerNotInListButton"){
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                fillOwnerNotInList();
                ownerNotInListPanel.setChoices(ownerNotInListList);
                info(ownerNotInListList.size() + " cards detected");
                ajaxRequestTarget.add(feedback);
                ajaxRequestTarget.add(ownerNotInListPanel);
            }
        };
        form.add(checkOwnerNotInListButton);
        checkInListNotOwnerButton = new AjaxLink<Object>("checkInListNotOwnerButton"){
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                fillInListNotOwner();
                inListNotOwnerPanel.setChoices(inListNotOwnerList);
                info(inListNotOwnerList.size() + " cards detected");
                ajaxRequestTarget.add(feedback);
                ajaxRequestTarget.add(inListNotOwnerPanel);
            }
        };
        form.add(checkInListNotOwnerButton);

        clearListUserNilBtn = new AjaxLink<Object>("clearListUserNilBtn"){
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                BasicDBList remList = new BasicDBList();
                for(ShowingCard sc : ownerNotInListList){
                    int id = sc.cardId;
                    remList.add(id);
                }
                info(mongo.cardsCollection.remove(new BasicDBObject("id",new BasicDBObject("$in",remList))).toString());
            }
        };
        form.add(clearListUserNilBtn);
	}

	private void initBehaviours() {
		ownerNotInListPanel.listChooser.addEventListener(infoPanel);
		inListNotOwnerPanel.listChooser.addEventListener(infoPanel);
		ownerNotInListPanel.listChooser.addEventListener(image);
		inListNotOwnerPanel.listChooser.addEventListener(image);
		
		IEventListener selectionChange = new IEventListener() {
			@SuppressWarnings("unchecked")
			@Override
			public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
					String eventType) {
				ListChooser<ShowingCard> from;
				from = (ListChooser<ShowingCard>) sender;
				selectedCard = (ShowingCard) from.getDefaultModelObject();
				cardNameLbl.setDefaultModelObject(selectedCard.name);
				userChooser.setDefaultModelObject(selectedCard.owner);
				target.add(cardNameLbl);
				target.add(userChooser);
				return target;
			}
		};
		ownerNotInListPanel.listChooser.addEventListener(selectionChange);
		inListNotOwnerPanel.listChooser.addEventListener(selectionChange);
		
	}
}
