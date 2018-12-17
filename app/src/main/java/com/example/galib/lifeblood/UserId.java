package com.example.galib.lifeblood;

import com.google.firebase.firestore.IgnoreExtraProperties;

import io.reactivex.annotations.NonNull;
@IgnoreExtraProperties
public class UserId {

    public String userId;

    public <T extends UserId> T withId(@NonNull final String id) {
        this.userId = id;
        return (T) this;
    }
}
