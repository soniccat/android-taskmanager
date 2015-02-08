package com.playground.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.rssclient.controllers.R;

import java.text.ParseException;
import java.util.IllegalFormatException;

/**
 * TODO: document your custom view class.
 */
public class SettingsField extends FrameLayout {

    ViewGroup innerViewGroup;
    TextView titleTextView;

    EditText valueTextView;
    Spinner valueSpinner;

    public SettingsField(Context context) {
        super(context);
        init(null, 0);
    }

    public SettingsField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SettingsField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SettingsField, defStyle, 0);

        String title = a.getString(R.styleable.SettingsField_text);
        String defaultValue = a.getString(R.styleable.SettingsField_default_value);
        int inputTypeCode = a.getInteger(R.styleable.SettingsField_input_type, 0);
        int inputType = 0;
        if (inputTypeCode == 0) {
            inputType = InputType.TYPE_CLASS_TEXT;

        } else if (inputTypeCode == 1) {
            inputType = InputType.TYPE_CLASS_NUMBER;
        }

        if (inputTypeCode == 2) {
            innerViewGroup = (ViewGroup)View.inflate(getContext(), R.layout.settings_values_field, null);
            valueSpinner = (Spinner)innerViewGroup.findViewById(R.id.value);

            int arrayResId = a.getResourceId(R.styleable.SettingsField_values, 0);
            if (arrayResId == 0 && !isInEditMode()) {
                throw new IllegalArgumentException(
                        "You provided input type = values but missed values parameter in xml");
            }

            if (!isInEditMode()) {
                valueSpinner.setAdapter(createSpinnerAdapter(arrayResId));
            }

        } else {
            innerViewGroup = (ViewGroup)View.inflate(getContext(), R.layout.settings_text_field, null);

            valueTextView = (EditText)innerViewGroup.findViewById(R.id.value);
            valueTextView.setText(defaultValue);
            valueTextView.setInputType(inputType);
        }

        titleTextView = (TextView)innerViewGroup.findViewById(R.id.name);
        titleTextView.setText(title);

        a.recycle();

        ViewGroup.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        innerViewGroup.setLayoutParams(lp);

        addView(innerViewGroup);
    }

    public String getString() {
        if (valueTextView != null) {
            return valueTextView.getText().toString();
        }

        return "";
    }

    public int getInt() {
        if (valueTextView != null) {
            return Integer.parseInt(valueTextView.getText().toString());

        } else if (valueSpinner != null) {
            return valueSpinner.getSelectedItemPosition();
        }

        return 0;
    }

    public void setString(String value) {
        if (valueTextView != null) {
            valueTextView.setText(value);
        }
    }

    public void setInt(int value) {
        if (valueTextView != null) {
            valueTextView.setText(Integer.toString(value));

        } else if (valueSpinner != null) {
            valueSpinner.setSelection(value);
        }
    }

    private SpinnerAdapter createSpinnerAdapter(int arrayRes) {
        return ArrayAdapter.createFromResource(getContext(), arrayRes, android.R.layout.simple_spinner_item);
    }
}
