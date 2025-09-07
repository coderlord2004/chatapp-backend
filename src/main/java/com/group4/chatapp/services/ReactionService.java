package com.group4.chatapp.services;

import com.group4.chatapp.dtos.ReactionDto;
import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.models.Reaction;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ReactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class ReactionService {
    private UserService userService;
    private ReactionRepository reactionRepository;

    public void saveReaction(ReactionDto reactionDto) {
        User authUser = userService.getUserOrThrows();

        Reaction reaction = reactionRepository.findByUserIdAndTargetId(authUser.getId(), reactionDto.getTargetId(), reactionDto.getTargetType());
        if (reaction != null) {
            String reactionType = String.valueOf(reaction.getReactionType());
            if (reactionType.equalsIgnoreCase(reactionDto.getReactionType())) {
                reactionRepository.delete(reaction);
            } else {
                reaction.setReactionType(ReactionType.valueOf(reactionDto.getReactionType()));
                reactionRepository.save(reaction);
            }
        } else {
            reaction = Reaction.builder()
                    .targetId(reactionDto.getTargetId())
                    .targetType(reactionDto.getTargetType())
                    .reactionType(ReactionType.valueOf(reactionDto.getReactionType()))
                    .user(authUser)
                    .build();
            reactionRepository.save(reaction);
        }
    }

    public void getTotalReactionsOfPost() {

    }
}
