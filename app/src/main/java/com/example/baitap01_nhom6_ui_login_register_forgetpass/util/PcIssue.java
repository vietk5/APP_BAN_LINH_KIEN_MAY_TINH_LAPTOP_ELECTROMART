package com.example.baitap01_nhom6_ui_login_register_forgetpass.util;

import java.util.ArrayList;
import java.util.List;

public class PcIssue {
    public enum Severity { WARN, ERROR }

    public Severity severity;
    public String message;
    public final List<Suggestion> suggestions = new ArrayList<>();

    public PcIssue(Severity severity, String message) {
        this.severity = severity;
        this.message = message;
    }

    public static class Suggestion {
        public String title;
        public String keyword;
        public String targetSlot; // "MAIN", "RAM", "CPU"...

        public Suggestion(String title, String keyword, String targetSlot) {
            this.title = title;
            this.keyword = keyword;
            this.targetSlot = targetSlot;
        }
    }
}
