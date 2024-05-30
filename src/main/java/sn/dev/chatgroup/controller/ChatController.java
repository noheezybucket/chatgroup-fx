package sn.dev.chatgroup.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sn.dev.chatgroup.entity.Discussion;
import sn.dev.chatgroup.entity.Membre;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Scanner;

public class ChatController {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private boolean isUserIdentified = false;

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
    EntityManager em = emf.createEntityManager();
    EntityTransaction trans = em.getTransaction();

    public void saveMembre(String username) {
        em.getTransaction().begin();
        Membre membre = new Membre();
        membre.setUsername(username);
        membre.setDateAdhesion(new Timestamp(System.currentTimeMillis()));
        em.persist(membre);
        em.getTransaction().commit();
    }

    public void saveDiscussion(String message, Membre membre) {
        em.getTransaction().begin();
        Discussion discussion = new Discussion();
        discussion.setMessage(message);
        discussion.setDateMessage(new Timestamp(System.currentTimeMillis()));
        discussion.setMembre(membre);
        em.persist(discussion);
        em.getTransaction().commit();
    }

    public Membre getMembreByUsername(String username) {
        List<Membre> membres = em.createQuery("SELECT m FROM Membre m WHERE m.username = :username", Membre.class)
                .setParameter("username", username)
                .getResultList();
        if (!membres.isEmpty()) {
            return membres.get(0);
        } else {
            // Handle the case where the user is not found
            return null;
        }
    }

    public List<Membre> getAllMembres() {
        return em.createQuery("SELECT m FROM Membre m", Membre.class).getResultList();
    }

    public List<Discussion> getAllDiscussions() {
        return em.createQuery("SELECT d FROM Discussion d", Discussion.class).getResultList();
    }

    @FXML
    private TextArea txtChat;

    @FXML
    private TextField txtDiscussion;

    @FXML
    private TextField txtUsername;

    @FXML
    public void initialize() {
        txtDiscussion.setDisable(true);
        try {
            this.socket = new Socket("localhost", 1234);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.listenForMessage();
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        //this.listenForMessage();
    }

    @FXML
    void seConnecter() {
        String username = txtUsername.getText().trim();

        if (!username.isEmpty()) {
            saveMembre(username);
            this.username = username;
            isUserIdentified = true;
            txtUsername.setDisable(true);
            txtDiscussion.setDisable(false);

            try {
                bufferedWriter.write(username);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        } else {
            showAlert("Erreur", "Veuillez vous identifier");
        }
    }

    @FXML
    void envoyerDiscussion(ActionEvent event) {
        sendMessage(txtDiscussion.getText());
        if(!txtDiscussion.getText().isEmpty()) {
            Membre membre = getMembreByUsername(username);
            saveDiscussion(txtDiscussion.getText(), membre);
        } else {
            showAlert("Erreur", "Veuillez saisir un message.");
        }
        txtDiscussion.clear();
    }

    public void sendMessage(String message) {
        try {
                String messageToSend = username + " : " + message;
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            try {
                String msgFromGroupChat;
                while ((msgFromGroupChat = bufferedReader.readLine()) != null) {
                    final String message = msgFromGroupChat;
                    Platform.runLater(() -> txtChat.appendText(message + "\n"));
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                e.printStackTrace();
            }
        }).start();
    }

    public void displayMessage(String message) {
        Platform.runLater(() -> txtChat.appendText(message + "\n"));
    }



    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
