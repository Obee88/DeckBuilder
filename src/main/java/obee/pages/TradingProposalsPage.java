package obee.pages;

import java.util.ArrayList;
import java.util.List;

import obee.pages.master.MasterPage;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import custom.classes.Administration;
import custom.classes.ShowingCard;
import custom.classes.TradingProposal;
import custom.classes.User;
import custom.classes.UserMessage;
import custom.components.panels.TradingProposalPanel;
import suport.MailSender;

@AuthorizeInstantiation("USER")
@SuppressWarnings({ "unchecked", "rawtypes","serial","unused" })
public class TradingProposalsPage extends MasterPage {
	
	private Form form;
	private List<TradingProposal> proposalsChoices, myProposalsChoices;
	private ListChoice<TradingProposal> proposalsList, myProposalsList;
	private TradingProposalPanel proposalsPanel;
	private String status;
	private Label statusLbl;
	private Form<Object> msgForm;
	private TextArea<String> textField;
	private TextField<String> subjectField;
	private DropDownChoice<String> msgUserSelector;
	private ArrayList<String> userStringList;
	private String msgSelectedUser;
	
	public TradingProposalsPage(PageParameters params) {
		super(params, "Proposals");
		initLists();
		initForm();
		initComponents();
		initBehaviours();
	}

	private void initLists() {
		proposalsChoices = Administration.getTradingProposalsListTo(getUserName());
		myProposalsChoices = Administration.getTradingProposalsListFrom(getUserName());
		if(proposalsChoices.isEmpty() && myProposalsChoices.isEmpty())
			status = "You don't have any trading proposals at the moment.";
		
		userStringList = new ArrayList<String>();
		userStringList.add("all");
		for(User u : mongo.getAllUsers())
			userStringList.add(u.getUserName());
		msgSelectedUser=userStringList.get(0);
		
	}

	private void initComponents() {
		msgUserSelector = new DropDownChoice<String>("msgUserSelector", 
				new Model(msgSelectedUser),
				new Model(userStringList));
		textField = new TextArea<String>("textTbx", Model.of(""));
		subjectField = new TextField<String>("subjectTbx", new Model<String>());

		msgForm.add(textField);
		msgForm.add(subjectField);
		msgForm.add(msgUserSelector);
		
		statusLbl = new Label("statusLbl", new PropertyModel<String>(this, "status"));
		proposalsList = new ListChoice<TradingProposal>("proposalsList",
				new Model(proposalsChoices.isEmpty()?null:proposalsChoices.get(0)),
				new PropertyModel(this, "proposalsChoices")){
			@Override
			public boolean isVisible() {
				return super.isVisible()&&!proposalsChoices.isEmpty();
			}
		};
		myProposalsList = new ListChoice<TradingProposal>("myProposalsList",
				new Model(myProposalsChoices.isEmpty()?null:myProposalsChoices.get(0)),
				new PropertyModel(this, "myProposalsChoices")){
			@Override
			public boolean isVisible() {
				return super.isVisible()&&!myProposalsChoices.isEmpty();
			}
		};
		proposalsPanel = new TradingProposalPanel("proposalsPanel") {
			
			@Override
			public void onDismiss(AjaxRequestTarget target) {
				TradingProposal tp = proposalsPanel.getProposal();
				if(tp.isValid()){
					User from = mongo.getUser(tp.getFrom());
					from.addMessage(new UserMessage(from.getNextMessageId(), "Rejected!", tp.getTo()+" rejected your trade proposal."));
					from.UPDATE();
					Administration.removeFromTradingProposalList(tp);
					for(ShowingCard sc : tp.getFromList())
						mongo.setCardInProposal(sc.cardId, "false");
				}
				setResponsePage(TradingProposalsPage.class);
			}
			
			@Override
			public void onAcept(AjaxRequestTarget target) {
				TradingProposal tp = proposalsPanel.getProposal();
				if(tp.isValid()){
					User from = mongo.getUser(tp.getFrom());
					User to = mongo.getUser(tp.getTo());
					for(ShowingCard sc: tp.getFromList()){
						mongo.setCardOwner(sc.cardId,to.getUserName());
						mongo.removeFromTradingList(sc.cardId, from.getUserName());
						mongo.setCardStatus(sc.cardId, "boosters");
						mongo.addToBoosterList(sc.cardId, to.getUserName());
						mongo.setCardInProposal(sc.cardId,"false");
					}
					for(ShowingCard sc: tp.getToList()){
						mongo.setCardOwner(sc.cardId,from.getUserName());
						mongo.removeFromTradingList(sc.cardId, to.getUserName());
						mongo.setCardStatus(sc.cardId, "boosters");
						mongo.addToBoosterList(sc.cardId, from.getUserName());
						mongo.setCardInProposal(sc.cardId,"false");
					}
					from=mongo.getUser(tp.getFrom());
					from.addMessage(new UserMessage(from.getNextMessageId(), "Trade sucessful!", tp.getTo()+" accepted your trade proposal."));
                    if(from.wantsProposalMail())
                        MailSender.sendProposalNotification(tp, MailSender.ProposalNotificationType.Accept);
					from.UPDATE();
					Administration.removeFromTradingProposalList(tp);
				}
				setResponsePage(TradingProposalsPage.class);
			}
		};
		if(!myProposalsChoices.isEmpty()){
			proposalsPanel.setProposal(myProposalsChoices.get(0));
			proposalsPanel.setAcceptButtonVisible(false);
		}
		if(!proposalsChoices.isEmpty()){
			proposalsPanel.setProposal(proposalsChoices.get(0));
			proposalsPanel.setAcceptButtonVisible(true);
		}
		form.add(myProposalsList);
		form.add(proposalsList);
		form.add(proposalsPanel);
		form.add(statusLbl);
	}

