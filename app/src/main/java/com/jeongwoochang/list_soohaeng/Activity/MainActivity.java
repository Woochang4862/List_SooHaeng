package com.jeongwoochang.list_soohaeng.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jeongwoochang.list_soohaeng.Fragment.DateFragment;
import com.jeongwoochang.list_soohaeng.Fragment.AuthFragment;
import com.jeongwoochang.list_soohaeng.Fragment.SubjectFragment;
import com.jeongwoochang.list_soohaeng.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton listButton;
    private Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.action_date: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                    }
                    selectedFragment = DateFragment.newInstance();
                    break;
                }
                case R.id.action_subject: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                    }
                    selectedFragment = SubjectFragment.newInstance();
                    break;
                }
                case R.id.action_auth: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                    }
                    selectedFragment = AuthFragment.newInstance();
                    break;
                }
            }
            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, selectedFragment);
                transaction.commit();
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.action_date);

        listButton = findViewById(R.id.list_button);
        listButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TestGroupListActivity.class);
            startActivityForResult(intent, 201);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 201:
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.detach(selectedFragment).attach(selectedFragment).commit();
                    break;
            }
        }
    }
}
