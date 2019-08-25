package com.example.uisaludmovilv01;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.uisaludmovilv01.modelos.Doctor;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeSet;

import javax.security.auth.login.LoginException;

public class AgendarActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener {
//    implements AdapterView.OnItemSelectedListener

    private static final String TAG = "AgendarActivity";

    //Fake data
    private static TreeSet<String> treeEspecialidades = new TreeSet<>();
    private ArrayList<Doctor> doctores = new ArrayList<>();

    //Array Spinners
    private ArrayList<String> especialidades;
    private ArrayList<Doctor> filtroDoctores = new ArrayList<>();

    //UI elements
    private Spinner ag_esp;
    private Spinner ag_doctor;
    private TextView ag_fecha, ag_hora;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    //Variables
    private String especialidad;
    private Doctor doctor;
    private LocalDate fecha;
    private LocalTime hora;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agendar);

        ag_esp = findViewById(R.id.ag_esp);
        ag_doctor = findViewById(R.id.ag_doctor);
        ag_fecha = findViewById(R.id.ag_fecha);
        ag_hora = findViewById(R.id.ag_hora);


        initializeFaxeData();
        Log.i(TAG, "onCreate: Se llenaron los doctores i.");

        Log.i(TAG, "onCreate: Vamos a selectItems() i.");
        selectItems();

        ag_fecha.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: ag_fecha clicked i.");
                fecha = LocalDate.now();

                datePickerDialog = new DatePickerDialog(AgendarActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                ag_fecha.setText(day + " / " + (month + 1) + " / " + year);

                                //colocar restricciones

                                fecha = LocalDate.of(year, month, day);
                            }
                        }, fecha.getYear(), fecha.getMonthValue(), fecha.getDayOfMonth());
                datePickerDialog.show();
            }
        });

        ag_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ag_hora clicked i.");
                hora = LocalTime.now();

                timePickerDialog = new TimePickerDialog(AgendarActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                //colocar restricciones

                                hora = LocalTime.of(hourOfDay, minute);

                                String meridian = hourOfDay > 11 ? " PM" : " AM";

                                hourOfDay = hourOfDay%12 == 0 ? 12 : hourOfDay%12;

                                ag_hora.setText(hourOfDay + (minute < 10 ? " : 0" : " : ") + minute + meridian);

                            }
                        },hora.getHour(), hora.getMinute(), false);
                timePickerDialog.show();
            }
        });
    }


    private void selectItems() {

        populateEsp();

        ag_esp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.i(TAG, "onItemSelected: Selected from ag_esp. Position " + position + " i.");

                especialidad = ag_esp.getSelectedItem().toString();

                Log.i(TAG, "onItemSelected: Especialidad seleccionada " + especialidad + " i.");

                populateDoctores(especialidad);

                Log.i(TAG, "onItemSelected: Doctores populates i.");
                Log.i(TAG, "onItemSelected: ag_doctor.size() = " + ag_doctor.getCount() + " i.");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ag_doctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                doctor = findDoctor(ag_doctor.getSelectedItem().toString());

                Log.i(TAG, "onItemSelected: Doctor seleccionado " + doctor.getNombre() + " i.");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void populateEsp() {

        Log.i(TAG, "populateEsp: called i.");

        especialidades = new ArrayList<String>(treeEspecialidades);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item, new ArrayList<String>(especialidades));
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        ag_esp.setAdapter(adapter);


    }

    private void populateDoctores(String e) {

        Log.i(TAG, "populateDoctores: called i.");

        filtroDoctores = filtrarEsp(e);
        ArrayList<String> nombresDoctores = new ArrayList<>();

        for (Doctor d : filtroDoctores) {
            nombresDoctores.add(d.getNombre());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item, new ArrayList<String>(nombresDoctores));
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        ag_doctor.setAdapter(adapter);

    }

    private void initializeFaxeData() {

        Log.i(TAG, "llenarArrayDoctores: called i.");

        Doctor d1 = new Doctor("Dr. One", "101", "General");
        d1.anadirDia("MONDAY", new boolean[]{true, true, false, false, false,
                false, false, false, false, false, false});
        d1.anadirDia("THURSDAY", new boolean[]{true, true, true, false, false,
                false, false, false, true, true, false});
        doctores.add(d1);

        Doctor d2 = new Doctor("Dr. Two", "102", "Cardiologia");
        d2.anadirDia("TUESDAY", new boolean[]{false, false, false, false, false,
                false, true, true, true, true, true});
        d2.anadirDia("WEDNESDAY", new boolean[]{true, true, true, true, true,
                false, false, false, false, false, false});
        doctores.add(d2);

        Doctor d3 = new Doctor("Dr. Three", "103", "Odontologia");
        d3.anadirDia("MONDAY", new boolean[]{false, false, false, false, false,
                false, true, true, true, true, true});
        d3.anadirDia("WEDNESDAY", new boolean[]{true, true, true, true, true,
                false, false, false, false, false, false});
        d3.anadirDia("FRIDAY", new boolean[]{true, true, true, true, true,
                false, false, false, false, false, false});
        doctores.add(d3);

        Doctor d4 = new Doctor("Dr. Four", "104", "Ginecologia");
        d4.anadirDia("TUESDAY", new boolean[]{false, false, false, false, false,
                false, true, true, true, true, true});
        d4.anadirDia("THURSDAY", new boolean[]{false, false, false, false, false,
                false, true, true, true, true, true});
        doctores.add(d4);

        Doctor d5 = new Doctor("Dr. Five", "104", "General");
        d5.anadirDia("WEDNESDAY", new boolean[]{true, true, true, false, false,
                false, false, false, true, true, false});
        d5.anadirDia("THURSDAY", new boolean[]{true, true, true, false, false,
                false, false, false, true, true, false});
        doctores.add(d5);

        Doctor d6 = new Doctor("Dr. Six", "104", "Ginecologia");
        d6.anadirDia("TUESDAY", new boolean[]{false, false, false, false, false,
                false, true, true, true, true, true});
        d6.anadirDia("THURSDAY", new boolean[]{false, false, false, false, false,
                false, true, true, true, true, true});
        doctores.add(d6);

        for (int i = 0; i < doctores.size(); i++) {
            treeEspecialidades.add(doctores.get(i).getEspecialidad());
        }

    }

    private ArrayList<Doctor> filtrarEsp(String e) {

        Log.i(TAG, "filtrarEsp: called i.");

        ArrayList<Doctor> docEsp = new ArrayList<>();
        docEsp.clear();
        for (Doctor doc : doctores) {
            if (doc.getEspecialidad().equals(e)) {
                docEsp.add(doc);
            }
        }
        return docEsp;
    }

    private Doctor findDoctor(String esp) {

        Log.i(TAG, "findDoctor: called i.");

        Doctor doc = null;

        try {
            for (Doctor d : filtroDoctores) {
                if (d.getNombre().equals(esp)) return d;
                Log.i(TAG, "findDoctor: doctor found i.");
            }
        } catch (Exception e) {
            Log.i(TAG, "findDoctor: doctor not found i.");
        }

        return doc;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), "Selected User: " +
                especialidades.get(position), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i(TAG, "onNothingSelected: Spinner item not selected i.");
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.ag_esp:
                Log.i(TAG, "onClick: ag_esp");

                Log.i(TAG, "onClick: before selected especialidad = " + ag_esp.getSelectedItem().toString() + " i.");

                ag_esp.setOnItemSelectedListener(this);
                Log.i(TAG, "onClick: after selected especialidad = " + ag_esp.getSelectedItem().toString() + " i.");

                break;

            case R.id.ag_doctor:
                Log.i(TAG, "onClick: menu_empty. Close menu i.");
                break;
        }
    }
}
