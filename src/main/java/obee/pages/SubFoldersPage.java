package obee.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import obee.pages.master.MasterPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import custom.classes.Administration;
import custom.classes.ShowingCard;
import custom.classes.User;
import custom.components.IEventListener;
import custom.components.ListChooser;
import custom.components.panels.CardSelectionPanel;
import custom.components.panels.CardView;

@SuppressWarnings({"serial","unchecked"})
@AuthorizeInstantiation("USER")
public class SubFoldersPage extends MasterPage {

	private List<ShowingCard>[] subFolderList = new List[6];
	private ArrayList<ShowingCard> usingList ;
	private User user;
	private CardSelectionPanel usingPanel;
	private CardSelectionPanel[] subFolderPanel= new CardSelectionPanel[6];
	private Form<Object> form;
	private CheckBox[] checkBoxes = new CheckBox[6];
	private OnChangeAjaxBehavior[] cbChecked= new OnChangeAjaxBehavior[6];
	protected Integer selectedIndex;
	@SuppressWarnings("rawtypes")
	private AjaxLink printButton, pButton, uButton;
	private DropDownChoice<String> sortDropDown;
	private List<String> sortChoices;
	private HashMap<String, Comparator<ShowingCard>> comparatorMap;

	
	public SubFoldersPage(PageParameters params) {
		super(params, "Folders");
		user=session.getUser();
//		user.getSubfolders().validate();
//		user=mongo.getUser(getUserName());
		initLists();
		initForm();
		initComponents();
		initBehaviours();
	
	}

	private void initForm() {
		form = new Form<Object>("form"){
			@Override
			protected void onSubmit() {
				save();
			}
		};
		add(form);
	}

	protected void save() {
		user.getSubfolders().setSubFolders(subFolderList);
		user.UPDATE();
	}

