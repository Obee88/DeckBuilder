package custom.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.util.string.AppendingStringBuffer;

import custom.classes.ShowingCard;

@SuppressWarnings({"unchecked", "rawtypes", "serial"})
public class ListChooser<T> extends ListChoice<T>{
	List<IEventListener> eventListeners = new ArrayList<IEventListener>();
	
	ArrayList<T> availableChoices;
	public T selectedChoice;
	public boolean printerIndicator = false;
	static ShowingCard DEFAULT_OBJECT= new ShowingCard();
    Comparator comparator=null;
	Panel parent;

	public ListChooser(String id, ArrayList<T> choices, T choice) {
		super(id, new Model((Serializable) (choice==null?(T)DEFAULT_OBJECT:choice)),new CollectionModel(choices));
		setOutputMarkupId(true);
		availableChoices = new ArrayList<T>();
		this.availableChoices.addAll(choices);
		this.selectedChoice = choice==null?(T)DEFAULT_OBJECT:choice;
		initBehaviours();
		sort((Comparator<T>) new Comparator<ShowingCard>() {
			@Override
			public int compare(ShowingCard o1, ShowingCard o2) {
				if(o1.cardId>o2.cardId) return 1;
				if(o1.cardId<o2.cardId) return -1;
				return 0;
			}
		});
	}

	private void initBehaviours() {
		OnChangeAjaxBehavior onChange = new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Object obj = getDefaultModelObject();
				selectedChoice  = (T) obj;
				if(selectedChoice == null) {
					//selectedChoice =(T)DEFAULT_OBJECT;
					return;
				}
				target = informListeners(target, "onChange");
			}
		};
		add(onChange);
		AjaxEventBehavior dblClk =new AjaxEventBehavior("ondblclick"){
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				if(selectedChoice==null) return ;
				target = informListeners(target, "onDblClk");
			}
       	};
       	add(dblClk);
	}

	protected AjaxRequestTarget informListeners(AjaxRequestTarget target, String eventType) {
		ShowingCard sc = (ShowingCard)getDefaultModelObject();
		if(eventListeners==null || sc.cardId==null) return target;
		for(IEventListener listener : eventListeners)
			target = listener.onEvent(target, this, eventType);
		return target;
	}
	
	public T getSelectedChoice() {
		return selectedChoice;
	}
	
	public void addEventListener(IEventListener listener){
		eventListeners.add(listener);
	}
	
	public void addChoice(T choice){
		if(!availableChoices.contains(choice))
			availableChoices.add(0,choice);
	}
	
	public void removeChoice(T choice){
		if(selectedChoice.equals(choice)){
			int i = availableChoices.indexOf(selectedChoice);
			if(i==0)
				if(availableChoices.size()==1) 
					selectedChoice=null;
				else
					selectedChoice = availableChoices.get(1);
			else
					selectedChoice=availableChoices.get(i-1);
		}
		availableChoices.remove(choice);
	}
	
	//coloring row 
	protected void appendOptionHtml(AppendingStringBuffer buffer, T choice, int index,
		String selected)
	{
		super.appendOptionHtml(buffer, choice, index, selected);
		if(printerIndicator){
			String str = buffer.toString();
			int i = str.length()-2;
			String s ="";
			while(!s.equals(">")){
				s=new Character(str.charAt(i)).toString();
				i--;
			}
			buffer.insert(++i, getCSSStyle(choice));
		}
	}

	private String getCSSStyle(T choice) {
		ShowingCard sc = (ShowingCard) choice;
		String bc = "background-color:";
		StringBuilder sb = new StringBuilder();
		sb.append(" style=\"");
		if(sc.printed.equals("true"))
			sb.append(bc).append("#A9F5A9;");
		if(sc.printed.equals("false"))
			sb.append(bc).append("#F5A9A9;");
		if(sc.printed.equals("pending"))
			sb.append(bc).append("#F2F5A9;");
		sb.append("\"");
		return sb.toString();
	}

	public void setPrinterIndicator(boolean printerIndicator) {
		this.printerIndicator = printerIndicator;
	}

	public void clearChoices() {
		availableChoices.clear();
		selectedChoice=(T) DEFAULT_OBJECT;
	}

	public void addChoices(ArrayList<T> cards) {
		availableChoices.addAll(cards);
        sort();
	}
	
	public List<T> getChoices(){
		return availableChoices;
	}

	public void setAvailableChoices(ArrayList<ShowingCard> chs) {
		availableChoices.clear();
		for(Object cho : chs){
			T ch = (T)cho;
			addChoice(ch);
		}
        sort();
		selectedChoice = (T) (availableChoices.isEmpty()? DEFAULT_OBJECT:availableChoices.get(0));

	}
	
	public Panel getParentPanel() {
		return parent;
	}
	
	public void setParentPanel(Panel parent) {
		this.parent = parent;
	}
	
	@Override
	protected CharSequence getDefaultChoice(String selectedValue) {
		return "";
	}
	
	public void sort(Comparator<T> comparator){
        this.comparator=comparator;
		Collections.sort(availableChoices, comparator);
	}


    public void sort(){
        if(this.comparator!=null)
            Collections.sort(availableChoices, comparator);
    }

    public T getFirsNextCard(T sc) {
        int index  = getChoices().indexOf(sc);
        return getChoices().get(index==0?0:index-1);
    }
}
