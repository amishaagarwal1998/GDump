package g_dump.g_dump;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;


public class Profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail;
    TextView Name,Email;
    private DatabaseReference mDatabase;
    private Button bsub;
    private String main,code,poi;
    private EditText editTextcode;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();



        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(Profile.this, Signup.class));
            finish();
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getPhoneNumber());
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        editTextcode=(EditText)findViewById(R.id.editcode);



        bsub=(Button)findViewById(R.id.submit);
        bsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(editTextcode.getText().toString().equals("")){
                        Toast.makeText(Profile.this,"Please enter a code first",Toast.LENGTH_SHORT).show();
                        editTextcode.requestFocus();
                    }
                    else{
                        progressDialog = new ProgressDialog(Profile.this);

                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();

                        code=editTextcode.getText().toString();
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference userNameRef = rootRef.child("Codes").child(code);

                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {


                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                                    ref.child("Codes").child(code).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // This method is called once with the initial value and again
                                            // whenever data at this location is updated.
                                            dataSnapshot.getRef().removeValue();


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            // Failed to read value


                                        }
                                    });



                                    progressDialog.hide();
                                    Date currentTime = Calendar.getInstance().getTime();
                                    mDatabase.child("Codes Submitted").child(currentTime.toString()).setValue(code).addOnCompleteListener(Profile.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create();
                                            alertDialog.setTitle("Code Accepted");
                                            alertDialog.setMessage("Your Code has been accepted.");
                                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            alertDialog.show();
                                        }
                                    });

                                    int p=Integer.parseInt(poi);
                                    p=p+10;
                                    mDatabase.child("Points").setValue(Integer.toString(p)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            welcome();
                                        }
                                    });




                                }
                                else
                                {
                                    progressDialog.hide();



                                    AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create();
                                    alertDialog.setTitle("Invalid Code");
                                    alertDialog.setMessage("You have entered an invalid code or the code has been already entered.");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                    }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        };
                        userNameRef.addListenerForSingleValueEvent(eventListener);

                    }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        Name = (TextView)header.findViewById(R.id.Name);
        Email = (TextView)header.findViewById(R.id.textView12911);
        mDatabase.child("Email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue().toString();
                if(!value.equals("Not Provided"))
                Email.setText(value);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value


            }
        });
        welcome();


    }
    private void welcome()
    {
        mDatabase.child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue().toString();
                Name.setText(value);
                main=value;


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value


            }
        });

        mDatabase.child("Points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue().toString();
                poi=value;
                main="Welcome "+main+"\n"+"Your Current Score: "+value;
                textViewUserEmail.setText(main);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value


            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Help) {
            startActivity(new Intent(Profile.this,Help.class));
            return true;
        }
        else if(id == R.id.about) {
            startActivity(new Intent(Profile.this,About.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();



         if (id == R.id.nav_manage) {
             Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show();


         } else if (id == R.id.nav_share) {
             Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show();


         } else if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(Profile.this, Signup.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
