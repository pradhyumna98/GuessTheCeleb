package com.example.pradhyumna.guesstheceleb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURL = new ArrayList<String>();
    ArrayList<String> celebName = new ArrayList<String>();
    int choosenCeleb = 0;
    ImageView imgCeleb;
    String[] options = new String[4];
    int correctOption = 0;
    Button option1 , option2 , option3 , option4;

    public void celebChosen(View view){
        if(view.getTag().equals(Integer.toString(correctOption))){
            Toast.makeText(getApplicationContext() , "CORRECT" , Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext() , "WRONG IT IS" + choosenCeleb , Toast.LENGTH_SHORT).show();
        }
        nextQuestion();
    }

    public class ImageDownloader extends AsyncTask<String , Void , Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection = null;
            try{

                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap mybitmap = BitmapFactory.decodeStream(inputStream);
                return mybitmap;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }

    public class DownloadTask extends AsyncTask<String , Void , String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                    url = new URL(urls[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){
                    char currentData = (char) data;
                    result+=currentData;
                     data = reader.read();
                    
                }
                return result;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }

    public void nextQuestion(){
        try {
        Random rand = new Random();
        choosenCeleb = rand.nextInt(celebURL.size());

        ImageDownloader imageTask = new ImageDownloader();


            Bitmap celebImages = imageTask.execute(celebURL.get(choosenCeleb)).get();
            imgCeleb.setImageBitmap(celebImages);

            correctOption = rand.nextInt(4);
            int wrongAnswer;

            for (int i=0;i<4;i++){

                if(i==correctOption){
                    options[i] = celebName.get(choosenCeleb);
                }
                else{
                    wrongAnswer = rand.nextInt(celebURL.size());

                    while (wrongAnswer == choosenCeleb){
                        wrongAnswer = rand.nextInt(celebURL.size());
                    }
                    options[i] = celebName.get(wrongAnswer);
                }
                option1.setText(options[0]);
                option2.setText(options[1]);
                option3.setText(options[2]);
                option4.setText(options[3]);
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        String result;
        imgCeleb = findViewById(R.id.celebImage);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        try{

            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = result.split("<div class=\"channelListEntry\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
           for(int i=1;i<splitResult.length-1;i++) {
               Matcher m;
                m = p.matcher(splitResult[i]);
               while (m.find()){
                   celebURL.add(m.group(1));
               }
           }


             p = Pattern.compile("alt=\"(.*?)\"");
            for(int i=1;i<splitResult.length-1;i++) {
                Matcher m;
                m = p.matcher(splitResult[i]);
                while (m.find()){
                    celebName.add(m.group(1));
                    System.out.println(m.group(1));
                }
            }


            nextQuestion();
        }catch (Exception e){
            e.printStackTrace();

        }


    }
}
