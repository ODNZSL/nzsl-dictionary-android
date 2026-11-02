package com.hewgill.android.nzsldict;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.view.MenuItem;

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
