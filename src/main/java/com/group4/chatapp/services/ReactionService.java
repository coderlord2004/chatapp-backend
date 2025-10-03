package com.group4.chatapp.services;

import com.group4.chatapp.dtos.ReactionDto;
import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.models.Post;
import com.group4.chatapp.models.Reaction;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ContentRepository;
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

    public void saveReaction(ReactionDto reactionDto) {
        User authUser = userService.getUserOrThrows();

        Reaction reaction = reactionRepository.findByUserIdAndTargetId(authUser.getId(), reactionDto.getTargetId(), reactionDto.getTargetType());

        if (reactionDto.getReactionType() == null) {
            throw new IllegalArgumentException("ReactionType must not be null");
        }

        if (reaction != null) {
            if (reaction.getReactionType() == reactionDto.getReactionType()) {
                reactionRepository.delete(reaction);
                contentRepository.decreaseReactions(reactionDto.getTargetId());
            } else {
                reaction.setReactionType(reactionDto.getReactionType());
                reactionRepository.save(reaction);
            }
        } else {
            reaction = Reaction.builder()
                    .targetId(reactionDto.getTargetId())
                    .targetType(reactionDto.getTargetType())
                    .reactionType(reactionDto.getReactionType())
                    .user(authUser)
                    .build();
            reactionRepository.save(reaction);
            contentRepository.increaseReactions(reaction.getTargetId());
        }
    }

    public List<ReactionType> getTopReactionType(Long postId) {
        return reactionRepository.getTopReactionType(postId, TargetType.POST, PageRequest.of(0, 3));
    }

    public ReactionType getUserReaction(Long postId, Long userId) {
        return reactionRepository.getUserReaction(postId, userId);
    }
}
