package custom.components.panels;

import java.util.ArrayList;
import java.util.List;

import custom.classes.User;
import database.MongoHandler;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import custom.classes.ShowingCard;
import custom.components.IEventListener;
import custom.components.ListChooser;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@SuppressWarnings("serial")
public class InfoPanel extends Panel implements IEventListener{

    private final boolean isAdmin;
    Label nameLabel, rarityLabel, ownerLabel, printedLabel, twoSidedLabel, dateLabel, manaCostlabel;
	String name="", rarity="", owner="",printed="", isTwoSided="", idStr="", purpose="", date = "", manaCost="";
	ShowingCard card;
	List<Component> components = new ArrayList<Component>();
	private Label idLabel;
    private Label IDLabel;
    private Label purposeLabel;
    private Label PURPOSELabel;

    public InfoPanel(String id, boolean isAdmin) {
		super(id);
        this.isAdmin=isAdmin;
		intiComponents();
	}

	private void intiComponents() {

        IDLabel = new Label("IDLabel", "Card Id:");
		idLabel = new Label("idLabel", new PropertyModel<ShowingCard>(this, "idStr"));
        if(!isAdmin){
            hide(IDLabel);hide(idLabel);
        }
		nameLabel = new Label("nameLabel", new PropertyModel<ShowingCard>(this, "name"));
		rarityLabel = new Label("rarityLabel", new PropertyModel<ShowingCard>(this, "rarity"));
		ownerLabel = new Label("ownerLabel", new PropertyModel<ShowingCard>(this, "owner"));
		printedLabel = new Label("printedLabel", new PropertyModel<ShowingCard>(this, "printed"));
		twoSidedLabel = new Label("twoSidedLabel", new PropertyModel<ShowingCard>(this, "isTwoSided"));
        dateLabel = new Label("dateLabel", new PropertyModel<Object>(this,"date"));
        purposeLabel=new Label("purposeLabel", new PropertyModel<ShowingCard>(this, "purpose"));
        PURPOSELabel = new Label("PURPOSELabel", "In:");
//        manaCostlabel = new Label("manaCostLabel", new PropertyModel<ShowingCard>(this,"manaCost"));
		idLabel.setOutputMarkupId(true);
		nameLabel.setOutputMarkupId(true);
		rarityLabel.setOutputMarkupId(true);
		ownerLabel.setOutputMarkupId(true);
		printedLabel.setOutputMarkupId(true);
		twoSidedLabel.setOutputMarkupId(true);
        purposeLabel.setOutputMarkupId(true);
//        manaCostlabel.setOutputMarkupId(true);
        dateLabel.setOutputMarkupId(true);
        add(IDLabel); add(PURPOSELabel);
		add(idLabel); components.add(idLabel);
		add(nameLabel); components.add(nameLabel);
		add(rarityLabel); components.add(rarityLabel);
		add(ownerLabel); components.add(ownerLabel);
		add(printedLabel); components.add(printedLabel);
		add(twoSidedLabel); components.add(twoSidedLabel);
        add(purposeLabel);components.add(purposeLabel);
        add(dateLabel); components.add(dateLabel);
//        add(manaCostlabel); components.add(manaCostlabel);

	}

	@Override
	@SuppressWarnings("unchecked")
	public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
			String eventType) {		
		card = ((ListChooser<ShowingCard>)sender).selectedChoice;
		idStr = card==null? "":card.cardId.toString();
		name = card==null? "":card.name;
		rarity = card==null? "":card.cardInfo.rarity;
		owner = card==null? "":card.owner;
		printed = card==null? "":card.printed;
		isTwoSided = card==null? "":card.cardInfo.isTwoSided?"yes":"no";
        DateTime dt = new DateTime(card.getCreationDate());
        date =  card==null? "":dt.toString(DateTimeFormat.forPattern("dd.MM.yyyy. 'at' HH:mm:ss"));
        purpose = MongoHandler.getInstance().getUser(owner).getPurpose(card);
//        manaCost = card==null? "": card.cardInfo.manaCost;
		for(Component c : this.components)
			target.add(c);
		return target;
	}

    public void setPurposeLabelVisible(boolean b){
        if(b){
            show(purposeLabel);
            show(PURPOSELabel);
        } else{
            hide(purposeLabel);
            hide(PURPOSELabel);
        }
    }

	@Override
	public boolean isEnabled() {
		return super.isEnabled()&&card!=null;
	}

    private void hide(Component c){
        c.add(new AttributeModifier("style", "display:none;"));
    }
    private void show(Component c){
        c.add(new AttributeModifier("style", "display:inline;"));
    }

    public ShowingCard getCard() {
        return card;
    }
}
