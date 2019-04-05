package g_dump.g_dump;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class Database extends AppCompatActivity {
    EditText fname,email,age;
    Button next;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private CheckBox cb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();


        next=(Button)findViewById(R.id.buttonNext);
        fname=(EditText)findViewById(R.id.editTextName);
        email=(EditText)findViewById(R.id.editTextEmail);
        age=(EditText)findViewById(R.id.editTextAge);
        cb=(CheckBox)findViewById(R.id.checkBox);



        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userNameRef = rootRef.child("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber());

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    startActivity(new Intent(Database.this,Profile.class));
                    finish();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);



        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb.isChecked())
                {
                 email.setVisibility(View.INVISIBLE);
                }
                else{
                    email.setVisibility(View.VISIBLE);

                }
            }
        });




        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fname.getText().toString().equals("")){
                    Toast.makeText(Database.this,"Please Enter your full name",Toast.LENGTH_SHORT).show();
                    fname.requestFocus();

                }
                else if(!cb.isChecked()){
                if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    Toast.makeText(Database.this,"Please enter a valid Email Address",Toast.LENGTH_LONG).show();
                    email.requestFocus();
                }}
                else if(age.getText().toString().equals("")){
                    Toast.makeText(Database.this,"Please enter your age",Toast.LENGTH_LONG).show();
                    age.requestFocus();
                }
                else
                {
                    progressDialog = new ProgressDialog(Database.this);

                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    mDatabase.child("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Name").setValue(fname.getText().toString());
                    if(cb.isChecked()){
                        mDatabase.child("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Email").setValue("Not Provided");

                    }
                    else {
                        mDatabase.child("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Email").setValue(email.getText().toString());
                    }
                    mDatabase.child("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Points").setValue("0");

                    mDatabase.child("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Age").setValue(age.getText().toString()).addOnCompleteListener(Database.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            progressDialog.hide();
                            startActivity(new Intent(Database.this,Profile.class));
                            finish();

                        }
                    });

                    }

            }
        });
    }
}
