package sg.edu.rp.webservices.dmsdchatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddMessageActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FirebaseAuth firebaseAuth;

    TextView textView;
    ListView lvMessage;
    ArrayList<Message> alMessage;
    ArrayAdapter<Message> aaMessage;
    EditText editText;
    Button button;

    // TODO: Task 1 - Declare Firebase variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference studentListRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        firebaseAuth = FirebaseAuth.getInstance();
        textView = (TextView) findViewById(R.id.textView);
        lvMessage = (ListView) findViewById(R.id.lvMessage);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.buttonAdd);

        alMessage = new ArrayList<Message>();
        aaMessage = new CustomMessager(this, R.layout.row, alMessage);
        lvMessage.setAdapter(aaMessage);


        // TODO: Task 2: Get Firebase database instance and reference
        firebaseDatabase = FirebaseDatabase.getInstance();
        studentListRef = firebaseDatabase.getReference("messages/");

        studentListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("AddMessageActivity", "onChildAdded()");
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    message.setId(dataSnapshot.getKey());

                    alMessage.add(message);
                    aaMessage.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("MainActivity", "onChildChanged()");
                String selectedId = dataSnapshot.getKey();
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    for (int i = 0; i < alMessage.size(); i++) {
                        if (alMessage.get(i).getId().equals(selectedId)) {
                            message.setId(selectedId);
                            alMessage.set(i, message);
                            break;
                        }
                    }
                    aaMessage.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.i("MainActivity", "onChildRemoved()");
                String selectedId = dataSnapshot.getKey();
                for(int i= 0; i < alMessage.size(); i++) {
                    if (alMessage.get(i).getId().equals(selectedId)) {
                        alMessage.remove(i);
                        break;
                    }
                }
                aaMessage.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("MainActivity", "onChildMoved()");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Database error occurred", databaseError.toException());
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Task 3: Add student to database and go back to main screen
                String text = editText.getText().toString();
               String name = getIntent().getStringExtra("name");
                Message message = new Message(text,name);


                studentListRef.push().setValue(message);
                editText.setText("");

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_logout) {

            firebaseAuth.signOut();

            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}

