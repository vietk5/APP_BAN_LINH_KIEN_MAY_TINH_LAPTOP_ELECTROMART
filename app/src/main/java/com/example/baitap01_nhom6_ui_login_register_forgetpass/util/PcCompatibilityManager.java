package com.example.baitap01_nhom6_ui_login_register_forgetpass.util;

import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.PcPartSlot;
import com.example.baitap01_nhom6_ui_login_register_forgetpass.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PcCompatibilityManager {
    private static PcCompatibilityManager instance;
    private List<PcPartSlot> slots;

    private PcCompatibilityManager() {
        resetBuild();
    }

    public static synchronized PcCompatibilityManager getInstance() {
        if (instance == null) instance = new PcCompatibilityManager();
        return instance;
    }

    public void resetBuild() {
        slots = new ArrayList<>();
        slots.add(new PcPartSlot("CPU", "Vi xử lý (CPU)", android.R.drawable.ic_menu_info_details, true));
        slots.add(new PcPartSlot("MAIN", "Bo mạch chủ (Mainboard)", android.R.drawable.ic_menu_info_details, true));
        slots.add(new PcPartSlot("RAM", "Bộ nhớ trong (RAM)", android.R.drawable.ic_menu_info_details, true));
        slots.add(new PcPartSlot("VGA", "Card màn hình (VGA)", android.R.drawable.ic_menu_info_details, false));
        slots.add(new PcPartSlot("SSD", "Ổ cứng (SSD/HDD)", android.R.drawable.ic_menu_info_details, true));
        slots.add(new PcPartSlot("PSU", "Nguồn máy tính (PSU)", android.R.drawable.ic_menu_info_details, true));
        slots.add(new PcPartSlot("CASE", "Vỏ máy tính (Case)", android.R.drawable.ic_menu_info_details, true));
    }

    public List<PcPartSlot> getSlots() {
        return slots;
    }

    public void selectProductForSlot(String slotKey, Product product) {
        for (PcPartSlot slot : slots) {
            if (slot.getKey().equals(slotKey)) {
                slot.setProduct(product);
                break;
            }
        }
    }

    public long getTotalPrice() {
        long total = 0;
        for (PcPartSlot slot : slots) {
            if (slot.getProduct() != null) {
                try {
                    String priceStr = slot.getProduct().getPrice().replaceAll("[^0-9]", "");
                    if (!priceStr.isEmpty()) total += Long.parseLong(priceStr);
                } catch (Exception ignored) {}
            }
        }
        return total;
    }

    /**
     * NEW: phân tích tương thích + sinh gợi ý linh hoạt (chạy local, không gọi API)
     */
    public List<PcIssue> analyze() {
        List<PcIssue> issues = new ArrayList<>();

        Product cpu  = getProductByKey("CPU");
        Product main = getProductByKey("MAIN");
        Product ram  = getProductByKey("RAM");

        // ===== SOCKET / PLATFORM =====
        if (cpu != null && main != null) {
            String cpuName  = safe(cpu.getName());
            String mainName = safe(main.getName());

            boolean cpuIsRyzen = contains(cpuName, "ryzen");
            boolean cpuLooksIntel = containsAny(cpuName, "i3", "i5", "i7", "i9");

            boolean mainLooksIntelLGA1700 = containsAny(mainName, "H610", "B660", "B760", "Z690", "Z790");
            boolean mainLooksAM4 = containsAny(mainName, "A520", "B450", "B550", "X570");
            boolean mainLooksAM5 = containsAny(mainName, "B650", "X670");

            // Ryzen + Intel chipset -> ERROR + gợi ý đổi MAIN
            if (cpuIsRyzen && mainLooksIntelLGA1700) {
                String chipset = pickChipset(mainName);
                PcIssue issue = new PcIssue(
                        PcIssue.Severity.ERROR,
                        "Sai socket/nền tảng: CPU \"" + cpuName + "\" không tương thích mainboard \"" + mainName + "\"."
                                + " (main " + chipset + " thường dành cho Intel LGA1700)"
                );

                boolean cpuLikelyAM5 = containsAny(cpuName, "7000", "7600", "7700", "7800", "7900", "7950");
                if (cpuLikelyAM5) {
                    issue.suggestions.add(new PcIssue.Suggestion(
                            "Gợi ý: đổi MAIN sang AM5 (B650/X670)",
                            "B650 X670 AM5",
                            "MAIN"
                    ));
                } else {
                    issue.suggestions.add(new PcIssue.Suggestion(
                            "Gợi ý: đổi MAIN sang AM4 (A520/B450/B550/X570)",
                            "A520 B450 B550 X570 AM4",
                            "MAIN"
                    ));
                }
                issues.add(issue);
            }

            // Ryzen nhưng main không giống AM4/AM5 -> WARN + gợi ý tìm AM4/AM5
            if (cpuIsRyzen && !mainLooksIntelLGA1700 && !mainLooksAM4 && !mainLooksAM5) {
                PcIssue issue = new PcIssue(
                        PcIssue.Severity.WARN,
                        "Kiểm tra lại socket: CPU \"" + cpuName + "\" là Ryzen, hãy chọn mainboard đúng nền tảng (AM4/AM5) nếu cần."
                );
                issue.suggestions.add(new PcIssue.Suggestion(
                        "Gợi ý: tìm main AM4 (A520/B450/B550/X570)",
                        "A520 B450 B550 X570 AM4",
                        "MAIN"
                ));
                issue.suggestions.add(new PcIssue.Suggestion(
                        "Gợi ý: tìm main AM5 (B650/X670)",
                        "B650 X670 AM5",
                        "MAIN"
                ));
                issues.add(issue);
            }

            // Intel CPU nhưng main không giống LGA1700 -> WARN
            if (cpuLooksIntel && !mainLooksIntelLGA1700) {
                PcIssue issue = new PcIssue(
                        PcIssue.Severity.WARN,
                        "Kiểm tra lại socket: CPU \"" + cpuName + "\" có thể cần mainboard Intel đúng chipset/socket tương ứng."
                );
                issue.suggestions.add(new PcIssue.Suggestion(
                        "Gợi ý: tìm main Intel LGA1700 (H610/B660/B760/Z690/Z790)",
                        "H610 B660 B760 Z690 Z790 LGA1700",
                        "MAIN"
                ));
                issues.add(issue);
            }
        }

        // ===== DDR MISMATCH =====
        if (main != null && ram != null) {
            String mainName = safe(main.getName());
            String ramName  = safe(ram.getName());

            boolean mainDDR4 = containsAny(mainName, "DDR4", " D4");
            boolean mainDDR5 = containsAny(mainName, "DDR5", " D5");

            boolean ramDDR4 = contains(ramName, "DDR4");
            boolean ramDDR5 = contains(ramName, "DDR5");

            if (mainDDR4 && !ramDDR4) {
                PcIssue issue = new PcIssue(
                        PcIssue.Severity.ERROR,
                        "Sai chuẩn RAM: mainboard \"" + mainName + "\" là DDR4 nhưng RAM \"" + ramName + "\" không phải DDR4."
                );
                issue.suggestions.add(new PcIssue.Suggestion(
                        "Gợi ý: chọn RAM DDR4 (3200/3600)",
                        "DDR4 3200 3600",
                        "RAM"
                ));
                issues.add(issue);
            }

            if (mainDDR5 && !ramDDR5) {
                PcIssue issue = new PcIssue(
                        PcIssue.Severity.ERROR,
                        "Sai chuẩn RAM: mainboard \"" + mainName + "\" là DDR5 nhưng RAM \"" + ramName + "\" không phải DDR5."
                );
                issue.suggestions.add(new PcIssue.Suggestion(
                        "Gợi ý: chọn RAM DDR5 (5200/5600)",
                        "DDR5 5200 5600",
                        "RAM"
                ));
                issues.add(issue);
            }
        }

        return issues;
    }

    // ===== helpers =====
    private Product getProductByKey(String key) {
        for (PcPartSlot slot : slots) {
            if (slot.getKey().equals(key) && slot.getProduct() != null) {
                return slot.getProduct();
            }
        }
        return null;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean contains(String s, String part) {
        if (s == null) return false;
        return s.toLowerCase(Locale.ROOT).contains(part.toLowerCase(Locale.ROOT));
    }

    private boolean containsAny(String s, String... parts) {
        if (s == null) return false;
        String lower = s.toLowerCase(Locale.ROOT);
        for (String p : parts) {
            if (lower.contains(p.toLowerCase(Locale.ROOT))) return true;
        }
        return false;
    }

    private String pickChipset(String mainName) {
        String u = mainName == null ? "" : mainName.toUpperCase(Locale.ROOT);
        if (u.contains("H610")) return "H610";
        if (u.contains("B660")) return "B660";
        if (u.contains("B760")) return "B760";
        if (u.contains("Z690")) return "Z690";
        if (u.contains("Z790")) return "Z790";
        return "chipset đó";
    }
}
