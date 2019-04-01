package com.example.gw00175646.kwfragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.gw00175646.kwfragment.fragment.iLog;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iLog.setEnableLog(true);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.main_page, new MyFragment())
                .commit();
    }
}
