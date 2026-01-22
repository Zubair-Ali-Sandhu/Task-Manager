package me.zubair.taskmanager.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Client for fetching motivational quotes from an API
 */
public class QuoteApiClient {
    private static final String TAG = "QuoteApiClient";
    private static final String API_URL = "https://api.quotable.io/quotes/random?tags=motivational";
    
    private final ExecutorService executorService;
    private final Handler mainHandler;
    
    public QuoteApiClient() {
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Callback interface for quote fetching operations
     */
    public interface QuoteCallback {
        void onQuoteFetched(String quote, String author);
        void onError(String errorMessage);
    }
    
    /**
     * Fetches a random motivational quote asynchronously
     * @param callback The callback to handle the result
     */
    public void fetchRandomQuote(QuoteCallback callback) {
        executorService.execute(() -> {
            try {
                Quote quote = fetchQuoteFromApi();
                mainHandler.post(() -> callback.onQuoteFetched(quote.getContent(), quote.getAuthor()));
            } catch (UnknownHostException e) {
                Log.e(TAG, "Network connectivity issue", e);
                mainHandler.post(() -> callback.onError("No network connection"));
            } catch (IOException e) {
                Log.e(TAG, "I/O error fetching quote", e);
                mainHandler.post(() -> callback.onError("Connection error: " + e.getMessage()));
            } catch (JSONException e) {
                Log.e(TAG, "JSON parsing error", e);
                mainHandler.post(() -> callback.onError("Error parsing response"));
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error fetching quote", e);
                mainHandler.post(() -> callback.onError("Unexpected error: " + e.getMessage()));
            }
        });
    }
    
    /**
     * Fetches a quote from the API synchronously
     * @return Quote object containing content and author
     * @throws IOException if there's a network error
     * @throws JSONException if there's an error parsing the response
     */
    private Quote fetchQuoteFromApi() throws IOException, JSONException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        
        try {
            URL url = new URL(API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);  // Reduced timeout for faster feedback
            connection.setReadTimeout(5000);     // Reduced timeout for faster feedback
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            JSONArray jsonArray = new JSONArray(response.toString());
            if (jsonArray.length() > 0) {
                JSONObject quoteObject = jsonArray.getJSONObject(0);
                String content = quoteObject.getString("content");
                String author = quoteObject.getString("author");
                return new Quote(content, author);
            } else {
                return getDefaultQuote();
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing reader", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    private Quote getDefaultQuote() {
        return new Quote(
                "The secret of getting ahead is getting started.",
                "Mark Twain");
    }
    
    /**
     * Simple class to hold quote data
     */
    public static class Quote {
        private final String content;
        private final String author;
        
        public Quote(String content, String author) {
            this.content = content;
            this.author = author;
        }
        
        public String getContent() {
            return content;
        }
        
        public String getAuthor() {
            return author;
        }
        
        @Override
        public String toString() {
            return "\"" + content + "\" - " + author;
        }
    }
}
