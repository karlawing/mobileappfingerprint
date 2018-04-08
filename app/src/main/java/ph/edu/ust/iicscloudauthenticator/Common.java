package ph.edu.ust.iicscloudauthenticator;

/**
 * Created by Phillip on 3/5/2017.
 */

public class Common {
    static final String SERVICE_API_URL = "http://10.1.54.57/owncloud/oc_env/user.php";
    static final String SERVICE_API_URL2 = "http://10.1.54.57/owncloud/oc_env/bind.php";
    static final String SERVICE_API_URL3 = "http://10.1.54.57/owncloud/oc_env/bday.php";
    static final String SERVICE_API_URL4 = "http://10.1.54.57/owncloud/oc_env/address.php";
    static final String SERVICE_API_URL5 = "http://10.1.54.57/owncloud/oc_env/getCode.php";
    static final String SERVICE_API_URL6 = "http://10.1.54.57/owncloud/oc_env/email.php";
    static final String SERVICE_API_URL7 = "http://10.1.54.57/owncloud/oc_env/recoveryCode.php";

   /* static final String SERVICE_API_URL =  "http://192.168.0.117:8080/PHP_ENV/fcm/user.php";
    static final String SERVICE_API_URL2 = "http://192.168.0.117:8080/PHP_ENV/fcm/bind.php";
    static final String SERVICE_API_URL3 = "http://192.168.0.117:8080/PHP_ENV/fcm/bday.php";
    static final String  SERVICE_API_URL4 = "http://192.168.0.117:8080/PHP_ENV/fcm/address.php";
    static final String SERVICE_API_URL5 = "http://192.168.0.117:8080/PHP_ENV/fcm/getCode.php";
    static final String SERVICE_API_URL6 = "http://192.168.0.117:8080/PHP_ENV/fcm/email.php";
    static final String SERVICE_API_URL7 = "http://192.168.0.117:8080/PHP_ENV/fcm/recoveryCode.php";*/
    static final int RESULT_SUCCESS = 0;
    static final int RESULT_ERROR= 1;
    static final int RESULT_SUCCESS_NOT_BINDED = 2;
    static final int RESULT_BINDED = 3;
    static final int RESULT_BINDED_ACCOUNT = 4;
    static final int RESULT_BINDED_PHONE = 5;
}
