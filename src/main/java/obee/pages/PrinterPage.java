package obee.pages;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import obee.pages.master.MasterPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ByteArrayResource;

import suport.Printer;
import suport.Slika;
import custom.classes.Administration;
import custom.classes.OwnerMap;
import custom.classes.ShowingCard;
import custom.components.IEventListener;
import custom.components.ListChooser;
import custom.components.panels.CardSelectionPanel;

@AuthorizeInstantiation("PRINTER")

@SuppressWarnings({ "unchecked", "rawtypes","serial" })
public class PrinterPage extends MasterPage {
	
	Form<Object> form;
	DropDownChoice<String> userChooser;
	CardSelectionPanel cardPanel,readyPanel;
	
	List<ShowingCard> fullList,filteredList, readyList;
	List<String> usersChoices;
	AjaxLink addAllBtn;
	
	////////////////////////////////////////////////////////
	
	List<ShowingCard> bridge = new ArrayList<ShowingCard>();
	
	////////////////////////////////////////////////////////
	List<ShowingCard> printingReadyList,prepared;
	TextField<Integer> takeNumTbx; 
	Label readyNumLbl,pagesNumLbl, posibleNumLbl;
	Integer takeNum=0, readyNum, pagesNum=0, posibleNum;
	Form<Object> prepareForm, downloadForm;
	ByteArrayResource downloadResource;
	ResourceLink<?> downloadBtn;
	
	public PrinterPage(final PageParameters params) {
		super(params,"Printer");
		initNumbers();
		initForms();
		initLists();
		initComponents();
		initBehaviours();
	}

	private void initLists() {
		Set<String> _usersChoices = new HashSet<String>();
		fullList = Administration.getPrintingReadyList();
		for(ShowingCard sc: fullList)
			_usersChoices.add(sc.owner);
		usersChoices = new ArrayList<String>();
		usersChoices.add("All users");
		for(String ch : _usersChoices)
			usersChoices.add(ch);
		filteredList = new ArrayList<ShowingCard>();
		filteredList.addAll(fullList);
		readyList = new ArrayList<ShowingCard>();
	}
	
	private void initComponents() {
		userChooser = new DropDownChoice<String>("userChooser", 
				new Model(usersChoices.get(0)),
				new Model((Serializable) usersChoices));
		cardPanel = new CardSelectionPanel("cardsPanel", (ArrayList<ShowingCard>) filteredList);
		cardPanel.setPrintCheckBoxVisible(true);
		cardPanel.setFilterVisible(false);
		readyPanel = new CardSelectionPanel("readyPanel", (ArrayList<ShowingCard>) readyList);
		readyPanel.setPrintCheckBoxVisible(true);
		readyPanel.setFilterVisible(false);
		addAllBtn = new AjaxLink("addAllBtn") {
			@Override
			public void onClick(AjaxRequestTarget target) {
    				for(ShowingCard sc: filteredList){
					readyList.add(sc);
					fullList.remove(sc);
					cardPanel.removeChoice(sc);
					readyPanel.addChoice(sc);
				}
				refreshFilterList();
				target.add(readyPanel);
				target.add(cardPanel);
			}
		};
		form.add(addAllBtn);
		form.add(userChooser);
		form.add(cardPanel);
		form.add(readyPanel);
		//////////////////////////////////////////////////////////////
		takeNumTbx = new TextField<Integer>("takeNum", new PropertyModel<Integer>(this, "takeNum"));
		takeNumTbx.setOutputMarkupId(true);
		readyNumLbl = new Label("readyNum", new PropertyModel<Integer>(this, "readyNum"));
		readyNumLbl.setOutputMarkupId(true);
		pagesNumLbl = new Label("pagesNum", new PropertyModel<Integer>(this, "pagesNum"));
		pagesNumLbl.setOutputMarkupId(true);
		posibleNumLbl = new Label("posibleNum", new PropertyModel<Integer>(this, "posibleNum"));
		posibleNumLbl.setOutputMarkupId(true);
		downloadForm.add(pagesNumLbl);
		prepareForm.add(readyNumLbl);
		prepareForm.add(takeNumTbx);
		prepareForm.add(posibleNumLbl);
	}

