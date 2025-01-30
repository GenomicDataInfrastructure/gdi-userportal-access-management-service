package io.github.genomicdatainfrastructure.daam.exceptions;

public class MemberNotInvitedException extends RuntimeException {

    public MemberNotInvitedException(String name) {
        super("Member %s could not be invited".formatted(name));
    }
}
