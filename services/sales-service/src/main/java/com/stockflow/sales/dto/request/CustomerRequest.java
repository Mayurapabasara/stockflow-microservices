package com.stockflow.sales.dto.request;
import java.util.*;
import java.math.BigDecimal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.stockflow.sales.enums.PaymentMethod;
public record CustomerRequest(@NotBlank @Size(max = 160) String name, @Email @Size(max = 254) String email, @Size(max = 40) String phone, @Size(max = 500) String address, @NotNull Boolean active) {}
