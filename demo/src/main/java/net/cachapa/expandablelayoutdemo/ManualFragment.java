package net.cachapa.expandablelayoutdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import net.cachapa.expandablelayout.ExpandableLayout;

public class ManualFragment extends Fragment implements ExpandableLayout.OnExpansionUpdateListener, SeekBar.OnSeekBarChangeListener {
    private SeekBar seekbar;
    private ExpandableLayout expandableLayout;
    private View content;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.manual_fragment, container, false);

        seekbar = rootView.findViewById(R.id.seek_bar);
        seekbar.setOnSeekBarChangeListener(this);

        expandableLayout = rootView.findViewById(R.id.expandable_layout);
        expandableLayout.setOnExpansionUpdateListener(this);

        content = rootView.findViewById(R.id.content);

        return rootView;
    }

    @Override
    public void onExpansionUpdate(float expansionFraction, int state) {
        Log.d("ExpandableLayout", "State: " + state);
        content.setAlpha(expansionFraction);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        expandableLayout.setExpansion(seekbar.getProgress() / (float) seekbar.getMax());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
