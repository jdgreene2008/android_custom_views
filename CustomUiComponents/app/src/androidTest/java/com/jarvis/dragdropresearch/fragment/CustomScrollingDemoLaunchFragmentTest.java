package com.jarvis.dragdropresearch.fragment;

import android.os.Bundle;

import com.jarvis.dragdropresearch.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;

@RunWith(AndroidJUnit4.class)
public class CustomScrollingDemoLaunchFragmentTest {

    @Test
    public void test_FragmentLaunchDisplaysButtons() {
        setupFragmentScenario(null);
        onView(ViewMatchers.withId(R.id.btn_launch_shape_flash_view)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.btn_launch_scrolling_rails)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.btn_launch_image_flash_view)).check(ViewAssertions.matches(
                ViewMatchers.isDisplayed()));
    }

    private void setupFragmentScenario(Bundle args) {
        FragmentScenario.launchInContainer(CustomScrollingDemoLaunchFragment.class, args,
                new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader,
                            @NonNull String className) {
                        // No specific customizations needed.
                        return super.instantiate(classLoader, className);
                    }
                });
    }
}
