package com.readytoborad.job;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.google.android.gms.identity.intents.AddressConstants;
import com.google.common.eventbus.EventBus;
import com.readytoborad.di.component.AppComponent;
import com.readytoborad.interfaces.ApiInterface;
import com.readytoborad.util.Util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.inject.Inject;
import javax.net.ssl.SSLHandshakeException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Response;

public abstract class BaseJob extends Job {

    public static final int UI_HIGH = 10;
    public static final int BACKGROUND = 1;

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_MULTIPART_FORM = "multipart/form-data";

    private static final String MARKET_CD = "marketCd";
    private static final String LANG_CD = "langCd";
    private static final String SST_API_TOKEN = "sstApiToken";
    private static final String TRANSACTION_ID = "transactionId";
    private static final String SST_SERVICE_CONFIG = "sst-service-config";
    private static final String OCP_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";

    private static final String SST_USER_ID = "sstUserId";
    private static final String SST_USER_ACCOUNT_NO = "sellerAcctNr";

    private static final String TAG = BaseJob.class.getSimpleName();
    private final Context context;
    /*@Inject
    transient public EventBus mEventBus;*/
   /* @Inject
    transient public ApiInterface mApiService;*/
    protected JobCallback jobCallback;
    protected String flag;
   /* @Inject
    JobManager mJobManager;*/
    boolean isSSLException;
    private AppComponent appComponent;

    public BaseJob(Context context, int priority, String group, JobCallback jobCallback) {
        super(new Params(priority).addTags(group));     // this call not require internet connection to run the job onRun() method
        this.context = context;
        this.jobCallback = jobCallback;
    }

    public BaseJob(Context context, int priority, String group, JobCallback jobCallback, String flag) {
        super(new Params(priority).addTags(group));
        this.context = context;
        this.jobCallback = jobCallback;
        this.flag = flag;
    }

    public abstract void onSuccess(Response<?> response);

    public abstract void onFailure(Response<?> response);

    public void callApi(Call<?> call) {
        if (null != context) {
            Log.d(TAG, "API Calling Class - " + context.getClass().getSimpleName());
        }
        //TODO check for token expiry time for defensive approach in future
        // TODO handle multiple cases for errors like 401 unauthorized, 500 server error, 503 server maintenance etc.
        if (Util.isInternetOn(context)) {
            try {
                Response<?> response = call.execute();
                if (response.isSuccessful()) {
                    triggerSuccessResponse(response);
                } else {
                    /*if (401 == response.code() && getCurrentRunCount() < getRetryLimit()) {
                        Request request = call.request();
                        String sstConfig = request.header("sst-service-config");
                        if (null != sstConfig && sstConfig.contains("sstApiToken")) {
                            checkUnAuthorizedExp(response);
                        } else {
                            showFailureResponse(response);
                        }
                    } else {
                        showFailureResponse(response);
                    }*/
                }
            } catch (UnknownHostException e) {
                //no way to proceed, send error to controller
                Log.e(TAG, "UnknownHostException : " + e.getMessage());
              //  handleExceptionCode(AddressConstants.ErrorCodes.HTTP_UNKNOWN_HOST);
            } catch (SocketTimeoutException e) {
                Log.e(TAG, "SocketTimeoutException : " + e.getMessage());
                //We'll retry until max attempt limit reached
                if (getCurrentRunCount() < getRetryLimit()) {
                    // throwing exception would trigger retry (via call to shouldReRunOnThrowable())
                //    throw new NetworkException(AddressConstants.ErrorCodes.HTTP_SOCKET_TIMEOUT);
                } else {
                    // time to send error to controller
                  //  handleExceptionCode(AddressConstants.ErrorCodes.HTTP_SOCKET_TIMEOUT);
                }
            } catch (SSLHandshakeException e) {
                isSSLException = true;
                handleExceptionCode(1);
                Log.e("SSLHand", e.getMessage());
            } catch (Exception e) {
                //no way to proceed, send error to controller
                Log.e(TAG, "Exception : " + e.getMessage());
                handleExceptionCode(1);
            }
        } else {
            //TODO can't show toast from a thread, need to discuss
            handleExceptionCode(1);
        }
    }

    private void handleExceptionCode(int errorCode) {
        ResponseBody body = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 0;
            }

