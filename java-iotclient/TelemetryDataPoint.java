import com.google.gson.Gson;

import java.time.LocalDate;


class TelemetryDataPoint {

    public String deviceId;
    public double windSpeed;
    public String eventDate;
    public String eventType;

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

