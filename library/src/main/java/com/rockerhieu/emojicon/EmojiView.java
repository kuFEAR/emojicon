package com.rockerhieu.emojicon;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.rockerhieu.emojicon.emoji.Emojicon;
import com.rockerhieu.emojicon.emoji.Nature;
import com.rockerhieu.emojicon.emoji.Objects;
import com.rockerhieu.emojicon.emoji.People;
import com.rockerhieu.emojicon.emoji.Places;
import com.rockerhieu.emojicon.emoji.Symbols;

import java.util.ArrayList;


/**
 * Created by kuFEAR on 7/30/14.
 */
public class EmojiView extends LinearLayout implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {
    private onEmojiClickListener onEmojiClickListener;
    public static final int EMOJI_DARK_STYLE = 0;
    public static final int EMOJI_LIGHT_STYLE = 1;
    private static final int[] icons = {
            R.drawable.ic_emoji_people_light,
            R.drawable.ic_emoji_nature_light,
            R.drawable.ic_emoji_objects_light,
            R.drawable.ic_emoji_places_light,
            R.drawable.ic_emoji_symbols_light};

    private static final Emojicon[][] emojiData = {
            People.DATA,
            Nature.DATA,
            Objects.DATA,
            Places.DATA,
            Symbols.DATA
    };

    private ArrayList<GridView> views = new ArrayList<GridView>();

    // Views
    private ViewPager emojisPager;
    private TabHost tabs;

    public EmojiView(Context context, int style, onEmojiClickListener onEmojiClickListener) {
        super(context);
        this.onEmojiClickListener = onEmojiClickListener;
        setup(context, style);
    }

    public EmojiView(Context context, int style, AttributeSet attrs, onEmojiClickListener onEmojiClickListener) {
        super(context, attrs);
        this.onEmojiClickListener = onEmojiClickListener;
        setup(context, style);
    }

    /* SETUP EMOJI VIEW */
    private void setup(Context context, int style) {
        // Set Views and viewParams
        setOrientation(VERTICAL);
        switch (style) {
            case EMOJI_DARK_STYLE:
                // Light emoji background
                setBackgroundDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{-14145496, -16777216}));
                break;
            case EMOJI_LIGHT_STYLE:
                //Dark emoji background
                setBackgroundDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xffffffff, 0xffffffff}));
                break;
        }


        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.emojicons, this, true);
        emojisPager = (ViewPager) findViewById(R.id.emojis_pager);
        tabs = (TabHost) view.findViewById(android.R.id.tabhost);

        if (isInEditMode()) return;

        // Set Emoji pages
        for (Emojicon[] emojicons : emojiData) {
            View gridViewItem = inflater.inflate(R.layout.emojicon_grid, this, false);
            GridView gridView = (GridView) gridViewItem.findViewById(R.id.Emoji_GridView);
            gridView.setAdapter(new EmojiAdapter(context, emojicons, onEmojiClickListener));
            views.add(gridView);
        }

        // Create tabs in tabhost
        createTabs();

        emojisPager.setAdapter(new EmojisPagerAdapter());
        emojisPager.setOnPageChangeListener(this);

        // Backspace onClickListener
        view.findViewById(R.id.emoji_backspace).setOnTouchListener(new RepeatListener(1000, 50, new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmojiClickListener.onBackspace();
            }
        }));
    }

    private void createTabs() {
        tabs.setup();
        TabHost.TabSpec spec;
        for (int icon : icons) {
            spec = tabs.newTabSpec(String.valueOf(icon));
            spec.setIndicator("", getResources().getDrawable(icon));
            spec.setContent(R.id.tab1);
            tabs.addTab(spec);
        }
        tabs.setOnTabChangedListener(this);
    }

    /**
     * Input method for add emoji to EditText, should be call by onEmojiSelected(Emojicon)
     */
    public static void input(EditText editText, Emojicon emojicon) {

        if (editText == null || emojicon == null) {
            return;
        }

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < 0) {
            editText.append(emojicon.getEmoji());
        } else {
            editText.getText().replace(
                    Math.min(start, end),
                    Math.max(start, end),
                    emojicon.getEmoji(),
                    0,
                    emojicon.getEmoji().length()
            );
        }

    }

    public void setOnEmojiClickListener(onEmojiClickListener paramOnEmojiClickListener) {
        this.onEmojiClickListener = paramOnEmojiClickListener;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        if (i != tabs.getCurrentTab())
            tabs.setCurrentTab(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onTabChanged(String tabId) {
        if (emojisPager.getCurrentItem() != tabs.getCurrentTab())
            emojisPager.setCurrentItem(tabs.getCurrentTab());
    }

    private class EmojisPagerAdapter extends PagerAdapter {


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View localObject = views.get(position);
            container.removeView(localObject);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        public EmojisPagerAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return icons.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View localObject = views.get(position);
            container.addView(localObject);
            return localObject;
        }
    }

    /**
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     * <p/>
     * <p>Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     */
    public static class RepeatListener implements View.OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final View.OnClickListener clickListener;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }
                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                clickListener.onClick(downView);
            }
        };

        private View downView;

        /**
         * @param initialInterval The interval before first click event
         * @param normalInterval  The interval before second and subsequent click
         *                        events
         * @param clickListener   The OnClickListener, that will be called
         *                        periodically
         */
        public RepeatListener(int initialInterval, int normalInterval,
                              View.OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(
                            handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
            }
            return false;
        }
    }

    public static abstract interface onEmojiClickListener {
        public abstract void onBackspace();

        public abstract void onEmojiSelected(Emojicon emojicon);
    }

}