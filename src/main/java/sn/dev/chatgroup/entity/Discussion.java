package sn.dev.chatgroup.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Discussion {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "message")
    private String message;
    @Basic
    @Column(name = "date_message")
    private Timestamp dateMessage;
   /* @Basic
    @Column(name = "id_membre")
    private int idMembre;*/

    @ManyToOne
    @JoinColumn(name = "id_membre", referencedColumnName = "id")
    private Membre membre;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDateMessage() {
        return dateMessage;
    }

    public void setDateMessage(Timestamp dateMessage) {
        this.dateMessage = dateMessage;
    }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    public Membre getMembre() {
        return membre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discussion that = (Discussion) o;
        return id == that.id && Objects.equals(message, that.message) && Objects.equals(dateMessage, that.dateMessage) && Objects.equals(membre, that.membre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, dateMessage);
    }
}
