package obee.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import obee.pages.master.MasterPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import custom.classes.Card;
import custom.classes.CardGenerator;
import custom.classes.ShowingCard;
import custom.classes.StartingDeck;
import custom.classes.User;
import custom.components.IEventListener;
import custom.components.ListChooser;
import custom.components.panels.CardSelectionPanel;
import custom.components.panels.CardView;
import custom.components.panels.InfoPanel;


import database.MongoHandler;

@AuthorizeInstantiation("USER")
@SuppressWarnings({ "unchecked", "rawtypes", "serial"})
public class BoosterPage extends MasterPage{

	MongoHandler mongo =MongoHandler.getInstance();
	
 	private ArrayList<ShowingCard> booster,using,trading;
	private Boolean isGood=true, isBad=false;
	InfoPanel infoPanel;
	Form<?> form;
	CheckBox goodCheck, badCheck;
	User u = mongo.getUser(getUserName());
	int cardsAv = u.cardsAvailable();
	CardSelectionPanel boosterPanel, goodChoice, badChoice;
	CardView cardView;
	boolean includeSubFolders = false;

	public BoosterPage(final PageParameters params) {
		super(params,"Boosters");
		getShowingCards();
		initComponents();
        addBehaviors();
		form = new Form("form") {
			@Override
			protected void onSubmit() {
				save();
				info("Saved!");
			}
		};
		//form.add(subFoldersCheck);
		form.add(goodCheck);
		form.add(badCheck);
		form.add(cardView);
		form.add(boosterPanel);
		form.add(goodChoice);
		form.add(badChoice);
		add(infoPanel);
		add(form);
	    boosterPanel.listChooser.addEventListener(infoPanel);
	    goodChoice.listChooser.addEventListener(infoPanel);
	    badChoice.listChooser.addEventListener(infoPanel);
	    boosterPanel.listChooser.addEventListener(cardView.image);
	    goodChoice.listChooser.addEventListener(cardView.image);
	    badChoice.listChooser.addEventListener(cardView.image);
	    IEventListener bigListener = new IEventListener() {
	    	@Override
	    	public AjaxRequestTarget onEvent(AjaxRequestTarget target,
	    			Object sender, String eventType) {
	    		if(eventType.equals("onDblClk")){
	    			CardSelectionPanel csp =(CardSelectionPanel)((ListChooser)sender).getParentPanel();
					ShowingCard sc = (ShowingCard) csp.listChooser.getDefaultModelObject();
					if(sc==null) return target;
					csp.removeChoice(sc);
					u.removeFromBooster(sc.cardId);
					if(isGood){
						goodChoice.addChoice(sc);
						u.addToUsing(sc.cardId);
					} else{
						badChoice.addChoice(sc);
						u.addToTrading(sc.cardId);
					}
					u.UPDATE();
					target.add(boosterPanel);
					target.add(goodChoice);
					target.add(badChoice);
	    		}
				return target;
	    	}
	    };
		boosterPanel.listChooser.addEventListener(bigListener);
		IEventListener smallListener = new IEventListener() {
		    	@Override
		    	public AjaxRequestTarget onEvent(AjaxRequestTarget target,
		    			Object sender, String eventType) {
		    		if(eventType.equals("onDblClk")){
		    			CardSelectionPanel csp =(CardSelectionPanel)((ListChooser)sender).getParentPanel();
						ShowingCard sc = (ShowingCard) csp.listChooser.getDefaultModelObject();
						if(sc==null) return target;
						csp.removeChoice(sc);
						if(csp.getId().equals("goodList"))
							u.removeFromUsing(sc.cardId);
						else
							u.removeFromTrading(sc.cardId);
						boosterPanel.addChoice(sc);
						u.addToBooster(sc.cardId);
						u.UPDATE();
						target.add(boosterPanel);
						target.add(goodChoice);
						target.add(badChoice);
		    		}
					return target;
		    	}
		    };
		goodChoice.listChooser.addEventListener(smallListener);
		badChoice.listChooser.addEventListener(smallListener);
		Form<?> getBoostersForm = new Form("getBoostersForm"){
			@Override
			protected void onSubmit() {
				
				u.addToBooster(CardGenerator.generateBooster(cardsAv,getUserName()));
				u.setLastBoosterDate(new Date());
				u.UPDATE();
				info(cardsAv+" cards added!");
				setResponsePage(BoosterPage.class);
			}
			@Override
			public boolean isVisible() {
				return super.isVisible()&&cardsAv>0;
			}
		};
		getBoostersForm.add(new Label("cardsAv",cardsAv));
		add(getBoostersForm);
		final RadioGroup<StartingDeck> group = new RadioGroup<StartingDeck>("group", new Model());
        Form<?> SDform = new Form("starterDeckForm")
        {
            @Override
            protected void onSubmit()
            {
                String sd = group.getDefaultModelObjectAsString();
                (new StartingDeck(sd)).dodajSe(getUserName());
				info(sd+" deck added!");
                setResponsePage(BoosterPage.class);
            }
			@Override
			public boolean isVisible() {
				User u = mongo.getUser(getUserName());
				return super.isVisible()&&u.getStarterDeck()==null;
			}
        };
        ListView<StartingDeck> decks = new ListView<StartingDeck>("decks",
                StartingDeck.getDecks())
            {

                @Override
                protected void populateItem(ListItem<StartingDeck> item)
                {
                    item.add(new Radio<StartingDeck>("radio", item.getModel(), group));
                    StartingDeck sd = new StartingDeck(item.getDefaultModelObjectAsString());
                    item.add(new ExternalLink("link", sd.Link,sd.name));
                }

            };
        group.add(decks);
        add(SDform);
        SDform.add(group);
	}
	
