package com.sybiload.recoverx;

public class Recovery
{
    private String brand;
    private String name;
    private String nameCode;
    private String recovery;
    private String recoveryCode;
    private String version;
    private String info;
    private String maintainer;
    private String date;
    private boolean trusted;
    private boolean dev;
    private String link;
    private String command;

    public Recovery(String brand, String name, String nameCode, String recovery, String recoveryCode, String version, String info, String maintainer, String date, boolean trusted, boolean dev, String link, String command)
    {
        this.brand = brand;
        this.name = name;
        this.nameCode = nameCode;
        this.recovery = recovery;
        this.recoveryCode = recoveryCode;
        this.version = version;
        this.info = info;
        this.maintainer = maintainer;
        this.date = date;
        this.trusted = trusted;
        this.dev = dev;
        this.link = link;
        this.command = command;
    }

    public String getBrand()
    {
        return this.brand;
    }

    public String getName()
    {
        return this.name;
    }

    public String getNameCode()
    {
        return this.nameCode;
    }

    public String getRecovery()
    {
        return this.recovery;
    }

    public String getRecoveryCode()
    {
        return this.recoveryCode;
    }

    public String getVersion()
    {
        return this.version;
    }

    public String getInfo()
    {
        return this.info;
    }

    public String getMaintainer()
    {
        return this.maintainer;
    }

    public String getDate()
    {
        return this.date;
    }

    public boolean getTrusted()
    {
        return this.trusted;
    }

    public boolean getDev()
    {
        return this.dev;
    }

    public String getLink()
    {
        return this.link;
    }

    public String getCommand()
    {
        return this.command;
    }
}