package net.cachapa.expandablelayout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import net.cachapa.expandablelayout.util.FastOutSlowInInterpolator;

import java.util.ArrayList;
import java.util.List;

public class ExpandableLinearLayout extends LinearLayout {

    public static final String KEY_SUPER_STATE = "super_state";
    public static final String KEY_EXPANDED = "expanded";

    private static final int DEFAULT_DURATION = 300;

    private int wms;
    private int hms;

    private List<View> expandableViews;

    private int duration = DEFAULT_DURATION;
    private boolean expanded = false;

    private Interpolator interpolator = new FastOutSlowInInterpolator();
    private AnimatorSet animatorSet;

    private OnExpansionUpdateListener listener;

    public ExpandableLinearLayout(Context context) {
        super(context);
        init(null);
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpandableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
            duration = a.getInt(R.styleable.ExpandableLayout_el_duration, DEFAULT_DURATION);
            expanded = a.getBoolean(R.styleable.ExpandableLayout_el_expanded, false);
            a.recycle();
        }

        expandableViews = new ArrayList<>();
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

        for (View expandableView : expandableViews) {
            expandableView.setVisibility(expanded ? VISIBLE : GONE);
        }

        super.onRestoreInstanceState(superState);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        LayoutParams lp = (LayoutParams) params;
        if (lp.expandable) {
            expandableViews.add(child);
            child.setVisibility(expanded ? VISIBLE : GONE);
        }

        super.addView(child, index, params);
    }

    @Override
    public void removeView(View child) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (lp.expandable) {
            expandableViews.remove(child);
        }

        super.removeView(child);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        wms = widthMeasureSpec;
        hms = heightMeasureSpec;
    }

    @Override
    public LinearLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
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

    @SuppressLint("WrongCall")
    public void expand(boolean animate) {
        if (expanded) {
            return;
        }

        if (animatorSet != null) {
            animatorSet.cancel();
            animatorSet = null;
        }

        expanded = true;

        for (View expandableView : expandableViews) {
            LayoutParams lp = (LayoutParams) expandableView.getLayoutParams();

            // Calculate view's original height
            expandableView.setVisibility(View.VISIBLE);
            lp.width = lp.originalWidth;
            lp.height = lp.originalHeight;
            lp.weight = lp.originalWeight;
            super.onMeasure(wms, hms);
        }

        for (View expandableView : expandableViews) {
            int targetSize = getOrientation() == HORIZONTAL
                    ? expandableView.getMeasuredWidth() : expandableView.getMeasuredHeight();

            if (animate) {
                animateSize(expandableView, targetSize);
            } else {
                setSize(expandableView, targetSize);
            }
        }

        if (animatorSet != null && animate) {
            animatorSet.start();
        }
    }

    public void collapse() {
        collapse(true);
    }

    public void collapse(boolean animate) {
        if (!expanded) {
            return;
        }

        if (animatorSet != null) {
            animatorSet.cancel();
            animatorSet = null;
        }

        expanded = false;

        for (View expandableView : expandableViews) {
            if (animate) {
                animateSize(expandableView, 0);
            } else {
                setSize(expandableView, 0);
            }
        }

        if (animatorSet != null && animate) {
            animatorSet.start();
        }
    }

    public void setOnExpansionUpdateListener(OnExpansionUpdateListener listener) {
        this.listener = listener;
    }

    private void animateSize(final View view, final int targetSize) {
        if (animatorSet == null) {
            animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(interpolator);
            animatorSet.setDuration(duration);
        }

        final LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.weight = 0;

        int currentSize;
        if (getOrientation() == HORIZONTAL) {
            currentSize = view.getWidth();
        } else {
            currentSize = view.getHeight();
        }

        ValueAnimator animator = ValueAnimator.ofInt(currentSize, targetSize);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (getOrientation() == HORIZONTAL) {
                    view.getLayoutParams().width = (Integer) valueAnimator.getAnimatedValue();
                } else {
                    view.getLayoutParams().height = (Integer) valueAnimator.getAnimatedValue();
                }
                view.requestLayout();

                if (listener != null) {
                    float fraction = targetSize == 0 ? 1 - valueAnimator.getAnimatedFraction() : valueAnimator.getAnimatedFraction();
                    listener.onExpansionUpdate(fraction);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (targetSize == 0) {
                    view.setVisibility(GONE);
                } else {
                    lp.width = lp.originalWidth;
                    lp.height = lp.originalHeight;
                    lp.weight = lp.originalWeight;
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animatorSet.playTogether(animator);
    }

    private void setSize(View view, int targetSize) {
        LayoutParams lp = (LayoutParams) view.getLayoutParams();

        if (targetSize == 0) {
            view.setVisibility(GONE);
        } else {
            lp.width = lp.originalWidth;
            lp.height = lp.originalHeight;
            lp.weight = lp.originalWeight;

            view.requestLayout();
        }

        if (listener != null) {
            listener.onExpansionUpdate(targetSize == 0 ? 0f : 1f);
        }
    }


    public static class LayoutParams extends LinearLayout.LayoutParams {
        private final boolean expandable;
        private final int originalWidth;
        private final int originalHeight;
        private final float originalWeight;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
            expandable = a.getBoolean(R.styleable.ExpandableLayout_layout_expandable, false);
            originalWidth = this.width;
            originalHeight = this.height;
            originalWeight = this.weight;
            a.recycle();
        }
    }

    public interface OnExpansionUpdateListener {
        void onExpansionUpdate(float expansionFraction);
    }
}
