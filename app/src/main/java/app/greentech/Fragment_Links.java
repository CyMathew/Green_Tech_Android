package app.greentech;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import app.greentech.Links.CustomLinksListAdapter;
import app.greentech.Links.LinksItem;

/**
 * Created by Cyril on 3/3/16.
 */
public class Fragment_Links extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_links, container, false);

        ArrayList image_details = getListData();
        final ListView lv1 = (ListView) v.findViewById(R.id.links_listView);
        lv1.setAdapter(new CustomLinksListAdapter(getActivity().getApplicationContext(), image_details));
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                LinksItem linksData = (LinksItem) lv1.getItemAtPosition(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(linksData.getSubtitle().toString()));
                startActivity(intent);

            }
        });

        return v;
    }

    private ArrayList getListData() {
        ArrayList<LinksItem> results = new ArrayList<LinksItem>();
        String[] linkTitles = getResources().getStringArray(R.array.LinkTitles);
        String[] linkSubtitles = getResources().getStringArray(R.array.LinkSubtitles);
        LinksItem linksData;

        for(int i = 0; i < linkTitles.length; i++)
        {
            linksData = new LinksItem();
            linksData.setTitle(linkTitles[i]);
            linksData.setSubtitle(linkSubtitles[i]);


            if(linkTitles[i].toLowerCase().contains("ttu"))
            {
                linksData.setImage(R.drawable.ttu_logo);
            }
            else
            {
                linksData.setImage(R.drawable.epa_logo);
            }


            results.add(linksData);
        }
        return results;
    }
}
