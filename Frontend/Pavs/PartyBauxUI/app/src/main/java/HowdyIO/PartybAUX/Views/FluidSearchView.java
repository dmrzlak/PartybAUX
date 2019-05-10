package HowdyIO.PartybAUX.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.SearchManager;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import HowdyIO.PartybAUX.Fragments.PartybAUXFragment;
import HowdyIO.PartybAUX.Tools.QuickTools;
import PartyBauxUI.R;

public class FluidSearchView extends RelativeLayout {
    private Toolbar toolbar;
    private Activity activity;
    private Context context;
    protected InputMethodManager imm;

    private View root;
    private RelativeLayout searchMag;
    private RelativeLayout searchClose;
    private ImageView searchMagIcon;
    private ImageView searchCloseIcon;
    private SearchEditText searchField;
    private View background;

    private OnClickListener searchClickListener;
    private OnClickListener givenSearchListener;
    private OnClickListener searchCloseListener;
    private OnClickListener givenCloseListener;
    private SearchView.OnQueryTextListener onQueryTextListener;
    private SearchView.OnSuggestionListener onSuggestionListener;
    private CursorAdapter suggestionsAdapter;

    private int animationTime;
    private int primaryColor;
    private int secondaryColor;
    private float searchMagStartLeft = -1;
    private float searchFieldStartLeft = -1;

    private boolean closeOnFirstBackPressed;
    private boolean animateFirst;
    private boolean searchShowing;
    private boolean keyboardVisible;
    private boolean searchAlwaysVisible;

    public static boolean isDetached = true;


    public FluidSearchView(Context context) {
        super(context);
        this.context = context;
    }

    public FluidSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void init(Toolbar toolbar, Activity activity){
        sharedConst(toolbar, activity, false);
    }

    public void init(Toolbar toolbar, Activity activity, boolean isStatic){
        sharedConst(toolbar, activity, isStatic);
    }

