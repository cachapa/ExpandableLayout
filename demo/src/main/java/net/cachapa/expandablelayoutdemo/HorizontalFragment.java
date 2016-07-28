package net.cachapa.expandablelayoutdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.cachapa.expandablelayout.ExpandableLayout;

public class HorizontalFragment extends Fragment implements View.OnClickListener {

    private ExpandableLayout expandableLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.horizontal_fragment, container, false);

        expandableLayout = (ExpandableLayout) rootView.findViewById(R.id.expandable_layout);
        rootView.findViewById(R.id.expand_button).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        expandableLayout.toggle();
    }
}
