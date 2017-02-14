package net.cachapa.expandablelayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import net.cachapa.expandablelayout.util.FastOutSlowInInterpolator;

public class ExpandableLayout extends FrameLayout {
    private static final int IDLE = 0;
    private static final int EXPANDING = 1;
    private static final int COLLAPSING = 2;

    public static final String KEY_SUPER_STATE = "super_state";
    public static final String KEY_EXPANSION = "expansion";

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final int DEFAULT_DURATION = 300;

    private int duration = DEFAULT_DURATION;
    private float parallax;
    private float expansion;
    private int orientation;
    private int state = IDLE;

    private Interpolator interpolator = new FastOutSlowInInterpolator();
    private ValueAnimator animator;

    private OnExpansionUpdateListener listener;

    public ExpandableLayout(Context context) {
        this(context, null);
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
            duration = a.getInt(R.styleable.ExpandableLayout_el_duration, DEFAULT_DURATION);
            expansion = a.getBoolean(R.styleable.ExpandableLayout_el_expanded, false) ? 1 : 0;
            orientation = a.getInt(R.styleable.ExpandableLayout_android_orientation, VERTICAL);
            float parallax = a.getBoolean(R.styleable.ExpandableLayout_el_translate_children, true) ? 1 : 0;
            parallax = a.getFloat(R.styleable.ExpandableLayout_el_parallax, parallax);
            a.recycle();

            setParallax(parallax);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();

        expansion = isExpanded() ? 1 : 0;

        bundle.putFloat(KEY_EXPANSION, expansion);
        bundle.putParcelable(KEY_SUPER_STATE, superState);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        expansion = bundle.getFloat(KEY_EXPANSION);
        Parcelable superState = bundle.getParcelable(KEY_SUPER_STATE);

        super.onRestoreInstanceState(superState);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int size = orientation == LinearLayout.HORIZONTAL ? width : height;

        setVisibility(expansion == 0 && size == 0 ? GONE : VISIBLE);

        int expansionDelta = size - Math.round(size * expansion);
        if (parallax > 0) {
            float parallaxDelta = expansionDelta * parallax;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (orientation == HORIZONTAL) {
                    int direction = -1;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                        direction = 1;
                    }
                    child.setTranslationX(direction * parallaxDelta);
                } else {
                    child.setTranslationY(-parallaxDelta);
                }
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
        if (animator != null) {
            animator.cancel();
        }
        super.onConfigurationChanged(newConfig);
    }

    public boolean isExpanded() {
        return state == EXPANDING || (Math.abs(expansion - 1) < 0.0001f);
    }

    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean animate) {
        if (isExpanded()) {
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

    /**
     * Convenience method - same as calling setExpanded(expanded, true)
     */
    public void setExpanded(boolean expand) {
        setExpanded(expand, true);
    }

    public void setExpanded(boolean expand, boolean animate) {
        if (expand && (state == EXPANDING || expansion == 1)) {
            return;
        }

        if (!expand && (state == COLLAPSING || expansion == 0)) {
            return;
        }

        int targetExpansion = expand ? 1 : 0;
        if (animate) {
            animateSize(targetExpansion);
        } else {
            setExpansion(targetExpansion);
        }
    }

    public int getDuration() {
        return duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getExpansion() {
        return expansion;
    }

    public void setExpansion(float expansion) {
        if (this.expansion == expansion) {
            return;
        }

        setVisibility(expansion == 0 ? GONE : VISIBLE);

        this.expansion = expansion;
        requestLayout();

        if (listener != null) {
            listener.onExpansionUpdate(expansion);
        }
    }

    public float getParallax() {
        return parallax;
    }

    public void setParallax(float parallax) {
        // Make sure parallax is between 0 and 1
        parallax = Math.min(1, Math.max(0, parallax));
        this.parallax = parallax;
    }

    /**
     * @deprecated use {@link #getParallax()}  instead
     */
    @Deprecated
    public boolean getTranslateChildren() {
        return parallax > 0;
    }

    /**
     * @deprecated use {@link #setParallax(float)} instead
     */
    public void setTranslateChildren(boolean translateChildren) {
        parallax = translateChildren ? 1 : 0;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        if (orientation < 0 || orientation > 1) {
            throw new IllegalArgumentException("Orientation must be either 0 (horizontal) or 1 (vertical)");
        }
        this.orientation = orientation;
    }

    public void setOnExpansionUpdateListener(OnExpansionUpdateListener listener) {
        this.listener = listener;
    }

    private void animateSize(final int targetExpansion) {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }

        animator = ValueAnimator.ofFloat(expansion, targetExpansion);
        animator.setInterpolator(interpolator);
        animator.setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setExpansion((float) valueAnimator.getAnimatedValue());
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                state = targetExpansion == 0 ? COLLAPSING : EXPANDING;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                state = IDLE;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                state = IDLE;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animator.start();
    }

    public interface OnExpansionUpdateListener {
        void onExpansionUpdate(float expansionFraction);
    }
}
