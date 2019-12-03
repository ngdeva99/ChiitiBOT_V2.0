package com.example.medico_bot;

import android.content.res.AssetFileDescriptor;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

//import org.tensorflow.lite.Interpreter;






import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.*;
import java.util.*;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    EditText userInput;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<ResponseMessage> responseMessageList;
//    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    // tflite graph



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInput=findViewById(R.id.userInput);
        recyclerView=findViewById(R.id.conversation);
        responseMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(responseMessageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(messageAdapter);









        userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {

                    ResponseMessage responseMessage = new ResponseMessage(userInput.getText().toString(), true);
                    responseMessageList.add(responseMessage);

                //    final Interpreter.Options tflieOptions = new Interpreter.Options();



                     TensorFlowInferenceInterface tensorFlowInferenceInterface;


                    tensorFlowInferenceInterface = new  TensorFlowInferenceInterface(getAssets(),"frozen_model.pb");




                    float[] res = {0, 0, 0, 0, 0, 0};
                    float[] bag = new float[50];

                    String[] labels = {"goodbye", "greeting", "headache", "opentoday", "payments", "thanks"};

                    String[] words = new String[]{"'s", "acceiv", "ach", "am", "anyon", "ar", "bad", "bye", "can", "card", "cash", "credit", "day", "do", "from", "good", "goodby",
                            "hav", "head", "headach", "hello", "help", "hi", "hour", "how", "hurt", "i", "is", "lat", "mastercard", "my", "not", "on", "op", "pain", "see", "sev", "suff", "tak",
                            "thank", "that", "the", "ther", "today", "tol", "very", "what", "when", "yo", "you"};

                    int j;
                    for (j = 0; j < words.length; j++) {
                        StringTokenizer str1 = new StringTokenizer(responseMessage.getText(), " ");

                        while (str1.hasMoreTokens()) {


                            String str = new String(str1.nextToken());
                            System.out.println(str + " " + words[j]);

                            if (str.equals(words[j]))

                                bag[j] = (float)1.0;
                        }
                    }

                    tensorFlowInferenceInterface.feed("my_input/X",bag,1,50);
                    tensorFlowInferenceInterface.run(new String[] {"my_outpu/Softmax"});
                    tensorFlowInferenceInterface.fetch("my_outpu/Softmax",res);

                    float max=res[0];
                    int index=0;
                    for( j=0;j<6;j++){
                        if(res[j]>max){
                            max=res[j];
                            index=j;
                        }
                    }



                    if(max<0.7)
                    {
                        ResponseMessage responseMessage1 = new ResponseMessage("Cannot understand u ", false);
                        responseMessageList.add(responseMessage1);
                    }


                    else if (index == 0) {

                            ResponseMessage responseMessage1 = new ResponseMessage("goodbye", false);
                            responseMessageList.add(responseMessage1);


                        }

                        else if (index == 1) {
                            ResponseMessage responseMessage1 = new ResponseMessage("greeting", false);
                            responseMessageList.add(responseMessage1);
                        }
                        else if (index == 2) {
                            ResponseMessage responseMessage1 = new ResponseMessage("headache", false);
                            responseMessageList.add(responseMessage1);
                        }
                        else if (index == 3) {
                            ResponseMessage responseMessage1 = new ResponseMessage("opentoday", false);
                            responseMessageList.add(responseMessage1);
                        }

                        else if (index == 4) {
                            ResponseMessage responseMessage1 = new ResponseMessage("payments", false);
                            responseMessageList.add(responseMessage1);
                        }
                       else if (index == 5) {
                            ResponseMessage responseMessage1 = new ResponseMessage("thanks", false);
                            responseMessageList.add(responseMessage1);
                        }

                    }




                    messageAdapter.notifyDataSetChanged();
                    if (!isLastVisible())
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

                return false;
            }
        });
    }
    boolean isLastVisible() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        int pos = layoutManager.findLastCompletelyVisibleItemPosition();
        int numItems = recyclerView.getAdapter().getItemCount();
        return (pos >= numItems);
    }



}
