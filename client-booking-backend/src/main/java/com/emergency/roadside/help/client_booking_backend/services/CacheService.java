package com.emergency.roadside.help.client_booking_backend.services;

import com.emergency.roadside.help.client_booking_backend.model.booking.BookingRequest;
import com.emergency.roadside.help.client_booking_backend.model.booking.BookingStatusResponse;
import com.emergency.roadside.help.client_booking_backend.model.client.LocalDateTimeDeserializer2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CacheService {
    private final CacheManager cacheManager;
    private final GsonBuilder gsonBuilder;

    public Gson getCustomGson(){
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer2());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        return gson;
    }

    public Optional<BookingStatusResponse> getBookingFromCache(String bookingId){
        Cache cache = cacheManager.getCache("booking");
        Cache.ValueWrapper valueWrapper = cache.get(bookingId);
        if (valueWrapper == null)
            return Optional.empty();
        else{
            Object cachedValue = valueWrapper.get();

            BookingStatusResponse bookingStatus = getCustomGson().fromJson(getCustomGson().toJson(cachedValue), BookingStatusResponse.class);

            //putting again to reset TTL
            cache.put(bookingId,bookingStatus);
            return Optional.ofNullable(bookingStatus);
        }
    }

    public void putBookingToCache(BookingStatusResponse bookingStatusResponse){
        Cache cache = cacheManager.getCache("booking");
        cache.put(bookingStatusResponse.getBookingId(),bookingStatusResponse);
    }

}
