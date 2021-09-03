package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.quizgame.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // //To display app name
        setSupportActionBar(binding.toolbar);

        // //Framgment manager = To handle transactions between fragments
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // // transaction is a way to add,replace,or remove fraagments
        transaction.setReorderingAllowed(true);
        transaction.replace(R.id.content, HomeFragment.class, null);
        transaction.commit();

        binding.bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (i) {
                    case 0:
                        transaction.setReorderingAllowed(true);
                        transaction.replace(R.id.content, HomeFragment.class, null);
                        transaction.commit();
                        break;
                    case 1:

                        transaction.setReorderingAllowed(true);
                        transaction.replace(R.id.content, LeaderboardFragment.class, null);

                        transaction.commit();
                        break;
                    case 2:
                        transaction.replace(R.id.content, WalletFragment.class, null);
                        transaction.setReorderingAllowed(true);
                        transaction.commit();

                        break;
                    case 3:
                        transaction.replace(R.id.content, ProfileFragment.class, null);
                        transaction.setReorderingAllowed(true);
                        transaction.commit();

                        break;
                }
                return false;
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout){
            Toast.makeText(this, "Signing out", Toast.LENGTH_SHORT).show();
            auth = FirebaseAuth.getInstance();
            auth.signOut();
            if (auth.getCurrentUser() == null){
                startActivity(new Intent(this,LoginActivity.class));
            }

        }
        return super.onOptionsItemSelected(item);
    }
}