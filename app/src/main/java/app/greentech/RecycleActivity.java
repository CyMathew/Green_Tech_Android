package app.greentech;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Activity that allows user to input a recycle event
 * @author Cyril Mathew
 */
public class RecycleActivity extends AppCompatActivity{

    /**
     * Used by other activities to determine if the activity is active in the foreground.
     */
    public static Boolean active = true;

    /**
     * RecycleActivity's intent value
     */
    private Intent data;

    /**
     * RadioGroup that holds all the recycling options. Used globally to be accessed by the button click
     */
    private RadioGroup radioTypeGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);

        data = new Intent();

        radioTypeGroup = (RadioGroup) findViewById(R.id.type_group);

    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        active = false;
    }

    /**
     * onClick method for the "Add" button found within RecycleActivity
     * @param view
     */
    public void addButtonClick(View view)
    {
        //Determine which radiobutton was selected
        int selectedId = radioTypeGroup.getCheckedRadioButtonId();

        //Get the value from the selected radio button
        RadioButton selectedButton = (RadioButton) findViewById(selectedId);
        String selection = selectedButton.getText().toString();

        //Set the result within the intent and finish the activity
        data.putExtra("Selection", selection);
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
