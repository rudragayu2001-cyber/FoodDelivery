package org.FoodDelivery.notification;

import org.FoodDelivery.notification.dto.NotificationResponse;
import org.FoodDelivery.security.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Async status fan-out delivered to the calling user.")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    @GetMapping
    @Operation(summary = "[ANY] List my notifications")
    public List<NotificationResponse> list(@RequestParam(defaultValue = "false") boolean unreadOnly) {
        Long userId = currentUserService.requireCurrentUserId();
        return notificationService.listFor(userId, unreadOnly).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "[ANY] Mark a notification read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        Long userId = currentUserService.requireCurrentUserId();
        notificationService.markRead(userId, id);
        return ResponseEntity.noContent().build();
    }
}
