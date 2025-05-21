class ReservaRequest {
    private String name;
    private String date;       // "YYYY-MM-DD"
    private String time;       // "HH:MM"
    private int numPeople;
    private int userId;
    private String sitioName;  // nombre_sitio

    public ReservaRequest(String name, String date, String time, int numPeople, int userId, String sitioName) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.numPeople = numPeople;
        this.userId = userId;
        this.sitioName = sitioName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSitioName() {
        return sitioName;
    }

    public void setSitioName(String sitioName) {
        this.sitioName = sitioName;
    }
}