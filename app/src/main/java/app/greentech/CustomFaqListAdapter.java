package app.greentech;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Custom List Adapter for FAQ Fragment and its ExpandableListView
 * @author Cyril Mathew
 */
public class CustomFaqListAdapter extends BaseExpandableListAdapter {

    /**
     * Application Context
     */
    private Context context;

    /**
     * List of questions
     */
    private List<String> listDataHeader;

    /**
     * List of answers
     */
    private List<String> listDataChild;

    public CustomFaqListAdapter(Context context, List<String> listDataHeader,
                                 List<String> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    /**
     * Gets the child of a certain parent
     * @param groupPosition
     * @param childPosititon
     * @return Child object of a parent
     */
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(groupPosition);
    }

    /**
     * Gets the ID of a child pointed to by groupPosition and childPosition
     * @param groupPosition
     * @param childPosition
     * @return The child's position as a number
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Create the view of the child
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return Fully inflated view of a child
     */
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.faq_child, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.faqChild);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Setup of the entire expandable listview's parents
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return Fully inflated/setup parents
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.faq_parent, null);
        }

        TextView faqParent = (TextView) convertView.findViewById(R.id.faqParent);
        faqParent.setTypeface(null, Typeface.BOLD);
        faqParent.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}