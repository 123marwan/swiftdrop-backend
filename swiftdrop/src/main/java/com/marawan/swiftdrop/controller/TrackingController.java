package com.marawan.swiftdrop.controller;

import com.marawan.swiftdrop.entity.DeliveryLocation;
import com.marawan.swiftdrop.repository.DeliveryLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class TrackingController {

    @Autowired
    private DeliveryLocationRepository locationRepository;

    // السائق يرسل الإحداثيات هنا: /app/update-location
    @MessageMapping("/update-location")
    @SendTo("/topic/live-tracking") // يتم بث الإحداثيات لكل العملاء المستمعين لهذه القناة
    public DeliveryLocation updateDriverLocation(DeliveryLocation location) {

        // اختياري: يمكننا حفظ الموقع الأخير في الداتابيز لتحديث السجل
        if (location.getDriver() != null && location.getDriver().getId() != null) {
            // حفظ أو تحديث الموقع في قاعدة البيانات
            locationRepository.save(location);
        }

        // إعادة إرسال الإحداثيات فوراً لكل المتصلين بالـ WebSocket
        return location;
    }
}