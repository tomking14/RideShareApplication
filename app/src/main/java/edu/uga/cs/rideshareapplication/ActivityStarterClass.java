package edu.uga.cs.rideshareapplication;

// replace with your package name import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class ActivityStarterClass implements View.OnClickListener {
    private Class<?> activityToStart;

    public ActivityStarterClass(Class<?> activityToStart) {
        this.activityToStart = activityToStart;
    }

    @Override
    public void onClick(View view) {
        try {
            Intent intent = new Intent(view.getContext(), activityToStart);
            view.getContext().startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(view.getContext(), "Uh oh, something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }
}

