package DAO;

public class SceduleModel {
    private String group, room,  type, subject;
    private int weekday, classN;
    public SceduleModel(String group,int weekday, String room, int classN, String type, String subject){
       this.classN = classN;
       this.group = group;
       this.room = room;
       this.subject = subject;
       this.type = type;
       this.weekday = weekday;
    }
    public SceduleModel(){

    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public int getWeekday() {
        return weekday;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getClassN() {
        return classN;
    }

    public void setClassN(int classN) {
        this.classN = classN;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }



}
