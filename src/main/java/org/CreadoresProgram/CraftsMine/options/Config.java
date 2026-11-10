/*
 * Copyright (c) 2021 BarrelMC Team
 * This project is licensed under the MIT License
 */

package org.CreadoresProgram.CraftsMine.options;

import lombok.Getter;
import lombok.Setter;

public class Config {

    @Setter
    @Getter
    public String bindAddress;

    @Setter
    @Getter
    public Integer port;

    @Setter
    @Getter
    public String motd;

    @Setter
    @Getter
    public String submotd;

    @Setter
    @Getter
    public String bedrockAddress;

    @Setter
    @Getter
    public Integer bedrockPort;
    
    @Setter
    @Getter
    public Integer maxplayers;
    
    @Setter
    @Getter
    public String shutdownMessage;

    @Getter
    public boolean debug;

    @Getter
    public boolean gcCollectionDiconnectPlayer;

    @Getter
    public boolean consoleLog;
    
    @Getter
    public boolean gcCollectionAuto;

    @Getter
    public Integer gcCollectionTime;

    @Getter
    public String langPlayer;

    @Getter
    public boolean synchServer;

    @Getter
    public Integer timeSynchServer;

    // versionBedrockServer removed: downstream is MCPE 1.1 (protocol v113)
    // only, hard-wired in Server.java. No longer configurable.
}
