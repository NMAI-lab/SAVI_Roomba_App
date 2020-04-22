package com.egreksystems.mailuber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class OrderRobot extends AppCompatActivity {

    String SERVER_IP;
    int SERVER_PORT;
    AppCompatSpinner fromSpinner;
    AppCompatSpinner toSpinner;
    Thread Thread1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_robot);
        Bundle extras = getIntent().getExtras();

        if (extras != null){
            SERVER_IP = extras.getString("IP");
            SERVER_PORT = extras.getInt("PORT");
            Thread1 = new Thread(new Thread1());
            Thread1.start();
        }

         fromSpinner = findViewById(R.id.from);
         toSpinner = findViewById(R.id.to);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.post_points, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);

        Button send = findViewById(R.id.order);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "" + fromSpinner.getSelectedItem() + "," + toSpinner.getSelectedItem();
                if (!message.isEmpty()) {
                    new Thread(new Thread3(message)).start();
                }
            }
        });



    }

    private PrintWriter output;
    private BufferedReader input;
    class Thread1 implements Runnable {
        public void run() {
            Socket socket;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Thread3 implements Runnable {
        private String message;
        Thread3(String message) {
            this.message = message;
        }
        @Override
        public void run() {
            output.write(message);
            output.flush();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Sent", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
