package com.example.gw00175646.kwfragment.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public interface FragmentLifeCycleCallbacks {
    void onFragmentCreated(Fragment fragment, Bundle savedInstanceState);

    void onFragmentViewCreated(Fragment fragment, View view, Bundle savedInstanceState);

    void onFragmentActivityCreated(Fragment fragment, Bundle savedInstanceState);

    void onFragmentStarted(Fragment fragment);

    void onFragmentResumed(Fragment fragment);

    void onFragmentPaused(Fragment fragment);

    void onFragmentStopped(Fragment fragment);

    void onFragmentViewDestroyed(Fragment fragment);

    void onFragmentDestroyed(Fragment fragment);

    void onFragmentSaveInstanceState(Fragment fragment, Bundle outState);

    void setFragmentUserVisibleHint(Fragment fragment, boolean isVisibleToUser);
}
