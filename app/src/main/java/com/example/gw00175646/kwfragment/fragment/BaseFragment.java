package com.example.gw00175646.kwfragment.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.gw00175646.kwfragment.BuildConfig;

import java.util.HashSet;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.gw00175646.kwfragment.fragment.DefaultConstants.UNDEFINED;

public abstract class BaseFragment extends Fragment {

    private static final boolean DEBUG = false;

    protected String mBaseTag = BaseFragment.class.getSimpleName();

    protected boolean mLifeCycleLogEnabled;

    protected IMenu mMenu;

    protected IMenu mContextMenu;

    private final LifeCycleCallbacksManager mLifeCycleCallbacksManager =
            new LifeCycleCallbacksManager();

    private Unbinder mUnbinder;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag, this + " onSaveInstanceState outState : " + outState);
        }
        mLifeCycleCallbacksManager.onFragmentSaveInstanceState(this, outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag, this + " onAttach() - context: " + (context != null));
        }
        mLifeCycleCallbacksManager.reset();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity a = getActivity();
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag,
                    this + " onCreate() - savedInstanceState: " + (savedInstanceState != null));
        }
        mLifeCycleCallbacksManager.onFragmentCreated(this, savedInstanceState);
    }

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mLifeCycleLogEnabled) {
            Log.d(mBaseTag, this + " onCreateView()");
        }
        return inflater.inflate(getLayoutRes(), container, false);
    }

    protected abstract int getLayoutRes();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag, this + " onViewCreated view " + view + " savedInstanceState " +
                    savedInstanceState);
        }
        mLifeCycleCallbacksManager.onFragmentViewCreated(this, view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag,
                    this + " onActivityCreated() - savedInstanceState: " + savedInstanceState);
        }
        mLifeCycleCallbacksManager.onFragmentActivityCreated(this, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag, this + " onStart()");
        }
        mLifeCycleCallbacksManager.onFragmentStarted(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag, this + " onResume()");
        }
        mLifeCycleCallbacksManager.onFragmentResumed(this);
    }

    @Override
    public void onPause() {
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag, this + " onPause()");
        }
        mLifeCycleCallbacksManager.onFragmentPaused(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag, this + " onStop()");
        }
        mLifeCycleCallbacksManager.onFragmentStopped(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag, this + " onDestroyView()");
        }
        mLifeCycleCallbacksManager.onFragmentViewDestroyed(this);
        super.onDestroyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag, this + " onHiddenChanged() " + hidden);
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag, this + " onDestroy()");
        }
        mLifeCycleCallbacksManager.onFragmentDestroyed(this);
        mUnbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag, this + " onDetach()");
        }
        super.onDetach();
    }


    @CallSuper
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        if (DEBUG) {
            iLog.d(mBaseTag, this + " onCreateContextMenu View : " + v + " menu : " + menu);
        }
        if (mContextMenu != null) {
            mContextMenu.onCreateOptionsMenu(menu, getActivity().getMenuInflater());
            mContextMenu.onPrepareOptionsMenu(menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (DEBUG) {
            iLog.d(mBaseTag, this + " onContextItemSelected item : " + item);
        }
        boolean handled = false;
        if (mContextMenu != null && isResumed() && getUserVisibleHint()) {
            handled = mContextMenu.onOptionsItemSelected(item);
        }
        if (DEBUG) {
            iLog.d(mBaseTag, this + " onContextItemSelected() - item id: " + item.getItemId() +
                    " handled: " + handled);
        }
        return handled;
    }

    @CallSuper
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (DEBUG) {
            iLog.d(mBaseTag, this + " onCreateOptionsMenu menu : " + menu);
        }
        if (mMenu != null) {
            mMenu.onCreateOptionsMenu(menu, inflater);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (DEBUG) {
            iLog.d(mBaseTag, this + " onPrepareOptionsMenu menu : " + menu);
        }
        if (mMenu != null) {
            mMenu.onPrepareOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (DEBUG) {
            iLog.d(mBaseTag, this + " onOptionsItemSelected item : " + item);
        }
        boolean handled = false;
        if (mMenu != null && isResumed()) {
            handled = mMenu.onOptionsItemSelected(item);
        }
        if (DEBUG) {
            iLog.d(mBaseTag, this + " onOptionsItemSelected() - item id: " + item.getItemId() +
                    " handled: " + handled);
        }
        return handled;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (mLifeCycleLogEnabled) {
            iLog.d(mBaseTag,
                    this + " setUserVisibleHint isVisibleToUser : " + isVisibleToUser);
        }
        mLifeCycleCallbacksManager.setFragmentUserVisibleHint(this, isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }

    private String toString;

    /**
     * Fragment toString implementation takes too long time, cache results since we actively using
     * toString for debugging
     */
    @Override
    public String toString() {
        if (toString == null) {
            toString = super.toString();
        }
        return toString;
    }

    public final void addFragmentLifeCycleCallbacks(FragmentLifeCycleCallbacks callbacks) {
        mLifeCycleCallbacksManager.addCallbacks(this, callbacks);
    }

    private static class LifeCycleCallbacksManager implements FragmentLifeCycleCallbacks {

        private final Set<FragmentLifeCycleCallbacks> mLifeCycleCallbacksBeforeViewCreated =
                new HashSet<>();

        private final Set<FragmentLifeCycleCallbacks> mLifeCycleCallbacks = new HashSet<>();

        private Bundle mSavedInstanceState;

        private View mView;

        private static final int ON_CREATED = 0;

        private static final int ON_VIEW_CREATED = 1;

        private static final int ON_ACTIVITY_CREATED = 2;

        private static final int ON_STARTED = 3;

        private static final int ON_RESUMED = 4;

        private static final int ON_PAUSED = 5;

        private static final int ON_STOPPED = 6;

        private static final int ON_VIEW_DESTROYED = 7;

        private static final int ON_DESTROYED = 8;

        private int mLifeCycleState = UNDEFINED;

        void addCallbacks(Fragment fragment, FragmentLifeCycleCallbacks callbacks) {
            switch (mLifeCycleState) {
                case UNDEFINED:
                    mLifeCycleCallbacksBeforeViewCreated.add(callbacks);
                    break;
                case ON_CREATED:
                    mLifeCycleCallbacksBeforeViewCreated.add(callbacks);
                    callbacks.onFragmentCreated(fragment, mSavedInstanceState);
                    break;
                case ON_VIEW_CREATED:
                    mLifeCycleCallbacks.add(callbacks);
                    callbacks.onFragmentViewCreated(fragment, mView, mSavedInstanceState);
                    break;
                case ON_ACTIVITY_CREATED:
                    mLifeCycleCallbacks.add(callbacks);
                    callbacks.onFragmentActivityCreated(fragment, mSavedInstanceState);
                    break;
                case ON_STARTED:
                    // fall through
                case ON_RESUMED:
                    // fall through
                case ON_PAUSED:
                    // fall through
                case ON_STOPPED:
                    // fall through
                case ON_VIEW_DESTROYED:
                    // fall through
                case ON_DESTROYED:
                    // fall through
                    if (BuildConfig.DEBUG) {
                        throw new IllegalStateException(
                                "Cannot addCallbacks() in invalid life cycle state: " +
                                        mLifeCycleState);
                    }
                    break;
                default:
            }
        }

        void reset() {
            mLifeCycleState = UNDEFINED;
        }

        @Override
        public void onFragmentCreated(Fragment fragment, Bundle savedInstanceState) {
            mLifeCycleState = ON_CREATED;
            mSavedInstanceState = savedInstanceState;
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacksBeforeViewCreated) {
                callbacks.onFragmentCreated(fragment, savedInstanceState);
            }
        }

        @Override
        public void onFragmentViewCreated(Fragment fragment, View view, Bundle savedInstanceState) {
            mLifeCycleState = ON_VIEW_CREATED;
            mView = view;
            mSavedInstanceState = savedInstanceState;
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacksBeforeViewCreated) {
                callbacks.onFragmentViewCreated(fragment, view, savedInstanceState);
            }
        }

        @Override
        public void onFragmentActivityCreated(Fragment fragment, Bundle savedInstanceState) {
            mLifeCycleState = ON_ACTIVITY_CREATED;
            mSavedInstanceState = savedInstanceState;
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacks) {
                callbacks.onFragmentActivityCreated(fragment, savedInstanceState);
            }
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacksBeforeViewCreated) {
                callbacks.onFragmentActivityCreated(fragment, savedInstanceState);
            }
        }

        @Override
        public void onFragmentStarted(Fragment fragment) {
            mLifeCycleState = ON_STARTED;
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacks) {
                callbacks.onFragmentStarted(fragment);
            }
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacksBeforeViewCreated) {
                callbacks.onFragmentStarted(fragment);
            }
        }

        @Override
        public void onFragmentResumed(Fragment fragment) {
            mLifeCycleState = ON_RESUMED;
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacks) {
                callbacks.onFragmentResumed(fragment);
            }
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacksBeforeViewCreated) {
                callbacks.onFragmentResumed(fragment);
            }
        }

        @Override
        public void onFragmentPaused(Fragment fragment) {
            mLifeCycleState = ON_PAUSED;
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacks) {
                callbacks.onFragmentPaused(fragment);
            }
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacksBeforeViewCreated) {
                callbacks.onFragmentPaused(fragment);
            }
        }

        @Override
        public void onFragmentStopped(Fragment fragment) {
            mLifeCycleState = ON_STOPPED;
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacks) {
                callbacks.onFragmentStopped(fragment);
            }
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacksBeforeViewCreated) {
                callbacks.onFragmentStopped(fragment);
            }
        }

        @Override
        public void onFragmentViewDestroyed(Fragment fragment) {
            mLifeCycleState = ON_VIEW_DESTROYED;
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacks) {
                callbacks.onFragmentViewDestroyed(fragment);
            }
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacksBeforeViewCreated) {
                callbacks.onFragmentViewDestroyed(fragment);
            }
            mLifeCycleCallbacks.clear();
        }

        @Override
        public void onFragmentDestroyed(Fragment fragment) {
            mLifeCycleState = ON_DESTROYED;
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacksBeforeViewCreated) {
                callbacks.onFragmentDestroyed(fragment);
            }
            mLifeCycleCallbacksBeforeViewCreated.clear();
        }

        @Override
        public void onFragmentSaveInstanceState(Fragment fragment, Bundle outState) {
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacks) {
                callbacks.onFragmentSaveInstanceState(fragment, outState);
            }
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacksBeforeViewCreated) {
                callbacks.onFragmentSaveInstanceState(fragment, outState);
            }
        }

        @Override
        public void setFragmentUserVisibleHint(Fragment fragment, boolean isVisibleToUser) {
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacks) {
                callbacks.setFragmentUserVisibleHint(fragment, isVisibleToUser);
            }
            for (FragmentLifeCycleCallbacks callbacks : mLifeCycleCallbacksBeforeViewCreated) {
                callbacks.setFragmentUserVisibleHint(fragment, isVisibleToUser);
            }
        }
    }
}
