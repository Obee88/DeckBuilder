package obee.pages.tokens;

import obee.pages.UserMainPage;
import obee.pages.master.MasterPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

@AuthorizeInstantiation("USER")
@SuppressWarnings({ "unchecked", "rawtypes", "serial"})
public class TokensPage extends MasterPage{

    private static final JavaScriptResourceReference MYPAGE_JS = new JavaScriptResourceReference(UserMainPage.class, "UserMainPage.js");

    public TokensPage(PageParameters params, String name) {
        super(params, name);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptReferenceHeaderItem.forReference(MYPAGE_JS));
    }
}

