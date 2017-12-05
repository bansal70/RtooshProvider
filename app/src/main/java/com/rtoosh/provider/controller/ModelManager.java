package com.rtoosh.provider.controller;

/*
 * Created by rishav on 10/26/2017.
 */

public class ModelManager {
    private static ModelManager modelManager;
    private PhoneVerificationManager phoneVerificationManager;
    private RegistrationManager registrationManager;
    private OtpManager otpManager;
    private PasswordManager passwordManager;
    private ServicesListManager servicesListManager;
    private ProviderInfoManager providerInfoManager;
    private UploadIDManager uploadIDManager;
    private StatusManager statusManager;
    private ReportManager reportManager;
    private ContactManager contactManager;
    private ProfileManager profileManager;
    private UpdateProfileManager updateProfileManager;
    private UpdatePasswordManager updatePasswordManager;
    private RequestManager requestManager;
    private ServiceStartedManager serviceStartedManager;
    private ServiceCompletedManager serviceCompletedManager;
    private AdditionalServiceManager additionalServiceManager;
    private FeedbackManager feedbackManager;
    private UpdateLocationManager updateLocationManager;
    private WalletManager walletManager;
    private RequestsHistoryManager requestsHistoryManager;
    private CalendarManager calendarManager;
    private UpdateInfoManager updateInfoManager;
    private RatingsManager ratingsManager;
    private OngoingRequestManager ongoingRequestManager;
    private OrderDetailsManager orderDetailsManager;
    private SpecialOffersManager specialOffersManager;

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
        profileManager = new ProfileManager();
        updateProfileManager = new UpdateProfileManager();
        updatePasswordManager = new UpdatePasswordManager();
        requestManager = new RequestManager();
        serviceStartedManager = new ServiceStartedManager();
        serviceCompletedManager = new ServiceCompletedManager();
        additionalServiceManager = new AdditionalServiceManager();
        feedbackManager = new FeedbackManager();
        updateLocationManager = new UpdateLocationManager();
        walletManager = new WalletManager();
        requestsHistoryManager = new RequestsHistoryManager();
        calendarManager = new CalendarManager();
        updateInfoManager = new UpdateInfoManager();
        ratingsManager = new RatingsManager();
        ongoingRequestManager = new OngoingRequestManager();
        orderDetailsManager = new OrderDetailsManager();
        specialOffersManager = new SpecialOffersManager();
        passwordManager = new PasswordManager();
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

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public UpdateProfileManager getUpdateProfileManager() {
        return updateProfileManager;
    }

    public UpdatePasswordManager getUpdatePasswordManager() {
        return updatePasswordManager;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public ServiceStartedManager getServiceStartedManager() {
        return serviceStartedManager;
    }

    public ServiceCompletedManager getServiceCompletedManager() {
        return serviceCompletedManager;
    }

    public AdditionalServiceManager getAdditionalServiceManager() {
        return additionalServiceManager;
    }

    public FeedbackManager getFeedbackManager() {
        return feedbackManager;
    }

    public UpdateLocationManager getUpdateLocationManager() {
        return updateLocationManager;
    }

    public WalletManager getWalletManager() {
        return walletManager;
    }

    public RequestsHistoryManager getRequestsHistoryManager() {
        return requestsHistoryManager;
    }

    public CalendarManager getCalendarManager() {
        return calendarManager;
    }

    public UpdateInfoManager getUpdateInfoManager() {
        return updateInfoManager;
    }

    public RatingsManager getRatingsManager() {
        return ratingsManager;
    }

    public OngoingRequestManager getOngoingRequestManager() {
        return ongoingRequestManager;
    }

    public OrderDetailsManager getOrderDetailsManager() {
        return orderDetailsManager;
    }

    public SpecialOffersManager getSpecialOffersManager() {
        return specialOffersManager;
    }

    public PasswordManager getPasswordManager() {
        return passwordManager;
    }
}
