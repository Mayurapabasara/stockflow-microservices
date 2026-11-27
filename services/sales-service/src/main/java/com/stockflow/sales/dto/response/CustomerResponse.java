package com.stockflow.sales.dto.response;
import java.util.*;
import java.math.BigDecimal;
import java.time.Instant;
import com.stockflow.sales.enums.*;
public record CustomerResponse(UUID id, String name, String email, String phone, String address, boolean active) {}
