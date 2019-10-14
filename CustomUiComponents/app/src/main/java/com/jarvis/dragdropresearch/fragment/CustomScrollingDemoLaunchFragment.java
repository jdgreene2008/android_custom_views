package com.jarvis.dragdropresearch.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jarvis.dragdropresearch.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class CustomScrollingDemoLaunchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_scrolling_demo_launch, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_launch_scrolling_rails).setOnClickListener(Navigation
                .createNavigateOnClickListener(
                        R.id.action_customScrollingDemoLaunchFragment_to_scrollingRailsViewFragment));
        view.findViewById(R.id.btn_launch_image_flash_view).setOnClickListener(Navigation
                .createNavigateOnClickListener(
                        R.id.action_customScrollingDemoLaunchFragment_to_imageFlashViewFragment));
        view.findViewById(R.id.btn_launch_shape_flash_view).setOnClickListener(Navigation
                .createNavigateOnClickListener(
                        R.id.action_customScrollingDemoLaunchFragment_to_flashShapeViewFragment));
    }
}
