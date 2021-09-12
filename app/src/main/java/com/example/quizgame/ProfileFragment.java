package com.example.quizgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.quizgame.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentProfileBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
//    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater,container,false);
//        Log.d("Email",auth.getCurrentUser().getEmail());
        binding.emailBox.setText(auth.getCurrentUser().getEmail());
        binding.emailBox.setFocusable(false);
//        binding.nameBox.setText(user.getName());
//        Log.d("name",user.getName());
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.emailBox.getText().toString().equals(auth.getCurrentUser().getEmail().toString())){
                    auth.getCurrentUser().updatePassword(String.valueOf(binding.passBox.getText().toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
//                            binding.emailBox.setText("");
//                            binding.nameBox.setText("");
//                            user.setPassword(String.valueOf(binding.passBox.getText()));
                            binding.passBox.setText("");
                            Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "Please enter correct details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false);
        return binding.getRoot();

    }
}