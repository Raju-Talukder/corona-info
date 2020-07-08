package com.corona.services;

import com.corona.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {
    private static String virus_data_url = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> stats = new ArrayList<>();

    public List<LocationStats> getStats() {
        return stats;
    }

    public void setStats(List<LocationStats> stats) {
        this.stats = stats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(virus_data_url)).build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        StringReader reader = new StringReader(response.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            LocationStats locationStats = new LocationStats();
            locationStats.setCountry(record.get("Country/Region"));
            locationStats.setState(record.get("Province/State"));
            int latestCases = Integer.parseInt(record.get(record.size()-1));
            int prevCases = Integer.parseInt(record.get(record.size()-2));
            locationStats.setLatestTotalCases(latestCases);
            locationStats.setDifFromPrevDay(latestCases - prevCases);
            newStats.add(locationStats);
        }
        this.stats=newStats;
    }
}
