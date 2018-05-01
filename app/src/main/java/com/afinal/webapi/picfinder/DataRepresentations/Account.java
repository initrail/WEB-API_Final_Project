package com.afinal.webapi.picfinder.DataRepresentations;

public class Account {
    private String email;
    private String password;
    private String username;
    private String passwordCheck;

    public String getPasswordCheck() {
        return passwordCheck;
    }

    public void setPasswordCheck(String passwordCheck) {
        this.passwordCheck = passwordCheck;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Account(){

    }
    public String getEmail(){
        return email;
    }
    public String getUserName(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setUserName(String name){
        this.username = name;
    }
    public void setPassword(String password){
        this.password = password;
    }
}
