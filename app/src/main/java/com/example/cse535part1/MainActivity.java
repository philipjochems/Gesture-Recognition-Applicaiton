package com.example.cse535part1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class MainActivity extends AppCompatActivity {

    public boolean nextScreen=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner=findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onStart(){
        super.onStart();
        Spinner mySpinner=findViewById(R.id.spinner);
        mySpinner.setSelection(17);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!nextScreen){
                    nextScreen=true;
                    return;
                }
                Spinner mySpinner=findViewById(R.id.spinner);
                String results=mySpinner.getItemAtPosition(mySpinner.getSelectedItemPosition()).toString();
                if(results.equals("SELECT AN ITEM")){
                    return;
                }
                Intent intent= new Intent(MainActivity.this, PracticeActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putString("value", results);
                myBundle.putInt("pos",mySpinner.getSelectedItemPosition());
                intent.putExtras(myBundle);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });
    }

}