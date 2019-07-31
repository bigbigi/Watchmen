package com.autofit.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;

//import net.sf.chineseutils.ChineseUtils;

@SuppressWarnings("deprecation")
public class DigitalClock extends android.widget.DigitalClock implements IAutoFit {

    private boolean mEnableAutoFit = true;

    public DigitalClock(Context context) {
        super(context);
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAutoView(context, attrs);
    }

    private void initAutoView(Context context, AttributeSet attrs) {
        this.setTextSize(getTextSize());
        mEnableAutoFit = ScreenParameter.getEnableAutoFit(context, attrs);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }


    @Override
    public boolean getEnabledAutoFit() {
        return mEnableAutoFit;
    }

    @Override
    public void setEnabledAutoFit(boolean autofit) {
        this.mEnableAutoFit = autofit;
    }

    @Override
    public void setTextSize(float size) {
        setTextSize(0, ScreenParameter.getFitSize(this, (int) size));
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    @Override
    public void setCompoundDrawablePadding(int pad) {
        super.setCompoundDrawablePadding(ScreenParameter.getFitSize(this, pad));
    }

    private int padingCount = 0;

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (padingCount == 0) {
            super.setPadding(ScreenParameter.getFitWidth(this, left), ScreenParameter.getFitHeight(this, top),
                    ScreenParameter.getFitWidth(this, right), ScreenParameter.getFitHeight(this, bottom));
            padingCount++;
        } else {
            super.setPadding(left, top, right, bottom);
        }
    }

    private int layoutCount = 0;
    @Override
    public void setLayoutParams(LayoutParams params) {
        if (layoutCount == 0) {
            super.setLayoutParams(ScreenParameter.getRealLayoutParams(this, params));
            layoutCount++;
        } else {
            super.setLayoutParams(params);
        }
    }


    @Override
    public void setMinimumHeight(int minHeight) {
        super.setMinimumHeight(ScreenParameter.getFitHeight(this, minHeight));
    }

    @Override
    public void setMinimumWidth(int minWidth) {
        super.setMinimumWidth(ScreenParameter.getFitWidth(this, minWidth));
    }

    @Override
    public void setTypeface(Typeface tf) {
        Typeface newTf = null;
        if(ScreenParameter.isNeedSetFont){
            newTf = ScreenParameter.getTypeFace(getContext().getApplicationContext());
        }
        if (!ScreenParameter.isFT && newTf != null) {
            super.setTypeface(newTf);
        } else {
            super.setTypeface(tf);
        }
    }

  /*  @Override
    public void setText(CharSequence text, BufferType type) {
        if (ScreenParameter.isFT && text != null && text.length() > 0) {
            super.setText(ChineseUtils.simpToTrad(text.toString()), type);
        } else {
            if (!TextUtils.isEmpty(text) && text.toString().contains("-")) {
                text = text.toString().replaceAll("-", "- ");
            }
            super.setText(text, type);
        }
    }*/
}
