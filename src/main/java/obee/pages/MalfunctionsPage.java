package obee.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import obee.pages.master.MasterPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
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
	private Form<Object> allToBoostersForm;
	

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
		for(int id = 1; id<= Administration.getMaxCardId();id++) if (mongo.cardExist(id)) {
			Card c;
            User ownerUser;
			try{
				c = mongo.getCard(id);
                ownerUser = mongo.getUser(c.getOwner());
			}
			catch(NullPointerException e){
				Administration.addProblematicId(id);
                continue;
			}
			if(ownerUser.hasCardIdInBoosters(id))
				continue;
			if(ownerUser.hasCardIdInUsing(id))
				continue;
			if(ownerUser.hasCardIdInTrading(id))
				continue;

			ShowingCard sc = new ShowingCard(c);
			if(sc.name.equals("Plains")||sc.name.toLowerCase().equals("swamp")||sc.name.toLowerCase().equals("forest")||
					sc.name.toLowerCase().equals("island")||sc.name.toLowerCase().equals("mountain"))
				continue;
			
			ownerNotInListList.add(sc);
		}
		for(User user: mongo.getAllUsers()){
			for(Card c: user.getBoosterCards())
				if(!c.getOwner().equals(user.getUserName()))
					inListNotOwnerList.add(new ShowingCard(c));
			for(Card c: user.getUsingCards())
				if(!c.getOwner().equals(user.getUserName()))
					inListNotOwnerList.add(new ShowingCard(c));
			for(Card c: user.getTradingCards())
				if(!c.getOwner().equals(user.getUserName()))
					inListNotOwnerList.add(new ShowingCard(c));
		}
	}

	private void initForm() {
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
				setResponsePage(MalfunctionsPage.class);
			}
		};
		add(form);
		allForm = new Form<Object>("allForm"){
			@Override
			protected void onSubmit() {
				super.onSubmit();
				for(ShowingCard sc: ownerNotInListList){
					User o = mongo.getUser(sc.owner);
					String st = sc.status;
					if(st.equals("booster"))
						o.addToBooster(sc.cardId);
					else if(st.equals("using"))
						o.addToUsing(sc.cardId);
					else if (st.equals("trading")) {
						o.addToTrading(sc.cardId);
					} 
					o.UPDATE();
				}
			}
		};
		add(allForm);
		checkDoublesForm = new Form<Object>("checkDoublesForm"){
			@Override
			protected void onSubmit() {
				super.onSubmit();
                int count=0;
				for(User u: mongo.getAllUsers()){
					List<Integer> tradeList = mongo.getTradeCardsIds(u.getUserName());
					List<Integer> usingList = mongo.getUsingCardsIds(u.getUserName());
					List<Integer> boostersList = mongo.getBoostersCardsIds(u.getUserName());
                    int cardsnum= tradeList.size()+usingList.size()+boostersList.size();
					List<ShowingCard> tradeCardsList = new ArrayList<ShowingCard>();
					List<ShowingCard> usingCardsList = new ArrayList<ShowingCard>();
					List<ShowingCard> boostersCardsList = new ArrayList<ShowingCard>();
					Set<Integer> tradeSet = new TreeSet<Integer>();
					Set<Integer> usingSet = new TreeSet<Integer>();
					Set<Integer> boostersSet = new TreeSet<Integer>();
					tradeSet.addAll(tradeList);
					usingSet.addAll(usingList);
					boostersSet.addAll(boostersList);
					tradeList.clear();
					usingList.clear();
					boostersList.clear();
					for(Integer id : tradeSet)
						if(mongo.cardExist(id))
							tradeCardsList.add(new ShowingCard(mongo.getCard(id)));
					for(Integer id: usingSet)
						if(mongo.cardExist(id))
							usingCardsList.add(new ShowingCard(mongo.getCard(id)));
					for(Integer id: boostersSet)
						if(mongo.cardExist(id))
							boostersCardsList.add(new ShowingCard(mongo.getCard(id)));
					u.setBooster(boostersCardsList);
					u.setTrading(tradeCardsList);
					u.setUsing(usingCardsList);
					u.UPDATE();
                    int removed = cardsnum-tradeCardsList.size()-usingCardsList.size()-boostersCardsList.size();
                    count+=removed;
				}
                info(count +" cards removed!");
			}
		};
		add(checkDoublesForm);
		allToBoostersForm = new Form<Object>("allToBoostersForm"){
			@Override
			protected void onSubmit() {
				super.onSubmit();
				for(User u: mongo.getAllUsers()){
					u.getSubfolders().clearAll();
					u.clearAllLists();
					u.UPDATE();
				}
				for(int id=0; id<=Administration.getMaxCardId();id++){
					if(mongo.cardExist(id)){
						Card c = mongo.getCard(id);
						ShowingCard sc = new ShowingCard(c);
						if(sc.cardInfo.type.equals("land")) continue;
						User owner = mongo.getUser(c.getOwner());
						String status = sc.status;
						if(status.equals("booster"))
							owner.addToBooster(sc.cardId);
						else if(status.equals("using"))
							owner.addToUsing(sc.cardId);
						else 
							owner.addToTrading(sc.cardId);
						owner.UPDATE();
					}
				}
			}
		};
		add(allToBoostersForm);
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
		infoPanel = new InfoPanel("infoPanel",mongo.getUser(getUserName()).getRoles().contains("ADMIN"));
		form.add(infoPanel);
		image = new CardView("image");
		form.add(image);
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
