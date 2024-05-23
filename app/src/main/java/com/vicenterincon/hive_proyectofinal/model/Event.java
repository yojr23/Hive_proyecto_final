package com.vicenterincon.hive_proyectofinal.model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Event {
    private String category;
    private DocumentReference creator;
    @ServerTimestamp
    private Date date;
    private String description;
    private int duration;
    private String id;
    private String image;
    private String name;
    private int numParticipants;
    private List<DocumentReference> participants;
    private String place;
    private boolean state;

    // No-argument constructor
    public Event() {
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public DocumentReference getCreator() {
        return creator;
    }

    public void setCreator(DocumentReference creator) {
        this.creator = creator;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumParticipants() {
        return numParticipants;
    }

    public void setNumParticipants(int numParticipants) {
        this.numParticipants = numParticipants;
    }

    public List<DocumentReference> getParticipants() {
        return participants;
    }

    public void setParticipants(List<DocumentReference> participants) {
        this.participants = participants;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    // Override equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return duration == event.duration &&
                numParticipants == event.numParticipants &&
                state == event.state &&
                Objects.equals(category, event.category) &&
                Objects.equals(creator, event.creator) &&
                Objects.equals(date, event.date) &&
                Objects.equals(description, event.description) &&
                Objects.equals(id, event.id) &&
                Objects.equals(image, event.image) &&
                Objects.equals(name, event.name) &&
                Objects.equals(participants, event.participants) &&
                Objects.equals(place, event.place);
    }

    // Override hashCode
    @Override
    public int hashCode() {
        return Objects.hash(category, creator, date, description, duration, id, image, name, numParticipants, participants, place, state);
    }
}
