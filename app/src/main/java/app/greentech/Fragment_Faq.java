package app.greentech;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
 * Fragment containing Frequently Asked Questions regarding recycling
 * @author Cyril Mathew
 */
public class Fragment_Faq extends Fragment
{
    /**
     * List of all Frequently Asked Questions and Answers to questions
     */
    private ArrayList<FAQItem> faqList;

    /**
     * Custom Adapter to fill the expandable list View used to view FAQs
     */
    private CustomFaqListAdapter expAdapter;

    /**
     * The ExpandableListView used to display all FAQ and Answers
     */
    private ExpandableListView expandList;

    /**
     * List containing all the questions
     */
    ArrayList<String> parentList;

    /**
     * List containing all the answers
     */
    ArrayList<String> childList;

    /**
     * Private class made to store the question and item as one single object
     * Set as an inner class to FAQ Fragment since only the fragment refers to it
     */
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

        //Load data
        loadArray();

        setListView();
        expAdapter = new CustomFaqListAdapter(getActivity(), parentList, childList);
        expandList.setAdapter(expAdapter);

        return v;
    }

    /**
     * Load the JSON file that contains all the questions and answers as literal strings
     * @return JSON file returned as a single string
     */
    private String loadJSON() {
        String json;
        try
        {
            //Get the raw file
            InputStream is = getResources().openRawResource(R.raw.faq_json);

            //Check if it's accessible
            int size = is.available();
            byte[] buffer = new byte[size];

            //Read the file
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

    /**
     * Load the ArrayList with all of the parsed JSON data
     */
    private void loadArray()
    {
        try
        {
            //Setup JSON string for parsing
            JSONObject obj = new JSONObject(loadJSON());
            JSONArray jsonArray = obj.getJSONArray("faq");
            faqList = new ArrayList<FAQItem>();
            FAQItem item;

            //Parse entire JSON and add data from JSON to ArrayList
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject interJObj = jsonArray.getJSONObject(i);
                String question = interJObj.getString("question");
                String answer = interJObj.getString("answer");

                //Adding values to the arraylist
                item = new FAQItem();
                item.setQuestion(question);
                item.setAnswer(answer);

                faqList.add(item);
            }
        }
        catch (JSONException e)
        {
            Log.e("JSON Error", "Error parsing FAQ JSON");
        }
    }

    /**
     * Take values from ArrayList and put them in the ListView
     */
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