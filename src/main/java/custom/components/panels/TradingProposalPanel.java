package custom.components.panels;

import java.util.List;

import obee.pages.TradePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import custom.classes.ShowingCard;
import custom.classes.TradingProposal;
import custom.components.IEventListener;
import custom.components.ImageWindow;

@SuppressWarnings("serial")
public abstract class TradingProposalPanel extends Panel implements IEventListener {

	String from="";
	List<ShowingCard> fromCards, toCards;
	String expire = "";
	TradingProposal proposal=null;
	private Label fromLabel,expireLbl,jadLbl;
	private CardView imgFrom1,imgFrom2,imgFrom3,imgFrom4,imgFrom5,imgFrom6;
	private CardView imgTo1,imgTo2,imgTo3,imgTo4,imgTo5,imgTo6;
	private CardView[] fromWindows, toWindows;
	private AjaxLink<Object> aceptButton;
	private AjaxLink<Object> dismissButton;
	private CardView zoom;
    private Form negotiateForm;
    private int jadOffer;

    public TradingProposalPanel(String id) {
		super(id);
		initComponents();
		setOutputMarkupId(true);
	}

	private void initComponents() {
		zoom = new CardView("zoom"){
			@Override
			public AjaxRequestTarget onEvent(AjaxRequestTarget target,
					Object sender, String eventType) {
				ImageWindow senderView = (ImageWindow) sender;
				ShowingCard sc = senderView.getCard();
				image.setCard(sc);
				target.add(this);
				return target;
			}
		};
		zoom.setOutputMarkupId(true);
		fromLabel = new Label("fromLabel", new PropertyModel<String>(this, "from"));
		imgFrom1 = new CardView("imgFrom1");
		imgFrom2 = new CardView("imgFrom2");
		imgFrom3 = new CardView("imgFrom3");
		imgFrom4 = new CardView("imgFrom4");
		imgFrom5 = new CardView("imgFrom5");
		imgFrom6 = new CardView("imgFrom6");
		imgTo1 = new CardView("imgTo1");
		imgTo2 = new CardView("imgTo2");
		imgTo3 = new CardView("imgTo3");
		imgTo4 = new CardView("imgTo4");
		imgTo5 = new CardView("imgTo5");
		imgTo6 = new CardView("imgTo6");
		fromWindows = new CardView[]{imgFrom1,imgFrom2,imgFrom3,imgFrom4,imgFrom5,imgFrom6};
		toWindows = new CardView[]{imgTo1,imgTo2,imgTo3,imgTo4,imgTo5,imgTo6};
		expireLbl = new Label("expireLbl",new PropertyModel<String>(this, "expire"));
        jadLbl = new Label("jadLbl",new PropertyModel<String>(this, "jadOffer"));
		aceptButton = new AjaxLink<Object>("aceptButton") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				onAcept(target);
			}
		};
		dismissButton = new AjaxLink<Object>("dismissButton") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				onDismiss(target);
			}
		};
        negotiateForm = new Form("negotiateForm"){
            @Override
            protected void onSubmit() {
                PageParameters params = new PageParameters();
                params.add("from",getIdList(fromCards));
                params.add("to",getIdList(toCards));
                setResponsePage(TradePage.class, params);
            }

            private String getIdList(List<ShowingCard> cards) {
                StringBuilder sb=new StringBuilder();
                int i=0;
                boolean f=true;
                for(ShowingCard sc : cards){
                    if (f) f=false;
                    else sb.append(",");
                    sb.append(sc.cardId);
                }
                return sb.toString();
            }
        } ;
        add(negotiateForm);
		add(fromLabel); add(expireLbl); add(jadLbl);
		add(aceptButton); add(dismissButton);
		for(int i = 0 ; i<6;i++){
			add(fromWindows[i]);
			add(toWindows[i]);
			fromWindows[i].addEventListener(zoom);
			toWindows[i].addEventListener(zoom);
			fromWindows[i].setFlipEnabled(false);
			toWindows[i].setFlipEnabled(false);
		}
		add(zoom);
	}

	@Override
	@SuppressWarnings("unchecked")
	public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
			String eventType) {
		TradingProposal tp = (TradingProposal) ((ListChoice<TradingProposal>)sender).getDefaultModelObject();
		setProposal(tp);
		target.add(this);
		return target;
	}

	private void setCardsUrls() {
		Integer fromSize = fromCards.size(), toSize = toCards.size();
		for(int i = 0 ; i<6; i++){
			if(i<fromSize)
				fromWindows[i].image.setCard(fromCards.get(i));
			else
				fromWindows[i].image.setCard(null);
			if(i<toSize)
				toWindows[i].image.setCard(toCards.get(i));
			else
				toWindows[i].image.setCard(null);
			
		}
		
	}
	
	@Override
	public boolean isVisible() {
		return super.isVisible()&&proposal!=null;
	}
	
	public abstract void onAcept(AjaxRequestTarget target);
	public abstract void onDismiss(AjaxRequestTarget target);

	public void setProposal(TradingProposal tp) {
		proposal = tp;
		from = tp.getFrom();
		fromCards = tp.getFromList();
		toCards = tp.getToList();
        jadOffer = tp.getJadOffer();
		DateTime dt = new DateTime(tp.getExpireDate());
		expire = dt.toString( DateTimeFormat.forPattern("dd. MM. YYYY : HH:mm"));
		setCardsUrls();
	}

	public void setAcceptButtonVisible(boolean b) {
		aceptButton.setVisible(b);
        negotiateForm.setVisible(b);
	}
	
	public TradingProposal getProposal() {
		return proposal;
	}

}
