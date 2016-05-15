package learning;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.wordteacher.R;

import main.BaseActivity;

/**
 * Created by alexeyglushkov on 15.05.16.
 */
public class SessionResultActivity extends BaseActivity {

    public static final String EXTERNAL_SESSION = "session";
    public static final int ACTIVITY_RESULT = 10001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_result);
    }
}
