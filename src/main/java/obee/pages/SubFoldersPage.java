package obee.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import custom.components.panels.puPanel;
import obee.pages.master.MasterPage;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.Panel;
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

	private List<ShowingCard>[] subFolderList = new List[12];
	private ArrayList<ShowingCard> usingList ;
	private User user = null;
	private CardSelectionPanel usingPanel;
	private CardSelectionPanel[] subFolderPanel= new CardSelectionPanel[12];
	private Form<Object> form;
	private CheckBox[] checkBoxes = new CheckBox[12];
	private OnChangeAjaxBehavior[] cbChecked= new OnChangeAjaxBehavior[12];
	protected Integer selectedIndex;
	@SuppressWarnings("rawtypes")
	private AjaxLink printButton;
	private DropDownChoice<String> sortDropDown;
	private List<String> sortChoices;
	private HashMap<String, Comparator<ShowingCard>> comparatorMap;
    private Panel printUnprintPanel;
    private AjaxLink pButton,uButton;
    private ShowingCard lastClickedCard =null;
    CardSelectionPanel lastClickedFolder= null;


    public SubFoldersPage(PageParameters params) {
		super(params, "Folders");
//		user=mongo.getUser(getUserName());
        user = currentUser;
        user.setSubfolders();
		user.getSubfolders().validate();
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
        printUnprintPanel = new puPanel("printUnprintPanel");
        form.add(printUnprintPanel);
        boolean isPrinter = user.isPrinter();
        printUnprintPanel.add(new AttributeAppender("style","display:"+(isPrinter?"inline":"none")));
        pButton = new AjaxLink("pButton"){
            @Override
            public void onClick(AjaxRequestTarget target) {
                if(lastClickedCard==null || lastClickedFolder==null) return;
                ShowingCard selectedCard = lastClickedCard;
                if (selectedCard==null) return;
                selectedCard.printed = "true";
                selectedCard.UPDATE();
                target.add(lastClickedFolder);
            }
        };
        printUnprintPanel.add(pButton);
        uButton = new AjaxLink("uButton"){
            @Override
            public void onClick(AjaxRequestTarget target) {
                if(lastClickedCard==null || lastClickedFolder==null) return;
                ShowingCard selectedCard = lastClickedCard;
                if (selectedCard==null) return;
                selectedCard.printed = "false";
                selectedCard.UPDATE();
                target.add(lastClickedFolder);
            }
        };
        printUnprintPanel.add(uButton);
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
		subFolderPanel[6] = new CardSelectionPanel("sfPanel6", (ArrayList<ShowingCard>) subFolderList[6]){
			@Override
			protected void onTitleChanged() {
				super.onTitleChanged();
				user.subFolderNames[6]=title;
				user.UPDATE();
			}
		};
		subFolderPanel[7] = new CardSelectionPanel("sfPanel7", (ArrayList<ShowingCard>) subFolderList[7]){
			@Override
			protected void onTitleChanged() {
				super.onTitleChanged();
				user.subFolderNames[7]=title;
				user.UPDATE();
			}
		};
		subFolderPanel[8] = new CardSelectionPanel("sfPanel8", (ArrayList<ShowingCard>) subFolderList[8]){
			@Override
			protected void onTitleChanged() {
				super.onTitleChanged();
				user.subFolderNames[8]=title;
				user.UPDATE();
			}
		};
		subFolderPanel[9] = new CardSelectionPanel("sfPanel9", (ArrayList<ShowingCard>) subFolderList[9]){
			@Override
			protected void onTitleChanged() {
				super.onTitleChanged();
				user.subFolderNames[9]=title;
				user.UPDATE();
			}
		};
		subFolderPanel[10] = new CardSelectionPanel("sfPanel10", (ArrayList<ShowingCard>) subFolderList[10]){
			@Override
			protected void onTitleChanged() {
				super.onTitleChanged();
				user.subFolderNames[10]=title;
				user.UPDATE();
			}
		};
		subFolderPanel[11] = new CardSelectionPanel("sfPanel11", (ArrayList<ShowingCard>) subFolderList[11]){
			@Override
			protected void onTitleChanged() {
				super.onTitleChanged();
				user.subFolderNames[11]=title;
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
		for (int i =0; i<12; ++i) {
			subFolderList[i] = new ArrayList<ShowingCard>();
			subFolderList[i].addAll(user.getSubfolders().getSubFolder(i));
		}
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

		cbChecked[6] = new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) checkBoxes[6].getDefaultModelObject();
				if(!b) checkBoxes[6].setDefaultModelObject(true);
				if(selectedIndex!=null){
					checkBoxes[selectedIndex].setDefaultModelObject(false);
					target.add(checkBoxes[selectedIndex]);
				}
				selectedIndex = 6;
				target.add(checkBoxes[6]);
			}
		};
		checkBoxes[6].add(cbChecked[6]);

		cbChecked[7] = new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) checkBoxes[7].getDefaultModelObject();
				if(!b) checkBoxes[7].setDefaultModelObject(true);
				if(selectedIndex!=null){
					checkBoxes[selectedIndex].setDefaultModelObject(false);
					target.add(checkBoxes[selectedIndex]);
				}
				selectedIndex = 7;
				target.add(checkBoxes[7]);
			}
		};
		checkBoxes[7].add(cbChecked[7]);

		cbChecked[8] = new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) checkBoxes[8].getDefaultModelObject();
				if(!b) checkBoxes[8].setDefaultModelObject(true);
				if(selectedIndex!=null){
					checkBoxes[selectedIndex].setDefaultModelObject(false);
					target.add(checkBoxes[selectedIndex]);
				}
				selectedIndex = 8;
				target.add(checkBoxes[8]);
			}
		};
		checkBoxes[8].add(cbChecked[8]);

		cbChecked[9] = new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) checkBoxes[9].getDefaultModelObject();
				if(!b) checkBoxes[9].setDefaultModelObject(true);
				if(selectedIndex!=null){
					checkBoxes[selectedIndex].setDefaultModelObject(false);
					target.add(checkBoxes[selectedIndex]);
				}
				selectedIndex = 9;
				target.add(checkBoxes[9]);
			}
		};
		checkBoxes[9].add(cbChecked[9]);

		cbChecked[10] = new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) checkBoxes[10].getDefaultModelObject();
				if(!b) checkBoxes[10].setDefaultModelObject(true);
				if(selectedIndex!=null){
					checkBoxes[selectedIndex].setDefaultModelObject(false);
					target.add(checkBoxes[selectedIndex]);
				}
				selectedIndex = 10;
				target.add(checkBoxes[10]);
			}
		};
		checkBoxes[10].add(cbChecked[10]);

		cbChecked[11] = new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Boolean b = (Boolean) checkBoxes[11].getDefaultModelObject();
				if(!b) checkBoxes[11].setDefaultModelObject(true);
				if(selectedIndex!=null){
					checkBoxes[selectedIndex].setDefaultModelObject(false);
					target.add(checkBoxes[selectedIndex]);
				}
				selectedIndex = 11;
				target.add(checkBoxes[11]);
			}
		};
		checkBoxes[11].add(cbChecked[11]);



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
        IEventListener clicked = new IEventListener() {
            @Override
            public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender, String eventType) {
                if(eventType.equals("onClick")){
                    lastClickedFolder = (CardSelectionPanel) ((ListChooser<ShowingCard>)sender).getParent();
                    lastClickedCard = ((ListChooser<ShowingCard>)sender).getSelectedChoice();
                }
                return target;
            }
        };
        usingPanel.listChooser.addEventListener(bigListener);
        for (int i = 0; i < subFolderPanel.length; i++) {
            subFolderPanel[i].listChooser.addEventListener(smallListener);
            subFolderPanel[i].listChooser.addEventListener(clicked);
        }
        usingPanel.listChooser.addEventListener(clicked);

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
