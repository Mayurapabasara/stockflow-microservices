package com.stockflow.reporting.controller;
import java.time.Instant;
import com.stockflow.reporting.dto.response.DashboardResponse;
import com.stockflow.reporting.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/reports/dashboard")
public class DashboardController {
    private final DashboardService service;
    public DashboardController(DashboardService service){this.service=service;}
    @GetMapping public DashboardResponse get(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to){return service.get(from,to);}
}
