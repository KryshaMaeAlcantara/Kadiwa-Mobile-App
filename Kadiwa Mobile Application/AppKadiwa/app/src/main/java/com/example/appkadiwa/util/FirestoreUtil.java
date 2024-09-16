package com.example.appkadiwa.util;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreUtil {
    private static final String USERS_COLLECTION = "users";
    private static final String MESSAGES_COLLECTION = "messages";

    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static CollectionReference getUsersCollection() {
        return firebaseFirestore.collection(USERS_COLLECTION);
    }

    public static CollectionReference getMessagesCollection() {
        return firebaseFirestore.collection(MESSAGES_COLLECTION);
    }
}
