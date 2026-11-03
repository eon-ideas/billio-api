package com.billio.billio_api.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for retrieving currency exchange rates.
 * CORS is handled globally in SecurityConfig.
 */
@RestController
@RequestMapping("/api/currency-exchange-rates")
@Tag(name = "Currency Exchange Rates", description = "Currency exchange rates API")
public class CurrencyExchangeRateResource {

    private final Logger log = LoggerFactory.getLogger(CurrencyExchangeRateResource.class);
    private final RestTemplate restTemplate;

    public CurrencyExchangeRateResource() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * GET /api/currency-exchange-rates : Get exchange rate for a specific currency and date
     *
     * @param date the date in format YYYY-MM-DD
     * @param currency the currency code (e.g., CHF, USD)
     * @return the ResponseEntity with status 200 (OK) and exchange rate data, or appropriate error status
     */
    @Operation(
        summary = "Get currency exchange rate",
        description = "Retrieves the exchange rate for a specific currency on a given date",
        tags = { "Currency Exchange Rates" }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Exchange rate retrieved successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error or error from external service",
            content = @Content
        )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getExchangeRate(
            @Parameter(description = "Date in format YYYY-MM-DD", required = true)
            @RequestParam String date,
            
            @Parameter(description = "Currency code (e.g., CHF, USD)", required = true)
            @RequestParam String currency) {
        
        log.debug("REST request to get exchange rate for currency {} on date {}", currency, date);
        
        try {
            // Call external API
            String apiUrl = String.format("https://api.hnb.hr/tecajn-eur/v3?datum-primjene=%s&valuta=%s", date, currency);
            ExchangeRateData[] response = restTemplate.getForObject(apiUrl, ExchangeRateData[].class);
            
            if (response == null || response.length == 0) {
                log.warn("No exchange rate data found for currency {} on date {}", currency, date);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "No exchange rate data found for the specified currency and date"
                ));
            }
            
            // Prepare response
            ExchangeRateData sourceData = response[0];
            Map<String, Object> result = new HashMap<>();
            result.put("date", date);
            result.put("exchange_rate", sourceData.getSrednji_tecaj());
            result.put("currency", currency);
            result.put("source", sourceData);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error retrieving exchange rate data for currency {} on date {}", currency, date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to retrieve exchange rate data: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Data class representing the exchange rate response from the HNB API
     */
    public static class ExchangeRateData {
        @JsonProperty("broj_tecajnice")
        private String broj_tecajnice;
        
        @JsonProperty("datum_primjene")
        private String datum_primjene;
        
        @JsonProperty("drzava")
        private String drzava;
        
        @JsonProperty("drzava_iso")
        private String drzava_iso;
        
        @JsonProperty("kupovni_tecaj")
        private String kupovni_tecaj;
        
        @JsonProperty("prodajni_tecaj")
        private String prodajni_tecaj;
        
        @JsonProperty("sifra_valute")
        private String sifra_valute;
        
        @JsonProperty("srednji_tecaj")
        private String srednji_tecaj;
        
        @JsonProperty("valuta")
        private String valuta;
        
        public ExchangeRateData() {}

        public String getBroj_tecajnice() {
            return broj_tecajnice;
        }

        public void setBroj_tecajnice(String broj_tecajnice) {
            this.broj_tecajnice = broj_tecajnice;
        }

        public String getDatum_primjene() {
            return datum_primjene;
        }

        public void setDatum_primjene(String datum_primjene) {
            this.datum_primjene = datum_primjene;
        }

        public String getDrzava() {
            return drzava;
        }

        public void setDrzava(String drzava) {
            this.drzava = drzava;
        }

        public String getDrzava_iso() {
            return drzava_iso;
        }

        public void setDrzava_iso(String drzava_iso) {
            this.drzava_iso = drzava_iso;
        }

        public String getKupovni_tecaj() {
            return kupovni_tecaj;
        }

        public void setKupovni_tecaj(String kupovni_tecaj) {
            this.kupovni_tecaj = kupovni_tecaj;
        }

        public String getProdajni_tecaj() {
            return prodajni_tecaj;
        }

        public void setProdajni_tecaj(String prodajni_tecaj) {
            this.prodajni_tecaj = prodajni_tecaj;
        }

        public String getSifra_valute() {
            return sifra_valute;
        }

        public void setSifra_valute(String sifra_valute) {
            this.sifra_valute = sifra_valute;
        }

        public String getSrednji_tecaj() {
            return srednji_tecaj;
        }

        public void setSrednji_tecaj(String srednji_tecaj) {
            this.srednji_tecaj = srednji_tecaj;
        }

        public String getValuta() {
            return valuta;
        }

        public void setValuta(String valuta) {
            this.valuta = valuta;
        }
    }
}
