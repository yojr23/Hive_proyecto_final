package com.vicenterincon.hive_proyectofinal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.vicenterincon.hive_proyectofinal.adapters.SessionManager;
import com.vicenterincon.hive_proyectofinal.model.Event;
import com.vicenterincon.hive_proyectofinal.model.UserSession;
import com.vicenterincon.hive_proyectofinal.utils.EventCreationHelper;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

public class EventCreationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_creation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventCreationHelper eventCreationHelper = new EventCreationHelper();
        SessionManager session = new SessionManager(requireContext());
        Button buttonCreateEvent = getView().findViewById(R.id.submitButton);
        buttonCreateEvent.setOnClickListener(v -> {
            String name = ((EditText) getView().findViewById(R.id.inputBox)).getText().toString();
            String place = ((EditText) getView().findViewById(R.id.inputBoxLugar)).getText().toString();
            DatePicker calendarDate = getView().findViewById(R.id.datePicker);
            int year = calendarDate.getYear();
            int month = calendarDate.getMonth() + 1;
            int dayOfMonth = calendarDate.getDayOfMonth();
            String formattedDate = String.format("%04d-%02d-%02d", year, month, dayOfMonth);
            String description = ((EditText) getView().findViewById(R.id.textBoxDescription)).getText().toString();
            String num_participants = ((EditText) getView().findViewById(R.id.inputBoxParticipant)).getText().toString();
            String category = ((Spinner) getView().findViewById(R.id.spinner1)).getSelectedItem().toString();

            switch (category) {
                case "Académico":
                    category = "ACADEMIC";
                    break;
                case "Deportivo":
                    category = "SPORTS";
                    break;
                case "Cultural":
                    category = "CULTURAL";
                    break;
                case "Entretenimiento":
                    category = "ENTERTAINMENT";
                    break;
                case "Otros":
                    category = "OTHER";
                    break;
            }

            boolean state = true;
            String duration = ((EditText) getView().findViewById(R.id.inputBoxDuration)).getText().toString();
            String creador = session.getUserSession().getUserId().toString();
            List<String> tags = Arrays.asList(((EditText) getView().findViewById(R.id.textBox2)).getText().toString().split(" "));
            List<String> links = Arrays.asList(((EditText) getView().findViewById(R.id.textBox)).getText().toString().split(" "));

            if (name.isEmpty()) {
                ((EditText) getView().findViewById(R.id.inputBox)).setError(requireContext().getString(R.string.error_nombre_evento));
                getView().findViewById(R.id.inputBox).requestFocus();
                return;
            }
            if (place.isEmpty()) {
                ((EditText) getView().findViewById(R.id.inputBoxLugar)).setError(requireContext().getString(R.string.error_lugar_evento));
                getView().findViewById(R.id.inputBoxLugar).requestFocus();
                return;
            }
            if (description.isEmpty()) {
                ((EditText) getView().findViewById(R.id.textBoxDescription)).setError(requireContext().getString(R.string.error_descripcion_evento));
                getView().findViewById(R.id.textBoxDescription).requestFocus();
                return;
            }
            if (num_participants.isEmpty()) {
                ((EditText) getView().findViewById(R.id.inputBoxParticipant)).setError(requireContext().getString(R.string.error_participantes_evento));
                getView().findViewById(R.id.inputBoxParticipant).requestFocus();
                return;
            }
            if (duration.isEmpty()) {
                ((EditText) getView().findViewById(R.id.inputBoxDuration)).setError(requireContext().getString(R.string.error_duracion_evento));
                getView().findViewById(R.id.inputBoxDuration).requestFocus();
                return;
            }
            if (!links.isEmpty() && links.stream().anyMatch(link -> !android.util.Patterns.WEB_URL.matcher(link).matches())) {
                ((EditText) getView().findViewById(R.id.textBox)).setError(requireContext().getString(R.string.error_enlaces_evento));
                getView().findViewById(R.id.textBox).requestFocus();
                return;
            }

            if (name.isEmpty() || place.isEmpty() || formattedDate.isEmpty() || description.isEmpty() || num_participants.isEmpty() || category.isEmpty() || duration.isEmpty() || creador.isEmpty()) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.error_registro_evento), Toast.LENGTH_SHORT).show();
                return;
            }

            // New date with the formattedDate
            Date date = Date.valueOf(formattedDate);

            Event event = new Event(category, date, description, Integer.valueOf(duration), "", "", name, Integer.valueOf(num_participants), place, state);

            UserSession userSession = session.getUserSession();

            eventCreationHelper.createEvent(event, userSession, new EventCreationHelper.CreateEventCallback() {
                @Override
                public void onCreateEventSuccess() {
                    Toast.makeText(requireContext(), getString(R.string.evento_registrado), Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomePageFragment())
                            .commit();
                }

                @Override
                public void onCreateEventFailure() {
                    Toast.makeText(requireContext(), requireContext().getString(R.string.error_registro_evento), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
