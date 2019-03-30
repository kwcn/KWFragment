package com.example.gw00175646.kwfragment.fragment;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public interface IMenu {
    void onCreateOptionsMenu(Menu menu, MenuInflater inflater);

    void onPrepareOptionsMenu(Menu menu);

    boolean onOptionsItemSelected(MenuItem item);
}
