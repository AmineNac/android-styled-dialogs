package eu.inmite.android.lib.dialogs;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class EditDialogFragment extends BaseDialogFragment {
    private final static String      ARG_TITLE           = "title";
    private final static String      ARG_POSITIVE_BUTTON = "positive_button";
    private final static String      ARG_NEGATIVE_BUTTON = "negative_button";
    private final static String      ARG_HINT            = "hint";
    private              FrameLayout mContainer          = null;
    private              EditText    mEditText           = null;

    public static SimpleEditDialogBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new SimpleEditDialogBuilder(context, fragmentManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null) {
            throw new IllegalArgumentException(
                    "use SimpleEditDialogBuilder to construct this dialog");
        }
    }

    public static class SimpleEditDialogBuilder extends BaseDialogBuilder<SimpleEditDialogBuilder> {
        private String       mTitle              = null;
        private String       mHint               = null;
        private String       mPositiveButtonText = null;
        private String       mNegativeButtonText = null;

        public SimpleEditDialogBuilder(Context context, FragmentManager fragmentManager) {
            super(context, fragmentManager, EditDialogFragment.class);
        }

        @Override
        protected SimpleEditDialogBuilder self() {
            return this;
        }

        private Resources getResources() {
            return mContext.getResources();
        }

        public SimpleEditDialogBuilder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public SimpleEditDialogBuilder setTitle(@StringRes int titleResID) {
            mTitle = getResources().getString(titleResID);
            return this;
        }

        public SimpleEditDialogBuilder setHint(String hint) {
            mHint = hint;
            return this;
        }

        public SimpleEditDialogBuilder setHint(@StringRes int hintResID) {
            mHint = getResources().getString(hintResID);
            return this;
        }

        public SimpleEditDialogBuilder setPositiveButtonText(String text) {
            mPositiveButtonText = text;
            return this;
        }

        public SimpleEditDialogBuilder setPositiveButtonText(@StringRes int textResourceId) {
            mPositiveButtonText = mContext.getString(textResourceId);
            return this;
        }

        public SimpleEditDialogBuilder setNegativeButtonText(String text) {
            mNegativeButtonText = text;
            return this;
        }

        public SimpleEditDialogBuilder setNegativeButtonText(@StringRes int textResourceId) {
            mNegativeButtonText = getResources().getString(textResourceId);
            return this;
        }

        @Override
        public EditDialogFragment show() {
            return (EditDialogFragment) super.show();
        }

        @Override
        protected Bundle prepareArguments() {

            Bundle args = new Bundle();
            args.putString(ARG_TITLE, mTitle);
            args.putString(ARG_POSITIVE_BUTTON, mPositiveButtonText);
            args.putString(ARG_NEGATIVE_BUTTON, mNegativeButtonText);
            args.putString(ARG_HINT, mHint);

            return args;
        }
    }

    @Override
    protected Builder build(Builder builder) {
        builder = super.build(builder);

        final String title = getTitle();
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        if (!TextUtils.isEmpty(getPositiveButtonText())) {
            builder.setPositiveButton(getPositiveButtonText(), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IEditDialogListener listener = getDialogListener();
                    if (listener != null) {
                        listener.onTextEntered(mEditText.getText().toString());
                    }
                    dismiss();
                }
            });
        }

        if (!TextUtils.isEmpty(getNegativeButtonText())) {
            builder.setNegativeButton(getNegativeButtonText(), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

        mContainer = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_part_edit, null);
        mEditText = (EditText) mContainer.findViewById(R.id.sdl__editext);
        mEditText.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        mEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EditorInfo.IME_ACTION_DONE == actionId) {
                    IEditDialogListener listener = getDialogListener();
                    if (listener != null) {
                        listener.onTextEntered(mEditText.getText().toString());
                    }
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        String hint = getHint();
        if (!TextUtils.isEmpty(hint)) {
            mEditText.setHint(hint);
        }

        builder.setView(mContainer);

        return builder;
    }

    private IEditDialogListener getDialogListener() {
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            if (targetFragment instanceof IEditDialogListener) {
                return (IEditDialogListener) targetFragment;
            }
        } else {
            if (getActivity() instanceof IEditDialogListener) {
                return (IEditDialogListener) getActivity();
            }
        }
        return null;
    }

    private String getTitle() {
        return getArguments().getString(ARG_TITLE);
    }

    private String getHint() {
        return getArguments().getString(ARG_HINT);
    }

    private String getPositiveButtonText() {
        return getArguments().getString(ARG_POSITIVE_BUTTON);
    }

    private String getNegativeButtonText() {
        return getArguments().getString(ARG_NEGATIVE_BUTTON);
    }

}
