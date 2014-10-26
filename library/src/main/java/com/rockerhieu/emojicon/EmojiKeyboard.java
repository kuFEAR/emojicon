package com.rockerhieu.emojicon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.rockerhieu.emojicon.emoji.Emojicon;


/**
 * Created by kuFEAR on 9/3/14.
 */
public class EmojiKeyboard {

    private static final String TAG = "EmojiKeyboard";
    private static final String PREF_KEY_HEIGHT_KB = "EmojiKbHeight";

    private Context context;
    private int screenHeight = -1;
    private int emojiKbHeight = -1;
    private PopupWindow emojiKeyboardPopup;
    private View view;
    private SharedPreferences preferences;

    public EmojiKeyboard(Context context, View view) {
        if (context instanceof Activity) {
            this.context = context;
            this.view = view;
            preferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);

            //Restore EmojiKeyboard Height
            emojiKbHeight = preferences.getInt(PREF_KEY_HEIGHT_KB, -1);

            //TODO support less then 11 API, and not perfect resizing when switched the keyboard
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    /*
                    * Get root view height
                    * */
                    screenHeight = screenHeight == -1 && bottom > oldBottom
                            ? bottom
                            : screenHeight;

                    /*
                    * Calculate soft keyboard height
                    * */
                    int dHeight = oldBottom - bottom;
                    boolean validHeight = emojiKbHeight == -1 && dHeight > 80 && bottom != oldBottom;

                    /*
                    * Сheck twice because the keyboard may have been switched
                    * */
                    emojiKbHeight = validHeight
                            ? dHeight : emojiKbHeight != (dHeight) && dHeight > 0
                            ? dHeight
                            : emojiKbHeight;

                    /*
                    * Store emoji keyboard height into SharedPreferences
                    * */
                    preferences.edit().putInt(PREF_KEY_HEIGHT_KB, emojiKbHeight).commit();

                    /*
                    * If layout returned to a standard height then dismissing keyboard (OnBackPressed)
                    * */
                    if (screenHeight == bottom) {
                        dismissEmojiKeyboard();
                    }

                    /*
                    * Resize emoji on the go when a user switches between keyboards
                    * */
                    resizeEmoji();
                }
            });
        }
    }


    public void showEmoji() {
        if (emojiKeyboardPopup == null) {
            createEmojiKeyboard();
        }
        if (!isShowed()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    emojiKeyboardPopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                    resizeEmoji();
                }
            }, 10L);

        } else {
            dismissEmojiKeyboard();
        }
    }

    public void createEmojiKeyboard() {
        EmojiView emojiKeyboard = new EmojiView(context, EmojiView.EMOJI_DARK_STYLE, new EmojiView.onEmojiClickListener() {
            public void onBackspace() {
                if (((Activity) context).getWindow().getCurrentFocus() instanceof EditText) {
                    ((Activity) context).getWindow().getCurrentFocus().dispatchKeyEvent(new KeyEvent(0, 67));
                }
            }

            public void onEmojiSelected(Emojicon emojicon) {
                if (((Activity) context).getWindow().getCurrentFocus() instanceof EditText) {
                    EmojiView.input((EditText) ((Activity) context).getWindow().getCurrentFocus(), emojicon);
                }
            }
        });
        emojiKeyboardPopup = new PopupWindow(emojiKeyboard);
        emojiKeyboardPopup.setHeight(View.MeasureSpec.makeMeasureSpec(setEmojiKeyboardHeight(), View.MeasureSpec.EXACTLY));
        emojiKeyboardPopup.setWidth(View.MeasureSpec.makeMeasureSpec(getDisplayDimensions(context).x, View.MeasureSpec.EXACTLY));
        emojiKeyboardPopup.setAnimationStyle(0);
    }

    public void dismissEmojiKeyboard() {
        if (isShowed()) {
            emojiKeyboardPopup.dismiss();
        }
    }

    public boolean isShowed() {
        return emojiKeyboardPopup != null && emojiKeyboardPopup.isShowing();
    }

    /*
    * Emoji set up size
    * */
    public void resizeEmoji() {
        if (isShowed()) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) emojiKeyboardPopup.getContentView().getLayoutParams();
            layoutParams.height = setEmojiKeyboardHeight();
            wm.updateViewLayout(emojiKeyboardPopup.getContentView(), layoutParams);
        }
    }

    public int setEmojiKeyboardHeight() {
        return emojiKbHeight == -1 && emojiKbHeight != screenHeight && emojiKbHeight < 80
                ? (getDisplayDimensions(context).y / 2)
                : emojiKbHeight;
    }

    public Point getDisplayDimensions(Context context) {
        Point size = new Point();
        WindowManager w = ((Activity) context).getWindowManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            w.getDefaultDisplay().getSize(size);
        } else {
            Display d = w.getDefaultDisplay();
            size.x = d.getWidth();
            size.y = d.getHeight();
        }
        return size;
    }
}
