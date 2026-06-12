package com.example.api.domains.auth.domain;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.auth.requests.UpdateProfileRequest;

public final class Profile {

    private Profile() {
    }

    public static User revise(String id, UpdateProfileRequest request, UserRepository directory) {
        User user = Account.locate(id, directory);

        String handle = null;
        if (request.handle() != null) {
            String clean = request.handle().replace("@", "");
            if (!clean.equals(user.handle()) && directory.existsByHandle(clean)) {
                throw new IllegalArgumentException("Handle already taken");
            }
            handle = clean;
        }

        user.update(request.name(), handle, request.avatar());

        if (request.role() != null) {
            user.reassign(request.role());
        }
        if (request.level() != null) {
            user.promote(request.level());
        }

        return directory.save(user);
    }
}
