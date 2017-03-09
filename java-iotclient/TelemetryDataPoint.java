import com.google.gson.Gson;


class TelemetryDataPoint {

    public String deviceId;
    public double windSpeed;

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
