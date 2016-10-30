package notused;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by alexeyglushkov on 15.10.16.
 */

public class BottomRefreshBehavior extends CoordinatorLayout.Behavior<FrameLayout> {
    public BottomRefreshBehavior() {
    }

    public BottomRefreshBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FrameLayout child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FrameLayout child, View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
