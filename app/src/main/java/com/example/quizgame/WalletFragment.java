package com.example.quizgame;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.quizgame.databinding.FragmentWalletBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class WalletFragment extends Fragment {

    public WalletFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentWalletBinding binding;
    FirebaseFirestore database;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWalletBinding.inflate(inflater, container,false);

        database = FirebaseFirestore.getInstance();

//        database.collection("users")
//                .document(FirebaseAuth.getInstance().getUid())
//                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                // //getting data from database and making obj of user
//                user = documentSnapshot.toObject(User.class);
//                // //Setting values of coins
//                binding.currentCoins.setText(String.valueOf(user.getCoins()));
//            }
//        });

        // Khud se lkha for real time change
        database.collection("users")
                .document(FirebaseAuth.getInstance().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                user = Objects.requireNonNull(value).toObject(User.class);
                binding.currentCoins.setText(String.valueOf(user.getCoins()));

            }
        });

        binding.sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getCoins() > 50000){
                    String uid = FirebaseAuth.getInstance().getUid();
                    String number = String.valueOf(binding.contactBox.getText());
                    WithdrawRequest request = new WithdrawRequest(uid, number,user.getName());
                    // // creating new collection in database
                    database.collection("withdraws")
                            .document(uid)
                            .set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // //Extra hoshyari (updating coins)
                            database.collection("users")
                                    .document(uid)
                                    .update("coins",0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    database.collection("users")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                    user.setCoins(0);
                                                    binding.currentCoins.setText(String.valueOf(user.getCoins()));
                                                }
                                            });
                                }
                            });
                            binding.contactBox.setText("");
                            Toast.makeText(getContext(), "Request Send successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    Toast.makeText(getContext(), "Earn 50,000 coins to withdraw", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return binding.getRoot();


    }
}