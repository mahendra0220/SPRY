package com.maahi.spry.Services;

import com.maahi.spry.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Async("notificationExecutor")
    public void notifyWishlistedUsers(Book book) {
        if (book == null || book.getWishListUsers() == null)
            return;

        for (String userId : book.getWishListUsers()) {
            log.info("Notification prepared for user_id: {}. Book [{}] is now available.", userId, book.getBookName());
            // In real application we will sent notification to user through mail or sms.
        }
    }
}

