package app.greentech;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Cyril Mathew on 3/3/16.
 */
public class Fragment_Faq extends Fragment
{
    private ArrayList<FAQItem> faqList;
    private CustomFaqListAdapter expAdapter;
    private ExpandableListView expandList;
    ArrayList<String> parentList;
    ArrayList<String> childList;

    private class FAQItem {

        private String question;
        private String answer;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_faqs, container, false);

        expandList = (ExpandableListView) v.findViewById(R.id.faqListView);

        loadArray();
        setListView();
        expAdapter = new CustomFaqListAdapter(getActivity(), parentList, childList);
        expandList.setAdapter(expAdapter);

        return v;
    }

    private String loadJSON() {
        String json;
        try
        {
            InputStream is = getResources().openRawResource(R.raw.faq_json);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void loadArray()
    {
        try
        {
            JSONObject obj = new JSONObject(loadJSON());
            JSONArray jsonArray = obj.getJSONArray("faq");
            faqList = new ArrayList<FAQItem>();
            FAQItem item;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject interJObj = jsonArray.getJSONObject(i);
                String question = interJObj.getString("question");
                String answer = interJObj.getString("answer");

                //Add your values in your `ArrayList` as below:
                item = new FAQItem();
                item.setQuestion(question);
                item.setAnswer(answer);

                faqList.add(item);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void setListView() {
        parentList = new ArrayList<String>();
        childList = new ArrayList<String>();

        for (FAQItem item: faqList)
        {
            parentList.add(item.getQuestion());
            childList.add(item.getAnswer());
        }
    }

}