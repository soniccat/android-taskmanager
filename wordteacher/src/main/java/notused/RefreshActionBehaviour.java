package notused;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by alexeyglushkov on 16.10.16.
 */

public class RefreshActionBehaviour extends AppBarLayout.ScrollingViewBehavior {

    public RefreshActionBehaviour() {
    }

    public RefreshActionBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        boolean result = super.layoutDependsOn(parent, child, dependency);
        if (!result /*&& dependency.getId() == R.id.statusView*/) {
            result = true;
        }

        return result;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        boolean result = false;
        boolean isBottomChanged = false;

        /*
        if (dependency.getId() == R.id.statusView) {
            int dy = dependency.getHeight() - (int) dependency.getTranslationY();

            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();

            int expectedHeight = dy;
            if (dy != child.getPaddingBottom()) {
                //int bottomChange = expectedHeight - child.getHeight();
                //child.setBottom(child.getBottom() + bottomChange);

                child.setPadding(0,0,0,dy);

                isBottomChanged = true;
            }
        } else {
            result = super.onDependentViewChanged(parent, child, dependency);
        }*/

        return result || isBottomChanged;
    }
}
