package com.aslan.personalguardianapp.logic;

import android.os.AsyncTask;
import android.util.Log;

import com.aslan.personalguardianapp.util.Constants;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * WebService client to send HTTP requests.
 * <p/>
 * Created by gobinath on 1/21/16.
 */
public class WebServiceClient {
    private static final String CONTEXT_URL = "http://localhost:8080/personalguardian/service/v1";

    private static final String REGISTRATION_URL = CONTEXT_URL + "/create";

    private static final String CREATE_GUARDIAN_URL = CONTEXT_URL + "/guardian/{user_id}";

    private static final String START_JOURNEY_URL = CONTEXT_URL + "/start/{user_id}";

    private static final String LOCATION_UPDATE_URL = CONTEXT_URL + "/location/{user_id}";


    public void registerUser(OnRequestListener listener, String name, String phoneNumber, String email) {
        new UserRegistrationTask(listener).execute(name, phoneNumber, email);
    }

    public void updateGuardian(OnRequestListener listener, String userID, String name, String phoneNumber, String email) {
        new GuardianRegistrationTask(listener).execute(userID, name, phoneNumber, email);
    }

    public void startJourney(OnRequestListener listener, String userID, Double destLatitude, Double destLongitude) {
        new StartJourneyTask(listener).execute(userID, destLatitude.toString(), destLongitude.toString());
    }

    public void updateLocation(OnRequestListener listener, String userID, Double destLatitude, Double destLongitude) {
        new LocationUpdateTask(listener).execute(userID, destLatitude.toString(), destLongitude.toString());
    }

    public static interface OnRequestListener {
        public void onComplete(boolean success);
    }

    private abstract class WebServiceTask extends AsyncTask<String, Void, Boolean> {
        private OnRequestListener listener;

        public WebServiceTask(OnRequestListener listener) {
            this.listener = listener;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (listener != null) {
                listener.onComplete(result);
            }
        }
    }

    private class UserRegistrationTask extends WebServiceTask {

        public UserRegistrationTask(OnRequestListener listener) {
            super(listener);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // Create HttpHeaders
                HttpHeaders requestHeaders = new HttpHeaders();

                // Set the content type
                requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                // Create the parameters
                MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
                formData.add("name", params[0]);
                formData.add("phoneNumber", params[1]);
                formData.add("email", params[2]);


                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
                        formData, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate(true);

                // Make the network request, posting the message, expecting a String in response from the server
                ResponseEntity response = restTemplate.postForEntity(REGISTRATION_URL, requestEntity, ResponseEntity.class);

                if (response.getStatusCode().value() == Constants.HTTP_CREATED) {
                    // Return the response body to display to the user
                    return true;
                }
            } catch (Exception e) {
                Log.e(WebServiceClient.class.getName(), e.getMessage(), e);
            }

            return false;
        }
    }


    private class GuardianRegistrationTask extends WebServiceTask {
        public GuardianRegistrationTask(OnRequestListener listener) {
            super(listener);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // Create HttpHeaders
                HttpHeaders requestHeaders = new HttpHeaders();

                // Set the content type
                requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                // Create the parameters
                MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
                formData.add("name", params[1]);
                formData.add("phoneNumber", params[2]);
                formData.add("email", params[3]);
                formData.add("registered", false);


                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
                        formData, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate(true);

                // Make the network request, posting the message, expecting a String in response from the server
                restTemplate.put(CREATE_GUARDIAN_URL, requestEntity, params[0]);

                return true;
            } catch (Exception e) {
                Log.e(WebServiceClient.class.getName(), e.getMessage(), e);
            }

            return false;
        }
    }

    private class StartJourneyTask extends WebServiceTask {
        public StartJourneyTask(OnRequestListener listener) {
            super(listener);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // Create HttpHeaders
                HttpHeaders requestHeaders = new HttpHeaders();

                // Set the content type
                requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                // Create the parameters
                MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
                formData.add("latitude", Double.valueOf(params[1]));
                formData.add("longitude", Double.valueOf(params[2]));


                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
                        formData, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate(true);

                // Make the network request, posting the message, expecting a String in response from the server
                ResponseEntity response = restTemplate.postForEntity(START_JOURNEY_URL, requestEntity, ResponseEntity.class, params[0]);

                if (response.getStatusCode().value() == Constants.HTTP_OK) {
                    // Return the response body to display to the user
                    return true;
                }
            } catch (Exception e) {
                Log.e(WebServiceClient.class.getName(), e.getMessage(), e);
            }

            return false;
        }
    }

    private class LocationUpdateTask extends WebServiceTask {
        public LocationUpdateTask(OnRequestListener listener) {
            super(listener);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // Create HttpHeaders
                HttpHeaders requestHeaders = new HttpHeaders();

                // Set the content type
                requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                // Create the parameters
                MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
                formData.add("latitude", Double.valueOf(params[1]));
                formData.add("longitude", Double.valueOf(params[2]));


                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
                        formData, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate(true);

                // Make the network request, posting the message, expecting a String in response from the server
                ResponseEntity response = restTemplate.postForEntity(LOCATION_UPDATE_URL, requestEntity, ResponseEntity.class, params[0]);

                if (response.getStatusCode().value() == Constants.HTTP_OK) {
                    // Return the response body to display to the user
                    return true;
                }
            } catch (Exception e) {
                Log.e(WebServiceClient.class.getName(), e.getMessage(), e);
            }

            return false;
        }
    }
}
