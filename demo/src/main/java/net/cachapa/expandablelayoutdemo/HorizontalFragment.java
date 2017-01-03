package net.cachapa.expandablelayoutdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.horizontal_fragment, container, false);

        expandableLayout = (ExpandableLayout) rootView.findViewById(R.id.expandable_layout);
        expandButton = (ImageView) rootView.findViewById(R.id.expand_button);

        expandableLayout.setOnExpansionUpdateListener(this);
        expandButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onExpansionUpdate(float expansionFraction) {
        expandButton.setRotation(expansionFraction * 180);
    }

    @Override
    public void onClick(View view) {
        expandableLayout.toggle();
    }
}
