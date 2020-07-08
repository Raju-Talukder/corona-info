package com.corona.controllers;

import com.corona.models.LocationStats;
import com.corona.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model){
        List<LocationStats> stats = coronaVirusDataService.getStats();
        int totalCases = stats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = stats.stream().mapToInt(stat -> stat.getDifFromPrevDay()).sum();
        model.addAttribute("allStats",stats);
        model.addAttribute("totalCases",totalCases);
        model.addAttribute("totalNewCases",totalNewCases);
        return "home";
    }
}