	protected void save() {
		User u = mongo.getUser(getUserName());
		booster = (ArrayList<ShowingCard>) boosterPanel.getChoices();
		using = (ArrayList<ShowingCard>) goodChoice.getChoices();
		using.addAll(u.getSubfolders().getAllCards());
		trading = (ArrayList<ShowingCard>) badChoice.getChoices();
		u.setBooster(booster);
		u.setTrading(trading);
		u.setUsing(using);
		u.UPDATE();
		for(ShowingCard b : booster){
			b.status = "booster";
			b.UPDATE();
		}
		for(ShowingCard b : trading){
			b.status = "trading";
			b.UPDATE();
		}
		for(ShowingCard b : using){
			b.status = "using";
			b.UPDATE();
		}
		
	}

	private void initComponents() {
		infoPanel=new InfoPanel("panel",u.hasRole("ADMIN"));
        infoPanel.setPurposeLabelVisible(false);
		cardView = new CardView("cardView");
		boosterPanel = new CardSelectionPanel("boosterPanel", booster) ;
		boosterPanel.listChooser.setMaxRows(18);
		goodChoice = new CardSelectionPanel("goodList", using); 
		goodChoice.listChooser.setMaxRows(8);
		goodChoice.setFilterVisible(false);
		goodChoice.setPrintCheckBoxVisible(false);
		badChoice = new CardSelectionPanel("badList", trading);
		badChoice.listChooser.setMaxRows(8);
		badChoice.setFilterVisible(false);
		badChoice.setPrintCheckBoxVisible(false);
		goodCheck = new CheckBox("goodCheck", new PropertyModel(this, "isGood"));
		goodCheck.setOutputMarkupId(true);
		badCheck = new CheckBox("badCheck", new PropertyModel(this, "isBad"));
		badCheck.setOutputMarkupId(true);
	}

	private void addBehaviors() {
		OnChangeAjaxBehavior goodChecked = new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				isBad=!isGood;
				target.add(badCheck);
			}
		};
		OnChangeAjaxBehavior baddChecked = new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				isGood=!isBad;
				target.add(goodCheck);
			}
		};
		OnChangeAjaxBehavior printerChecked = new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) boosterPanel.printIndicator.getDefaultModelObject();
				goodChoice.listChooser.setPrinterIndicator(b);
				badChoice.listChooser.setPrinterIndicator(b);
				target.add(goodChoice);
				target.add(badChoice);
			}
		};
		boosterPanel.printIndicator.add(printerChecked);
		goodCheck.add(goodChecked);
		badCheck.add(baddChecked);
	}

	private void getShowingCards() {
		User u =mongo.getUser(getUserName());
		booster=Lc2Lsc(u.getBoosterCards());
		using= (ArrayList<ShowingCard>) usingListWithoutSubfolders();
		trading=Lc2Lsc(u.getTradingCards());
	}

	private List<ShowingCard> usingListWithoutSubfolders() {
		List<ShowingCard> ret = new ArrayList<ShowingCard>();
		for(ShowingCard sc: mongo.getUser(getUserName()).getUsingShowingCards()){
			if(!sc.isInSubfolder())
				ret.add(sc);
		}
		return ret;
	}
	
	private ArrayList<ShowingCard> Lc2Lsc(List<Card> cards){
		ArrayList<ShowingCard> ret = new ArrayList<ShowingCard>();
		for(Card c: cards)
			ret.add(new ShowingCard(c));
		return ret;
	}	

}

