package de.bastianb.carlogbook.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

import de.bastianb.carlogbook.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * open the activity to list all rides
     * @param view
     */
    public void openListRidesActivity(View view){
        Intent intent = new Intent(this, ListRidesActivity.class);
        startActivity(intent);
    }

    /**
     * open the activity to list all drivers
     * @param view
     */
    public void openListDriversActivity(View view){
        Intent intent = new Intent(this, ListDriversActivity.class);
        startActivity(intent);
    }

    public void openAddRidesActivity(View view){
        Intent intent = new Intent(this, AddRideActivity.class);
        startActivity(intent);
    }
}
