package alonbd.simpler.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import alonbd.simpler.R;
import alonbd.simpler.TaskLogic.Action;
import alonbd.simpler.TaskLogic.NotificationAction;

public class NotificationActionFragment extends ActionFragment {

    EditText content;
    String taskName;
    @Override
    public Action genAction() {
        return new NotificationAction(content.getText().toString(),taskName);
    }

    public NotificationActionFragment(String taskName){
        super();
        this.taskName = taskName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_action_notification,container,false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        content = view.findViewById(R.id.content);
    }
}