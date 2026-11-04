package com.hewgill.android.nzsldict;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class BaseActivity extends AppCompatActivity {

    protected void setupAppToolbar() {
        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        applySystemBarInsets(this);
    }

    /**
     * Apps targeting API 35+ draw edge-to-edge. Push the toolbar below the status bar (so its
     * background extends behind it) and keep the content clear of the navigation bar, display
     * cutouts and the keyboard. Call after setContentView, for layouts that include R.layout.toolbar.
     */
    static void applySystemBarInsets(Activity activity) {
        View content = activity.findViewById(android.R.id.content);
        View toolbar = activity.findViewById(R.id.app_toolbar);
        int toolbarPaddingTop = toolbar.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(content, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout()
                    | WindowInsetsCompat.Type.ime());
            view.setPadding(insets.left, 0, insets.right, insets.bottom);
            toolbar.setPadding(toolbar.getPaddingLeft(), toolbarPaddingTop + insets.top,
                    toolbar.getPaddingRight(), toolbar.getPaddingBottom());
            return WindowInsetsCompat.CONSUMED;
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        // Respond to the toolbar's NavigationIcon as up/home button
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_favourites) {
            startActivity(new Intent(this, FavouritesActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
