package com.stockflow.product.api;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createsReadsAndDeletesProduct() throws Exception {
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Widget",
                                  "sku": "WIDGET-001",
                                  "description": "Sample item",
                                  "price": 12.50,
                                  "quantity": 20,
                                  "category": "Accessories"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku", is("WIDGET-001")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Widget")));

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidProductRequest() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "sku": "WIDGET-INVALID",
                                  "price": 0,
                                  "quantity": -1,
                                  "category": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation failed")));
    }

    @Test
    void rejectsPriceWithMoreThanTwoFractionalDigits() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Widget",
                                  "sku": "WIDGET-PRICE",
                                  "price": 12.501,
                                  "quantity": 1,
                                  "category": "Accessories"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation failed")));
    }
}
