package obee.pages;

import custom.classes.*;
import custom.components.IEventListener;
import custom.components.ListChooser;
import custom.components.panels.CardSelectionPanel;
import custom.components.panels.CardView;
import obee.pages.master.MasterPage;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import suport.MailSender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@AuthorizeInstantiation("USER")
@SuppressWarnings({ "unchecked", "rawtypes","serial" })
public class TradePage extends MasterPage {

	private CardSelectionPanel homeTrade, awayTrade, homeOffer, awayOffer;
	private Form<Object> form;
	private ArrayList<String> usersStringList;
	private List<ShowingCard> awayTradeList, homeTradeList, awayOfferList, homeOfferList;
	private DropDownChoice<String> userChooser;
	private AjaxLink<Object> tradeBtn;
	private CardView cardView;
    private List<ShowingCard> trejdL;
    User user = mongo.getUser(getUserName());
    private NumberTextField<Integer> jadOfferTbx;

    public TradePage(PageParameters params) {
		super(params, "Trade");
		initForm(); sw.checkpoint("forms init done");
		initLists(params); sw.checkpoint("lists init done");
		initComponents(); sw.checkpoint("components init done");
		initBehaviours(); sw.checkpoint("behaviors init done");
        fillBoxes(); sw.checkpoint("boxes fill done");
        sw.checkpoint("page loaded");
	}

    private void fillBoxes() {
        for(ShowingCard sc: trejdL){
            handleUserSelection(null,sc);
            return;
        }
    }

    private ArrayList<Integer> getList(String strL) {
        ArrayList<Integer>ret = new ArrayList<Integer>();
        String[] strs = strL.split(",");
        for (int i = 0; i < strs.length; i++) {
            if(strs[i].equals("")) continue;
            int id = Integer.parseInt(strs[i]);
            ret.add(id);
        }
        return ret;
    }

