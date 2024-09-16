package com.example.appkadiwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appkadiwa.util.Message;
import com.example.appkadiwa.util.MessageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class MessageActivity extends AppCompatActivity {

    private ImageView backButton;
    private RecyclerView recyclerView;
    private EditText inputEditText;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private FirebaseFirestore db;
    private CollectionReference messagesCollectionRef;
    private EventListener<QuerySnapshot> messageListener;
    private com.google.firebase.firestore.ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        backButton = findViewById(R.id.back);
        recyclerView = findViewById(R.id.messageRecyclerView);
        inputEditText = findViewById(R.id.messageEditText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);

        db = FirebaseFirestore.getInstance();
        messagesCollectionRef = db.collection("messages");

        messageListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MessageActivity.this, "Failed to listen for messages: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {
                    switch (documentChange.getType()) {
                        case ADDED:
                            // New message added to the collection
                            Message message = documentChange.getDocument().toObject(Message.class);
                            messageList.add(message);
                            messageAdapter.notifyDataSetChanged();
                            break;
                        // Handle other types of changes if needed (e.g., MODIFIED, REMOVED)
                    }
                }
            }
        };

        fetchMessages();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, UserListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = inputEditText.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    Message message = new Message(messageText, System.currentTimeMillis());
                    messagesCollectionRef.add(message)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    inputEditText.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MessageActivity.this, "Failed to store message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void fetchMessages() {
        messagesCollectionRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                Message message = documentChange.getDocument().toObject(Message.class);
                                messageList.add(message);
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MessageActivity.this, "Failed to fetch messages: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenerRegistration = messagesCollectionRef.addSnapshotListener(messageListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}