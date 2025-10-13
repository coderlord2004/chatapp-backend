package com.group4.chatapp.services;

import com.group4.chatapp.dtos.ReactionDto;
import com.group4.chatapp.models.*;
import com.group4.chatapp.models.Enum.NotificationType;
import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.repositories.ContentRepository;
import com.group4.chatapp.repositories.NotificationRepository;
import com.group4.chatapp.repositories.ReactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class ReactionService {
    private UserService userService;
    private ReactionRepository reactionRepository;
    private ContentRepository contentRepository;
    private TargetResolverService targetResolverService;
    private NotificationService notificationService;

    public void saveReaction(ReactionDto reactionDto) {
        User authUser = userService.getUserOrThrows();

        Long targetId = reactionDto.getTargetId();
        TargetType targetType = reactionDto.getTargetType();
        ReactionType reactionType = reactionDto.getReactionType();

        Reaction reaction = reactionRepository.findByUserIdAndTargetId(authUser.getId(), targetId, targetType);

        if (reaction != null) {
            if (reaction.getReactionType() == reactionType) {
                reactionRepository.delete(reaction);
                contentRepository.decreaseReactions(targetId);
            } else {
                reaction.setReactionType(reactionType);
                reactionRepository.save(reaction);
            }
        } else {
            reaction = Reaction.builder()
                    .targetId(targetId)
                    .targetType(targetType)
                    .reactionType(reactionType)
                    .user(authUser)
                    .build();
            reactionRepository.save(reaction);
            contentRepository.increaseReactions(reaction.getTargetId());

            Notification notification = Notification.builder()
                    .title("Bày tỏ cảm xúc mới")
                    .content(authUser.getUsername() + " đã bình luận vào 1 bài viết của bạn")
                    .type(NotificationType.COMMENT)
                    .targetId(targetId)
                    .targetType(targetType)
                    .build();
            notificationService.notifyAndCreateToUser(
                    authUser,
                    targetResolverService.getAuthor(targetId, targetType),
                    notification
            );
        }
    }

}
