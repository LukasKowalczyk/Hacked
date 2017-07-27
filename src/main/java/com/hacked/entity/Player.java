package com.hacked.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author pd06286
 */
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Role role;

    private String name;

    private String gameId;

    private boolean ready;

    private boolean heacked;

    private boolean deaktivatet;

    /**
     * @return the heacked
     */
    public boolean isHeacked() {
        return heacked;
    }

    /**
     * @param heacked the heacked to set
     */
    public void setHeacked(boolean heacked) {
        if (heacked) {
            deaktivatet = true;
        }
        this.heacked = heacked;
    }

    /**
     * @return the deaktivatet
     */
    public boolean isDeaktivatet() {
        return deaktivatet;
    }

    /**
     * @param deaktivatet the deaktivatet to set
     */
    public void setDeaktivatet(boolean deaktivatet) {
        this.deaktivatet = deaktivatet;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the ready
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * @param ready the ready to set
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * @return the gameId
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * @param gameId the gameId to set
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

}
