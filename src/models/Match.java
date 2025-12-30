package models;

public class Match {
    private String codeMatch;
    private String equipeA;
    private String equipeB;
    private String stade;
    private String date;// date (DD/MM/YYYY)
    private String heure; // heure (HH:MM)
    private int importance; // 1: faible, 2: moyenne, 3: élevée

    public Match(String codeMatch, String equipeA, String equipeB, String stade, String date, String heure, int importance) {
        this.codeMatch = codeMatch;
        this.equipeA = equipeA;
        this.equipeB = equipeB;
        this.stade = stade;
        this.date = date;
        this.heure = heure;
        this.importance = importance;
    }

    public String getCodeMatch() {
        return codeMatch;
    } 

    public String getEquipeA() {
        return equipeA;
    }

    public String getEquipeB() {
        return equipeB;
    }

    public String getStade() {
        return stade;
    }

    public String getDate() {
        return date;
    }

    public String getHeure() {
        return heure;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public void setStade(String stade) {
        this.stade = stade;
    }

    public void setEquipeA(String equipeA) {
        this.equipeA = equipeA;
    }   

    public void setEquipeB(String equipeB) {
        this.equipeB = equipeB;
    }

    public void setCodeMatch(String codeMatch) {
        this.codeMatch = codeMatch;
    }

    

    @Override
    public String toString() {
        return "\tMatch [codeMatch=" + codeMatch + ", equipeA=" + equipeA + ", equipeB=" + equipeB + ", stade=" + stade
                + ", date=" + date + ", heure=" + heure + ", importance=" + importance + "]";
    }


}
