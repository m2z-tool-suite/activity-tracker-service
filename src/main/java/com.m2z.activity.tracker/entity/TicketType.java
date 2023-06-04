package com.m2z.activity.tracker.entity;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Entity
@Getter
@Setter
public class TicketType {
    @Id
    private String id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "type")
    private List<Ticket> tickets;

    public TicketType(Map<String, Object> issuetype) {
        this.id = (String) issuetype.get("id");
        this.name = (String) issuetype.get("name");
    }

    public TicketType() {
        this.id = new ULID().nextULID();
        this.name = "Task";
        this.description = "Task description";
    }
}
