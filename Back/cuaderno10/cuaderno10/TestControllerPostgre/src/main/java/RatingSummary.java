public class RatingSummary {
    private int totalVotes;
    private double avgRating;

    public RatingSummary(int totalVotes, double avgRating) {
        this.totalVotes = totalVotes;
        this.avgRating  = avgRating;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public double getAvgRating() {
        return avgRating;
    }
}
