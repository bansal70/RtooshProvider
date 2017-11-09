package com.rtoosh.provider.controller;

/*
 * Created by rishav on 10/26/2017.
 */

public class ModelManager {
    private static ModelManager modelManager;
    private PhoneVerificationManager phoneVerificationManager;
    private RegistrationManager registrationManager;
    private OtpManager otpManager;
    private ServicesListManager servicesListManager;
    private ProviderInfoManager providerInfoManager;
    private UploadIDManager uploadIDManager;
    private StatusManager statusManager;
    private ReportManager reportManager;
    private ContactManager contactManager;

    public static ModelManager getInstance() {
        if (modelManager != null)
            return modelManager;
        else
            return modelManager = new ModelManager();
    }

    private ModelManager() {
        phoneVerificationManager = new PhoneVerificationManager();
        registrationManager = new RegistrationManager();
        otpManager = new OtpManager();
        servicesListManager = new ServicesListManager();
        providerInfoManager = new ProviderInfoManager();
        uploadIDManager = new UploadIDManager();
        statusManager = new StatusManager();
        reportManager = new ReportManager();
        contactManager = new ContactManager();
    }

    public PhoneVerificationManager getPhoneVerificationManager() {
        return phoneVerificationManager;
    }

    public RegistrationManager getRegistrationManager() {
        return registrationManager;
    }

    public OtpManager getOtpManager() {
        return otpManager;
    }

    public ServicesListManager getServicesListManager() {
        return servicesListManager;
    }

    public ProviderInfoManager getProviderInfoManager() {
        return providerInfoManager;
    }

    public UploadIDManager getUploadIDManager() {
        return uploadIDManager;
    }

    public StatusManager getStatusManager() {
        return statusManager;
    }

    public ReportManager getReportManager() {
        return reportManager;
    }

    public ContactManager getContactManager() {
        return contactManager;
    }
}
