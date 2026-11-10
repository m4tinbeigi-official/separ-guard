package ir.ricksanchez.separ;

import android.app.Application;
import ir.ricksanchez.separ.billing.PromoAndAffiliateManager;
import ir.ricksanchez.separ.billing.UnifiedBillingManager;
import ir.ricksanchez.separ.data.RescueLedgerRepository;
import ir.ricksanchez.separ.data.UserCustomWhitelistManager;
import ir.ricksanchez.separ.data.UserModeManager;
import ir.ricksanchez.separ.services.ClipboardHijackGuardian;

public class SeparApp extends Application {

    private static SeparApp instance;
    private RescueLedgerRepository rescueLedger;
    private UnifiedBillingManager billingManager;
    private PromoAndAffiliateManager promoManager;
    private UserModeManager userModeManager;
    private UserCustomWhitelistManager userWhitelist;
    private ClipboardHijackGuardian clipboardGuardian;
    private ir.ricksanchez.separ.data.RecentOtpAuditRepository otpAuditRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // بررسی یکپارچگی دودویی، امضای دیجیتال و ضد مهندسی معکوس
        try {
            ir.ricksanchez.separ.security.AntiTamperGuardian.performComprehensiveAudit(this);
        } catch (Exception ignored) {}

        rescueLedger = new RescueLedgerRepository(this);
        otpAuditRepository = new ir.ricksanchez.separ.data.RecentOtpAuditRepository(this);
        billingManager = new UnifiedBillingManager(this);
        promoManager = new PromoAndAffiliateManager(this, billingManager);
        userModeManager = new UserModeManager(this);
        userWhitelist = new UserCustomWhitelistManager(this);

        // فعال‌سازی مانیتورینگ کلیپ‌بورد در برابر بدافزارهای کلیپر
        try {
            clipboardGuardian = new ClipboardHijackGuardian(this);
            clipboardGuardian.startMonitoring();
        } catch (Exception ignored) {}
    }

    public static SeparApp getInstance() {
        return instance;
    }

    public RescueLedgerRepository getRescueLedger() {
        return rescueLedger;
    }

    public UnifiedBillingManager getBillingManager() {
        return billingManager;
    }

    public PromoAndAffiliateManager getPromoManager() {
        return promoManager;
    }

    public UserModeManager getUserModeManager() {
        return userModeManager;
    }

    public UserCustomWhitelistManager getUserWhitelist() {
        return userWhitelist;
    }

    public ClipboardHijackGuardian getClipboardGuardian() {
        return clipboardGuardian;
    }

    public ir.ricksanchez.separ.data.RecentOtpAuditRepository getOtpAuditRepository() {
        return otpAuditRepository;
    }
}
