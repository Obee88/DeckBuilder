package obee.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import custom.components.panels.PlusMinusPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import custom.classes.Administration;
import custom.classes.CardGenerator;
import custom.classes.ShowingCard;
import custom.classes.User;
import custom.components.IEventListener;
import custom.components.ListChooser;
import custom.components.panels.CardSelectionPanel;
import custom.components.panels.CardView;

import obee.pages.master.MasterPage;
import org.apache.wicket.util.string.*;

@AuthorizeInstantiation("USER")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RecyclePage extends MasterPage {
	private static final long serialVersionUID = 1L;
	private Form form;
	private CardSelectionPanel cardsPanel,recycleShortlistPanel;
	private List<ShowingCard> tradeList;
	private CardSelectionPanel sacCards;
	private CardView cardView;
	private List<ShowingCard> sacList;
    private PlusMinusPanel weeksOldPanel;
    private AjaxLink<Object> filterButton;
    User usr =  currentUser;
    private ArrayList<ShowingCard> recycleShortlistList;
    private Form recycleIllegalForm;
    private AjaxLink<Object> fillFromShortlist;


    public RecyclePage(final PageParameters params) {
		super(params,"Recycle");
		initLists();
		initComponents();
		initForms();
		initBehaviours();
	}

	private void initLists() {
        recycleShortlistList = (ArrayList<ShowingCard>) usr.getRecycleShortlistShowingCards();
		tradeList = usr.getTradingShowingCards();
//		List<ShowingCard> tmpList = new ArrayList<ShowingCard>();
//		for(ShowingCard sc: tradeList)
//			if(sc.printed.toLowerCase().equals("false"))
//				tmpList.add(sc);
//		tradeList= tmpList;
		sacList=new ArrayList<ShowingCard>();
	}

	private void initForms() {
		form = new Form("form"){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				super.onSubmit();
                int jad = 0;
				for(ShowingCard sc : sacList){
                    try{
                        usr.removeFromTrading(sc.cardId);
                        usr.removeFromUsing(sc.cardId);
                        usr.removeFromBooster(sc.cardId);
                        usr.removeFromRecycleShortlist(sc.cardId);
                        mongo.deleteCard(sc.cardId);
                    } catch (Exception ignorable){
                    }
                    finally {
                        jad++;
                    }
                }
				usr.increaseJad(jad);
                usr.UPDATE();
                String msg ="";
                if(jad==1)
				    msg = jad + " jad added to your balance!";
                else
                    msg = jad + " jada added to your balance!";
				setResponsePage(RecyclePage.class,new PageParameters().add("infoMsg",msg));
			}
		};
		form.add(cardsPanel);
		form.add(sacCards);
		form.add(cardView);
        form.add(weeksOldPanel);
        form.add(filterButton);
        add(form);
	}

	private void initComponents() {
        fillFromShortlist = new AjaxLink<Object>("fillFromShortlist"){

            @Override
            public void onClick(AjaxRequestTarget target) {
                while (sacList.size()<6 && recycleShortlistList.size()>0){
                    ShowingCard sc = recycleShortlistList.remove(0);
                    recycleShortlistPanel.removeChoice(sc);
                    sacCards.addChoice(sc);
                    sacList.add(sc);
                    target.add(recycleShortlistPanel);
                    target.add(sacCards);
                }
            }
        };
        add(fillFromShortlist);
        recycleShortlistPanel = new CardSelectionPanel("recycleShortlistPanel",recycleShortlistList);
        recycleShortlistPanel.listChooser.setMaxRows(19);
        recycleShortlistPanel.setPrintCheckBoxVisible(false);
        recycleShortlistPanel.setFilterVisible(false);
        recycleShortlistPanel.setOutputMarkupId(true);
		add(recycleShortlistPanel);
        cardsPanel = new CardSelectionPanel("userCards", (ArrayList<ShowingCard>) tradeList);
		cardsPanel.listChooser.setMaxRows(19);
		cardsPanel.setPrintCheckBoxVisible(false);
	    sacCards = new CardSelectionPanel("sacCards",(ArrayList<ShowingCard>) sacList);
	    sacCards.listChooser.setMaxRows(6);
	    sacCards.setPrintCheckBoxVisible(false);
	    sacCards.setFilterVisible(false);
        sacCards.setOutputMarkupId(true);
		cardView = new CardView("cardView");
		cardView.setRarityLblVisible(true);
		cardsPanel.listChooser.addEventListener(cardView);
		sacCards.listChooser.addEventListener(cardView);
        recycleShortlistPanel.listChooser.addEventListener(cardView);
        final List<String> rarity = new ArrayList<String>();
        rarity.add("common");rarity.add("uncommon");
        rarity.add("rare"); rarity.add("mythic");
        cardsPanel.sort(new Comparator<ShowingCard>(){
                    @Override
                    public int compare(ShowingCard o1, ShowingCard o2) {
                        int i1 = rarity.indexOf(o1.cardInfo.rarity);
                        int i2 = rarity.indexOf(o2.cardInfo.rarity);
                        if(i1>i2) return 1;
                        else if(i1<i2) return -1;
                        else{
                            int mc1 = o1.cardInfo.convertedManaCost;
                            int mc2 = o2.cardInfo.convertedManaCost;
                            if(mc1>mc2) return -1;
                            else if(mc1<mc2) return 1;
                            if(o1.cardId>o2.cardId) return -1;
                            if(o2.cardId>o1.cardId) return 1;

                        }
                        return 0;
                    }
                });
        weeksOldPanel = new PlusMinusPanel("weeksOldPanel");
        filterButton = new AjaxLink<Object>("filterButton"){
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                boolean ismore = weeksOldPanel.isMore();
                int wo = weeksOldPanel.getNumber();
                List<ShowingCard> filteredList = currentUser.getTradingShowingCardsOlderThan(wo, ismore);
                filteredList.removeAll(sacList);
                tradeList=filteredList;
                cardsPanel.setChoices(tradeList);
                ajaxRequestTarget.add(cardsPanel);
            }
        };
	}

	private void initBehaviours() {
		IEventListener trejd = new IEventListener() {
			@Override
			public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
					String eventType) {
				if(eventType.equals("onDblClk")){
					CardSelectionPanel from, to;
					List<ShowingCard> fromList, toList;
					String senderId = ((ListChooser<ShowingCard>)sender).getParentPanel().getId();
                    ShowingCard sc = null;
					if(senderId.equals("userCards")){
						from = cardsPanel;
                        sc= (ShowingCard) from.listChooser.getDefaultModelObject();
						to = sacCards;
						fromList = tradeList;
						toList = sacList;
						if(to.getChoices().size()>=6) {
							info("Can't sac more than 6 cards at once.");
							target.add(feedback);
							return target;
						}
					} else if(senderId.equals("sacCards")){
						to = cardsPanel;
						from = sacCards;
                        sc = (ShowingCard) from.listChooser.getDefaultModelObject();
						toList = tradeList;
						fromList = sacList;
                        usr.removeFromRecycleShortlist(sc.cardId);
                        usr.addToTrading(sc.cardId);
                        sc.status = "trading";
                        usr.UPDATE();
                        sc.UPDATE();
					} else return target;
					if(sc==null) return target;
					fromList.remove(sc);
					from.setChoices(fromList);
					toList.add(sc);
					to.setChoices(toList);
					target.add(from);
					target.add(to);
				}
				return target;
			}
		};
		sacCards.listChooser.addEventListener(trejd);
		cardsPanel.listChooser.addEventListener(trejd);

        IEventListener recycle = new IEventListener() {
            @Override
            public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
                                             String eventType) {
                if(eventType.equals("onDblClk")){
                    CardSelectionPanel from, to;
                    List<ShowingCard> fromList, toList;
                    String senderId = ((ListChooser<ShowingCard>)sender).getParentPanel().getId();
                    ShowingCard sc = null;
                    if(senderId.equals("recycleShortlistPanel")){
                        from = recycleShortlistPanel;
                        sc= (ShowingCard) from.listChooser.getDefaultModelObject();
                        to = sacCards;
                        fromList = recycleShortlistList;
                        toList = sacList;
                        if(to.getChoices().size()>=6) {
                            info("Can't sac more than 6 cards at once.");
                            target.add(feedback);
                            return target;
                        }
                    } else return target;
                    if(sc==null) return target;
                    fromList.remove(sc);
                    from.setChoices(fromList);
                    toList.add(sc);
                    to.setChoices(toList);
                    target.add(from);
                    target.add(to);
                }
                return target;
            }
        };
        recycleShortlistPanel.listChooser.addEventListener(recycle);
    }
}
