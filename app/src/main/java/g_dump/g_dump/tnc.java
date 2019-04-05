package g_dump.g_dump;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class tnc extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView tnc;
    private Button accept;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnc);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Terms and Conditions");
        tnc=(TextView)findViewById(R.id.textView3);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue().toString();
                tnc.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value


            }
        });
        accept=(Button)findViewById(R.id.tncDone);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




    }
}
