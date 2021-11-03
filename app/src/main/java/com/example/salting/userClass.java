package com.example.salting;

public class userClass {
    String name;
    String email;
    String friends;
    int shakeCount;
    String profPic;
    String regDate;

    public userClass(String name,String email, String friends, int shakeCount,String profPic, String regDate) {
        this.name = name;
        this.email = email;
        this.friends = friends;
        this.shakeCount = shakeCount;
        this.profPic = profPic;
        this.regDate = regDate;
    }

    public userClass(){
        name = "";
        email = "";
        friends = "";
        shakeCount = -1;
        profPic = "";
        regDate = "";
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public int getShakeCount() {
        return shakeCount;
    }

    public void setShakeCount(int shakeCount) {
        this.shakeCount = shakeCount;
    }

    public String getProfPic() { return profPic; }

    public void setProfPic(String profPic) { this.profPic = profPic; }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

}
