package com.ncb.rename;

import java.util.regex.Pattern;

public final class Validations {

    private static final Pattern CONTAINER_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_.-]+$");

    private static final Pattern REPOSITORY_PATTERN = Pattern.compile("^[a-z0-9_\\.-]+$");

    private static final Pattern NAMESPACE_PATTERN = Pattern.compile("^[a-z0-9_-]+$");

    private static final Pattern FORBIDDEN_REPOSITORY_PATTERN = Pattern.compile("^[a-f0-9]{64}$");

    private static final Pattern TAG_PATTERN = Pattern.compile("^[A-Za-z0-9_\\.-]+$");

    private Validations() {
        super();
    }
}