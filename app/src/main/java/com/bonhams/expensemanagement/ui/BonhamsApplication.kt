package com.bonhams.expensemanagement.ui

import android.app.Application
import com.bonhams.expensemanagement.utils.AppPreferences
import com.google.android.gms.security.ProviderInstaller
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext

class BonhamsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

            // Google Play will install latest OpenSSL
        try {
            ProviderInstaller.installIfNeeded(applicationContext);
            val  sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (e: Exception) {
        }


        AppPreferences.init(this)
    }
}