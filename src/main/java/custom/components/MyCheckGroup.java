package custom.components;

import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: comsystogmbh
 * Date: 7/20/13
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyCheckGroup<T> extends CheckBoxMultipleChoice {

    private int ROW_NUM=3;
    public MyCheckGroup(String id) {
        super(id);
    }

    public MyCheckGroup(String id, Model model, List<T> types) {
        super(id,model,types);
    }

    public void setROW_NUM(int ROW_NUM) {
        this.ROW_NUM = ROW_NUM;
    }

    public int getROW_NUM() {
        return ROW_NUM;
    }

    @Override
    protected void appendOptionHtml(AppendingStringBuffer buffer, Object choice, int index, String selected) {

        if(index==0)
            buffer.append("<table><tr>");
        else if(index%ROW_NUM==0)
            buffer.append("</tr><tr>");
        buffer.append("<td>");

        Object displayValue = getChoiceRenderer().getDisplayValue(choice);
        Class<?> objectClass = displayValue == null ? null : displayValue.getClass();
        // Get label for choice
        String label = "";
        if (objectClass != null && objectClass != String.class)
        {
            IConverter converter = getConverter(objectClass);
            label = converter.convertToString(displayValue, getLocale());
        }
        else if (displayValue != null)
        {
            label = displayValue.toString();
        }

        // If there is a display value for the choice, then we know that the
        // choice is automatic in some way. If label is /null/ then we know
        // that the choice is a manually created checkbox tag at some random
        // location in the page markup!
        if (label != null)
        {
            // Append option suffix
            buffer.append(getPrefix());

            String id = getChoiceRenderer().getIdValue(choice, index);
            final String idAttr = getCheckBoxMarkupId(id);

            // Add checkbox element
            buffer.append("<table><tr><td>");
            buffer.append("<input name=\"");
            buffer.append(getInputName());
            buffer.append("\"");
            buffer.append(" type=\"checkbox\"");
            if (isSelected(choice, index, selected))
            {
                buffer.append(" checked=\"checked\"");
            }
            if (isDisabled(choice, index, selected))
            {
                buffer.append(" disabled=\"disabled\"");
            }
            buffer.append(" value=\"");
            buffer.append(id);
            buffer.append("\" id=\"");
            buffer.append(idAttr);
            buffer.append("\"/>");
            buffer.append("</td><td>");

            // Add label for checkbox
            String display = label;
            if (localizeDisplayValues())
            {
                display = getLocalizer().getString(label, this, label);
            }

            final CharSequence escaped = (getEscapeModelStrings() ? Strings.escapeMarkup(display)
                    : display);

            buffer.append("<label for=\"");
            buffer.append(idAttr);
            buffer.append("\">").append(escaped).append("</label>");

            // Append option suffix
            buffer.append("</td></tr></table></td>");
            String[] arr = this.getInputAsArray();
            int size=this.getChoices().size();
            if(index==size-1)
                buffer.append("</tr></table>");
        }
    }
}
