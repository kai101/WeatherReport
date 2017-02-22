# Android Weather App

An Android weather app to report the weather in response to voice input. - GW test.

## Features.
1. Push-to-talk and voice recognition.
2. Simple keyword triggered for "weather" e.g. "How is the weather".
3. Device location detection, report the weather on the location of the device.
4. Report today's and tomorrow's forcast weather.
5. Visual and voice mode of report for user convenience.

## Technical integration.
1. VoiceRecognizer as recognization for voice input.
2. Google play Service for device location detection.
3. OpenWeatherMap API integration to get weather report base on latitude and longitude.
4. Permissions configuration on RECORD_AUDIO, INTERNET, and ACCESS_COARSE_LOCATION.


## Production code base.
1. Seperation of important features by class in seperate class file for better abstraction. 
2. Good Architecture for future extension with proper seperation of functions and classes.
3. Commenting on MainActivity public functions for easy reference on the dependent services.

## Known Limitation

Very basic trigger word "weather" to activate weather report.

## Future
1. 2 seperate interface for the activity to voice recognizer and weather report needed to prevent breaking on dangerous casting on "MainActivity".
2. Seperation of TTS and Google Play Service will be helpful to keep the MainActivity clean on managing the UI.