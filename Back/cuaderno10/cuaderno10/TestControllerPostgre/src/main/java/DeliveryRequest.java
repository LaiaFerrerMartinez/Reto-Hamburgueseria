public class DeliveryRequest {
    private String name;
    private String address;
    private String phoneNumber;
    private int userId;

    // Constructor usado por Gson
    public DeliveryRequest() {}

    public String getName()       { return name; }
    public String getAddress()    { return address; }
    public String getPhoneNumber(){ return phoneNumber; }
    public int getUserId()        { return userId; }
}
