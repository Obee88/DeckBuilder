package obee.pages;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import custom.classes.User;
import custom.classes.UserMessage;

import obee.pages.master.MasterPage;
import org.apache.wicket.request.resource.JavaScriptResourceReference;


@SuppressWarnings("serial")
@AuthorizeInstantiation("USER")
public class UserMainPage extends MasterPage {

    private static final JavaScriptResourceReference MYPAGE_JS = new JavaScriptResourceReference(UserMainPage.class, "UserMainPage.js");

    private final AjaxLink<Object> inboxBtn,outboxBtn,systemBtn,allBtn;
    Form<Object> form;
	ListView<UserMessage> messagesView;
    Box currentBox = Box.all;
    User user = mongo.getUser(getUserName());

    public UserMainPage(PageParameters parameters) {
		super(parameters,"Home");
		form = new Form<Object>("form");
        String bs = parameters.get("box").toString();
        List<UserMessage> messages;
        if(bs!=null)
            if( bs.equals("system")){
                currentBox=Box.system;
                messages=user.getSystemMessages();
            }
            else if(bs.equals("inbox")){
                currentBox=Box.inbox;
                messages=user.getIngoingMessages();
            }
            else
                messages = user.getMessages();
        else
            messages = user.getMessages();
		messagesView = new ListView<UserMessage>("msgList",messages) {
			@Override
			protected void populateItem(ListItem<UserMessage> item) {
				final UserMessage msg =item.getModelObject();
                String subject = msg.getSubject();
                AjaxLink delbtn = new AjaxLink<Void>("deleteBtn") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        int id = msg.getId();
                        user.removeMessage(id);
                        user.UPDATE();
                        setResponsePage(UserMainPage.class,new PageParameters().add("box",currentBox.toString()));
                    }
                };
                if(currentBox==Box.outbox){
                    subject=subject.substring(subject.indexOf("]"));
                    subject="[to:"+mongo.getMessageOwner(msg)+subject;
                    delbtn.add(new AttributeModifier("style", "display:none;"));
                }
				////////////////////////////////////////////////////////////////////////////////////////
                WebComponent messageComponent = new WebComponent("textLbl"){
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        Response response = getRequestCycle().getResponse();
                        response.write("<p>");
                        response.write(msg.getEscapedText());
                        response.write("</p>");
                    }
                };
                WebComponent subjectComponent = new WebComponent("subjectLbl"){
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        Response response = getRequestCycle().getResponse();
                        response.write(msg.getEscapedSubject());
                    }
                };
                ////////////////////////////////////////////////////////////////////////////////////////
//                item.add(new Label("subjectLbl",subject));
//				item.add(new Label("textLbl",msg.getText()));
                item.add(subjectComponent);
                item.add(messageComponent);
				item.add(new Label("dateLbl",msg.getDateString()));
				item.add(delbtn);
            }
		};
        messagesView.setOutputMarkupId(true);
		form.add(messagesView);
		add(form);
        inboxBtn = new AjaxLink<Object>("inboxBtn") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                messagesView.setList(user.getIngoingMessages());
                blue(outboxBtn);
                green(inboxBtn);
                blue(systemBtn);
                blue(allBtn);
                currentBox = Box.inbox;
                ajaxRequestTarget.add(form);
                ajaxRequestTarget.add(outboxBtn);
                ajaxRequestTarget.add(inboxBtn);
                ajaxRequestTarget.add(systemBtn);
                ajaxRequestTarget.add(allBtn);
            }
        };
        inboxBtn.setOutputMarkupId(true);
        add(inboxBtn);
        outboxBtn = new AjaxLink<Object>("outboxBtn") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                messagesView.setList(user.getOutgoingMessages());
                green(outboxBtn);
                blue(inboxBtn);
                blue(systemBtn);
                blue(allBtn);
                currentBox= Box.outbox;
                ajaxRequestTarget.add(form);
                ajaxRequestTarget.add(outboxBtn);
                ajaxRequestTarget.add(inboxBtn);
                ajaxRequestTarget.add(systemBtn);
                ajaxRequestTarget.add(allBtn);
            }
        };
        outboxBtn.setOutputMarkupId(true);
        add(outboxBtn);
        systemBtn = new AjaxLink<Object>("systemBtn") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                messagesView.setList(user.getSystemMessages());
                blue(outboxBtn);
                blue(inboxBtn);
                green(systemBtn);
                blue(allBtn);
                currentBox=Box.system;
                ajaxRequestTarget.add(form);
                ajaxRequestTarget.add(outboxBtn);
                ajaxRequestTarget.add(inboxBtn);
                ajaxRequestTarget.add(systemBtn);
                ajaxRequestTarget.add(allBtn);
            }
        };
        systemBtn.setOutputMarkupId(true);
        add(systemBtn);
        allBtn = new AjaxLink<Object>("allBtn") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                messagesView.setList(user.getMessages());
                blue(outboxBtn);
                blue(inboxBtn);
                blue(systemBtn);
                green(allBtn);
                currentBox=Box.all;
                ajaxRequestTarget.add(form);
                ajaxRequestTarget.add(outboxBtn);
                ajaxRequestTarget.add(inboxBtn);
                ajaxRequestTarget.add(systemBtn);
                ajaxRequestTarget.add(allBtn);
            }
        } ;
        allBtn.setOutputMarkupId(true);
        add(allBtn);
        if(currentBox==Box.system){
            blue(allBtn);
            green(systemBtn);
            blue(inboxBtn);
            blue(outboxBtn);
        } else if(currentBox==Box.inbox){
            blue(allBtn);
            blue(systemBtn);
            green(inboxBtn);
            blue(outboxBtn);
        }
	}

    private void blue(AjaxLink btn){
        btn.add(new AttributeModifier("class", "btn btn-small btn-primary"));
    }

    private void green(AjaxLink btn){
        btn.add(new AttributeModifier("class", "btn btn-small btn-success"));
    }

    private enum Box{
        inbox,
        outbox,
        system,
        all
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptReferenceHeaderItem.forReference(MYPAGE_JS));
    }
}
