package com.stockflow.reporting.service;
import java.time.Instant;
import com.stockflow.reporting.dto.response.DashboardResponse;
public interface DashboardService { DashboardResponse get(Instant from,Instant to); }
