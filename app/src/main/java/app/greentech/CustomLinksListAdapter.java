package app.greentech;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Matias on 8/8/2016.
 */
public class CustomLinksListAdapter extends BaseAdapter {
    private ArrayList<LinksItem> listData;
    private LayoutInflater layoutInflater;

    public CustomLinksListAdapter(Context aContext, ArrayList<LinksItem> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

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

        holder.title.setText(listData.get(position).getTitle());
        holder.subTitle.setText(listData.get(position).getSubtitle());
        holder.image.setImageResource(listData.get(position).getImage());
        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView subTitle;
        ImageView image;
    }
}