	private void initForm() {
		form = new Form("form") {
			@Override
			protected void onSubmit() {
			}
		};
		add(form);
		msgForm = new Form<Object>("msgForm"){
			@Override
			protected void onSubmit() {
				String subject = "[from:"+getUserName()+"] "+subjectField.getModelObject().toString();
				String text = textField.getModelObject().toString();
				String uName = msgUserSelector.getModelObject();
				if(uName.equals("all")){
					for(User u: mongo.getAllUsers()){
						UserMessage msg = new UserMessage(u.getNextMessageId(),subject,text);
						u.addMessage(msg);
						u.UPDATE();
						setResponsePage(TradingProposalsPage.class);
					}
				} else{
					User u = mongo.getUser(uName);
					UserMessage msg = new UserMessage(u.getNextMessageId(),subject,text);
					u.addMessage(msg);
					u.UPDATE();
					setResponsePage(TradingProposalsPage.class);
				}
				
			}
		};
		add(msgForm);
	}

	private void initBehaviours() {
		OnChangeAjaxBehavior onChange = new OnChangeAjaxBehavior() {
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				TradingProposal tp = (TradingProposal) proposalsList.getDefaultModelObject();
				target = proposalsPanel.onEvent(target, proposalsList,"onClick");
				proposalsPanel.setAcceptButtonVisible(true);
			}
		};
		proposalsList.add(onChange);
		AjaxEventBehavior onClick = new AjaxEventBehavior("onclick") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				TradingProposal tp = (TradingProposal) proposalsList.getDefaultModelObject();
				target = proposalsPanel.onEvent(target, proposalsList,"onClick");
				proposalsPanel.setAcceptButtonVisible(true);
				
			}
		};
		proposalsList.add(onClick);
		OnChangeAjaxBehavior onMyChange = new OnChangeAjaxBehavior() {
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				TradingProposal tp = (TradingProposal) myProposalsList.getDefaultModelObject();
				target = proposalsPanel.onEvent(target,myProposalsList,"onClick");
				proposalsPanel.setAcceptButtonVisible(false);
			}
		};
		myProposalsList.add(onMyChange);
		AjaxEventBehavior onMyClick = new AjaxEventBehavior("onclick") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				TradingProposal tp = (TradingProposal) myProposalsList.getDefaultModelObject();
				target = proposalsPanel.onEvent(target,myProposalsList,"onClick");
				proposalsPanel.setAcceptButtonVisible(false);
			}
		};
		myProposalsList.add(onMyClick);
	}

	
}
