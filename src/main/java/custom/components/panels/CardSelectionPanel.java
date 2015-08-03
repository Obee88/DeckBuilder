package custom.components.panels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import custom.classes.ShowingCard;
import custom.components.IEventListener;
import custom.components.ListChooser;

@SuppressWarnings("serial")
public class CardSelectionPanel extends Panel implements IEventListener{

	public ListChooser<ShowingCard> listChooser;
	public DropDownChoice<String> colorPicker;
	public CheckBox printIndicator;
	public Label numLbl;
	public String title;
	public boolean titleVisible  =false;
	
	Integer filterSize;
	ArrayList<ShowingCard> filteredList,cardsList;
	List<String> colorChoices;
	private Form<Object> titleChangeForm;
	private Label titleLbl;
	private TextField<String> titleTbx;
	
	public CardSelectionPanel(String id, ArrayList<ShowingCard> cardList) {
		super(id);
		setOutputMarkupId(true);
		this.cardsList = new ArrayList<ShowingCard>();
		this.cardsList.addAll(cardList);
		initLists();
		initComponents();
		initBehaviours();
	}

	private void initLists() {
		colorChoices = new ArrayList<String>();
		colorChoices.add("All colors"); colorChoices.add("Black");
		colorChoices.add("Blue"); colorChoices.add("Green");
		colorChoices.add("Red"); colorChoices.add("White");
		colorChoices.add("Colorless");
		filteredList  = new ArrayList<ShowingCard>();
		filteredList.addAll(cardsList);
		filterSize = filteredList.size();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initComponents() {
		colorPicker = new DropDownChoice<String>("colorPicker", 
				new Model(colorChoices.get(0)),
				new Model((Serializable) colorChoices));
		listChooser = new ListChooser<ShowingCard>("listChooser", filteredList, null);
		listChooser.setParentPanel(this);
		printIndicator = new CheckBox("printIndicator",new PropertyModel<Boolean>(listChooser, "printerIndicator"));
		numLbl = new Label("numLbl", new PropertyModel<Integer>(this, "filterSize"));
		numLbl.setOutputMarkupId(true);
		add(numLbl);
		add(colorPicker);
		add(listChooser);
		add(printIndicator);
		titleChangeForm = new Form<Object>("titleChangeForm"){
			@Override
			public boolean isVisible() {
				return super.isVisible() && titleVisible;
			}
			@Override
			protected void onSubmit() {
				onTitleChanged();
			}
		};
		titleLbl = new Label("titleLbl", new PropertyModel<String>(this, "title"));
		titleLbl.setOutputMarkupId(true);
		titleTbx = new TextField<String>("titleTbx",Model.of(title));
		titleTbx.setOutputMarkupId(true);
		titleTbx.add(new AttributeModifier("style", "display:none;"));
		titleChangeForm.add(titleLbl);
		titleChangeForm.add(titleTbx);
		add(titleChangeForm);
	}

	private void initBehaviours() {
		AjaxFormSubmitBehavior formSubmitBeh = new AjaxFormSubmitBehavior("onchange"){
			protected void onSubmit(AjaxRequestTarget target) {
				titleTbx.add(new AttributeModifier("style", "display:none;"));
				titleLbl.add(new AttributeModifier("style", "display:inline;"));
				title = titleTbx.getDefaultModelObjectAsString();
				target.add(titleLbl);
				target.add(titleTbx);
				
			}
		};
		titleTbx.add(formSubmitBeh);
		AjaxEventBehavior titleLblDblClk =new AjaxEventBehavior("ondblclick"){
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				titleLbl.add(new AttributeModifier("style", "display:none;"));
				titleTbx.add(new AttributeModifier("style", "display:inline;"));
				target.add(titleLbl);
				target.add(titleTbx);
			}
       	};
       	titleLbl.add(titleLblDblClk);
		OnChangeAjaxBehavior onChangeColor = new OnChangeAjaxBehavior(){

			@Override
		    protected void onUpdate(AjaxRequestTarget target)
		    {
				refreshFilterList();
		        target.add(listChooser);
		        target.add(numLbl);
		    }
		};
		colorPicker.add(onChangeColor);
		OnChangeAjaxBehavior printChecked = new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = getPrintIndicator();
				listChooser.setPrinterIndicator(b);
				target.add(listChooser);
			}
		};
		printIndicator.add(printChecked);
	}
	
	protected void refreshFilterList() {
		String color =colorPicker.getDefaultModelObjectAsString();
        filteredList.clear();
        for(ShowingCard sc: cardsList){
        	if(isColorOK(color, sc))
        		filteredList.add(sc);
        }
        listChooser.setAvailableChoices(filteredList);
        filterSize  = filteredList.size();
	}

	private boolean isColorOK(String color, ShowingCard sc) {
		color = color.toLowerCase();
		if(color.equals("black")) return sc.isBlack();
		if(color.equals("blue")) return sc.isBlue();
		if(color.equals("green")) return sc.isGreen();
		if(color.equals("red")) return sc.isRed();
		if(color.equals("white")) return sc.isWhite();
		if(color.equals("colorless")) 
			return (!sc.isBlack() && !sc.isBlue() && !sc.isGreen() && !sc.isRed() && !sc.isWhite());
		return true;
	}

	public Boolean getPrintIndicator(){
		return (Boolean) printIndicator.getDefaultModelObject();
	}

	@Override
	public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
			String eventType) {
		if(sender.equals(listChooser)&& eventType.equals("DblClk"))
			filterSize = filteredList.size();
			target.add(numLbl);
		return null;
	}
	
	public void addChoice(ShowingCard sc) {
		cardsList.add(sc);
		refreshFilterList();
	}
	
	public void removeChoice(ShowingCard sc) {
		cardsList.remove(sc);
		refreshFilterList();
	}
	
	public void setFilterVisible(Boolean visible){
		colorPicker.setVisible(visible);
	}

	public List<ShowingCard> getChoices() {
		return cardsList;
	}
	
	public void setPrintCheckBoxVisible(Boolean visible){
		printIndicator.setVisible(visible);
	}

	public void setChoices(List<ShowingCard> list) {
		cardsList.clear();
		cardsList.addAll(list);
		refreshFilterList();	
	}
	 
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setTitleVisible(boolean titleVisible) {
		this.titleVisible = titleVisible;
	}
	
	protected void onTitleChanged() {
		
	}

    public void sort(Comparator<ShowingCard> comparator){
        listChooser.sort( comparator);
    }

    public void unselect() {
        listChooser.selectedChoice = null;
    }

	public void showColorByStatus(boolean b){
		listChooser.showColorByStatus(b);
	}

	public void markInterests(boolean b){
		this.listChooser.markInterests(b);
	}
}
