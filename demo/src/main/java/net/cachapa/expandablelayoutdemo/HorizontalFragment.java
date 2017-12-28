package net.cachapa.expandablelayoutdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.cachapa.expandablelayout.ExpandableLayout;

public class HorizontalFragment extends Fragment implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
    private ExpandableLayout expandableLayout;
    private ImageView expandButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.horizontal_fragment, container, false);

        expandableLayout = rootView.findViewById(R.id.expandable_layout);
        expandButton = rootView.findViewById(R.id.expand_button);

        expandableLayout.setOnExpansionUpdateListener(this);
        expandButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onExpansionUpdate(float expansionFraction, int state) {
        Log.d("ExpandableLayout", "State: " + state);
        expandButton.setRotation(expansionFraction * 180);
    }

    @Override
    public void onClick(View view) {
        expandableLayout.toggle();
    }
}
