package com.mad41.tripreminder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mad41.tripreminder.constants.Constants;
import com.mad41.tripreminder.room_database.MyRoomDataBase;
import com.mad41.tripreminder.room_database.trip.Trip;
import com.mad41.tripreminder.trip_ui.RoundTripDialogue;
import com.mad41.tripreminder.trip_ui.TripModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.xml.namespace.QName;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddTripFragments extends Fragment {

    public static final String TAG = "room";

    Switch roundSwitch;
    EditText txt_place;
    TextView txtDate;
    TextView txtTtime;
    CircleImageView btnDate;
    CircleImageView btnTime;
    int t1Hour, t1Minuite;
    private int mYear, mMonth, mDay;
    private MyRoomDataBase dataBaseInstance;

    public final static String START = "START";
    public final static String END = "END";
    public static final String ID = "ID";


    private int id;

    //Return Date and Time
    String round_date;
    String round_time;


    int AUTOCOMPLETE_REQUEST_CODE_START = 1;
    int AUTOCOMPLETE_REQUEST_CODE2_END = 2;
    EditText txt_start;
    EditText txt_end;
    Button btn_place;
    Context context;
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    RoundTripDialogue roundTripDialogue;

    private Communicator communicatorListener;

    ArrayList<Trip> arrayList;

    public AddTripFragments() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {


        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        communicatorListener = (Communicator) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_trip_fragments, container, false);
        Places.initialize(getContext().getApplicationContext(), "AIzaSyA7dH75J8SZ0-GkeHqHANbflPhdpbfU5yI");

        txtDate = (TextView) view.findViewById(R.id.txt_date);
        txtTtime = (TextView) view.findViewById(R.id.txt_time);
        txt_place = (EditText) view.findViewById(R.id.txt_place);

        txt_start = (EditText) view.findViewById(R.id.txt_startPlace);
        txt_end = (EditText) view.findViewById(R.id.txt_endPlace);

        btnDate = (CircleImageView) view.findViewById(R.id.btn_date);
        btnTime = (CircleImageView) view.findViewById(R.id.btn_time);
        btn_place = (Button) view.findViewById(R.id.btn_addTrip);

        roundSwitch = (Switch) view.findViewById(R.id.round_switch);
        dataBaseInstance = MyRoomDataBase.getUserDataBaseInstance(getContext().getApplicationContext());

        btn_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long alarmTime = getAlarmTime();
                if (alarmTime > 0) {
                    Trip myTrip = new Trip(txt_place.getText().toString(), txt_start.getText().toString(), txt_end.getText().toString(),
                            txtTtime.getText().toString(), txtDate.getText().toString(), Constants.TRIP_UPCOMING, true, true);
                    new Thread() {
                        @Override
                        public void run() {
                            id = (int) dataBaseInstance.tripDao().insertTrip(myTrip);
                            Log.i("room", "id is: " + id);
                            printTrip();
                        }
                    }.start();
                    communicatorListener.respon(alarmTime, id, txt_end.getText().toString());
                    myData();
                } else {
                    Toast.makeText(getContext(), "Please enter a valid date & time", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(getContext(), round_date + round_time, Toast.LENGTH_LONG).show();

            }
        });

        selectDate();
        selectTime();
        Start_trip();
        End_trip();
        setRound();


        return view;
    }

    void setRound() {
        roundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    openDialog();

                } else {

                }
            }
        });
    }

    public void openDialog() {
        RoundTripDialogue exampleDialog = new RoundTripDialogue();
        exampleDialog.show(getActivity().getSupportFragmentManager(), "example dialog");
    }

    private long getAlarmTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, calendar1.get(Calendar.YEAR));
        calendar1.set(Calendar.MONTH, calendar1.get(Calendar.MONTH));
        calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH));
        calendar1.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY));
        calendar1.set(Calendar.MINUTE, calendar1.get(Calendar.MINUTE));
        long l = calendar1.getTimeInMillis();

        Calendar calendar = Calendar.getInstance();
        //alarm time
        calendar.set(mYear, mMonth, mDay, t1Hour, t1Minuite);
        long alarmTime = calendar.getTimeInMillis() - l;
        Log.i("room", " check " + alarmTime);
        return alarmTime;
    }

    void selectDate() {
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtDate.setText("Date: " + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                                txtDate.setText("Date: " + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
    }

    void selectTime() {
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //initialize TimePicker Dialogue
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //Intialize Hour and Minute
                        t1Hour = hourOfDay;
                        t1Minuite = minute;
                        //initialize Calender
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0, 0, 0, t1Hour, t1Minuite);

                        txtTtime.setText("Time:" + DateFormat.format("hh:mm:aa", calendar));


                    }
                }, 12, 0, false
                );

                //Displayed previous Selected time
                timePickerDialog.updateTime(t1Hour, t1Minuite);
                timePickerDialog.show();
            }
        });
    }

    private void Start_trip() {

        txt_start.setFocusable(false);
        txt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields1 = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields1) //FullScreen
                        .build(getContext().getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_START);
            }
        });
    }

    private void End_trip() {
        txt_end.setFocusable(false);
        txt_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields1 = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields1) //FullScreen
                        .build(getContext().getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE2_END);
            }
        });
    }

    void myData() {
        communicatorListener.returnToOnGoingActivity();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    private void printTrip() {

        new Thread() {
            @Override
            public void run() {
                ArrayList<Trip> trips = (ArrayList<Trip>) dataBaseInstance.tripDao().getUpcomingTrips();
            }
        }.start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_START) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                txt_start.setText(place.getAddress());
                Toast.makeText(getContext(), place.getLatLng() + "", Toast.LENGTH_LONG).show();

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;

        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE2_END) {

            if (resultCode == Activity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                txt_end.setText(place.getAddress());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                // Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void getArrayList(ArrayList<Trip> arrayList) {
        this.arrayList = arrayList;
    }


    public interface Communicator {
        void respon(long alarmTime, int id, String end);

        void sendArrayListToRecycleView(ArrayList<Trip> arrayList2);

        void returnToOnGoingActivity();
    }

}
