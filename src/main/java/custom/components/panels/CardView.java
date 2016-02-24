package custom.components.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import custom.components.IEventListener;
import custom.components.ImageWindow;

@SuppressWarnings("serial")
public class CardView extends Panel implements IEventListener {

	private boolean hackerMode = false;
	Boolean flipEnabled = true;
	List<IEventListener> listeners = new ArrayList<IEventListener>();
	public AjaxLink<?> flipButton, link;
	public ImageWindow image;
	private Label rarityLbl;
	@SuppressWarnings("unused")
	private String rarityValue;
	
	
	public CardView(String id) {
		super(id);
		setOutputMarkupId(true);
		image = new ImageWindow("image", null,this, hackerMode);
		flipButton = new AjaxLink<Object>("flipButton") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				image.flip();
				target.add(image);
			}
			
			@Override
			public boolean isVisible() {
				return super.isVisible()&& flipEnabled;
			}
		};
		link = new AjaxLink<Object>("link") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				informListeners(target, "onClick");
			}
		};
		add(link);
		link.add(image);
		add(flipButton);
		rarityLbl = new Label("rarityLbl", new PropertyModel<String>( this,"rarityValue"));
		add(rarityLbl);
		setRarityLblVisible(false);
	}

	public CardView(String id, boolean hackerMode) {
		super(id);
		this.hackerMode = hackerMode;
		setOutputMarkupId(true);
		image = new ImageWindow("image", null,this, this.hackerMode);
		flipButton = new AjaxLink<Object>("flipButton") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				image.flip();
				target.add(image);
			}

			@Override
			public boolean isVisible() {
				return super.isVisible()&& flipEnabled;
			}
		};
		link = new AjaxLink<Object>("link") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				informListeners(target, "onClick");
			}
		};
		add(link);
		link.add(image);
		add(flipButton);
		rarityLbl = new Label("rarityLbl", new PropertyModel<String>( this,"rarityValue"));
		add(rarityLbl);
		setRarityLblVisible(false);
	}
	
	public Boolean isFlipEnabled() {
		return flipEnabled;
	}
	
	public void setFlipEnabled(Boolean flipEnabled) {
		this.flipEnabled = flipEnabled;
	}
	
	private void informListeners(AjaxRequestTarget target, String eventType) {
		for(IEventListener listener : listeners)
			listener.onEvent(target, image, eventType);
	}
	
	public void addEventListener(IEventListener l){
		listeners.add(l);
	}
	
	public void setRarityLblVisible(boolean b){
		rarityLbl.setVisible(b);
	}

	@Override
	public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
			String eventType) {
		return image.onEvent(target, sender, eventType);
	}
	
	public void setRarity(String rarity){
		this.rarityValue=rarity;
	}
}
