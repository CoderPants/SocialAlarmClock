package com.donteco.alarmClock.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.alarm.DayOfWeek;

import java.util.List;

@SuppressLint("AppCompatCustomView")
//Don't have any idea, what is going on here
//i mean, annotation
public class MultiSpinner extends Spinner
        implements DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener
{
    public interface MultiSpinnerListener{
         void onItemsSelected(boolean[] selected);
    }

    private List<String> items;
    private boolean[] selected;
    private String defaultText;
    private MultiSpinnerListener listener;

    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context context, AttributeSet attr){
        super(context, attr);
    }

    public MultiSpinner(Context context, AttributeSet attr, int defStyleAttr){
        super(context, attr, defStyleAttr);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface)
    {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isAllChecked = true;

        for (int i = 0; i < items.size(); i++)
        {
            if(selected[i])
            {
                stringBuilder.append(items.get(i));
                stringBuilder.append(", ");
            }
            else
                isAllChecked = false;
        }

        String spinnerText;

        if(isAllChecked)
            spinnerText = defaultText;
        else
        {
            spinnerText = stringBuilder.toString();
            if(spinnerText.length() > 2)
                spinnerText = spinnerText.substring(0, spinnerText.length()-2);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, new String[]{spinnerText});
        setAdapter(adapter);
        listener.onItemsSelected(selected);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
        selected[which] = isChecked;
    }

    @Override
    public boolean performClick()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(items.toArray(new CharSequence[items.size()]), selected, this);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setOnCancelListener(this);
        builder.show();

        return true;
    }

    public void setItems(List<String> items, String defaultText, MultiSpinnerListener listener)
    {
        this.items = items;
        this.defaultText = defaultText;
        this.listener = listener;

        selected = new boolean[items.size()];

        for (int i = 0; i < selected.length; i++)
            selected[i] = true;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item,
                new String[]{defaultText});

        setAdapter(adapter);
    }
}
