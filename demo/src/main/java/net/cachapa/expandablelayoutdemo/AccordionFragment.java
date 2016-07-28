package net.cachapa.expandablelayoutdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.cachapa.expandablelayout.ExpandableLayout;

public class AccordionFragment extends Fragment implements View.OnClickListener {

    private ExpandableLayout expandableLayout0;
    private ExpandableLayout expandableLayout1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.accordion_fragment, container, false);

        expandableLayout0 = (ExpandableLayout) rootView.findViewById(R.id.expandable_layout_0);
        expandableLayout1 = (ExpandableLayout) rootView.findViewById(R.id.expandable_layout_1);

        rootView.findViewById(R.id.expand_button_0).setOnClickListener(this);
        rootView.findViewById(R.id.expand_button_1).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.expand_button_0) {
            expandableLayout0.expand();
            expandableLayout1.collapse();
        } else {
            expandableLayout0.collapse();
            expandableLayout1.expand();
        }
    }
}
