package me.sonam.catalog.repo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * General name for a external resource that a application may
 * interact with for data storage like a postgresdb, kafka,
 * and redis cacge.
 */

public class Component implements Persistable<UUID> {

    @Id
    private UUID id;

    private String name;

    private UUID parentId;

    @Transient
    private Component parent;

    private LocalDate created;

    @Transient
    private boolean isNew;

    public Component() {
    }

    public Component(String name, UUID parentId) {
        this.name = name;
        this.parentId = parentId;
        this.created = LocalDate.now();
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public String getName() {
        return name;
    }

    public UUID getParentId() {
        return parentId;
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public void setIsNew(boolean val) {
        this.isNew = val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component store = (Component) o;
        return Objects.equals(id, store.id) &&
                Objects.equals(name, store .name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Component{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", isNew=" + isNew +
                '}';
    }
}