	@SuppressWarnings("rawtypes")
	private void initComponents() {
		sortDropDown = new DropDownChoice<String>("sortDropDown", 
				new Model(sortChoices.get(0)),
				new ListModel(sortChoices));
		sortDropDown.setVisible(false);
		form.add(sortDropDown);
		printButton = new AjaxLink("printButton") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				List<ShowingCard> printList = new ArrayList<ShowingCard>();
				for(ShowingCard sc:subFolderList[selectedIndex])
					if(sc.printed.equals("false"))
						printList.add(sc);
				Administration.sendToPrinter(printList);
				info(printList.size()+" cards sent for printing.");
				target.add(feedback);
				target.add(subFolderPanel[selectedIndex]);
			}
			
		};
		form.add(printButton);
        pButton = new AjaxLink("pButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                CardSelectionPanel csp = subFolderPanel[selectedIndex];
                ShowingCard sc = csp.listChooser.selectedChoice;
                sc.printed = "true";
                sc.UPDATE();
                target.add(csp);
            }
        };
        form.add(pButton);
        uButton = new AjaxLink("uButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                CardSelectionPanel csp = subFolderPanel[selectedIndex];
                ShowingCard sc = csp.listChooser.selectedChoice;
                sc.printed = "false";
                sc.UPDATE();
                target.add(csp);
            }
        };
        if(!user.isAdmin()){
            pButton.add(new AttributeAppender("style",";display:none"));
            uButton.add(new AttributeAppender("style",";display:none"));
        }
        form.add(uButton);
		CardView image = new CardView("image");
		image.setRarityLblVisible(true);
		usingPanel = new CardSelectionPanel("usingPanel", usingList);
		usingPanel.listChooser.addEventListener(image);
		usingPanel.listChooser.setMaxRows(18);
		subFolderPanel[0] = new CardSelectionPanel("sfPanel0", (ArrayList<ShowingCard>) subFolderList[0]){
			@Override
			protected void onTitleChanged() {
				super.onTitleChanged();
				user.subFolderNames[0]=title;
				user.UPDATE();
			}
		};
		subFolderPanel[1] = new CardSelectionPanel("sfPanel1", (ArrayList<ShowingCard>) subFolderList[1]){
			@Override
			protected void onTitleChanged() {
				super.onTitleChanged();
				user.subFolderNames[1]=title;
				user.UPDATE();
			}
		};
		subFolderPanel[2] = new CardSelectionPanel("sfPanel2", (ArrayList<ShowingCard>) subFolderList[2]){
			@Override
			protected void onTitleChanged() {
				super.onTitleChanged();
				user.subFolderNames[2]=title;
				user.UPDATE();
			}
		};
		subFolderPanel[3] = new CardSelectionPanel("sfPanel3", (ArrayList<ShowingCard>) subFolderList[3]){
			@Override
			protected void onTitleChanged() {
				super.onTitleChanged();
				user.subFolderNames[3]=title;
				user.UPDATE();
			}
		};
		subFolderPanel[4] = new CardSelectionPanel("sfPanel4", (ArrayList<ShowingCard>) subFolderList[4]){
			@Override
			protected void onTitleChanged() {
				super.onTitleChanged();
				user.subFolderNames[4]=title;
				user.UPDATE();
			}
		};
		subFolderPanel[5] = new CardSelectionPanel("sfPanel5", (ArrayList<ShowingCard>) subFolderList[5]){
			@Override
			protected void onTitleChanged() {
				super.onTitleChanged();
				user.subFolderNames[5]=title;
				user.UPDATE();
			}
		};
	
		form.add(usingPanel);
		for (int i = 0; i < subFolderPanel.length; i++) {
			form.add(subFolderPanel[i]);
			checkBoxes[i] = new CheckBox("cb"+i, new Model<Boolean>());
			checkBoxes[i].setOutputMarkupId(true);
			form.add(checkBoxes[i]);
			subFolderPanel[i].setFilterVisible(false);
			subFolderPanel[i].setPrintCheckBoxVisible(false);
			subFolderPanel[i].listChooser.addEventListener(image);
			subFolderPanel[i].setTitle(user.subFolderNames[i]);
			subFolderPanel[i].setTitleVisible(true);
		}
		selectedIndex=0;
		checkBoxes[0].setDefaultModelObject(true);
		form.add(image);
	}

	private void initLists() {
		usingList = new ArrayList<ShowingCard>();
		usingList.addAll(user.getSubfolders().getFreeCards());
		subFolderList[0] =new ArrayList<ShowingCard>();
		subFolderList[0].addAll(user.getSubfolders().getSubFolder(0));
		subFolderList[1] =new ArrayList<ShowingCard>();
		subFolderList[1].addAll(user.getSubfolders().getSubFolder(1));
		subFolderList[2] =new ArrayList<ShowingCard>();
		subFolderList[2].addAll(user.getSubfolders().getSubFolder(2));
		subFolderList[3] =new ArrayList<ShowingCard>();
		subFolderList[3].addAll(user.getSubfolders().getSubFolder(3));
		subFolderList[4] =new ArrayList<ShowingCard>();
		subFolderList[4].addAll(user.getSubfolders().getSubFolder(4));
		subFolderList[5] =new ArrayList<ShowingCard>();
		subFolderList[5].addAll(user.getSubfolders().getSubFolder(5));
		sortChoices = new ArrayList<String>();
		sortChoices.add("mana cost");
		sortChoices.add("rarity");
		comparatorMap = new HashMap<String, Comparator<ShowingCard>>();
		comparatorMap.put("mana cost",new Comparator<ShowingCard>() {
			@Override
			public int compare(ShowingCard o1, ShowingCard o2) {
				int mc1 = o1.cardInfo.convertedManaCost;
				int mc2 = o2.cardInfo.convertedManaCost;
				if(mc1<mc2) return -1;
				if(mc1>mc2) return 1;
				return 0;
			}
		});
		comparatorMap.put("rarity",new Comparator<ShowingCard>() {
			List<String> hierarchy = Arrays.asList(new String[]{"common","uncommon","rare","mythic"});
			@Override
			public int compare(ShowingCard o1, ShowingCard o2) {
				int mc1 = hierarchy.indexOf(o1.cardInfo.rarity.toLowerCase());
				int mc2 = hierarchy.indexOf(o2.cardInfo.rarity.toLowerCase());
				if(mc1<mc2) return -1;
				if(mc1>mc2) return 1;
				return 0;
			}
		});
		sortEmAll(comparatorMap.get(sortChoices.get(0)));
	}

	private void initBehaviours() {
		OnChangeAjaxBehavior onChangesort = new OnChangeAjaxBehavior(){

			@Override
		    protected void onUpdate(AjaxRequestTarget target)
		    {
				String selection = sortDropDown.getDefaultModelObjectAsString();
				Comparator<ShowingCard> c = comparatorMap.get(selection);
				sortEmAll(c);
				target.add(usingPanel);
				for(int i=0;i<4;i++)
					target.add(subFolderPanel[i]);
				
		    }
		};
		sortDropDown.add(onChangesort);
		cbChecked[0] = new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) checkBoxes[0].getDefaultModelObject();
				if(!b) 
					checkBoxes[0].setDefaultModelObject(true);
				else if(selectedIndex!=null){
					checkBoxes[selectedIndex].setDefaultModelObject(false);
					target.add(checkBoxes[selectedIndex]);
				}
				selectedIndex = 0;
				target.add(checkBoxes[0]);
			}
		};
		checkBoxes[0].add(cbChecked[0]);
		cbChecked[1] = new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) checkBoxes[1].getDefaultModelObject();
				if(!b) checkBoxes[1].setDefaultModelObject(true);
				if(selectedIndex!=null){
					checkBoxes[selectedIndex].setDefaultModelObject(false);
					target.add(checkBoxes[selectedIndex]);
				}
				selectedIndex = 1;
				target.add(checkBoxes[1]);
			}
		};
		checkBoxes[1].add(cbChecked[1]);
		cbChecked[2] = new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) checkBoxes[2].getDefaultModelObject();
				if(!b) checkBoxes[2].setDefaultModelObject(true);
				if(selectedIndex!=null){
					checkBoxes[selectedIndex].setDefaultModelObject(false);
					target.add(checkBoxes[selectedIndex]);
				}
				selectedIndex = 2;
				target.add(checkBoxes[2]);
			}
		};
		checkBoxes[2].add(cbChecked[2]);
		cbChecked[3] = new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) checkBoxes[3].getDefaultModelObject();
				if(!b) checkBoxes[3].setDefaultModelObject(true);
				if(selectedIndex!=null){
					checkBoxes[selectedIndex].setDefaultModelObject(false);
					target.add(checkBoxes[selectedIndex]);
				}
				selectedIndex = 3;
				target.add(checkBoxes[3]);
			}
		};
		checkBoxes[3].add(cbChecked[3]);
		cbChecked[4] = new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) checkBoxes[4].getDefaultModelObject();
				if(!b) checkBoxes[4].setDefaultModelObject(true);
				if(selectedIndex!=null){
					checkBoxes[selectedIndex].setDefaultModelObject(false);
					target.add(checkBoxes[selectedIndex]);
				}
				selectedIndex = 4;
				target.add(checkBoxes[4]);
			}
		};
		checkBoxes[4].add(cbChecked[4]);
		cbChecked[5] = new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) checkBoxes[5].getDefaultModelObject();
				if(!b) checkBoxes[5].setDefaultModelObject(true);
				if(selectedIndex!=null){
					checkBoxes[selectedIndex].setDefaultModelObject(false);
					target.add(checkBoxes[selectedIndex]);
				}
				selectedIndex = 5;
				target.add(checkBoxes[5]);
			}
		};
		checkBoxes[5].add(cbChecked[5]);
		/*IEventListener trejder = new IEventListener() {
			@SuppressWarnings("rawtypes")
			@Override
			public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
					String eventType) {
				if(eventType.equals("onDblClk")){
					
					CardSelectionPanel from, to;
					List<ShowingCard> fromList, toList;
					from = (CardSelectionPanel) ((ListChooser) sender).getParentPanel();
					String fromIdS = from.getId();
					if(fromIdS.equals("usingPanel")){
						fromList =usingList;
						to = subFolderPanel[selectedIndex];
						toList = subFolderList[selectedIndex];
					} else {
						int fromId = Integer.parseInt(fromIdS.substring(fromIdS.length()-1));
						to = usingPanel;
						fromList = subFolderList[fromId];
						toList = usingList;
					}
					ShowingCard sc = (ShowingCard) from.listChooser.getDefaultModelObject();
					if(sc==null)
						return setSelectedIndex(from, target);
					fromList.remove(sc);
					toList.add(sc);
					from.setChoices(fromList);
					to.setChoices(toList);
					target.add(from);
					target.add(to);
				}
				save();
				return target;
			}

			private AjaxRequestTarget setSelectedIndex(CardSelectionPanel from,
					AjaxRequestTarget target) {
				String idS = from.getId();
				int len = idS.length();
				String indexS = idS.substring(len-1);
				int index = Integer.parseInt(indexS);
				if(!selectedIndex.equals(index)){
					checkBoxes[index].setDefaultModelObject(true);
					target.add(checkBoxes[index]);
					if(selectedIndex!=null){
						checkBoxes[selectedIndex].setDefaultModelObject(false);
						target.add(checkBoxes[selectedIndex]);
					}
					selectedIndex = index;
				}
				return target;
			}

		};*/
		 IEventListener bigListener = new IEventListener() {
		    	@Override
		    	public AjaxRequestTarget onEvent(AjaxRequestTarget target,
		    			Object sender, String eventType) {
		    		if(eventType.equals("onDblClk")){
		    			CardSelectionPanel csp =(CardSelectionPanel)((ListChooser<ShowingCard>)sender).getParentPanel();
						CardSelectionPanel sfp = subFolderPanel[selectedIndex];
						ShowingCard sc = (ShowingCard) csp.listChooser.getDefaultModelObject();
						if(sc==null) return target;
						csp.removeChoice(sc);
						user.getSubfolders().add(sc, selectedIndex);
						user.UPDATE();
						sfp.addChoice(sc);
						target.add(usingPanel);
						target.add(sfp);
		    		}
					return target;
		    	}
		    };
			IEventListener smallListener = new IEventListener() {
			    	@Override
			    	public AjaxRequestTarget onEvent(AjaxRequestTarget target,
			    			Object sender, String eventType) {
			    		if(eventType.equals("onDblClk")){
			    			CardSelectionPanel csp =(CardSelectionPanel)((ListChooser<ShowingCard>)sender).getParentPanel();
							ShowingCard sc = (ShowingCard) csp.listChooser.getDefaultModelObject();
							if(sc==null) return target;
							csp.removeChoice(sc);
							int index = getIndex(csp.getId());
							user.getSubfolders().remove(sc, index);
							usingPanel.addChoice(sc);
							user.UPDATE();
							target.add(usingPanel);
							target.add(csp);
			    		}
						return target;
			    	}

					private int getIndex(String id) {
						return Integer.parseInt(id.substring(id.length()-1));
					}
			    };
		usingPanel.listChooser.addEventListener(bigListener);
		for (int i = 0; i < subFolderPanel.length; i++) {
			subFolderPanel[i].listChooser.addEventListener(smallListener);
		}
		OnChangeAjaxBehavior printerChecked = new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) usingPanel.printIndicator.getDefaultModelObject();
				for (int i = 0; i < subFolderPanel.length; i++) {
					subFolderPanel[i].listChooser.setPrinterIndicator(b);
					target.add(subFolderPanel[i]);
				}
			}
		};
		usingPanel.printIndicator.add(printerChecked);
	}
	
	protected void updateList() {
		// TODO Auto-generated method stub
		
	}

	public void sortEmAll(Comparator<ShowingCard> comp){
		Collections.sort(usingList, comp);
		for(int i=0; i<4;i++){
			Collections.sort(subFolderList[i],comp);
		}
	}
}
