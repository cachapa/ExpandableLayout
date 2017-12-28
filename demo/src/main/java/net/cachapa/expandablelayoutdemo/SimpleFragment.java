package net.cachapa.expandablelayoutdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.cachapa.expandablelayout.ExpandableLayout;

public class SimpleFragment extends Fragment implements View.OnClickListener {

    private ExpandableLayout expandableLayout0;
    private ExpandableLayout expandableLayout1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.simple_fragment, container, false);

        expandableLayout0 = rootView.findViewById(R.id.expandable_layout_0);
        expandableLayout1 = rootView.findViewById(R.id.expandable_layout_1);

        expandableLayout0.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d("ExpandableLayout0", "State: " + state);
            }
        });

        expandableLayout1.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d("ExpandableLayout1", "State: " + state);
            }
        });

        rootView.findViewById(R.id.expand_button).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (expandableLayout0.isExpanded()) {
            expandableLayout0.collapse();
        } else if (expandableLayout1.isExpanded()) {
            expandableLayout1.collapse();
        } else {
            expandableLayout0.expand();
            expandableLayout1.expand();
        }
    }
}
