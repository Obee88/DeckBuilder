package obee.pages;

import custom.classes.ShowingCard;
import custom.classes.User;
import custom.classes.WishlistsChecker;
import custom.components.IEventListener;
import custom.components.ListChooser;
import custom.components.panels.CardSelectionPanel;
import custom.components.panels.CardView;
import custom.components.panels.InfoPanel;
import custom.components.panels.PlusMinusPanel;
import obee.pages.master.MasterPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AuthorizeInstantiation("USER")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RecyclePage extends MasterPage {
	private static final long serialVersionUID = 1L;
	private CardSelectionPanel cardsPanel,recycleShortlistPanel;
	private List<ShowingCard> tradeList, dontWantRecycleList;
	private CardView cardView;
    User usr =  currentUser;
    private ArrayList<ShowingCard> recycleShortlistList;
    private Form recycleAllForm;
    private InfoPanel infoPanel;
    private CardSelectionPanel dontWantRecyclePanel;


    public RecyclePage(final PageParameters params) {
		super(params,"Recycle");
		initLists();
		initComponents();
		initForms();
		initBehaviours();
        AjaxEventBehavior keypress =new AjaxEventBehavior("onkeypress"){
            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes
                                                        attributes) {
                super.updateAjaxAttributes(attributes);

                IAjaxCallListener listener = new AjaxCallListener(){
                    @Override
                    public CharSequence getPrecondition(Component component) {
                        //this javascript code evaluates wether an
                        //ajaxcall is necessary.
                        //Here only by keyocdes for 1,2,3
                        return  "var keycode ="+
                                "Wicket.Event.keyCode(attrs.event);" +
                                "if (keycode >=49 && keycode <= 51)" +
                                "    return true;" +
                                "else" +
                                "    return false;";
                    }
                };
                attributes.getAjaxCallListeners().add(listener);

                //Append the pressed keycode to the ajaxrequest
                attributes.getDynamicExtraParameters()
                        .add("var eventKeycode = "+
                                "Wicket.Event.keyCode(attrs.event);" +
                                "return {keycode: eventKeycode};");
            }
            @Override
            protected void onEvent(AjaxRequestTarget target) {

                final Request request = RequestCycle.get().getRequest();
                final String code =request.getRequestParameters()
                        .getParameterValue("keycode").toString("");
                String key = null;
                if (code.equals("51"))
                    key = "3";
                if (code.equals("49"))
                    key = "1";
                if (code.equals("50"))
                    key = "2";
                if(cardsPanel.listChooser.selectedChoice==null || key==null) return ;
                target = cardsPanel.listChooser.informListeners(target, "onKeyPress-"+key);
            }
        };
        add(keypress);
	}

	private void initLists() {
        WishlistsChecker wlc = WishlistsChecker.fromMongo(mongo);
        recycleShortlistList = (ArrayList<ShowingCard>) usr.getRecycleShortlistShowingCards();
        wlc.checkList(recycleShortlistList);
		tradeList = usr.getTradingShowingCards();
        dontWantRecycleList = usr.getDontWantRecycleShowingCards();
        tradeList.removeAll(dontWantRecycleList);
        tradeList.removeAll(recycleShortlistList);
        wlc.checkList(tradeList);
	}



	private void initForms() {
	}

	private void initComponents() {
        recycleAllForm = new Form("recycleAllForm"){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                int jad = 0;
                for(ShowingCard sc : recycleShortlistList){
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
        add(recycleAllForm);

        infoPanel = new InfoPanel("infoPanel", currentUser.isAdmin());
        infoPanel.setOutputMarkupId(true);
        infoPanel.setInterestListlVisible(true);
        add(infoPanel);
        cardView = new CardView("cardView", currentUser.isInHackerMode());
        cardView.setRarityLblVisible(true);
        add(cardView);
        recycleShortlistPanel = new CardSelectionPanel("recycleShortlistPanel",recycleShortlistList);
        recycleShortlistPanel.listChooser.setMaxRows(19);
        recycleShortlistPanel.setPrintCheckBoxVisible(false);
        recycleShortlistPanel.setFilterVisible(false);
        recycleShortlistPanel.setOutputMarkupId(true);
        recycleShortlistPanel.markInterests(true);
        recycleShortlistPanel.listChooser.addEventListener(cardView);
        recycleShortlistPanel.listChooser.addEventListener(infoPanel);
		add(recycleShortlistPanel);
        dontWantRecyclePanel = new CardSelectionPanel("dontWantRecyclePanel", (ArrayList<ShowingCard>) dontWantRecycleList);
        dontWantRecyclePanel.listChooser.setMaxRows(19);
        dontWantRecyclePanel.setPrintCheckBoxVisible(false);
        dontWantRecyclePanel.setFilterVisible(true);
        dontWantRecyclePanel.setOutputMarkupId(true);
        dontWantRecyclePanel.markInterests(true);
        dontWantRecyclePanel.listChooser.addEventListener(cardView);
        dontWantRecyclePanel.listChooser.addEventListener(infoPanel);
        add(dontWantRecyclePanel);
        cardsPanel = new CardSelectionPanel("userCards", (ArrayList<ShowingCard>) tradeList);
		cardsPanel.listChooser.setMaxRows(19);
		cardsPanel.setPrintCheckBoxVisible(false);
        cardsPanel.markInterests(true);
		cardsPanel.listChooser.addEventListener(cardView);
        cardsPanel.listChooser.addEventListener(infoPanel);
        add(cardsPanel);

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
					if(senderId.equals("dontWantRecyclePanel")){
						from = dontWantRecyclePanel;
                        sc= (ShowingCard) from.listChooser.getDefaultModelObject();
						to = cardsPanel;
						fromList = dontWantRecycleList;
						toList = tradeList;
                        usr.removeFromDontWantRecycle(sc.cardId);
                        sc.status = "trading";
                        usr.UPDATE();
                        sc.UPDATE();
					} else if(senderId.equals("recycleShortlistPanel")){
						to = cardsPanel;
						from = recycleShortlistPanel;
                        sc = (ShowingCard) from.listChooser.getDefaultModelObject();
						toList = tradeList;
						fromList = recycleShortlistList;
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
		dontWantRecyclePanel.listChooser.addEventListener(trejd);
		recycleShortlistPanel.listChooser.addEventListener(trejd);

        IEventListener keyPressListener = new IEventListener() {
            @Override
            public AjaxRequestTarget onEvent(AjaxRequestTarget target,
                                             Object sender, String eventType) {
                if(eventType.startsWith("onKeyPress-")){
                    String key = eventType.substring(eventType.length()-1);
                    if(key.equals("3")){
                        CardSelectionPanel from, to;
                        List<ShowingCard> fromList, toList;
                        String senderId = ((ListChooser<ShowingCard>)sender).getParentPanel().getId();
                        ShowingCard sc = null;
                        if(senderId.equals("userCards")){
                            from = cardsPanel;
                            sc= (ShowingCard) from.listChooser.getDefaultModelObject();
                            to = recycleShortlistPanel;
                            fromList = tradeList;
                            if (!fromList.contains(sc)) return target;
                            toList = recycleShortlistList;
                            sc.status = "removing";
                            usr.addToRecycleShortlist(sc);
                        } else if (senderId.equals("dontWantRecyclePanel")){
                            from = dontWantRecyclePanel;
                            sc= (ShowingCard) from.listChooser.getDefaultModelObject();
                            to = recycleShortlistPanel;
                            fromList = dontWantRecycleList;
                            if (!fromList.contains(sc)) return target;
                            toList = recycleShortlistList;

                        } else return target;
                        if(sc==null) return target;
                        fromList.remove(sc);
                        from.setChoices(fromList);
                        toList.add(sc);
                        to.setChoices(toList);
                        sc.UPDATE();
                        usr.UPDATE();
                        target.add(from);
                        target.add(to);
                    }
                    if(key.equals("1")){
                        CardSelectionPanel from, to;
                        List<ShowingCard> fromList, toList;
                        String senderId = ((ListChooser<ShowingCard>)sender).getParentPanel().getId();
                        ShowingCard sc = null;
                        if (senderId.equals("userCards")){
                            from = cardsPanel;
                            sc= (ShowingCard) from.listChooser.getDefaultModelObject();
                            to = dontWantRecyclePanel;
                            fromList = tradeList;
                            if (!fromList.contains(sc)) return target;
                            toList = dontWantRecycleList;
                            sc.status = "trading";
                            usr.addToDontWantRecycle(sc);
                        } else if(senderId.equals("recycleShortlistPanel")){
                            from = recycleShortlistPanel;
                            sc= (ShowingCard) from.listChooser.getDefaultModelObject();
                            to = dontWantRecyclePanel;
                            fromList = recycleShortlistList;
                            if (!fromList.contains(sc)) return target;
                            toList = dontWantRecycleList;
                        } else return target;
                        if(sc==null) return target;
                        fromList.remove(sc);
                        from.setChoices(fromList);
                        toList.add(sc);
                        to.setChoices(toList);
                        usr.UPDATE();
                        sc.UPDATE();
                        target.add(from);
                        target.add(to);
                    }

                }
                return target;
            }
        };
        cardsPanel.listChooser.addEventListener(keyPressListener);
    }
}
