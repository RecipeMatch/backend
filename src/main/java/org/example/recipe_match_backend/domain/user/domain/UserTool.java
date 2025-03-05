package org.example.recipe_match_backend.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.recipe_match_backend.domain.tool.domain.Tool;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserTool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id")
    private Tool tool;

    public void addTool(Tool tool) {
        this.tool = tool;
    }

    public void addUser(User user) {
        this.user = user;
    }

    public UserTool(User user, Tool tool) {
        this.user = user;
        this.tool = tool;
    }
}
