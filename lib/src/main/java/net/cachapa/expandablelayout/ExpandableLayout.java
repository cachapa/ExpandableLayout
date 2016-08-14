package net.cachapa.expandablelayout;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import net.cachapa.expandablelayout.util.FastOutSlowInInterpolator;

public class ExpandableLayout extends FrameLayout {
    public static final String KEY_SUPER_STATE = "super_state";
    public static final String KEY_EXPANDED = "expanded";

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    private static final int DEFAULT_DURATION = 300;

    private int duration = DEFAULT_DURATION;
    private boolean expanded;
    private float expansion;
    private int orientation;

    private Interpolator interpolator = new FastOutSlowInInterpolator();
    private ValueAnimator animatorSet;

    private OnExpansionUpdateListener listener;

    public ExpandableLayout(Context context) {
        super(context);
        init(null);
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
            duration = a.getInt(R.styleable.ExpandableLayout_el_duration, DEFAULT_DURATION);
            expanded = a.getBoolean(R.styleable.ExpandableLayout_el_expanded, false);
            orientation = a.getInt(R.styleable.ExpandableLayout_android_orientation, VERTICAL);
            a.recycle();
        }

        expansion = expanded ? 1 : 0;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();

        bundle.putBoolean(KEY_EXPANDED, expanded);
        bundle.putParcelable(KEY_SUPER_STATE, superState);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        expanded = bundle.getBoolean(KEY_EXPANDED);
        Parcelable superState = bundle.getParcelable(KEY_SUPER_STATE);

        expansion = expanded ? 1 : 0;

        super.onRestoreInstanceState(superState);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int size = orientation == LinearLayout.HORIZONTAL ? width : height;

        setVisibility(!expanded && size == 0 ? GONE : VISIBLE);

        int expansionDelta = (int) (size - size * expansion);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (orientation == HORIZONTAL) {
                child.setTranslationX(-expansionDelta);
            } else {
                child.setTranslationY(-expansionDelta);
            }
        }

        if (orientation == HORIZONTAL) {
            setMeasuredDimension(width - expansionDelta, height);
        } else {
            setMeasuredDimension(width, height - expansionDelta);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        super.onConfigurationChanged(newConfig);
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean animate) {
        if (expanded) {
            collapse(animate);
        } else {
            expand(animate);
        }
    }

    public void expand() {
        expand(true);
    }

    public void expand(boolean animate) {
        setExpanded(true, animate);
    }

    public void collapse() {
        collapse(true);
    }

    public void collapse(boolean animate) {
        setExpanded(false, animate);
    }

    public void setOnExpansionUpdateListener(OnExpansionUpdateListener listener) {
        this.listener = listener;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private void setExpanded(boolean expand, boolean animate) {
        if (expand == expanded) {
            return;
        }

        if (expand) setVisibility(VISIBLE);

        expanded = expand;

        int targetExpansion = expand ? 1 : 0;
        if (animate) {
            animateSize(targetExpansion);
        } else {
            expansion = targetExpansion;
            requestLayout();
        }
    }

    private void animateSize(int targetExpansion) {
        if (animatorSet != null) {
            animatorSet.cancel();
            animatorSet = null;
        }

        animatorSet = ValueAnimator.ofFloat(expansion, targetExpansion);
        animatorSet.setInterpolator(interpolator);
        animatorSet.setDuration(duration);

        animatorSet.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                expansion = (float) valueAnimator.getAnimatedValue();
                requestLayout();

                if (listener != null) {
                    listener.onExpansionUpdate(expansion);
                }
            }
        });

        animatorSet.start();
    }

    public interface OnExpansionUpdateListener {
        void onExpansionUpdate(float expansionFraction);
    }
}
