package com.example.quizgame;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizgame.databinding.ActivityQuizBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    ActivityQuizBinding binding;
    ArrayList<Questions> questions; // //Array
    Questions question; // //Object
    CountDownTimer timer;
    FirebaseFirestore database;
    ProgressDialog dialog;

    int index = 0;
    int correctAnswers = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Questions");

        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        questions = new ArrayList<>();
        database = FirebaseFirestore.getInstance();

        dialog.show();

        // //Getting category id from categoryadapter
        String catId = getIntent().getStringExtra("catId");


        Random random = new Random();
        int rand = random.nextInt(12);
        Log.d("random", String.valueOf(rand));
        database.collection("categories")
                .document(catId)
                .collection("questions")
                .whereGreaterThanOrEqualTo("index",rand)
                .orderBy("index")
                .limit(5)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() < 5){
                    database.collection("categories")
                            .document(catId)
                            .collection("questions")
                            .whereLessThanOrEqualTo("index",rand)
                            .orderBy("index")
                            .limit(5)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            dialog.dismiss();
                                for (DocumentSnapshot snapshot: queryDocumentSnapshots ) {
                                    Questions question = snapshot.toObject(Questions.class);
                                    questions.add(question);
                                }
                            setNextQuestion();
                            }
                    });
                }else {
                    dialog.dismiss();
                    for (DocumentSnapshot snapshot: queryDocumentSnapshots ) {
                        Questions question = snapshot.toObject(Questions.class);
                        questions.add(question);
                    }
                    setNextQuestion();
                }
            }
        });

        resetTimer();

    }
// //Code for timer
    void resetTimer(){
        timer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.timer.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                if (timer!=null)
                    timer.cancel();
                timer.start();
                index++;
                if (index < questions.size()){
                    binding.questionCounter.setText(String.format("%d/%d",(index+1),questions.size()));
                    // // getting questions object from array
                    question = questions.get(index);
                    // //Setting fields with question and options
                    binding.question.setText(question.getQuestion());
                    binding.option1.setText(question.getOption1());
                    binding.option2.setText(question.getOption2());
                    binding.option3.setText(question.getOption3());
                    binding.option4.setText(question.getOption4());
                }
            }
        };
    }

    // //Show Correct answer with green color
    void showAnser(){
        if (question.getAnswer().equals(binding.option1.getText().toString()))
            binding.option1.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if (question.getAnswer().equals(binding.option2.getText().toString())){
            binding.option2.setBackground(getResources().getDrawable(R.drawable.option_right));
        } else if (question.getAnswer().equals(binding.option3.getText().toString())){
            binding.option3.setBackground(getResources().getDrawable(R.drawable.option_right));
        } else if (question.getAnswer().equals(binding.option4.getText().toString()))
            binding.option4.setBackground(getResources().getDrawable(R.drawable.option_right));
    }

    // //Display next question
   void setNextQuestion() {
       binding.option1.setOnClickListener(this::onClick);
       binding.option2.setOnClickListener(this::onClick);
       binding.option3.setOnClickListener(this::onClick);
       binding.option4.setOnClickListener(this::onClick);
        if (timer!=null)
            timer.cancel();
        timer.start();
        if (index < questions.size()){
            binding.questionCounter.setText(String.format("%d/%d",(index+1),questions.size()));
            // // getting questions object from array
            question = questions.get(index);
            // //Setting fields with question and options
            binding.question.setText(question.getQuestion());
            binding.option1.setText(question.getOption1());
            binding.option2.setText(question.getOption2());
            binding.option3.setText(question.getOption3());
            binding.option4.setText(question.getOption4());
        }
    }
// //Checking if the answer is correct
    void checkAnwer(TextView textView){
        String selectedAnwser = textView.getText().toString();
        if (selectedAnwser.equals(question.getAnswer())){
            textView.setBackground(getResources().getDrawable(R.drawable.option_right));
            correctAnswers++;
            binding.option1.setOnClickListener(null);
            binding.option2.setOnClickListener(null);
            binding.option3.setOnClickListener(null);
            binding.option4.setOnClickListener(null);
        }else{
            showAnser();
            textView.setBackground(getResources().getDrawable(R.drawable.option_wrong));
            binding.option1.setOnClickListener(null);
            binding.option2.setOnClickListener(null);
            binding.option3.setOnClickListener(null);
            binding.option4.setOnClickListener(null);
        }

    }

    void reset(){
        binding.option1.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option2.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option3.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option4.setBackground(getResources().getDrawable(R.drawable.option_unselected));
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.option_1:
            case R.id.option_2:
            case R.id.option_3:
            case R.id.option_4:
                if (timer != null)
                    timer.cancel();
                TextView selected = (TextView) view;
                checkAnwer(selected);
                break;
            case R.id.nextBtn:
                reset();
                if (index+1 == questions.size()){
                    Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                    intent.putExtra("correct",correctAnswers);
                    intent.putExtra("total",questions.size());
                    startActivity(intent);
                    Toast.makeText(this, "Quiz finished", Toast.LENGTH_SHORT).show();
                }
                else {
                    index++;
                    setNextQuestion();
                }
                break;
        }

    }
}