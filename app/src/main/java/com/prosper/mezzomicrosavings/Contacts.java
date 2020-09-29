package com.prosper.mezzomicrosavings;

public class Contacts {

    private	int	id;
    private	String name;
    private	String balance;
    private	String phno;

    public Contacts(String name, String balance, String phno) {
        this.name = name;
        this.balance = balance;
        this.phno = phno;
    }

    public Contacts(int id, String name, String balance, String phno) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.phno = phno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }
}
