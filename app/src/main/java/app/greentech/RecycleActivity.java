package app.greentech;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class RecycleActivity extends AppCompatActivity{

    public static Boolean active = true;
    private Intent data;
    private RadioGroup radioTypeGroup;

    private final static int TYPE_PAPER = 0x1;
    private final static int TYPE_PLASTIC = 0x2;
    private final static int TYPE_ALUMIN = 0x3;
    private final static int TYPE_GLASS = 0x4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);

        data = new Intent();

        radioTypeGroup = (RadioGroup) findViewById(R.id.type_group);

    }

    public void addButtonClick(View view)
    {
        int selectedId = radioTypeGroup.getCheckedRadioButtonId();

        RadioButton selectedButton = (RadioButton) findViewById(selectedId);
        String selection = selectedButton.getText().toString();

        data.putExtra("Selection", selection);
        setResult(Activity.RESULT_OK, data);
        finish();
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
}