            @Override
            public BufferedSource source() {
                return null;
            }
        };
        Response<?> errorResponse = Response.error(errorCode, body);
        onFailure(errorResponse);
    }

    private boolean triggerSuccessResponse(Response<?> response) {
        if (null != jobCallback && null != response) {
            //TODO needs to remove this in final build by bimal
            //Checking whether response coming from retrofit cache or server or coming from both
            // i.e. #https://futurestud.io/tutorials/retrofit-2-check-response-origin-network-cache-or-both
            if (null != response.raw().cacheResponse()) {
                Log.d(TAG, "cached response : OkHttp");
            }

            if (null != response.raw().networkResponse()) {
                Log.d(TAG, "network response : OkHttp");
            }
            if (isSSLException)
               // sslErrorDialog();
            onSuccess(response);
        }
        return true;
    }

 /*   private boolean checkUnAuthorizedExp(Response<?> response) {
        boolean isSuccess = sessionUtil.backgroundLogin(context);
        if (isSuccess)
            throw new UnAuthorizedException();
        else {
            showFailureResponse(response);
        }
        return true;
    }*/

    private boolean showFailureResponse(Response<?> response) {
        if (null != jobCallback && null != response) {
            onFailure(response);
        }
        return true;
    }

    protected boolean shouldRetry(Throwable throwable) {
        if (throwable instanceof Exception) {
            return true;
        } else if (throwable instanceof Exception) {
            return true;
        }
        return false;
    }



    /**
     * creates request headers
     *
     * @param isToken
     * @param isAccountNo
     * @param subscriptionKey
     * @param isChangeSstUserId - if not provided, default value set to SST_USER_ID
     * @return
     */
   /* protected HashMap<String, String> createHeader(boolean isToken, boolean isAccountNo, boolean isChangeSstUserId, String subscriptionKey) {
        return createHeader(isToken, isAccountNo, subscriptionKey, isChangeSstUserId, CONTENT_TYPE_JSON);
    }
*/
    /**
     * creates request headers
     *
     * @param isToken
     * @param isAccountNo
     * @param subscriptionKey
     * @param contentyType    - if not provided, default value set to application/json
     * @return
     */
   /* protected HashMap<String, String> createHeader(boolean isToken, boolean isAccountNo, String subscriptionKey, String contentyType) {
        return createHeader(isToken, isAccountNo, subscriptionKey, false, contentyType);
    }*/

    /*protected HashMap<String, String> createHeader(boolean isToken, boolean isAccountNo, String subscriptionKey,
                                                   boolean isChangeSstUserId, String contentyType) {
        HashMap<String, String> map = new HashMap<>();
        try {
            JSONObject sstJson = new JSONObject();
            sstJson.put(MARKET_CD, Utils.getStringValueFromPreference(App.getInstance(), MarketConstants.MRKT_CD));
            sstJson.put(LANG_CD, Utils.getStringValueFromPreference(App.getInstance(), MarketConstants.LANG_CD));
            if (isToken) {
                sstJson.put(SST_API_TOKEN, sessionUtil.getSessionToken(context));
            }
            sstJson.put(TRANSACTION_ID, UUID.randomUUID().toString());
            map.put(SST_SERVICE_CONFIG, sstJson.toString());

            if (isAccountNo && (!isChangeSstUserId)) {
                map.put(SST_USER_ID, sessionUtil.getSellerId(context));
            }

            if (isChangeSstUserId) {
                map.put(SST_USER_ACCOUNT_NO, sessionUtil.getSellerId(context));
            }

            if (null != subscriptionKey) {
                map.put(OCP_SUBSCRIPTION_KEY, subscriptionKey);
            }
            if (null != contentyType && contentyType.length() > 0) {
                map.put(CONTENT_TYPE, contentyType);
            }


        } catch (Exception e) {
            Log.e(TAG, "createHeader() - " + e.getMessage(), e);
        }
        return map;
    }*/

   /* protected HashMap<String, String> createHeader(boolean isToken, boolean isAccountNo, String subscriptionKey) {
        return createHeader(isToken, isAccountNo, subscriptionKey, false, CONTENT_TYPE_JSON);
    }*/


 /*   private void sslErrorDialog() {
        new AlertDialog.Builder(context)
                .setTitle(TranslationUtil.getTranslationValue(TranslationConstants.GENERIC, R.string.ssl_error_title))
                .setMessage(TranslationUtil.getTranslationValue(TranslationConstants.GENERIC, R.string.ssl_error_message))
                .setCancelable(false)
                .setPositiveButton(TranslationUtil.getTranslationValue(TranslationConstants.GENERIC, R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(TranslationUtil.getTranslationValue(TranslationConstants.GENERIC, R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mJobManager.cancelJobsInBackground(null, TagConstraint.ALL, "12");
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }*/


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UI_HIGH, BACKGROUND})
    public @interface Priority {
    }


    public interface JobCallback {
        void onSuccess(Object object);

        void onFailure(int errorCode, Object object);

        void onCancelled();
    }
}