	private void initForms() {
		form = new Form<Object>("form"){
			@Override
			protected void onSubmit() {
				bridge.addAll(readyList);
				initNumbers();
				prepareForm.setVisible(true);
			}
		};
		add(form);
		prepareForm = new Form<Object>("prepareForm") {
			@Override
			protected void onSubmit() {
				prepare();
				if(pagesNum>0){
					downloadForm.add(downloadBtn = new ResourceLink<Object>("downloadButton", downloadResource){
						@Override
						public void onClick() {
							super.onClick();
							Administration.removeFromPrinter(prepared);
							for(ShowingCard sc: prepared){
								sc.printed="true";
								sc.UPDATE();
							}
							
						}
					});
					downloadForm.setVisible(true);
				}
			}
		};
		downloadForm= new Form<Object>("downloadForm"){
			@Override
			protected void onSubmit() {
				super.onSubmit();
				setResponsePage(PrinterPage.class);
			}
		};
		prepareForm.setVisible(false);
		downloadForm.setVisible(false);
		add(prepareForm);
		add(downloadForm);
	}

	private void initBehaviours() {
		OnChangeAjaxBehavior onChangeAjaxBehavior = new OnChangeAjaxBehavior(){

			@Override
		    protected void onUpdate(AjaxRequestTarget target)
		    {
		        refreshFilterList();
		        cardPanel.setChoices(filteredList);
		        target.add(cardPanel);
		    }
		};
		userChooser.add(onChangeAjaxBehavior);
		IEventListener trejder = new IEventListener() {
			@Override
			public AjaxRequestTarget onEvent(AjaxRequestTarget target, Object sender,
					String eventType) {
				if(eventType.equals("onDblClk")){
					CardSelectionPanel from, to;
					from = (CardSelectionPanel) ((ListChooser) sender).getParentPanel();
					ShowingCard sc = from.listChooser.getSelectedChoice();
					if(from.getId().equals("cardsPanel")){
						to = readyPanel;
						readyList.add(sc);
						fullList.remove(sc);
						refreshFilterList();
					}
					else {
						to=cardPanel;
						readyList.remove(sc);
						fullList.add(sc);
						refreshFilterList();
					}
					from.removeChoice(sc);
					to.addChoice(sc);
					target.add(from);
					target.add(to);
				}
				return target;
			}
		};
		cardPanel.listChooser.addEventListener(trejder);
		readyPanel.listChooser.addEventListener(trejder);
	}
	
	private void refreshFilterList() {
        String selectedUserName = userChooser.getDefaultModelObjectAsString();
        filteredList.clear();
        
        for(ShowingCard sc: fullList)
        	if(sc.owner.equals(selectedUserName) || selectedUserName.equals("All users"))
        		filteredList.add(sc);
	}
	
	protected void prepare() {
		Integer counter=(Integer)takeNumTbx.getDefaultModelObject(), i=0;
		if(counter<=0 || counter>posibleNum || counter==null ){
			info("Illegal value!");
			return;
		}
		List<Slika> pages = new ArrayList<Slika>();
		OwnerMap ownerMap = new OwnerMap();
		while (!printingReadyList.isEmpty() && counter-- > 0) {
			List<ShowingCard> pageList =new ArrayList<ShowingCard>(); 
			for (int j = 0; j < 8; j++) {
				try {
					ShowingCard sc = printingReadyList.get(i++);
					pageList.add(sc);
					ownerMap.add(sc);
					prepared.add(sc);
				} catch (IndexOutOfBoundsException ignorable) {}
			}
			Slika[] sl =Printer.getInstance().generatePrintingPage(pageList);
			pages.addAll(Arrays.asList(sl));
			pagesNum++;
		}
		generateZipSource(pages,ownerMap);
	}

	private void generateZipSource(List<Slika> pages, OwnerMap ownerMap) {
		ByteArrayOutputStream fos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(fos);
		try {
			int index=0;
			for (Slika slika : pages) {
				zos.putNextEntry(new ZipEntry("slika"+index+++".png"));
				zos.write(getBytes(slika));
				zos.closeEntry();
			}
			zos.putNextEntry(new ZipEntry("vlasnici.txt"));
			zos.write(ownerMap.toString().getBytes());
			zos.closeEntry();
			zos.close();
			fos.flush();
			downloadResource = new ByteArrayResource("archive/zip",fos.toByteArray(),"pages.zip");
			fos.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initNumbers() {
		printingReadyList = new ArrayList<ShowingCard>();
		printingReadyList.addAll(bridge);
		prepared = new ArrayList<ShowingCard>();
		int cardnum = printingReadyList.size();
		readyNum = cardnum;
		takeNum = readyNum/8+ (cardnum%8==0?0:1);
		posibleNum= readyNum/8+ (cardnum%8==0?0:1);
		System.out.println("");
	}
	
	private byte[] getBytes(Slika s) {
		byte[] imageInByte=null;
		BufferedImage originalImage = s.toBufferedImage();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write( originalImage, "png", baos );
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageInByte;
	}

}
