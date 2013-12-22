package obee.pages;

import java.util.ArrayList;
import java.util.List;

import obee.pages.master.MasterPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import custom.classes.Administration;
import custom.classes.Card;
import custom.classes.ShowingCard;
import custom.classes.User;
import custom.components.IEventListener;
import custom.components.ListChooser;
import custom.components.panels.CardSelectionPanel;
import custom.components.panels.CardView;
import database.MongoHandler;

@AuthorizeInstantiation("USER")
@SuppressWarnings({"serial","rawtypes"})
public class PrintingManagerPage extends MasterPage {

	MongoHandler mongo =MongoHandler.getInstance();
	User u ;
	
	private ArrayList<ShowingCard> using,printing;
	Form<?> form, landForm;
	CardSelectionPanel usingCards,printingCards;
	CardView image;
	Label forestLbl, plainsLbl, islandLbl, swampLbl, mountainLbl;
	Integer forestN=0, islandN=0, plainsN=0,swampN=0, mountainN=0;
	Link forestBtn,islandBtn,plainsBtn,mountainBtn,swampBtn;
	
	public PrintingManagerPage(PageParameters params) {
		super(params,"Printing");
		u= mongo.getUser(getUserName());
		initLists();
		initForm();
		initComponents();
        addBehaviors();
        
	}

	private void initForm() {
        form = new Form("form") {
			@Override
			protected void onSubmit() {
				printing = (ArrayList<ShowingCard>) printingCards.getChoices();
				for(ShowingCard sc : printing)
					if(!sc.printed.toLowerCase().equals("false")){
						info("You can't print same card twice!");
						return;
					}
					
				Administration.sendToPrinter(printing);
				setResponsePage(PrintingManagerPage.class);
			}
		};
		add(form);
        landForm = new Form("landForm") {
			@Override
			protected void onSubmit() {
				sendLandForPrinting();
				setResponsePage(PrintingManagerPage.class);
			}
		};
		add(landForm);
	}
	
	private void initComponents() {
		usingCards = new CardSelectionPanel("usingCards", using);
		usingCards.listChooser.setMaxRows(14);
		printingCards = new CardSelectionPanel("printingCards", printing);
		printingCards.listChooser.setMaxRows(14);
		printingCards.setFilterVisible(false);
		image = new CardView("image");
		forestLbl = new Label("forestLbl", new PropertyModel<Integer>(this, "forestN"));
		forestLbl.setOutputMarkupId(true);
		islandLbl = new Label("islandLbl", new PropertyModel<Integer>(this, "islandN"));
		islandLbl.setOutputMarkupId(true);
		swampLbl = new Label("swampLbl", new PropertyModel<Integer>(this, "swampN"));
		swampLbl.setOutputMarkupId(true);
		plainsLbl = new Label("plainsLbl", new PropertyModel<Integer>(this, "plainsN"));
		plainsLbl.setOutputMarkupId(true);
		mountainLbl = new Label("mountainLbl", new PropertyModel<Integer>(this, "mountainN"));
		mountainLbl.setOutputMarkupId(true);
		forestBtn = new Link("forestBtn") {
			@Override
			public void onClick() {
				forestN++;
			}
		};
		plainsBtn = new Link("plainsBtn") {
			@Override
			public void onClick() {
				plainsN++;
			}
		};
		islandBtn = new Link("islandBtn") {
			@Override
			public void onClick() {
				islandN++;
			}
		};
		swampBtn = new Link("swampBtn") {
			@Override
			public void onClick() {
				swampN++;
			}
		};
		mountainBtn = new Link("mountainBtn") {
			@Override
			public void onClick() {
				mountainN++;
			}
		};
		form.add(image);
		landForm.add(forestLbl); landForm.add(forestBtn);
		landForm.add(islandLbl); landForm.add(islandBtn);
		landForm.add(mountainLbl); landForm.add(mountainBtn);
		landForm.add(swampLbl); landForm.add(swampBtn);
		landForm.add(plainsLbl); landForm.add(plainsBtn);
		form.add(usingCards);
		form.add(printingCards);
	}

	private void addBehaviors() {
		IEventListener trejder = new IEventListener() {
			@Override
			public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
					String eventType) {
				if(eventType.equals("onDblClk")){
					CardSelectionPanel from, to;
					from = (CardSelectionPanel) ((ListChooser) sender).getParentPanel();
					ShowingCard sc = from.listChooser.getSelectedChoice();
					if(from.getId().equals("usingCards"))
						to = printingCards;
					else 
						to=usingCards;
					from.removeChoice(sc);
					to.addChoice(sc);
					target.add(from);
					target.add(to);
				}
				return target;
			}
		};
		OnChangeAjaxBehavior printerChecked = new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) usingCards.printIndicator.getDefaultModelObject();
				printingCards.listChooser.setPrinterIndicator(b);
				target.add(printingCards);
			}
		};
		usingCards.printIndicator.add(printerChecked);
		usingCards.listChooser.addEventListener(trejder);
		printingCards.listChooser.addEventListener(trejder);
		usingCards.listChooser.addEventListener(image.image);
		printingCards.listChooser.addEventListener(image.image);
		printingCards.setPrintCheckBoxVisible(false);
	}
	
	private void initLists() {
		using= Lc2Lsc(u.getUsingCards());
		printing = new ArrayList<ShowingCard>();
	}

	private ArrayList<ShowingCard> Lc2Lsc(List<Card> cards){
		ArrayList<ShowingCard> ret = new ArrayList<ShowingCard>();
		for(Card c: cards)
			ret.add(new ShowingCard(c));
		return ret;
	}	

	private void sendLandForPrinting() {
		List<ShowingCard> landList = new ArrayList<ShowingCard>();
		String owner = getUserName();
		for(int i = 0 ; i<forestN; i++){
			Card c = Card.generateFromCardName("Forest", owner);
			ShowingCard sc = new ShowingCard(c);
			landList.add(sc);
		}
		for(int i = 0 ; i<islandN; i++){
			Card c = Card.generateFromCardName("Island", owner);
			ShowingCard sc = new ShowingCard(c);
			landList.add(sc);
		}
		for(int i = 0 ; i<plainsN; i++){
			Card c = Card.generateFromCardName("Plains", owner);
			ShowingCard sc = new ShowingCard(c);
			landList.add(sc);
		}
		for(int i = 0 ; i<swampN; i++){
			Card c = Card.generateFromCardName("Swamp", owner);
			ShowingCard sc = new ShowingCard(c);
			landList.add(sc);
		}
		for(int i = 0 ; i<mountainN; i++){
			Card c = Card.generateFromCardName("Mountain", owner);
			ShowingCard sc = new ShowingCard(c);
			landList.add(sc);
		}
		Administration.sendToPrinter(landList);
	}
}
