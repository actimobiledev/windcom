package com.actiknow.windcom.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actiknow.windcom.R;

public class MainActivity extends AppCompatActivity {
    LinearLayout llMain;
    ImageView ivSave;
  //  int type = 3;

    EditText etQuestionType1;
    TextView tvQuestionType1;

    TextView tvQuestionType2;
    RadioGroup rgMain;

    TextView tvQuestionType3;
    LinearLayout llQuestionOption;

    TextView tvQuestionType4;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        llMain = (LinearLayout) findViewById(R.id.llMain);
        ivSave=(ImageView)findViewById(R.id.ivSave);

    }

    private void initData() {

        //type 1- edit text
        //type 2- radio button
        //type 3- check box
        //type 4- image

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int type=0; type<5;type++) {
            switch (type) {
                case 1:
                    final View addView = layoutInflater.inflate(R.layout.question_type_edit_text, null);
                    etQuestionType1 = (EditText) addView.findViewById(R.id.etQuestionType1);
                    tvQuestionType1 = (TextView) addView.findViewById(R.id.tvQuestionType1);
                    llMain.addView(addView);
                    break;

                case 2:
                    final View addView2 = layoutInflater.inflate(R.layout.question_type_radio_button, null);
                    tvQuestionType2 = (TextView) addView2.findViewById(R.id.tvQuestionType2);
                    rgMain = (RadioGroup) addView2.findViewById(R.id.rgMain);

                    for (int i = 1; i <= 3; i++) {
                        RadioButton rdbtn = new RadioButton(this);
                        rdbtn.setId(i);
                        rdbtn.setText("Radio " + rdbtn.getId());
                        rgMain.addView(rdbtn);
                    }

                    llMain.addView(addView2);
                    break;


                case 3:
                    final View addView3 = layoutInflater.inflate(R.layout.question_type_check_box, null);
                    tvQuestionType3 = (TextView) addView3.findViewById(R.id.tvQuestionType3);
                    llQuestionOption = (LinearLayout) addView3.findViewById(R.id.llQuestionOption);


                    for (int i = 1; i <= 3; i++) {
                        final CheckBox cbOption = new CheckBox(this);
                        cbOption.setId(i);
                        cbOption.setText("Checkbox " + cbOption.getId());
                        llQuestionOption.addView(cbOption);

                        cbOption.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View arg0) {
                                doCheck(cbOption);
                            }
                        });
                    }
                    



                    llMain.addView(addView3);

                    break;


                case 4:
                    final View addView4 = layoutInflater.inflate(R.layout.question_type_image, null);
                    tvQuestionType4 = (TextView) addView4.findViewById(R.id.tvQuestionType4);
                    ivImage = (ImageView) addView4.findViewById(R.id.ivImage);
                    llMain.addView(addView4);

                    break;
            }
        }


    }

    private void initListener() {
        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb=(RadioButton)findViewById(checkedId);
                Log.e("You Selected " , rb.getText().toString());
                //Toast.makeText(getApplicationContext(), rb.getText(), Toast.LENGTH_SHORT).show();
            }
        });


        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {







            }
        });

    }


    protected void doCheck(CheckBox check) {
        try {
            Toast.makeText(this, check.getText(), Toast.LENGTH_SHORT).show();
            for(int i=0;i<llQuestionOption.getChildCount();i++)
            {
                CheckBox checkbox=(CheckBox)llQuestionOption.getChildAt(i);
                checkbox.setChecked(false);
            }
            check.setChecked(true);
            String strCheck2 = check.getText().toString();
            Log.e("strCheck2",strCheck2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
