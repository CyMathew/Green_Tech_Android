package app.greentech.Links;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import app.greentech.R;

/**
 * List adapter created to fill in links in the ListView found within Links Fragment
 *
 * @author Cyril Mathew
 */
public class CustomLinksListAdapter extends BaseAdapter
{
    /**
     * ArrayList used to hold all the preliminary data for the links
     */
    private ArrayList<LinksItem> listData;

    /**
     * LayoutInflater used by the automatic inflater for filling data into ListView
     */
    private LayoutInflater layoutInflater;

    public CustomLinksListAdapter(Context aContext, ArrayList<LinksItem> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    /**
     * Gets the count of the elements within the list
     * @return The size of the list
     */
    @Override
    public int getCount() {
        return listData.size();
    }

    /**
     * Retrieves an item from the list
     * @param position
     * @return Returns the item requested
     */
    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    /**
     * Retrieves the ID of the item in question
     * @param position
     * @return Returns the ID
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * Used to setup the view and elements of the Links ListView
     * @param position
     * @param convertView
     * @param parent
     * @return Returns the view after creating/inflating it
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.links_item, null);
            holder = new ViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.links_title);
            holder.subTitle = (TextView) convertView.findViewById(R.id.links_subtitle);
            holder.image = (ImageView) convertView.findViewById(R.id.links_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Set the title of the link
        holder.title.setText(listData.get(position).getTitle());

        //Set the subtitle of the link
        holder.subTitle.setText(listData.get(position).getSubtitle());

        //Set the image of the link
        holder.image.setImageResource(listData.get(position).getImage());
        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView subTitle;
        ImageView image;
    }
}
