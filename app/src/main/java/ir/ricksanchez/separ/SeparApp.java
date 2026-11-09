package ir.ricksanchez.separ;

import android.app.Application;
import ir.ricksanchez.separ.billing.UnifiedBillingManager;
import ir.ricksanchez.separ.data.RescueLedgerRepository;
import ir.ricksanchez.separ.data.UserCustomWhitelistManager;
import ir.ricksanchez.separ.data.UserModeManager;

public class SeparApp extends Application {

    private static SeparApp instance;
    private RescueLedgerRepository rescueLedger;
    private UnifiedBillingManager billingManager;
    private UserModeManager userModeManager;
    private UserCustomWhitelistManager userWhitelist;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        rescueLedger = new RescueLedgerRepository(this);
        billingManager = new UnifiedBillingManager(this);
        userModeManager = new UserModeManager(this);
        userWhitelist = new UserCustomWhitelistManager(this);
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

    public UserModeManager getUserModeManager() {
        return userModeManager;
    }

    public UserCustomWhitelistManager getUserWhitelist() {
        return userWhitelist;
    }
}