    private void initComponents() {
		homeTrade = new CardSelectionPanel("homeTrade", (ArrayList<ShowingCard>) homeTradeList);
		homeTrade.listChooser.setMaxRows(15);
		awayTrade = new CardSelectionPanel("awayTrade", (ArrayList<ShowingCard>) awayTradeList);
		awayTrade.listChooser.setMaxRows(15);
		awayTrade.setPrintCheckBoxVisible(false);
		homeOffer = new CardSelectionPanel("homeOffer", (ArrayList<ShowingCard>) homeOfferList);
		homeOffer.listChooser.setMaxRows(6);
		homeOffer.setPrintCheckBoxVisible(false);
		homeOffer.setFilterVisible(false);
		awayOffer = new CardSelectionPanel("awayOffer", (ArrayList<ShowingCard>) awayOfferList);
		awayOffer.listChooser.setMaxRows(6);
		awayOffer.setPrintCheckBoxVisible(false);
		awayOffer.setFilterVisible(false);
		cardView = new CardView("cardView", currentUser.isInHackerMode());
		cardView.setRarityLblVisible(true);
		userChooser = new DropDownChoice<String>("userChooser",
				new Model(usersStringList.get(0)),
				new Model(usersStringList));
		userChooser.setOutputMarkupId(true);
//		tradeBtn = new AjaxLink<Object>("tradeBtn") {
//			@Override
//			public void onClick(AjaxRequestTarget target) {
//
//
//			}
//		};
        jadOfferTbx = new NumberTextField<Integer>("jadOfferTbx",new Model(0));
        jadOfferTbx.add(new AbstractDefaultAjaxBehavior(){

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                String js = "{var k = event.which;if (k < 48 || k > 58 ) event.preventDefault();}";
                tag.put("onkeypress", js);
            }

            @Override
            protected void respond(AjaxRequestTarget target) {
                FormComponent c = (FormComponent) this.getComponent();
                c.processInput();
                if (c.hasErrorMessage()) {
                    Serializable msg = "moja poruka";
                    // do something with the message
                }
            }
        });
//        tradeBtn.setOutputMarkupId(true);
        jadOfferTbx.setMinimum(0);
        form.add(jadOfferTbx);
		form.add(cardView);
		form.add(homeTrade); form.add(homeOffer);
		form.add(awayTrade); form.add(awayOffer);
		form.add(userChooser);
		//form.add(tradeBtn);
	}

	private void initLists(PageParameters params) {

        List<Integer> fromL = getList(params.get("from").toString(""));
        List<Integer> toL = getList(params.get("to").toString(""));
		homeTradeList = notInProposal(user.getTradingShowingCards());
        sw.checkpoint("home trade list filled");
		awayTradeList = new ArrayList<ShowingCard>();
		usersStringList = new ArrayList<String>();
		usersStringList.add("Select user");
		homeOfferList = new ArrayList<ShowingCard>();
		awayOfferList = new ArrayList<ShowingCard>();
        sw.checkpoint("lists initiated");
		List<User> usrs = mongo.getAllUsers();
        sw.checkpoint("all users loaded");
		for(User usr : usrs){
			if(!usr.getUserName().equals(getUserName())){
				usersStringList.add(usr.getUserName());
// 				adding all cards to tradelist --- SLOW
//				awayTradeList.addAll(usr.getTradingShowingCards());
			}
            sw.checkpoint("user "+usr.getUserName()+" done");
		}
        trejdL= new ArrayList();
        for(ShowingCard sc : homeTradeList)
            if (toL.contains(sc.cardId))
                trejdL.add(sc);
        homeTradeList.removeAll(trejdL);
        homeOfferList.addAll(trejdL);
        trejdL.clear();
        for(ShowingCard sc : awayTradeList)
            if (fromL.contains(sc.cardId)){
                trejdL.add(sc);
            }
        awayTradeList.removeAll(trejdL);
        awayOfferList.addAll(trejdL);
	}

	private List<ShowingCard> notInProposal(
			List<ShowingCard> list) {
		List<ShowingCard> ret = new ArrayList<ShowingCard>();
		for(ShowingCard sc : list){
			if(!sc.isInProposal())
				ret.add(sc);
		}
		return ret;
	}

	private void initForm() {
		form = new Form<Object>("form"){
			@Override
			protected void onSubmit() {
                Integer jadOffer = jadOfferTbx.getDefaultModelObjectAsString().equals("")?0: Integer.parseInt(jadOfferTbx.getDefaultModelObjectAsString());
                if(homeOfferList.isEmpty() && awayOfferList.isEmpty() && jadOffer==0)
                    return ;
                if(awayOfferList.isEmpty())
                    return ;
                String toUserName =userChooser.getDefaultModelObjectAsString();
                if (jadOffer>user.getJadBalance()) {
                    info("Not enough Jad!");
                    return;
                }
                TradingProposal tp = new TradingProposal(getUserName(),
                        toUserName,
                        homeOfferList,
                        awayOfferList,
                        jadOffer);
                User to = mongo.getUser(toUserName);
                UserMessage msg = new UserMessage(
                        to.getNextMessageId(),
                        getUserName()+" wish to trade with you!",
                        "Hey! It's "+getUserName()+". I just sent you a trade proposal. Check it out.");
                to.addMessage(msg);
                to.UPDATE();
                Administration.addToTradingProposalList(tp);
                if(to.wantsProposalMail()){
                    MailSender.sendProposalNotification(tp, MailSender.ProposalNotificationType.Offer);
                }
                for(ShowingCard sc: homeOfferList)
                    mongo.setCardInProposal(sc.cardId, "true");
                setResponsePage(TradePage.class);
			}
		};
		add(form);
	}

	private void initBehaviours() {
		homeOffer.listChooser.addEventListener(cardView.image);
		homeTrade.listChooser.addEventListener(cardView.image);
		awayOffer.listChooser.addEventListener(cardView.image);
		awayTrade.listChooser.addEventListener(cardView.image);
		OnChangeAjaxBehavior printerChecked = new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) homeTrade.printIndicator.getDefaultModelObject();
				homeOffer.listChooser.setPrinterIndicator(b);
				target.add(homeOffer);
				awayOffer.listChooser.setPrinterIndicator(b);
				target.add(awayOffer);
				awayTrade.listChooser.setPrinterIndicator(b);
				target.add(awayTrade);
			}
		};
		homeTrade.printIndicator.add(printerChecked);
		OnChangeAjaxBehavior onUserChange = new OnChangeAjaxBehavior(){

			@Override
		    protected void onUpdate(AjaxRequestTarget target)
		    {
				String userSelection = userChooser.getDefaultModelObjectAsString();
				awayTradeList.clear();
				if(userSelection.equals("All users")){
					awayTradeList.clear();
				} else {
					User u = mongo.getUser(userSelection);
					awayTradeList.addAll(u.getTradingShowingCards());
				}
				awayTrade.setChoices(awayTradeList);
				awayOfferList.clear();
				awayOffer.setChoices(awayOfferList);
				target.add(awayTrade);
				target.add(awayOffer);
		    }
		};
		userChooser.add(onUserChange);
		IEventListener homeTrejder = new IEventListener() {
			@Override
			public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
					String eventType) {
				if(eventType.equals("onDblClk")){
					CardSelectionPanel from, to;
					List<ShowingCard> fromList, toList;
					String senderId = ((ListChooser<ShowingCard>)sender).getParentPanel().getId();
					if(senderId.equals("homeTrade")){
						from = homeTrade;
						to = homeOffer;
						fromList = homeTradeList;
						toList = homeOfferList;
						if(to.getChoices().size()>=6) {
							info("Can't trade more than 6 cards at once.");
							target.add(feedback);
							return target;
						}
					} else if(senderId.equals("homeOffer")){
						to = homeTrade;
						from = homeOffer;
						toList = homeTradeList;
						fromList = homeOfferList;
					} else return target;
					ShowingCard sc = (ShowingCard) from.listChooser.getDefaultModelObject();
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
		homeTrade.listChooser.addEventListener(homeTrejder);
		homeOffer.listChooser.addEventListener(homeTrejder);
		IEventListener awayTrejder = new IEventListener() {
			@Override
			public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
					String eventType) {
				if(eventType.equals("onDblClk")){
					CardSelectionPanel from, to;
					List<ShowingCard> fromList, toList;
					String senderId = ((ListChooser<ShowingCard>)sender).getParentPanel().getId();
					if(senderId.equals("awayTrade")){
						from = awayTrade;
						to = awayOffer;
						fromList = awayTradeList;
						toList = awayOfferList;
						if(to.getChoices().size()>=6) {
							info("Can't trade more than 6 cards at once.");
							target.add(feedback);
							return target;
						}
					} else if(senderId.equals("awayOffer")){
						to = awayTrade;
						from = awayOffer;
						toList = awayTradeList;
						fromList = awayOfferList;
					} else return target;
					ShowingCard sc = (ShowingCard) from.listChooser.getDefaultModelObject();
					fromList.remove(sc);
					from.setChoices(fromList);
					toList.add(sc);
					to.setChoices(toList);
					if(senderId.equals("awayTrade")){
						target = handleUserSelection(target,sc);
					}
					target.add(from);
					target.add(to);
					
				}
				return target;
			}

		};
		awayTrade.listChooser.addEventListener(awayTrejder);
		awayOffer.listChooser.addEventListener(awayTrejder);

		AjaxEventBehavior onAwayOfferClick = new AjaxEventBehavior("onclick") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target = cardView.onEvent(target, awayOffer.listChooser,"onChange");
			}
		};
		awayOffer.add(onAwayOfferClick);
		AjaxEventBehavior onHomeOfferClick = new AjaxEventBehavior("onclick") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target = cardView.onEvent(target, homeOffer.listChooser,"onChange");
			}
		};
		homeOffer.add(onHomeOfferClick);
	}
    private AjaxRequestTarget handleUserSelection(AjaxRequestTarget target, ShowingCard sc) {
        userChooser.setDefaultModelObject(sc.owner);
        if(target!=null)
            target.add(userChooser);
        User u = mongo.getUser(sc.owner);
        awayTradeList.clear();
        awayTradeList.addAll(u.getTradingShowingCards());
        List<ShowingCard> trRemLst = new ArrayList<ShowingCard>();
        List<ShowingCard> ofRemLst = new ArrayList<ShowingCard>();
        for(ShowingCard osc : awayOfferList){
            if(osc.owner.equals(sc.owner))
                for(ShowingCard rem : awayTradeList){
                    if(rem.equals(osc))
                        trRemLst.add(rem);
                }
            else
                ofRemLst.add(osc);
        }
        awayTradeList.removeAll(trRemLst);
        awayOfferList.removeAll(ofRemLst);
        awayTrade.setChoices(awayTradeList);
        awayOffer.setChoices(awayOfferList);
        return target;
    }

}