    private void sharedConst(Toolbar toolbar, Activity activity, boolean isStatic){
        this.toolbar = toolbar;
        this.activity = activity;
        this.context = toolbar.getContext();
        //lifecycleOwner.getLifecycle().addObserver(this);
        searchShowing = false;
        animateFirst = false;
        closeOnFirstBackPressed = false;
        searchAlwaysVisible = isStatic;

        //Instantiate field variables
        primaryColor = ContextCompat.getColor(getContext(), R.color.white);
        secondaryColor = ContextCompat.getColor(getContext(), R.color.white);
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        build();
    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    public void connectListener() {
//        build();
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    public void disconnectListener() {
//        detach();
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    public void destroy() {
//        detach();
//    }


    /**
     * Must be called to display the SearchView.
     * <p>
     * This method will inflate the necessary views and instantiate listeners.
     * In order to remove the SearchView, detach() must be called.
     * <p>
     * With the implementation of
     */
    private void build() {
        //if (!FluidSearchView.isDetached) return;
        //If the view is not already inflated, inflate it
        root = inflate(context, R.layout.view_fluid_searchview,null);
        addView(root);
/*        if (parent != null && getParent() != null) ((ViewGroup) parent).addView(root);
        else
            parent = activity.getLayoutInflater().inflate(R.layout.view_fluid_searchview, null, false);*/

        //Find views in layout
        searchMagIcon = root.findViewById(R.id.search_mag);
        searchCloseIcon = root.findViewById(R.id.search_close);
        searchField = root.findViewById(R.id.search_field);
        background = root.findViewById(R.id.background);

        searchMag = root.findViewById(R.id.search_mag_wrapper);
        searchClose = root.findViewById(R.id.search_close_wrapper);

        //SearchEditText needs a delegate of this class to function
        searchField.setFluidSearchView(this);

        //Setup listeners and Views
        setUpSearchField();
        setUpOnSearchClick();
        setUpOnCloseClick();
        setUpDefaultQueryTextListener();
        setUpInputListener();
        setUpDefaultSuggestionListener();


        //Make sure mag icon is the leftmost item in the toolbar options
        //checkMagOffset();

        if(searchAlwaysVisible){
            toolbar.post(new Runnable() {
                @Override
                public void run() {
                    staticAdjustSearch(true);
                }
            });
        }

        show();

        //Tells user the SearchView is built
        FluidSearchView.isDetached = false;
    }

    /**
     * Must be called to remove SearchView from view
     * <p>
     * Recommended in onPause()
     */
    public void detach() {
        if (root == null || FluidSearchView.isDetached) return;
        if (searchShowing) staticAdjustSearch(false);
        ((ViewGroup) root).removeView(root);
        root.requestLayout();
        FluidSearchView.isDetached = true;
    }

    public void setSearchAlwaysVisible(final boolean visible) {
        searchAlwaysVisible = visible;

        toolbar.post(new Runnable() {
            @Override
            public void run() {
                if(root == null || root == null)return;

                if (visible) {
                    staticAdjustSearch(true);
                } else
                    staticAdjustSearch(false);
            }
        });


    }

    public void setVerticalOffset(int offset) {
        if (FluidSearchView.isDetached) return;
        int y = offset / 2;

        background.setY(background.getY() + y);
        searchMag.setY(searchMag.getY() + y);
        searchField.setY(searchField.getY() + y);
        searchClose.setY(searchClose.getY() + y);
    }

    /**
     * Makes sure mag icon is added in the left-most spot on the Toolbar
     */
    private void checkMagOffset() {
        final int endPadding = QuickTools.convertDpToPx(context, 56);

        int actionMenuIndex = 0;
        for (int i = 0; i < toolbar.getChildCount(); i++)
            if (toolbar.getChildAt(i) instanceof ActionMenuView) actionMenuIndex = i;

        if ((toolbar.getChildAt(actionMenuIndex) instanceof ViewGroup)) {
            final ViewGroup actionMenu = (ViewGroup) toolbar.getChildAt(actionMenuIndex);
            actionMenu.post(new Runnable() {
                @Override
                public void run() {
                    if (actionMenu.getChildCount() != 0) {
                        final View firstItem = actionMenu.getChildAt(0); //First item from the left
                        if (firstItem != null) {
                            int[] loc = new int[2];
                            firstItem.getLocationOnScreen(loc);
                            searchMagStartLeft = loc[0] + ((ViewGroup) searchMag.getParent()).getPaddingEnd() - endPadding;
                            if(!searchAlwaysVisible)
                                searchMag.setX((int) searchMagStartLeft);
                        }
                    }
                    searchMag.setVisibility(View.VISIBLE);
                }
            });
        }else{
            searchMag.setVisibility(VISIBLE);
            toolbar.post(new Runnable() {
                @Override
                public void run() {
                    if(!searchAlwaysVisible)
                        searchMag.setX(searchMagStartLeft = toolbar.getWidth() - endPadding);
                }
            });
        }
    }

    /**
     * Changes width of search field to fit on any screen
     * Sets click listener so we can have a guess when the keyboard is open
     */
    private void setUpSearchField() {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                searchField.getLayoutParams().width = toolbar.getWidth() - QuickTools.convertDpToPx(context,128);
            }
        });
        searchField.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) keyboardVisible = true;
            }
        });
    }

    /**
     * Default click listener will open/close SearchView depending on
     * whether or not it is open
     */
    private void setUpOnSearchClick() {
        if (searchClickListener != null) return;

        searchClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (givenCloseListener != null && searchShowing) givenCloseListener.onClick(v);
                animateViews(!searchShowing);
                searchField.setText("");
            }
        };

        searchMag.setOnClickListener(searchClickListener);
    }

    /**
     * Default Close button either clears text or closes Search
     */
    private void setUpOnCloseClick() {
        if (searchCloseListener != null) return;

        searchCloseListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(searchField.getText())) {
                    animateViews(false);
                } else {
                    searchField.setText("");
                    onQueryTextListener.onQueryTextSubmit("");
                }
            }
        };

        searchClose.setOnClickListener(searchCloseListener);
    }

    /**
     * Assigned in case user does not specify one, prevents null crashing
     */
    private void setUpDefaultQueryTextListener() {
        if (onQueryTextListener != null) return;

        onQueryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
    }

    /**
     * Assigned in case user does not specify one, prevents null crashing
     */
    private void setUpDefaultSuggestionListener() {
        if (onSuggestionListener != null) return;

        onSuggestionListener = new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                return false;
            }
        };
    }

    /**
     * For filtering results when user types
     */
    private void setUpInputListener() {
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) onQueryTextListener.onQueryTextChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Setting a new onClick will use the default onClick, then the given one
     */
    public void setOnSearchClickListener(OnClickListener clickListener) {
        if (FluidSearchView.isDetached || searchAlwaysVisible) return;

        givenSearchListener = clickListener;
        if (searchMag == null) return;
        searchMag.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchClickListener.onClick(v);

                if (searchShowing && !animateFirst) givenSearchListener.onClick(v);
            }
        });
    }

    /**
     * Setting a new onClick will use the default onClick, then the given one.
     * The given onClick is only called if the SearchView was actually closed
     */
    public void setOnCloseClickListener(OnClickListener clickListener) {
        if (FluidSearchView.isDetached) return;

        givenCloseListener = clickListener;
        if (searchClose == null) return;
        searchClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hadNoText = TextUtils.isEmpty(searchField.getText());
                searchCloseListener.onClick(v);

                if (hadNoText)
                    givenCloseListener.onClick(v);
            }
        });
    }

    public void setOnQueryTextListener(SearchView.OnQueryTextListener queryTextListener) {
        this.onQueryTextListener = queryTextListener;
    }

    public void setOnSuggestionListener(SearchView.OnSuggestionListener suggestionListener) {
        this.onSuggestionListener = suggestionListener;
    }

    public void setSuggestionsAdapter(CursorAdapter adapter) {
        suggestionsAdapter = adapter;

        searchField.setAdapter(suggestionsAdapter);
        searchField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onSuggestionListener != null) onSuggestionListener.onSuggestionClick(position);
            }
        });
    }

    /**
     * Sets current query in the SearchField to queryText
     *
     * @param queryText - New query
     */
    public void setQuery(String queryText, boolean shouldSearch) {
        if (searchField == null) return;

        searchField.setText(queryText);
        if (shouldSearch) {
            //TODO
        }
    }

    /**
     * Changes the hint in the Search Field
     *
     * @param hint - New hint
     */
    public void setQueryHint(String hint) {
        if (searchField != null)
            searchField.setHint(hint);
    }

    /**
     * Get the current text the hint displays
     *
     * @return String - Hint
     */
    public String getQueryHint() {
        return searchField.getHint().toString();
    }

    /**
     * Changes the theme of the SearchView
     *
     * @param closedColor - Color of the items when closed
     * @param openedColor - Color of the items when open
     */
    public void setColorScheme(int closedColor, int openedColor) {
        primaryColor = closedColor;
        secondaryColor = openedColor;

        searchClose.getBackground().setColorFilter(openedColor, PorterDuff.Mode.SRC_IN);
        background.setBackgroundColor(openedColor);

        if (searchShowing)
            searchMag.getBackground().setColorFilter(openedColor, PorterDuff.Mode.SRC_IN);
        else
            searchMag.getBackground().setColorFilter(closedColor, PorterDuff.Mode.SRC_IN);

    }

    /**
     * Changes color of query hint
     *
     * @param color - New color
     */
    public void setQueryHintColor(int color) {
        searchField.setHintTextColor(color);
    }

    public void setAnimateFirst(boolean animate) {
        this.animateFirst = animate;
    }

    public boolean getAnimateFirst() {
        return animateFirst;
    }


    void launchQuerySearch(int actionKey, String actionMsg, String query) {
        String action = Intent.ACTION_SEARCH;
        //Intent intent = createIntent(action, null, null, query, actionKey, actionMsg);
        //getContext().startActivity(intent);
    }

    private Intent createIntent(String action, Uri data, String extraData, String query, int actionKey, String actionMsg) {
        // Now build the Intent
        Intent intent = new Intent(action);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // We need CLEAR_TOP to avoid reusing an old task that has other activities
        // on top of the one we want. We don't want to do this in in-app search though,
        // as it can be destructive to the activity stack.
        if (data != null) {
            intent.setData(data);
        }
        //intent.putExtra(SearchManager.USER_QUERY, mUserQuery);
        if (query != null) {
            intent.putExtra(SearchManager.QUERY, query);
        }
        if (extraData != null) {
            intent.putExtra(SearchManager.EXTRA_DATA_KEY, extraData);
        }
//        if (mAppSearchData != null) {
//            intent.putExtra(SearchManager.APP_DATA, mAppSearchData);
//        }
        if (actionKey != KeyEvent.KEYCODE_UNKNOWN) {
            intent.putExtra(SearchManager.ACTION_KEY, actionKey);
            intent.putExtra(SearchManager.ACTION_MSG, actionMsg);
        }
        //intent.setComponent(mSearchable.getSearchActivity());
        return intent;
    }

    /**
     * Manually execute the onClose click
     *
     * @param force - Make the SearchView close, even with text in the query
     */
    public void onClose(boolean force) {
        if (force)
            searchField.setText("");

        searchClose.callOnClick();
    }

    public void staticCloseSearch() {
        staticAdjustSearch(false);
    }

    /**
     * The magic of every animation that handles showing this SearchView
     *
     * @param show - Show/Hide
     */
    //This is obnoxiously long
    protected void animateViews(boolean show) {
        if (searchMagStartLeft == -1) searchMagStartLeft = searchMag.getLeft();
        if (searchFieldStartLeft == -1) searchFieldStartLeft = searchField.getX();
        final int magOffset = QuickTools.convertDpToPx(context, 8);
        final int searchOffset = QuickTools.convertDpToPx(context, 78);

        if(searchAlwaysVisible && !show){
            if (imm != null) imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
            keyboardVisible = false;
            searchMag.requestFocus();
            return;
        }

        setMenuItemsEnabled(!show);


        if (show) { //Animate SearchView open
            searchShowing = true;

            //Animate Nav Burger shrink
            final View navBurger = toolbar.getChildAt(0);
            final AnimatorSet shrinkSet = new AnimatorSet();
            ObjectAnimator shrinkX = ObjectAnimator.ofFloat(navBurger, "ScaleX", 1, 0);
            ObjectAnimator shrinkY = ObjectAnimator.ofFloat(navBurger, "ScaleY", 1, 0);
            shrinkSet.playTogether(shrinkX, shrinkY);
            searchClose.setVisibility(View.VISIBLE);
            shrinkSet.setDuration(100).start();
            shrinkSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    navBurger.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    navBurger.setVisibility(View.GONE);
                }
            });

            //Animate Search Mag color change
            final ValueAnimator searchMagColorChange = ValueAnimator.ofObject(new ArgbEvaluator(), primaryColor, secondaryColor);
            searchMagColorChange.setDuration(100);
            searchMagColorChange.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedAmt = (int) animation.getAnimatedValue();
                    searchMagIcon.getBackground().setColorFilter(animatedAmt, PorterDuff.Mode.SRC_IN);
                }
            });
            searchMagColorChange.start();

            //Animate Background in
            background.setBackground(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
            background.setAlpha(0);
            background.setVisibility(VISIBLE);
            ObjectAnimator backgroundFadeIn = ObjectAnimator.ofFloat(background, "alpha", 0f, 1f);
            backgroundFadeIn.setDuration(100);
            backgroundFadeIn.start();

            //Set holding all movement animations
            final AnimatorSet moveViews = new AnimatorSet();

            //Move Mag Icon
            ObjectAnimator magMove = ObjectAnimator.ofFloat(searchMag, "x", searchMag.getX(), magOffset - 20);
            magMove.setInterpolator(new AccelerateInterpolator());

            //Move EditText
            ObjectAnimator textMove = ObjectAnimator.ofFloat(searchField, "x", searchMag.getX() + magOffset, searchOffset - 20);
            textMove.setInterpolator(new AccelerateInterpolator());

            //Fade it in too
            searchField.setAlpha(0);
            searchField.setVisibility(VISIBLE);
            ObjectAnimator textFadeIn = ObjectAnimator.ofFloat(searchField, "alpha", 0f, 1f);
            textFadeIn.setDuration(150);
            textFadeIn.setStartDelay(50);
            textFadeIn.start();

            moveViews.playTogether(magMove, textMove);
            moveViews.setDuration(100);
            moveViews.start();

            //Listener to do things after the animations
            moveViews.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    finishMoving();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    finishMoving();
                }

                private void finishMoving() {
                    //Ease Mag Icon Back
                    ObjectAnimator magMove = ObjectAnimator.ofFloat(searchMag, "x", searchMag.getX(), searchMag.getX() + 20);
                    magMove.setInterpolator(new DecelerateInterpolator());

                    //Ease EditText Back
                    ObjectAnimator textMove = ObjectAnimator.ofFloat(searchField, "x", searchField.getX(), searchField.getX() + 20);
                    textMove.setInterpolator(new DecelerateInterpolator());

                    //I hate nesting this but it needed to be done
                    moveViews.removeAllListeners();
                    moveViews.setDuration(100).playTogether(magMove, textMove);
                    moveViews.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            finish();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            finish();
                        }

                        private void finish() {
                            searchMag.setX(magOffset);
                            searchField.setX(searchOffset);

                            if (animateFirst && searchShowing)
                                givenSearchListener.onClick(searchMag);
                            searchField.requestFocus();
                            if (imm != null)
                                imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT);
                            keyboardVisible = true;
                        }
                    });
                    moveViews.start();

                    searchField.setAlpha(1f);
                    searchField.setVisibility(View.VISIBLE);

                }
            });

            //Animate Close Button grow
            AnimatorSet growSet = new AnimatorSet();
            ObjectAnimator growX = ObjectAnimator.ofFloat(searchClose, "ScaleX", 0, 1);
            ObjectAnimator growY = ObjectAnimator.ofFloat(searchClose, "ScaleY", 0, 1);
            growSet.playTogether(growX, growY);
            searchClose.setScaleX(0);
            searchClose.setScaleY(0);
            searchClose.setVisibility(View.VISIBLE);
            searchClose.setClickable(true);
            growSet.setStartDelay(25);
            growSet.setDuration(100).start();
            growSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    searchClose.setVisibility(VISIBLE);
                    searchClose.setScaleX(1);
                    searchClose.setScaleY(1);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    searchClose.setVisibility(VISIBLE);
                    searchClose.setScaleX(1);
                    searchClose.setScaleY(1);
                }
            });

        } else { //Animate SearchView close
            searchShowing = false;

            //Animate Nav Burger grow
            final View navBurger = toolbar.getChildAt(0);
            AnimatorSet growSet = new AnimatorSet();
            ObjectAnimator growX = ObjectAnimator.ofFloat(navBurger, "ScaleX", 0, 1);
            ObjectAnimator growY = ObjectAnimator.ofFloat(navBurger, "ScaleY", 0, 1);
            growSet.playTogether(growX, growY);
            navBurger.setVisibility(View.VISIBLE);
            growSet.setStartDelay(25);
            growSet.setDuration(100).start();
            growSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    navBurger.setScaleX(1);
                    navBurger.setScaleY(1);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    navBurger.setScaleX(1);
                    navBurger.setScaleY(1);

                }
            });

            //Animate Search Mag color change
            final ValueAnimator searchMagColorChange = ValueAnimator.ofObject(new ArgbEvaluator(), secondaryColor, primaryColor);
            searchMagColorChange.setDuration(100);
            searchMagColorChange.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedAmt = (int) animation.getAnimatedValue();
                    searchMagIcon.getBackground().setColorFilter(animatedAmt, PorterDuff.Mode.SRC_IN);
                }
            });
            searchMagColorChange.start();

            //Animate Background out
            background.setBackground(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
            background.setAlpha(1f);
            background.setVisibility(VISIBLE);
            ObjectAnimator backgroundFadeOut = ObjectAnimator.ofFloat(background, "alpha", 1f, 0f);
            backgroundFadeOut.setStartDelay(50);
            backgroundFadeOut.setDuration(100);
            backgroundFadeOut.start();

            //Set holding all movement animations
            AnimatorSet moveViews = new AnimatorSet();

            //Fade out Search Field
            ObjectAnimator textFadeOut = ObjectAnimator.ofFloat(searchField, "alpha", 1f, 0f);
            textFadeOut.setDuration(150);
            textFadeOut.start();

            //Move Mag Icon
            ObjectAnimator magMove = ObjectAnimator.ofFloat(searchMag, "x", searchMag.getX(), searchMagStartLeft);
            magMove.setInterpolator(new AccelerateInterpolator());

            //Move EditText
            ObjectAnimator textMove = ObjectAnimator.ofFloat(searchField, "x", searchField.getX(), searchMagStartLeft + magOffset);
            textMove.setInterpolator(new AccelerateInterpolator());

            moveViews.playTogether(magMove, textMove);
            moveViews.setDuration(150);
            moveViews.start();

            //Listener to do things after the animations
            moveViews.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    finishMoving();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    finishMoving();
                }

                private void finishMoving() {
                    /*//Ease Mag Icon Back
                    ObjectAnimator magMove = ObjectAnimator.ofFloat(searchMag, "x", searchMag.getX(), searchMagStartLeft);
                    magMove.setInterpolator(new AccelerateInterpolator());

                    //Ease EditText Back
                    ObjectAnimator textMove = ObjectAnimator.ofFloat(searchField, "x", searchField.getX(), searchMagStartLeft + magOffset);
                    textMove.setInterpolator(new AccelerateInterpolator());*/

                    searchField.setVisibility(GONE);
                    if (imm != null) imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                    keyboardVisible = false;
                }
            });

            //Animate Close Button shrink
            AnimatorSet shrinkSet = new AnimatorSet();
            ObjectAnimator shrinkX = ObjectAnimator.ofFloat(searchClose, "ScaleX", 1, 0);
            ObjectAnimator shrinkY = ObjectAnimator.ofFloat(searchClose, "ScaleY", 1, 0);
            shrinkSet.playTogether(shrinkX, shrinkY);
            searchClose.setVisibility(View.VISIBLE);
            searchClose.setClickable(false);
            shrinkSet.setDuration(100).start();
            shrinkSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    searchClose.setVisibility(View.GONE);
                    searchClose.setClickable(false);
                    searchClose.setScaleX(0);
                    searchClose.setScaleY(0);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    searchClose.setVisibility(View.GONE);
                    searchClose.setClickable(false);
                    searchClose.setScaleX(0);
                    searchClose.setScaleY(0);
                }
            });
        }
    }

    public void openSearch(){
        searchMag.callOnClick();
    }

    /**
     * Open/Close search without any animations
     *
     * @param show - Show/Hide
     */
    private void staticAdjustSearch(boolean show) {
        if(FluidSearchView.isDetached)return;
        setMenuItemsEnabled(!show);
        searchShowing = show;
        final int magOffset = (int) context.getResources().getDimension(R.dimen.padding_medium);
        final int searchOffset = QuickTools.convertDpToPx(context, 78);

        if (show) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchField.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_END, 0);
            searchMag.setX(magOffset);
            searchMagIcon.getBackground().setColorFilter(secondaryColor, PorterDuff.Mode.SRC_IN);
            searchField.setVisibility(View.VISIBLE);
            searchField.setX(searchOffset);
            background.setAlpha(1f);
            background.setVisibility(View.VISIBLE);
            searchClose.setVisibility(View.VISIBLE);
            searchClose.setClickable(true);

            if (!searchAlwaysVisible) {
                View navBurger = toolbar.getChildAt(0);
                if (navBurger != null) {
                    navBurger.setVisibility(View.GONE);
                    navBurger.setScaleX(0);
                    navBurger.setScaleY(0);
                }

                searchField.requestFocus();
                if (givenSearchListener != null) givenSearchListener.onClick(searchMag);
            }

            searchShowing = true;
        } else {
            searchMag.setX((int) searchMagStartLeft);
            searchMagIcon.getBackground().setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
            searchField.setVisibility(View.GONE);
            background.setAlpha(0f);
            background.setVisibility(View.GONE);
            searchClose.setVisibility(View.GONE);
            searchClose.setClickable(false);

            View navBurger = toolbar.getChildAt(0);
            if (navBurger != null) {
                navBurger.setVisibility(View.VISIBLE);
                navBurger.setScaleX(1);
                navBurger.setScaleY(1);
            }

            final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
            if (givenCloseListener != null) givenCloseListener.onClick(searchClose);

            searchShowing = false;
        }
    }

    /**
     * Enable/Disable other menu items
     * Used to prevent user from clicking items behind SearchView when it's open
     *
     * @param enabled - Enabled/Disabled
     */
    private void setMenuItemsEnabled(boolean enabled) {
        ActionMenuView menuView = null;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (toolbar.getChildAt(i) instanceof ActionMenuView)
                menuView = (ActionMenuView) toolbar.getChildAt(i);
        }
        if (menuView == null) return;

        Menu menu = menuView.getMenu();
        if (menu == null) return;

        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setEnabled(enabled);
    }

    /**
     * Says whether search is open or not
     *
     * @return - Open/Closed
     */
    public boolean isOpened() {
        return searchShowing;
    }


    public void enableSearch() {
        if (searchMag == null) return;

        searchMag.setAlpha(1f);
        searchMag.setClickable(true);
        searchMag.setEnabled(true);
    }

    public void disableSearch() {
        if (searchMag == null) return;

        searchMag.setAlpha(0.6f);
        searchMag.setClickable(false);
        searchMag.setEnabled(false);
    }

    public boolean requestSearchFocus(){
        if(searchField == null) return false;

        searchField.requestFocus();
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT);
            keyboardVisible = true;
        }

        if(searchField.hasFocus())return true;
        else return false;
    }

    public void clearSearchFocus(){
        if(searchField == null) return;

        searchField.clearFocus();
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
            keyboardVisible = false;
        }

    }


    public boolean getCloseOnFirstBackPressed() {
        return closeOnFirstBackPressed;
    }

    public void setCloseOnFirstBackPressed(boolean close) {
        this.closeOnFirstBackPressed = close;
    }

    public void show() {
        root.setVisibility(View.VISIBLE);
    }

    public void hide() {
        root.setVisibility(View.GONE);
    }


    /**
     * EditText class to handle backpresses correctly without another class consuming them first
     */
    public static class SearchEditText extends AppCompatAutoCompleteTextView {
        private FluidSearchView fluidSearchView;

        public SearchEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void setFluidSearchView(FluidSearchView fluidSearchView) {
            this.fluidSearchView = fluidSearchView;
            setUpSubmitListener();
        }

/*        int curX, curY, lastX, lastY;
        boolean isMove;
        @Override
        public boolean onTouchEvent(MotionEvent ev){
            switch (ev.getAction()){
            isMove = false;
            case MotionEvent.ACTION_DOWN:
                isMove = false;
            case MotionEvent.ACTION_UP:
            if (!isMove || (Xdiff < 10 && Ydiff < 10 ) {

            }
            case MotionEvent.ACTION_MOVE:
                isMove = true;
            }
            return super.onTouchEvent(ev);
        }*/

        @Override
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (fluidSearchView == null) return false;
            if (fluidSearchView.searchAlwaysVisible && !fluidSearchView.keyboardVisible) return false;

            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && fluidSearchView.searchShowing) { //Search open, keyboard back pressed
                if (fluidSearchView.closeOnFirstBackPressed) //If should close on first back press
                    fluidSearchView.onClose(true);
                else {
                    if (fluidSearchView.keyboardVisible) { //If not, hide keyboard if visible
                        if (fluidSearchView.imm != null)
                            fluidSearchView.imm.hideSoftInputFromWindow(fluidSearchView.searchField.getWindowToken(), 0);
                        fluidSearchView.keyboardVisible = false;
                        return true;
                    } else //Second back press should close SearchView regardless
                        fluidSearchView.onClose(true);
                }
                return true;
            }
            return false;
        }

        private void setUpSubmitListener(){
            setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    final boolean isEnterEvent = event != null
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
                    final boolean isEnterUpEvent = isEnterEvent && event.getAction() == KeyEvent.ACTION_UP;
                    final boolean isEnterDownEvent = isEnterEvent && event.getAction() == KeyEvent.ACTION_DOWN;

                    if (actionId == EditorInfo.IME_ACTION_DONE || isEnterUpEvent) {
                        fluidSearchView.onQueryTextListener.onQueryTextSubmit(fluidSearchView.searchField.getText().toString());
                        fluidSearchView.animateViews(false);
                        return true;
                    } else if (isEnterDownEvent)
                        return true;

                    return false;
                }
            });

        }
    }

/*    @Override
    protected void onDetachedFromWindow() {
        if(imageUpdateTimer != null) imageUpdateTimer.stopTimer();
        super.onDetachedFromWindow();
    }*/
}