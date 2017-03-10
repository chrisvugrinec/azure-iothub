import com.google.gson.Gson;

import java.time.LocalDate;


class TelemetryDataPoint {

    public String deviceId;
    public double windSpeed;
    public double powerOutput;
    public String payerId;
    public String eventDate;
    public String eventType;

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
