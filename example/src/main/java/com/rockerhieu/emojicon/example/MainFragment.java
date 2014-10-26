package com.rockerhieu.emojicon.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.rockerhieu.emojicon.EmojiKeyboard;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by kuFEAR on 26/10/14.
 */
public class MainFragment extends Fragment {
    private static final String TAG = "EmojiFragment";
    EmojiconEditText mEditEmojicon;
    EmojiconTextView mTxtEmojicon;
    CheckBox mCheckBox;
    EmojiKeyboard emojiKeyboard;
    ImageView mShowEmoji;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, null);
        mEditEmojicon = (EmojiconEditText) view.findViewById(R.id.editEmojicon);
        mTxtEmojicon = (EmojiconTextView) view.findViewById(R.id.txtEmojicon);
        mShowEmoji = (ImageView) view.findViewById(R.id.showEmojiKeyboard);
        mEditEmojicon.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTxtEmojicon.setText(s);
            }
        });
        mCheckBox = (CheckBox) view.findViewById(R.id.use_system_default);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mEditEmojicon.setUseSystemDefault(b);
                mTxtEmojicon.setUseSystemDefault(b);
            }
        });
        emojiKeyboard = new EmojiKeyboard(getActivity(), view);
        mShowEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiKeyboard.showEmoji();
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        emojiKeyboard.dismissEmojiKeyboard();
        super.onPause();
    }
}
