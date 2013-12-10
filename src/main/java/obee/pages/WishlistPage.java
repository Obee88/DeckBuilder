package obee.pages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import obee.pages.master.MasterPage;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.core.request.handler.BookmarkablePageRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.DefaultCssAutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import custom.classes.Administration;
import custom.classes.TradingProposal;
import custom.classes.User;
import custom.classes.WishListItem;
import custom.components.panels.CardView;
import custom.components.panels.DeleteRowPanel;
import custom.components.panels.TradingProposalPanel;

@AuthorizeInstantiation("USER")
@SuppressWarnings({ "unchecked", "rawtypes","serial","unused" })
public class WishlistPage extends MasterPage {
	
	private Form form;
	private AjaxLink searcbBtn;
	private TextField<String> searchTbx;
	private DataTable wishlistTable;
    String tbxValue="";
	User usr = session.getUser();
	List<WishListItem> wishList;
	List<User> allUsrs;
    String focusCard;
	private CardView image;
    private DropDownChoice<User> filterCmbx;
    List<String> usersStringList;
    private ArrayList<WishListItem> fullWishList;

    public WishlistPage(PageParameters params) {
		super(params, "WishList");
        focusCard = params.get("focus")==null?null:params.get("focus").toString();
		initWishlist();
		initForm();
		initComponents();
		initBehaviours();
	}
	
	private void initWishlist() {
		wishList = new ArrayList<WishListItem>();
        fullWishList = new ArrayList<WishListItem>();
        getUserStringList();
		List<String> list = usr.getWishList();
        DBCursor cur = mongo.cardsCollection.find(new BasicDBObject("info.name",new BasicDBObject("$in",strl2DBL(list))));
        BasicDBList dbl = new BasicDBList();
        while (cur.hasNext())
            dbl.add(cur.next());
		for(String s:list)  {
            WishListItem w = new WishListItem(s, dbl);
			wishList.add(w);
            fullWishList.add(w);
        }
		Collections.sort(wishList);
	}

    private BasicDBList strl2DBL(List<String> list) {
        BasicDBList ret = new BasicDBList();
        for (String s : list)
            ret.add(s);
        return ret;

    }

    private void initComponents() {
		image = new CardView("image"){
			@Override
			public AjaxRequestTarget onEvent(AjaxRequestTarget target,
					Object sender, String eventType) {
				image.setUrl(mongo.getImageUrlFromName( (String) sender));
				target.add(this.image);
				return target;
			}
		};
		image.setFlipEnabled(false);
        if(focusCard!=null)
            image.image.setUrl(mongo.getImageUrlFromName(focusCard));
		form.add(image);
		wishlistTable = new AjaxFallbackDefaultDataTable("wishlistTable", initColumns(), getDataProvider(getUserName()), 6){
			@Override
			protected Item newRowItem(String id, int index, final IModel model) {
				Item rowItem = new Item(id, index, model);
			    rowItem.add(new AjaxEventBehavior("onclick") {
					
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						WishListItem wli =(WishListItem)model.getObject();
						target =image.onEvent(target,wli.getName(),"onChange");
					}
				}); 
			    return rowItem;
			}
		};
        wishlistTable.setOutputMarkupId(true);
		wishlistTable.setCurrentPage(getIndex(focusCard)/wishlistTable.getItemsPerPage());
		searchTbx = new DefaultCssAutoCompleteTextField<String>("searchTbx", new PropertyModel(this,"tbxValue")) {
			@Override
			protected Iterator<String> getChoices(String input) {
				Set<String> choices = mongo.getCardsThatStartsWith(input, 11);
				Iterator<String> it = choices.iterator();
				List<String> ret = new ArrayList<String>();
				for(int i=0; i< 10 ; i++){
					if(it.hasNext())
						ret.add(it.next());
					else break;
				}
				if(choices.size()>10){
					ret.add("...");
				}
				return ret.iterator();
			}
		};
		searcbBtn = new AjaxLink("searchBtn"){
			@Override
			public void onClick(AjaxRequestTarget target) {
				//TODO:
				System.out.println("");
			}
			
		};
        filterCmbx = new DropDownChoice<User>("filterCmbx",
                new Model(usersStringList.get(0)),
                new Model((Serializable) usersStringList));
        form.add(filterCmbx);
		form.add(wishlistTable);
		form.add(searchTbx);
		form.add(searcbBtn);
	}

    private int getIndex(String focusCard) {
        int len = wishList.size();
        for(int i = 0; i<len;++i){
            WishListItem wli = wishList.get(i);
            if (wli.getName().equals(focusCard))
                return i;
        }
        return 0;
    }

    private void initForm() {
		form = new Form("form") {
			@Override
			protected void onSubmit() {
				//
				System.out.println("");
			}
		};
		add(form);
	}

	private void initBehaviours() {
		 searchTbx.add(new AjaxFormSubmitBehavior(form, "onchange")
	        {
	            @Override
	            protected void onSubmit(AjaxRequestTarget target)
	            {
	            	if(!mongo.isLegalName(tbxValue))
	            		return;
					usr.addToWishlist(tbxValue);
					usr.UPDATE();
                    PageParameters params = new PageParameters();
                    params.add("focus",tbxValue.toString());
                    tbxValue="";
					setResponsePage(WishlistPage.class, params);
	            }

	            @Override
	            protected void onError(AjaxRequestTarget target)
	            {
	            }
	        });

        filterCmbx.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String selectedUser = filterCmbx.getDefaultModelObjectAsString();
                wishList.clear();
                for(WishListItem wli : fullWishList)
                    if(wli.containsUser(selectedUser))
                        wishList.add(wli);
                Collections.sort(wishList);
                target.add(wishlistTable);
            }
        });
	}

    private List initColumns() {
		List<IColumn> columns = new ArrayList<IColumn>();
		PropertyColumn pr = new PropertyColumn(new Model<String>("Name"),"name");
		columns.add(pr);
		columns.add(new PropertyColumn(new Model<String>("Using"),"usingString"));
		columns.add(new PropertyColumn(new Model<String>("Trading"),"tradingString"));
		columns.add(new PropertyColumn(new Model<String>("Booster"),"boosterString"));
		columns.add(new AbstractColumn(new Model<String>("Actions"))
        {
			@Override
			public void populateItem(Item cellItem, String componentId, IModel model) {
				//
				WishListItem wli = (WishListItem)model.getObject();
				cellItem.add(new DeleteRowPanel(componentId,wli.getName()) {
					
					@Override
					public void doDelete() {
						String name = getName();
						usr.removeFromWishList(name);
						usr.UPDATE();
						setResponsePage(WishlistPage.class);
					}
				});
			}
        });
		return columns;
	}

	private ISortableDataProvider getDataProvider(final String userName) {
		ISortableDataProvider provider = new ISortableDataProvider() {
			
			@Override
			public void detach() {
				
			}

			@Override
			public Iterator<? extends WishListItem> iterator(long first,
					long count) {
					
				return wishList.subList((int)first, (int)count+(int)first).iterator();
			}

			@Override
			public long size() {
				return wishList.size();
			}

			@Override
			public IModel model(Object object) {
				return new Model((Serializable) object);
			}

			@Override
			public ISortState getSortState() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		return provider;
	}

	public String getTbxValue() {
		return tbxValue;
	}

	public void setTbxValue(String tbxValue) {
		this.tbxValue = tbxValue;
	}

    private void getUserStringList() {
        usersStringList = (List<String>) mongo.usersNameList();
        usersStringList.add(0,"all");
    }
}


