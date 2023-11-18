import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CurrencyConverter {
    // Get your API key from https://www.exchangerate-api.com/
    private static final String API_KEY = "a6c2dae643f3c8895d6e1fb4";

    // API URL for getting latest exchange rates
    private static final String
            API_URL = "https://v6.exchangerate-api.com/v6/a6c2dae643f3c8895d6e1fb4/latest/USD";

    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);

        System.out.println("Welcome to Currency Converter!");

        System.out.println("Enter the source currency code (e.g., USD):");
        String sourceCurrency = userInput.next().toUpperCase();
        // Convert the source currency to uppercase to match the API response

        System.out.println("Enter the target currency code (e.g., EUR):");
        String targetCurrency = userInput.next().toUpperCase();
        // Convert the target currency to uppercase to match the API response

        System.out.println("Enter the amount to convert:");
        double amount = userInput.nextDouble();
        // Get the amount to convert from the user

        // Get the exchange rate for the given currencies and convert the amount
        double exchangeRate = getExchangeRate(sourceCurrency, targetCurrency);

        // Display the converted amount if the exchange rate is valid
        if (exchangeRate != -1) {

            // Round the converted amount to 2 decimal places
            double convertedAmount = amount * exchangeRate;
            System.out.printf("%.2f %s is equal to %.2f %s\n",
                    amount, sourceCurrency, convertedAmount, targetCurrency);
            // Display the converted amount to 2 decimal places
        } else {
            System.out.println("Unable to fetch exchange rates. Please try again later.");
        }
        userInput.close(); // Close the scanner
    }

    // Get the exchange rate for the given currencies
    private static double getExchangeRate(String sourceCurrency, String targetCurrency) {
        try {
            String jsonResponse = getString(); // Get the JSON response from the API

            double sourceRate = getRateFromJson(jsonResponse, sourceCurrency);
            // Get the exchange rate for the source currency

            double targetRate = getRateFromJson(jsonResponse, targetCurrency);
            // Get the exchange rate for the target currency

            // Return the exchange rate if both rates are valid
            if (sourceRate != -1 && targetRate != -1) {
                return targetRate / sourceRate; // Return the exchange rate
            } else {
                System.out.println("Invalid currency codes.");
            }

        } catch (IOException e) {
            System.out.println("Error fetching exchange rates. " +
                    "Please check your internet connection.");
            e.printStackTrace(); // Print the exception stack trace
        }

        return -1; // Return -1 if exchange rate is invalid
    }

    // Get the JSON response from the API as a string
    private static String getString() throws IOException {
        // Create a URL object from the API URL string above
        URL url = new URL(API_URL + "?apikey=" + API_KEY);

        // Open a connection to the API URL above and set the request method to GET
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET"); // Set the request method to GET

        // Create a BufferedReader to read the response from the API and store it in a StringBuilder
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder(); // Store the response here
        String line; // Store each line of the response here

        // Read each line of the response and append it to the StringBuilder
        while ((line = reader.readLine()) != null) {
            response.append(line); // Append the line to the StringBuilder
        }
        reader.close(); // Close the BufferedReader after reading the response

        // Parse JSON response to get exchange rates
        return response.toString();
    }

    // Get the exchange rate for the given currency code from the JSON response
    private static double getRateFromJson(String jsonResponse, String currencyCode) {
        // Search for the currency code in the JSON response
        String searchString = "\"" + currencyCode + "\":";

        // Get the index of the search string in the JSON response string
        int index = jsonResponse.indexOf(searchString);

        // If the search string is found in the JSON response string
        if (index != -1) {
            // Get the index of the exchange rate in the JSON response string
            int startIndex = index + searchString.length();

            // Get the exchange rate from the JSON response string
            int endIndex = jsonResponse.indexOf(",", startIndex);

            // Remove any leading or trailing whitespace from the exchange rate string
            String rateString = jsonResponse.substring(startIndex, endIndex).trim();
            return Double.parseDouble(rateString); // Return the exchange rate
        }
        return -1; // Return -1 if exchange rate is invalid
    }
}
