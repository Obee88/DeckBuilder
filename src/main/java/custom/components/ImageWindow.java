package custom.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import suport.Printer;

import custom.classes.ShowingCard;
import custom.components.panels.CardView;

@SuppressWarnings("serial")
public class ImageWindow extends WebComponent implements IEventListener {
	
	private final String BACK_URL = "http://www.wizards.com/magic/images/mtgcom/fcpics/making/mr224_back.jpg";
	private ShowingCard card = null;
	private CardView parent;
	
    public ImageWindow(String id, IModel<String> model, CardView parent) {
        super(id, model);
        setOutputMarkupId(true);
        setUrl(BACK_URL);
        this.parent = parent;
    }

	protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "img");
        tag.put("src", getDefaultModelObjectAsString());
    }
    
	public void setUrl(String url) {
		setDefaultModel(new Model<String>(url));
	}

	@Override
	@SuppressWarnings("unchecked")
	public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
			String eventType) {
		if(eventType.equals("onChange")){
			ListChooser<ShowingCard> cc = (ListChooser<ShowingCard>)sender;
			ShowingCard sc = cc.getSelectedChoice();
			if(sc.cardId==null) return target;
			setCard(sc);
			parent.setRarity(sc.cardInfo.rarity);
			target.add(parent);
			target.add(this);
		}
		return target;
	}
	
	public void setCard(ShowingCard sc){
		card=sc;
		if(card!=null)
			setUrl(sc.cardInfo.downloadLink);
		else
			setUrl(BACK_URL);
	}
	
	public void flip(){
		if(card==null) return;
		if(card.cardInfo.isTwoSided)
			setUrl(Printer.flipLink(getDefaultModelObjectAsString()));
		else
			if(getDefaultModelObjectAsString().equals(BACK_URL))
				setUrl(card.cardInfo.downloadLink);
			else
				setUrl(BACK_URL);
		
	}
	
	public ShowingCard getCard() {
		return card;
	}
    
}