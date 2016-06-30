package net.cachapa.expandablelayoutdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.cachapa.expandablelayout.ExpandableLinearLayout;

public class SimpleFragment extends Fragment implements ExpandableLinearLayout.ExpandListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.simple_fragment, container, false);

        ExpandableLinearLayout expandableLayout = (ExpandableLinearLayout) rootView.findViewById(R.id.expandable_layout);
        expandableLayout.setExpandListener(this);
        return rootView;
    }

    @Override
    public void onToggle(boolean expanded) {
        String msg = expanded ? "Expanded" : "Collapsed";
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
