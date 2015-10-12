package io.github.pxsrt.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by George on 2015-06-06.
 */
public class HelpButton extends ImageButton {

    private final String messageBody;
    private final String messageTitle;

    public HelpButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(io.github.pxsrt.R.drawable.ic_help_outline_black_24dp);
        TypedArray array = context.obtainStyledAttributes(attrs, io.github.pxsrt.R.styleable.HelpButton);

        try {
            messageBody = array.getString(io.github.pxsrt.R.styleable.HelpButton_messageBody);
            messageTitle = array.getString(io.github.pxsrt.R.styleable.HelpButton_messageTitle);
        } finally {
            array.recycle();
        }
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getMessageTitle() {
        return messageTitle;
    }
}
