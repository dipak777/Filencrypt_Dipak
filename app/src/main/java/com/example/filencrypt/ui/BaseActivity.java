package com.example.filencrypt.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.filencrypt.R;

public abstract class BaseActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {
    private int selectedMenuId;
    private long pressedTime;
    DrawerLayout drawerLayout;

    protected abstract int getMenuItemId();

    @Override
    protected void onResume() {
        super.onResume();
        this.selectedMenuId = getMenuItemId();
        if (this.selectedMenuId == -1) ; /*{
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }*/
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            new AlertDialog.Builder(this)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle("Closing Application")
                    .setMessage("Are you sure you want to close this application?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        } else if (item.getItemId() == R.id.delete_passcode) {
            new AlertDialog.Builder(this)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle("Deleting Passcode")
                    .setMessage("Are you sure you want to delete the passcode and Exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sp = getSharedPreferences("filencrypt", MODE_PRIVATE);
                            SharedPreferences.Editor ed = sp.edit();
                            ed.clear();//Clear stored passcode SharedPreference file "filencrypt"
                            ed.commit();
                            finish();
                            //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            //ActivityCompat.finishAffinity(BaseActivity.this);
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else if (item.getItemId() == R.id.info) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_info_black)
                    .setTitle("Filencrypt android app")
                    .setMessage("Our app will use AES Encryption to encrypt the file that user wants to save in the app and hide from other people.")
                    .setPositiveButton("Ok", null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    //Back press destroy activity
    @Override
    public void onBackPressed() {
        //Press back button again to exit
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
            System.exit(0);
        } else {
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            Snackbar snackbar = Snackbar.make(drawerLayout, "Press back again to exit", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        pressedTime = System.currentTimeMillis();
    }

    //Dialog box for exit on back button
        /*new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle("Closing Application")
                .setMessage("Are you sure you want to close this application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }*/

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        this.selectedMenuId = item.getItemId();
        return true;
    }
}